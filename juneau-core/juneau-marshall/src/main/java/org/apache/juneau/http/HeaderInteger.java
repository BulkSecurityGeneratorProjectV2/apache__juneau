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
package org.apache.juneau.http;

import org.apache.juneau.http.annotation.*;

/**
 * Category of headers that consist of a single integer value.
 *
 * <p>
 * <h5 class='figure'>Example</h5>
 * <p class='bcode w800'>
 * 	Age: 300
 * </p>
 *
 * <ul class='seealso'>
 * 	<li class='extlink'>{@doc RFC2616}
 * </ul>
 */
@Header(type="integer",format="int32")
public class HeaderInteger {

	private final Integer value;

	/**
	 * Constructor.
	 *
	 * @param value The raw header value.
	 */
	protected HeaderInteger(String value) {
		int _value = 0;
		try {
			_value = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			try {
				Long.parseLong(value);
				_value = Integer.MAX_VALUE;
			} catch (NumberFormatException e2) {}
		}
		this.value = _value;
	}

	/**
	 * Constructor.
	 *
	 * @param value The parsed header value.
	 */
	protected HeaderInteger(Integer value) {
		this.value = value;
	}

	/**
	 * Returns this header as a simple string value.
	 *
	 * <p>
	 * Functionally equivalent to calling {@link #toString()}.
	 *
	 * @return This header as a simple string.
	 */
	public int asInt() {
		return value;
	}

	@Override /* Object */
	public String toString() {
		return String.valueOf(value);
	}
}
