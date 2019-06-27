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
package org.apache.juneau.utils;

import static org.junit.Assert.*;

import org.apache.juneau.internal.*;
import org.junit.*;

@SuppressWarnings({"resource"})
public class StringBuilderWriterTest {

	//====================================================================================================
	// Basic tests
	//====================================================================================================
	@Test
	public void test() throws Exception {
		StringBuilderWriter sbw = new StringBuilderWriter();
		sbw.write("abc");
		assertEquals("abc", sbw.toString());
		sbw.append("abc");
		assertEquals("abcabc", sbw.toString());
		sbw.write("abc", 1, 1);
		assertEquals("abcabcb", sbw.toString());
		sbw.append("abc", 1, 2);
		assertEquals("abcabcbb", sbw.toString());
		sbw.write((String)null);
		assertEquals("abcabcbbnull", sbw.toString());
		sbw.append((String)null);
		assertEquals("abcabcbbnullnull", sbw.toString());
		sbw.append((String)null,0,4);
		assertEquals("abcabcbbnullnullnull", sbw.toString());

		char[] buff = "abc".toCharArray();
		sbw = new StringBuilderWriter();
		sbw.write(buff, 0, buff.length);
		assertEquals("abc", sbw.toString());
		sbw.write(buff, 0, 0);
		assertEquals("abc", sbw.toString());

		try { sbw.write(buff, -1, buff.length); fail(); } catch (IndexOutOfBoundsException e) {}
		try { sbw.write(buff, buff.length+1, 0); fail(); } catch (IndexOutOfBoundsException e) {}
		try { sbw.write(buff, buff.length-1, 2); fail(); } catch (IndexOutOfBoundsException e) {}
		try { sbw.write(buff, 0, buff.length+1); fail(); } catch (IndexOutOfBoundsException e) {}
		try { sbw.write(buff, 0, -1); fail(); } catch (IndexOutOfBoundsException e) {}

		sbw.flush();
		sbw.close();
	}
}
