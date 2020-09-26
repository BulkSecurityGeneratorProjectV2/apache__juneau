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
package org.apache.juneau.rest;

import static org.apache.juneau.internal.ArrayUtils.*;
import static org.apache.juneau.internal.StringUtils.*;

import java.lang.reflect.*;
import java.util.*;

import javax.servlet.http.*;

import org.apache.juneau.*;
import org.apache.juneau.collections.*;
import org.apache.juneau.httppart.*;
import org.apache.juneau.internal.*;
import org.apache.juneau.json.*;
import org.apache.juneau.oapi.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.http.exception.*;
import org.apache.juneau.utils.*;

/**
 * Represents the query parameters in an HTTP request.
 *
 * <p>
 * Similar in functionality to the {@link HttpServletRequest#getParameter(String)} except only looks in the URL string, not parameters from
 * URL-Encoded FORM posts.
 * <br>This can be useful in cases where you're using GET parameters on FORM POSTs, and you don't want the body of the request to be read.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc RestmRequestQuery}
 * </ul>
 */
@SuppressWarnings("unchecked")
public final class RequestQuery extends LinkedHashMap<String,String[]> {
	private static final long serialVersionUID = 1L;

	private final RestRequest req;
	private HttpPartParserSession parser;

	RequestQuery(RestRequest req) {
		this.req = req;
	}

	RequestQuery parser(HttpPartParserSession parser) {
		this.parser = parser;
		return this;
	}

	/*
	 * Create a copy of the request query parameters.
	 */
	RequestQuery copy() {
		RequestQuery rq = new RequestQuery(req);
		rq.putAll(this);
		return rq;
	}

	/**
	 * Adds default entries to these query parameters.
	 *
	 * <p>
	 * This includes the default queries defined at the resource and method levels.
	 *
	 * @param defaultEntries
	 * 	The default entries.
	 * 	<br>Can be <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public RequestQuery addDefault(Map<String,Object> defaultEntries) {
		if (defaultEntries != null) {
			for (Map.Entry<String,Object> e : defaultEntries.entrySet()) {
				String key = e.getKey();
				Object value = e.getValue();
				String[] v = get(key);
				if (v == null || v.length == 0 || StringUtils.isEmpty(v[0]))
					put(key, stringifyAll(value));
			}
		}
		return this;
	}

	/**
	 * Adds a default entries to these query parameters.
	 *
	 * <p>
	 * Similar to {@link #put(String, Object)} but doesn't override existing values.
	 *
	 * @param name
	 * 	The query parameter name.
	 * @param value
	 * 	The query parameter value.
	 * 	<br>Converted to a String using <c>toString()</c>.
	 * 	<br>Ignored if value is <jk>null</jk> or blank.
	 * @return This object (for method chaining).
	 */
	public RequestQuery addDefault(String name, Object value) {
		return addDefault(Collections.singletonMap(name, value));
	}

	/**
	 * Same as {@link #get(Object)} but allows you to find the query parameter using a case-insensitive match.
	 *
	 * @param name The query parameter name.
	 * @param caseInsensitive If <jk>true</jk> use case-insensitive matching on the query parameter name.
	 * @return The resolved entry, or <jk>null</jk> if not found.
	 */
	public String[] get(String name, boolean caseInsensitive) {
		if (! caseInsensitive)
			return get(name);
		for (Map.Entry<String,String[]> e : entrySet())
			if (e.getKey().equalsIgnoreCase(name))
				return e.getValue();
		return null;
	}

	/**
	 * Sets a request query parameter value.
	 *
	 * <p>
	 * This overwrites any existing value.
	 *
	 * @param name The parameter name.
	 * @param value
	 * 	The parameter value.
	 * 	<br>Can be <jk>null</jk>.
	 */
	public void put(String name, Object value) {
		if (value == null)
			put(name, null);
		else
			put(name, stringifyAll(value));
	}

	/**
	 * Returns a query parameter value as a string.
	 *
	 * <p>
	 * If multiple query parameters have the same name, this returns only the first instance.
	 *
	 * @param name The URL parameter name.
	 * @return
	 * 	The parameter value, or <jk>null</jk> if parameter not specified or has no value (e.g. <js>"&amp;foo"</js>).
	 */
	public String getString(String name) {
		return getString(name, false);
	}

