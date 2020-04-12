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
package org.apache.juneau.httppart;

import static org.apache.juneau.internal.StringUtils.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;

import org.apache.juneau.*;
import org.apache.juneau.collections.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.jsonschema.annotation.Schema;
import org.apache.juneau.jsonschema.annotation.Items;
import org.apache.juneau.jsonschema.annotation.SubItems;
import org.apache.juneau.reflect.*;

/**
 * The builder class for creating {@link HttpPartSchema} objects.
 *
 */
public class HttpPartSchemaBuilder {
	String name, _default;
	Set<Integer> codes;
	Set<String> _enum;
	Boolean allowEmptyValue, exclusiveMaximum, exclusiveMinimum, required, uniqueItems, skipIfEmpty;
	HttpPartCollectionFormat collectionFormat = HttpPartCollectionFormat.NO_COLLECTION_FORMAT;
	HttpPartDataType type = HttpPartDataType.NO_TYPE;
	HttpPartFormat format = HttpPartFormat.NO_FORMAT;
	Pattern pattern;
	Number maximum, minimum, multipleOf;
	Long maxLength, minLength, maxItems, minItems, maxProperties, minProperties;
	Map<String,HttpPartSchemaBuilder> properties;
	HttpPartSchemaBuilder items, additionalProperties;
	boolean noValidate;
	Class<? extends HttpPartParser> parser;
	Class<? extends HttpPartSerializer> serializer;

	/**
	 * Instantiates a new {@link HttpPartSchema} object based on the configuration of this builder.
	 *
	 * <p>
	 * This method can be called multiple times to produce new schema objects.
	 *
	 * @return
	 * 	A new {@link HttpPartSchema} object.
	 * 	<br>Never <jk>null</jk>.
	 */
	public HttpPartSchema build() {
		return new HttpPartSchema(this);
	}

	HttpPartSchemaBuilder apply(Class<? extends Annotation> c, ParamInfo mpi) {
		apply(c, mpi.getParameterType().innerType());
		for (Annotation a : mpi.getDeclaredAnnotations())
			if (c.isInstance(a))
				apply(a);
		return this;
	}

	HttpPartSchemaBuilder apply(Class<? extends Annotation> c, Method m) {
		apply(c, m.getGenericReturnType());
		Annotation a = m.getAnnotation(c);
		if (a != null)
			return apply(a);
		return this;
	}

	HttpPartSchemaBuilder apply(Class<? extends Annotation> c, java.lang.reflect.Type t) {
		if (t instanceof Class<?>) {
			ClassInfo ci = ClassInfo.of((Class<?>)t);
			for (Annotation a : ci.getAnnotations(c))
				apply(a);
		} else if (Value.isType(t)) {
			apply(c, Value.getParameterType(t));
		}
		return this;
	}

