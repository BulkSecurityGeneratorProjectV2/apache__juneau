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
package org.apache.juneau.dto.html5;

import org.apache.juneau.annotation.*;

/**
 * DTO for an HTML {@doc ext.HTML5.grouping-content#the-dl-element <dl>}
 * element.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jd.Html5}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
@Bean(typeName="dl")
public class Dl extends HtmlElementContainer {

	/**
	 * Creates an empty {@link Dl} element.
	 */
	public Dl() {}

	/**
	 * Creates a {@link Dl} element with the specified child nodes.
	 *
	 * @param children The child nodes.
	 */
	public Dl(Object...children) {
		children(children);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Overridden methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Dl _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Dl id(String id) {
		super.id(id);
		return this;
	}

	@Override /* HtmlElement */
	public final Dl style(String style) {
		super.style(style);
		return this;
	}

	@Override /* HtmlElementContainer */
	public final Dl children(Object...children) {
		super.children(children);
		return this;
	}

	@Override /* HtmlElementContainer */
	public final Dl child(Object child) {
		super.child(child);
		return this;
	}
}
