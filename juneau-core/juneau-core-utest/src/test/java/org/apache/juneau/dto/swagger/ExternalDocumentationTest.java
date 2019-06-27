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

import static org.apache.juneau.testutils.TestUtils.*;
import static org.junit.Assert.*;

import java.net.*;

import org.apache.juneau.json.*;
import org.junit.*;

/**
 * Testcase for {@link ExternalDocumentation}.
 */
public class ExternalDocumentationTest {

	/**
	 * Test method for {@link ExternalDocumentation#description(java.lang.Object)}.
	 */
	@Test
	public void testDescription() {
		ExternalDocumentation t = new ExternalDocumentation();

		t.description("foo");
		assertEquals("foo", t.getDescription());

		t.description(new StringBuilder("foo"));
		assertEquals("foo", t.getDescription());
		assertInstanceOf(String.class, t.getDescription());

		t.description(null);
		assertNull(t.getDescription());
	}

	/**
	 * Test method for {@link ExternalDocumentation#url(java.lang.Object)}.
	 */
	@Test
	public void testUrl() {
		ExternalDocumentation t = new ExternalDocumentation();

		t.url("foo");
		assertEquals("foo", t.getUrl().toString());

		t.url(new StringBuilder("foo"));
		assertEquals("foo", t.getUrl().toString());
		assertInstanceOf(URI.class, t.getUrl());

		t.url(null);
		assertNull(t.getUrl());
	}

	/**
	 * Test method for {@link ExternalDocumentation#set(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testSet() throws Exception {
		ExternalDocumentation t = new ExternalDocumentation();

		t
			.set("description", "foo")
			.set("url", "bar")
			.set("$ref", "baz");

		assertObjectEquals("{description:'foo',url:'bar','$ref':'baz'}", t);

		t
			.set("description", new StringBuilder("foo"))
			.set("url", new StringBuilder("bar"))
			.set("$ref", new StringBuilder("baz"));

		assertObjectEquals("{description:'foo',url:'bar','$ref':'baz'}", t);

		assertEquals("foo", t.get("description", String.class));
		assertEquals("bar", t.get("url", URI.class).toString());
		assertEquals("baz", t.get("$ref", String.class));

		assertInstanceOf(String.class, t.get("description", String.class));
		assertInstanceOf(URI.class, t.get("url", URI.class));
		assertInstanceOf(String.class, t.get("$ref", String.class));

		t.set("null", null).set(null, "null");
		assertNull(t.get("null", Object.class));
		assertNull(t.get(null, Object.class));
		assertNull(t.get("foo", Object.class));

		assertObjectEquals("{description:'foo',url:'bar','$ref':'baz'}", JsonParser.DEFAULT.parse("{description:'foo',url:'bar','$ref':'baz'}", ExternalDocumentation.class));
	}
}
