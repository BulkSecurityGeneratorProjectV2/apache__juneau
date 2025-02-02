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
package org.apache.juneau.http.part;

import static org.apache.juneau.internal.CollectionUtils.*;
import static org.apache.juneau.internal.StringUtils.*;

import java.util.*;
import java.util.function.*;

import org.apache.http.*;
import org.apache.juneau.assertions.*;

/**
 * A {@link NameValuePair} that consists of a single boolean value.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../../overview-summary.html#juneau-rest-common">juneau-rest-common</a>
 * </ul>
 */
public class BasicBooleanPart extends BasicPart {

	//-----------------------------------------------------------------------------------------------------------------
	// Static
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Static creator.
	 *
	 * @param name The part name.
	 * @param value The part value.
	 * @return A new {@link BasicBooleanPart} object, or <jk>null</jk> if the name or value is <jk>null</jk>.
	 */
	public static BasicBooleanPart of(String name, Boolean value) {
		if (isEmpty(name) || value == null)
			return null;
		return new BasicBooleanPart(name, value);
	}

	/**
	 * Static creator with delayed value.
	 *
	 * <p>
	 * Part value is re-evaluated on each call to {@link NameValuePair#getValue()}.
	 *
	 * @param name The part name.
	 * @param value The part value supplier.
	 * @return A new {@link BasicBooleanPart} object, or <jk>null</jk> if the name or supplier is <jk>null</jk>.
	 */
	public static BasicBooleanPart of(String name, Supplier<Boolean> value) {
		if (isEmpty(name) || value == null)
			return null;
		return new BasicBooleanPart(name, value);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Instance
	//-----------------------------------------------------------------------------------------------------------------

	private final Boolean value;
	private final Supplier<Boolean> supplier;

	/**
	 * Constructor.
	 *
	 * @param name The part name.  Must not be <jk>null</jk>.
	 * @param value The part value.  Can be <jk>null</jk>.
	 */
	public BasicBooleanPart(String name, Boolean value) {
		super(name, value);
		this.value = value;
		this.supplier = null;
	}

	/**
	 * Constructor.
	 *
	 * @param name The part name.  Must not be <jk>null</jk>.
	 * @param value The part value supplier.  Can be <jk>null</jk> or supply <jk>null</jk>.
	 */
	public BasicBooleanPart(String name, Supplier<Boolean> value) {
		super(name, value);
		this.value = null;
		this.supplier = value;
	}

	/**
	 * Constructor.
	 *
	 * <p>
	 * <jk>null</jk> and empty values are treated as <jk>null</jk>.
	 * Otherwise parses using {@link Boolean#valueOf(String)}.
	 *
	 * @param name The part name.  Must not be <jk>null</jk>.
	 * @param value The part value.  Can be <jk>null</jk>.
	 */
	public BasicBooleanPart(String name, String value) {
		super(name, value);
		this.value = isEmpty(value) ? null : Boolean.valueOf(value);
		this.supplier = null;
	}

	@Override /* NameValuePair */
	public String getValue() {
		return stringify(value());
	}

	/**
	 * Returns The part value as a {@link Boolean} wrapped in an {@link Optional}.
	 *
	 * @return The part value as a {@link Boolean} wrapped in an {@link Optional}.  Never <jk>null</jk>.
	 */
	public Optional<Boolean> asBoolean() {
		return optional(toBoolean());
	}

	/**
	 * Returns The part value as a {@link Boolean}.
	 *
	 * @return The part value as a {@link Boolean}, or <jk>null</jk> if the value <jk>null</jk>.
	 */
	public Boolean toBoolean() {
		return value();
	}

	/**
	 * Provides the ability to perform fluent-style assertions on this part.
	 *
	 * @return A new fluent assertion object.
	 * @throws AssertionError If assertion failed.
	 */
	public FluentBooleanAssertion<BasicBooleanPart> assertBoolean() {
		return new FluentBooleanAssertion<>(value(), this);
	}

	/**
	 * Return the value if present, otherwise return <c>other</c>.
	 *
	 * <p>
	 * This is a shortened form for calling <c>asBoolean().orElse(<jv>other</jv>)</c>.
	 *
	 * @param other The value to be returned if there is no value present, can be <jk>null</jk>.
	 * @return The value, if present, otherwise <c>other</c>.
	 */
	public Boolean orElse(Boolean other) {
		Boolean x = value();
		return x != null ? x : other;
	}

	private Boolean value() {
		if (supplier != null)
			return supplier.get();
		return value;
	}
}
