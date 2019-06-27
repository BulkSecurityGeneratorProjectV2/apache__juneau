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
package org.apache.juneau;

import static org.apache.juneau.testutils.TestUtils.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.*;

import org.apache.juneau.json.*;
import org.junit.*;

public class IgnoredClassesTest {

	//====================================================================================================
	// testFilesRenderedAsStrings
	//====================================================================================================
	@Test
	public void testFilesRenderedAsStrings() throws Exception {
		assumeTrue(System.getProperty("os.name").toLowerCase().startsWith("win"));
		// Files should be rendered as strings.
		File f = new File("C:/temp");
		assertObjectEquals("'C:\\\\temp'", f);
	}

	//====================================================================================================
	// testIgnorePackages
	//====================================================================================================
	@Test
	public void testIgnorePackages() throws Exception {
		A a = new A();
		JsonSerializerBuilder s = JsonSerializer.create().ssq();
		assertEquals("{f1:'isBean'}", s.build().serialize(a));
		s.notBeanPackages("org.apache.juneau");
		assertEquals("'isNotBean'", s.build().serialize(a));
		s.notBeanPackagesRemove("org.apache.juneau");
		assertEquals("{f1:'isBean'}", s.build().serialize(a));
		s.notBeanPackages("org.apache.juneau.*");
		assertEquals("'isNotBean'", s.build().serialize(a));
		s.notBeanPackagesRemove("org.apache.juneau.*");
		assertEquals("{f1:'isBean'}", s.build().serialize(a));
		s.notBeanPackages("org.apache.juneau.*");
		assertEquals("'isNotBean'", s.build().serialize(a));
		s.notBeanPackagesRemove("org.apache.juneau.*");
		assertEquals("{f1:'isBean'}", s.build().serialize(a));
		s.notBeanPackages("org.apache.juneau");
		assertEquals("'isNotBean'", s.build().serialize(a));
		s.notBeanPackages("org.apache.juneau.x");
		assertEquals("'isNotBean'", s.build().serialize(a));
	}

	public static class A {
		public String f1 = "isBean";
		@Override /* Object */
		public String toString() {
			return "isNotBean";
		}
	}
}
