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
package org.apache.juneau.http.header;

import java.util.*;
import java.util.function.*;

import org.apache.juneau.http.annotation.*;

/**
 * Represents a parsed <l>Allow</l> HTTP response header.
 *
 * <p>
 * Valid methods for a specified resource. To be used for a 405 Method not allowed.
 *
 * <h5 class='figure'>Example</h5>
 * <p class='bcode w800'>
 * 	Allow: GET, HEAD
 * </p>
 *
 * <h5 class='topic'>RFC2616 Specification</h5>
 *
 * The Allow entity-header field lists the set of methods supported by the resource identified by the Request-URI.
 * The purpose of this field is strictly to inform the recipient of valid methods associated with the resource.
 * An Allow header field MUST be present in a 405 (Method Not Allowed) response.
 *
 * <p class='bcode w800'>
 * 	Allow   = "Allow" ":" #Method
 * </p>
 *
 * <p>
 * Example of use:
 * <p class='bcode w800'>
 * 	Allow: GET, HEAD, PUT
 * </p>
 *
 * <p>
 * This field cannot prevent a client from trying other methods.
 * However, the indications given by the Allow header field value SHOULD be followed.
 *
 * <p>
 * The actual set of allowed methods is defined by the origin server at the time of each request.
 *
 * <p>
 * The Allow header field MAY be provided with a PUT request to recommend the methods to be supported by the new or
 * modified resource.
 *
 * <p>
 * The server is not required to support these methods and SHOULD include an Allow header in the response giving the
 * actual supported methods.
 *
 * <p>
 * A proxy MUST NOT modify the Allow header field even if it does not understand all the methods specified, since the
 * user agent might
 * have other means of communicating with the origin server.
 *
 * <ul class='seealso'>
 * 	<li class='extlink'>{@doc RFC2616}
 * </ul>
 */
@Header("Allow")
public class Allow extends BasicCsvArrayHeader {

	private static final long serialVersionUID = 1L;

	/**
	 * Convenience creator.
	 *
	 * @param value
	 * 	The header value.
	 * 	<br>Can be any of the following:
	 * 	<ul>
	 * 		<li><c>String</c> - A comma-delimited string.
	 * 		<li><c>String[]</c> - A pre-parsed value.
	 * 		<li>Any other array type - Converted to <c>String[]</c>.
	 * 		<li>Any {@link Collection} - Converted to <c>String[]</c>.
	 * 		<li>Anything else - Converted to <c>String</c>.
	 * 	</ul>
	 * @return The parsed <c>Allow</c> header, or <jk>null</jk> if the value was null.
	 */
	public static Allow of(Object value) {
		if (value == null)
			return null;
		return new Allow(value);
	}

	/**
	 * Convenience creator using supplier.
	 *
	 * <p>
	 * Header value is re-evaluated on each call to {@link #getValue()}.
	 *
	 * @param value
	 * 	The header value supplier.
	 * 	<br>Can be any of the following:
	 * 	<ul>
	 * 		<li><c>String</c> - A comma-delimited string.
	 * 		<li><c>String[]</c> - A pre-parsed value.
	 * 		<li>Any other array type - Converted to <c>String[]</c>.
	 * 		<li>Any {@link Collection} - Converted to <c>String[]</c>.
	 * 		<li>Anything else - Converted to <c>String</c>.
	 * 	</ul>
	 * @return The parsed <c>Allow</c> header, or <jk>null</jk> if the value was null.
	 */
	public static Allow of(Supplier<?> value) {
		if (value == null)
			return null;
		return new Allow(value);
	}

	/**
	 * Constructor.
	 *
	 * @param value
	 * 	The header value.
	 * 	<br>Can be any of the following:
	 * 	<ul>
	 * 		<li><c>String</c> - A comma-delimited string.
	 * 		<li><c>String[]</c> - A pre-parsed value.
	 * 		<li>Any other array type - Converted to <c>String[]</c>.
	 * 		<li>Any {@link Collection} - Converted to <c>String[]</c>.
	 * 		<li>Anything else - Converted to <c>String</c>.
	 * 		<li>A {@link Supplier} of anything on this list.
	 * 	</ul>
	 */
	public Allow(Object value) {
		super("Allow", value);
	}

	/**
	 * Constructor
	 *
	 * @param value
	 * 	The header value.
	 */
	public Allow(String value) {
		this((Object)value);
	}
}
