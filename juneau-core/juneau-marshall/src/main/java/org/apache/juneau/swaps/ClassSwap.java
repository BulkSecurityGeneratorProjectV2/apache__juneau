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
package org.apache.juneau.swaps;

import org.apache.juneau.*;
import org.apache.juneau.swap.*;

/**
 * Transforms {@link Class} objects to and from {@link String Strings}.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-marshall.jm.Swaps">Swaps</a>
 * </ul>
 */
public class ClassSwap extends StringSwap<Class<?>> {

	@Override /* ObjectSwap */
	public String swap(BeanSession session, Class<?> o) throws Exception {
		return o == null ? null : o.getName();
	}

	@Override /* ObjectSwap */
	public Class<?> unswap(BeanSession session, String o, ClassMeta<?> hint) throws Exception {
		return o == null ? null : Class.forName(o);
	}
}
