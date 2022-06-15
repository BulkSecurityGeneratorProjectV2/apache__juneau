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
package org.apache.juneau.rest.client;

import static org.apache.juneau.assertions.Assertions.*;
import static org.apache.juneau.http.HttpHeaders.*;
import static org.apache.juneau.httppart.HttpPartSchema.*;
import static org.apache.juneau.internal.CollectionUtils.*;
import static org.junit.runners.MethodSorters.*;
import static java.time.format.DateTimeFormatter.*;
import static java.time.temporal.ChronoUnit.*;
import static org.apache.juneau.ListOperation.*;

import java.time.*;
import java.util.*;

import org.apache.http.Header;
import org.apache.juneau.http.header.*;
import org.apache.juneau.httppart.*;
import org.apache.juneau.marshall.*;
import org.apache.juneau.oapi.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.rest.config.*;
import org.apache.juneau.rest.logging.*;
import org.apache.juneau.rest.mock.*;
import org.apache.juneau.rest.servlet.*;
import org.apache.juneau.serializer.*;
import org.apache.juneau.testutils.*;
import org.apache.juneau.uon.*;
import org.junit.*;

@FixMethodOrder(NAME_ASCENDING)
public class RestClient_Headers_Test {

	public static final CaptureLogger LOGGER = new CaptureLogger();

	public static class CaptureLogger extends BasicTestCaptureRestLogger {
		public static CaptureLogger getInstance(RestLogger.Builder x) {
			return LOGGER;
		}
	}

	public static class ABean {
		public int f;
		static ABean get() {
			ABean x = new ABean();
			x.f = 1;
			return x;
		}
		@Override
		public String toString() {
			return SimpleJson.DEFAULT.toString(this);
		}
	}

	private static ABean bean = ABean.get();

	@Rest(callLogger=CaptureLogger.class)
	public static class A extends BasicRestObject implements BasicUniversalConfig {
		@RestGet
		public String[] headers(org.apache.juneau.rest.RestRequest req) {
			return req.getHeaders().getAll(req.getHeader("Check").orElse(null)).stream().map(x -> x.getValue()).toArray(String[]::new);
		}
	}

	private static final ZonedDateTime ZONEDDATETIME = ZonedDateTime.from(RFC_1123_DATE_TIME.parse("Mon, 3 Dec 2007 10:15:30 GMT")).truncatedTo(SECONDS);
	private static final String PARSEDZONEDDATETIME = "Mon, 3 Dec 2007 10:15:30 GMT";

	//------------------------------------------------------------------------------------------------------------------
	// Method tests
	//------------------------------------------------------------------------------------------------------------------

	@Test
	public void a01_header_String_Object() throws Exception {
		checkFooClient().header("Foo","bar").build().get("/headers").run().assertContent().is("['bar']");
		checkFooClient().build().get("/headers").header("Foo","baz").run().assertContent().is("['baz']");
		checkFooClient().header("Foo","bar").build().get("/headers").header("Foo","baz").run().assertContent().is("['bar','baz']");
		checkFooClient().headers(header("Foo",bean,null)).build().get("/headers").header("Foo",bean).run().assertContent().is("['f=1','f=1']");
		checkFooClient().headers(header("Foo",null,null)).build().get("/headers").header("Foo",null).run().assertContent().is("[]");
	}

	@Test
	public void a02_header_String_Object_Schema() throws Exception {
		List<String> l1 = list("bar","baz"), l2 = list("qux","quux");
		checkFooClient().headers(header("Foo",l1,T_ARRAY_PIPES)).build().get("/headers").header(header("Foo",l2,T_ARRAY_PIPES)).run().assertContent().is("['bar|baz','qux|quux']");
	}

	@Test
	public void a03_header_Header() throws Exception {
		checkFooClient().headers(header("Foo","bar")).build().get("/headers").header(header("Foo","baz")).run().assertContent().is("['bar','baz']");
		checkFooClient().headers(stringHeader("Foo","bar")).build().get("/headers").header(stringHeader("Foo","baz")).run().assertContent().is("['bar','baz']");
	}

