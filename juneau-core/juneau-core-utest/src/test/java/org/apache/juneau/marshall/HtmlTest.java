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
package org.apache.juneau.marshall;

import static org.apache.juneau.testutils.TestUtils.*;
import static org.junit.Assert.assertEquals;

import java.io.*;
import java.util.*;

import org.junit.*;

public class HtmlTest {

	CharMarshall m = Html.DEFAULT;

	@Test
	public void write1() throws Exception {
		assertEquals("<string>foo</string>", m.write("foo"));
	}

	@Test
	public void write2() throws Exception {
		StringWriter sw = new StringWriter();
		m.write("foo", sw);
		assertEquals("<string>foo</string>", sw.toString());
	}

	@Test
	public void toString1() throws Exception {
		assertEquals("<string>foo</string>", m.toString("foo"));
	}

	@Test
	public void read1() throws Exception {
		String s = m.read("<string>foo</string>", String.class);
		assertEquals("foo", s);
	}

	@Test
	public void read2() throws Exception {
		Map<?,?> o = m.read("<table><tr><td>foo</td><td>bar</td></tr></table>", Map.class, String.class, String.class);
		assertObjectEquals("{foo:'bar'}", o);
	}
}
