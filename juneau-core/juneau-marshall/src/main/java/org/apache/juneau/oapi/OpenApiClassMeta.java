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
package org.apache.juneau.oapi;

import org.apache.juneau.*;
import org.apache.juneau.oapi.annotation.*;

/**
 * Metadata on classes specific to the OpenApi serializers and parsers pulled from the {@link OpenApi @OpenApi} annotation on
 * the class.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-marshall.jm.OpenApiDetails">OpenAPI Details</a>
 * </ul>
 */
public class OpenApiClassMeta extends ExtendedClassMeta {

	/**
	 * Constructor.
	 *
	 * @param cm The class that this annotation is defined on.
	 * @param mp OpenApi metadata provider (for finding information about other artifacts).
	 */
	public OpenApiClassMeta(ClassMeta<?> cm, OpenApiMetaProvider mp) {
		super(cm);
	}
}
