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

import static org.apache.juneau.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

import org.apache.juneau.*;
import org.apache.juneau.oapi.*;
import org.junit.*;

@FixMethodOrder(NAME_ASCENDING)
public class ResponseAnnotation_Test {

	private static final String CNAME = ResponseAnnotation_Test.class.getName();

	public static class X1 {}

	//------------------------------------------------------------------------------------------------------------------
	// Basic tests
	//------------------------------------------------------------------------------------------------------------------

	Response a1 = ResponseAnnotation.create()
		.examples("examples")
		.headers(HeaderAnnotation.DEFAULT)
		.on("on")
		.onClass(X1.class)
		.parser(OpenApiParser.class)
		.schema(SchemaAnnotation.DEFAULT)
		.serializer(OpenApiSerializer.class)
		.build();

	Response a2 = ResponseAnnotation.create()
		.examples("examples")
		.headers(HeaderAnnotation.DEFAULT)
		.on("on")
		.onClass(X1.class)
		.parser(OpenApiParser.class)
		.schema(SchemaAnnotation.DEFAULT)
		.serializer(OpenApiSerializer.class)
		.build();

	@Test
	public void a01_basic() {
		assertObject(a1).asJson().isMatches(""
			+ "{"
				+ "examples:['examples'],"
				+ "headers:[*],"
				+ "on:['on'],"
				+ "onClass:['"+CNAME+"$X1'],"
				+ "parser:'org.apache.juneau.oapi.OpenApiParser',"
				+ "schema:{*,"
				+ "serializer:'org.apache.juneau.oapi.OpenApiSerializer'"
			+ "}"
		);
	}

	@Test
	public void a02_testEquivalency() {
		assertObject(a1).is(a2);
		assertInteger(a1.hashCode()).is(a2.hashCode()).isNotAny(0,-1);
	}

	//------------------------------------------------------------------------------------------------------------------
	// PropertyStore equivalency.
	//------------------------------------------------------------------------------------------------------------------

	@Test
	public void b01_testEquivalencyInPropertyStores() {
		BeanContext bc1 = BeanContext.create().annotations(a1).build();
		BeanContext bc2 = BeanContext.create().annotations(a2).build();
		assertTrue(bc1 == bc2);
	}

	//------------------------------------------------------------------------------------------------------------------
	// Other methods.
	//------------------------------------------------------------------------------------------------------------------

	public static class C1 {
		public int f1;
		public void m1() {}
	}
	public static class C2 {
		public int f2;
		public void m2() {}
	}

	@Test
	public void c01_otherMethods() throws Exception {
		Response c1 = ResponseAnnotation.create(C1.class).on(C2.class).build();
		Response c2 = ResponseAnnotation.create("a").on("b").build();
		Response c4 = ResponseAnnotation.create().on(C1.class.getMethod("m1")).on(C2.class.getMethod("m2")).build();

		assertObject(c1).asJson().isContains("on:['"+CNAME+"$C1','"+CNAME+"$C2']");
		assertObject(c2).asJson().isContains("on:['a','b']");
		assertObject(c4).asJson().isContains("on:['"+CNAME+"$C1.m1()','"+CNAME+"$C2.m2()']");
	}

	//------------------------------------------------------------------------------------------------------------------
	// Comparison with declared annotations.
	//------------------------------------------------------------------------------------------------------------------

	@Response(
		examples="examples",
		headers=@Header,
		on="on",
		onClass=X1.class,
		parser=OpenApiParser.class,
		schema=@Schema,
		serializer=OpenApiSerializer.class
	)
	public static class D1 {}
	Response d1 = D1.class.getAnnotationsByType(Response.class)[0];

	@Response(
		examples="examples",
		headers=@Header,
		on="on",
		onClass=X1.class,
		parser=OpenApiParser.class,
		schema=@Schema,
		serializer=OpenApiSerializer.class
	)
	public static class D2 {}
	Response d2 = D2.class.getAnnotationsByType(Response.class)[0];

	@Test
	public void d01_comparisonWithDeclarativeAnnotations() {
		assertObject(d1).is(d2).is(a1);
		assertInteger(d1.hashCode()).is(d2.hashCode()).is(a1.hashCode()).isNotAny(0,-1);
	}
}
