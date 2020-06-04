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
package org.apache.juneau;

import static org.apache.juneau.internal.StringUtils.*;
import static org.apache.juneau.internal.ClassUtils.*;
import static org.apache.juneau.Context.*;

import java.lang.reflect.*;
import java.text.*;
import java.time.*;
import java.util.*;

import org.apache.juneau.collections.*;
import org.apache.juneau.http.*;
import org.apache.juneau.internal.*;
import org.apache.juneau.json.*;
import org.apache.juneau.reflect.*;

/**
 * A one-time-use non-thread-safe object that's meant to be used once and then thrown away.
 *
 * <p>
 * Serializers and parsers use session objects to retrieve config properties and to use it as a scratchpad during
 * serialize and parse actions.
 */
public abstract class Session {

	private final OMap properties;
	private Map<String,Object> cache;
	private List<String> warnings;                 // Any warnings encountered.

	private final boolean debug;
	private final Locale locale;
	private final TimeZone timeZone;
	private final MediaType mediaType;


	/**
	 * Default constructor.
	 *
	 * @param ctx The context object.
	 * @param args
	 * 	Runtime arguments.
	 */
	protected Session(Context ctx, SessionArgs args) {
		this.properties = args.properties == null ? OMap.EMPTY_MAP : args.properties;
		debug = getProperty(CONTEXT_debug, Boolean.class, ctx.isDebug());
		locale = getProperty(CONTEXT_locale, Locale.class, ctx.getLocale());
		timeZone = getProperty(CONTEXT_timeZone, TimeZone.class, ctx.getTimeZone());
		mediaType = getProperty(CONTEXT_mediaType, MediaType.class, ctx.getMediaType());
	}

	/**
	 * Returns <jk>true</jk> if this session has the specified property defined.
	 *
	 * @param key The property key.
	 * @return <jk>true</jk> if this session has the specified property defined.
	 */
	public final boolean hasProperty(String key) {
		return properties != null && properties.containsKey(key);
	}

	/**
	 * Returns the session property with the specified key.
	 *
	 * <p>
	 * The returned type is the raw value of the property.
	 *
	 * @param key The property key.
	 * @return The session property, or <jk>null</jk> if the property does not exist.
	 */
	public final Object getProperty(String key) {
		if (properties == null)
			return null;
		return properties.get(key);
	}

	/**
	 * Returns the session property with the specified key and type.
	 *
	 * @param key The property key.
	 * @param type The type to convert the property to.
	 * @param def The default value if the session property does not exist or is <jk>null</jk>.
	 * @return The session property.
	 */
	@SuppressWarnings("unchecked")
	public final <T> T getProperty(String key, Class<T> type, T def) {
		if (properties == null)
			return def;
		Object o = properties.get(key);
		if (o == null)
			return def;
		type = (Class<T>)ClassInfo.of(type).getWrapperIfPrimitive();
		T t = properties.get(key, type);
		return t == null ? def : t;
	}

	/**
	 * Same as {@link #getProperty(String, Class, Object)} but allows for more than one default value.
	 *
	 * @param key The property key.
	 * @param type The type to convert the property to.
	 * @param def
	 * 	The default values if the session property does not exist or is <jk>null</jk>.
	 * 	The first non-null value is returned.
	 * @return The session property.
	 */
	@SafeVarargs
	public final <T> T getProperty(String key, Class<T> type, T...def) {
		return getProperty(key, type, ObjectUtils.firstNonNull(def));
	}

	/**
	 * Returns the session class property with the specified name.
	 *
	 * @param key The property name.
	 * @param type The class type of the property.
	 * @param def The default value.
	 * @return The property value, or the default value if it doesn't exist.
	 */
	@SuppressWarnings("unchecked")
	public final <T> Class<? extends T> getClassProperty(String key, Class<T> type, Class<? extends T> def) {
		return getProperty(key, Class.class, def);
	}

	/**
	 * Returns an instantiation of the specified class property.
	 *
	 * @param key The property name.
	 * @param type The class type of the property.
	 * @param def
	 * 	The default instance or class to instantiate if the property doesn't exist.
	 * @return A new property instance.
	 */
	public <T> T getInstanceProperty(String key, Class<T> type, Object def) {
		return newInstance(type, getProperty(key), def);
	}

	/**
	 * Returns the specified property as an array of instantiated objects.
	 *
	 * @param key The property name.
	 * @param type The class type of the property.
	 * @param def The default object to return if the property doesn't exist.
	 * @return A new property instance.
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] getInstanceArrayProperty(String key, Class<T> type, T[] def) {
		Object o = getProperty(key);
		T[] t = null;
		if (o == null)
			t = def;
		else if (o.getClass().isArray()) {
			if (o.getClass().getComponentType() == type)
				t = (T[])o;
			else {
				t = (T[])Array.newInstance(type, Array.getLength(o));
				for (int i = 0; i < Array.getLength(o); i++)
					t[i] = newInstance(type, Array.get(o, i), null);
			}
		} else if (o instanceof Collection) {
			Collection<?> c = (Collection<?>)o;
			t = (T[])Array.newInstance(type, c.size());
			int i = 0;
			for (Object o2 : c)
				t[i++] = newInstance(type, o2, null);
		}
		if (t != null)
			return t;
		throw new ConfigException("Could not instantiate property ''{0}'' as type {1}", key, type);
	}

	/**
	 * Returns the session properties.
	 *
	 * @return The session properties passed in through the constructor.
	 */
	protected OMap getProperties() {
		return properties;
	}

	/**
	 * Returns the session property keys.
	 *
	 * @return The session property keys passed in through the constructor.
	 */
	public Set<String> getPropertyKeys() {
		return properties.keySet();
	}

