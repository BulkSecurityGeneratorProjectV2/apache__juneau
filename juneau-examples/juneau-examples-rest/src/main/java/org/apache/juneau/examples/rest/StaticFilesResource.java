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

import static org.apache.juneau.http.HttpMethodName.*;

import org.apache.juneau.dto.*;
import org.apache.juneau.dto.swagger.*;
import org.apache.juneau.dto.swagger.ui.*;
import org.apache.juneau.http.*;
import org.apache.juneau.microservice.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.rest.widget.*;

/**
 * Sample resource that shows how to generate ATOM feeds.
 */
@RestResource(
	path="/staticFiles",
	title="SwaggerUI testbed",
	description="Sample resource that shows how to use static files.",
	htmldoc=@HtmlDoc(
		widgets={
			ContentTypeMenuItem.class,
			StyleMenuItem.class
		},
		navlinks={
			"up: request:/..",
			"options: servlet:/?method=OPTIONS",
			"$W{ContentTypeMenuItem}",
			"$W{StyleMenuItem}",
			"source: $C{Source/gitHub}/org/apache/juneau/examples/rest/$R{staticFilesResource}.java"
		}
	),
	staticFiles= {
		// Serve up files in /files under the child URI /static
		"static:files"
	},
	swagger={
		"info: {",
			"contact:{name:'Juneau Developer',email:'dev@juneau.apache.org'},",
			"license:{name:'Apache 2.0',url:'http://www.apache.org/licenses/LICENSE-2.0.html'},",
			"version:'2.0',",
			"termsOfService:'You are on your own.'",
		"},",
		"externalDocs:{description:'Apache Juneau',url:'http://juneau.apache.org'}"
	}
)
public class StaticFilesResource extends BasicRestServletJena {
	private static final long serialVersionUID = 1L;

	/**
	 * GET request handler
	 */
	@RestMethod(name=GET, path="/", summary="Get the sample ATOM feed")
	public LinkString[] getFiles() throws Exception {
		return new LinkString[] {
			new LinkString("petstore.html","static/petstore.html")
		};
	}
	
	@RestMethod(name=GET, path="/swagger", summary="Normal")
	public Swagger testSwagger() throws Exception {
		Swagger s = getContext().getClasspathResource(Swagger.class, MediaType.JSON, "files/petstore.json", null);
		return s;
	}
	
	@RestMethod(name=GET, path="/swagger2", summary="SwaggerUI", pojoSwaps=SwaggerUI.class)
	public Swagger testSwagger2() throws Exception {
		Swagger s = getContext().getClasspathResource(Swagger.class, MediaType.JSON, "files/petstore.json", null);
		return s;
	}
}
