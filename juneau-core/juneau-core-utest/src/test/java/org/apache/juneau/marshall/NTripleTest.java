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

import org.junit.*;

public class NTripleTest {

	CharMarshall m = NTriple.DEFAULT;

	String r = "_:xxx <http://www.apache.org/juneau/value> \"foo\" .";

	@Test
	public void write1() throws Exception {
		assertContains(m.write("foo"), "<http://www.apache.org/juneau/value> \"foo\"");
	}

	@Test
	public void write2() throws Exception {
		StringWriter sw = new StringWriter();
		m.write("foo", sw);
		assertContains(sw.toString(), "<http://www.apache.org/juneau/value> \"foo\"");
	}

	@Test
	public void toString1() throws Exception {
		assertContains(m.toString("foo"), "<http://www.apache.org/juneau/value> \"foo\"");
	}

	@Test
	public void read1() throws Exception {
		String s = m.read(r, String.class);
		assertEquals("foo", s);
	}
}
