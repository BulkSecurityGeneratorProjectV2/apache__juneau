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

import org.apache.juneau.parser.*;
import org.apache.juneau.reflect.*;
import org.apache.juneau.rest.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.rest.httppart.*;

/**
 * Resolves method parameters of type {@link InputStreamParser} on {@link RestOp}-annotated Java methods.
 *
 * <p>
 * The parameter value is resolved using:
 * <p class='bjava'>
 * 	<jv>opSession</jv>
 * 		.{@link RestOpSession#getRequest() getRequest}()
 * 		.{@link RestRequest#getContent() getContent}()
 * 		.{@link RequestContent#getParserMatch() getParserMatch}()
 * 		.{@link ParserMatch#getParser() getParser}();
 * </p>
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../../overview-summary.html#juneau-rest-server.jrs.RestOpAnnotatedMethods.jrs.JavaMethodParameters">Java Method Parameters</a>
 * </ul>
 */
public class InputStreamParserArg extends SimpleRestOperationArg {

	/**
	 * Static creator.
	 *
	 * @param paramInfo The Java method parameter being resolved.
	 * @return A new {@link InputStreamParserArg}, or <jk>null</jk> if the parameter type is not {@link InputStreamParser}.
	 */
	public static InputStreamParserArg create(ParamInfo paramInfo) {
		if (paramInfo.isType(InputStreamParser.class))
			return new InputStreamParserArg();
		return null;
	}

	/**
	 * Constructor.
	 */
	protected InputStreamParserArg() {
		super((opSession)->opSession.getRequest().getContent().getParserMatch().map(ParserMatch::getParser).filter(InputStreamParser.class::isInstance).orElse(null));
	}
}
