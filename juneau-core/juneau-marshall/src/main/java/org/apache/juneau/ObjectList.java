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

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.juneau.json.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.serializer.*;
import org.apache.juneau.utils.*;

/**
 * Java implementation of a JSON array.
 *
 * <p>
 * An extension of {@link LinkedList}, so all methods available to in that class are also available to this class.
 *
 * <p>
 * Note that the use of this class is optional.
 * The serializers will accept any objects that implement the {@link Collection} interface.
 * But this class provides some useful additional functionality when working with JSON models constructed from Java
 * Collections Framework objects.
 * For example, a constructor is provided for converting a JSON array string directly into a {@link List}.
 * It also contains accessor methods for to avoid common typecasting when accessing elements in a list.
 *
 * <h5 class='section'>Example:</h5>
 * <p class='bcode w800'>
 * 	<jc>// Construct an empty List</jc>
 * 	List l = <jk>new</jk> ObjectList();
 *
 * 	<jc>// Construct a list of objects using various methods</jc>
 * 	l = <jk>new</jk> ObjectList().append(<js>"foo"</js>).append(123).append(<jk>true</jk>);
 * 	l = <jk>new</jk> ObjectList().append(<js>"foo"</js>, 123, <jk>true</jk>);  <jc>// Equivalent</jc>
 * 	l = <jk>new</jk> ObjectList(<js>"foo"</js>, 123, <jk>true</jk>);  <jc>// Equivalent</jc>
 *
 * 	<jc>// Construct a list of integers from JSON</jc>
 * 	l = <jk>new</jk> ObjectList(<js>"[1,2,3]"</js>);
 *
 * 	<jc>// Construct a list of generic ObjectMap objects from JSON</jc>
 * 	l = <jk>new</jk> ObjectList(<js>"[{foo:'bar'},{baz:'bing'}]"</js>);
 *
 * 	<jc>// Construct a list of integers from XML</jc>
 * 	String xml = <js>"&lt;array&gt;&lt;number&gt;1&lt;/number&gt;&lt;number&gt;2&lt;/number&gt;&lt;number&gt;3&lt;/number&gt;&lt;/array&gt;"</js>;
 * 	l = <jk>new</jk> ObjectList(xml, DataFormat.<jsf>XML</jsf>);
 * 	l = (List)XmlParser.<jsf>DEFAULT</jsf>.parse(xml);  <jc>// Equivalent</jc>
 * 	l = (List)XmlParser.<jsf>DEFAULT</jsf>.parse(Object.<jk>class</jk>, xml);  <jc>// Equivalent</jc>
 * 	l = XmlParser.<jsf>DEFAULT</jsf>.parse(List.<jk>class</jk>, xml);  <jc>// Equivalent</jc>
 * 	l = XmlParser.<jsf>DEFAULT</jsf>.parse(ObjectList.<jk>class</jk>, xml);  <jc>// Equivalent</jc>
 *
 * 	<jc>// Construct JSON from ObjectList</jc>
 * 	l = <jk>new</jk> ObjectList(<js>"[{foo:'bar'},{baz:'bing'}]"</js>);
 * 	String json = l.toString();  <jc>// Produces "[{foo:'bar'},{baz:'bing'}]"</jc>
 * 	json = l.toString(JsonSerializer.<jsf>DEFAULT_CONDENSED</jsf>);  <jc>// Equivalent</jc>
 * 	json = JsonSerializer.<jsf>DEFAULT_CONDENSED</jsf>.serialize(l);  <jc>// Equivalent</jc>
 *
 * 	<jc>// Get one of the entries in the list as an Integer</jc>
 * 	l = <jk>new</jk> ObjectList(<js>"[1,2,3]"</js>);
 * 	Integer i = l.getInt(1);
 * 	i = l.get(Integer.<jk>class</jk>, 1);  <jc>// Equivalent</jc>
 *
 * 	<jc>// Get one of the entries in the list as an Float</jc>
 * 	l = <jk>new</jk> ObjectList(<js>"[1,2,3]"</js>);
 * 	Float f = l.getFloat(1); <jc>// Returns 2f </jc>
 * 	f = l.get(Float.<jk>class</jk>, 1);  <jc>// Equivalent</jc>
 *
 * 	<jc>// Same as above, except converted to a String</jc>
 * 	l = <jk>new</jk> ObjectList(<js>"[1,2,3]"</js>);
 * 	String s = l.getString(1); <jc>// Returns "2" </jc>
 * 	s = l.get(String.<jk>class</jk>, 1);  <jc>// Equivalent</jc>
 *
 * 	<jc>// Get one of the entries in the list as a bean (converted to a bean if it isn't already one)</jc>
 * 	l = <jk>new</jk> ObjectList(<js>"[{name:'John Smith',age:45}]"</js>);
 * 	Person p = l.get(Person.<jk>class</jk>, 0);
 *
 * 	<jc>// Iterate over a list of beans using the elements() method</jc>
 * 	ObjectList ObjectList = <jk>new</jk> ObjectList(<js>"[{name:'John Smith',age:45}]"</js>);
 * 	<jk>for</jk> (Person p : ObjectList.elements(Person.<jk>class</jk>) {
 * 		<jc>// Do something with p</jc>
 * 	}
 * </p>
 *
 * <p>
 * This class is not thread safe.
 */
