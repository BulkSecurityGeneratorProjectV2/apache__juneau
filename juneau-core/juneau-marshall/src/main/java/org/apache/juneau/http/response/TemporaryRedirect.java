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
package org.apache.juneau.http.response;

import static org.apache.juneau.http.response.TemporaryRedirect.*;

import java.net.*;

import org.apache.juneau.annotation.BeanIgnore;
import org.apache.juneau.http.annotation.*;

/**
 * Represents an <c>HTTP 307 Temporary Redirect</c> response.
 *
 * <p>
 * In this case, the request should be repeated with another URI; however, future requests should still use the original URI.
 * In contrast to how 302 was historically implemented, the request method is not allowed to be changed when reissuing the original request.
 * For example, a POST request should be repeated using another POST request.
 */
@Response(code=CODE, description=MESSAGE)
@BeanIgnore
public class TemporaryRedirect extends HttpResponse {

	/** HTTP status code */
	public static final int CODE = 307;

	/** Default message */
	public static final String MESSAGE = "Temporary Redirect";

	/** Reusable instance. */
	public static final TemporaryRedirect INSTANCE = new TemporaryRedirect();

	private final URI location;

	/**
	 * Constructor using HTTP-standard message.
	 */
	public TemporaryRedirect() {
		this(MESSAGE, null);
	}

	/**
	 * Constructor with no redirect.
	 * <p>
	 * Used for end-to-end interfaces.
	 *
	 * @param message Message to send as the response.
	 */
	public TemporaryRedirect(String message) {
		super(message);
		this.location = null;
	}

	/**
	 * Constructor using custom message.
	 * @param message Message to send as the response.
	 * @param location <c>Location</c> header value.
	 */
	public TemporaryRedirect(String message, URI location) {
		super(message);
		this.location = location;
	}

	/**
	 * Constructor.
	 * @param location <c>Location</c> header value.
	 */
	public TemporaryRedirect(URI location) {
		this(MESSAGE, location);
	}

	/**
	 * @return <c>Location</c> header value.
	 */
	@ResponseHeader(name="Location", description="Temporary location of resource.")
	public URI getLocation() {
		return location;
	}
}