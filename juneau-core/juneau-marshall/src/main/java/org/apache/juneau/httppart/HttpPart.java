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
package org.apache.juneau.httppart;

import org.apache.juneau.parser.*;
import org.apache.juneau.serializer.*;

/**
 * Represents an instance of an HTTP part.
 *
 * <p>
 * Can be used to represent both request and response parts.
 */
public class HttpPart {
	private final String name;
	private final Object opart;
	private final String spart;
	private final HttpPartType partType;
	private final HttpPartSchema schema;
	private final HttpPartSerializerSession serializer;
	private final HttpPartParserSession parser;

	/**
	 * Constructor.
	 *
	 * <p>
	 * Used when the part is in POJO form and needs to be converted to a String.
	 *
	 * @param name The HTTP part name (e.g. the header name).
	 * @param partType The HTTP part type.
	 * @param schema Schema information about the part.
	 * @param serializer The part serializer to use to serialize the part.
	 * @param part The part POJO being serialized.
	 */
	public HttpPart(String name, HttpPartType partType, HttpPartSchema schema, HttpPartSerializerSession serializer, Object part) {
		this.name = name;
		this.partType = partType;
		this.schema = schema;
		this.serializer = serializer;
		this.opart = part;
		this.spart = null;
		this.parser = null;
	}

	/**
	 * Constructor.
	 *
	 * <p>
	 * Used when the part is in String form and needs to be converted to a POJO.
	 *
	 * @param name The HTTP part name (e.g. the header name).
	 * @param partType The HTTP part type.
	 * @param schema Schema information about the part.
	 * @param parser The part parser to use to parse the part.
	 * @param part The part string being parsed.
	 */
	public HttpPart(String name, HttpPartType partType, HttpPartSchema schema, HttpPartParserSession parser, String part) {
		this.name = name;
		this.partType = partType;
		this.schema = schema;
		this.parser = parser;
		this.spart = part;
		this.serializer = null;
		this.opart = null;
	}

	/**
	 * Returns the name of the part.
	 *
	 * @return The name of the part.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value of the part converted to a string.
	 *
	 * @return The value of the part converted to a string.
	 * @throws SchemaValidationException HTTP part failed schema validation.
	 * @throws SerializeException HTTP part could not be serialized.
	 */
	public String asString() throws SchemaValidationException, SerializeException {
		if (spart != null)
			return spart;
		return serializer.serialize(partType, schema, opart);
	}

	/**
	 * Returns the value of the part converted to a string.
	 *
	 * @param c The type to convert to.
	 * @return The value of the part converted to a string.
	 * @throws SchemaValidationException HTTP part failed schema validation.
	 * @throws ParseException Malformed input encountered.
	 */
	public <T> T asType(Class<T> c) throws SchemaValidationException, ParseException {
		return parser.parse(partType, schema, spart, c);
	}
}