public class ObjectList extends LinkedList<Object> {
	private static final long serialVersionUID = 1L;

	transient BeanSession session = null;
	private transient PojoRest pojoRest;

	/**
	 * An empty read-only ObjectList.
	 */
	public static final ObjectList EMPTY_LIST = new ObjectList() {
		private static final long serialVersionUID = 1L;

		@Override /* List */
		public void add(int location, Object object) {
			throw new UnsupportedOperationException();
		}

		@Override /* List */
		public ListIterator<Object> listIterator(final int location) {
			return Collections.emptyList().listIterator(location);
		}

		@Override /* List */
		public Object remove(int location) {
			throw new UnsupportedOperationException();
		}

		@Override /* List */
		public Object set(int location, Object object) {
			throw new UnsupportedOperationException();
		}

		@Override /* List */
		public List<Object> subList(int start, int end) {
			return Collections.emptyList().subList(start, end);
		}
	};

	/**
	 * Static constructor from JSON string.
	 *
	 * @param s JSON initialization string.  Can be <jk>null</jk>.
	 * @return A new {@link ObjectList} object, or <jk>null</jk> if the input is <jk>null</jk>.
	 * @throws ParseException Invalid JSON string.
	 */
	public static ObjectList parse(CharSequence s) throws ParseException {
		return s == null ? null : new ObjectList(s);
	}

	/**
	 * Construct a JSON array directly from text using the specified parser.
	 *
	 * @param s The string being parsed.
	 * @param p The parser to use to parse the input.
	 * @throws ParseException Malformed input encountered.
	 */
	public ObjectList(CharSequence s, Parser p) throws ParseException {
		this(p == null ? null : p.createBeanSession());
		if (p == null)
			p = JsonParser.DEFAULT;
		if (s != null)
			p.parseIntoCollection(s, this, bs2().object());
	}

	/**
	 * Shortcut for <code><jk>new</jk> ObjectList(String,JsonParser.<jsf>DEFAULT</jsf>);</code>
	 *
	 * @param s The string being parsed.
	 * @throws ParseException Malformed input encountered.
	 */
	public ObjectList(CharSequence s) throws ParseException {
		this(s, null);
	}

	/**
	 * Construct a JSON array directly from a reader using the specified parser.
	 *
	 * @param r
	 * 	The reader to read from.
	 * 	Will automatically be wrapped in a {@link BufferedReader} if it isn't already a BufferedReader.
	 * @param p The parser to use to parse the input.
	 * @throws ParseException Malformed input encountered.
	 * @throws IOException If a problem occurred trying to read from the reader.
	 */
	public ObjectList(Reader r, Parser p) throws ParseException, IOException {
		this(p == null ? null : p.createBeanSession());
		parseReader(r, p);
	}

	/**
	 * Shortcut for <code><jk>new</jk> ObjectList(reader, JsonParser.<jsf>DEFAULT</jsf>)</code>.
	 *
	 * @param r
	 * 	The reader to read from.
	 * 	The reader will be wrapped in a {@link BufferedReader} if it isn't already.
	 * @throws ParseException Malformed input encountered.
	 * @throws IOException If a problem occurred trying to read from the reader.
	 */
	public ObjectList(Reader r) throws ParseException, IOException {
		parseReader(r, JsonParser.DEFAULT);
	}

	private void parseReader(Reader r, Parser p) throws ParseException {
		if (p == null)
			p = JsonParser.DEFAULT;
		p.parseIntoCollection(r, this, bs2().object());
	}

