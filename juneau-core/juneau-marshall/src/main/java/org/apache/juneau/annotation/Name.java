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
package org.apache.juneau.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

/**
 * Annotation that can be used on method parameters to identify their name.
 *
 * <p>
 * Can be used in the following locations:
 * <ul>
 * 	<li>On constructor and method arguments when the parameter names are not in the compiled bytecode.
 * </ul>
 *
 * <h5 class='figure'>Examples:</h5>
 * <p class='bjava'>
 * 	<jc>// Identifying bean property names.
 * 	// The field name can be anything.</jc>
 * 	<jk>public class</jk> MyBean {
 *
 * 		<jk>public</jk> MyBean(<ja>@Name</ja>(<js>"bar"</js>) <jk>int</jk> <jv>foo</jv>) {}
 * 	}
 * </p>
 *
 * <ul class='seealso'>
 * </ul>
 */
@Documented
@Target({PARAMETER})
@Retention(RUNTIME)
@Inherited
public @interface Name {

	/**
	 * The bean property or parameter name.
	 *
	 * @return The annotation value.
	 */
	String value();
}
