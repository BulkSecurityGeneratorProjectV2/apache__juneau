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
package org.apache.juneau.dto.swagger;

import static org.apache.juneau.internal.StringUtils.*;
import static org.apache.juneau.internal.ConverterUtils.*;

import java.util.*;

import org.apache.juneau.annotation.*;
import org.apache.juneau.collections.*;
import org.apache.juneau.internal.*;

/**
 * A metadata object that allows for more fine-tuned XML model definitions.
 *
 * <p>
 * When using arrays, XML element names are not inferred (for singular/plural forms) and the name property should be
 * used to add that information.
 *
 * <h5 class='section'>Example:</h5>
 * <p class='bcode w800'>
 * 	<jc>// Construct using SwaggerBuilder.</jc>
 * 	Xml x = <jsm>xml</jsm>()
 * 		.name(<js>"foo"</js>)
 * 		.namespace(<js>"http://foo"</js>)
 *
 * 	<jc>// Serialize using JsonSerializer.</jc>
 * 	String json = JsonSerializer.<jsf>DEFAULT</jsf>.toString(x);
 *
 * 	<jc>// Or just use toString() which does the same as above.</jc>
 * 	String json = x.toString();
 * </p>
 * <p class='bcode w800'>
 * 	<jc>// Output</jc>
 * 	{
 * 		<js>"name"</js>: <js>"foo"</js>,
 * 		<js>"namespace"</js>: <js>"http://foo"</js>
 * 	}
 * </p>
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc DtoSwagger}
 * </ul>
 */
@Bean(properties="name,namespace,prefix,attribute,wrapped,*")
public class Xml extends SwaggerElement {

	private String
		name,
		namespace,
		prefix;
	private Boolean
		attribute,
		wrapped;

	/**
	 * Default constructor.
	 */
	public Xml() {}

	/**
	 * Copy constructor.
	 *
	 * @param copyFrom The object to copy.
	 */
	public Xml(Xml copyFrom) {
		super(copyFrom);

		this.name = copyFrom.name;
		this.namespace = copyFrom.namespace;
		this.prefix = copyFrom.prefix;
		this.attribute = copyFrom.attribute;
		this.wrapped = copyFrom.wrapped;
	}

	/**
	 * Make a deep copy of this object.
	 *
	 * @return A deep copy of this object.
	 */
	public Xml copy() {
		return new Xml(this);
	}

	/**
	 * Bean property getter:  <property>name</property>.
	 *
	 * <p>
	 * Replaces the name of the element/attribute used for the described schema property.
	 *
	 * <p>
	 * When defined within the Items Object (<c>items</c>), it will affect the name of the individual XML elements
	 * within the list.
	 * <br>When defined alongside <c>type</c> being array (outside the <c>items</c>), it will affect the
	 * wrapping element and only if wrapped is <jk>true</jk>.
	 * <br>If wrapped is <jk>false</jk>, it will be ignored.
	 *
	 * @return The property value, or <jk>null</jk> if it is not set.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Bean property setter:  <property>name</property>.
	 *
	 * <p>
	 * Replaces the name of the element/attribute used for the described schema property.
	 *
	 * <p>
	 * When defined within the Items Object (<c>items</c>), it will affect the name of the individual XML elements
	 * within the list.
	 * <br>When defined alongside <c>type</c> being array (outside the <c>items</c>), it will affect the
	 * wrapping element and only if wrapped is <jk>true</jk>.
	 * <br>If wrapped is <jk>false</jk>, it will be ignored.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object (for method chaining).
	 */
	public Xml setName(String value) {
		name = value;
		return this;
	}

	/**
	 * Same as {@link #setName(String)}.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Non-String values will be converted to String using <c>toString()</c>.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object (for method chaining).
	 */
	public Xml name(Object value) {
		return setName(stringify(value));
	}

	/**
	 * Bean property getter:  <property>namespace</property>.
	 *
	 * <p>
	 * The URL of the namespace definition. Value SHOULD be in the form of a URL.
	 *
	 * @return The property value, or <jk>null</jk> if it is not set.
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Bean property setter:  <property>namespace</property>.
	 *
	 * <p>
	 * The URL of the namespace definition. Value SHOULD be in the form of a URL.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object (for method chaining).
	 */
	public Xml setNamespace(String value) {
		namespace = value;
		return this;
	}

	/**
	 * Same as {@link #setNamespace(String)}.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Non-String values will be converted to String using <c>toString()</c>.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object (for method chaining).
	 */
	public Xml namespace(Object value) {
		return setNamespace(stringify(value));
	}