	/**
	 * Same as {@link #getString(String)} but allows you to search for the query parameter using case-insensitive matching.
	 *
	 * <p>
	 * If multiple query parameters have the same name, this returns only the first instance.
	 *
	 * @param name The URL parameter name.
	 * @param caseInsensitive If <jk>true</jk> use case insensitive matching on the query parameter name.
	 * @return
	 * 	The parameter value, or <jk>null</jk> if parameter not specified or has no value (e.g. <js>"&amp;foo"</js>).
	 */
	public String getString(String name, boolean caseInsensitive) {
		String[] v = get(name, caseInsensitive);
		if (v == null || v.length == 0)
			return null;

		// Fix for behavior difference between Tomcat and WAS.
		// getParameter("foo") on "&foo" in Tomcat returns "".
		// getParameter("foo") on "&foo" in WAS returns null.
		if (v.length == 1 && v[0] == null)
			return "";

		return v[0];
	}

	/**
	 * Same as {@link #getString(String)} but returns the specified default value if the query parameter was not
	 * specified.
	 *
	 * @param name The URL parameter name.
	 * @param def The default value.
	 * @return
	 * 	The parameter value, or the default value if parameter not specified or has no value (e.g. <js>"&amp;foo"</js>).
	 */
	public String getString(String name, String def) {
		String s = getString(name);
		return StringUtils.isEmpty(s) ? def : s;
	}

	/**
	 * Same as {@link #getString(String)} but converts the value to an integer.
	 *
	 * @param name The URL parameter name.
	 * @return
	 * 	The parameter value, or <c>0</c> if parameter not specified or has no value (e.g. <js>"&amp;foo"</js>).
	 */
	public int getInt(String name) {
		return getInt(name, 0);
	}

	/**
	 * Same as {@link #getString(String,String)} but converts the value to an integer.
	 *
	 * @param name The URL parameter name.
	 * @param def The default value.
	 * @return
	 * 	The parameter value, or the default value if parameter not specified or has no value (e.g. <js>"&amp;foo"</js>).
	 */
	public int getInt(String name, int def) {
		String s = getString(name);
		return StringUtils.isEmpty(s) ? def : Integer.parseInt(s);
	}

	/**
	 * Same as {@link #getString(String)} but converts the value to a boolean.
	 *
	 * @param name The URL parameter name.
	 * @return
	 * 	The parameter value, or <jk>false</jk> if parameter not specified or has no value (e.g. <js>"&amp;foo"</js>).
	 */
	public boolean getBoolean(String name) {
		return getBoolean(name, false);
	}

	/**
	 * Same as {@link #getString(String,String)} but converts the value to a boolean.
	 *
	 * @param name The URL parameter name.
	 * @param def The default value.
	 * @return
	 * 	The parameter value, or the default value if parameter not specified or has no value (e.g. <js>"&amp;foo"</js>).
	 */
	public boolean getBoolean(String name, boolean def) {
		String s = getString(name);
		return StringUtils.isEmpty(s) ? def : Boolean.parseBoolean(s);
	}

	/**
	 * Returns the specified query parameter value converted to a POJO using the {@link HttpPartParser} registered with the resource.
	 *
	 * <h5 class='section'>Examples:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Parse into an integer.</jc>
	 * 	<jk>int</jk> myparam = query.get(<js>"myparam"</js>, <jk>int</jk>.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into an int array.</jc>
	 * 	<jk>int</jk>[] myparam = query.get(<js>"myparam"</js>, <jk>int</jk>[].<jk>class</jk>);

	 * 	<jc>// Parse into a bean.</jc>
	 * 	MyBean myparam = query.get(<js>"myparam"</js>, MyBean.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a linked-list of objects.</jc>
	 * 	List myparam = query.get(<js>"myparam"</js>, LinkedList.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a map of object keys/values.</jc>
	 * 	Map myparam = query.get(<js>"myparam"</js>, TreeMap.<jk>class</jk>);
	 * </p>
	 *
	 * <ul class='seealso'>
	 * 	<li class='jf'>{@link RestContext#REST_partParser}
	 * </ul>
	 *
	 * @param name The parameter name.
	 * @param type The class type to convert the parameter value to.
	 * @param <T> The class type to convert the parameter value to.
	 * @return The parameter value converted to the specified class type.
	 * @throws BadRequest Thrown if input could not be parsed.
	 * @throws InternalServerError Thrown if any other exception occurs.
	 */
	public <T> T get(String name, Class<T> type) throws BadRequest, InternalServerError {
		return getInner(null, null, name, null, getClassMeta(type));
	}

