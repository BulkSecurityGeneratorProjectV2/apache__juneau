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
package org.apache.juneau.rest.httppart;

import org.apache.juneau.httppart.*;

/**
 * Represents the information needed to serialize a response part such as a response header or content.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../../overview-summary.html#juneau-rest-server.jrs.HttpParts">HTTP Parts</a>
 * </ul>
 */
public class ResponsePartMeta {

	/**
	 * Represents a non-existent meta.
	 */
	public static final ResponsePartMeta NULL = new ResponsePartMeta(null, null, null);

	private final HttpPartType partType;
	private final HttpPartSchema schema;
	private final HttpPartSerializer serializer;

	/**
	 * Constructor.
	 *
	 * @param partType The part type.
	 * @param schema The part schema.
	 * @param serializer The serializer to use to serialize the part.
	 */
	public ResponsePartMeta(HttpPartType partType, HttpPartSchema schema, HttpPartSerializer serializer) {
		this.partType = partType;
		this.schema = schema;
		this.serializer = serializer;
	}

	/**
	 * Returns the part type.
	 *
	 * @return The part type.
	 */
	public HttpPartType getPartType() {
		return partType;
	}

	/**
	 * Returns the part schema.
	 *
	 * @return The part schema.
	 */
	public HttpPartSchema getSchema() {
		return schema;
	}

	/**
	 * Returns the part serializer.
	 *
	 * @return The part serializer.
	 */
	public HttpPartSerializer getSerializer() {
		return serializer;
	}
}