	/**
	 * Construct an empty JSON array (an empty {@link LinkedList}).
	 */
	public ObjectList() {
	}

	/**
	 * Construct an empty JSON array (an empty {@link LinkedList}) with the specified bean context.
	 *
	 * @param session The bean context to associate with this object list for creating beans.
	 */
	public ObjectList(BeanSession session) {
		super();
		this.session = session;
	}

	/**
	 * Construct a JSON array and fill it with the specified objects.
	 *
	 * @param o A list of objects to add to this list.
	 */
	public ObjectList(Object... o) {
		super();
		Collections.addAll(this, o);
	}

	/**
	 * Construct a JSON array and fill it with the specified collection of objects.
	 *
	 * @param c A list of objects to add to this list.
	 */
	public ObjectList(Collection<?> c) {
		super(c);
	}

	/**
	 * Override the default bean session used for converting POJOs.
	 *
	 * <p>
	 * Default is {@link BeanContext#DEFAULT}, which is sufficient in most cases.
	 *
	 * <p>
	 * Useful if you're serializing/parsing beans with transforms defined.
	 *
	 * @param session The new bean session.
	 * @return This object (for method chaining).
	 */
	public ObjectList setBeanSession(BeanSession session) {
		this.session = session;
		return this;
	}

	/**
	 * Returns the {@link BeanSession} currently associated with this list.
	 *
	 * @return The {@link BeanSession} currently associated with this list.
	 */
	public BeanSession getBeanSession() {
		return session;
	}

	/**
	 * Convenience method for adding multiple objects to this list.
	 *
	 * @param o The objects to add to the list.
	 * @return This object (for method chaining).
	 */
	public ObjectList append(Object...o) {
		for (Object o2 : o)
			add(o2);
		return this;
	}

	/**
	 * Convenience method for appending another list to this list.
	 *
	 * @param l
	 * 	The list containing the elements to append to this list.
	 * 	<br>Can be <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public ObjectList appendAll(ObjectList l) {
		if (l != null)
			addAll(l);
		return this;
	}

	/**
	 * Convenience method for adding multiple objects to this list.
	 *
	 * <p>
	 * <jk>null</jk> and empty strings are skipped.
	 *
	 * @param o The objects to add to the list.
	 * @return This object (for method chaining).
	 */
	public ObjectList appendIfNotEmpty(String...o) {
		for (String s : o)
			if (isNotEmpty(s))
				add(s);
		return this;
	}

	/**
	 * Convenience method for adding multiple objects to this list.
	 *
	 * <p>
	 * <jk>null</jk> values are skipped.
	 *
	 * @param o The objects to add to the list.
	 * @return This object (for method chaining).
	 */
	public ObjectList appendIfNotNull(Object...o) {
		for (Object o2 : o)
			if (o2 != null)
				add(o2);
		return this;
	}

	/**
	 * Get the entry at the specified index, converted to the specified type.
	 *
	 * <p>
	 * This is the preferred get method for simple types.
	 *
	 * <h5 class='section'>Examples:</h5>
	 * <p class='bcode w800'>
	 * 	ObjectList l = <jk>new</jk> ObjectList(<js>"..."</js>);
	 *
	 * 	<jc>// Value converted to a string.</jc>
	 * 	String s = l.get(1, String.<jk>class</jk>);
	 *
	 * 	<jc>// Value converted to a bean.</jc>
	 * 	MyBean b = l.get(2, MyBean.<jk>class</jk>);
	 *
	 * 	<jc>// Value converted to a bean array.</jc>
	 * 	MyBean[] ba = l.get(3, MyBean[].<jk>class</jk>);
	 *
	 * 	<jc>// Value converted to a linked-list of objects.</jc>
	 * 	List l1 = l.get(4, LinkedList.<jk>class</jk>);
	 *
	 * 	<jc>// Value converted to a map of object keys/values.</jc>
	 * 	Map m1 = l.get(5, TreeMap.<jk>class</jk>);
	 * </p>
	 *
	 * <p>
	 * See {@link BeanSession#convertToType(Object, ClassMeta)} for the list of valid data conversions.
	 *
	 * @param index The index into this list.
	 * @param type The type of object to convert the entry to.
	 * @param <T> The type of object to convert the entry to.
	 * @return The converted entry.
	 */
	public <T> T get(int index, Class<T> type) {
		return bs2().convertToType(get(index), type);
	}

