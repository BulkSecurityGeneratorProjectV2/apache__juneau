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
 * Represents a parsed <l>If-Unmodified-Since</l> HTTP request header.
 *
 * <p>
 * Only send the response if the entity has not been modified since a specific time.
 *
 * <h5 class='figure'>Example</h5>
 * <p class='bcode w800'>
 * 	If-Unmodified-Since: Sat, 29 Oct 1994 19:43:31 GMT
 * </p>
 *
 * <h5 class='topic'>RFC2616 Specification</h5>
 *
 * The If-Unmodified-Since request-header field is used with a method to make it conditional.
 * If the requested resource has not been modified since the time specified in this field, the server SHOULD perform the
 * requested operation as if the If-Unmodified-Since header were not present.
 *
 * <p>
 * If the requested variant has been modified since the specified time, the server MUST NOT perform the requested
 * operation, and MUST return a 412 (Precondition Failed).
 *
 * <p class='bcode w800'>
 * 	If-Unmodified-Since = "If-Unmodified-Since" ":" HTTP-date
 * </p>
 *
 * <p>
 * An example of the field is:
 * <p class='bcode w800'>
 * 	If-Unmodified-Since: Sat, 29 Oct 1994 19:43:31 GMT
 * </p>
 *
 * <p>
 * If the request normally (i.e., without the If-Unmodified-Since header) would result in anything other than a 2xx or
 * 412 status, the If-Unmodified-Since header SHOULD be ignored.
 *
 * <p>
 * If the specified date is invalid, the header is ignored.
 *
 * <p>
 * The result of a request having both an If-Unmodified-Since header field and either an If-None-Match or an
 * If-Modified-Since header fields is undefined by this specification.
 *
 * <ul class='seealso'>
 * 	<li class='extlink'>{@doc RFC2616}
 * </ul>
 */
@Header("If-Unmodified-Since")
public final class IfUnmodifiedSince extends BasicDateHeader {

	/**
	 * Returns a parsed <c>If-Unmodified-Since</c> header.
	 *
	 * @param value The <c>If-Unmodified-Since</c> header string.
	 * @return The parsed <c>If-Unmodified-Since</c> header, or <jk>null</jk> if the string was null.
	 */
	public static IfUnmodifiedSince forString(String value) {
		if (value == null)
			return null;
		return new IfUnmodifiedSince(value);
	}

	private IfUnmodifiedSince(String value) {
		super("If-Unmodified-Since", value);
	}
}