	/**
	 * Same as {@link #get(String, Class)} but allows you to override the part parser.
	 *
	 * @param parser
	 * 	The parser to use for parsing the string value.
	 * 	<br>If <jk>null</jk>, uses the part parser defined on the resource/method.
	 * @param schema
	 * 	The schema object that defines the format of the input.
	 * 	<br>If <jk>null</jk>, defaults to the schema defined on the parser.
	 * 	<br>If that's also <jk>null</jk>, defaults to {@link HttpPartSchema#DEFAULT}.
	 * 	<br>Only used if parser is schema-aware (e.g. {@link OpenApiParser}).
	 * @param name The parameter name.
	 * @param type The class type to convert the parameter value to.
	 * @param <T> The class type to convert the parameter value to.
	 * @return The parameter value converted to the specified class type.
	 * @throws BadRequest Thrown if input could not be parsed or fails schema validation.
	 * @throws InternalServerError Thrown if any other exception occurs.
	 */
	public <T> T get(HttpPartParserSession parser, HttpPartSchema schema, String name, Class<T> type) throws BadRequest, InternalServerError {
		return getInner(parser, schema, name, null, getClassMeta(type));
	}

	/**
	 * Same as {@link #get(String, Class)} except returns a default value if not found.
	 *
	 * @param name The parameter name.
	 * @param def The default value if the parameter was not specified or is <jk>null</jk>.
	 * @param type The class type to convert the parameter value to.
	 * @param <T> The class type to convert the parameter value to.
	 * @return The parameter value converted to the specified class type.
	 * @throws BadRequest Thrown if input could not be parsed.
	 * @throws InternalServerError Thrown if any other exception occurs.
	 */
	public <T> T get(String name, T def, Class<T> type) throws BadRequest, InternalServerError {
		return getInner(null, null, name, def, getClassMeta(type));
	}

	/**
	 * Same as {@link #get(String, Object, Class)} but allows you to override the part parser.
	 *
	 * @param parser
	 * 	The parser to use for parsing the string value.
	 * 	<br>If <jk>null</jk>, uses the part parser defined on the resource/method.
	 * @param schema
	 * 	The schema object that defines the format of the input.
	 * 	<br>If <jk>null</jk>, defaults to the schema defined on the parser.
	 * 	<br>If that's also <jk>null</jk>, defaults to {@link HttpPartSchema#DEFAULT}.
	 * 	<br>Only used if parser is schema-aware (e.g. {@link OpenApiParser}).
	 * @param name The parameter name.
	 * @param def The default value if the parameter was not specified or is <jk>null</jk>.
	 * @param type The class type to convert the parameter value to.
	 * @param <T> The class type to convert the parameter value to.
	 * @return The parameter value converted to the specified class type.
	 * @throws BadRequest Thrown if input could not be parsed or fails schema validation.
	 * @throws InternalServerError Thrown if any other exception occurs.
	 */
	public <T> T get(HttpPartParserSession parser, HttpPartSchema schema, String name, T def, Class<T> type) throws BadRequest, InternalServerError {
		return getInner(parser, schema, name, def, getClassMeta(type));
	}