	/**
	 * Get the entry at the specified index, converted to the specified type.
	 *
	 * <p>
	 * The type can be a simple type (e.g. beans, strings, numbers) or parameterized type (collections/maps).
	 *
	 * <h5 class='section'>Examples:</h5>
	 * <p class='bcode w800'>
	 * 	ObjectList l = <jk>new</jk> ObjectList(<js>"..."</js>);
	 *
	 * 	<jc>// Value converted to a linked-list of strings.</jc>
	 * 	List&lt;String&gt; l1 = l.get(1, LinkedList.<jk>class</jk>, String.<jk>class</jk>);
	 *
	 * 	<jc>// Value converted to a linked-list of beans.</jc>
	 * 	List&lt;MyBean&gt; l2 = l.get(2, LinkedList.<jk>class</jk>, MyBean.<jk>class</jk>);
	 *
	 * 	<jc>// Value converted to a linked-list of linked-lists of strings.</jc>
	 * 	List&lt;List&lt;String&gt;&gt; l3 = l.get(3, LinkedList.<jk>class</jk>, LinkedList.<jk>class</jk>, String.<jk>class</jk>);
	 *
	 * 	<jc>// Value converted to a map of string keys/values.</jc>
	 * 	Map&lt;String,String&gt; m1 = l.get(4, TreeMap.<jk>class</jk>, String.<jk>class</jk>, String.<jk>class</jk>);
	 *
	 * 	<jc>// Value converted to a map containing string keys and values of lists containing beans.</jc>
	 * 	Map&lt;String,List&lt;MyBean&gt;&gt; m2 = l.get(5, TreeMap.<jk>class</jk>, String.<jk>class</jk>, List.<jk>class</jk>, MyBean.<jk>class</jk>);
	 * </p>
	 *
	 * <p>
	 * <c>Collection</c> classes are assumed to be followed by zero or one objects indicating the element type.
	 *
	 * <p>
	 * <c>Map</c> classes are assumed to be followed by zero or two meta objects indicating the key and value types.
	 *
	 * <p>
	 * The array can be arbitrarily long to indicate arbitrarily complex data structures.
	 *
	 * <p>
	 * See {@link BeanSession#convertToType(Object, ClassMeta)} for the list of valid data conversions.
	 *
	 * @param index The index into this list.
	 * @param type The type of object to convert the entry to.
	 * @param args The type arguments of the type to convert the entry to.
	 * @param <T> The type of object to convert the entry to.
	 * @return The converted entry.
	 */
	public <T> T get(int index, Type type, Type...args) {
		return bs2().convertToType(get(index), type, args);
	}

	/**
	 * Shortcut for calling <code>get(index, String.<jk>class</jk>)</code>.
	 *
	 * @param index The index.
	 * @return The converted value.
	 */
	public String getString(int index) {
		return get(index, String.class);
	}

	/**
	 * Shortcut for calling <code>get(index, Integer.<jk>class</jk>)</code>.
	 *
	 * @param index The index.
	 * @return The converted value.
	 * @throws InvalidDataConversionException If value cannot be converted.
	 */
	public Integer getInt(int index) {
		return get(index, Integer.class);
	}

	/**
	 * Shortcut for calling <code>get(index, Boolean.<jk>class</jk>)</code>.
	 *
	 * @param index The index.
	 * @return The converted value.
	 * @throws InvalidDataConversionException If value cannot be converted.
	 */
	public Boolean getBoolean(int index) {
		return get(index, Boolean.class);
	}

	/**
	 * Shortcut for calling <code>get(index, Long.<jk>class</jk>)</code>.
	 *
	 * @param index The index.
	 * @return The converted value.
	 * @throws InvalidDataConversionException If value cannot be converted.
	 */
	public Long getLong(int index) {
		return get(index, Long.class);
	}

	/**
	 * Shortcut for calling <code>get(index, Map.<jk>class</jk>)</code>.
	 *
	 * @param index The index.
	 * @return The converted value.
	 * @throws InvalidDataConversionException If value cannot be converted.
	 */
	public Map<?,?> getMap(int index) {
		return get(index, Map.class);
	}

