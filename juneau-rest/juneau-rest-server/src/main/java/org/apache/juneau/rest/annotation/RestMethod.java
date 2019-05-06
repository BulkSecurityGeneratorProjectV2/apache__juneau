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
package org.apache.juneau.rest.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.rest.*;
import org.apache.juneau.remote.*;

/**
 * Identifies a REST Java method on a {@link RestServlet} implementation class.
 *
 * <h5 class='section'>See Also:</h5>
 * <ul>
 * 	<li class='link'>{@doc juneau-rest-server.RestMethod}
 * </ul>
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
@Inherited
public @interface RestMethod {

	/**
	 * Sets the bean filters for the serializers and parsers defined on this method.
	 *
	 * <p>
	 * If no value is specified, the bean filters are inherited from the class.
	 * <br>Otherwise, this value overrides the bean filters defined on the class.
	 *
	 * <p>
	 * Use {@link Inherit} to inherit bean filters defined on the class.
	 *
	 * <p>
	 * Use {@link None} to suppress inheriting bean filters defined on the class.
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link BeanContext#BEAN_beanFilters}
	 * </ul>
	 *
	 * @deprecated Use {@link BeanConfig#beanFilters()}
	 */
	@Deprecated
	Class<?>[] beanFilters() default {};

	/**
	 * Shortcut for specifying the {@link BeanContext#BEAN_includeProperties} property on all serializers.
	 *
	 * <p>
	 * The typical use case is when you're rendering summary and details views of the same bean in a resource and
	 * you want to expose or hide specific properties depending on the level of detail you want.
	 *
	 * <p>
	 * In the example below, our 'summary' view is a list of beans where we only want to show the ID property,
	 * and our detail view is a single bean where we want to expose different fields:
	 * <p class='bcode w800'>
	 * 	<jc>// Our bean</jc>
	 * 	<jk>public class</jk> MyBean {
	 *
	 * 		<jc>// Summary properties</jc>
	 * 		<ja>@Html</ja>(link=<js>"servlet:/mybeans/{id}"</js>)
	 * 		<jk>public</jk> String <jf>id</jf>;
	 *
	 * 		<jc>// Detail properties</jc>
	 * 		<jk>public</jk> String <jf>a</jf>, <jf>b</jf>;
	 * 	}
	 *
	 * 	<jc>// Only render "id" property.</jc>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/mybeans"</js>, bpi=<js>"MyBean: id"</js>)
	 * 	<jk>public</jk> List&lt;MyBean&gt; getBeanSummary() {...}
	 *
	 * 	<jc>// Only render "a" and "b" properties.</jc>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/mybeans/{id}"</js>, bpi=<js>"MyBean: a,b"</js>)
	 * 	<jk>public</jk> MyBean getBeanDetails(<ja>@Path</ja> String id) {...}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The format of each value is: <js>"Key: comma-delimited-tokens"</js>.
	 * 	<li>
	 * 		Keys can be fully-qualified or short class names or <js>"*"</js> to represent all classes.
	 * 	<li>
	 * 		Values are comma-delimited lists of bean property names.
	 * 	<li>
	 * 		Properties apply to specified class and all subclasses.
	 * 	<li>
	 * 		Semicolons can be used as an additional separator for multiple values:
	 * 		<p class='bcode w800'>
	 * 	<jc>// Equivalent</jc>
	 * 	bpi={<js>"Bean1: foo"</js>,<js>"Bean2: bar,baz"</js>}
	 * 	bpi=<js>"Bean1: foo; Bean2: bar,baz"</js>
	 * 		</p>
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link BeanContext#BEAN_includeProperties}
	 * </ul>
	 *
	 * @deprecated Use {@link BeanConfig#bpi()}
	 */
	@Deprecated
	String[] bpi() default {};

	/**
	 * Shortcut for specifying the {@link BeanContext#BEAN_excludeProperties} property on all serializers.
	 *
	 * <p>
	 * Same as {@link #bpi()} except you specify a list of bean property names that you want to exclude from
	 * serialization.
	 *
	 * <p>
	 * In the example below, our 'summary' view is a list of beans where we want to exclude some properties:
	 * <p class='bcode w800'>
	 * 	<jc>// Our bean</jc>
	 * 	<jk>public class</jk> MyBean {
	 *
	 * 		<jc>// Summary properties</jc>
	 * 		<ja>@Html</ja>(link=<js>"servlet:/mybeans/{id}"</js>)
	 * 		<jk>public</jk> String <jf>id</jf>;
	 *
	 * 		<jc>// Detail properties</jc>
	 * 		<jk>public</jk> String <jf>a</jf>, <jf>b</jf>;
	 * 	}
	 *
	 * 	<jc>// Don't show "a" and "b" properties.</jc>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/mybeans"</js>, bpx=<js>"MyBean: a,b"</js>)
	 * 	<jk>public</jk> List&lt;MyBean&gt; getBeanSummary() {...}
	 *
	 * 	<jc>// Render all properties.</jc>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/mybeans/{id}"</js>)
	 * 	<jk>public</jk> MyBean getBeanDetails(<ja>@Path</ja> String id) {...}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The format of each value is: <js>"Key: comma-delimited-tokens"</js>.
	 * 	<li>
	 * 		Keys can be fully-qualified or short class names or <js>"*"</js> to represent all classes.
	 * 	<li>
	 * 		Values are comma-delimited lists of bean property names.
	 * 	<li>
	 * 		Properties apply to specified class and all subclasses.
	 * 	<li>
	 * 		Semicolons can be used as an additional separator for multiple values:
	 * 		<p class='bcode w800'>
	 * 	<jc>// Equivalent</jc>
	 * 	bpx={<js>"Bean1: foo"</js>,<js>"Bean2: bar,baz"</js>}
	 * 	bpx=<js>"Bean1: foo; Bean2: bar,baz"</js>
	 * 		</p>
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link BeanContext#BEAN_excludeProperties}
	 * </ul>
	 *
	 * @deprecated Use {@link BeanConfig#bpx()}
	 */
	@Deprecated
	String[] bpx() default {};

	/**
	 * Specifies whether this method can be called based on the client version.
	 *
	 * <p>
	 * The client version is identified via the HTTP request header identified by
	 * {@link RestResource#clientVersionHeader() @RestResource(clientVersionHeader)} which by default is <js>"X-Client-Version"</js>.
	 *
	 * <p>
	 * This is a specialized kind of {@link RestMatcher} that allows you to invoke different Java methods for the same
	 * method/path based on the client version.
	 *
	 * <p>
	 * The format of the client version range is similar to that of OSGi versions.
	 *
	 * <p>
	 * In the following example, the Java methods are mapped to the same HTTP method and URL <js>"/foobar"</js>.
	 * <p class='bcode w800'>
	 * 	<jc>// Call this method if X-Client-Version is at least 2.0.
	 * 	// Note that this also matches 2.0.1.</jc>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/foobar"</js>, clientVersion=<js>"2.0"</js>)
	 * 	<jk>public</jk> Object method1()  {...}
	 *
	 * 	<jc>// Call this method if X-Client-Version is at least 1.1, but less than 2.0.</jc>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/foobar"</js>, clientVersion=<js>"[1.1,2.0)"</js>)
	 * 	<jk>public</jk> Object method2()  {...}
	 *
	 * 	<jc>// Call this method if X-Client-Version is less than 1.1.</jc>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/foobar"</js>, clientVersion=<js>"[0,1.1)"</js>)
	 * 	<jk>public</jk> Object method3()  {...}
	 * </p>
	 *
	 * <p>
	 * It's common to combine the client version with transforms that will convert new POJOs into older POJOs for
	 * backwards compatibility.
	 * <p class='bcode w800'>
	 * 	<jc>// Call this method if X-Client-Version is at least 2.0.</jc>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/foobar"</js>, clientVersion=<js>"2.0"</js>)
	 * 	<jk>public</jk> NewPojo newMethod()  {...}
	 *
	 * 	<jc>// Call this method if X-Client-Version is at least 1.1, but less than 2.0.</jc>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/foobar"</js>, clientVersion=<js>"[1.1,2.0)"</js>, transforms={NewToOldPojoSwap.<jk>class</jk>})
	 * 	<jk>public</jk> NewPojo oldMethod() {
	 * 		<jk>return</jk> newMethod();
	 * 	}
	 *
	 * <p>
	 * Note that in the previous example, we're returning the exact same POJO, but using a transform to convert it into
	 * an older form.
	 * The old method could also just return back a completely different object.
	 * The range can be any of the following:
	 * <ul>
	 * 	<li><js>"[0,1.0)"</js> = Less than 1.0.  1.0 and 1.0.0 does not match.
	 * 	<li><js>"[0,1.0]"</js> = Less than or equal to 1.0.  Note that 1.0.1 will match.
	 * 	<li><js>"1.0"</js> = At least 1.0.  1.0 and 2.0 will match.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link RestContext#REST_clientVersionHeader}
	 * </ul>
	 */
	String clientVersion() default "";

	/**
	 * Class-level response converters.
	 *
	 * <p>
	 * Associates one or more {@link RestConverter converters} with this method.
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link RestContext#REST_converters}
	 * </ul>
	 */
	Class<? extends RestConverter>[] converters() default {};

	/**
	 * Default <code>Accept</code> header.
	 *
	 * <p>
	 * The default value for the <code>Accept</code> header if not specified on a request.
	 *
	 * <p>
	 * This is a shortcut for using {@link #defaultRequestHeaders()} for just this specific header.
	 */
	String defaultAccept() default "";

	/**
	 * Default character encoding.
	 *
	 * <p>
	 * The default character encoding for the request and response if not specified on the request.
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		Supports {@doc DefaultRestSvlVariables}
	 * 		(e.g. <js>"$S{mySystemProperty}"</js>).
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link RestContext#REST_defaultCharset}
	 * </ul>
	 */
	String defaultCharset() default "";

	/**
	 * Default <code>Content-Type</code> header.
	 *
	 * <p>
	 * The default value for the <code>Content-Type</code> header if not specified on a request.
	 *
	 * <p>
	 * This is a shortcut for using {@link #defaultRequestHeaders()} for just this specific header.
	 */
	String defaultContentType() default "";

	/**
	 * Specifies default values for form-data parameters.
	 *
	 * <p>
	 * Strings are of the format <js>"name=value"</js>.
	 *
	 * <p>
	 * Affects values returned by {@link RestRequest#getFormData(String)} when the parameter is not present on the
	 * request.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(name=<jsf>POST</jsf>, path=<js>"/*"</js>, defaultFormData={<js>"foo=bar"</js>})
	 * 	<jk>public</jk> String doGet(<ja>@FormData</ja>(<js>"foo"</js>) String foo)  {...}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		You can use either <js>':'</js> or <js>'='</js> as the key/value delimiter.
	 * 	<li>
	 * 		Key and value is trimmed of whitespace.
	 * 	<li>
	 * 		Supports {@doc DefaultRestSvlVariables}
	 * 		(e.g. <js>"$S{mySystemProperty}"</js>).
	 * </ul>
	 */
	String[] defaultFormData() default {};

	/**
	 * Specifies default values for query parameters.
	 *
	 * <p>
	 * Strings are of the format <js>"name=value"</js>.
	 *
	 * <p>
	 * Affects values returned by {@link RestRequest#getQuery(String)} when the parameter is not present on the request.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/*"</js>, defaultQuery={<js>"foo=bar"</js>})
	 * 	<jk>public</jk> String doGet(<ja>@Query</ja>(<js>"foo"</js>) String foo)  {...}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		You can use either <js>':'</js> or <js>'='</js> as the key/value delimiter.
	 * 	<li>
	 * 		Key and value is trimmed of whitespace.
	 * 	<li>
	 * 		Supports {@doc DefaultRestSvlVariables}
	 * 		(e.g. <js>"$S{mySystemProperty}"</js>).
	 * </ul>
	 */
	String[] defaultQuery() default {};

	/**
	 * Default request headers.
	 *
	 * <p>
	 * Specifies default values for request headers if they're not passed in through the request.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Assume "text/json" Accept value when Accept not specified</jc>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/*"</js>, defaultRequestHeaders={<js>"Accept: text/json"</js>})
	 * 	<jk>public</jk> String doGet()  {...}
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		Supports {@doc DefaultRestSvlVariables}
	 * 		(e.g. <js>"$S{mySystemProperty}"</js>).
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link RestContext#REST_defaultRequestHeaders}
	 * </ul>
	 */
	String[] defaultRequestHeaders() default {};

	/**
	 * Optional description for the exposed API.
	 *
	 * <p>
	 * This description is used in the following locations:
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The value returned by {@link RestRequest#getMethodDescription()}.
	 * 	<li>
	 * 		The <js>"$R{methodDescription}"</js> variable.
	 * 	<li>
	 * 		The description of the method in the Swagger page.
	 * </ul>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		Corresponds to the swagger field <code>/paths/{path}/{method}/description</code>.
	 * 	<li>
	 * 		Supports {@doc DefaultRestSvlVariables}
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jm'>{@link RestInfoProvider#getDescription(RestRequest)}
	 * </ul>
	 */
	String[] description() default {};

	/**
	 * Compression encoders.
	 *
	 * <p>
	 * Use this annotation when the list of encoders assigned to a method differs from the list of encoders assigned at
	 * the servlet level.
	 *
	 * <p>
	 * These can be used to enable various kinds of compression (e.g. <js>"gzip"</js>) on requests and responses.
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		Use <code>inherit={<js>"ENCODERS"</js>}</code> to inherit encoders from the resource class.
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link RestContext#REST_encoders}
	 * </ul>
	 */
	Class<?>[] encoders() default {};

	/**
	 * Shortcut for setting {@link #properties()} of simple boolean types.
	 *
	 * <p>
	 * Setting a flag is equivalent to setting the same property to <js>"true"</js>.
	 */
	String[] flags() default {};

	/**
	 * Method-level guards.
	 *
	 * <p>
	 * Associates one or more {@link RestGuard RestGuards} with this method.
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link RestContext#REST_guards}
	 * </ul>
	 */
	Class<? extends RestGuard>[] guards() default {};

	/**
	 * Provides HTML-doc-specific metadata on this method.
	 *
	 * <p>
	 * Information provided here overrides information provided in the servlet-level annotation.
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='link'>{@doc juneau-rest-server.HtmlDocAnnotation}
	 * </ul>
	 */
	HtmlDoc htmldoc() default @HtmlDoc;

	/**
	 * Method matchers.
	 *
	 * <p>
	 * Associates one more more {@link RestMatcher RestMatchers} with this method.
	 *
	 * <p>
	 * Matchers are used to allow multiple Java methods to handle requests assigned to the same URL path pattern, but
	 * differing based on some request attribute, such as a specific header value.
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jac'>{@link RestMatcher}
	 * </ul>
	 */
	Class<? extends RestMatcher>[] matchers() default {};

	/**
	 * The maximum allowed input size (in bytes) on HTTP requests.
	 *
	 * <p>
	 * Useful for alleviating DoS attacks by throwing an exception when too much input is received instead of resulting
	 * in out-of-memory errors which could affect system stability.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(
	 * 		maxInput=<js>"100M"</js>
	 * 	)
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		Supports {@doc DefaultRestSvlVariables}
	 * 		(e.g. <js>"$S{mySystemProperty}"</js>).
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link RestContext#REST_maxInput}
	 * </ul>
	 */
	String maxInput() default "";

	/**
	 * REST method name.
	 *
	 * <p>
	 * Typically <js>"GET"</js>, <js>"PUT"</js>, <js>"POST"</js>, <js>"DELETE"</js>, or <js>"OPTIONS"</js>.
	 *
	 * <p>
	 * Method names are case-insensitive (always folded to upper-case).
	 *
	 * <p>
	 * Note that you can use {@link org.apache.juneau.http.HttpMethodName} for constant values.
	 *
	 * <p>
	 * Besides the standard HTTP method names, the following can also be specified:
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		<js>"*"</js>
	 * 		- Denotes any method.
	 * 		<br>Use this if you want to capture any HTTP methods in a single Java method.
	 * 		<br>The {@link Method @Method} annotation and/or {@link RestRequest#getMethod()} method can be used to
	 * 		distinguish the actual HTTP method name.
	 * 	<li>
	 * 		<js>""</js>
	 * 		- Auto-detect.
	 * 		<br>The method name is determined based on the Java method name.
	 * 		<br>For example, if the method is <code>doPost(...)</code>, then the method name is automatically detected
	 * 		as <js>"POST"</js>.
	 * 		<br>Otherwise, defaults to <js>"GET"</js>.
	 * 	<li>
	 * 		<js>"RRPC"</js>
	 * 		- Remote-proxy interface.
	 * 		<br>This denotes a Java method that returns an object (usually an interface, often annotated with the
	 * 		{@link RemoteInterface @RemoteInterface} annotation) to be used as a remote proxy using
	 * 		<code>RestClient.getRemoteInterface(Class&lt;T&gt; interfaceClass, String url)</code>.
	 * 		<br>This allows you to construct client-side interface proxies using REST as a transport medium.
	 * 		<br>Conceptually, this is simply a fancy <code>POST</code> against the url <js>"/{path}/{javaMethodName}"</js>
	 * 		where the arguments are marshalled from the client to the server as an HTTP body containing an array of
	 * 		objects, passed to the method as arguments, and then the resulting object is marshalled back to the client.
	 * 	<li>
	 * 		Anything else
	 * 		- Overloaded non-HTTP-standard names that are passed in through a <code>&amp;method=methodName</code> URL
	 * 		parameter.
	 * </ul>
	 */
	String name() default "";

	/**
	 * REST method name.
	 *
	 * Synonym for {@link #name()}.
	 */
	String method() default "";

	/**
	 * Parsers.
	 *
	 * <p>
	 * If no value is specified, the parsers are inherited from the class.
	 * <br>Otherwise, this value overrides the parsers defined on the class.
	 *
	 * <p>
	 * Use {@link Inherit} to inherit parsers defined on the class.
	 *
	 * <p>
	 * Use {@link None} to suppress inheriting parsers defined on the class.
	 *
	 * <p class='bcode w800'>
	 * 	<jk>public class</jk> MyResource <jk>extends</jk> RestServlet {
	 *
	 * 		<ja>@RestMethod</ja>(
	 * 			name=<jsf>PUT</jsf>,
	 * 			path=<js>"/foo"</js>,
	 * 			parsers=MySpecialParser.<jk>class</jk>
	 * 		)
	 * 		<jk>public</jk> Object doGetWithSpecialAcceptType() {
	 * 			<jc>// Handle request for special Accept type</jc>
	 * 		}
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link RestContext#REST_parsers}
	 * </ul>
	 */
	Class<?>[] parsers() default {};

	/**
	 * Optional path pattern for the specified method.
	 *
	 * <p>
	 * Appending <js>"/*"</js> to the end of the path pattern will make it match any remainder too.
	 * <br>Not appending <js>"/*"</js> to the end of the pattern will cause a 404 (Not found) error to occur if the exact
	 * pattern is not found.
	 *
	 * <p>
	 * The path can contain variables that get resolved to {@link org.apache.juneau.http.annotation.Path @Path} parameters.
	 *
	 * <h5 class='figure'>Examples:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/myurl/{foo}/{bar}/{baz}/*"</js>)
	 * </p>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>, path=<js>"/myurl/{0}/{1}/{2}/*"</js>)
	 * </p>
	 *
	 * <p>
	 * If you do not specify a path name, then the path name is inferred from the Java method name.
	 *
	 * <h5 class='figure'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Path is assumed to be "/foo".</jc>
	 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>)
	 * 	<jk>public void</jk> foo() {...}
	 * </p>
	 *
	 * <p>
	 * If you also do not specify the {@link #name()} and the Java method name starts with <js>"get"</js>, <js>"put"</js>, <js>"post"</js>, or <js>"deleted"</js>,
	 * then the HTTP method name is stripped from the inferred path.
	 *
	 * <h5 class='figure'>Examples:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Method is GET, path is "/foo".</jc>
	 * 	<ja>@RestMethod</ja>
	 * 	<jk>public void</jk> getFoo() {...}
	 * </p>
	 * <p class='bcode w800'>
	 * 	<jc>// Method is DELETE, path is "/bar".</jc>
	 * 	<ja>@RestMethod</ja>
	 * 	<jk>public void</jk> deleteBar() {...}
	 * </p>
	 * <p class='bcode w800'>
	 * 	<jc>// Method is GET, path is "/foobar".</jc>
	 * 	<ja>@RestMethod</ja>
	 * 	<jk>public void</jk> foobar() {...}
	 * </p>
	 * <p class='bcode w800'>
	 * 	<jc>// Method is GET, path is "/".</jc>
	 * 	<ja>@RestMethod</ja>
	 * 	<jk>public void</jk> get() {...}
	 * </p>
	 *
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='ja'>{@link org.apache.juneau.http.annotation.Path}
	 * </ul>
	 */
	String path() default "";

	/**
	 * Sets the POJO swaps for the serializers and parsers defined on this method.
	 *
	 * <p>
	 * If no value is specified, the POJO swaps are inherited from the class.
	 * <br>Otherwise, this value overrides the POJO swaps defined on the class.
	 *
	 * <p>
	 * Use {@link Inherit} to inherit POJO swaps defined on the class.
	 *
	 * <p>
	 * Use {@link None} to suppress inheriting POJO swaps defined on the class.
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link BeanContext#BEAN_pojoSwaps}
	 * </ul>
	 *
	 * @deprecated Use {@link BeanConfig#pojoSwaps()}
	 */
	@Deprecated
	Class<?>[] pojoSwaps() default {};

	/**
	 * URL path pattern priority.
	 *
	 * <p>
	 * To force path patterns to be checked before other path patterns, use a higher priority number.
	 *
	 * <p>
	 * By default, it's <code>0</code>, which means it will use an internal heuristic to determine a best match.
	 */
	int priority() default 0;

	/**
	 * Same as {@link RestResource#properties() @RestResource(properties)}, except defines property values by default when this method is called.
	 *
	 * <p>
	 * This is equivalent to simply calling <code>res.addProperties()</code> in the Java method, but is provided for
	 * convenience.
	 */
	Property[] properties() default {};

	/**
	 * Serializers.
	 *
	 * <p>
	 * If no value is specified, the serializers are inherited from the class.
	 * <br>Otherwise, this value overrides the serializers defined on the class.
	 *
	 * <p>
	 * Use {@link Inherit} to inherit serializers defined on the class.
	 *
	 * <p>
	 * Use {@link None} to suppress inheriting serializers defined on the class.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jk>public class</jk> MyResource <jk>extends</jk> RestServlet {
	 *
	 * 		<ja>@RestMethod</ja>(
	 * 			name=<jsf>GET</jsf>,
	 * 			path=<js>"/foo"</js>,
	 * 			serializers=MySpecialSerializer.<jk>class</jk>
	 * 		)
	 * 		<jk>public</jk> Object doGetWithSpecialAcceptType() {
	 * 			<jc>// Handle request for special Accept type</jc>
	 * 		}
	 * 	}
	 * </p>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link RestContext#REST_serializers}
	 * </ul>
	 */
	Class<?>[] serializers() default {};

	/**
	 * Optional summary for the exposed API.
	 *
	 * <p>
	 * This summary is used in the following locations:
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The value returned by {@link RestRequest#getMethodSummary()}.
	 * 	<li>
	 * 		The <js>"$R{methodSummary}"</js> variable.
	 * 	<li>
	 * 		The summary of the method in the Swagger page.
	 * </ul>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		Corresponds to the swagger field <code>/paths/{path}/{method}/summary</code>.
	 * 	<li>
	 * 		Supports {@doc DefaultRestSvlVariables}
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * </ul>
	 */
	String summary() default "";

	/**
	 * Supported accept media types.
	 *
	 * <p>
	 * Overrides the media types inferred from the serializers that identify what media types can be produced by the resource.
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		Supports {@doc DefaultRestSvlVariables}
	 * 		(e.g. <js>"$S{mySystemProperty}"</js>).
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link RestContext#REST_produces}
	 * </ul>
	 */
	String[] produces() default {};

	/**
	 * Supported content media types.
	 *
	 * <p>
	 * Overrides the media types inferred from the parsers that identify what media types can be consumed by the resource.
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		Supports {@doc DefaultRestSvlVariables}
	 * 		(e.g. <js>"$S{mySystemProperty}"</js>).
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link RestContext#REST_consumes}
	 * </ul>
	 */
	String[] consumes() default {};

	/**
	 * Provides swagger-specific metadata on this method.
	 *
	 * <p>
	 * Used to populate the auto-generated OPTIONS swagger documentation.
	 *
	 * <p>
	 * The format of this annotation is JSON when all individual parts are concatenated.
	 * <br>The starting and ending <js>'{'</js>/<js>'}'</js> characters around the entire value are optional.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<ja>@RestMethod</ja>(
	 * 		name=<jsf>PUT</jsf>,
	 * 		path=<js>"/{propertyName}"</js>,
	 *
	 * 		<jc>// Swagger info.</jc>
	 * 		swagger={
	 * 			<js>"parameters:["</js>,
	 * 				<js>"{name:'propertyName',in:'path',description:'The system property name.'},"</js>,
	 * 				<js>"{in:'body',description:'The new system property value.'}"</js>,
	 * 			<js>"],"</js>,
	 * 			<js>"responses:{"</js>,
	 * 				<js>"302: {headers:{Location:{description:'The root URL of this resource.'}}},"</js>,
	 * 				<js>"403: {description:'User is not an admin.'}"</js>,
	 * 			<js>"}"</js>
	 * 		}
	 * 	)
	 * </p>
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The format is {@doc juneau-marshall.JsonDetails.SimplifiedJson}.
	 * 		<br>Multiple lines are concatenated with newlines.
	 * 	<li>
	 * 		The starting and ending <js>'{'</js>/<js>'}'</js> characters around the entire value are optional.
	 * 	<li>
	 * 		These values are superimposed on top of any Swagger JSON file present for the resource in the classpath.
	 * 	<li>
	 * 		Supports {@doc DefaultRestSvlVariables}
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * </ul>
	 *
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='ja'>{@link MethodSwagger}
	 * 	<li class='jm'>{@link RestInfoProvider#getSwagger(RestRequest)}
	 * </ul>
	 */
	MethodSwagger swagger() default @MethodSwagger;
}
