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
 * 	Xml <jv>xml</jv> = <jsm>xml</jsm>()
 * 		.name(<js>"foo"</js>)
 * 		.namespace(<js>"http://foo"</js>)
 *
 * 	<jc>// Serialize using JsonSerializer.</jc>
 * 	String <jv>json</jv> = JsonSerializer.<jsf>DEFAULT</jsf>.toString(<jv>xml</jv>x);
 *
 * 	<jc>// Or just use toString() which does the same as above.</jc>
 *  <jv>json</jv> = <jv>xml</jv>.toString();
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
 * 	<li class='link'>{@doc jd.Swagger}
 * 	<li class='extlink'>{@source}
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

		this.attribute = copyFrom.attribute;
		this.name = copyFrom.name;
		this.namespace = copyFrom.namespace;
		this.prefix = copyFrom.prefix;
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

	//-----------------------------------------------------------------------------------------------------------------
	// attribute
	//-----------------------------------------------------------------------------------------------------------------

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
	 */
	public void setAttribute(Boolean value) {
		attribute = value;
	}

	/**
	 * Bean property fluent getter:  <property>attribute</property>.
	 *
	 * <p>
	 * Declares whether the property definition translates to an attribute instead of an element.
	 *
	 * @return The property value as an {@link Optional}.  Never <jk>null</jk>.
	 */
	public Optional<Boolean> attribute() {
		return Optional.ofNullable(getAttribute());
	}

	/**
	 * Bean property fluent setter:  <property>attribute</property>.
	 *
	 * <p>
	 * Declares whether the property definition translates to an attribute instead of an element.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Default value is <jk>false</jk>.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object.
	 */
	public Xml attribute(Boolean value) {
		setAttribute(value);
		return this;
	}

	/**
	 * Bean property fluent setter:  <property>attribute</property>.
	 *
	 * <p>
	 * Declares whether the property definition translates to an attribute instead of an element.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Default value is <jk>false</jk>.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object.
	 */
	public Xml attribute(String value) {
		setAttribute(toBoolean(value));
		return this;
	}

	//-----------------------------------------------------------------------------------------------------------------
	// name
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Bean property getter:  <property>name</property>.
	 *
	 * <p>
	 * The name of the element/attribute used for the described schema property.
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
	 * The name of the element/attribute used for the described schema property.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 */
	public void setName(String value) {
		name = value;
	}

	/**
	 * Bean property fluent getter:  <property>name</property>.
	 *
	 * <p>
	 * The name of the element/attribute used for the described schema property.
	 *
	 * @return The property value as an {@link Optional}.  Never <jk>null</jk>.
	 */
	public Optional<String> name() {
		return Optional.ofNullable(getName());
	}

	/**
	 * Bean property fluent setter:  <property>name</property>.
	 *
	 * <p>
	 * The name of the element/attribute used for the described schema property.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object.
	 */
	public Xml name(String value) {
		setName(value);
		return this;
	}

	//-----------------------------------------------------------------------------------------------------------------
	// namespace
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Bean property getter:  <property>namespace</property>.
	 *
	 * <p>
	 * The URL of the namespace definition.
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
	 * The URL of the namespace definition.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 */
	public void setNamespace(String value) {
		namespace = value;
	}

	/**
	 * Bean property fluent getter:  <property>namespace</property>.
	 *
	 * <p>
	 * The URL of the namespace definition.
	 *
	 * @return The property value as an {@link Optional}.  Never <jk>null</jk>.
	 */
	public Optional<String> namespace() {
		return Optional.ofNullable(getNamespace());
	}

	/**
	 * Bean property fluent setter:  <property>namespace</property>.
	 *
	 * <p>
	 * The URL of the namespace definition.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object.
	 */
	public Xml namespace(String value) {
		setNamespace(value);
		return this;
	}

	//-----------------------------------------------------------------------------------------------------------------
	// prefix
	//-----------------------------------------------------------------------------------------------------------------

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
	 */
	public void setPrefix(String value) {
		prefix = value;
	}

	/**
	 * Bean property fluent getter:  <property>prefix</property>.
	 *
	 * <p>
	 * The prefix to be used for the name.
	 *
	 * @return The property value as an {@link Optional}.  Never <jk>null</jk>.
	 */
	public Optional<String> prefix() {
		return Optional.ofNullable(getPrefix());
	}

	/**
	 * Bean property fluent setter:  <property>prefix</property>.
	 *
	 * <p>
	 * The prefix to be used for the name.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object.
	 */
	public Xml prefix(String value) {
		setPrefix(value);
		return this;
	}

	//-----------------------------------------------------------------------------------------------------------------
	// wrapped
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Bean property getter:  <property>wrapped</property>.
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
	 */
	public void setWrapped(Boolean value) {
		this.wrapped = value;
	}

	/**
	 * Bean property fluent getter:  <property>wrapped</property>.
	 *
	 * <p>
	 * Signifies whether the array is wrapped (for example,
	 * <c>&lt;books&gt;&lt;book/&gt;&lt;book/&gt;&lt;books&gt;</c>) or unwrapped
	 * (<c>&lt;book/&gt;&lt;book/&gt;</c>).
	 * <br>The definition takes effect only when defined alongside <c>type</c> being <c>array</c>
	 * (outside the <c>items</c>).
	 *
	 * @return The property value as an {@link Optional}.  Never <jk>null</jk>.
	 */
	public Optional<Boolean> wrapped() {
		return Optional.ofNullable(getWrapped());
	}

	/**
	 * Bean property fluent setter:  <property>wrapped</property>.
	 *
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
	 * @return This object.
	 */
	public Xml wrapped(Boolean value) {
		setWrapped(value);
		return this;
	}

	/**
	 * Bean property fluent setter:  <property>wrapped</property>.
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
	 * @return This object.
	 */
	public Xml wrapped(String value) {
		setWrapped(toBoolean(value));
		return this;
	}


	@Override /* SwaggerElement */
	public <T> T get(String property, Class<T> type) {
		if (property == null)
			return null;
		switch (property) {
			case "attribute": return toType(getAttribute(), type);
			case "name": return toType(getName(), type);
			case "namespace": return toType(getNamespace(), type);
			case "prefix": return toType(getPrefix(), type);
			case "wrapped": return toType(getWrapped(), type);
			default: return super.get(property, type);
		}
	}

	@Override /* SwaggerElement */
	public Xml set(String property, Object value) {
		if (property == null)
			return this;
		switch (property) {
			case "attribute": return attribute(toBoolean(value));
			case "name": return name(stringify(value));
			case "namespace": return namespace(stringify(value));
			case "prefix": return prefix(stringify(value));
			case "wrapped": return wrapped(toBoolean(value));
			default:
				super.set(property, value);
				return this;
		}
	}

	@Override /* SwaggerElement */
	public Set<String> keySet() {
		ASet<String> s = ASet.<String>of()
			.appendIf(attribute != null, "attribute")
			.appendIf(name != null, "name")
			.appendIf(namespace != null, "namespace")
			.appendIf(prefix != null, "prefix")
			.appendIf(wrapped != null, "wrapped");
		return new MultiSet<>(s, super.keySet());
	}
}