	/**
	 * Same as {@link #getMap(int)} except converts the keys and values to the specified types.
	 *
	 * @param index The index.
	 * @param keyType The key type class.
	 * @param valType The value type class.
	 * @return The converted value.
	 * @throws InvalidDataConversionException If value cannot be converted.
	 */
	public <K,V> Map<K,V> getMap(int index, Class<K> keyType, Class<V> valType) {
		return bs2().convertToType(get(index), Map.class, keyType, valType);
	}

	/**
	 * Shortcut for calling <code>get(index, List.<jk>class</jk>)</code>.
	 *
	 * @param index The index.
	 * @return The converted value.
	 * @throws InvalidDataConversionException If value cannot be converted.
	 */
	public List<?> getList(int index) {
		return get(index, List.class);
	}

	/**
	 * Same as {@link #getList(int)} except converts the elements to the specified types.
	 *
	 * @param index The index.
	 * @param elementType The element type class.
	 * @return The converted value.
	 * @throws InvalidDataConversionException If value cannot be converted.
	 */
	public <E> List<E> getList(int index, Class<E> elementType) {
		return bs2().convertToType(get(index), List.class, elementType);
	}

	/**
	 * Shortcut for calling <code>get(index, ObjectMap.<jk>class</jk>)</code>.
	 *
	 * @param index The index.
	 * @return The converted value.
	 * @throws InvalidDataConversionException If value cannot be converted.
	 */
	public ObjectMap getObjectMap(int index) {
		return get(index, ObjectMap.class);
	}

	/**
	 * Shortcut for calling <code>get(index, ObjectList.<jk>class</jk>)</code>.
	 *
	 * @param index The index.
	 * @return The converted value.
	 * @throws InvalidDataConversionException If value cannot be converted.
	 */
	public ObjectList getObjectList(int index) {
		return get(index, ObjectList.class);
	}

	/**
	 * Same as {@link #get(int,Class) get(int,Class)}, but the key is a slash-delimited path used to traverse entries in
	 * this POJO.
	 *
	 * <p>
	 * For example, the following code is equivalent:
	 * </p>
	 * <p class='bcode w800'>
	 * 	ObjectMap m = getObjectMap();
	 *
	 * 	<jc>// Long way</jc>
	 * 	<jk>long</jk> l = m.getObjectMap(<js>"foo"</js>).getObjectList(<js>"bar"</js>).getObjectMap(<js>"0"</js>).getLong(<js>"baz"</js>);
	 *
	 * 	<jc>// Using this method</jc>
	 * 	<jk>long</jk> l = m.getAt(<js>"foo/bar/0/baz"</js>, <jk>long</jk>.<jk>class</jk>);
	 * </p>
	 *
	 * <p>
	 * This method uses the {@link PojoRest} class to perform the lookup, so the map can contain any of the various
	 * class types that the {@link PojoRest} class supports (e.g. beans, collections, arrays).
	 *
	 * @param path The path to the entry.
	 * @param type The class type.
	 *
	 * @param <T> The class type.
	 * @return The value, or <jk>null</jk> if the entry doesn't exist.
	 */
	public <T> T getAt(String path, Class<T> type) {
		return getPojoRest().get(path, type);
	}

	/**
	 * Same as {@link #getAt(String,Class)}, but allows for conversion to complex maps and collections.
	 *
	 * @param path The path to the entry.
	 * @param type The class type.
	 * @param args The class parameter types.
	 *
	 * @param <T> The class type.
	 * @return The value, or <jk>null</jk> if the entry doesn't exist.
	 */
	public <T> T getAt(String path, Type type, Type...args) {
		return getPojoRest().get(path, type, args);
	}

	/**
	 * Same as {@link #set(int,Object) set(int,Object)}, but the key is a slash-delimited path used to traverse entries
	 * in this POJO.
	 *
	 * <p>
	 * For example, the following code is equivalent:
	 * </p>
	 * <p class='bcode w800'>
	 * 	ObjectMap m = getObjectMap();
	 *
	 * 	<jc>// Long way</jc>
	 * 	m.getObjectMap(<js>"foo"</js>).getObjectList(<js>"bar"</js>).getObjectMap(<js>"0"</js>).put(<js>"baz"</js>, 123);
	 *
	 * 	<jc>// Using this method</jc>
	 * 	m.putAt(<js>"foo/bar/0/baz"</js>, 123);
	 * </p>
	 *
	 * <p>
	 * This method uses the {@link PojoRest} class to perform the lookup, so the map can contain any of the various
	 * class types that the {@link PojoRest} class supports (e.g. beans, collections, arrays).
	 *
	 * @param path The path to the entry.
	 * @param o The new value.
	 * @return The previous value, or <jk>null</jk> if the entry doesn't exist.
	 */
	public Object putAt(String path, Object o) {
		return getPojoRest().put(path, o);
	}

