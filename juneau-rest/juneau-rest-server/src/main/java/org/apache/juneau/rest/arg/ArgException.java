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
package org.apache.juneau.rest.arg;

import static org.apache.juneau.internal.StringUtils.*;

import org.apache.juneau.http.response.*;
import org.apache.juneau.reflect.*;

/**
 * General exception due to a malformed Java parameter.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../../overview-summary.html#juneau-rest-server.jrs.RestOpAnnotatedMethods.jrs.JavaMethodParameters">Java Method Parameters</a>
 * </ul>
 *
 * @serial exclude
 */
public class ArgException extends InternalServerError {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param pi The parameter with the issue.
	 * @param msg The message.
	 * @param args The message args.
	 */
	public ArgException(ParamInfo pi, String msg, Object...args) {
		super(format(msg, args) + " on parameter "+pi.getIndex()+" of method "+pi.getMethod().getFullName()+".");
	}
}
