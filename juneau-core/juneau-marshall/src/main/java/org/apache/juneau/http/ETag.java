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
package org.apache.juneau.http;

import org.apache.juneau.http.annotation.*;

/**
 * Represents a parsed <l>ETag</l> HTTP response header.
 *
 * <p>
 * An identifier for a specific version of a resource, often a message digest.
 *
 * <h5 class='figure'>Example</h5>
 * <p class='bcode w800'>
 * 	ETag: "737060cd8c284d8af7ad3082f209582d"
 * </p>
 *
 * <h5 class='topic'>RFC2616 Specification</h5>
 *
 * The ETag response-header field provides the current value of the entity tag for the requested variant.
 * The headers used with entity tags are described in sections 14.24, 14.26 and 14.44.
 * The entity tag MAY be used for comparison with other entities from the same resource (see section 13.3.3).
 *
 * <p class='bcode w800'>
 * 	ETag = "ETag" ":" entity-tag
 * </p>
 *
 * <p>
 * Examples:
 * <p class='bcode w800'>
 * 	ETag: "xyzzy"
 * 	ETag: W/"xyzzy"
 * 	ETag: ""
 * </p>
 *
 * <ul class='seealso'>
 * 	<li class='extlink'>{@doc RFC2616}
 * </ul>
 */
@Header("ETag")
public final class ETag extends BasicStringHeader {

	/**
	 * Returns a parsed <c>ETag</c> header.
	 *
	 * @param value The <c>ETag</c> header string.
	 * @return The parsed <c>ETag</c> header, or <jk>null</jk> if the string was null.
	 */
	public static ETag forString(String value) {
		if (value == null)
			return null;
		return new ETag(value);
	}

	/**
	 * Constructor.
	 *
	 * @param value The value for this header.
	 */
	public ETag(String value) {
		super("ETag", value);
	}
}