	/**
	 * Apply the specified annotation to this schema.
	 *
	 * @param a The annotation to apply.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder apply(Annotation a) {
		if (a instanceof Body)
			apply((Body)a);
		else if (a instanceof Header)
			apply((Header)a);
		else if (a instanceof FormData)
			apply((FormData)a);
		else if (a instanceof Query)
			apply((Query)a);
		else if (a instanceof Path)
			apply((Path)a);
		else if (a instanceof Response)
			apply((Response)a);
		else if (a instanceof ResponseHeader)
			apply((ResponseHeader)a);
		else if (a instanceof HasQuery)
			apply((HasQuery)a);
		else if (a instanceof HasFormData)
			apply((HasFormData)a);
		else if (a instanceof Schema)
			apply((Schema)a);
		else
			throw new RuntimeException("HttpPartSchemaBuilder.apply(@"+a.getClass().getSimpleName()+") not defined");
		return this;
	}

	HttpPartSchemaBuilder apply(Body a) {
		required(a.required());
		allowEmptyValue(! a.required());
		apply(a.schema());
		return this;
	}

	HttpPartSchemaBuilder apply(Header a) {
		name(a.value());
		name(a.name());
		required(a.required());
		type(a.type());
		format(a.format());
		allowEmptyValue(a.allowEmptyValue());
		items(a.items());
		collectionFormat(a.collectionFormat());
		_default(a._default().length == 0 ? null : joinnl(a._default()));
		maximum(HttpPartSchema.toNumber(a.maximum()));
		exclusiveMaximum(a.exclusiveMaximum());
		minimum(HttpPartSchema.toNumber(a.minimum()));
		exclusiveMinimum(a.exclusiveMinimum());
		maxLength(a.maxLength());
		minLength(a.minLength());
		pattern(a.pattern());
		maxItems(a.maxItems());
		minItems(a.minItems());
		uniqueItems(a.uniqueItems());
		_enum(HttpPartSchema.toSet(a._enum()));
		multipleOf(HttpPartSchema.toNumber(a.multipleOf()));
		skipIfEmpty(a.skipIfEmpty());
		parser(a.parser());
		serializer(a.serializer());
		return this;
	}

	HttpPartSchemaBuilder apply(ResponseHeader a) {
		name(a.value());
		name(a.name());
		codes(a.code());
		type(a.type());
		format(a.format());
		items(a.items());
		collectionFormat(a.collectionFormat());
		_default(a._default().length == 0 ? null : joinnl(a._default()));
		maximum(HttpPartSchema.toNumber(a.maximum()));
		exclusiveMaximum(a.exclusiveMaximum());
		minimum(HttpPartSchema.toNumber(a.minimum()));
		exclusiveMinimum(a.exclusiveMinimum());
		maxLength(a.maxLength());
		minLength(a.minLength());
		pattern(a.pattern());
		maxItems(a.maxItems());
		minItems(a.minItems());
		uniqueItems(a.uniqueItems());
		_enum(HttpPartSchema.toSet(a._enum()));
		multipleOf(HttpPartSchema.toNumber(a.multipleOf()));
		allowEmptyValue(false);
		serializer(a.serializer());
		return this;
	}

	HttpPartSchemaBuilder apply(FormData a) {
		name(a.value());
		name(a.name());
		required(a.required());
		type(a.type());
		format(a.format());
		allowEmptyValue(a.allowEmptyValue());
		items(a.items());
		collectionFormat(a.collectionFormat());
		_default(a._default().length == 0 ? null : joinnl(a._default()));
		maximum(HttpPartSchema.toNumber(a.maximum()));
		exclusiveMaximum(a.exclusiveMaximum());
		minimum(HttpPartSchema.toNumber(a.minimum()));
		exclusiveMinimum(a.exclusiveMinimum());
		maxLength(a.maxLength());
		minLength(a.minLength());
		pattern(a.pattern());
		maxItems(a.maxItems());
		minItems(a.minItems());
		uniqueItems(a.uniqueItems());
		_enum(HttpPartSchema.toSet(a._enum()));
		multipleOf(HttpPartSchema.toNumber(a.multipleOf()));
		skipIfEmpty(a.skipIfEmpty());
		parser(a.parser());
		serializer(a.serializer());
		return this;
	}

	HttpPartSchemaBuilder apply(Query a) {
		name(a.value());
		name(a.name());
		required(a.required());
		type(a.type());
		format(a.format());
		allowEmptyValue(a.allowEmptyValue());
		items(a.items());
		collectionFormat(a.collectionFormat());
		_default(a._default().length == 0 ? null : joinnl(a._default()));
		maximum(HttpPartSchema.toNumber(a.maximum()));
		exclusiveMaximum(a.exclusiveMaximum());
		minimum(HttpPartSchema.toNumber(a.minimum()));
		exclusiveMinimum(a.exclusiveMinimum());
		maxLength(a.maxLength());
		minLength(a.minLength());
		pattern(a.pattern());
		maxItems(a.maxItems());
		minItems(a.minItems());
		uniqueItems(a.uniqueItems());
		_enum(HttpPartSchema.toSet(a._enum()));
		multipleOf(HttpPartSchema.toNumber(a.multipleOf()));
		skipIfEmpty(a.skipIfEmpty());
		parser(a.parser());
		serializer(a.serializer());
		return this;
	}

	HttpPartSchemaBuilder apply(Path a) {
		name(a.value());
		name(a.name());
		type(a.type());
		format(a.format());
		items(a.items());
		allowEmptyValue(a.allowEmptyValue());
		collectionFormat(a.collectionFormat());
		maximum(HttpPartSchema.toNumber(a.maximum()));
		exclusiveMaximum(a.exclusiveMaximum());
		minimum(HttpPartSchema.toNumber(a.minimum()));
		exclusiveMinimum(a.exclusiveMinimum());
		maxLength(a.maxLength());
		minLength(a.minLength());
		pattern(a.pattern());
		maxItems(a.maxItems());
		minItems(a.minItems());
		uniqueItems(a.uniqueItems());
		_enum(HttpPartSchema.toSet(a._enum()));
		multipleOf(HttpPartSchema.toNumber(a.multipleOf()));
		parser(a.parser());
		serializer(a.serializer());

		// Path remainder always allows empty value.
		if (startsWith(name, '/')) {
			allowEmptyValue();
			required(false);
		} else {
			required(a.required());
		}

		return this;
	}

	HttpPartSchemaBuilder apply(Response a) {
		codes(a.value());
		codes(a.code());
		required(false);
		allowEmptyValue(true);
		serializer(a.partSerializer());
		parser(a.partParser());
		apply(a.schema());
		return this;
	}

	HttpPartSchemaBuilder apply(Items a) {
		type(a.type());
		format(a.format());
		items(a.items());
		collectionFormat(a.collectionFormat());
		_default(a._default().length == 0 ? null : joinnl(a._default()));
		maximum(HttpPartSchema.toNumber(a.maximum()));
		exclusiveMaximum(a.exclusiveMaximum());
		minimum(HttpPartSchema.toNumber(a.minimum()));
		exclusiveMinimum(a.exclusiveMinimum());
		maxLength(a.maxLength());
		minLength(a.minLength());
		pattern(a.pattern());
		maxItems(a.maxItems());
		minItems(a.minItems());
		uniqueItems(a.uniqueItems());
		_enum(HttpPartSchema.toSet(a._enum()));
		multipleOf(HttpPartSchema.toNumber(a.multipleOf()));
		return this;
	}

	HttpPartSchemaBuilder apply(SubItems a) {
		type(a.type());
		format(a.format());
		items(HttpPartSchema.toOMap(a.items()));
		collectionFormat(a.collectionFormat());
		_default(a._default().length == 0 ? null : joinnl(a._default()));
		maximum(HttpPartSchema.toNumber(a.maximum()));
		exclusiveMaximum(a.exclusiveMaximum());
		minimum(HttpPartSchema.toNumber(a.minimum()));
		exclusiveMinimum(a.exclusiveMinimum());
		maxLength(a.maxLength());
		minLength(a.minLength());
		pattern(a.pattern());
		maxItems(a.maxItems());
		minItems(a.minItems());
		uniqueItems(a.uniqueItems());
		_enum(HttpPartSchema.toSet(a._enum()));
		multipleOf(HttpPartSchema.toNumber(a.multipleOf()));
		return this;
	}

	HttpPartSchemaBuilder apply(Schema a) {
		type(a.type());
		format(a.format());
		items(a.items());
		collectionFormat(a.collectionFormat());
		_default(a._default().length == 0 ? null : joinnl(a._default()));
		maximum(HttpPartSchema.toNumber(a.maximum()));
		exclusiveMaximum(a.exclusiveMaximum());
		minimum(HttpPartSchema.toNumber(a.minimum()));
		exclusiveMinimum(a.exclusiveMinimum());
		maxLength(a.maxLength());
		minLength(a.minLength());
		pattern(a.pattern());
		maxItems(a.maxItems());
		minItems(a.minItems());
		uniqueItems(a.uniqueItems());
		_enum(HttpPartSchema.toSet(a._enum()));
		multipleOf(HttpPartSchema.toNumber(a.multipleOf()));
		maxProperties(a.maxProperties());
		minProperties(a.minProperties());
		properties(HttpPartSchema.toOMap(a.properties()));
		additionalProperties(HttpPartSchema.toOMap(a.additionalProperties()));
		return this;
	}

	HttpPartSchemaBuilder apply(HasQuery a) {
		name(a.value());
		name(a.name());
		return this;
	}

	HttpPartSchemaBuilder apply(HasFormData a) {
		name(a.value());
		name(a.name());
		return this;
	}

	HttpPartSchemaBuilder apply(OMap m) {
		if (m != null && ! m.isEmpty()) {
			_default(m.getString("default"));
			_enum(HttpPartSchema.toSet(m.getString("enum")));
			allowEmptyValue(m.getBoolean("allowEmptyValue"));
			exclusiveMaximum(m.getBoolean("exclusiveMaximum"));
			exclusiveMinimum(m.getBoolean("exclusiveMinimum"));
			required(m.getBoolean("required"));
			uniqueItems(m.getBoolean("uniqueItems"));
			collectionFormat(m.getString("collectionFormat"));
			type(m.getString("type"));
			format(m.getString("format"));
			pattern(m.getString("pattern"));
			maximum(m.get("maximum", Number.class));
			minimum(m.get("minimum", Number.class));
			multipleOf(m.get("multipleOf", Number.class));
			maxItems(m.get("maxItems", Long.class));
			maxLength(m.get("maxLength", Long.class));
			maxProperties(m.get("maxProperties", Long.class));
			minItems(m.get("minItems", Long.class));
			minLength(m.get("minLength", Long.class));
			minProperties(m.get("minProperties", Long.class));

			items(m.getMap("items"));
			properties(m.getMap("properties"));
			additionalProperties(m.getMap("additionalProperties"));

			apply(m.getMap("schema", null));
		}
		return this;
	}

	/**
	 * <mk>name</mk> field.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder name(String value) {
		if (isNotEmpty(value))
			name = value;
		return this;
	}

	/**
	 * <mk>httpStatusCode</mk> key.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerResponsesObject Responses}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if <jk>null</jk> or an empty array.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder codes(int[] value) {
		if (value != null && value.length != 0)
			for (int v : value)
				code(v);
		return this;
	}

	/**
	 * <mk>httpStatusCode</mk> key.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerResponsesObject Responses}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <c>0</c>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder code(int value) {
		if (value != 0) {
			if (codes == null)
				codes = new TreeSet<>();
			codes.add(value);
		}
		return this;
	}

	/**
	 * <mk>required</mk> field.
	 *
	 * <p>
	 * Determines whether the parameter is mandatory.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder required(Boolean value) {
		required = resolve(value, required);
		return this;
	}

	/**
	 * <mk>required</mk> field.
	 *
	 * <p>
	 * Determines whether the parameter is mandatory.
	 *
	 * <p>
	 * Same as {@link #required(Boolean)} but takes in a boolean value as a string.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder required(String value) {
		required = resolve(value, required);
		return this;
	}

	/**
	 * <mk>required</mk> field.
	 *
	 * <p>
	 * Shortcut for calling <code>required(<jk>true</jk>);</code>.
	 *
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder required() {
		return required(true);
	}

	/**
	 * <mk>type</mk> field.
	 *
	 * <p>
	 * The type of the parameter.
	 *
	 * <p>
	 * The possible values are:
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		<js>"string"</js>
	 * 		<br>Parameter must be a string or a POJO convertible from a string.
	 * 	<li>
	 * 		<js>"number"</js>
	 * 		<br>Parameter must be a number primitive or number object.
	 * 		<br>If parameter is <c>Object</c>, creates either a <c>Float</c> or <c>Double</c> depending on the size of the number.
	 * 	<li>
	 * 		<js>"integer"</js>
	 * 		<br>Parameter must be a integer/long primitive or integer/long object.
	 * 		<br>If parameter is <c>Object</c>, creates either a <c>Short</c>, <c>Integer</c>, or <c>Long</c> depending on the size of the number.
	 * 	<li>
	 * 		<js>"boolean"</js>
	 * 		<br>Parameter must be a boolean primitive or object.
	 * 	<li>
	 * 		<js>"array"</js>
	 * 		<br>Parameter must be an array or collection.
	 * 		<br>Elements must be strings or POJOs convertible from strings.
	 * 		<br>If parameter is <c>Object</c>, creates an {@link OList}.
	 * 	<li>
	 * 		<js>"object"</js>
	 * 		<br>Parameter must be a map or bean.
	 * 		<br>If parameter is <c>Object</c>, creates an {@link OMap}.
	 * 		<br>Note that this is an extension of the OpenAPI schema as Juneau allows for arbitrarily-complex POJOs to be serialized as HTTP parts.
	 * 	<li>
	 * 		<js>"file"</js>
	 * 		<br>This type is currently not supported.
	 * </ul>
	 *
	 * <p>
	 * If the type is not specified, it will be auto-detected based on the parameter class type.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerSecuritySchemeObject SecurityScheme}
	 * </ul>
	 *
	 * <ul class='seealso'>
	 * 	<li class='extlink'>{@doc SwaggerDataTypes}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder type(String value) {
		try {
			if (isNotEmpty(value))
				type = HttpPartDataType.fromString(value);
		} catch (Exception e) {
			throw new ContextRuntimeException("Invalid value ''{0}'' passed in as type value.  Valid values: {1}", value, HttpPartDataType.values());
		}
		return this;
	}

	/**
	 * <mk>format</mk> field.
	 *
	 * <p>
	 * The extending format for the previously mentioned {@doc SwaggerParameterTypes parameter type}.
	 *
	 * <p>
	 * The possible values are:
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		<js>"int32"</js> - Signed 32 bits.
	 * 		<br>Only valid with type <js>"integer"</js>.
	 * 	<li>
	 * 		<js>"int64"</js> - Signed 64 bits.
	 * 		<br>Only valid with type <js>"integer"</js>.
	 * 	<li>
	 * 		<js>"float"</js> - 32-bit floating point number.
	 * 		<br>Only valid with type <js>"number"</js>.
	 * 	<li>
	 * 		<js>"double"</js> - 64-bit floating point number.
	 * 		<br>Only valid with type <js>"number"</js>.
	 * 	<li>
	 * 		<js>"byte"</js> - BASE-64 encoded characters.
	 * 		<br>Only valid with type <js>"string"</js>.
	 * 		<br>Parameters of type POJO convertible from string are converted after the string has been decoded.
	 * 	<li>
	 * 		<js>"binary"</js> - Hexadecimal encoded octets (e.g. <js>"00FF"</js>).
	 * 		<br>Only valid with type <js>"string"</js>.
	 * 		<br>Parameters of type POJO convertible from string are converted after the string has been decoded.
	 * 	<li>
	 * 		<js>"binary-spaced"</js> - Hexadecimal encoded octets, spaced (e.g. <js>"00 FF"</js>).
	 * 		<br>Only valid with type <js>"string"</js>.
	 * 		<br>Parameters of type POJO convertible from string are converted after the string has been decoded.
	 * 	<li>
	 * 		<js>"date"</js> - An <a href='http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14'>RFC3339 full-date</a>.
	 * 		<br>Only valid with type <js>"string"</js>.
	 * 	<li>
	 * 		<js>"date-time"</js> - An <a href='http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14'>RFC3339 date-time</a>.
	 * 		<br>Only valid with type <js>"string"</js>.
	 * 	<li>
	 * 		<js>"password"</js> - Used to hint UIs the input needs to be obscured.
	 * 		<br>This format does not affect the serialization or parsing of the parameter.
	 * 	<li>
	 * 		<js>"uon"</js> - UON notation (e.g. <js>"(foo=bar,baz=@(qux,123))"</js>).
	 * 		<br>Only valid with type <js>"object"</js>.
	 * 		<br>If not specified, then the input is interpreted as plain-text and is converted to a POJO directly.
	 * </ul>
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * <ul class='seealso'>
	 * 	<li class='extlink'>{@doc SwaggerDataTypeFormats}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder format(String value) {
		try {
			if (isNotEmpty(value))
				format = HttpPartFormat.fromString(value);
		} catch (Exception e) {
			throw new ContextRuntimeException("Invalid value ''{0}'' passed in as format value.  Valid values: {1}", value, HttpPartFormat.values());
		}
		return this;
	}

	/**
	 * <mk>allowEmptyValue</mk> field.
	 *
	 * <p>
	 * Sets the ability to pass empty-valued parameters.
	 * <br>This is valid only for either query or formData parameters and allows you to send a parameter with a name only or an empty value.
	 * <br>The default value is <jk>false</jk>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder allowEmptyValue(Boolean value) {
		allowEmptyValue = resolve(value, allowEmptyValue);
		return this;
	}

	/**
	 * <mk>allowEmptyValue</mk> field.
	 *
	 * <p>
	 * Same as {@link #allowEmptyValue(Boolean)} but takes in a string boolean value.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder allowEmptyValue(String value) {
		allowEmptyValue = resolve(value, allowEmptyValue);
		return this;
	}

	/**
	 * <mk>allowEmptyValue</mk> field.
	 *
	 * <p>
	 * Shortcut for calling <code>allowEmptyValue(<jk>true</jk>);</code>.
	 *
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder allowEmptyValue() {
		return allowEmptyValue(true);
	}

	/**
	 * <mk>items</mk> field.
	 *
	 * <p>
	 * Describes the type of items in the array.
	 * <p>
	 * Required if <c>type</c> is <js>"array"</js>.
	 * <br>Can only be used if <c>type</c> is <js>"array"</js>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder items(HttpPartSchemaBuilder value) {
		if (value != null)
			this.items = value;
		return this;
	}

	HttpPartSchemaBuilder items(OMap value) {
		if (value != null && ! value.isEmpty())
			items = HttpPartSchema.create().apply(value);
		return this;
	}

	HttpPartSchemaBuilder items(Items value) {
		if (! AnnotationUtils.empty(value))
			items = HttpPartSchema.create().apply(value);
		return this;
	}

	HttpPartSchemaBuilder items(SubItems value) {
		if (! AnnotationUtils.empty(value))
			items = HttpPartSchema.create().apply(value);
		return this;
	}


	/**
	 * <mk>collectionFormat</mk> field.
	 *
	 * <p>
	 * Determines the format of the array if <c>type</c> <js>"array"</js> is used.
	 * <br>Can only be used if <c>type</c> is <js>"array"</js>.
	 *
	 * <br>Possible values are:
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		<js>"csv"</js> (default) - Comma-separated values (e.g. <js>"foo,bar"</js>).
	 * 	<li>
	 * 		<js>"ssv"</js> - Space-separated values (e.g. <js>"foo bar"</js>).
	 * 	<li>
	 * 		<js>"tsv"</js> - Tab-separated values (e.g. <js>"foo\tbar"</js>).
	 * 	<li>
	 * 		<js>"pipes</js> - Pipe-separated values (e.g. <js>"foo|bar"</js>).
	 * 	<li>
	 * 		<js>"multi"</js> - Corresponds to multiple parameter instances instead of multiple values for a single instance (e.g. <js>"foo=bar&amp;foo=baz"</js>).
	 * 	<li>
	 * 		<js>"uon"</js> - UON notation (e.g. <js>"@(foo,bar)"</js>).
	 * 	<li>
	 * </ul>
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * <p>
	 * Note that for collections/arrays parameters with POJO element types, the input is broken into a string array before being converted into POJO elements.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder collectionFormat(String value) {
		try {
			if (isNotEmpty(value))
				this.collectionFormat = HttpPartCollectionFormat.fromString(value);
		} catch (Exception e) {
			throw new ContextRuntimeException("Invalid value ''{0}'' passed in as collectionFormat value.  Valid values: {1}", value, HttpPartCollectionFormat.values());
		}
		return this;
	}

	/**
	 * <mk>default</mk> field.
	 *
	 * <p>
	 * Declares the value of the parameter that the server will use if none is provided, for example a "count" to control the number of results per page might default to 100 if not supplied by the client in the request.
	 * <br>(Note: "default" has no meaning for required parameters.)
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder _default(String value) {
		if (value != null)
			this._default = value;
		return this;
	}

	/**
	 * <mk>maximum</mk> field.
	 *
	 * <p>
	 * Defines the maximum value for a parameter of numeric types.
	 *
	 * <p>
	 * Only allowed for the following types: <js>"integer"</js>, <js>"number"</js>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder maximum(Number value) {
		if (value != null)
			this.maximum = value;
		return this;
	}

	/**
	 * <mk>exclusiveMaximum</mk> field.
	 *
	 * <p>
	 * Defines whether the maximum is matched exclusively.
	 *
	 * <p>
	 * Only allowed for the following types: <js>"integer"</js>, <js>"number"</js>.
	 * <br>If <jk>true</jk>, must be accompanied with <c>maximum</c>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder exclusiveMaximum(Boolean value) {
		exclusiveMaximum = resolve(value, exclusiveMaximum);
		return this;
	}

	/**
	 * <mk>exclusiveMaximum</mk> field.
	 *
	 * <p>
	 * Same as {@link #exclusiveMaximum(Boolean)} but takes in a string boolean value.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder exclusiveMaximum(String value) {
		exclusiveMaximum = resolve(value, exclusiveMaximum);
		return this;
	}

	/**
	 * <mk>exclusiveMaximum</mk> field.
	 *
	 * <p>
	 * Shortcut for calling <code>exclusiveMaximum(<jk>true</jk>);</code>.
	 *
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder exclusiveMaximum() {
		return exclusiveMaximum(true);
	}

	/**
	 * <mk>minimum</mk> field.
	 *
	 * <p>
	 * Defines the minimum value for a parameter of numeric types.
	 *
	 * <p>
	 * Only allowed for the following types: <js>"integer"</js>, <js>"number"</js>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder minimum(Number value) {
		if (value != null)
			this.minimum = value;
		return this;
	}

	/**
	 * <mk>exclusiveMinimum</mk> field.
	 *
	 * <p>
	 * Defines whether the minimum is matched exclusively.
	 *
	 * <p>
	 * Only allowed for the following types: <js>"integer"</js>, <js>"number"</js>.
	 * <br>If <jk>true</jk>, must be accompanied with <c>minimum</c>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder exclusiveMinimum(Boolean value) {
		exclusiveMinimum = resolve(value, exclusiveMinimum);
		return this;
	}

	/**
	 * <mk>exclusiveMinimum</mk> field.
	 *
	 * <p>
	 * Same as {@link #exclusiveMinimum(Boolean)} but takes in a string boolean value.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder exclusiveMinimum(String value) {
		exclusiveMinimum = resolve(value, exclusiveMinimum);
		return this;
	}

	/**
	 * <mk>exclusiveMinimum</mk> field.
	 *
	 * <p>
	 * Shortcut for calling <code>exclusiveMinimum(<jk>true</jk>);</code>.
	 *
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder exclusiveMinimum() {
		return exclusiveMinimum(true);
	}

	/**
	 * <mk>maxLength</mk> field.
	 *
	 * <p>
	 * A string instance is valid against this keyword if its length is less than, or equal to, the value of this keyword.
	 * <br>The length of a string instance is defined as the number of its characters as defined by <a href='https://tools.ietf.org/html/rfc4627'>RFC 4627</a>.
	 *
	 * <p>
	 * Only allowed for the following types: <js>"string"</js>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or <c>-1</c>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder maxLength(Long value) {
		maxLength = resolve(value, maxLength);
		return this;
	}

	/**
	 * <mk>maxLength</mk> field.
	 *
	 * <p>
	 * Same as {@link #maxLength(Long)} but takes in a string number.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder maxLength(String value) {
		maxLength = resolve(value, maxLength);
		return this;
	}

	/**
	 * <mk>minLength</mk> field.
	 *
	 * <p>
	 * A string instance is valid against this keyword if its length is greater than, or equal to, the value of this keyword.
	 * <br>The length of a string instance is defined as the number of its characters as defined by <a href='https://tools.ietf.org/html/rfc4627'>RFC 4627</a>.
	 *
	 * <p>
	 * Only allowed for the following types: <js>"string"</js>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or <c>-1</c>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder minLength(Long value) {
		minLength = resolve(value, minLength);
		return this;
	}

	/**
	 * <mk>minLength</mk> field.
	 *
	 * <p>
	 * Same as {@link #minLength(Long)} but takes in a string number.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder minLength(String value) {
		minLength = resolve(value, minLength);
		return this;
	}

	/**
	 * <mk>pattern</mk> field.
	 *
	 * <p>
	 * A string input is valid if it matches the specified regular expression pattern.
	 *
	 * <p>
	 * Only allowed for the following types: <js>"string"</js>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder pattern(String value) {
		try {
			if (isNotEmpty(value))
				this.pattern = Pattern.compile(value);
		} catch (Exception e) {
			throw new ContextRuntimeException(e, "Invalid value {0} passed in as pattern value.  Must be a valid regular expression.", value);
		}
		return this;
	}

	/**
	 * <mk>maxItems</mk> field.
	 *
	 * <p>
	 * An array or collection is valid if its size is less than, or equal to, the value of this keyword.
	 *
	 * <p>
	 * Only allowed for the following types: <js>"array"</js>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or <c>-1</c>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder maxItems(Long value) {
		maxItems = resolve(value, maxItems);
		return this;
	}

	/**
	 * <mk>maxItems</mk> field.
	 *
	 * <p>
	 * Same as {@link #maxItems(Long)} but takes in a string number.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder maxItems(String value) {
		maxItems = resolve(value, maxItems);
		return this;
	}

	/**
	 * <mk>minItems</mk> field.
	 *
	 * <p>
	 * An array or collection is valid if its size is greater than, or equal to, the value of this keyword.
	 *
	 * <p>
	 * Only allowed for the following types: <js>"array"</js>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or <c>-1</c>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder minItems(Long value) {
		minItems = resolve(value, minItems);
		return this;
	}

	/**
	 * <mk>minItems</mk> field.
	 *
	 * <p>
	 * Same as {@link #minItems(Long)} but takes in a string number.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder minItems(String value) {
		minItems = resolve(value, minItems);
		return this;
	}

	/**
	 * <mk>uniqueItems</mk> field.
	 *
	 * <p>
	 * If <jk>true</jk>, the input validates successfully if all of its elements are unique.
	 *
	 * <p>
	 * <br>If the parameter type is a subclass of {@link Set}, this validation is skipped (since a set can only contain unique items anyway).
	 * <br>Otherwise, the collection or array is checked for duplicate items.
	 *
	 * <p>
	 * Only allowed for the following types: <js>"array"</js>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder uniqueItems(Boolean value) {
		uniqueItems = resolve(value, uniqueItems);
		return this;
	}

	/**
	 * <mk>uniqueItems</mk> field.
	 *
	 * <p>
	 * Same as {@link #uniqueItems(Boolean)} but takes in a string boolean.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty..
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder uniqueItems(String value) {
		uniqueItems = resolve(value, uniqueItems);
		return this;
	}

	/**
	 * <mk>uniqueItems</mk> field.
	 *
	 * <p>
	 * Shortcut for calling <code>uniqueItems(<jk>true</jk>);</code>.
	 *
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder uniqueItems() {
		return uniqueItems(true);
	}

	/**
	 * <mk>skipIfEmpty</mk> field.
	 *
	 * <p>
	 * Identifies whether an item should be skipped during serialization if it's empty.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder skipIfEmpty(Boolean value) {
		skipIfEmpty = resolve(value, skipIfEmpty);
		return this;
	}

	/**
	 * <mk>skipIfEmpty</mk> field.
	 *
	 * <p>
	 * Same as {@link #skipIfEmpty(Boolean)} but takes in a string boolean.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder skipIfEmpty(String value) {
		skipIfEmpty = resolve(value, skipIfEmpty);
		return this;
	}

	/**
	 * Identifies whether an item should be skipped if it's empty.
	 *
	 * <p>
	 * Shortcut for calling <code>skipIfEmpty(<jk>true</jk>);</code>.
	 *
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder skipIfEmpty() {
		return skipIfEmpty(true);
	}

	/**
	 * <mk>enum</mk> field.
	 *
	 * <p>
	 * If specified, the input validates successfully if it is equal to one of the elements in this array.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or an empty set.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder _enum(Set<String> value) {
		if (value != null && ! value.isEmpty())
			this._enum = value;
		return this;
	}

	/**
	 * <mk>_enum</mk> field.
	 *
	 * <p>
	 * Same as {@link #_enum(Set)} but takes in a var-args array.
	 *
	 * @param values
	 * 	The new values for this property.
	 * 	<br>Ignored if value is empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder _enum(String...values) {
		return _enum(ASet.of(values));
	}

	/**
	 * <mk>multipleOf</mk> field.
	 *
	 * <p>
	 * A numeric instance is valid if the result of the division of the instance by this keyword's value is an integer.
	 *
	 * <p>
	 * Only allowed for the following types: <js>"integer"</js>, <js>"number"</js>.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerParameterObject Parameter}
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * 	<li>{@doc SwaggerItemsObject Items}
	 * 	<li>{@doc SwaggerHeaderObject Header}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder multipleOf(Number value) {
		if (value != null)
			this.multipleOf = value;
		return this;
	}

	/**
	 * <mk>mapProperties</mk> field.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or <c>-1</c>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder maxProperties(Long value) {
		maxProperties = resolve(value, maxProperties);
		return this;
	}

	/**
	 * <mk>mapProperties</mk> field.
	 *
	 * <p>
	 * Same as {@link #maxProperties(Long)} but takes in a string number.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder maxProperties(String value) {
		maxProperties = resolve(value, maxProperties);
		return this;
	}

	/**
	 * <mk>minProperties</mk> field.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder minProperties(Long value) {
		minProperties = resolve(value, minProperties);
		return this;
	}

	/**
	 * <mk>minProperties</mk> field.
	 *
	 * <p>
	 * Same as {@link #minProperties(Long)} but takes in a string boolean.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder minProperties(String value) {
		minProperties = resolve(value, minProperties);
		return this;
	}

	/**
	 * <mk>properties</mk> field.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * </ul>
	 *
	 * @param key
	 *	The property name.
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder property(String key, HttpPartSchemaBuilder value) {
		if ( key != null && value != null) {
			if (properties == null)
				properties = new LinkedHashMap<>();
			properties.put(key, value);
		}
		return this;
	}

	private HttpPartSchemaBuilder properties(OMap value) {
		if (value != null && ! value.isEmpty())
		for (Map.Entry<String,Object> e : value.entrySet())
			property(e.getKey(), HttpPartSchema.create().apply((OMap)e.getValue()));
		return this;
	}

	/**
	 * <mk>additionalProperties</mk> field.
	 *
	 * <p>
	 * Applicable to the following Swagger schema objects:
	 * <ul>
	 * 	<li>{@doc SwaggerSchemaObject Schema}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or empty.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder additionalProperties(HttpPartSchemaBuilder value) {
		if (value != null)
			additionalProperties = value;
		return this;
	}

	private HttpPartSchemaBuilder additionalProperties(OMap value) {
		if (value != null && ! value.isEmpty())
			additionalProperties = HttpPartSchema.create().apply(value);
		return this;
	}

	/**
	 * Identifies the part serializer to use for serializing this part.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or {@link HttpPartSerializer.Null}.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder serializer(Class<? extends HttpPartSerializer> value) {
		if (value != null && value != HttpPartSerializer.Null.class)
			serializer = value;
		return this;
	}

	/**
	 * Identifies the part parser to use for parsing this part.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Ignored if value is <jk>null</jk> or {@link HttpPartParser.Null}.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder parser(Class<? extends HttpPartParser> value) {
		if (value != null && value != HttpPartParser.Null.class)
			parser = value;
		return this;
	}

	/**
	 * Disables Swagger schema usage validation checking.
	 *
	 * @param value Specify <jk>true</jk> to prevent {@link ContextRuntimeException} from being thrown if invalid Swagger usage was detected.
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder noValidate(Boolean value) {
		if (value != null)
			this.noValidate = value;
		return this;
	}

	/**
	 * Disables Swagger schema usage validation checking.
	 *
	 * <p>
	 * Shortcut for calling <code>noValidate(<jk>true</jk>);</code>.
	 *
	 * @return This object (for method chaining).
	 */
	public HttpPartSchemaBuilder noValidate() {
		return noValidate(true);
	}

	private Boolean resolve(String newValue, Boolean oldValue) {
		return isEmpty(newValue) ? oldValue : Boolean.valueOf(newValue);
	}

	private Boolean resolve(Boolean newValue, Boolean oldValue) {
		return newValue == null ? oldValue : newValue;
	}

	private Long resolve(String newValue, Long oldValue) {
		return isEmpty(newValue) ? oldValue : Long.parseLong(newValue);
	}

	private Long resolve(Long newValue, Long oldValue) {
		return (newValue == null || newValue == -1) ? oldValue : newValue;
	}
}