	/**
	 * Bean property getter:  <property>prefix</property>.
	 *
	 * <p>
	 * The prefix to be used for the name.
	 *
	 * @return The property value, or <jk>null</jk> if it is not set.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Bean property setter:  <property>prefix</property>.
	 *
	 * <p>
	 * The prefix to be used for the name.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object (for method chaining).
	 */
	public Xml setPrefix(String value) {
		prefix = value;
		return this;
	}

	/**
	 * Same as {@link #setPrefix(String)}.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Non-String values will be converted to String using <c>toString()</c>.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object (for method chaining).
	 */
	public Xml prefix(Object value) {
		return setPrefix(stringify(value));
	}

	/**
	 * Bean property getter:  <property>attribute</property>.
	 *
	 * <p>
	 * Declares whether the property definition translates to an attribute instead of an element.
	 *
	 * @return The property value, or <jk>null</jk> if it is not set.
	 */
	public Boolean getAttribute() {
		return attribute;
	}

	/**
	 * Bean property setter:  <property>attribute</property>.
	 *
	 * <p>
	 * Declares whether the property definition translates to an attribute instead of an element.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Default value is <jk>false</jk>.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object (for method chaining).
	 */
	public Xml setAttribute(Boolean value) {
		attribute = value;
		return this;
	}

	/**
	 * Same as {@link #setAttribute(Boolean)}.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Default value is <jk>false</jk>.
	 * 	<br>Non-boolean values will be converted to boolean using <code>Boolean.<jsm>valueOf</jsm>(value.toString())</code>.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object (for method chaining).
	 */
	public Xml attribute(Object value) {
		return setAttribute(toBoolean(value));
	}

	/**
	 * Bean property getter:  <property>wrapped</property>.
	 *
	 * <p>
	 * MAY be used only for an array definition.
	 *
	 * <p>
	 * Signifies whether the array is wrapped (for example,
	 * <c>&lt;books&gt;&lt;book/&gt;&lt;book/&gt;&lt;books&gt;</c>) or unwrapped
	 * (<c>&lt;book/&gt;&lt;book/&gt;</c>).
	 * <br>The definition takes effect only when defined alongside <c>type</c> being <c>array</c>
	 * (outside the <c>items</c>).
	 *
	 * @return The property value, or <jk>null</jk> if it is not set.
	 */
	public Boolean getWrapped() {
		return wrapped;
	}

	/**
	 * Bean property setter:  <property>wrapped</property>.
	 *
	 * <p>
	 * MAY be used only for an array definition.
	 *
	 * <p>
	 * Signifies whether the array is wrapped (for example,
	 * <c>&lt;books&gt;&lt;book/&gt;&lt;book/&gt;&lt;books&gt;</c>) or unwrapped
	 * (<c>&lt;book/&gt;&lt;book/&gt;</c>).
	 * <br>The definition takes effect only when defined alongside <c>type</c> being <c>array</c>
	 * (outside the <c>items</c>).
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object (for method chaining).
	 */
	public Xml setWrapped(Boolean value) {
		this.wrapped = value;
		return this;
	}

	/**
	 * Same as {@link #setWrapped(Boolean)}.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Non-boolean values will be converted to boolean using <code>Boolean.<jsm>valueOf</jsm>(value.toString())</code>.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object (for method chaining).
	 */
	public Xml wrapped(Object value) {
		return setWrapped(toBoolean(value));
	}

	@Override /* SwaggerElement */
	public <T> T get(String property, Class<T> type) {
		if (property == null)
			return null;
		switch (property) {
			case "name": return toType(getName(), type);
			case "namespace": return toType(getNamespace(), type);
			case "prefix": return toType(getPrefix(), type);
			case "attribute": return toType(getAttribute(), type);
			case "wrapped": return toType(getWrapped(), type);
			default: return super.get(property, type);
		}
	}

	@Override /* SwaggerElement */
	public Xml set(String property, Object value) {
		if (property == null)
			return this;
		switch (property) {
			case "name": return name(value);
			case "namespace": return namespace(value);
			case "prefix": return prefix(value);
			case "attribute": return attribute(value);
			case "wrapped": return wrapped(value);
			default:
				super.set(property, value);
				return this;
		}
	}

	@Override /* SwaggerElement */
	public Set<String> keySet() {
		ASet<String> s = ASet.<String>of()
			.appendIf(name != null, "name")
			.appendIf(namespace != null, "namespace")
			.appendIf(prefix != null, "prefix")
			.appendIf(attribute != null, "attribute")
			.appendIf(wrapped != null, "wrapped");
		return new MultiSet<>(s, super.keySet());
	}
}
