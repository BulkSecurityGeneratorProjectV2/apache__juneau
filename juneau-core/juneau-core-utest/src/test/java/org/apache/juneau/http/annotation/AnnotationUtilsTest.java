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
package org.apache.juneau.http.annotation;

import static org.junit.Assert.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import static org.apache.juneau.http.annotation.AnnotationUtils.*;

import org.apache.juneau.jsonschema.annotation.ExternalDocs;
import org.apache.juneau.jsonschema.annotation.Schema;
import org.apache.juneau.jsonschema.annotation.SubItems;
import org.apache.juneau.jsonschema.annotation.Items;
import java.lang.annotation.*;
import java.lang.reflect.*;

import org.junit.*;
import org.junit.runners.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AnnotationUtilsTest {

	//-----------------------------------------------------------------------------------------------------------------
	// Test empty checks
	//-----------------------------------------------------------------------------------------------------------------

	@Target(TYPE)
	@Retention(RUNTIME)
	public @interface X {
		Contact x1() default @Contact;
		ExternalDocs x2() default @ExternalDocs;
		License x3() default @License;
		Schema x4() default @Schema;
		SubItems x5() default @SubItems;
		Items x6() default @Items;
	}

	@Body
	@Response
	@ResponseHeader
	@X
	public static class A01 {
		@Query @Header @FormData @Path @Schema
		public int f1;
	}

	@Test
	public void testEmpty() throws Exception {
		assertTrue(empty(A01.class.getAnnotation(Body.class)));
		assertTrue(empty(A01.class.getAnnotation(Response.class)));
		assertTrue(empty(A01.class.getAnnotation(ResponseHeader.class)));

		X x = A01.class.getAnnotation(X.class);
		assertTrue(empty(x.x1()));
		assertTrue(empty(x.x2()));
		assertTrue(empty(x.x3()));
		assertTrue(empty(x.x4()));
		assertTrue(empty(x.x5()));
		assertTrue(empty(x.x6()));

		Field f = A01.class.getField("f1");
		assertTrue(empty(f.getAnnotation(Query.class)));
		assertTrue(empty(f.getAnnotation(Header.class)));
		assertTrue(empty(f.getAnnotation(FormData.class)));
		assertTrue(empty(f.getAnnotation(Path.class)));
	}

	public static class B01 {
		public int f1;
	}

	@Test
	public void testEmptyNonExistent() throws Exception {
		assertTrue(empty(B01.class.getAnnotation(Body.class)));
		assertTrue(empty(B01.class.getAnnotation(Response.class)));
		assertTrue(empty(B01.class.getAnnotation(ResponseHeader.class)));

		assertTrue(empty((Contact)null));
		assertTrue(empty((ExternalDocs)null));
		assertTrue(empty((License)null));
		assertTrue(empty((Schema)null));
		assertTrue(empty((Items)null));
		assertTrue(empty((SubItems)null));

		Field f = B01.class.getField("f1");
		assertTrue(empty(f.getAnnotation(Query.class)));
		assertTrue(empty(f.getAnnotation(Header.class)));
		assertTrue(empty(f.getAnnotation(FormData.class)));
		assertTrue(empty(f.getAnnotation(Path.class)));
	}

	@Test
	public void testAllEmpty1() {
		assertTrue(allEmpty(new String[0]));
		assertTrue(allEmpty(""));
		assertTrue(allEmpty(null,""));
		assertFalse(allEmpty(null,"","x"));
	}

	@Test
	public void testAllEmpty2() {
		assertTrue(allEmpty(new String[0],new String[0]));
		assertTrue(allEmpty(null,new String[0]));
		assertFalse(allEmpty(null,new String[]{""}));
		assertFalse(allEmpty(null,new String[]{"x"}));
	}

	@Test
	public void testAllFalse() {
		assertTrue(allFalse());
		assertTrue(allFalse(false));
		assertTrue(allFalse(false,false));
		assertFalse(allFalse(false,true));
		assertFalse(allFalse(true));
	}

	@Test
	public void testAllMinusOne() {
		assertTrue(allMinusOne());
		assertTrue(allMinusOne(-1));
		assertTrue(allMinusOne(-1,-1));
		assertFalse(allMinusOne(-1,0));
		assertFalse(allMinusOne(0));
	}

	@Test
	public void testAllMinusOneLongs() {
		assertTrue(allMinusOne(-1l));
		assertTrue(allMinusOne(-1l,-1l));
		assertFalse(allMinusOne(-1l,0l));
		assertFalse(allMinusOne(0l));
	}
}