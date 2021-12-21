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
 * DTO for an HTML {@doc ext.HTML5.forms#the-meter-element <meter>}
 * element.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jd.Html5}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
@Bean(typeName="meter")
public class Meter extends HtmlElementMixed {

	/**
	 * Creates an empty {@link Meter} element.
	 */
	public Meter() {}

	/**
	 * Creates a {@link Meter} element with the specified child nodes.
	 *
	 * @param children The child nodes.
	 */
	public Meter(Object...children) {
		children(children);
	}

	/**
	 * {@doc ext.HTML5.forms#attr-meter-high high} attribute.
	 *
	 * <p>
	 * Low limit of high range.
	 *
	 * @param high
	 * 	The new value for this attribute.
	 * 	Typically a {@link Number} or {@link String}.
	 * @return This object.
	 */
	public final Meter high(Object high) {
		attr("high", high);
		return this;
	}

	/**
	 * {@doc ext.HTML5.forms#attr-meter-low low} attribute.
	 *
	 * <p>
	 * High limit of low range.
	 *
	 * @param low
	 * 	The new value for this attribute.
	 * 	Typically a {@link Number} or {@link String}.
	 * @return This object.
	 */
	public final Meter low(Object low) {
		attr("low", low);
		return this;
	}

	/**
	 * {@doc ext.HTML5.forms#attr-meter-max max} attribute.
	 *
	 * <p>
	 * Upper bound of range.
	 *
	 * @param max
	 * 	The new value for this attribute.
	 * 	Typically a {@link Number} or {@link String}.
	 * @return This object.
	 */
	public final Meter max(Object max) {
		attr("max", max);
		return this;
	}

	/**
	 * {@doc ext.HTML5.forms#attr-meter-min min} attribute.
	 *
	 * <p>
	 * Lower bound of range.
	 *
	 * @param min
	 * 	The new value for this attribute.
	 * 	Typically a {@link Number} or {@link String}.
	 * @return This object.
	 */
	public final Meter min(Object min) {
		attr("min", min);
		return this;
	}

	/**
	 * {@doc ext.HTML5.forms#attr-meter-optimum optimum} attribute.
	 *
	 * <p>
	 * Optimum value in gauge.
	 *
	 * @param optimum
	 * 	The new value for this attribute.
	 * 	Typically a {@link Number} or {@link String}.
	 * @return This object.
	 */
	public final Meter optimum(Object optimum) {
		attr("optimum", optimum);
		return this;
	}

	/**
	 * {@doc ext.HTML5.forms#attr-meter-value value} attribute.
	 *
	 * <p>
	 * Current value of the element.
	 *
	 * @param value
	 * 	The new value for this attribute.
	 * 	Typically a {@link Number} or {@link String}.
	 * @return This object.
	 */
	public final Meter value(Object value) {
		attr("value", value);
		return this;
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Overridden methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Meter _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Meter id(String id) {
		super.id(id);
		return this;
	}

	@Override /* HtmlElement */
	public final Meter style(String style) {
		super.style(style);
		return this;
	}

	@Override /* HtmlElementMixed */
	public Meter children(Object...children) {
		super.children(children);
		return this;
	}

	@Override /* HtmlElementMixed */
	public Meter child(Object child) {
		super.child(child);
		return this;
	}
}
