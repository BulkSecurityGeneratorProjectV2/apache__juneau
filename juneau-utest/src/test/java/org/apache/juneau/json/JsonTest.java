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
package org.apache.juneau.json;

import static org.apache.juneau.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

import java.util.*;

import org.apache.juneau.collections.*;
import org.apache.juneau.json.annotation.*;
import org.apache.juneau.serializer.*;
import org.junit.*;

@SuppressWarnings({"serial"})
@FixMethodOrder(NAME_ASCENDING)
public class JsonTest {

	//====================================================================================================
	// testBasic
	//====================================================================================================
	@Test
	public void testBasic() throws Exception {
		Map<String,Object> m = new LinkedHashMap<>();
		List<Object> l = new LinkedList<>();

		WriterSerializer s1 = JsonSerializer.create().json5().keepNullProperties().build();
		WriterSerializer s2 = JsonSerializer.create().simpleAttrs().keepNullProperties().build();
		String r;

		// Null keys and values
		m.clear();
		m.put(null, null);
		m.put("aaa", "bbb");
		assertEquals("A1", "{null:null,aaa:'bbb'}", s1.serialize(m));

		// Escapes.
		// String = ["]
		m.clear();
		m.put("x", "[\"]");
		assertEquals("{x:\"[\\\"]\"}", s2.serialize(m));
		// String = [\"]
		// JSON = {x:"\\\""}
		m.clear();
		m.put("x", "[\\\"]");
		assertEquals("{x:\"[\\\\\\\"]\"}", s2.serialize(m));

		// String = [\w[\w\-\.]{3,}\w]
		// JSON = {x:"\\w[\\w\\-\\.]{3,}\\w"}
		m.clear();
		r = "\\w[\\w\\-\\.]{3,}\\w";
		m.put("x", r);
		assertEquals("{x:\"\\\\w[\\\\w\\\\-\\\\.]{3,}\\\\w\"}", s2.serialize(m));
		assertEquals(r, JsonMap.ofJson(s2.serialize(m)).getString("x"));

		// String = [foo\bar]
		// JSON = {x:"foo\\bar"}
		m.clear();
		m.put("x", "foo\\bar");
		assertEquals("{x:\"foo\\\\bar\"}", s2.serialize(m));

		m.clear();
		m.put("null", null);
		m.put("aaa", "bbb");
		assertEquals("A2", "{'null':null,aaa:'bbb'}", s1.serialize(m));

		m.clear();
		m.put(null, "null");
		m.put("aaa", "bbb");
		assertEquals("A3", "{null:'null',aaa:'bbb'}", s1.serialize(m));

		// Arrays
		m.clear();
		l.clear();
		m.put("J", "f1");
		m.put("B", "b");
		m.put("C", "c");
		l.add("1");
		l.add("2");
		l.add("3");
		Object o = new Object[] { m, l };
		Object o2 = new Object[] { o, "foo", "bar", new Integer(1), new Boolean(false), new Float(1.2), null };
		assertEquals("K1", "[[{J:'f1',B:'b',C:'c'},['1','2','3']],'foo','bar',1,false,1.2,null]", s1.serialize(o2));
	}

	@Test
	public void testReservedKeywordAttributes() throws Exception {
		Map<String,Object> m = new LinkedHashMap<>();

		// Keys with reserved names.
		for (String attr : new String[]{"","true","false","null","try","123","1x","-123",".123"}) {
			m.clear();
			m.put(attr,1);
			assertObject(m).asJson().is("{'"+attr+"':1}");
		}
	}

	//====================================================================================================
	// Validate various backslashes in strings.
	//====================================================================================================
	@Test
	public void testBackslashesInStrings() throws Exception {
		JsonSerializer s = JsonSerializer.create().simpleAttrs().keepNullProperties().build();
		String r, r2;

		// [\\]
		r = "\\";
		r2 = s.serialize(r);
		assertEquals(r2, "\"\\\\\"");
		assertEquals(JsonParser.DEFAULT.parse(r2, Object.class), r);

		// [\b\f\n\t]
		r = "\b\f\n\t";
		r2 = s.serialize(r);
		assertEquals("\"\\b\\f\\n\\t\"", r2);
		assertEquals(r, JsonParser.DEFAULT.parse(r2, Object.class));

		// Special JSON case:  Forward slashes can OPTIONALLY be escaped.
		// [\/]
		assertEquals(JsonParser.DEFAULT.parse("\"\\/\"", Object.class), "/");

		// Unicode
		r = "\u1234\u1ABC\u1abc";
		r2 = s.serialize(r);
		assertEquals("\"\u1234\u1ABC\u1abc\"", r2);

		assertEquals("\u1234", JsonParser.DEFAULT.parse("\"\\u1234\"", Object.class));
	}