	/**
	 * Similar to {@link #putAt(String,Object) putAt(String,Object)}, but used to append to collections and arrays.
	 *
	 * <p>
	 * For example, the following code is equivalent:
	 * </p>
	 * <p class='bcode w800'>
	 * 	ObjectMap m = getObjectMap();
	 *
	 * 	<jc>// Long way</jc>
	 * 	m.getObjectMap(<js>"foo"</js>).getObjectList(<js>"bar"</js>).append(123);
	 *
	 * 	<jc>// Using this method</jc>
	 * 	m.postAt(<js>"foo/bar"</js>, 123);
	 * </p>
	 *
	 * <p>
	 * This method uses the {@link PojoRest} class to perform the lookup, so the map can contain any of the various
	 * class types that the {@link PojoRest} class supports (e.g. beans, collections, arrays).
	 *
	 * @param path The path to the entry.
	 * @param o The new value.
	 * @return The previous value, or <jk>null</jk> if the entry doesn't exist.
	 */
	public Object postAt(String path, Object o) {
		return getPojoRest().post(path, o);
	}

	/**
	 * Similar to {@link #remove(int) remove(int)},but the key is a slash-delimited path used to traverse entries in
	 * this POJO.
	 *
	 * <p>
	 * For example, the following code is equivalent:
	 * </p>
	 * <p class='bcode w800'>
	 * 	ObjectMap m = getObjectMap();
	 *
	 * 	<jc>// Long way</jc>
	 * 	m.getObjectMap(<js>"foo"</js>).getObjectList(<js>"bar"</js>).getObjectMap(1).remove(<js>"baz"</js>);
	 *
	 * 	<jc>// Using this method</jc>
	 * 	m.deleteAt(<js>"foo/bar/0/baz"</js>);
	 * </p>
	 *
	 * <p>
	 * This method uses the {@link PojoRest} class to perform the lookup, so the map can contain any of the various
	 * class types that the {@link PojoRest} class supports (e.g. beans, collections, arrays).
	 *
	 * @param path The path to the entry.
	 * @return The previous value, or <jk>null</jk> if the entry doesn't exist.
	 */
	public Object deleteAt(String path) {
		return getPojoRest().delete(path);
	}

	/**
	 * Creates an {@link Iterable} with elements of the specified child type.
	 *
	 * <p>
	 * Attempts to convert the child objects to the correct type if they aren't already the correct type.
	 *
	 * <p>
	 * The <c>next()</c> method on the returned iterator may throw a {@link InvalidDataConversionException} if
	 * the next element cannot be converted to the specified type.
	 *
	 * <p>
	 * See {@link BeanSession#convertToType(Object, ClassMeta)} for a description of valid conversions.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Iterate over a list of ObjectMaps.</jc>
	 * 	ObjectList l = <jk>new</jk> ObjectList(<js>"[{foo:'bar'},{baz:123}]"</js>);
	 * 	for (ObjectMap m : l.elements(ObjectMap.<jk>class</jk>)) {
	 * 		<jc>// Do something with m.</jc>
	 * 	}
	 *
	 * 	<jc>// Iterate over a list of ints.</jc>
	 * 	ObjectList l = <jk>new</jk> ObjectList(<js>"[1,2,3]"</js>);
	 * 	for (Integer i : l.elements(Integer.<jk>class</jk>)) {
	 * 		<jc>// Do something with i.</jc>
	 * 	}
	 *
	 * 	<jc>// Iterate over a list of beans.</jc>
	 * 	<jc>// Automatically converts to beans.</jc>
	 * 	ObjectList l = <jk>new</jk> ObjectList(<js>"[{name:'John Smith',age:45}]"</js>);
	 * 	for (Person p : l.elements(Person.<jk>class</jk>)) {
	 * 		<jc>// Do something with p.</jc>
	 * 	}
	 * </p>
	 *
	 * @param <E> The child object type.
	 * @param childType The child object type.
	 * @return A new <c>Iterable</c> object over this list.
	 */
	public <E> Iterable<E> elements(final Class<E> childType) {
		final Iterator<?> i = iterator();
		return new Iterable<E>() {

			@Override /* Iterable */
			public Iterator<E> iterator() {
				return new Iterator<E>() {

					@Override /* Iterator */
					public boolean hasNext() {
						return i.hasNext();
					}

					@Override /* Iterator */
					public E next() {
						return bs2().convertToType(i.next(), childType);
					}

					@Override /* Iterator */
					public void remove() {
						i.remove();
					}

				};
			}
		};
	}

