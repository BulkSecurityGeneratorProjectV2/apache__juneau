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

import java.util.function.*;

import org.apache.juneau.http.annotation.*;

/**
 * Represents a parsed <l>Vary</l> HTTP response header.
 *
 * <p>
 * Tells downstream proxies how to match future request headers to decide whether the cached response can be used rather
 * than requesting a fresh one from the origin server.
 *
 * <h5 class='figure'>Example</h5>
 * <p class='bcode'>
 * 	Vary: *
 * 	Vary: Accept-Language
 * </p>
 *
 * <h5 class='topic'>RFC2616 Specification</h5>
 *
 * The Vary field value indicates the set of request-header fields that fully determines, while the response is fresh,
 * whether a cache is permitted to use the response to reply to a subsequent request without revalidation.
 * For uncacheable or stale responses, the Vary field value advises the user agent about the criteria that were used to
 * select the representation.
 * A Vary field value of "*" implies that a cache cannot determine from the request headers of a subsequent request
 * whether this response is the appropriate representation.
 * See section 13.6 for use of the Vary header field by caches.
 * <p class='bcode'>
 * 	Vary  = "Vary" ":" ( "*" | 1#field-name )
 * </p>
 *
 * <p>
 * An HTTP/1.1 server SHOULD include a Vary header field with any cacheable response that is subject to server-driven
 * negotiation.
 * Doing so allows a cache to properly interpret future requests on that resource and informs the user agent about the
 * presence of negotiation on that resource.
 * A server MAY include a Vary header field with a non-cacheable response that is subject to server-driven negotiation,
 * since this might provide the user agent with useful information about the dimensions over which the response varies
 * at the time of the response.
 *
 * <p>
 * A Vary field value consisting of a list of field-names signals that the representation selected for the response is
 * based on a selection algorithm which considers ONLY the listed request-header field values in selecting the most
 * appropriate representation.
 * A cache MAY assume that the same selection will be made for future requests with the same values for the listed
 * field names, for the duration of time for which the response is fresh.
 *
 * <p>
 * The field-names given are not limited to the set of standard request-header fields defined by this specification.
 * Field names are case-insensitive.
 *
 * <p>
 * A Vary field value of "*" signals that unspecified parameters not limited to the request-headers (e.g., the network
 * address of the client), play a role in the selection of the response representation.
 * The "*" value MUST NOT be generated by a proxy server; it may only be generated by an origin server.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../../overview-summary.html#juneau-rest-common">juneau-rest-common</a>
 * 	<li class='extlink'><a class="doclink" href="https://www.w3.org/Protocols/rfc2616/rfc2616.html">Hypertext Transfer Protocol -- HTTP/1.1</a>
 * </ul>
 *
 * @serial exclude
 */
@Header("Vary")
public class Vary extends BasicStringHeader {

	//-----------------------------------------------------------------------------------------------------------------
	// Static
	//-----------------------------------------------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	private static final String NAME = "Vary";

	/**
	 * Static creator.
	 *
	 * @param value
	 * 	The header value.
	 * 	<br>Can be <jk>null</jk>.
	 * @return A new header bean, or <jk>null</jk> if the value is <jk>null</jk>.
	 */
	public static Vary of(String value) {
		return value == null ? null : new Vary(value);
	}

	/**
	 * Static creator with delayed value.
	 *
	 * <p>
	 * Header value is re-evaluated on each call to {@link #getValue()}.
	 *
	 * @param value
	 * 	The supplier of the header value.
	 * 	<br>Can be <jk>null</jk>.
	 * @return A new header bean, or <jk>null</jk> if the value is <jk>null</jk>.
	 */
	public static Vary of(Supplier<String> value) {
		return value == null ? null : new Vary(value);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Instance
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Constructor.
	 *
	 * @param value
	 * 	The header value.
	 * 	<br>Can be <jk>null</jk>.
	 */
	public Vary(String value) {
		super(NAME, value);
	}

	/**
	 * Constructor with delayed value.
	 *
	 * <p>
	 * Header value is re-evaluated on each call to {@link #getValue()}.
	 *
	 * @param value
	 * 	The supplier of the header value.
	 * 	<br>Can be <jk>null</jk>.
	 */
	public Vary(Supplier<String> value) {
		super(NAME, value);
	}
}
