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

import static java.util.Collections.*;
import static java.util.logging.Level.*;
import static org.apache.juneau.html.HtmlDocSerializer.*;
import static org.apache.juneau.httppart.HttpPartType.*;
import static org.apache.juneau.internal.IOUtils.*;
import static org.apache.juneau.serializer.Serializer.*;

import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.*;
import java.nio.charset.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.juneau.*;
import org.apache.juneau.config.*;
import org.apache.juneau.dto.swagger.*;
import org.apache.juneau.http.*;
import org.apache.juneau.http.ReaderResource;
import org.apache.juneau.http.StreamResource;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.http.annotation.Body;
import org.apache.juneau.http.annotation.FormData;
import org.apache.juneau.http.annotation.Header;
import org.apache.juneau.http.annotation.Path;
import org.apache.juneau.http.annotation.Query;
import org.apache.juneau.http.annotation.Response;
import org.apache.juneau.httppart.*;
import org.apache.juneau.httppart.bean.*;
import org.apache.juneau.internal.*;
import org.apache.juneau.jsonschema.*;
import org.apache.juneau.oapi.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.remote.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.rest.exception.*;
import org.apache.juneau.rest.helper.*;
import org.apache.juneau.rest.util.*;
import org.apache.juneau.rest.util.RestUtils;
import org.apache.juneau.rest.widget.*;
import org.apache.juneau.serializer.*;
import org.apache.juneau.svl.*;
import org.apache.juneau.uon.*;
import org.apache.juneau.utils.*;

/**
 * Represents an HTTP request for a REST resource.
 *
 * <p>
 * Equivalent to {@link HttpServletRequest} except with some additional convenience methods.
 *
 * <p>
 * For reference, given the URL <js>"http://localhost:9080/contextRoot/servletPath/foo?bar=baz#qux"</js>, the
 * following methods return the following values....
 * <table class='styled'>
 * 	<tr><th>Method</th><th>Value</th></tr>
 * 	<tr><td>{@code getContextPath()}</td><td>{@code /contextRoot}</td></tr>
 * 	<tr><td>{@code getPathInfo()}</td><td>{@code /foo}</td></tr>
 * 	<tr><td>{@code getPathTranslated()}</td><td>{@code path-to-deployed-war-on-filesystem/foo}</td></tr>
 * 	<tr><td>{@code getQueryString()}</td><td>{@code bar=baz}</td></tr>
 * 	<tr><td>{@code getRequestURI()}</td><td>{@code /contextRoot/servletPath/foo}</td></tr>
 * 	<tr><td>{@code getRequestURL()}</td><td>{@code http://localhost:9080/contextRoot/servletPath/foo}</td></tr>
 * 	<tr><td>{@code getServletPath()}</td><td>{@code /servletPath}</td></tr>
 * </table>
 *
 * <h5 class='section'>See Also:</h5>
 * <ul>
 * 	<li class='link'>{@doc juneau-rest-server.RestMethod.RestRequest}
 * </ul>
 */
@SuppressWarnings({ "unchecked", "unused" })
public final class RestRequest extends HttpServletRequestWrapper {

	private final RestContext context;
	private RestMethodContext restJavaMethod;

	private final String method;
	private RequestBody body;
	private Method javaMethod;
	@SuppressWarnings("deprecation")
	private RequestProperties properties;
	private final boolean debug;
	private BeanSession beanSession;
	private VarResolverSession varSession;
	private final RequestQuery queryParams;
	private RequestFormData formData;
	private RequestPath pathParams;
	private boolean isPost;
	private UriContext uriContext;
	private String charset, authorityPath;
	private RequestHeaders headers;
	private RequestAttributes attributes;
	private Config cf;
	private Swagger swagger;
	private SerializerSessionArgs serializerSessionArgs;
	private ParserSessionArgs parserSessionArgs;
	private RestResponse res;

