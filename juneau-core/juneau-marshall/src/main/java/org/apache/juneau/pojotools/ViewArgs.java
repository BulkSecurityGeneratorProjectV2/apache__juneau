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
package org.apache.juneau.pojotools;

import static org.apache.juneau.internal.CollectionUtils.*;

import java.util.*;

/**
 * Encapsulates arguments for the {@link PojoViewer} class.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jm.PojoTools}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
public class ViewArgs {

	private final List<String> view;

	/**
	 * Constructor.
	 *
	 * @param viewArgs
	 * 	View arguments.
	 * 	<br>Values are column names.
	 */
	public ViewArgs(String...viewArgs) {
		this(alist(viewArgs));
	}

	/**
	 * Constructor.
	 *
	 * @param viewArgs
	 * 	View arguments.
	 * 	<br>Values are column names.
	 */
	public ViewArgs(Collection<String> viewArgs) {
		this.view = unmodifiable(listFrom(viewArgs));
	}

	/**
	 * The view columns.
	 *
	 * <p>
	 * The view columns are the list of columns that should be displayed.
	 * An empty list implies all columns should be displayed.
	 *
	 * @return An unmodifiable list of columns to view.
	 */
	public List<String> getView() {
		return view;
	}
}