	/**
	 * Returns the {@link ClassMeta} of the class of the object at the specified index.
	 *
	 * @param index An index into this list, zero-based.
	 * @return The data type of the object at the specified index, or <jk>null</jk> if the value is null.
	 */
	public ClassMeta<?> getClassMeta(int index) {
		return bs2().getClassMetaForObject(get(index));
	}

	private PojoRest getPojoRest() {
		if (pojoRest == null)
			pojoRest = new PojoRest(this);
		return pojoRest;
	}

	/**
	 * Serialize this array to a string using the specified serializer.
	 *
	 * @param serializer The serializer to use to convert this object to a string.
	 * @return This object as a serialized string.
	 * @throws SerializeException If a problem occurred trying to convert the output.
	 */
	public String toString(WriterSerializer serializer) throws SerializeException {
		return serializer.serialize(this);
	}

	/**
	 * Returns <jk>true</jk> if this list is unmodifiable.
	 *
	 * @return <jk>true</jk> if this list is unmodifiable.
	 */
	public boolean isUnmodifiable() {
		return false;
	}

	/**
	 * Returns a modifiable copy of this list if it's unmodifiable.
	 *
	 * @return A modifiable copy of this list if it's unmodifiable, or this list if it is already modifiable.
	 */
	public ObjectList modifiable() {
		if (isUnmodifiable())
			return new ObjectList(this);
		return this;
	}

	/**
	 * Returns an unmodifiable copy of this list if it's modifiable.
	 *
	 * @return An unmodifiable copy of this list if it's modifiable, or this list if it is already unmodifiable.
	 */
	public ObjectList unmodifiable() {
		if (this instanceof UnmodifiableObjectList)
			return this;
		return new UnmodifiableObjectList(this);
	}

	/**
	 * Serialize this array to JSON using the {@link JsonSerializer#DEFAULT} serializer.
	 */
	@Override /* Object */
	public String toString() {
		try {
			return this.toString(SimpleJsonSerializer.DEFAULT);
		} catch (SerializeException e) {
			return e.getLocalizedMessage();
		}
	}

	/**
	 * Convenience method for serializing this ObjectList to the specified Writer using the JsonSerializer.DEFAULT
	 * serializer.
	 *
	 * @param w The writer to send the serialized contents of this object.
	 * @throws IOException If a problem occurred trying to write to the writer.
	 * @throws SerializeException If a problem occurred trying to convert the output.
	 */
	public void serializeTo(Writer w) throws IOException, SerializeException {
		JsonSerializer.DEFAULT.serialize(this);
	}

	/**
	 * Converts this object into the specified class type.
	 *
	 * <p>
	 * TODO - The current implementation is very inefficient.
	 *
	 * @param cm The class type to convert this object to.
	 * @return A converted object.
	 */
	public Object cast(ClassMeta<?> cm) {
		try {
			return JsonParser.DEFAULT.parse(SimpleJsonSerializer.DEFAULT.serialize(this), cm);
		} catch (ParseException | SerializeException e) {
			throw new RuntimeException(e);
		}
	}

	private static final class UnmodifiableObjectList extends ObjectList {
		private static final long serialVersionUID = 1L;

		UnmodifiableObjectList(ObjectList contents) {
			super();
			if (contents != null) {
				for (Object e : this) {
					super.add(e);
				}
			}
		}

		@Override /* List */
		public void add(int location, Object object) {
			throw new UnsupportedOperationException("ObjectList is read-only.");
		}

		@Override /* List */
		public Object remove(int location) {
			throw new UnsupportedOperationException("ObjectList is read-only.");
		}

		@Override /* List */
		public Object set(int location, Object object) {
			throw new UnsupportedOperationException("ObjectList is read-only.");
		}

		@Override
		public final boolean isUnmodifiable() {
			return true;
		}
	}

	BeanSession bs2() {
		if (session == null)
			session = BeanContext.DEFAULT.createBeanSession();
		return session;
	}
}