	/**
	 * Returns the specified query parameter value converted to a POJO using the {@link HttpPartParser} registered with the resource.
	 *
	 * <p>
	 * Similar to {@link #get(String,Class)} but allows for complex collections of POJOs to be created.
	 *
	 * <h5 class='section'>Examples:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Parse into a linked-list of strings.</jc>
	 * 	List&lt;String&gt; myparam = query.get(<js>"myparam"</js>, LinkedList.<jk>class</jk>, String.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a linked-list of linked-lists of strings.</jc>
	 * 	List&lt;List&lt;String&gt;&gt; myparam = query.get(<js>"myparam"</js>, LinkedList.<jk>class</jk>, LinkedList.<jk>class</jk>, String.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a map of string keys/values.</jc>
	 * 	Map&lt;String,String&gt; myparam = query.get(<js>"myparam"</js>, TreeMap.<jk>class</jk>, String.<jk>class</jk>, String.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a map containing string keys and values of lists containing beans.</jc>
	 * 	Map&lt;String,List&lt;MyBean&gt;&gt; myparam = query.get(<js>"myparam"</js>, TreeMap.<jk>class</jk>, String.<jk>class</jk>, List.<jk>class</jk>, MyBean.<jk>class</jk>);
	 * </p>
	 *
	 * <ul class='notes'>
	 * 	<li>
	 * 		<c>Collections</c> must be followed by zero or one parameter representing the value type.
	 * 	<li>
	 * 		<c>Maps</c> must be followed by zero or two parameters representing the key and value types.
	 * </ul>
	 *
	 * <ul class='seealso'>
	 * 	<li class='jf'>{@link RestContext#REST_partParser}
	 * </ul>
	 *
	 * @param name The parameter name.
	 * @param type
	 * 	The type of object to create.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * @param args
	 * 	The type arguments of the class if it's a collection or map.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * 	<br>Ignored if the main type is not a map or collection.
	 * @param <T> The class type to convert the parameter value to.
	 * @return The parameter value converted to the specified class type.
	 * @throws BadRequest Thrown if input could not be parsed.
	 * @throws InternalServerError Thrown if any other exception occurs.
	 */
	public <T> T get(String name, Type type, Type...args) throws BadRequest, InternalServerError {
		return getInner(null, null, name, null, (ClassMeta<T>)getClassMeta(type, args));
	}

	/**
	 * Same as {@link #get(String, Type, Type...)} but allows you to override the part parser.
	 *
	 * @param parser
	 * 	The parser to use for parsing the string value.
	 * 	<br>If <jk>null</jk>, uses the part parser defined on the resource/method.
	 * @param schema
	 * 	The schema object that defines the format of the input.
	 * 	<br>If <jk>null</jk>, defaults to the schema defined on the parser.
	 * 	<br>If that's also <jk>null</jk>, defaults to {@link HttpPartSchema#DEFAULT}.
	 * 	<br>Only used if parser is schema-aware (e.g. {@link OpenApiParser}).
	 * @param name The parameter name.
	 * @param type
	 * 	The type of object to create.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * @param args
	 * 	The type arguments of the class if it's a collection or map.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * 	<br>Ignored if the main type is not a map or collection.
	 * @param <T> The class type to convert the parameter value to.
	 * @return The parameter value converted to the specified class type.
	 * @throws BadRequest Thrown if input could not be parsed or fails schema validation.
	 * @throws InternalServerError Thrown if any other exception occurs.
	 */
	public <T> T get(HttpPartParserSession parser, HttpPartSchema schema, String name, Type type, Type...args) throws BadRequest, InternalServerError {
		return getInner(parser, schema, name, null, (ClassMeta<T>)getClassMeta(type, args));
	}

	/**
	 * Same as {@link #get(String, Class)} except returns a default value if not found.
	 *
	 * @param name The parameter name.
	 * @param type
	 * 	The type of object to create.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * @param args
	 * 	The type arguments of the class if it's a collection or map.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * 	<br>Ignored if the main type is not a map or collection.
	 * @param def The default value if the parameter was not specified or is <jk>null</jk>.
	 * @param <T> The class type to convert the parameter value to.
	 * @return The parameter value converted to the specified class type.
	 * @throws BadRequest Thrown if input could not be parsed.
	 * @throws InternalServerError Thrown if any other exception occurs.
	 */
	public <T> T get(String name, T def, Type type, Type...args) throws BadRequest, InternalServerError {
		return getInner(null, null, name, def, (ClassMeta<T>)getClassMeta(type, args));
	}

