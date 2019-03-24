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
package org.apache.juneau.rest.mock2;

import java.util.*;

/**
 * Represent the basic connection for mock HTTP requests.
 *
 * <p>
 * Used as a shim between the server and client APIs that allow the <code>RestClient</code>
 * class to send and receive mocked requests using the <code>MockRest</code> interface.
 */
public interface MockHttpConnection {

	/**
	 * Creates a mocked HTTP request.
	 *
	 * @param method The HTTP request method.
	 * @param path The HTTP request path.
	 * @param headers Optional HTTP request headers.
	 * @param body The HTTP request body.
	 * @return A new mock request.
	 * @throws Exception
	 */
	MockHttpRequest request(String method, String path, Map<String,Object> headers, Object body) throws Exception;
}