	/**
	 * Constructor.
	 */
	RestRequest(RestContext context, HttpServletRequest req) throws ServletException {
		super(req);
		this.context = context;

		try {
			isPost = req.getMethod().equalsIgnoreCase("POST");

			// If this is a POST, we want to parse the query parameters ourselves to prevent
			// the servlet code from processing the HTTP body as URL-Encoded parameters.
			queryParams = new RequestQuery(this);
			if (isPost)
				RestUtils.parseQuery(getQueryString(), queryParams);
			else
				queryParams.putAll(req.getParameterMap());

			// Get the HTTP method.
			// Can be overridden through a "method" GET attribute.
			String _method = super.getMethod();

			String m = getQuery().getString("method");
			if (m != null) {
				Set<String> s = context.getAllowedMethodParams();
				if (! s.isEmpty() && (s.contains("*") || s.contains(m)))
					_method = m;
			}

			m = req.getHeader("X-Method");
			if (m != null) {
				Set<String> s = context.getAllowedMethodHeaders();
				if (! s.isEmpty() && (s.contains("*") || s.contains(m)))
					_method = m;
			}

			method = _method;

			headers = new RequestHeaders(this);
			for (Enumeration<String> e = getHeaderNames(); e.hasMoreElements();) {
				String name = e.nextElement();
				headers.put(name, super.getHeaders(name));
			}

			body = new RequestBody(this);

			if (context.isAllowBodyParam()) {
				String b = getQuery().getString("body");
				if (b != null) {
					headers.put("Content-Type", UonSerializer.DEFAULT.getResponseContentType());
					body.load(MediaType.UON, UonParser.DEFAULT, b.getBytes(UTF8));
				}
			}

			Set<String> s = context.getAllowedHeaderParams();
			if (! s.isEmpty()) {
				if (s.contains("*"))
					headers.queryParams(queryParams);
				else for (String q : s) {
					Object o = queryParams.getString(q);
					if (o != null)
						headers.put(q, o);
				}
			}

			debug = "true".equals(getQuery().getString("debug", "false")) || "true".equals(getHeaders().getString("Debug", "false"));

			this.pathParams = new RequestPath(this);

		} catch (RestException e) {
			throw e;
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/*
	 * Called from RestServlet after a match has been made but before the guard or method invocation.
	 */
	final void init(RestMethodContext rjm, @SuppressWarnings("deprecation") RequestProperties properties) {
		this.restJavaMethod = rjm;
		this.javaMethod = rjm.method;
		this.properties = properties;
		this.beanSession = rjm.createSession();
		this.pathParams
			.parser(rjm.partParser);
		this.queryParams
			.addDefault(rjm.defaultQuery)
			.parser(rjm.partParser);
		this.headers
			.addDefault(rjm.defaultRequestHeaders)
			.addDefault(context.getDefaultRequestHeaders())
			.parser(rjm.partParser);
		this.attributes = new RequestAttributes(this, rjm.defaultRequestAttributes);
		this.body
			.encoders(rjm.encoders)
			.parsers(rjm.parsers)
			.headers(headers)
			.maxInput(rjm.maxInput);

		String stylesheet = getQuery().getString("stylesheet");
		if (stylesheet != null)
			getSession().setAttribute("stylesheet", stylesheet.replace(' ', '$'));  // Prevent SVL insertion.
		stylesheet = (String)getSession().getAttribute("stylesheet");
		if (stylesheet != null)
			properties.put(HTMLDOC_stylesheet, new String[]{stylesheet});

		if (debug) {
			String msg = ""
				+ "\n=== HTTP Request (incoming) ===================================================="
				+ toString()
				+ "\n=== END ========================================================================";
			context.getLogger().log(Level.WARNING, msg);
		}
	}

	RestRequest setResponse(RestResponse res) {
		this.res = res;
		return this;
	}

	/**
	 * Returns a string of the form <js>"HTTP method-name full-url"</js>
	 *
	 * @return A description string of the request.
	 */
	public String getDescription() {
		String qs = getQueryString();
		return "HTTP " + getMethod() + " " + getRequestURI() + (qs == null ? "" : "?" + qs);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Retrieve the properties active for this request.
	 *
	 * <p>
	 * This contains all resource and method level properties from the following:
	 * <ul>
	 * 	<li class='ja'>{@link RestResource#properties()}
	 * 	<li class='ja'>{@link RestMethod#properties()}
	 * 	<li class='jm'>{@link RestContextBuilder#set(String, Object)}
	 * </ul>
	 *
	 * <p>
	 * The returned object is modifiable and allows you to override session-level properties before
	 * they get passed to the serializers.
	 * <br>However, properties are open-ended, and can be used for any purpose.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(
	 * 		properties={
	 * 			<ja>@Property</ja>(name=<jsf>SERIALIZER_sortMaps</jsf>, value=<js>"false"</js>)
	 * 		}
	 * 	)
	 * 	<jk>public</jk> Map doGet(RestRequest req, <ja>@Query</ja>(<js>"sortMaps"</js>) Boolean sortMaps) {
	 *
	 * 		<jc>// Override value if specified through query parameter.</jc>
	 * 		<jk>if</jk> (sortMaps != <jk>null</jk>)
	 * 			req.getProperties().put(<jsf>SERIALIZER_sortMaps</jsf>, sortMaps);
	 *
	 * 		<jk>return</jk> <jsm>getMyMap</jsm>();
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jm'>{@link #prop(String, Object)}
	 * 	<li class='link'>{@doc juneau-rest-server.Properties}
	 * </ul>
	 *
	 * @return The properties active for this request.
	 * @deprecated Use {@link #getAttributes()}
	 */
	@Deprecated
	public RequestProperties getProperties() {
		return this.properties;
	}

	/**
	 * Shortcut for calling <code>getProperties().append(name, value);</code> fluently.
	 *
	 * @param name The property name.
	 * @param value The property value.
	 * @return This object (for method chaining).
	 * @deprecated Use {@link RequestAttributes#put(String, Object)} or {@link #setAttribute(String, Object)}.
	 */
	@Deprecated
	public RestRequest prop(String name, Object value) {
		this.properties.append(name, value);
		return this;
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Headers
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Request headers.
	 *
	 * <p>
	 * Returns a {@link RequestHeaders} object that encapsulates access to HTTP headers on the request.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(...)
	 * 	<jk>public</jk> Object myMethod(RestRequest req) {
	 *
	 * 		<jc>// Get access to headers.</jc>
	 * 		RequestHeaders h = req.getHeaders();
	 *
	 * 		<jc>// Add a default value.</jc>
	 * 		h.addDefault(<js>"ETag"</js>, <jsf>DEFAULT_UUID</jsf>);
	 *
	 *  	<jc>// Get a header value as a POJO.</jc>
	 * 		UUID etag = h.get(<js>"ETag"</js>, UUID.<jk>class</jk>);
	 *
	 * 		<jc>// Get a standard header.</jc>
	 * 		CacheControl = h.getCacheControl();
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		This object is modifiable.
	 * 	<li>
	 * 		Values are converted from strings using the registered {@link RestContext#REST_partParser part-parser} on the resource class.
	 * 	<li>
	 * 		The {@link RequestHeaders} object can also be passed as a parameter on the method.
	 * 	<li>
	 * 		The {@link Header @Header} annotation can be used to access individual header values.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='link'>{@doc juneau-rest-server.RestMethod.RequestHeaders}
	 * </ul>
	 *
	 * @return
	 * 	The headers on this request.
	 * 	<br>Never <jk>null</jk>.
	 */
	public RequestHeaders getHeaders() {
		return headers;
	}

	@Override /* ServletRequest */
	public String getHeader(String name) {
		return getHeaders().getString(name);
	}

	@Override /* ServletRequest */
	public Enumeration<String> getHeaders(String name) {
		String[] v = headers.get(name);
		if (v == null || v.length == 0)
			return Collections.enumeration(Collections.EMPTY_LIST);
		return Collections.enumeration(Arrays.asList(v));
	}

	/**
	 * Returns the media types that are valid for <code>Accept</code> headers on the request.
	 *
	 * @return The set of media types registered in the serializer group of this request.
	 */
	public List<MediaType> getProduces() {
		return restJavaMethod == null ? Collections.<MediaType>emptyList() : restJavaMethod.supportedAcceptTypes;
	}

	/**
	 * Returns the media types that are valid for <code>Content-Type</code> headers on the request.
	 *
	 * @return The set of media types registered in the parser group of this request.
	 */
	public List<MediaType> getConsumes() {
		return restJavaMethod == null ? Collections.<MediaType>emptyList() : restJavaMethod.supportedContentTypes;
	}

	/**
	 * Returns the {@link PropertyStore} for this request.
	 *
	 * <p>
	 * Consists of a read-only roll-up of all configuration properties defined on this method and class.
	 *
	 * @return
	 * 	The property store for this request.
	 * 	<br>Never <jk>null</jk>.
	 */
	public PropertyStore getPropertyStore() {
		return restJavaMethod == null ? PropertyStore.DEFAULT : restJavaMethod.getPropertyStore();
	}

	/**
	 * Sets the charset to expect on the request body.
	 */
	@Override /* ServletRequest */
	public void setCharacterEncoding(String charset) {
		this.charset = charset;
	}

	/**
	 * Returns the charset specified on the <code>Content-Type</code> header, or <js>"UTF-8"</js> if not specified.
	 */
	@Override /* ServletRequest */
	public String getCharacterEncoding() throws UnsupportedMediaType {
		if (charset == null) {
			// Determine charset
			// NOTE:  Don't use super.getCharacterEncoding() because the spec is implemented inconsistently.
			// Jetty returns the default charset instead of null if the character is not specified on the request.
			String h = getHeader("Content-Type");
			if (h != null) {
				int i = h.indexOf(";charset=");
				if (i > 0)
					charset = h.substring(i+9).trim();
			}
			if (charset == null && restJavaMethod != null)
				charset = restJavaMethod.defaultCharset;
			if (charset == null)
				charset = "UTF-8";
			if (! Charset.isSupported(charset))
				throw new UnsupportedMediaType("Unsupported charset in header ''Content-Type'': ''{0}''", h);
		}
		return charset;
	}

	/**
	 * Wrapper around {@link #getCharacterEncoding()} that converts the value to a {@link Charset}.
	 *
	 * @return The request character encoding converted to a {@link Charset}.
	 */
	public Charset getCharset() {
		String s = getCharacterEncoding();
		return s == null ? null : Charset.forName(s);
	}

	@Override /* ServletRequest */
	public Locale getLocale() {
		String h = headers.getString("Accept-Language");
		if (h != null) {
			MediaTypeRange[] mr = MediaTypeRange.parse(h);
			if (mr.length > 0)
				return toLocale(mr[0].getMediaType().getType());
		}
		return super.getLocale();
	}

	@Override /* ServletRequest */
	public Enumeration<Locale> getLocales() {
		String h = headers.getString("Accept-Language");
		if (h != null) {
			MediaTypeRange[] mr = MediaTypeRange.parse(h);
			if (mr.length > 0) {
				List<Locale> l = new ArrayList<>(mr.length);
				for (MediaTypeRange r : mr)
					l.add(toLocale(r.getMediaType().getType()));
				return enumeration(l);
			}
		}
		return super.getLocales();
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Attributes
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Request attributes.
	 *
	 * <p>
	 * Returns a {@link RequestAttributes} object that encapsulates access to attributes on the request.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(...)
	 * 	<jk>public</jk> Object myMethod(RestRequest req) {
	 *
	 * 		<jc>// Get access to attributes.</jc>
	 * 		RequestAttributes a = req.getAttributes();
	 *
	 *  	<jc>// Get a header value as a POJO.</jc>
	 * 		UUID etag = a.get(<js>"ETag"</js>, UUID.<jk>class</jk>);
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		This object is modifiable.
	 * 	<li>
	 * 		Values are converted from strings using the registered {@link RestContext#REST_partParser part-parser} on the resource class.
	 * 	<li>
	 * 		The {@link RequestAttributes} object can also be passed as a parameter on the method.
	 * 	<li>
	 * 		The {@link Attr @Attr} annotation can be used to access individual attribute values.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='link'>{@doc juneau-rest-server.RestMethod.RequestAttributes}
	 * </ul>
	 *
	 * @return
	 * 	The headers on this request.
	 * 	<br>Never <jk>null</jk>.
	 */
	public RequestAttributes getAttributes() {
		return attributes;
	}

	/**
	 * Same as {@link #getAttribute(String)} but returns a default value if not found.
	 *
	 * @param name The request attribute name.
	 * @param def The default value if the attribute doesn't exist.
	 * @return The request attribute value.
	 */
	public Object getAttribute(String name, Object def) {
		Object o = super.getAttribute(name);
		return (o == null ? def : o);
	}

	/**
	 * Shorthand method for calling {@link #setAttribute(String, Object)} fluently.
	 *
	 * @param name The request attribute name.
	 * @param value The request attribute value.
	 * @return This object (for method chaining).
	 */
	public RestRequest attr(String name, Object value) {
		setAttribute(name, value);
		return this;
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Query parameters
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Query parameters.
	 *
	 * <p>
	 * Returns a {@link RequestQuery} object that encapsulates access to URL GET parameters.
	 *
	 * <p>
	 * Similar to {@link #getParameterMap()} but only looks for query parameters in the URL and not form posts.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(...)
	 * 	<jk>public void</jk> doGet(RestRequest req) {
	 *
	 * 		<jc>// Get access to query parameters on the URL.</jc>
	 * 		RequestQuery q = req.getQuery();
	 *
	 * 		<jc>// Get query parameters converted to various types.</jc>
	 * 		<jk>int</jk> p1 = q.get(<js>"p1"</js>, 0, <jk>int</jk>.<jk>class</jk>);
	 * 		String p2 = q.get(<js>"p2"</js>, String.<jk>class</jk>);
	 * 		UUID p3 = q.get(<js>"p3"</js>, UUID.<jk>class</jk>);
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		This object is modifiable.
	 * 	<li>
	 * 		This method can be used to retrieve query parameters without triggering the underlying servlet API to load and parse the request body.
	 * 	<li>
	 * 		Values are converted from strings using the registered {@link RestContext#REST_partParser part-parser} on the resource class.
	 * 	<li>
	 * 		The {@link RequestQuery} object can also be passed as a parameter on the method.
	 * 	<li>
	 * 		The {@link Query @Query} annotation can be used to access individual query parameter values.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='link'>{@doc juneau-rest-server.RestMethod.RequestQuery}
	 * </ul>
	 *
	 * @return
	 * 	The query parameters as a modifiable map.
	 * 	<br>Never <jk>null</jk>.
	 */
	public RequestQuery getQuery() {
		return queryParams;
	}

	/**
	 * Shortcut for calling <code>getQuery().getString(name)</code>.
	 *
	 * @param name The query parameter name.
	 * @return The query parameter value, or <jk>null</jk> if not found.
	 */
	public String getQuery(String name) {
		return getQuery().getString(name);
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Form data parameters
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Form-data.
	 *
	 * <p>
	 * Returns a {@link RequestFormData} object that encapsulates access to form post parameters.
	 *
	 * <p>
	 * Similar to {@link #getParameterMap()}, but only looks for form data in the HTTP body.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(...)
	 * 	<jk>public void</jk> doPost(RestRequest req) {
	 *
	 * 		<jc>// Get access to parsed form data parameters.</jc>
	 * 		RequestFormData fd = req.getFormData();
	 *
	 * 		<jc>// Get form data parameters converted to various types.</jc>
	 * 		<jk>int</jk> p1 = fd.get(<js>"p1"</js>, 0, <jk>int</jk>.<jk>class</jk>);
	 * 		String p2 = fd.get(<js>"p2"</js>, String.<jk>class</jk>);
	 * 		UUID p3 = fd.get(<js>"p3"</js>, UUID.<jk>class</jk>);
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		This object is modifiable.
	 * 	<li>
	 * 		Values are converted from strings using the registered {@link RestContext#REST_partParser part-parser} on the resource class.
	 * 	<li>
	 * 		The {@link RequestFormData} object can also be passed as a parameter on the method.
	 * 	<li>
	 * 		The {@link FormData @FormDAta} annotation can be used to access individual form data parameter values.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='link'>{@doc juneau-rest-server.RestMethod.RequestFormData}
	 * </ul>
	 *
	 * @return
	 * 	The URL-encoded form data from the request.
	 * 	<br>Never <jk>null</jk>.
	 * @throws InternalServerError If query parameters could not be parsed.
	 * @see org.apache.juneau.http.annotation.FormData
	 */
	public RequestFormData getFormData() throws InternalServerError {
		try {
			if (formData == null) {
				formData = new RequestFormData(this, restJavaMethod == null ? OpenApiParser.DEFAULT : restJavaMethod.partParser);
				if (! body.isLoaded()) {
					formData.putAll(getParameterMap());
				} else {
					Map<String,String[]> m = RestUtils.parseQuery(body.getReader());
					for (Map.Entry<String,String[]> e : m.entrySet()) {
						for (String v : e.getValue())
							formData.put(e.getKey(), v);
					}
				}
			}
			formData.addDefault(restJavaMethod == null ? null : restJavaMethod.defaultFormData);
			return formData;
		} catch (Exception e) {
			throw new InternalServerError(e);
		}
	}

	/**
	 * Shortcut for calling <code>getFormData().getString(name)</code>.
	 *
	 * @param name The form data parameter name.
	 * @return The form data parameter value, or <jk>null<jk> if not found.
	 */
	public String getFormData(String name) {
		return getFormData().getString(name);
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Path parameters
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Request path match.
	 *
	 * <p>
	 * Returns a {@link RequestPath} object that encapsulates access to everything related to the URL path.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(..., path=<js>"/{foo}/{bar}/{baz}/*"</js>)
	 * 	<jk>public void</jk> doGet(RestRequest req) {
	 *
	 * 		<jc>// Get access to path data.</jc>
	 * 		RequestPathMatch pm = req.getPathMatch();
	 *
	 * 		<jc>// Example URL:  /123/qux/true/quux</jc>
	 *
	 * 		<jk>int</jk> foo = pm.getInt(<js>"foo"</js>);  <jc>// =123</jc>
	 * 		String bar = pm.getString(<js>"bar"</js>);  <jc>// =qux</jc>
	 * 		<jk>boolean</jk> baz = pm.getBoolean(<js>"baz"</js>);  <jc>// =true</jc>
	 * 		String remainder = pm.getRemainder();  <jc>// =quux</jc>
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		This object is modifiable.
	 * 	<li>
	 * 		Values are converted from strings using the registered {@link RestContext#REST_partParser part-parser} on the resource class.
	 * 	<li>
	 * 		The {@link RequestPath} object can also be passed as a parameter on the method.
	 * 	<li>
	 * 		The {@link Path @Path} annotation can be used to access individual values.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='link'>{@doc juneau-rest-server.RestMethod.RequestPathMatch}
	 * </ul>
	 *
	 * @return
	 * 	The path data from the URL.
	 * 	<br>Never <jk>null</jk>.
	 */
	public RequestPath getPathMatch() {
		return pathParams;
	}

	/**
	 * Shortcut for calling <code>getPathMatch().get(name)</code>.
	 *
	 * @param name The path variable name.
	 * @return The path variable value, or <jk>null</jk> if not found.
	 */
	public String getPath(String name) {
		return getPathMatch().get(name);
	}

	/**
	 * Shortcut for calling <code>getPathMatch().getRemainder()</code>.
	 *
	 * @return The path remainder value, or <jk>null</jk> if not found.
	 */
	public String getPathRemainder() {
		return getPathMatch().getRemainder();
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Body methods
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Request body.
	 *
	 * <p>
	 * Returns a {@link RequestBody} object that encapsulates access to the HTTP request body.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(...)
	 * 	<jk>public void</jk> doPost2(RestRequest req) {
	 *
	 * 		<jc>// Convert body to a linked list of Person objects.</jc>
	 * 		List&lt;Person&gt; l = req.getBody().asType(LinkedList.<jk>class</jk>, Person.<jk>class</jk>);
	 * 		..
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The {@link RequestBody} object can also be passed as a parameter on the method.
	 * 	<li>
	 * 		The {@link Body @Body} annotation can be used to access the body as well.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='link'>{@doc juneau-rest-server.RestMethod.RequestBody}
	 * </ul>
	 *
	 * @return
	 * 	The body of this HTTP request.
	 * 	<br>Never <jk>null</jk>.
	 */
	public RequestBody getBody() {
		return body;
	}

	/**
	 * Returns the HTTP body content as a {@link Reader}.
	 *
	 * <p>
	 * If {@code allowHeaderParams} init parameter is true, then first looks for {@code &body=xxx} in the URL query
	 * string.
	 *
	 * <p>
	 * Automatically handles GZipped input streams.
	 */
	@Override /* ServletRequest */
	public BufferedReader getReader() throws IOException {
		return getBody().getReader();
	}

	/**
	 * Returns the HTTP body content as an {@link InputStream}.
	 *
	 * <p>
	 * Automatically handles GZipped input streams.
	 *
	 * @return The negotiated input stream.
	 * @throws IOException If any error occurred while trying to get the input stream or wrap it in the GZIP wrapper.
	 */
	@Override /* ServletRequest */
	public ServletInputStream getInputStream() throws IOException {
		return getBody().getInputStream();
	}

	ServletInputStream getRawInputStream() throws IOException {
		return super.getInputStream();
	}


	//-----------------------------------------------------------------------------------------------------------------
	// URI-related methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HttpServletRequest */
	public String getContextPath() {
		String cp = context.getUriContext();
		return cp == null ? super.getContextPath() : cp;
	}

	/**
	 * Returns the URI authority portion of the request.
	 *
	 * @return The URI authority portion of the request.
	 */
	public String getAuthorityPath() {
		if (authorityPath == null)
			authorityPath = context.getUriAuthority();
		if (authorityPath == null) {
			String scheme = getScheme();
			int port = getServerPort();
			StringBuilder sb = new StringBuilder(getScheme()).append("://").append(getServerName());
			if (! (port == 80 && "http".equals(scheme) || port == 443 && "https".equals(scheme)))
				sb.append(':').append(port);
			authorityPath = sb.toString();
		}
		return authorityPath;
	}

	@Override /* HttpServletRequest */
	public String getServletPath() {
		String cp = context.getUriContext();
		String sp = super.getServletPath();
		return cp == null || ! sp.startsWith(cp) ? sp : sp.substring(cp.length());
	}

	/**
	 * Returns the URI context of the request.
	 *
	 * <p>
	 * The URI context contains all the information about the URI of the request, such as the servlet URI, context
	 * path, etc...
	 *
	 * @return The URI context of the request.
	 */
	public UriContext getUriContext() {
		if (uriContext == null)
			uriContext = new UriContext(getAuthorityPath(), getContextPath(), getServletPath(), StringUtils.urlEncodePath(super.getPathInfo()));
		return uriContext;
	}

	/**
	 * Returns a URI resolver that can be used to convert URIs to absolute or root-relative form.
	 *
	 * @param resolution The URI resolution rule.
	 * @param relativity The relative URI relativity rule.
	 * @return The URI resolver for this request.
	 */
	public UriResolver getUriResolver(UriResolution resolution, UriRelativity relativity) {
		return new UriResolver(resolution, relativity, getUriContext());
	}

	/**
	 * Shortcut for calling {@link #getUriResolver()} using {@link UriResolution#ROOT_RELATIVE} and
	 * {@link UriRelativity#RESOURCE}
	 *
	 * @return The URI resolver for this request.
	 */
	public UriResolver getUriResolver() {
		return new UriResolver(context.getUriResolution(), context.getUriRelativity(), getUriContext());
	}

	/**
	 * Returns the URI for this request.
	 *
	 * <p>
	 * Similar to {@link #getRequestURI()} but returns the value as a {@link URI}.
	 * It also gives you the capability to override the query parameters (e.g. add new query parameters to the existing
	 * URI).
	 *
	 * @param includeQuery If <jk>true</jk> include the query parameters on the request.
	 * @param addQueryParams Augment the request URI with the specified query parameters.
	 * @return A new URI.
	 */
	public URI getUri(boolean includeQuery, Map<String,?> addQueryParams) {
		String uri = getRequestURI();
		if (includeQuery || addQueryParams != null) {
			StringBuilder sb = new StringBuilder(uri);
			RequestQuery rq = this.queryParams.copy();
			if (addQueryParams != null)
				for (Map.Entry<String,?> e : addQueryParams.entrySet())
					rq.put(e.getKey(), e.getValue());
			if (! rq.isEmpty())
				sb.append('?').append(rq.toQueryString());
			uri = sb.toString();
		}
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			// Shouldn't happen.
			throw new RuntimeException(e);
		}
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Labels
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Resource information provider.
	 *
	 * <p>
	 * Returns a {@link RestInfoProvider} object that encapsulates all the textual meta-data on this resource such as
	 * descriptions, titles, and Swagger documentation.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(...)
	 * 	<jk>public void</jk> doGet(RestRequest req) {
	 *
	 * 		<jc>// Get information provider.</jc>
	 * 		RestInfoProvider p = req.getInfoProvider();
	 *
	 * 		<jc>// Get localized strings.</jc>
	 * 		String resourceTitle = p.getTitle(req);
	 * 		String methodDescription = p.getMethodDescription(req.getMethod(), req);
	 * 		Contact contact = p.getContact(req);
	 * 		..
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The {@link RestInfoProvider} object can also be passed as a parameter on the method.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link org.apache.juneau.rest.RestContext#REST_infoProvider}
	 * 	<li class='jic'>{@link org.apache.juneau.rest.RestInfoProvider}
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestRequest#getSiteName()}
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestRequest#getResourceTitle()}
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestRequest#getResourceDescription()}
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestRequest#getMethodSummary()}
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestRequest#getMethodDescription()}
	 * 	<li class='link'>{@doc juneau-rest-server.Swagger}
	 * </ul>
	 *
	 * @return
	 * 	The info provider on the resource.
	 * 	<br>Never <jk>null</jk>.
	 */
	public RestInfoProvider getInfoProvider() {
		return context.getInfoProvider();
	}

	/**
	 * Returns the localized swagger associated with the resource.
	 *
	 * <p>
	 * A shortcut for calling <code>getInfoProvider().getSwagger(request);</code>
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(...)
	 * 	<jk>public</jk> List&lt;Tag&gt; getSwaggerTags(RestRequest req) {
	 * 		<jk>return</jk> req.getSwagger().getTags();
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The {@link Swagger} object can also be passed as a parameter on the method.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link org.apache.juneau.rest.RestContext#REST_infoProvider}
	 * 	<li class='jic'>{@link org.apache.juneau.rest.RestInfoProvider}
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestRequest#getInfoProvider()}
	 * 	<li class='link'>{@doc juneau-rest-server.Swagger}
	 * </ul>
	 *
	 * @return
	 * 	The swagger associated with the resource.
	 * 	<br>Never <jk>null</jk>.
	 * @throws RestException
	 * @throws InternalServerError
	 */
	public Swagger getSwagger() {
		try {
			if (swagger == null)
				swagger = context.getInfoProvider().getSwagger(this);
			return swagger;
		} catch (RestException e) {
			throw e;
		} catch (Exception e) {
			throw new InternalServerError(e);
		}
	}

	/**
	 * Returns the localized site name.
	 *
	 * <p>
	 * The site name is intended to be a title that can be applied to the entire site.
	 *
	 * <p>
	 * One possible use is if you want to add the same title to the top of all pages by defining a header on a
	 * common parent class like so:
	 * <p class='bcode w800'>
	 * 	htmldoc=<ja>@HtmlDoc</ja>(
	 * 		header={
	 * 			<js>"&lt;h1&gt;$R{siteName}&lt;/h1&gt;"</js>,
	 * 			<js>"&lt;h2&gt;$R{resourceTitle}&lt;/h2&gt;"</js>
	 * 		}
	 * 	)
	 * </p>
	 *
	 * <p>
	 * Equivalent to calling {@link RestInfoProvider#getSiteName(RestRequest)} with this object.
	 *
	 * @return The localized site name.
	 * @throws RestException
	 * @throws InternalServerError
	 */
	public String getSiteName() {
		try {
			return context.getInfoProvider().getSiteName(this);
		} catch (RestException e) {
			throw e;
		} catch (Exception e) {
			throw new InternalServerError(e);
		}
	}

	/**
	 * Returns the localized resource title.
	 *
	 * <p>
	 * Equivalent to calling {@link RestInfoProvider#getTitle(RestRequest)} with this object.
	 *
	 * @return The localized resource title.
	 * @throws RestException
	 * @throws InternalServerError
	 */
	public String getResourceTitle() {
		try {
			return context.getInfoProvider().getTitle(this);
		} catch (RestException e) {
			throw e;
		} catch (Exception e) {
			throw new InternalServerError(e);
		}
	}

	/**
	 * Returns the localized resource description.
	 *
	 * <p>
	 * Equivalent to calling {@link RestInfoProvider#getDescription(RestRequest)} with this object.
	 *
	 * @return The localized resource description.
	 * @throws RestException
	 * @throws InternalServerError
	 */
	public String getResourceDescription() {
		try {
			return context.getInfoProvider().getDescription(this);
		} catch (RestException e) {
			throw e;
		} catch (Exception e) {
			throw new InternalServerError(e);
		}
	}

	/**
	 * Returns the localized method summary.
	 *
	 * <p>
	 * Equivalent to calling {@link RestInfoProvider#getMethodSummary(Method, RestRequest)} with this object.
	 *
	 * @return The localized method description.
	 * @throws RestException
	 * @throws InternalServerError
	 */
	public String getMethodSummary() {
		try {
			return context.getInfoProvider().getMethodSummary(javaMethod, this);
		} catch (RestException e) {
			throw e;
		} catch (Exception e) {
			throw new InternalServerError(e);
		}
	}

	/**
	 * Returns the localized method description.
	 *
	 * <p>
	 * Equivalent to calling {@link RestInfoProvider#getMethodDescription(Method, RestRequest)} with this object.
	 *
	 * @return The localized method description.
	 * @throws RestException
	 * @throws InternalServerError
	 */
	public String getMethodDescription() throws RestException, InternalServerError {
		try {
			return context.getInfoProvider().getMethodDescription(javaMethod, this);
		} catch (RestException e) {
			throw e;
		} catch (Exception e) {
			throw new InternalServerError(e);
		}
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Other methods
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Returns the serializers associated with this request.
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='link'>{@doc juneau-rest-server.Serializers}
	 * </ul>
	 *
	 * @return The serializers associated with this request.
	 */
	public SerializerGroup getSerializers() {
		return restJavaMethod == null ? SerializerGroup.EMPTY : restJavaMethod.serializers;
	}

	/**
	 * Returns the parsers associated with this request.
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='link'>{@doc juneau-rest-server.Parsers}
	 * </ul>
	 *
	 * @return The parsers associated with this request.
	 */
	public ParserGroup getParsers() {
		return restJavaMethod == null ? ParserGroup.EMPTY : restJavaMethod.parsers;
	}

	/**
	 * Returns the part serializer associated with this request.
	 *
	 * @return The part serializer associated with this request.
	 */
	public HttpPartParser getPartParser() {
		return restJavaMethod == null ? OpenApiParser.DEFAULT : restJavaMethod.partParser;
	}

	/**
	 * Returns the part serializer associated with this request.
	 *
	 * @return The part serializer associated with this request.
	 */
	public HttpPartSerializer getPartSerializer() {
		return restJavaMethod == null ? OpenApiSerializer.DEFAULT : restJavaMethod.partSerializer;
	}

	/**
	 * Returns the method of this request.
	 *
	 * <p>
	 * If <code>allowHeaderParams</code> init parameter is <jk>true</jk>, then first looks for
	 * <code>&amp;method=xxx</code> in the URL query string.
	 */
	@Override /* ServletRequest */
	public String getMethod() {
		return method;
	}

	/**
	 * Returns the HTTP 1.1 method name of the request as an enum.
	 *
	 * <p>
	 * Note that non-RFC2616 method names resolve as {@link HttpMethod#OTHER}.
	 *
	 * @return The HTTP method.
	 */
	public HttpMethod getHttpMethod() {
		return HttpMethod.forString(method);
	}

	@Override /* ServletRequest */
	public int getContentLength() {
		return getBody().getContentLength();
	}

	int getRawContentLength() {
		return super.getContentLength();
	}

	/**
	 * Returns <jk>true</jk> if <code>&amp;plainText=true</code> was specified as a URL parameter.
	 *
	 * <p>
	 * This indicates that the <code>Content-Type</code> of the output should always be set to <js>"text/plain"</js>
	 * to make it easy to render in a browser.
	 *
	 * <p>
	 * This feature is useful for debugging.
	 *
	 * @return <jk>true</jk> if {@code &amp;plainText=true} was specified as a URL parameter
	 */
	public boolean isPlainText() {
		return "true".equals(getQuery().getString("plainText", "false"));
	}

	/**
	 * Returns the resource bundle for the request locale.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(...)
	 * 	<jk>public</jk> String sayHello(RestRequest req, <ja>@Query</ja>(<js>"user"</js>) String user) {
	 *
	 * 		<jc>// Get message bundle.</jc>
	 * 		MessageBundle mb = req.getMessageBundle();
	 *
	 * 		<jc>// Return a localized message.</jc>
	 * 		<jk>return</jk> mb.getString(<js>"hello.message"</js>, user);
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The {@link MessageBundle} object can also be passed as a parameter on the method.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link org.apache.juneau.rest.RestContext#REST_messages}
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestRequest#getMessage(String,Object...)}
	 * 	<li class='link'>{@doc juneau-rest-server.Messages}
	 * </ul>
	 *
	 * @return
	 * 	The resource bundle.
	 * 	<br>Never <jk>null</jk>.
	 */
	public MessageBundle getMessageBundle() {
		return context.getMessages().getBundle(getLocale());
	}

	/**
	 * Shortcut method for calling {@link MessageBundle#getString(Locale, String, Object...)} based on the request locale.
	 *
	 * @param key The message key.
	 * @param args Optional {@link MessageFormat}-style arguments.
	 * @return The localized message.
	 */
	public String getMessage(String key, Object...args) {
		return context.getMessages().getString(getLocale(), key, args);
	}

	/**
	 * Returns the resource context handling the request.
	 *
	 * <p>
	 * Can be used to access servlet-init parameters or annotations during requests, such as in calls to
	 * {@link RestGuard#guard(RestRequest, RestResponse)}..
	 *
	 * @return The resource context handling the request.
	 */
	public RestContext getContext() {
		return context;
	}

	/**
	 * Returns the java method handling the request.
	 *
	 * <p>
	 * Can be used to access the method name or method annotations during requests, such as in calls to
	 * {@link RestGuard#guard(RestRequest, RestResponse)}.
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		This returns <jk>null</jk> when evaluating servlet-level guards since the method has not been resolved at that
	 * 		point of execution.
	 * </ul>
	 *
	 * @return The Java method handling the request, or <code>null</code> if the method has not yet been resolved.
	 */
	public Method getJavaMethod() {
		return javaMethod;
	}

	/**
	 * Returns the {@link BeanSession} associated with this request.
	 *
	 * @return The request bean session.
	 */
	public BeanSession getBeanSession() {
		return beanSession;
	}

	/**
	 * Returns <jk>true</jk> if debug mode is enabled.
	 *
	 * Debug mode is enabled by simply adding <js>"?debug=true"</js> to the query string or adding a <code>Debug: true</code> header on the request.
	 *
	 * @return <jk>true</jk> if debug mode is enabled.
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Request-level variable resolver session.
	 *
	 * <p>
	 * Used to resolve SVL variables in text.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(...)
	 * 	<jk>public</jk> String sayHello(RestRequest req) {
	 *
	 * 		<jc>// Get var resolver session.</jc>
	 * 		VarResolverSession session = getVarResolverSession();
	 *
	 * 		<jc>// Use it to construct a customized message from a query parameter.</jc>
	 * 		<jk>return</jk> session.resolve(<js>"Hello $RQ{user}!"</js>);
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The {@link VarResolverSession} object can also be passed as a parameter on the method.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestContext#getVarResolver()}
	 * 	<li class='link'>{@doc juneau-rest-server.SvlVariables}
	 * </ul>
	 *
	 * @return The variable resolver for this request.
	 */
	public VarResolverSession getVarResolverSession() {
		if (varSession == null)
			varSession = context
				.getVarResolver()
				.createSession(context.getCallHandler().getSessionObjects(this, context.getResponse()))
				.sessionObject("req", this)
				.sessionObject("res", res);
		return varSession;
	}

	/**
	 * Returns an instance of a {@link ReaderResource} that represents the contents of a resource text file from the
	 * classpath.
	 *
	 * <p>
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// A rest method that (unsafely!) returns the contents of a localized file </jc>
	 *	<jc>// from the classpath and resolves any SVL variables embedded in it.</jc>
	 * 	<ja>@RestMethod</ja>(...)
	 * 	<jk>public</jk> String myMethod(RestRequest req, <ja>@Query</ja>(<js>"file"</js>) String file) {
	 * 		<jk>return</jk> req.getClasspathResourceAsString(file, <jk>true</jk>);
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link org.apache.juneau.rest.RestContext#REST_classpathResourceFinder}
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestRequest#getClasspathReaderResource(String, boolean)}
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestRequest#getClasspathReaderResource(String)}
	 * </ul>
	 *
	 * @param name The name of the resource (i.e. the value normally passed to {@link Class#getResourceAsStream(String)}.
	 * @param resolveVars
	 * 	If <jk>true</jk>, any SVL variables will be
	 * 	resolved by the variable resolver returned by {@link #getVarResolverSession()}.
	 * 	<br>See {@link RestContext#getVarResolver()} for the list of supported variables.
	 * @param mediaType The value to set as the <js>"Content-Type"</js> header for this object.
	 * @param cached If <jk>true</jk>, the resource will be read into a byte array for fast serialization.
	 * @return A new reader resource, or <jk>null</jk> if resource could not be found.
	 * @throws IOException
	 */
	public ReaderResource getClasspathReaderResource(String name, boolean resolveVars, MediaType mediaType, boolean cached) throws IOException {
		String s = context.getClasspathResourceAsString(name, getLocale());
		if (s == null)
			return null;
		ResolvingReaderResource.Builder b = ResolvingReaderResource.create().mediaType(mediaType).contents(s);
		if (resolveVars)
			b.varResolver(getVarResolverSession());
		if (cached)
			b.cached();
		return b.build();
	}

	/**
	 * Same as {@link #getClasspathReaderResource(String, boolean, MediaType, boolean)} except uses the resource mime-type map
	 * constructed using {@link RestContextBuilder#mimeTypes(String...)} to determine the media type.
	 *
	 * @param name The name of the resource (i.e. the value normally passed to {@link Class#getResourceAsStream(String)}.
	 * @param resolveVars
	 * 	If <jk>true</jk>, any SVL variables will be
	 * 	resolved by the variable resolver returned by {@link #getVarResolverSession()}.
	 * 	<br>See {@link RestContext#getVarResolver()} for the list of supported variables.
	 * @return A new reader resource, or <jk>null</jk> if resource could not be found.
	 * @throws IOException
	 */
	public ReaderResource getClasspathReaderResource(String name, boolean resolveVars) throws IOException {
		return getClasspathReaderResource(name, resolveVars, MediaType.forString(context.getMediaTypeForName(name)), false);
	}

	/**
	 * Same as {@link #getClasspathReaderResource(String, boolean)} with <code>resolveVars == <jk>false</jk></code>
	 *
	 * @param name The name of the resource (i.e. the value normally passed to {@link Class#getResourceAsStream(String)}.
	 * @return A new reader resource, or <jk>null</jk> if resource could not be found.
	 * @throws IOException
	 */
	public ReaderResource getClasspathReaderResource(String name) throws IOException {
		return getClasspathReaderResource(name, false, MediaType.forString(context.getMediaTypeForName(name)), false);
	}

	/**
	 * Returns an instance of a {@link StreamResource} that represents the contents of a resource binary file from the
	 * classpath.
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link org.apache.juneau.rest.RestContext#REST_classpathResourceFinder}
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestRequest#getClasspathStreamResource(String)}
	 * </ul>
	 *
	 * @param name The name of the resource (i.e. the value normally passed to {@link Class#getResourceAsStream(String)}.
	 * @param mediaType The value to set as the <js>"Content-Type"</js> header for this object.
	 * @param cached If <jk>true</jk>, the resource will be read into a byte array for fast serialization.
	 * @return A new stream resource, or <jk>null</jk> if resource could not be found.
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public StreamResource getClasspathStreamResource(String name, MediaType mediaType, boolean cached) throws IOException {
		InputStream is = context.getClasspathResource(name, getLocale());
		if (is == null)
			return null;
		StreamResource.Builder b = StreamResource.create().mediaType(mediaType).contents(is);
		if (cached)
			b.cached();
		return b.build();
	}

	/**
	 * Same as {@link #getClasspathStreamResource(String, MediaType, boolean)} except uses the resource mime-type map
	 * constructed using {@link RestContextBuilder#mimeTypes(String...)} to determine the media type.
	 *
	 * @param name The name of the resource (i.e. the value normally passed to {@link Class#getResourceAsStream(String)}.
	 * @return A new stream resource, or <jk>null</jk> if resource could not be found.
	 * @throws IOException
	 */
	public StreamResource getClasspathStreamResource(String name) throws IOException {
		return getClasspathStreamResource(name, MediaType.forString(context.getMediaTypeForName(name)), false);
	}

	/**
	 * Config file associated with the resource.
	 *
	 * <p>
	 * Returns a config file with session-level variable resolution.
	 *
	 * The config file is identified via one of the following:
	 * <ul>
	 * 	<li class='ja'>{@link RestResource#config()}
	 * 	<li class='jm'>{@link RestContextBuilder#config(Config)}
	 * </ul>
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(...)
	 * 	<jk>public void</jk> doGet(RestRequest req) {
	 *
	 * 		<jc>// Get config file.</jc>
	 * 		Config cf = req.getConfig();
	 *
	 * 		<jc>// Get simple values from config file.</jc>
	 * 		<jk>int</jk> timeout = cf.getInt(<js>"MyResource/timeout"</js>, 10000);
	 *
	 * 		<jc>// Get complex values from config file.</jc>
	 * 		MyBean b = cf.getObject(<js>"MyResource/myBean"</js>, MyBean.<jk>class</jk>);
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The {@link Config} object can also be passed as a parameter on the method.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='link'>{@doc juneau-rest-server.ConfigurationFiles}
	 * </ul>
	 *
	 * @return
	 * 	The config file associated with the resource, or <jk>null</jk> if resource does not have a config file
	 * 	associated with it.
	 */
	public Config getConfig() {
		if (cf == null)
			cf = context.getConfig().resolving(getVarResolverSession());
		return cf;
	}

	/**
	 * Returns the widgets used for resolving <js>"$W{...}"</js> string variables.
	 *
	 * @return
	 * 	The widgets used for resolving <js>"$W{...}"</js> string variables.
	 * 	Never <jk>null</jk>.
	 *
	 * @deprecated
	 */
	@Deprecated
	public Map<String,Widget> getWidgets() {
		return restJavaMethod == null ? Collections.<String,Widget>emptyMap() : restJavaMethod.widgets;
	}

	/**
	 * Creates a proxy interface to retrieve HTTP parts of this request as a proxy bean.
	 *
	 * <h5 class='section'>Examples:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(path=<js>"/mypath/{p1}/{p2}/*"</js>)
	 * 	<jk>public void</jk> myMethod(@Request MyRequest rb) {...}
	 *
	 * 	<jk>public interface</jk> MyRequest {
	 *
	 * 		<ja>@Path</ja> <jc>// Path variable name inferred from getter.</jc>
	 * 		String getP1();
	 *
	 * 		<ja>@Path</ja>(<js>"p2"</js>)
	 * 		String getX();
	 *
	 * 		<ja>@Path</ja>(<js>"/*"</js>)
	 * 		String getRemainder();
	 *
	 * 		<ja>@Query</ja>
	 * 		String getQ1();
	 *
	 *		<jc>// Schema-based query parameter:  Pipe-delimited lists of comma-delimited lists of integers.</jc>
	 * 		<ja>@Query</ja>(
	 * 			collectionFormat=<js>"pipes"</js>
	 * 			items=<ja>@Items</ja>(
	 * 				items=<ja>@SubItems</ja>(
	 * 					collectionFormat=<js>"csv"</js>
	 * 					type=<js>"integer"</js>
	 * 				)
	 * 			)
	 * 		)
	 * 		<jk>int</jk>[][] getQ3();
	 *
	 * 		<ja>@Header</ja>(<js>"*"</js>)
	 * 		Map&lt;String,Object&gt; getHeaders();
	 * </p>
	 *
	 * @param c The request bean interface to instantiate.
	 * @return A new request bean proxy for this REST request.
	 */
	public <T> T getRequest(Class<T> c) {
		return getRequest(RequestBeanMeta.create(c, getContext().getPropertyStore()));
	}

	/**
	 * Same as {@link #getRequest(Class)} but used on pre-instantiated {@link RequestBeanMeta} objects.
	 *
	 * @param rbm The metadata about the request bean interface to create.
	 * @return A new request bean proxy for this REST request.
	 */
	public <T> T getRequest(final RequestBeanMeta rbm) {
		try {
			Class<T> c = (Class<T>)rbm.getClassMeta().getInnerClass();
			final BeanMeta<T> bm = getBeanSession().getBeanMeta(c);
			return (T)Proxy.newProxyInstance(
				c.getClassLoader(),
				new Class[] { c },
				new InvocationHandler() {
					@Override /* InvocationHandler */
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						RequestBeanPropertyMeta pm = rbm.getProperty(method.getName());
						if (pm != null) {
							HttpPartParser pp = pm.getParser(getPartParser());
							HttpPartSchema schema = pm.getSchema();
							String name = pm.getPartName();
							ClassMeta<?> type = getContext().getClassMeta(method.getGenericReturnType());
							HttpPartType pt = pm.getPartType();
							if (pt == HttpPartType.BODY)
								return getBody().schema(schema).asType(type);
							if (pt == QUERY)
								return getQuery().get(pp, schema, name, type);
							if (pt == FORMDATA)
								return getFormData().get(pp, schema, name, type);
							if (pt == HEADER)
								return getHeaders().get(pp, schema, name, type);
							if (pt == PATH)
								return getPathMatch().get(pp, schema, name, type);
						}
						return null;
					}

			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override /* Object */
	public String toString() {
		StringBuilder sb = new StringBuilder("\n").append(getDescription()).append("\n");
		sb.append("---Headers---\n");
		for (Enumeration<String> e = getHeaderNames(); e.hasMoreElements();) {
			String h = e.nextElement();
			sb.append("\t").append(h).append(": ").append(getHeader(h)).append("\n");
		}
		sb.append("---Default Servlet Headers---\n");
		for (Map.Entry<String,Object> e : context.getDefaultRequestHeaders().entrySet()) {
			sb.append("\t").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
		}
		if (javaMethod == null) {
			sb.append("***init() not called yet!***\n");
		} else if (method.equals("PUT") || method.equals("POST")) {
			try {
				sb.append("---Body UTF-8---\n");
				sb.append(body.asString()).append("\n");
				sb.append("---Body Hex---\n");
				sb.append(body.asSpacedHex()).append("\n");
			} catch (Exception e1) {
				sb.append(e1.getLocalizedMessage());
				context.getLogger().log(WARNING, e1, "Error occurred while trying to read debug input.");
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the session arguments to pass to serializers.
	 *
	 * @return The session arguments to pass to serializers.
	 */
	public SerializerSessionArgs getSerializerSessionArgs() {
		if (serializerSessionArgs == null)
			serializerSessionArgs = SerializerSessionArgs
				.create()
				.properties(getProperties())
				.javaMethod(getJavaMethod())
				.locale(getLocale())
				.timeZone(getHeaders().getTimeZone())
				.debug(isDebug() ? true : null)
				.uriContext(getUriContext())
				.resolver(getVarResolverSession())
				.useWhitespace(isPlainText() ? true : null);
		return serializerSessionArgs;
	}

	/**
	 * Returns the session arguments to pass to parsers.
	 *
	 * @return The session arguments to pass to parsers.
	 */
	public ParserSessionArgs getParserSessionArgs() {
		if (parserSessionArgs == null)
			parserSessionArgs =
				ParserSessionArgs
					.create()
					.properties(getProperties())
					.javaMethod(getJavaMethod())
					.locale(getLocale())
					.timeZone(getHeaders().getTimeZone())
					.debug(isDebug() ? true : null);
		return parserSessionArgs;
	}

	/**
	 * Logger.
	 *
	 * <p>
	 * Shortcut for calling <code>getContext().getLogger()</code>.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(...)
	 * 	<jk>public void</jk> doGet(RestRequest req) {
	 *
	 * 		req.getLogger().logObjects(<jsf>FINE</jsf>, <js>"Request query parameters = {0}"</js>, req.getQuery());
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The {@link RestLogger} object can also be passed as a parameter on the method.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link org.apache.juneau.rest.RestContext#REST_logger}
	 * 	<li class='jac'>{@link org.apache.juneau.rest.RestLogger}
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestServlet#log(Level, String, Object...)}
	 * 	<li class='jm'>{@link org.apache.juneau.rest.RestServlet#logObjects(Level, String, Object...)}
	 * 	<li class='link'>{@doc juneau-rest-server.LoggingAndErrorHandling}
	 * </ul>
	 *
	 * @return
	 * 	The logger associated with the resource context.
	 * 	<br>Never <jk>null</jk>.
	 */
	public RestLogger getLogger() {
		return context.getLogger();
	}

	void close() {
		if (cf != null) {
			try {
				cf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns metadata about the specified response object if it's annotated with {@link Response @Response}.
	 *
 	 * @param o The response POJO.
	 * @return Metadata about the specified response object, or <jk>null</jk> if it's not annotated with {@link Response @Response}.
	 */
	public ResponseBeanMeta getResponseBeanMeta(Object o) {
		return restJavaMethod == null ? null : restJavaMethod.getResponseBeanMeta(o);
	}

	/**
	 * Returns metadata about the specified response object if it's annotated with {@link ResponseHeader @ResponseHeader}.
	 *
 	 * @param o The response POJO.
	 * @return Metadata about the specified response object, or <jk>null</jk> if it's not annotated with {@link ResponseHeader @ResponseHeader}.
	 */
	public ResponsePartMeta getResponseHeaderMeta(Object o) {
		return restJavaMethod == null ? null : restJavaMethod.getResponseHeaderMeta(o);
	}

	/**
	 * Returns metadata about the specified response object if it's annotated with {@link ResponseBody @ResponseBody}.
	 *
 	 * @param o The response POJO.
	 * @return Metadata about the specified response object, or <jk>null</jk> if it's not annotated with {@link ResponseBody @ResponseBody}.
	 */
	public ResponsePartMeta getResponseBodyMeta(Object o) {
		return restJavaMethod == null ? null : restJavaMethod.getResponseBodyMeta(o);
	}

	/**
	 * Returns the schema generator with settings assigned on this method and class.
	 *
	 * @return The schema generator.
	 */
	public JsonSchemaGenerator getJsonSchemaGenerator() {
		return restJavaMethod == null ? context.getJsonSchemaGenerator() : restJavaMethod.getJsonSchemaGenerator();
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Utility methods
	//-----------------------------------------------------------------------------------------------------------------

	/*
	 * Converts an Accept-Language value entry to a Locale.
	 */
	private static Locale toLocale(String lang) {
		String country = "";
		int i = lang.indexOf('-');
		if (i > -1) {
			country = lang.substring(i+1).trim();
			lang = lang.substring(0,i).trim();
		}
		return new Locale(lang, country);
	}

	void setJavaMethod(Method method) {
		this.javaMethod = method;
	}
}