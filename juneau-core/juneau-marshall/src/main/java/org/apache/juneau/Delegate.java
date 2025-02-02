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
package org.apache.juneau;

/**
 * An object that represents another object, often wrapping that object.
 *
 * <p>
 * <b>*** Internal Interface - Not intended for external use ***</b>
 *
 * <p>
 * For example, {@link BeanMap} is a map representation of a bean.
 *
 * <ul class='seealso'>
 * </ul>
 *
 * @param <T> The represented class type.
 */
public interface Delegate<T> {

	/**
	 * The {@link ClassMeta} of the class of the represented object.
	 *
	 * @return The class type of the represented object.
	 */
	public ClassMeta<T> getClassMeta();
}
