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
 * DTO for an HTML {@doc ext.HTML5.edits#the-ins-element <ins>}
 * element.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jd.Html5}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
@Bean(typeName="ins")
public class Ins extends HtmlElementMixed {

	/**
	 * Creates an empty {@link Ins} element.
	 */
	public Ins() {}

	/**
	 * Creates an {@link Ins} element with the specified child nodes.
	 *
	 * @param children The child nodes.
	 */
	public Ins(Object...children) {
		children(children);
	}

	/**
	 * {@doc ext.HTML5.edits#attr-mod-cite cite} attribute.
	 *
	 * <p>
	 * Link to the source of the quotation or more information about the edit.
	 *
	 * @param cite The new value for this attribute.
	 * @return This object.
	 */
	public final Ins cite(String cite) {
		attr("cite", cite);
		return this;
	}

	/**
	 * {@doc ext.HTML5.edits#attr-mod-datetime datetime} attribute.
	 *
	 * <p>
	 * Date and (optionally) time of the change.
	 *
	 * @param datetime The new value for this attribute.
	 * @return This object.
	 */
	public final Ins datetime(String datetime) {
		attr("datetime", datetime);
		return this;
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Overridden methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Ins _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Ins id(String id) {
		super.id(id);
		return this;
	}

	@Override /* HtmlElement */
	public final Ins style(String style) {
		super.style(style);
		return this;
	}

	@Override /* HtmlElementMixed */
	public Ins children(Object...children) {
		super.children(children);
		return this;
	}

	@Override /* HtmlElementMixed */
	public Ins child(Object child) {
		super.child(child);
		return this;
	}

	// <FluentSetters>

	// </FluentSetters>
}
