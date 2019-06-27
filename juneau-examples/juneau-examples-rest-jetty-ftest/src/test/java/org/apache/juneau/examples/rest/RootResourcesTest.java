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
package org.apache.juneau.examples.rest;

import static org.junit.Assert.*;

import org.apache.juneau.*;
import org.apache.juneau.dto.swagger.*;
import org.apache.juneau.html.*;
import org.apache.juneau.json.*;
import org.apache.juneau.rest.client.*;
import org.apache.juneau.rest.helper.*;
import org.junit.*;

public class RootResourcesTest extends RestTestcase {

	private static boolean debug = false;

	private RestClient jsonClient = SamplesMicroservice.DEFAULT_CLIENT;


	//====================================================================================================
	// text/json
	//====================================================================================================
	@Test
	public void testJson() throws Exception {
		RestClient client = SamplesMicroservice.DEFAULT_CLIENT;

		try (RestCall r = client.doGet("")) {
			ResourceDescription[] x = r.getResponse(ResourceDescription[].class);
			assertEquals("helloWorld", x[0].getName());
			assertEquals("Hello World", x[0].getDescription());
		}

		try (RestCall r = jsonClient.doOptions("")) {
			ObjectMap x2 = r.getResponse(ObjectMap.class);
			String s = x2.getObjectMap("info").getString("description");
			if (debug) System.err.println(s);
			assertTrue(s, s.startsWith("Example of a router resource page"));
		}
	}

	//====================================================================================================
	// text/xml
	//====================================================================================================
	@Test
	public void testXml() throws Exception {
		try (RestClient client = SamplesMicroservice.client().xml().build()) {

			try (RestCall r = client.doGet("")) {
				ResourceDescription[] x = r.getResponse(ResourceDescription[].class);
				assertEquals("helloWorld", x[0].getName());
				assertEquals("Hello World", x[0].getDescription());
			}

			try (RestCall r = jsonClient.doOptions("")) {
				ObjectMap x2 = r.getResponse(ObjectMap.class);
				String s = x2.getObjectMap("info").getString("description");
				if (debug) System.err.println(s);
				assertTrue(s, s.startsWith("Example of a router resource page"));
			}
		}
	}

	//====================================================================================================
	// text/html+stripped
	//====================================================================================================
	@Test
	public void testHtmlStripped() throws Exception {
		try (RestClient client = SamplesMicroservice.client().parser(HtmlParser.DEFAULT).accept("text/html+stripped").build()) {

			try (RestCall r = client.doGet("")) {
				ResourceDescription[] x = r.getResponse(ResourceDescription[].class);
				assertEquals("helloWorld", x[0].getName());
				assertEquals("Hello World", x[0].getDescription());
			}

			try (RestCall r = jsonClient.doOptions("").accept("text/json")) {
				ObjectMap x2 = r.getResponse(ObjectMap.class);
				String s = x2.getObjectMap("info").getString("description");
				if (debug) System.err.println(s);
				assertTrue(s, s.startsWith("Example of a router resource page"));
			}
		}
	}

	//====================================================================================================
	// application/json+schema
	//====================================================================================================
	@Test
	public void testJsonSchema() throws Exception {
		try (RestClient client = SamplesMicroservice.client().parser(JsonParser.DEFAULT).accept("text/json+schema").build()) {
			try (RestCall r = client.doGet("")) {
				ObjectMap m = r.getResponse(ObjectMap.class);
				if (debug) System.err.println(m);
				client.closeQuietly();
			}
		}
	}

	//====================================================================================================
	// OPTIONS page
	//====================================================================================================
	@Test
	public void testOptionsPage() throws Exception {
		try (RestCall r = jsonClient.doOptions("")) {
			Swagger o = r.getResponse(Swagger.class);
			if (debug) System.err.println(o);
			assertEquals("Example of a router resource page.", o.getInfo().getDescription());
		}
	}
}
