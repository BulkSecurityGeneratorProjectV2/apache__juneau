// ***************************************************************************************************************************
// * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file *
// * distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file        *
// * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance            *
// * with the License.  You may obtain a copy of the License at                                                              *
// *                                                                                                                         *
// *  http://www.apache.org/licenses/LICENSE-2.0                                                                             *
// *                                                                                                                         *
// * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an  *
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the        *
// * specific language governing permissions and limitations under the License.                                              *
// ***************************************************************************************************************************
package org.apache.juneau.xmlschema;

import static org.apache.juneau.internal.ArrayUtils.*;
import static org.apache.juneau.xml.annotation.XmlFormat.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.xml.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;

import org.apache.juneau.*;
import org.apache.juneau.collections.*;
import org.apache.juneau.serializer.*;
import org.apache.juneau.xml.*;
import org.apache.juneau.xml.annotation.*;
import org.w3c.dom.bootstrap.*;
import org.w3c.dom.ls.*;

/**
 * Session object that lives for the duration of a single use of {@link XmlSchemaSerializer}.
 *
 * <p>
 * This class is NOT thread safe.
 * It is typically discarded after one-time use although it can be reused within the same thread.
 */
@Deprecated
public class XmlSchemaSerializerSession extends XmlSerializerSession {

	/**
	 * Create a new session using properties specified in the context.
	 *
	 * @param ctx
	 * 	The context creating this session object.
	 * 	The context contains all the configuration settings for this object.
	 * @param args
	 * 	Runtime arguments.
	 * 	These specify session-level information such as locale and URI context.
	 * 	It also include session-level properties that override the properties defined on the bean and
	 * 	serializer contexts.
	 */
	protected XmlSchemaSerializerSession(XmlSerializer ctx, SerializerSessionArgs args) {
		super(ctx, args);
	}

	@Override /* SerializerSession */
	protected void doSerialize(SerializerPipe out, Object o) throws IOException, SerializeException {
		if (isEnableNamespaces() && isAutoDetectNamespaces())
			findNsfMappings(o);

		Namespace xs = getXsNamespace();
		Namespace[] allNs = append(new Namespace[]{getDefaultNamespace()}, getNamespaces());

		Schemas schemas = new Schemas(this, xs, getDefaultNamespace(), allNs);
		schemas.process(o);
		schemas.serializeTo(out.getWriter());
	}