	//====================================================================================================
	// Indentation
	//====================================================================================================
	@Test
	public void testIndentation() throws Exception {
		JsonMap m = JsonMap.ofJson("{J:{B:['c',{D:'e'},['f',{G:'h'},1,false]]},I:'j'}");
		String e = ""
			+ "{"
			+ "\n	J: {"
			+ "\n		B: ["
			+ "\n			'c',"
			+ "\n			{"
			+ "\n				D: 'e'"
			+ "\n			},"
			+ "\n			["
			+ "\n				'f',"
			+ "\n				{"
			+ "\n					G: 'h'"
			+ "\n				},"
			+ "\n				1,"
			+ "\n				false"
			+ "\n			]"
			+ "\n		]"
			+ "\n	},"
			+ "\n	I: 'j'"
			+ "\n}";
		assertEquals(e, Json5Serializer.DEFAULT_READABLE.serialize(m));
	}

	//====================================================================================================
	// Escaping double quotes
	//====================================================================================================
	@Test
	public void testEscapingDoubleQuotes() throws Exception {
		JsonSerializer s = JsonSerializer.DEFAULT;
		String r = s.serialize(JsonMap.of("f1", "x'x\"x"));
		assertEquals("{\"f1\":\"x'x\\\"x\"}", r);
		JsonParser p = JsonParser.DEFAULT;
		assertEquals("x'x\"x", p.parse(r, JsonMap.class).getString("f1"));
	}

	//====================================================================================================
	// Escaping single quotes
	//====================================================================================================
	@Test
	public void testEscapingSingleQuotes() throws Exception {
		JsonSerializer s = Json5Serializer.DEFAULT;
		String r = s.serialize(JsonMap.of("f1", "x'x\"x"));
		assertEquals("{f1:'x\\'x\"x'}", r);
		JsonParser p = JsonParser.DEFAULT;
		assertEquals("x'x\"x", p.parse(r, JsonMap.class).getString("f1"));
	}

	//====================================================================================================
	// testWrapperAttrAnnotationOnBean
	//====================================================================================================
	@Test
	public void testWrapperAttrAnnotationOnBean() throws Exception {
		JsonSerializer s = Json5Serializer.DEFAULT;
		JsonParser p = JsonParser.DEFAULT;
		String r;

		A t = A.create();
		r = s.serialize(t);
		assertEquals("{foo:{f1:1}}", r);
		t = p.parse(r, A.class);
		assertEquals(1, t.f1);

		Map<String,A> m = new LinkedHashMap<>();
		m.put("bar", A.create());
		r = s.serialize(m);
		assertEquals("{bar:{foo:{f1:1}}}", r);

		m = p.parse(r, LinkedHashMap.class, String.class, A.class);
		assertEquals(1, m.get("bar").f1);
	}

	@Json(wrapperAttr="foo")
	public static class A {
		public int f1;

		static A create() {
			A a = new A();
			a.f1 = 1;
			return a;
		}
	}

	@Test
	public void testWrapperAttrAnnotationOnBean_usingConfig() throws Exception {
		JsonSerializer s = Json5Serializer.DEFAULT.copy().applyAnnotations(A2Config.class).build();
		JsonParser p = JsonParser.DEFAULT.copy().applyAnnotations(A2Config.class).build();
		String r;

		A2 t = A2.create();
		r = s.serialize(t);
		assertEquals("{foo:{f1:1}}", r);
		t = p.parse(r, A2.class);
		assertEquals(1, t.f1);

		Map<String,A2> m = new LinkedHashMap<>();
		m.put("bar", A2.create());
		r = s.serialize(m);
		assertEquals("{bar:{foo:{f1:1}}}", r);

		m = p.parse(r, LinkedHashMap.class, String.class, A2.class);
		assertEquals(1, m.get("bar").f1);
	}

