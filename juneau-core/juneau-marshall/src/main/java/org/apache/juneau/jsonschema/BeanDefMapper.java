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
package org.apache.juneau.jsonschema;

import java.net.*;

import org.apache.juneau.*;

/**
 * Interface used to retrieve identifiers and URIs for bean classes.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-marshall.jm.JsonSchemaDetails">JSON-Schema Support</a>
 * </ul>
 */
public interface BeanDefMapper {

	/**
	 * Represents the absence of a bean definition mapper class.
	 */
	public interface Void extends BeanDefMapper {}

	/**
	 * Returns the ID for the specified class.
	 *
	 * @param cm The class.
	 * @return The ID for the specified class.
	 */
	String getId(ClassMeta<?> cm);

	/**
	 * Returns the URI for the specified class.
	 *
	 * @param cm The class.
	 * @return The URI for the specified class.
	 */
	URI getURI(ClassMeta<?> cm);

	/**
	 * Returns the URI for the specified class by its ID.
	 *
	 * @param id The class ID.
	 * @return The URI for the specified class.
	 */
	URI getURI(String id);
}