	/**
	 * Same as {@link #get(String, Object, Type, Type...)} but allows you to override the part parser.
	 *
	 * @param parser
	 * 	The parser to use for parsing the string value.
	 * 	<br>If <jk>null</jk>, uses the part parser defined on the resource/method.
	 * @param schema
	 * 	The schema object that defines the format of the input.
	 * 	<br>If <jk>null</jk>, defaults to the schema defined on the parser.
	 * 	<br>If that's also <jk>null</jk>, defaults to {@link HttpPartSchema#DEFAULT}.
	 * 	<br>Only used if parser is schema-aware (e.g. {@link OpenApiParser}).
	 * @param name The parameter name.
	 * @param type
	 * 	The type of object to create.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * @param args
	 * 	The type arguments of the class if it's a collection or map.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * 	<br>Ignored if the main type is not a map or collection.
	 * @param def The default value if the parameter was not specified or is <jk>null</jk>.
	 * @param <T> The class type to convert the parameter value to.
	 * @return The parameter value converted to the specified class type.
	 * @throws BadRequest Thrown if input could not be parsed or fails schema validation.
	 * @throws InternalServerError Thrown if any other exception occurs.
	 */
	public <T> T get(HttpPartParserSession parser, HttpPartSchema schema, String name, T def, Type type, Type...args) throws BadRequest, InternalServerError {
		return getInner(parser, schema, name, def, (ClassMeta<T>)getClassMeta(type, args));
	}

	/**
	 * Same as {@link #get(String, Class)} except for use on multi-part parameters
	 * (e.g. <js>"&amp;key=1&amp;key=2&amp;key=3"</js> instead of <js>"&amp;key=@(1,2,3)"</js>).
	 *
	 * <p>
	 * This method must only be called when parsing into classes of type Collection or array.
	 *
	 * @param name The query parameter name.
	 * @param c The class type to convert the parameter value to.
	 * @param <T> The class type to convert the parameter value to.
	 * @return The query parameter value converted to the specified class type.
	 * @throws BadRequest Thrown if input could not be parsed.
	 * @throws InternalServerError Thrown if any other exception occurs.
	 */
	public <T> T getAll(String name, Class<T> c) throws BadRequest, InternalServerError {
		return getAllInner(null, null, name, getClassMeta(c));
	}

	/**
	 * Same as {@link #get(String, Type, Type...)} except for use on multi-part parameters
	 * (e.g. <js>"&amp;key=1&amp;key=2&amp;key=3"</js> instead of <js>"&amp;key=@(1,2,3)"</js>).
	 *
	 * <p>
	 * This method must only be called when parsing into classes of type Collection or array.
	 *
	 * @param name The query parameter name.
	 * @param type
	 * 	The type of object to create.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * @param args
	 * 	The type arguments of the class if it's a collection or map.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * 	<br>Ignored if the main type is not a map or collection.
	 * @param <T> The class type to convert the parameter value to.
	 * @return The query parameter value converted to the specified class type.
	 * @throws BadRequest Thrown if input could not be parsed.
	 * @throws InternalServerError Thrown if any other exception occurs.
	 */
	public <T> T getAll(String name, Type type, Type...args) throws BadRequest, InternalServerError {
		return getAllInner(null, null, name, (ClassMeta<T>)getClassMeta(type, args));
	}

