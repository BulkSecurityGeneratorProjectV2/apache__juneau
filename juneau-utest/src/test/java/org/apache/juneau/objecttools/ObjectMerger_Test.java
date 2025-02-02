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
package org.apache.juneau.objecttools;

import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

import org.junit.*;

/**
 * Test the PojoMerge class.
 */
@FixMethodOrder(NAME_ASCENDING)
public class ObjectMerger_Test {

	//====================================================================================================
	// Basic tests
	//====================================================================================================
	@Test
	public void a01_basic() throws Exception {
		IA a1, a2, am;

		a1 = new A("1"); a2 = new A("2");
		am = ObjectMerger.merge(IA.class, a1, a2);
		assertEquals("1", am.getA());
		am.setA("x");
		assertEquals("x", am.getA());
		assertEquals("x", a1.getA());
		assertEquals("2", a2.getA());

		a1 = new A("1"); a2 = new A("2");
		am = ObjectMerger.merge(IA.class, true, a1, a2);
		assertEquals("1", am.getA());
		am.setA("x");
		assertEquals("x", am.getA());
		assertEquals("x", a1.getA());
		assertEquals("x", a2.getA());

		a1 = new A(null); a2 = new A("2");
		am = ObjectMerger.merge(IA.class, a1, a2);
		assertEquals("2", am.getA());
		am.setA("x");
		assertEquals("x", am.getA());
		assertEquals("x", a1.getA());
		assertEquals("2", a2.getA());

		a1 = new A(null); a2 = new A(null);
		am = ObjectMerger.merge(IA.class, a1, a2);
		assertEquals(null, am.getA());

		a1 = new A(null); a2 = new A("2");
		am = ObjectMerger.merge(IA.class, null, a1, null, null, a2, null);
		assertEquals("2", am.getA());
	}

	public static interface IA {
		String getA();
		void setA(String a);
	}

	public static class A implements IA {
		private String a;

		public A(String a) {
			this.a = a;
		}

		@Override
		public String getA() {
			return a;
		}

		@Override
		public void setA(String a) {
			this.a = a;
		}
	}
}