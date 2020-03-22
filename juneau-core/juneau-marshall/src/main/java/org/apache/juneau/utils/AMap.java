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
package org.apache.juneau.utils;

import static java.util.Collections.*;

import java.util.*;

/**
 * An extension of {@link LinkedHashMap} with a convenience {@link #append(Object,Object)} method.
 *
 * <p>
 * Primarily used for testing purposes for quickly creating populated maps.
 * <p class='bcode w800'>
 * 	<jc>// Example:</jc>
 * 	Map&lt;String,Integer&gt; m = <jk>new</jk> AMap&lt;String,Integer&gt;()
 * 		.append(<js>"foo"</js>,1).append(<js>"bar"</js>,2);
 * </p>
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public final class AMap<K,V> extends LinkedHashMap<K,V> {

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------------------------------------------------------------
	// Constructors.
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Constructor.
	 */
	public AMap() {
		super();
	}

	/**
	 * Copy constructor.
	 *
	 * @param copy The map to copy.  Can be <jk>null</jk>.
	 */
	public AMap(Map<K,V> copy) {
		super(copy == null ? emptyMap() : copy);
	}

	//------------------------------------------------------------------------------------------------------------------
	// Creators.
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Creates an empty map.
	 *
	 * @return A new empty map.
	 */
	public static <K,V> AMap<K,V> of() {
		return new AMap<>();
	}

	/**
	 * Creates a map with one entry.
	 *
	 * @param key Entry key.
	 * @param value Entry value.
	 * @return A new map with one entry.
	 */
	public static <K,V> AMap<K,V> of(K key, V value) {
		return new AMap<K,V>().append(key, value);
	}

	/**
	 * Creates a new map initialized with the specified contents.
	 *
	 * @param copy Initialize with these contents.  Can be <jk>null</jk>.
	 * @return A new map.  Never <jk>null</jk>.
	 */
	public static <K,V> AMap<K,V> of(Map<K,V> copy) {
		return new AMap<>(copy);
	}

	/**
	 * Creates an unmodifiable copy of the specified map.
	 *
	 * @param copy The map to copy.
	 * @return A new unmodifiable map, never <jk>null</jk>.
	 */
	public static <K,V> Map<K,V> unmodifiable(Map<K,V> copy) {
		if (copy == null || copy.isEmpty())
			return emptyMap();
		return new AMap<>(copy).unmodifiable();
	}

	/**
	 * Creates a copy of the collection if it's not <jk>null</jk>.
	 *
	 * @param c The initial values.
	 * @return A new list, or <jk>null</jk> if the collection is <jk>null</jk>.
	 */
	public static <K,V> AMap<K,V> nullable(Map<K,V> c) {
		return c == null ? null : of(c);
	}

	//------------------------------------------------------------------------------------------------------------------
	// Methods.
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Adds an entry to this map.
	 *
	 * @param k The key.
	 * @param v The value.
	 * @return This object (for method chaining).
	 */
	public AMap<K,V> append(K k, V v) {
		put(k, v);
		return this;
	}

	/**
	 * Appends all the entries in the specified map to this map.
	 *
	 * @param c The map to copy.
	 * @return This object (for method chaining).
	 */
	public AMap<K,V> appendAll(Map<K,V> c) {
		super.putAll(c);
		return this;
	}

	/**
	 * Returns an unmodifiable view of this map.
	 *
	 * @return An unmodifiable view of this map.
	 */
	public Map<K,V> unmodifiable() {
		return this.isEmpty() ? emptyMap() : unmodifiableMap(this);
	}
}
