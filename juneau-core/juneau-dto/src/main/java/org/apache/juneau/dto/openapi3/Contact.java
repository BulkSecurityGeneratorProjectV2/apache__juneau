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
package org.apache.juneau.dto.openapi3;

import static org.apache.juneau.internal.StringUtils.*;
import static org.apache.juneau.internal.CollectionUtils.*;
import static org.apache.juneau.internal.ConverterUtils.*;

import org.apache.juneau.UriResolver;
import org.apache.juneau.annotation.Bean;
import org.apache.juneau.internal.*;

import java.net.URI;
import java.net.URL;
import java.util.Set;

/**
 * Contact information for the exposed API.
 *
 * <h5 class='section'>Example:</h5>
 * <p class='bcode'>
 * 	<jc>// Construct using SwaggerBuilder.</jc>
 * 	Contact x = <jsm>contact</jsm>(<js>"API Support"</js>, <js>"http://www.swagger.io/support"</js>, <js>"support@swagger.io"</js>);
 *
 * 	<jc>// Serialize using JsonSerializer.</jc>
 * 	String json = JsonSerializer.<jsf>DEFAULT</jsf>.toString(x);
 *
 * 	<jc>// Or just use toString() which does the same as above.</jc>
 * 	String json = x.toString();
 * </p>
 * <p class='bcode'>
 * 	<jc>// Output</jc>
 * 	{
 * 		<js>"name"</js>: <js>"API Support"</js>,
 * 		<js>"url"</js>: <js>"http://www.swagger.io/support"</js>,
 * 		<js>"email"</js>: <js>"support@swagger.io"</js>
 * 	}
 * </p>
 */
@Bean(properties="name,url,email,*")
@FluentSetters
public class Contact extends OpenApiElement {

	private String name;
	private URI url;
	private String email;

	/**
	 * Default constructor.
	 */
	public Contact() {}

	/**
	 * Copy constructor.
	 *
	 * @param copyFrom The object to copy.
	 */
	public Contact(Contact copyFrom) {
		super(copyFrom);

		this.name = copyFrom.name;
		this.url = copyFrom.url;
		this.email = copyFrom.email;
	}

	/**
	 * Make a deep copy of this object.
	 *
	 * @return A deep copy of this object.
	 */
	public Contact copy() {
		return new Contact(this);
	}

	/**
	 * Bean property getter:  <property>name</property>.
	 *
	 * <p>
	 * The identifying name of the contact person/organization.
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
	 * The identifying name of the contact person/organization.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object
	 */
	public Contact setName(String value) {
		name = value;
		return this;
	}

	/**
	 * Bean property getter:  <property>url</property>.
	 *
	 * <p>
	 * The URL pointing to the contact information.
	 *
	 * @return The property value, or <jk>null</jk> if it is not set.
	 */
	public URI getUrl() {
		return url;
	}

	/**
	 * Bean property setter:  <property>url</property>.
	 *
	 * <p>
	 * The value can be of any of the following types: {@link URI}, {@link URL}, {@link String}.
	 * <br>Strings must be valid URIs.
	 *
	 * <p>
	 * URIs defined by {@link UriResolver} can be used for values.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object
	 */
	public Contact setUrl(URI value) {
		url = value;
		return this;
	}

	/**
	 * Bean property getter:  <property>email</property>.
	 *
	 * <p>
	 * The email address of the contact person/organization.
	 *
	 * @return The property value, or <jk>null</jk> if it is not set.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Bean property setter:  <property>email</property>.
	 *
	 * <p>
	 * The email address of the contact person/organization.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>MUST be in the format of an email address.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object
	 */
	public Contact setEmail(String value) {
		email = value;
		return this;
	}

	// <FluentSetters>

	// </FluentSetters>

	@Override /* OpenApiElement */
	public <T> T get(String property, Class<T> type) {
		if (property == null)
			return null;
		switch (property) {
			case "name": return toType(getName(), type);
			case "url": return toType(getUrl(), type);
			case "email": return toType(getEmail(), type);
			default: return super.get(property, type);
		}
	}

	@Override /* OpenApiElement */
	public Contact set(String property, Object value) {
		if (property == null)
			return this;
		switch (property) {
			case "name": return setName(stringify(value));
			case "url": return setUrl(toURI(value));
			case "email": return setEmail(stringify(value));
			default:
				super.set(property, value);
				return this;
		}
	}

	@Override /* OpenApiElement */
	public Set<String> keySet() {
		Set<String> s = setBuilder(String.class)
			.addIf(name != null, "name")
			.addIf(url != null, "url")
			.addIf(email != null, "email")
			.build();
		return new MultiSet<>(s, super.keySet());
	}
}