	/**
	 * Returns an XML-Schema validator based on the output returned by {@link #doSerialize(SerializerPipe, Object)};
	 *
	 * @param out The target writer.
	 * @param o The object to serialize.
	 * @return The new validator.
	 * @throws Exception If a problem was detected in the XML-Schema output produced by this serializer.
	 */
	public Validator getValidator(SerializerPipe out, Object o) throws Exception {
		doSerialize(out, o);
		String xmlSchema = out.getWriter().toString();

		// create a SchemaFactory capable of understanding WXS schemas
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		if (xmlSchema.indexOf('\u0000') != -1) {

			// Break it up into a map of namespaceURI->schema document
			final Map<String,String> schemas = new HashMap<>();
			String[] ss = xmlSchema.split("\u0000");
			xmlSchema = ss[0];
			for (String s : ss) {
				Matcher m = pTargetNs.matcher(s);
				if (m.find())
					schemas.put(m.group(1), s);
			}

			// Create a custom resolver
			factory.setResourceResolver(
				new LSResourceResolver() {

					@Override /* LSResourceResolver */
					public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {

						String schema = schemas.get(namespaceURI);
						if (schema == null)
							throw new FormattedRuntimeException("No schema found for namespaceURI ''{0}''", namespaceURI);

						try {
							DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
							DOMImplementationLS domImplementationLS = (DOMImplementationLS)registry.getDOMImplementation("LS 3.0");
							LSInput in = domImplementationLS.createLSInput();
							in.setCharacterStream(new StringReader(schema));
							in.setSystemId(systemId);
							return in;

						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
			);
		}
		return factory.newSchema(new StreamSource(new StringReader(xmlSchema))).newValidator();
	}

	private static Pattern pTargetNs = Pattern.compile("targetNamespace=['\"]([^'\"]+)['\"]");


	/* An instance of a global element, global attribute, or XML type to be serialized. */
	private static class QueueEntry {
		Namespace ns;
		String name;
		ClassMeta<?> cm;
		QueueEntry(Namespace ns, String name, ClassMeta<?> cm) {
			this.ns = ns;
			this.name = name;
			this.cm = cm;
		}
	}

	/* An encapsulation of all schemas present in the metamodel of the serialized object. */
	final class Schemas extends LinkedHashMap<Namespace,Schema> {

		private static final long serialVersionUID = 1L;

		private Namespace defaultNs;
		BeanSession session;
		private LinkedList<QueueEntry>
			elementQueue = new LinkedList<>(),
			attributeQueue = new LinkedList<>(),
			typeQueue = new LinkedList<>();

		Schemas(BeanSession session, Namespace xs, Namespace defaultNs, Namespace[] allNs) throws IOException {
			this.session = session;
			this.defaultNs = defaultNs;
			for (Namespace ns : allNs)
				put(ns, new Schema(this, xs, ns, defaultNs, allNs));
		}

		Schema getSchema(Namespace ns) {
			if (ns == null)
				ns = defaultNs;
			Schema s = get(ns);
			if (s == null)
				throw new FormattedRuntimeException("No schema defined for namespace ''{0}''", ns);
			return s;
		}

		void process(Object o) throws IOException {
			ClassMeta<?> cm = getClassMetaForObject(o);
			if (cm != null && cm.isOptional())
				cm = getClassMetaForObject(((Optional<?>)o).orElse(null));
			Namespace ns = defaultNs;
			if (cm == null)
				queueElement(ns, "null", object());
			else {
				XmlClassMeta xmlMeta = getXmlClassMeta(cm);
				if (cm.getDictionaryName() != null && xmlMeta.getNamespace() != null)
					ns = xmlMeta.getNamespace();
				queueElement(ns, cm.getDictionaryName(), cm);
			}
			processQueue();
		}

		void processQueue() throws IOException {
			boolean b;
			do {
				b = false;
				while (! elementQueue.isEmpty()) {
					QueueEntry q = elementQueue.removeFirst();
					b |= getSchema(q.ns).processElement(q.name, q.cm);
				}
				while (! typeQueue.isEmpty()) {
					QueueEntry q = typeQueue.removeFirst();
					b |= getSchema(q.ns).processType(q.name, q.cm);
				}
				while (! attributeQueue.isEmpty()) {
					QueueEntry q = attributeQueue.removeFirst();
					b |= getSchema(q.ns).processAttribute(q.name, q.cm);
				}
			} while (b);
		}

		void queueElement(Namespace ns, String name, ClassMeta<?> cm) {
			elementQueue.add(new QueueEntry(ns, name, cm));
		}

		void queueType(Namespace ns, String name, ClassMeta<?> cm) {
			if (name == null)
				name = XmlUtils.encodeElementName(cm);
			typeQueue.add(new QueueEntry(ns, name, cm));
		}

		void queueAttribute(Namespace ns, String name, ClassMeta<?> cm) {
			attributeQueue.add(new QueueEntry(ns, name, cm));
		}

		void serializeTo(Writer w) throws IOException {
			boolean b = false;
			for (Schema s : values()) {
				if (b)
					w.append('\u0000');
				w.append(s.toString());
				b = true;
			}
		}
	}

	@Override /* SerializerSession */
	protected boolean isTrimStrings() {
		return super.isTrimStrings();
	}

	/* An encapsulation of a single schema. */
	private final class Schema {
		private StringWriter sw = new StringWriter();
		private XmlWriter w;
		private Namespace defaultNs, targetNs;
		private Schemas schemas;
		private Set<String>
			processedTypes = new HashSet<>(),
			processedAttributes = new HashSet<>(),
			processedElements = new HashSet<>();

		@SuppressWarnings("synthetic-access")
		public Schema(Schemas schemas, Namespace xs, Namespace targetNs, Namespace defaultNs, Namespace[] allNs) throws IOException {
			this.schemas = schemas;
			this.defaultNs = defaultNs;
			this.targetNs = targetNs;
			w = new XmlWriter(sw, isUseWhitespace(), getMaxIndent(), isTrimStrings(), getQuoteChar(), null, true, null);
			int i = indent;
			w.oTag(i, "schema");
			w.attr("xmlns", xs.getUri());
			w.attr("targetNamespace", targetNs.getUri());
			w.attr("elementFormDefault", "qualified");
			if (targetNs != defaultNs)
				w.attr("attributeFormDefault", "qualified");
			for (Namespace ns2 : allNs)
				w.attr("xmlns", ns2.getName(), ns2.getUri());
			w.append('>').nl(i);
			for (Namespace ns : allNs) {
				if (ns != targetNs) {
					w.oTag(i+1, "import")
						.attr("namespace", ns.getUri())
						.attr("schemaLocation", ns.getName()+".xsd")
						.append("/>").nl(i+1);
				}
			}
		}

		boolean processElement(String name, ClassMeta<?> cm) throws IOException {
			if (processedElements.contains(name))
				return false;
			processedElements.add(name);

			ClassMeta<?> ft = cm.getSerializedClassMeta(schemas.session);
			if (name == null)
				name = getElementName(ft);
			Namespace ns = first(getXmlClassMeta(ft).getNamespace(), defaultNs);
			String type = getXmlType(ns, ft);

			w.oTag(indent+1, "element")
				.attr("name", XmlUtils.encodeElementName(name))
				.attr("type", type)
				.append('/').append('>').nl(indent+1);

			schemas.queueType(ns, null, ft);
			schemas.processQueue();
			return true;
		}

		boolean processAttribute(String name, ClassMeta<?> cm) throws IOException {
			if (processedAttributes.contains(name))
				return false;
			processedAttributes.add(name);

			String type = getXmlAttrType(cm);

			w.oTag(indent+1, "attribute")
				.attr("name", name)
				.attr("type", type)
				.append('/').append('>').nl(indent+1);

			return true;
		}

		boolean processType(String name, ClassMeta<?> cm) throws IOException {
			if (processedTypes.contains(name))
				return false;
			processedTypes.add(name);

			int i = indent + 1;

			cm = cm.getSerializedClassMeta(schemas.session);
			while (cm.isOptional())
				cm = cm.getElementType();

			XmlBeanMeta xbm = cm.isBean() ? getXmlBeanMeta(cm.getBeanMeta()) : null;

			w.oTag(i, "complexType")
				.attr("name", name);

			// This element can have mixed content if:
			// 	1) It's a generic Object (so it can theoretically be anything)
			//		2) The bean has a property defined with @XmlFormat.CONTENT.
			if ((xbm != null && (xbm.getContentFormat() != null && xbm.getContentFormat().isOneOf(TEXT,TEXT_PWS,MIXED,MIXED_PWS,XMLTEXT))) || ! cm.isMapOrBean())
				w.attr("mixed", "true");

			w.cTag().nl(i);

			boolean hasAnyAttrs = false;

			if (! (cm.isMapOrBean() || cm.isCollectionOrArray() || (cm.isAbstract() && ! cm.isNumber()) || cm.isObject())) {
				w.oTag(i+1, "attribute").attr("name", getBeanTypePropertyName(cm)).attr("type", "string").ceTag().nl(i+1);
				w.oTag(i+1, "attribute").attr("name", getNamePropertyName()).attr("type", "string").ceTag().nl(i+1);

			} else {

				//----- Bean -----
				if (cm.isBean()) {
					BeanMeta<?> bm = cm.getBeanMeta();

					boolean hasChildElements = false;

					for (BeanPropertyMeta pMeta : bm.getPropertyMetas()) {
						if (pMeta.canRead()) {
							XmlFormat bpXml = getXmlBeanPropertyMeta(pMeta).getXmlFormat();
							if (bpXml == ATTRS)
								hasAnyAttrs = true;
							else if (bpXml != XmlFormat.ATTR)
								hasChildElements = true;
						}
					}

					XmlBeanMeta xbm2 = getXmlBeanMeta(bm);
					if (xbm2.getContentProperty() != null && xbm2.getContentFormat() == ELEMENTS) {
						w.sTag(i+1, "sequence").nl(i+1);
						w.oTag(i+2, "any")
							.attr("processContents", "skip")
							.attr("minOccurs", 0)
							.ceTag().nl(i+2);
						w.eTag(i+1, "sequence").nl(i+1);

					} else if (hasChildElements) {

						boolean hasOtherNsElement = false;
						boolean hasCollapsed = false;

						for (BeanPropertyMeta pMeta : bm.getPropertyMetas()) {
							if (pMeta.canRead()) {
								XmlBeanPropertyMeta xmlMeta = getXmlBeanPropertyMeta(pMeta);
								if (xmlMeta.getXmlFormat() != ATTR) {
									if (xmlMeta.getNamespace() != null) {
										ClassMeta<?> ct2 = pMeta.getClassMeta();
										Namespace cNs = first(xmlMeta.getNamespace(), getXmlClassMeta(ct2).getNamespace(), getXmlClassMeta(cm).getNamespace(), defaultNs);
										// Child element is in another namespace.
										schemas.queueElement(cNs, pMeta.getName(), ct2);
										hasOtherNsElement = true;
									}
									if (xmlMeta.getXmlFormat() == COLLAPSED)
										hasCollapsed = true;
								}
							}
						}

						if (hasAnyAttrs) {
							w.oTag(i+1, "anyAttribute").attr("processContents", "skip").ceTag().nl(i+1);
						} else if (hasOtherNsElement || hasCollapsed) {
							// If this bean has any child elements in another namespace,
							// we need to add an <any> element.
							w.oTag(i+1, "choice").attr("maxOccurs", "unbounded").cTag().nl(i+1);
							w.oTag(i+2, "any")
								.attr("processContents", "skip")
								.attr("minOccurs", 0)
								.ceTag().nl(i+2);
							w.eTag(i+1, "choice").nl(i+1);

						} else {
							w.sTag(i+1, "all").nl(i+1);
							for (BeanPropertyMeta pMeta : bm.getPropertyMetas()) {
								if (pMeta.canRead()) {
									XmlBeanPropertyMeta xmlMeta = getXmlBeanPropertyMeta(pMeta);
									if (xmlMeta.getXmlFormat() != ATTR) {
										boolean isCollapsed = xmlMeta.getXmlFormat() == COLLAPSED;
										ClassMeta<?> ct2 = pMeta.getClassMeta();
										String childName = pMeta.getName();
										if (isCollapsed) {
											if (xmlMeta.getChildName() != null)
												childName = xmlMeta.getChildName();
											ct2 = pMeta.getClassMeta().getElementType();
										}
										Namespace cNs = first(xmlMeta.getNamespace(), getXmlClassMeta(ct2).getNamespace(), getXmlClassMeta(cm).getNamespace(), defaultNs);
										if (xmlMeta.getNamespace() == null) {
											w.oTag(i+2, "element")
												.attr("name", XmlUtils.encodeElementName(childName), false)
												.attr("type", getXmlType(cNs, ct2))
												.attr("minOccurs", 0);

											w.ceTag().nl(i+2);
										} else {
											// Child element is in another namespace.
											schemas.queueElement(cNs, pMeta.getName(), ct2);
											hasOtherNsElement = true;
										}
									}
								}
							}
							w.eTag(i+1, "all").nl(i+1);
						}

					}

					for (BeanPropertyMeta pMeta : getXmlBeanMeta(bm).getAttrProperties().values()) {
						if (pMeta.canRead()) {
							Namespace pNs = getXmlBeanPropertyMeta(pMeta).getNamespace();
							if (pNs == null)
								pNs = defaultNs;

							// If the bean attribute has a different namespace than the bean, then it needs to
							// be added as a top-level entry in the appropriate schema file.
							if (pNs != targetNs) {
								schemas.queueAttribute(pNs, pMeta.getName(), pMeta.getClassMeta());
								w.oTag(i+1, "attribute")
									//.attr("name", pMeta.getName(), true)
									.attr("ref", pNs.getName() + ':' + pMeta.getName())
									.ceTag().nl(i+1);
							}

							// Otherwise, it's just a plain attribute of this bean.
							else {
								if (! hasAnyAttrs) {
									w.oTag(i+1, "attribute")
									.attr("name", pMeta.getName(), true)
									.attr("type", getXmlAttrType(pMeta.getClassMeta()))
									.ceTag().nl(i+1);
								}
							}
						}
					}

				//----- Collection -----
				} else if (cm.isCollectionOrArray()) {
					ClassMeta<?> elementType = cm.getElementType();
					if (elementType.isObject()) {
						w.sTag(i+1, "sequence").nl(i+1);
						w.oTag(i+2, "any")
							.attr("processContents", "skip")
							.attr("maxOccurs", "unbounded")
							.attr("minOccurs", "0")
							.ceTag().nl(i+2);
						w.eTag(i+1, "sequence").nl(i+1);
					} else {
						Namespace cNs = first(getXmlClassMeta(elementType).getNamespace(), getXmlClassMeta(cm).getNamespace(), defaultNs);
						schemas.queueType(cNs, null, elementType);
						w.sTag(i+1, "sequence").nl(i+1);
						w.oTag(i+2, "any")
							.attr("processContents", "skip")
							.attr("maxOccurs", "unbounded")
							.attr("minOccurs", "0")
							.ceTag().nl(i+2);
						w.eTag(i+1, "sequence").nl(i+1);
					}

				//----- Map -----
				} else if (cm.isMap() || cm.isAbstract() || cm.isObject()) {
					w.sTag(i+1, "sequence").nl(i+1);
					w.oTag(i+2, "any")
						.attr("processContents", "skip")
						.attr("maxOccurs", "unbounded")
						.attr("minOccurs", "0")
						.ceTag().nl(i+2);
					w.eTag(i+1, "sequence").nl(i+1);
				}

				if (! hasAnyAttrs) {
					w.oTag(i+1, "attribute").attr("name", getBeanTypePropertyName(null)).attr("type", "string").ceTag().nl(i+1);
					w.oTag(i+1, "attribute").attr("name", getNamePropertyName()).attr("type", "string").ceTag().nl(i+1);
				}
			}

			w.eTag(i, "complexType").nl(i);
			schemas.processQueue();

			return true;
		}

		private String getElementName(ClassMeta<?> cm) {
			cm = cm.getSerializedClassMeta(schemas.session);
			String name = cm.getDictionaryName();

			if (name == null) {
				if (cm.isBoolean())
					name = "boolean";
				else if (cm.isNumber())
					name = "number";
				else if (cm.isCollectionOrArray())
					name = "array";
				else if (! (cm.isMapOrBean() || cm.isCollectionOrArray() || cm.isObject() || cm.isAbstract()))
					name = "string";
				else
					name = "object";
			}
			return name;
		}

		@Override /* Object */
		public String toString() {
			try {
				w.eTag(indent, "schema").nl(indent);
			} catch (IOException e) {
				throw new RuntimeException(e); // Shouldn't happen.
			}
			return sw.toString();
		}

		private String getXmlType(Namespace currentNs, ClassMeta<?> cm) {
			String name = null;
			cm = cm.getSerializedClassMeta(schemas.session);
			if (currentNs == targetNs) {
				if (cm.isPrimitive()) {
					if (cm.isBoolean())
						name = "boolean";
					else if (cm.isNumber()) {
						if (cm.isDecimal())
							name = "decimal";
						else
							name = "integer";
					}
				}
			}
			if (name == null) {
				name = XmlUtils.encodeElementName(cm);
				schemas.queueType(currentNs, name, cm);
				return currentNs.getName() + ":" + name;
			}

			return name;
		}
	}

	@SafeVarargs
	static <T> T first(T...tt) {
		for (T t : tt)
			if (t != null)
				return t;
		return null;
	}

	static String getXmlAttrType(ClassMeta<?> cm) {
		if (cm.isBoolean())
			return "boolean";
		if (cm.isNumber()) {
			if (cm.isDecimal())
				return "decimal";
			return "integer";
		}
		return "string";
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Other methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* Session */
	public OMap toMap() {
		return super.toMap()
			.a("XmlSchemaSerializerSession", new DefaultFilteringOMap()
		);
	}
}
