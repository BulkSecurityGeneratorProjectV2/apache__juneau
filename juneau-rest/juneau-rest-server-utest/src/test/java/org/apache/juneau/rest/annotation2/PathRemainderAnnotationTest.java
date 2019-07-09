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
package org.apache.juneau.rest.annotation2;

import static org.apache.juneau.http.HttpMethodName.*;

import org.apache.juneau.http.annotation.Path;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.rest.mock2.*;
import org.junit.*;
import org.junit.runners.*;

/**
 * Tests related to @PathREmainder annotation.
 */
@SuppressWarnings({})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PathRemainderAnnotationTest {

	//=================================================================================================================
	// Simple tests
	//=================================================================================================================

	@RestResource
	public static class A  {
		@RestMethod(name=GET, path="/*")
		public String b(@Path("/*") String remainder) {
			return remainder;
		}
	}
	static MockRest a = MockRest.build(A.class, null);

	@Test
	public void a01_withoutRemainder() throws Exception {
		a.get("/").execute().assertBody("");
	}
	@Test
	public void a02_withRemainder() throws Exception {
		a.get("/foo").execute().assertBody("foo");
	}
	@Test
	public void a03_withRemainder2() throws Exception {
		a.get("/foo/bar").execute().assertBody("foo/bar");
	}
}
