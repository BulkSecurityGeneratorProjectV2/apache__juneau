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

import static org.apache.juneau.internal.StringUtils.*;

import java.net.*;
import java.net.URI;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;

/**
 * DTO for an HTML {@doc ext.HTML5.scripting-1#the-script-element <script>}
 * element.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jd.Html5}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
@Bean(typeName="script")
public class Script extends HtmlElementRawText {

	/**
	 * Creates an empty {@link Script} element.
	 */
	public Script() {}

	/**
	 * Creates a {@link Script} element with the specified {@link Script#type(String)} attribute and
	 * {@link Script#text(Object)} node.
	 *
	 * @param type The {@link Script#type(String)} attribute.
	 * @param text The child text node.
	 */
	public Script(String type, String...text) {
		type(type).text(joinnl(text));
	}

	/**
	 * {@doc ext.HTML5.scripting-1#attr-script-async async} attribute.
	 *
	 * <p>
	 * Execute script asynchronously.
	 *
	 * @param async
	 * 	The new value for this attribute.
	 * 	Typically a {@link Boolean} or {@link String}.
	 * @return This object.
	 */
	public final Script async(Object async) {
		attr("async", deminimize(async, "async"));
		return this;
	}

	/**
	 * {@doc ext.HTML5.scripting-1#attr-script-charset charset} attribute.
	 *
	 * <p>
	 * Character encoding of the external script resource.
	 *
	 * @param charset The new value for this attribute.
	 * @return This object.
	 */
	public final Script charset(String charset) {
		attr("charset", charset);
		return this;
	}

	/**
	 * {@doc ext.HTML5.scripting-1#attr-script-crossorigin crossorigin}
	 * attribute.
	 *
	 * <p>
	 * How the element handles cross-origin requests.
	 *
	 * @param crossorigin The new value for this attribute.
	 * @return This object.
	 */
	public final Script crossorigin(String crossorigin) {
		attr("crossorigin", crossorigin);
		return this;
	}

	/**
	 * {@doc ext.HTML5.scripting-1#attr-script-defer defer} attribute.
	 *
	 * <p>
	 * Defer script execution.
	 *
	 * @param defer
	 * 	The new value for this attribute.
	 * 	Typically a {@link Boolean} or {@link String}.
	 * @return This object.
	 */
	public final Script defer(Object defer) {
		attr("defer", deminimize(defer, "defer"));
		return this;
	}

	/**
	 * {@doc ext.HTML5.scripting-1#attr-script-src src} attribute.
	 *
	 * <p>
	 * Address of the resource.
	 *
	 * <p>
	 * The value can be of any of the following types: {@link URI}, {@link URL}, {@link String}.
	 * Strings must be valid URIs.
	 *
	 * <p>
	 * URIs defined by {@link UriResolver} can be used for values.
	 *
	 * @param src
	 * 	The new value for this attribute.
	 * 	Typically a {@link URL} or {@link String}.
	 * @return This object.
	 */
	public final Script src(Object src) {
		attrUri("src", src);
		return this;
	}

	/**
	 * {@doc ext.HTML5.scripting-1#attr-script-type type} attribute.
	 *
	 * <p>
	 * Type of embedded resource.
	 *
	 * @param type The new value for this attribute.
	 * @return This object.
	 */
	public final Script type(String type) {
		attr("type", type);
		return this;
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Overridden methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Script _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Script id(String id) {
		super.id(id);
		return this;
	}

	@Override /* HtmlElementText */
	public Script text(Object text) {
		super.text(text);
		return this;
	}
}
