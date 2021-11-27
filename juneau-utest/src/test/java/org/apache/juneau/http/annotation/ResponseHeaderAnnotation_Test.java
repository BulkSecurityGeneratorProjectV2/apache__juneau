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
public class ResponseHeaderAnnotation_Test {

	private static final String CNAME = ResponseHeaderAnnotation_Test.class.getName();

	public static class X1 {}

	//------------------------------------------------------------------------------------------------------------------
	// Basic tests
	//------------------------------------------------------------------------------------------------------------------

	ResponseHeader a1 = ResponseHeaderAnnotation.create()
		.name("name")
		.on("on")
		.onClass(X1.class)
		.serializer(OpenApiSerializer.class)
		.value("value")
		.build();

	ResponseHeader a2 = ResponseHeaderAnnotation.create()
		.name("name")
		.on("on")
		.onClass(X1.class)
		.serializer(OpenApiSerializer.class)
		.value("value")
		.build();

	@Test
	public void a01_basic() {
		assertObject(a1).asJson().matches(""
			+ "{"
				+ "name:'name',"
				+ "on:['on'],"
				+ "onClass:['"+CNAME+"$X1'],"
				+ "schema:{*},"
				+ "serializer:'org.apache.juneau.oapi.OpenApiSerializer',"
				+ "value:'value'"
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
		ResponseHeader c1 = ResponseHeaderAnnotation.create(C1.class).on(C2.class).build();
		ResponseHeader c2 = ResponseHeaderAnnotation.create("a").on("b").build();
		ResponseHeader c4 = ResponseHeaderAnnotation.create().on(C1.class.getMethod("m1")).on(C2.class.getMethod("m2")).build();

		assertObject(c1).asJson().contains("on:['"+CNAME+"$C1','"+CNAME+"$C2']");
		assertObject(c2).asJson().contains("on:['a','b']");
		assertObject(c4).asJson().contains("on:['"+CNAME+"$C1.m1()','"+CNAME+"$C2.m2()']");
	}

	//------------------------------------------------------------------------------------------------------------------
	// Comparison with declared annotations.
	//------------------------------------------------------------------------------------------------------------------

	@ResponseHeader(
		name="name",
		on="on",
		onClass=X1.class,
		serializer=OpenApiSerializer.class,
		value="value"
	)
	public static class D1 {}
	ResponseHeader d1 = D1.class.getAnnotationsByType(ResponseHeader.class)[0];

	@ResponseHeader(
		name="name",
		on="on",
		onClass=X1.class,
		serializer=OpenApiSerializer.class,
		value="value"
	)
	public static class D2 {}
	ResponseHeader d2 = D2.class.getAnnotationsByType(ResponseHeader.class)[0];

	@Test
	public void d01_comparisonWithDeclarativeAnnotations() {
		assertObject(d1).is(d2).is(a1);
		assertInteger(d1.hashCode()).is(d2.hashCode()).is(a1.hashCode()).isNotAny(0,-1);
	}
}
