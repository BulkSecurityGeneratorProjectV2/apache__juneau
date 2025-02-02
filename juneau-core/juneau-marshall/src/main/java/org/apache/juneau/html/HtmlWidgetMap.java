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
package org.apache.juneau.html;

import java.util.*;

/**
 * A collection of {@link HtmlWidget} objects keyed by their names.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-rest-server.jrs.HtmlDocAnnotation.jrs.HtmlWidgets">Widgets</a>
 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-marshall.jm.HtmlDetails">HTML Details</a>
 * </ul>
 *
 * @serial exclude
 */
public class HtmlWidgetMap extends LinkedHashMap<String,HtmlWidget> {
	private static final long serialVersionUID = 1L;

	/**
	 * Adds the specified widgets to this map.
	 *
	 * @param w The widgets to add to this map.
	 * @return This object.
	 */
	public HtmlWidgetMap append(HtmlWidget...w) {
		for (HtmlWidget ww : w)
			put(ww.getName(), ww);
		return this;
	}
}