	@SuppressWarnings("unchecked")
	private <T> T newInstance(Class<T> type, Object o, Object def) {
		T t = null;
		if (o == null) {
			if (def == null)
				return null;
			t = castOrCreate(type, def);
		}
		else if (type.isInstance(o))
			t = (T)o;
		else if (o.getClass() == Class.class)
			t = castOrCreate(type, o);
		else if (o.getClass() == String.class)
			t = ClassUtils.fromString(type, o.toString());
		if (t != null)
			return t;
		throw new ConfigException("Could not instantiate type ''{0}'' as type {1}", o, type);
	}

	/**
	 * Adds an arbitrary object to this session's cache.
	 *
	 * <p>
	 * Can be used to store objects for reuse during a session.
	 *
	 * @param key The key.  Can be any string.
	 * @param val The cached object.
	 */
	public final void addToCache(String key, Object val) {
		if (cache == null)
			cache = new TreeMap<>();
		cache.put(key, val);
	}

	/**
	 * Adds arbitrary objects to this session's cache.
	 *
	 * <p>
	 * Can be used to store objects for reuse during a session.
	 *
	 * @param cacheObjects
	 * 	The objects to add to this session's cache.
	 * 	No-op if <jk>null</jk>.
	 */
	public final void addToCache(Map<String,Object> cacheObjects) {
		if (cacheObjects != null) {
			if (cache == null)
				cache = new TreeMap<>();
			cache.putAll(cacheObjects);
		}
	}

	/**
	 * Returns an object stored in the session cache.
	 *
	 * @param c The class type of the object.
	 * @param key The session object key.
	 * @return The cached object, or <jk>null</jk> if it doesn't exist.
	 */
	@SuppressWarnings("unchecked")
	public final <T> T getFromCache(Class<T> c, String key) {
		return cache == null ? null : (T)cache.get(key);
	}

	/**
	 * Logs a warning message.
	 *
	 * @param msg The warning message.
	 * @param args Optional {@link MessageFormat}-style arguments.
	 */
	public void addWarning(String msg, Object... args) {
		if (warnings == null)
			warnings = new LinkedList<>();
		warnings.add((warnings.size() + 1) + ": " + format(msg, args));
	}

	/**
	 * Returns <jk>true</jk> if warnings occurred in this session.
	 *
	 * @return <jk>true</jk> if warnings occurred in this session.
	 */
	public final boolean hasWarnings() {
		return warnings != null && warnings.size() > 0;
	}

	/**
	 * Returns the warnings that occurred in this session.
	 *
	 * @return The warnings that occurred in this session, or <jk>null</jk> if no warnings occurred.
	 */
	public final List<String> getWarnings() {
		return warnings;
	}

	/**
	 * Throws a {@link BeanRuntimeException} if any warnings occurred in this session.
	 */
	public void checkForWarnings() {
		if (warnings != null && ! warnings.isEmpty())
			throw new BeanRuntimeException("Warnings occurred in session: \n" + join(getWarnings(), "\n"));
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Configuration properties
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Configuration property:  Debug mode.
	 *
	 * @see BeanContext#CONTEXT_debug
	 * @return
	 * 	<jk>true</jk> if debug mode is enabled.
	 */
	protected final boolean isDebug() {
		return debug;
	}

	/**
	 * Configuration property:  Locale.
	 *
	 * <p>
	 * The locale is determined in the following order:
	 * <ol>
	 * 	<li><c>locale</c> parameter passed in through constructor.
	 * 	<li>{@link Context#CONTEXT_locale} entry in parameter passed in through constructor.
	 * 	<li>{@link Context#CONTEXT_locale} setting on bean context.
	 * 	<li>Locale returned by {@link Locale#getDefault()}.
	 * </ol>
	 *
	 * @see Context#CONTEXT_locale
	 * @return The session locale.
	 */
	public final Locale getLocale() {
		return locale;
	}

	/**
	 * Configuration property:  Media type.
	 *
	 * <p>
	 * For example, <js>"application/json"</js>.
	 *
	 * @see Context#CONTEXT_mediaType
	 * @return The media type for this session, or <jk>null</jk> if not specified.
	 */
	public final MediaType getMediaType() {
		return mediaType;
	}

	/**
	 * Configuration property:  Time zone.
	 *
	 * <p>
	 * The timezone is determined in the following order:
	 * <ol>
	 * 	<li><c>timeZone</c> parameter passed in through constructor.
	 * 	<li>{@link Context#CONTEXT_timeZone} entry in parameter passed in through constructor.
	 * 	<li>{@link Context#CONTEXT_timeZone} setting on bean context.
	 * </ol>
	 *
	 * @see Context#CONTEXT_timeZone
	 * @return The session timezone, or <jk>null</jk> if timezone not specified.
	 */
	public final TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * Configuration property:  Time zone.
	 *
	 * <p>
	 * The timezone is determined in the following order:
	 * <ol>
	 * 	<li><c>timeZone</c> parameter passed in through constructor.
	 * 	<li>{@link Context#CONTEXT_timeZone} entry in parameter passed in through constructor.
	 * 	<li>{@link Context#CONTEXT_timeZone} setting on bean context.
	 * </ol>
	 *
	 * @see Context#CONTEXT_timeZone
	 * @return The session timezone, or the system timezone if not specified.  Never <jk>null</jk>.
	 */
	public final ZoneId getTimeZoneId() {
		return timeZone == null ? ZoneId.systemDefault() : timeZone.toZoneId();
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Other methods
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Returns the properties defined on this bean context as a simple map for debugging purposes.
	 *
	 * @return A new map containing the properties defined on this context.
	 */
	public OMap toMap() {
		return new DefaultFilteringOMap();
	}

	@Override /* Object */
	public String toString() {
		return SimpleJsonSerializer.DEFAULT_READABLE.toString(toMap());
	}
}
