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
package org.apache.juneau.a.rttests;

import static org.apache.juneau.testutils.TestUtils.*;
import static org.junit.Assert.*;

import org.apache.juneau.parser.*;
import org.apache.juneau.serializer.*;
import org.apache.juneau.utils.*;
import org.junit.*;

/**
 * Tests designed to serialize and parse objects to make sure we end up
 * with the same objects for all serializers and parsers.
 */
public class RoundTripClassesTest extends RoundTripTest {

	public RoundTripClassesTest(String label, SerializerBuilder s, ParserBuilder p, int flags) throws Exception {
		super(label, s, p, flags);
	}

	@Test
	public void classObjects() throws Exception {
		Object o = String.class;
		o = roundTrip(o);
		assertTrue(o == String.class);

		o = new Class[]{String.class};
		o = roundTrip(o);
		assertObjectEquals("['java.lang.String']", o);

		o = new AList<Class<?>>().append(String.class).append(Integer.class);
		o = roundTrip(o);
		assertObjectEquals("['java.lang.String','java.lang.Integer']", o);

		o = new AMap<Class<?>,Class<?>>().append(String.class,String.class);
		o = roundTrip(o);
		assertObjectEquals("{'java.lang.String':'java.lang.String'}", o);
	}
}
