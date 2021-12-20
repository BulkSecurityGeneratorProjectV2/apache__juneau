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

import java.net.*;
import java.net.URI;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;

/**
 * DTO for an HTML {@doc ext.HTML5.document-metadata#the-base-element <base>}
 * element.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jd.Html5}
 * </ul>
 */
@Bean(typeName="base")
public class Base extends HtmlElementVoid {

	/**
	 * Creates an empty {@link Base} element.
	 */
	public Base() {}

	/**
	 * Creates a {@link Base} element with the specified {@link Base#href(Object)} attribute.
	 *
	 * @param href The {@link Base#href(Object)} attribute.
	 */
	public Base(Object href) {
		href(href);
	}

	/**
	 * {@doc ext.HTML5.document-metadata#attr-base-href href} attribute.
	 *
	 * <p>
	 * Document base URL.
	 *
	 * <p>
	 * The value can be of any of the following types: {@link URI}, {@link URL}, {@link String}.
	 * Strings must be valid URIs.
	 *
	 * <p>
	 * URIs defined by {@link UriResolver} can be used for values.
	 *
	 * @param href
	 * 	The new value for this attribute.
	 * 	Typically a {@link URL} or {@link String}.
	 * @return This object.
	 */
	public final Base href(Object href) {
		attrUri("href", href);
		return this;
	}

	/**
	 * {@doc ext.HTML5.document-metadata#attr-base-target target}
	 * attribute.
	 *
	 * <p>
	 * Default browsing context for hyperlink navigation and form submission.
	 *
	 * @param target The new value for this attribute.
	 * @return This object.
	 */
	public final Base target(String target) {
		attr("target", target);
		return this;
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Overridden methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Base _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Base id(String id) {
		super.id(id);
		return this;
	}

	@Override /* HtmlElement */
	public final Base style(String style) {
		super.style(style);
		return this;
	}
}
