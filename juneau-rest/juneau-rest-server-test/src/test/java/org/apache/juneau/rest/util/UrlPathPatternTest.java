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
package org.apache.juneau.rest.util;

import static org.junit.Assert.*;

import java.util.*;

import org.apache.juneau.json.*;
import org.junit.*;
import org.junit.runners.*;

/**
 * Validates the functionality of the UrlPathPattern class.
 */
@SuppressWarnings({"javadoc"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UrlPathPatternTest {
	@Test
	public void testComparison() throws Exception {
		List<UrlPathPattern> l = new LinkedList<>();

		l.add(new UrlPathPattern("/foo"));
		l.add(new UrlPathPattern("/foo/*"));
		l.add(new UrlPathPattern("/foo/bar"));
		l.add(new UrlPathPattern("/foo/bar/*"));
		l.add(new UrlPathPattern("/foo/{id}"));
		l.add(new UrlPathPattern("/foo/{id}/*"));
		l.add(new UrlPathPattern("/foo/{id}/bar"));
		l.add(new UrlPathPattern("/foo/{id}/bar/*"));

		Collections.sort(l);
		assertEquals("[{patternString:'/foo/bar',vars:[]},{patternString:'/foo/bar/*',vars:[]},{patternString:'/foo/{id}/bar',vars:['id']},{patternString:'/foo/{id}/bar/*',vars:['id']},{patternString:'/foo/{id}',vars:['id']},{patternString:'/foo/{id}/*',vars:['id']},{patternString:'/foo',vars:[]},{patternString:'/foo/*',vars:[]}]", SimpleJsonSerializer.DEFAULT.builder().sortProperties().build().serialize(l));
	}
}