	@Test
	public void a06_headers_Objects() throws Exception {
		checkFooClient().headers((Header)null).build().get("/headers").headers((Header)null).run().assertContent().is("[]");
		checkFooClient().headers(header("Foo","bar"),header("Baz","baz")).build().get("/headers").headers(APPEND,header("Foo","baz"),header("Baz","quux")).run().assertContent().is("['bar','baz']");
		checkFooClient().headers(header("Foo","Bar",null).serializer(OpenApiSerializer.DEFAULT)).build().get("/headers").headers(APPEND,serializedHeader("Foo","Baz").serializer(OpenApiSerializer.DEFAULT)).run().assertContent().is("['Bar','Baz']");
		checkFooClient().headers(serializedHeader("Foo",()->"Bar").serializer(OpenApiSerializer.DEFAULT)).build().get("/headers").headers(APPEND,serializedHeader("Foo",()->"Baz").serializer(OpenApiSerializer.DEFAULT)).run().assertContent().is("['Bar','Baz']");
		checkClient("f").build().get("/headers").headersBean(bean).run().assertContent().is("['1']");

		checkFooClient().headers(header("Foo",null,null).skipIfEmpty().schema(HttpPartSchema.create()._default("bar").build())).build().get("/headers").run().assertContent().is("['bar']");
	}

	@Test
	public void a07_header_AddFlag_String_Object() throws Exception {
		checkFooClient().header("Foo","bar").build().get("/headers").headers(APPEND,header("Foo","baz")).run().assertContent().is("['bar','baz']");
		checkFooClient().header("Foo","bar").build().get("/headers").headers(SET,header("Foo","baz")).run().assertContent().is("['baz']");
		checkFooClient().header("Foo","bar").build().get("/headers").headers(PREPEND,header("Foo","baz")).run().assertContent().is("['baz','bar']");
	}

	@Test
	public void a07_header_AddFlag_String_Object_Schema() throws Exception {
		List<String> l = list("baz","qux");
		checkFooClient().header("Foo","bar").build().get("/headers").headers(APPEND,header("Foo",l,T_ARRAY_PIPES)).run().assertContent().is("['bar','baz|qux']");
		checkFooClient().header("Foo","bar").build().get("/headers").headers(SET,header("Foo",l,T_ARRAY_PIPES)).run().assertContent().is("['baz|qux']");
		checkFooClient().header("Foo","bar").build().get("/headers").headers(PREPEND,header("Foo",l,T_ARRAY_PIPES)).run().assertContent().is("['baz|qux','bar']");
	}

	@Test
	public void a07_headers_AddFlag_Objects() throws Exception {
		checkFooClient().header("Foo","bar").build().get("/headers").headers(APPEND,header("Foo","baz")).run().assertContent().is("['bar','baz']");
		checkFooClient().header("Foo","bar").build().get("/headers").headers(SET,header("Foo","baz")).run().assertContent().is("['baz']");
		checkFooClient().header("Foo","bar").build().get("/headers").headers(PREPEND,header("Foo","baz")).run().assertContent().is("['baz','bar']");
	}

	@Test
	public void a08_header_String_Supplier() throws Exception {
		TestSupplier s = TestSupplier.of("foo");
		RestClient x = checkFooClient().headers(header("Foo",s,null)).build();
		x.get("/headers").header("Foo",s).run().assertContent().is("['foo','foo']");
		s.set("bar");
		x.get("/headers").header("Foo",s).run().assertContent().is("['bar','bar']");
	}

	@Test
	public void a09_headers_String_Object_Schema_Serializer() throws Exception {
		checkFooClient().headers(header("Foo",bean,null).serializer(MockWriterSerializer.X)).build().get("/headers").run().assertContent().is("['x{f:1}x']");
	}

	@Test
	public void a10_headers_String_Supplier_Schema() throws Exception {
		TestSupplier s = TestSupplier.of(new String[]{"foo","bar"});
		RestClient x = checkFooClient().headers(header("Foo",s,T_ARRAY_PIPES)).build();
		x.get("/headers").header(header("Foo",s,T_ARRAY_PIPES)).run().assertContent().is("['foo|bar','foo|bar']");
		s.set(new String[]{"bar","baz"});
		x.get("/headers").header(header("Foo",s,T_ARRAY_PIPES)).run().assertContent().is("['bar|baz','bar|baz']");
	}

