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
 * DTO for an HTML {@doc ExtHTML5.tabular-data#the-col-element <col>}
 * element.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc DtoHtml5}
 * </ul>
 */
@Bean(typeName="col")
public class Col extends HtmlElementVoid {

	/**
	 * Creates an empty {@link Col} element.
	 */
	public Col() {}

	/**
	 * Creates a {@link Col} element with the specified {@link Col#span(Object)} attribute.
	 *
	 * @param span The {@link Col#span(Object)} attribute.
	 */
	public Col(Number span) {
		span(span);
	}

	/**
	 * {@doc ExtHTML5.tabular-data#attr-col-span span} attribute.
	 *
	 * <p>
	 * Number of columns spanned by the element.
	 *
	 * @param span
	 * 	The new value for this attribute.
	 * 	Typically a {@link Number} or {@link String}.
	 * @return This object (for method chaining).
	 */
	public final Col span(Object span) {
		attr("span", span);
		return this;
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Overridden methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Col _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Col id(String id) {
		super.id(id);
		return this;
	}

	@Override /* HtmlElement */
	public final Col style(String style) {
		super.style(style);
		return this;
	}
}
