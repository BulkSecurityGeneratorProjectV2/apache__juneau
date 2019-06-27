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

import java.util.*;

import org.apache.juneau.http.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(Parameterized.class)
public class RootContentTest extends ContentComboTestBase {

	@Parameterized.Parameters
	public static Collection<Object[]> getParameters() {
		return Arrays.asList(new Object[][] {
			{ 	/* 0 */
				new ComboInput("HTML-stylesheet", "/", MediaType.HTML,
					"@import '/htdocs/themes/devops.css';",
					".menu-item {"
				)
			},
			{ 	/* 1 */
				new ComboInput("HTML-stylesheet-contnt", "/htdocs/themes/devops.css", MediaType.PLAIN,
					"/** DevOps look-and-feel */"
				)
			},
			{ 	/* 2 */
				new ComboInput("HTML-header", "/", MediaType.HTML,
					"<head>",
					"<h1>Root resources</h1>",
					"<h2>Navigation page</h2>",
					"<img src='/htdocs/images/juneau.png' style='position:absolute;top:5;right:5;background-color:transparent;height:30px'/>"
				)
			},
			{ 	/* 3 */
				new ComboInput("HTML-nav", "/", MediaType.HTML,
					"<nav>",
					"<a href='/?method=OPTIONS'>options</a>",
					"<a onclick='menuClick(this);'>content-type</a>",
					"<a onclick='menuClick(this);'>themes</a>",
					"<a href='https://github.com/apache/juneau/blob/master/juneau-examples/juneau-examples-rest/src/main/java/org/apache/juneau/examples/rest/RootResources.java'>source</a>"
				)
			},
			{ 	/* 4 */
				new ComboInput("HTML-nav-popup-contentType", "/", MediaType.HTML,
					"<div class='popup-content'>",
					"/?plainText=true&Accept=application%2Fjson"
				)
			},
			{ 	/* 5 */
				new ComboInput("HTML-nav-popup-styles", "/", MediaType.HTML,
					"/?stylesheet=htdocs%2Fthemes%2Fdevops.css",
					"/?stylesheet=htdocs%2Fthemes%2Flight.css",
					"/?stylesheet=htdocs%2Fthemes%2Foriginal.css",
					"/?stylesheet=htdocs%2Fthemes%2Fdark.css"
				)
			},
			{ 	/* 6 */
				new ComboInput("HTML-aside", "/", MediaType.HTML,
					"<aside>",
					"<p>This is an example of a 'router' page that serves as a jumping-off point to child resources.</p>",
					"<p>Other features (such as this aside) are added through annotations.</p>"
				)
			},
			{ 	/* 7 */
				new ComboInput("HTML-footer", "/", MediaType.HTML,
					"<footer>",
					"<img style='float:right;padding-right:20px;height:32px' src='/htdocs/images/asf.png'>"
				)
			},
			{ 	/* 8 */
				new ComboInput("HTML-content-text/html", "/", MediaType.HTML,
					"<a href='/helloWorld'>helloWorld</a>",
					"<td>Hello World</td>"
				)
			},
			{ 	/* 9 */
				new ComboInput("HTML-content-application/json", "/", MediaType.JSON,
					"'name':'helloWorld',",
					"'description':'Hello World'"
				)
			},
			{ 	/* 9 */
				new ComboInput("HTML-content-octal/msgpack", "/", MediaType.MSGPACK,
					"",
					""
				)
			},
			{ 	/* 9 */
				new ComboInput("HTML-content-text/plain", "/", MediaType.PLAIN,
					"",
					""
				)
			},
			{ 	/* 9 */
				new ComboInput("HTML-content-text/uon", "/", MediaType.UON,
					"",
					""
				)
			},
			{ 	/* 9 */
				new ComboInput("HTML-content-application/x-www-form-urlencoded", "/", MediaType.URLENCODING,
					"",
					""
				)
			},
			{ 	/* 9 */
				new ComboInput("HTML-content-text/xml", "/", MediaType.XML,
					"",
					""
				)
			},
			{ 	/* 9 */
				new ComboInput("HTML-content-text/xml+rdf", "/", MediaType.RDF,
					"",
					""
				)
			},
			{ 	/* 9 */
				new ComboInput("HTML-content-text/n-triple", "/", MediaType.NTRIPLE,
					"",
					""
				)
			},
			{ 	/* 9 */
				new ComboInput("HTML-content-text/turtle", "/", MediaType.TURTLE,
					"",
					""
				)
			},
			{ 	/* 9 */
				new ComboInput("HTML-content-text/n3", "/", MediaType.N3,
					"",
					""
				)
			}

		});
	}


	public RootContentTest(ComboInput comboInput) {
		super(comboInput);
	}
}