	@Test
	public void a11_headers_String_Supplier_Schema_Serializer() throws Exception {
		TestSupplier s = TestSupplier.of(new String[]{"foo","bar"});
		checkFooClient().headers(header("Foo",s,T_ARRAY_PIPES).serializer(UonSerializer.DEFAULT)).build().get("/headers").run().assertContent().is("['@(foo,bar)']");
	}

	public static class A12 implements HttpPartSerializer {
		@Override
		public HttpPartSerializerSession getPartSession() {
			return new HttpPartSerializerSession() {
				@Override
				public String serialize(HttpPartType type, HttpPartSchema schema, Object value) throws SerializeException, SchemaValidationException {
					throw new SerializeException("bad");
				}
			};
		}
	}

	@Test
	public void a12_badSerialization() throws Exception {
		assertThrown(()->checkFooClient().headers(header("Foo","bar",null).serializer(new A12())).build().get().run()).asMessages().isContains("bad");
	}

	//------------------------------------------------------------------------------------------------------------------
	// Other tests
	//------------------------------------------------------------------------------------------------------------------

	@Test
	public void b01_standardHeaders() throws Exception {
		checkClient("Accept").accept("text/plain").build().get("/headers").run().assertContent().is("['text/plain']");
		checkClient("Accept-Charset").acceptCharset("UTF-8").build().get("/headers").run().assertContent().is("['UTF-8']");
		checkClient("Accept-Encoding").acceptEncoding("identity").build().get("/headers").run().assertContent().is("['identity']");
		checkClient("Accept-Language").acceptLanguage("en").build().get("/headers").run().assertContent().is("['en']");
		checkClient("Authorization").authorization("foo").build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Cache-Control").cacheControl("none").build().get("/headers").run().assertContent().is("['none']");
		checkClient("Client-Version").clientVersion("1").build().get("/headers").run().assertContent().is("['1']");
		checkClient("Connection").connection("foo").build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Content-Type").contentType("foo").build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Content-Encoding").contentEncoding("identity").build().get("/headers").run().assertContent().is("['identity']");
		checkClient("From").from("foo").build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Host").host("foo").build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Max-Forwards").maxForwards(10).build().get("/headers").run().assertContent().is("['10']");
		checkClient("No-Trace").noTrace().build().get("/headers").run().assertContent().is("['true','true']");
		checkClient("Origin").origin("foo").build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Pragma").pragma("foo").build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Proxy-Authorization").proxyAuthorization("foo").build().get("/headers").run().assertContent().is("['foo']");
		checkClient("User-Agent").userAgent("foo").build().get("/headers").run().assertContent().is("['foo']");

		checkClient("Accept").build().get("/headers").accept("text/plain").run().assertContent().is("['text/plain']");
		checkClient("Accept-Charset").build().get("/headers").acceptCharset("UTF-8").run().assertContent().is("['UTF-8']");
		checkClient("Accept-Encoding").build().get("/headers").acceptEncoding("identity").run().assertContent().is("['identity']");
		checkClient("Accept-Language").build().get("/headers").acceptLanguage("en").run().assertContent().is("['en']");
		checkClient("Authorization").build().get("/headers").authorization("foo").run().assertContent().is("['foo']");
		checkClient("Cache-Control").build().get("/headers").cacheControl("none").run().assertContent().is("['none']");
		checkClient("Client-Version").build().get("/headers").clientVersion("1").run().assertContent().is("['1']");
		checkClient("Connection").build().get("/headers").connection("foo").run().assertContent().is("['foo']");
		checkClient("Content-Length").build().get("/headers").contentLength(123l).run().assertContent().is("['123']");
		checkClient("Content-Type").build().get("/headers").contentType("foo").run().assertContent().is("['foo']");
		checkClient("Content-Encoding").build().get("/headers").contentEncoding("identity").run().assertContent().is("['identity']");
		checkClient("Date").build().get("/headers").date(ZONEDDATETIME).run().assertContent().is("['"+PARSEDZONEDDATETIME+"']");
		checkClient("Expect").build().get("/headers").expect("foo").run().assertContent().is("['foo']");
		checkClient("Forwarded").build().get("/headers").forwarded("foo").run().assertContent().is("['foo']");
		checkClient("From").build().get("/headers").from("foo").run().assertContent().is("['foo']");
		checkClient("Host").build().get("/headers").hostHeader("foo").run().assertContent().is("['foo']");
		checkClient("If-Match").build().get("/headers").ifMatch("\"foo\"").run().assertContent().is("['\"foo\"']");
		checkClient("If-Modified-Since").build().get("/headers").ifModifiedSince(ZONEDDATETIME).run().assertContent().is("['"+PARSEDZONEDDATETIME+"']");
		checkClient("If-None-Match").build().get("/headers").ifNoneMatch("\"foo\"").run().assertContent().is("['\"foo\"']");
		checkClient("If-Range").build().get("/headers").ifRange("\"foo\"").run().assertContent().is("['\"foo\"']");
		checkClient("If-Unmodified-Since").build().get("/headers").ifUnmodifiedSince(ZONEDDATETIME).run().assertContent().is("['"+PARSEDZONEDDATETIME+"']");
		checkClient("Max-Forwards").build().get("/headers").maxForwards(10).run().assertContent().is("['10']");
		checkClient("No-Trace").build().get("/headers").noTrace().run().assertContent().is("['true','true']");
		checkClient("Origin").build().get("/headers").origin("foo").run().assertContent().is("['foo']");
		checkClient("Pragma").build().get("/headers").pragma("foo").run().assertContent().is("['foo']");
		checkClient("Proxy-Authorization").build().get("/headers").proxyAuthorization("foo").run().assertContent().is("['foo']");
		checkClient("Range").build().get("/headers").range("foo").run().assertContent().is("['foo']");
		checkClient("Referer").build().get("/headers").referer("foo").run().assertContent().is("['foo']");
		checkClient("TE").build().get("/headers").te("foo").run().assertContent().is("['foo']");
		checkClient("User-Agent").build().get("/headers").userAgent("foo").run().assertContent().is("['foo']");
		checkClient("Upgrade").build().get("/headers").upgrade("foo").run().assertContent().is("['foo']");
		checkClient("Via").build().get("/headers").via("foo").run().assertContent().is("['foo']");
		checkClient("Warning").build().get("/headers").warning("foo").run().assertContent().is("['foo']");
	}

	@Test
	public void b02_headerBeans() throws Exception {
		checkClient("Accept").headers(new Accept("text/plain")).build().get("/headers").run().assertContent().is("['text/plain']");
		checkClient("Accept-Charset").headers(new AcceptCharset("UTF-8")).build().get("/headers").run().assertContent().is("['UTF-8']");
		checkClient("Accept-Encoding").headers(new AcceptEncoding("identity")).build().get("/headers").run().assertContent().is("['identity']");
		checkClient("Accept-Language").headers(new AcceptLanguage("en")).build().get("/headers").run().assertContent().is("['en']");
		checkClient("Authorization").headers(new Authorization("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Cache-Control").headers(new CacheControl("none")).header("X-Expect","none").build().get("/headers").run().assertContent().is("['none']");
		checkClient("Client-Version").headers(new ClientVersion("1")).build().get("/headers").run().assertContent().is("['1']");
		checkClient("Connection").headers(new Connection("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Content-Length").headers(new ContentLength(123l)).build().get("/headers").run().assertContent().is("['123']");
		checkClient("Content-Type").headers(new ContentType("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Date").headers(new org.apache.juneau.http.header.Date(PARSEDZONEDDATETIME)).build().get("/headers").run().assertContent().is("['"+PARSEDZONEDDATETIME+"']");
		checkClient("Date").headers(new org.apache.juneau.http.header.Date(ZONEDDATETIME)).build().get("/headers").run().assertContent().is("['"+PARSEDZONEDDATETIME+"']");
		checkClient("Expect").headers(new Expect("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Forwarded").headers(new Forwarded("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("From").headers(new From("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Host").headers(new Host("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("If-Match").headers(new IfMatch("\"foo\"")).build().get("/headers").run().assertContent().is("['\"foo\"']");
		checkClient("If-Modified-Since").headers(new IfModifiedSince(ZONEDDATETIME)).build().get("/headers").run().assertContent().is("['"+PARSEDZONEDDATETIME+"']");
		checkClient("If-Modified-Since").headers(new IfModifiedSince(PARSEDZONEDDATETIME)).build().get("/headers").run().assertContent().is("['"+PARSEDZONEDDATETIME+"']");
		checkClient("If-None-Match").headers(new IfNoneMatch("\"foo\"")).build().get("/headers").run().assertContent().is("['\"foo\"']");
		checkClient("If-Range").headers(new IfRange("\"foo\"")).build().get("/headers").run().assertContent().is("['\"foo\"']");
		checkClient("If-Unmodified-Since").headers(new IfUnmodifiedSince(ZONEDDATETIME)).build().get("/headers").run().assertContent().is("['"+PARSEDZONEDDATETIME+"']");
		checkClient("If-Unmodified-Since").headers(new IfUnmodifiedSince(PARSEDZONEDDATETIME)).build().get("/headers").run().assertContent().is("['"+PARSEDZONEDDATETIME+"']");
		checkClient("Max-Forwards").headers(new MaxForwards(10)).build().get("/headers").run().assertContent().is("['10']");
		checkClient("No-Trace").headers(new NoTrace("true")).build().get("/headers").run().assertContent().is("['true','true']");
		checkClient("Origin").headers(new Origin("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Pragma").headers(new Pragma("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Proxy-Authorization").headers(new ProxyAuthorization("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Range").headers(new Range("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Referer").headers(new Referer("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("TE").headers(new TE("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("User-Agent").headers(new UserAgent("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Upgrade").headers(new Upgrade("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Via").headers(new Via("foo")).build().get("/headers").run().assertContent().is("['foo']");
		checkClient("Warning").headers(new Warning("foo")).build().get("/headers").run().assertContent().is("['foo']");
	}

	@Test
	public void b03_debugHeader() throws Exception {
		checkClient("Debug").build().get("/headers").debug().suppressLogging().run().assertContent().is("['true']");
	}

	@Test
	public void b04_dontOverrideAccept() throws Exception {
		checkClient("Accept").header("Accept","text/plain").build().get("/headers").run().assertContent().is("['text/plain']");
		checkClient("Accept").header("Accept","text/foo").build().get("/headers").header("Accept","text/plain").run().assertContent().is("['text/foo','text/plain']");
		RestClient rc = checkClient("Accept").header("Accept","text/foo").build();
		RestRequest req = rc.get("/headers");
		req.setHeader("Accept","text/plain");
		req.run().assertContent().is("['text/plain']");
	}

	@Test
	public void b05_dontOverrideContentType() throws Exception {
		checkClient("Content-Type").header("Content-Type","text/plain").build().get("/headers").run().assertContent().is("['text/plain']");
		checkClient("Content-Type").header("Content-Type","text/foo").build().get("/headers").header("Content-Type","text/plain").run().assertContent().is("['text/foo','text/plain']");
	}

	//------------------------------------------------------------------------------------------------------------------
	// Helper methods.
	//------------------------------------------------------------------------------------------------------------------

	private static org.apache.http.Header header(String name, String val) {
		return basicHeader(name, val);
	}

	private static SerializedHeader header(String name, Object val, HttpPartSchema schema) {
		return serializedHeader(name, val).schema(schema);
	}

	private static RestClient.Builder checkFooClient() {
		return MockRestClient.create(A.class).simpleJson().header("Check","Foo");
	}

	private static RestClient.Builder checkClient(String headerToCheck) {
		return MockRestClient.create(A.class).simpleJson().header("Check",headerToCheck).noTrace();
	}
}
