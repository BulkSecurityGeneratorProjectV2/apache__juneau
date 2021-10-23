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
 * DTO for an HTML {@doc ExtHTML5.tabular-data#the-table-element <table>}
 * element.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc DtoHtml5}
 * </ul>
 */
@Bean(typeName="table")
public class Table extends HtmlElementContainer {

	/**
	 * Creates an empty {@link Table} element.
	 */
	public Table() {}

	/**
	 * Creates a {@link Table} element with the specified child nodes.
	 *
	 * @param children The child nodes.
	 */
	public Table(Object...children) {
		children(children);
	}

	/**
	 * {@doc ExtHTML5.tabular-data#attr-table-border border} attribute.
	 *
	 * @param border
	 * 	The new value for this attribute.
	 * 	Typically a {@link Number} or {@link String}.
	 * @return This object.
	 */
	public final Table border(Object border) {
		attr("border", border);
		return this;
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Overridden methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Table _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Table id(String id) {
		super.id(id);
		return this;
	}

	@Override /* HtmlElement */
	public final Table style(String style) {
		super.style(style);
		return this;
	}

	@Override /* HtmlElementContainer */
	public final Table children(Object...children) {
		super.children(children);
		return this;
	}

	@Override /* HtmlElementContainer */
	public final Table child(Object child) {
		super.child(child);
		return this;
	}
}
