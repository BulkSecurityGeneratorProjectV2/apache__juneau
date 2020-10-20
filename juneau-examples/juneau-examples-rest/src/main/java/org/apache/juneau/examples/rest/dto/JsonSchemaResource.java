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
package org.apache.juneau.examples.rest.dto;

import org.apache.juneau.annotation.*;
import org.apache.juneau.jsonschema.annotation.ExternalDocs;
import org.apache.juneau.dto.jsonschema.*;
import org.apache.juneau.html.annotation.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.http.annotation.Body;
import org.apache.juneau.rest.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.rest.widget.*;

/**
 * Sample resource that shows how to serialize JSON-Schema documents.
 *
 * <ul class='seealso'>
 * 	<li class='extlink'>{@source}
 * </ul>
 */
@Rest(
	path="/jsonSchema",
	messages="nls/JsonSchemaResource",
	title="Sample JSON-Schema document",
	description="Sample resource that shows how to generate JSON-Schema documents",
	swagger=@ResourceSwagger(
		contact=@Contact(name="Juneau Developer",email="dev@juneau.apache.org"),
		license=@License(name="Apache 2.0",url="http://www.apache.org/licenses/LICENSE-2.0.html"),
		version="2.0",
		termsOfService="You are on your own.",
		externalDocs=@ExternalDocs(description="Apache Juneau",url="http://juneau.apache.org")
	)
)
@HtmlDocConfig(
	widgets={
		ContentTypeMenuItem.class,
		ThemeMenuItem.class
	},
	navlinks={
		"up: request:/..",
		"options: servlet:/?method=OPTIONS",
		"stats: servlet:/stats",
		"$W{ContentTypeMenuItem}",
		"$W{ThemeMenuItem}",
		"source: $C{Source/gitHub}/org/apache/juneau/examples/rest/dto/$R{servletClassSimple}.java"
	},
	aside={
		"<div style='min-width:200px' class='text'>",
		"	<p>Shows how to produce JSON-Schema documents in a variety of languages using the JSON-Schema DTOs.</p>",
		"</div>"
	}
)
@BeanConfig(
	applyMarshalled=@Marshalled(on="Schema",example="$F{JsonSchemaResource_example.json}")
)
public class JsonSchemaResource extends BasicRestServletJena {
	private static final long serialVersionUID = 1L;

	private JsonSchema schema;     // The schema document

	@Override /* Servlet */
	public void init() {

		try {
			schema = new JsonSchema()
				.setId("http://example.com/sample-schema#")
				.setSchemaVersionUri("http://json-schema.org/draft-04/schema#")
				.setTitle("Example Schema")
				.setType(JsonType.OBJECT)
				.addProperties(
					new JsonSchemaProperty("firstName", JsonType.STRING),
					new JsonSchemaProperty("lastName", JsonType.STRING),
					new JsonSchemaProperty("age", JsonType.INTEGER)
						.setDescription("Age in years")
						.setMinimum(0)
				)
				.addRequired("firstName", "lastName");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the JSON-Schema document.
	 *
	 * @return The JSON-Schema document.
	 */
	@RestMethod(
		summary="Get the JSON-Schema document"
	)
	public JsonSchema get() {
		return schema;
	}

	/**
	 * Overwrite the JSON-Schema document
	 *
	 * @param schema The new schema document.
	 * @return The updated schema document.
	 */
	@RestMethod(
		summary="Overwrite the JSON-Schema document",
		description="Replaces the schema document with the specified content, and then mirrors it as the response."
	)
	public JsonSchema put(@Body JsonSchema schema) {
		this.schema = schema;
		return schema;
	}
}