	/**
	 * Same as {@link #getAll(String, Type, Type...)} but allows you to override the part parser.
	 *
	 * @param parser
	 * 	The parser to use for parsing the string value.
	 * 	<br>If <jk>null</jk>, uses the part parser defined on the resource/method.
	 * @param schema
	 * 	The schema object that defines the format of the input.
	 * 	<br>If <jk>null</jk>, defaults to the schema defined on the parser.
	 * 	<br>If that's also <jk>null</jk>, defaults to {@link HttpPartSchema#DEFAULT}.
	 * 	<br>Only used if parser is schema-aware (e.g. {@link OpenApiParser}).
	 * @param name The query parameter name.
	 * @param type
	 * 	The type of object to create.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * @param args
	 * 	The type arguments of the class if it's a collection or map.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * 	<br>Ignored if the main type is not a map or collection.
	 * @param <T> The class type to convert the parameter value to.
	 * @return The query parameter value converted to the specified class type.
	 * @throws BadRequest Thrown if input could not be parsed or fails schema validation.
	 * @throws InternalServerError Thrown if any other exception occurs.
	 */
	public <T> T getAll(HttpPartParserSession parser, HttpPartSchema schema, String name, Type type, Type...args) throws BadRequest, InternalServerError {
		return getAllInner(parser, schema, name, getClassMeta(type, args));
	}

	/**
	 * Returns <jk>true</jk> if the request contains any of the specified query parameters.
	 *
	 * @param params The list of parameters to check for.
	 * @return <jk>true</jk> if the request contains any of the specified query parameters.
	 */
	public boolean containsAnyKeys(String...params) {
		for (String p : params)
			if (containsKey(p))
				return true;
		return false;
	}

	/**
	 * Locates the special search query arguments in the query and returns them as a {@link SearchArgs} object.
	 *
	 * <p>
	 * The query arguments are as follows:
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		<js>"&amp;s="</js> - A comma-delimited list of column-name/search-token pairs.
	 * 		<br>Example: <js>"&amp;s=column1=foo*,column2=*bar"</js>
	 * 	<li>
	 * 		<js>"&amp;v="</js> - A comma-delimited list column names to view.
	 * 		<br>Example: <js>"&amp;v=column1,column2"</js>
	 * 	<li>
	 * 		<js>"&amp;o="</js> - A comma-delimited list column names to sort by.
	 * 		<br>Column names can be suffixed with <js>'-'</js> to indicate descending order.
	 * 		<br>Example: <js>"&amp;o=column1,column2-"</js>
	 * 	<li>
	 * 		<js>"&amp;p="</js> - The zero-index row number of the first row to display.
	 * 		<br>Example: <js>"&amp;p=100"</js>
	 * 	<li>
	 * 		<js>"&amp;l="</js> - The number of rows to return.
	 * 		<br><c>0</c> implies return all rows.
	 * 		<br>Example: <js>"&amp;l=100"</js>
	 * 	<li>
	 * 		<js>"&amp;i="</js> - The case-insensitive search flag.
	 * 		<br>Example: <js>"&amp;i=true"</js>
	 * </ul>
	 *
	 * <ul class='notes'>
	 * 	<li>
	 * 		Whitespace is trimmed in the parameters.
	 * </ul>
	 *
	 * @return
	 * 	A new {@link SearchArgs} object initialized with the special search query arguments.
	 * 	<br>Returns <jk>null</jk> if no search arguments were found.
	 */
	public SearchArgs getSearchArgs() {
		if (hasAny("s","v","o","p","l","i")) {
			return new SearchArgs.Builder()
				.search(getString("s"))
				.view(getString("v"))
				.sort(getString("o"))
				.position(getInt("p"))
				.limit(getInt("l"))
				.ignoreCase(getBoolean("i"))
				.build();
		}
		return null;
	}

	/**
	 * Returns <jk>true</jk> if the query parameters contains any of the specified names.
	 *
	 * @param paramNames The parameter names to check for.
	 * @return <jk>true</jk> if the query parameters contains any of the specified names.
	 */
	public boolean hasAny(String...paramNames) {
		for (String p : paramNames)
			if (containsKey(p))
				return true;
		return false;
	}

