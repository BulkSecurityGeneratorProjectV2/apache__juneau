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

import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.swap.*;

/**
 * Transforms {@link Iterator Iterators} to {@code List<Object>} objects.
 *
 * <p>
 * This is a one-way transform, since {@code Iterators} cannot be reconstituted.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-marshall.jm.Swaps">Swaps</a>
 * </ul>
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class IteratorSwap extends ObjectSwap<Iterator,List> {

	/**
	 * Converts the specified {@link Iterator} to a {@link List}.
	 */
	@Override /* ObjectSwap */
	public List swap(BeanSession session, Iterator o) {
		List l = new LinkedList();
		while (o.hasNext())
			l.add(o.next());
		return l;
	}
}
