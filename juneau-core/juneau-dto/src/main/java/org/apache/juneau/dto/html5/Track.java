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

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;

/**
 * DTO for an HTML {@doc ext.HTML5.embedded-content-0#the-track-element <track>}
 * element.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jd.Html5}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
@Bean(typeName="track")
public class Track extends HtmlElementVoid {

	/**
	 * Creates an empty {@link Track} element.
	 */
	public Track() {}

	/**
	 * Creates a {@link Track} element with the specified {@link Track#src(Object)} and {@link Track#kind(String)}
	 * attributes.
	 *
	 * @param src The {@link Track#src(Object)} attribute.
	 * @param kind The {@link Track#kind(String)} attribute.
	 */
	public Track(Object src, String kind) {
		src(src).kind(kind);
	}

	/**
	 * {@doc ext.HTML5.embedded-content-0#attr-track-default default}
	 * attribute.
	 *
	 * <p>
	 * Enable the track if no other text track is more suitable.
	 *
	 * @param _default The new value for this attribute.
	 * @return This object.
	 */
	public final Track _default(String _default) {
		attr("default", _default);
		return this;
	}

	/**
	 * {@doc ext.HTML5.embedded-content-0#attr-track-kind kind} attribute.
	 *
	 * <p>
	 * The type of text track.
	 *
	 * @param kind The new value for this attribute.
	 * @return This object.
	 */
	public final Track kind(String kind) {
		attr("kind", kind);
		return this;
	}

	/**
	 * {@doc ext.HTML5.embedded-content-0#attr-track-label label} attribute.
	 *
	 * <p>
	 * User-visible label.
	 *
	 * @param label The new value for this attribute.
	 * @return This object.
	 */
	public final Track label(String label) {
		attr("label", label);
		return this;
	}

	/**
	 * {@doc ext.HTML5.embedded-content-0#attr-track-src src} attribute.
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
	public final Track src(Object src) {
		attrUri("src", src);
		return this;
	}

	/**
	 * {@doc ext.HTML5.embedded-content-0#attr-track-srclang srclang}
	 * attribute.
	 *
	 * <p>
	 * Language of the text track.
	 *
	 * @param srclang The new value for this attribute.
	 * @return This object.
	 */
	public final Track srclang(String srclang) {
		attr("srclang", srclang);
		return this;
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Overridden methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Track _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Track id(String id) {
		super.id(id);
		return this;
	}

	// <FluentSetters>

	// </FluentSetters>
}
