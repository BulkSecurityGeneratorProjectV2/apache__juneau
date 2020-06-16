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
package org.apache.juneau.http.remote;

import static org.apache.juneau.internal.StringUtils.*;

import java.lang.reflect.*;

import org.apache.juneau.internal.*;

/**
 * Contains the meta-data about a Java method on a remote class.
 *
 * <p>
 * Captures the information in {@link Remote @Remote} annotations for caching and reuse.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc juneau-rest-server.restRPC}
 * </ul>
 */
public class RrpcInterfaceMethodMeta {

	private final String url, path;
	private final Method method;

	/**
	 * Constructor.
	 *
	 * @param restUrl The absolute URL of the REST interface backing the interface proxy.
	 * @param m The Java method.
	 */
	public RrpcInterfaceMethodMeta(final String restUrl, Method m) {
		this.method = m;
		this.path =  m.getName() + '/' + HttpUtils.getMethodArgsSignature(m, true);
		this.url = trimSlashes(restUrl) + '/' + urlEncode(path);
	}

	/**
	 * Returns the absolute URL of the REST interface invoked by this Java method.
	 *
	 * @return The absolute URL of the REST interface, never <jk>null</jk>.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Returns the HTTP path of this method.
	 *
	 * @return
	 * 	The HTTP path of this method relative to the parent interface.
	 * 	<br>Never <jk>null</jk>.
	 * 	<br>Never has leading or trailing slashes.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns the underlying Java method that this metadata is about.
	 *
	 * @return
	 * 	The underlying Java method that this metadata is about.
	 * 	<br>Never <jk>null</jk>.
	 */
	public Method getJavaMethod() {
		return method;
	}
}