	/* Workhorse method */
	private <T> T getInner(HttpPartParserSession parser, HttpPartSchema schema, String name, T def, ClassMeta<T> cm) throws BadRequest, InternalServerError {
		if (parser == null)
			parser = req.getPartParser();
		try {
			if (cm.isMapOrBean() && isOneOf(name, "*", "")) {
				OMap m = new OMap();
				for (Map.Entry<String,String[]> e : this.entrySet()) {
					String k = e.getKey();
					HttpPartSchema pschema = schema == null ? null : schema.getProperty(k);
					ClassMeta<?> cm2 = cm.getValueType();
					if (cm.getValueType().isCollectionOrArray())
						m.put(k, getAllInner(parser, pschema, k, cm2));
					else
						m.put(k, getInner(parser, pschema, k, null, cm2));
				}
				return req.getBeanSession().convertToType(m, cm);
			}
			T t = parse(parser, schema, getString(name), cm);
			return (t == null ? def : t);
		} catch (SchemaValidationException e) {
			throw new BadRequest(e, "Validation failed on query parameter ''{0}''. ", name);
		} catch (ParseException e) {
			throw new BadRequest(e, "Could not parse query parameter ''{0}''.", name) ;
		} catch (Exception e) {
			throw new InternalServerError(e, "Could not parse query parameter ''{0}''.", name) ;
		}
	}

	/* Workhorse method */
	@SuppressWarnings("rawtypes")
	private <T> T getAllInner(HttpPartParserSession parser, HttpPartSchema schema, String name, ClassMeta<T> cm) throws BadRequest, InternalServerError {
		String[] p = get(name);
		if (schema == null)
			schema = HttpPartSchema.DEFAULT;
		try {
			if (cm.isArray()) {
				List c = new ArrayList();
				for (int i = 0; i < p.length; i++)
					c.add(parse(parser, schema.getItems(), p[i], cm.getElementType()));
				return (T)toArray(c, cm.getElementType().getInnerClass());
			} else if (cm.isCollection()) {
				Collection c = (Collection)(cm.canCreateNewInstance() ? cm.newInstance() : new OList());
				for (int i = 0; i < p.length; i++)
					c.add(parse(parser, schema.getItems(), p[i], cm.getElementType()));
				return (T)c;
			}
		} catch (SchemaValidationException e) {
			throw new BadRequest(e, "Validation failed on query parameter ''{0}''. ", name);
		} catch (ParseException e) {
			throw new BadRequest(e, "Could not parse query parameter ''{0}''.", name) ;
		} catch (Exception e) {
			throw new InternalServerError(e, "Could not parse query parameter ''{0}''.", name) ;
		}
		throw new InternalServerError("Invalid call to getParameters(String, ClassMeta).  Class type must be a Collection or array.");
	}

	private <T> T parse(HttpPartParserSession parser, HttpPartSchema schema, String val, ClassMeta<T> c) throws SchemaValidationException, ParseException {
		if (parser == null)
			parser = this.parser;
		return parser.parse(HttpPartType.QUERY, schema, val, c);
	}

	/**
	 * Converts the query parameters to a readable string.
	 *
	 * @param sorted Sort the query parameters by name.
	 * @return A JSON string containing the contents of the query parameters.
	 */
	public String toString(boolean sorted) {
		Map<String,Object> m = null;
		if (sorted)
			m = new TreeMap<>();
		else
			m = new LinkedHashMap<>();
		for (Map.Entry<String,String[]> e : this.entrySet()) {
			String[] v = e.getValue();
			m.put(e.getKey(), v.length == 1 ? v[0] : v);
		}
		return SimpleJsonSerializer.DEFAULT.toString(m);
	}

	/**
	 * Converts this object to a query string.
	 *
	 * <p>
	 * Returned query string does not start with <js>'?'</js>.
	 *
	 * @return A new query string, or an empty string if this object is empty.
	 */
	public String asQueryString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String,String[]> e : this.entrySet()) {
			for (int i = 0; i < e.getValue().length; i++) {
				if (sb.length() > 0)
					sb.append("&");
				sb.append(urlEncode(e.getKey())).append('=').append(urlEncode(e.getValue()[i]));
			}
		}
		return sb.toString();
	}

	@Override /* Object */
	public String toString() {
		return toString(false);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Helper methods
	//-----------------------------------------------------------------------------------------------------------------

	private <T> ClassMeta<T> getClassMeta(Type type, Type...args) {
		return req.getBeanSession().getClassMeta(type, args);
	}

	private <T> ClassMeta<T> getClassMeta(Class<T> type) {
		return req.getBeanSession().getClassMeta(type);
	}
}
