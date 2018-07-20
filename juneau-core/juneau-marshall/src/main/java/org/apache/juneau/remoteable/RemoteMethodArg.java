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
package org.apache.juneau.remoteable;

import static org.apache.juneau.internal.ClassUtils.*;
import static org.apache.juneau.internal.ReflectionUtils.*;
import static org.apache.juneau.httppart.HttpPartType.*;

import java.lang.reflect.*;

import org.apache.juneau.http.annotation.*;
import org.apache.juneau.httppart.*;
import org.apache.juneau.internal.*;

/**
 * Represents the metadata about an annotated argument of a method on a remote proxy interface.
 *
 * <h5 class='section'>See Also:</h5>
 * <ul class='doctree'>
 * 	<li class='link'><a class='doclink' href='../../../../overview-summary.html#juneau-rest-client.3rdPartyProxies'>Overview &gt; juneau-rest-client &gt; Interface Proxies Against 3rd-party REST Interfaces</a>
 * </ul>
 */
public final class RemoteMethodArg {

	private final int index;
	private final HttpPartType partType;
	private final HttpPartSerializer serializer;
	private final HttpPartSchema schema;
	private final String name;
	private final boolean skipIfEmpty;

	RemoteMethodArg(int index, HttpPartType partType, HttpPartSchema schema) {
		this.index = index;
		this.partType = partType;
		this.serializer = newInstance(HttpPartSerializer.class, schema == null ? null : schema.getSerializer());
		this.schema = schema;
		this.name = schema == null ? null : schema.getName();
		this.skipIfEmpty = schema == null ? false : schema.isSkipIfEmpty();
	}

	RemoteMethodArg(HttpPartType partType, HttpPartSchema schema, String defaultName) {
		this.index = -1;
		this.partType = partType;
		this.serializer = newInstance(HttpPartSerializer.class, schema == null ? null : schema.getSerializer());
		this.schema = schema;
		this.name = StringUtils.firstNonEmpty(schema == null ? null : schema.getName(), defaultName);
		this.skipIfEmpty = schema == null ? false : schema.isSkipIfEmpty();
	}

	/**
	 * Returns the name of the HTTP part.
	 *
	 * @return The name of the HTTP part.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns whether the <code>skipIfEmpty</code> flag was found in the schema.
	 *
	 * @return <jk>true</jk> if the <code>skipIfEmpty</code> flag was found in the schema.
	 */
	public boolean isSkipIfEmpty() {
		return skipIfEmpty;
	}

	/**
	 * Returns the method argument index.
	 *
	 * @return The method argument index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the HTTP part type.
	 *
	 * @return The HTTP part type.  Never <jk>null</jk>.
	 */
	public HttpPartType getPartType() {
		return partType;
	}

	/**
	 * Returns the HTTP part serializer to use for serializing this part.
	 *
	 * @param _default The default serializer to use if the serializer was not defined via annotations.
	 * @return The HTTP part serializer, or <jk>null</jk> if not specified.
	 */
	public HttpPartSerializer getSerializer(HttpPartSerializer _default) {
		return serializer == null ? _default : serializer;
	}

	/**
	 * Returns the HTTP part schema information about this part.
	 *
	 * @return The HTTP part schema information, or <jk>null</jk> if not found.
	 */
	public HttpPartSchema getSchema() {
		return schema;
	}

	static RemoteMethodArg create(Method m, int i) {
		if (hasAnnotation(Header.class, m, i)) {
			return new RemoteMethodArg(i, HEADER, HttpPartSchema.create(Header.class, m, i));
		} else if (hasAnnotation(Query.class, m, i)) {
			return new RemoteMethodArg(i, QUERY, HttpPartSchema.create(Query.class, m, i));
		} else if (hasAnnotation(FormData.class, m, i)) {
			return new RemoteMethodArg(i, FORMDATA, HttpPartSchema.create(FormData.class, m, i));
		} else if (hasAnnotation(Path.class, m, i)) {
			return new RemoteMethodArg(i, PATH, HttpPartSchema.create(Path.class, m, i));
		} else if (hasAnnotation(Body.class, m, i)) {
			return new RemoteMethodArg(i, BODY, HttpPartSchema.create(Body.class, m, i));
		}
		return null;
	}
}
