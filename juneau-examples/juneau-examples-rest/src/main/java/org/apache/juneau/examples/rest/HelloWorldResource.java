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

import org.apache.juneau.html.annotation.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.rest.servlet.*;

/**
 * Sample REST resource that prints out a simple "Hello world!" message.
 *
 * <ul class='seealso'>
 * </ul>
 */
@Rest(
	title="Hello World",
	description="An example of the simplest-possible resource",
	path="/helloWorld"
)
@HtmlDocConfig(
	aside={
		"<div style='max-width:400px' class='text'>",
		"	<p>This page shows a resource that simply response with a 'Hello world!' message</p>",
		"	<p>The POJO serialized is a simple String.</p>",
		"</div>"
	}
)
public class HelloWorldResource extends BasicRestObject {

	/**
	 * [HTTP GET /helloworld]
	 * Returns a simple string message.
	 *
	 * @return A simple Hello-World message.
	 */
	@RestGet(path="/*", summary="Responds with \"Hello world!\"")
	public String sayHello() {
		return "Hello world!";
	}
}