	@Json(on="Dummy1",wrapperAttr="foo")
	@Json(on="A2",wrapperAttr="foo")
	@Json(on="Dummy2",wrapperAttr="foo")
	private static class A2Config {}

	public static class A2 {
		public int f1;

		static A2 create() {
			A2 a = new A2();
			a.f1 = 1;
			return a;
		}
	}

	//====================================================================================================
	// testWrapperAttrAnnotationOnNonBean
	//====================================================================================================
	@Test
	public void testWrapperAttrAnnotationOnNonBean() throws Exception {
		JsonSerializer s = Json5Serializer.DEFAULT;
		JsonParser p = JsonParser.DEFAULT;
		String r;

		B t = B.create();
		r = s.serialize(t);
		assertEquals("{foo:'1'}", r);
		t = p.parse(r, B.class);
		assertEquals(1, t.f1);

		Map<String,B> m = new LinkedHashMap<>();
		m.put("bar", B.create());
		r = s.serialize(m);
		assertEquals("{bar:{foo:'1'}}", r);

		m = p.parse(r, LinkedHashMap.class, String.class, B.class);
		assertEquals(1, m.get("bar").f1);
	}

	@Json(wrapperAttr="foo")
	public static class B {
		int f1;

		static B create() {
			B b = new B();
			b.f1 = 1;
			return b;
		}

		@Override /* Object */
		public String toString() {
			return String.valueOf(f1);
		}

		public static B valueOf(String s) {
			B b = new B();
			b.f1 = Integer.parseInt(s);
			return b;
		}
	}

	@Test
	public void testWrapperAttrAnnotationOnNonBean_usingConfig() throws Exception {
		JsonSerializer s = Json5Serializer.DEFAULT.copy().applyAnnotations(B2Config.class).build();
		JsonParser p = JsonParser.DEFAULT.copy().applyAnnotations(B2Config.class).build();;
		String r;

		B2 t = B2.create();
		r = s.serialize(t);
		assertEquals("{foo:'1'}", r);
		t = p.parse(r, B2.class);
		assertEquals(1, t.f1);

		Map<String,B2> m = new LinkedHashMap<>();
		m.put("bar", B2.create());
		r = s.serialize(m);
		assertEquals("{bar:{foo:'1'}}", r);

		m = p.parse(r, LinkedHashMap.class, String.class, B2.class);
		assertEquals(1, m.get("bar").f1);
	}

	@Json(on="B2",wrapperAttr="foo")
	private static class B2Config {}

	public static class B2 {
		int f1;

		static B2 create() {
			B2 b = new B2();
			b.f1 = 1;
			return b;
		}

		@Override /* Object */
		public String toString() {
			return String.valueOf(f1);
		}

		public static B2 valueOf(String s) {
			B2 b = new B2();
			b.f1 = Integer.parseInt(s);
			return b;
		}
	}

	//====================================================================================================
	// testSubclassedList
	//====================================================================================================
	@Test
	public void testSubclassedList() throws Exception {
		JsonSerializer s = JsonSerializer.DEFAULT;
		Map<String,Object> o = new HashMap<>();
		o.put("c", new C());
		assertEquals("{\"c\":[]}", s.serialize(o));
	}

	public static class C extends LinkedList<String> {
	}

	//====================================================================================================
	// testEscapeSolidus
	//====================================================================================================
	@Test
	public void testEscapeSolidus() throws Exception {
		JsonSerializer s = JsonSerializer.create().build();
		String r = s.serialize("foo/bar");
		assertEquals("\"foo/bar\"", r);
		r = JsonParser.DEFAULT.parse(r, String.class);
		assertEquals("foo/bar", r);

		s = JsonSerializer.create().escapeSolidus().build();
		r = s.serialize("foo/bar");
		assertEquals("\"foo\\/bar\"", r);
		r = JsonParser.DEFAULT.parse(r, String.class);
		assertEquals("foo/bar", r);

		s = JsonSerializer.create().escapeSolidus().build();
		r = s.serialize("foo/bar");
		assertEquals("\"foo\\/bar\"", r);
		r = JsonParser.DEFAULT.parse(r, String.class);
		assertEquals("foo/bar", r);
	}
}