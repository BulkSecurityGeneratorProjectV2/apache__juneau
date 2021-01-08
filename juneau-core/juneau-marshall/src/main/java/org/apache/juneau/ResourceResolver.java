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
 * Class used to resolve {@link Class} objects to instances.
 */
@Deprecated
public interface ResourceResolver {

	/**
	 * Look for constructors where the arguments passed in must match exactly.
	 */
	public static final ResourceResolver BASIC = new BasicResourceResolver();

	/**
	 * Look for constructors where arguments may or may not exist in any order.
	 */
	public static final ResourceResolver FUZZY = new FuzzyResourceResolver();

	/**
	 * Resolves the specified class to a resource object.
	 *
	 * <p>
	 * Subclasses can override this method to provide their own custom resolution.
	 *
	 * <p>
	 * The default implementation simply creates a new class instance using {@link Class#newInstance()}.
	 *
	 * @param parent
	 * 	The parent resource.
	 * @param c The class to resolve.
	 * @param args Optional arguments to pass to constructor
	 * @return The instance of that class.
	 */
	<T> T resolve(Object parent, Class<T> c, Object...args);
}
