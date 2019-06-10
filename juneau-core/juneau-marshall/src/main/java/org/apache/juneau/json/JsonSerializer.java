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
package org.apache.juneau.json;

import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.serializer.*;

/**
 * Serializes POJO models to JSON.
 *
 * <h5 class='topic'>Media types</h5>
 *
 * Handles <code>Accept</code> types:  <code><b>application/json, text/json</b></code>
 * <p>
 * Produces <code>Content-Type</code> types:  <code><b>application/json</b></code>
 *
 * <h5 class='topic'>Description</h5>
 *
 * The conversion is as follows...
 * <ul class='spaced-list'>
 * 	<li>
 * 		Maps (e.g. {@link HashMap HashMaps}, {@link TreeMap TreeMaps}) are converted to JSON objects.
 * 	<li>
 * 		Collections (e.g. {@link HashSet HashSets}, {@link LinkedList LinkedLists}) and Java arrays are converted to
 * 		JSON arrays.
 * 	<li>
 * 		{@link String Strings} are converted to JSON strings.
 * 	<li>
 * 		{@link Number Numbers} (e.g. {@link Integer}, {@link Long}, {@link Double}) are converted to JSON numbers.
 * 	<li>
 * 		{@link Boolean Booleans} are converted to JSON booleans.
 * 	<li>
 * 		{@code nulls} are converted to JSON nulls.
 * 	<li>
 * 		{@code arrays} are converted to JSON arrays.
 * 	<li>
 * 		{@code beans} are converted to JSON objects.
 * </ul>
 *
 * <p>
 * The types above are considered "JSON-primitive" object types.
 * Any non-JSON-primitive object types are transformed into JSON-primitive object types through
 * {@link org.apache.juneau.transform.PojoSwap PojoSwaps} associated through the
 * {@link BeanContextBuilder#pojoSwaps(Class...)} method.
 * Several default transforms are provided for transforming Dates, Enums, Iterators, etc...
 *
 * <p>
 * This serializer provides several serialization options.
 * Typically, one of the predefined DEFAULT serializers will be sufficient.
 * However, custom serializers can be constructed to fine-tune behavior.
 *
 * <h5 class='topic'>Behavior-specific subclasses</h5>
 *
 * The following direct subclasses are provided for convenience:
 * <ul class='spaced-list'>
 * 	<li>
 * 		{@link SimpleJsonSerializer} - Default serializer, single quotes, simple mode.
 * 	<li>
 * 		{@link SimpleJsonSerializer.Readable} - Default serializer, single quotes, simple mode, with whitespace.
 * </ul>
 *
 * <h5 class='section'>Example:</h5>
 * <p class='bcode w800'>
 * 	<jc>// Use one of the default serializers to serialize a POJO</jc>
 * 	String json = JsonSerializer.<jsf>DEFAULT</jsf>.serialize(someObject);
 *
 * 	<jc>// Create a custom serializer for lax syntax using single quote characters</jc>
 * 	JsonSerializer serializer = JsonSerializer.<jsm>create</jsm>().simple().sq().build();
 *
 * 	<jc>// Clone an existing serializer and modify it to use single-quotes</jc>
 * 	JsonSerializer serializer = JsonSerializer.<jsf>DEFAULT</jsf>.builder().sq().build();
 *
 * 	<jc>// Serialize a POJO to JSON</jc>
 * 	String json = serializer.serialize(someObject);
 * </p>
 */
@ConfigurableContext
public class JsonSerializer extends WriterSerializer {

	//-------------------------------------------------------------------------------------------------------------------
	// Configurable properties
	//-------------------------------------------------------------------------------------------------------------------

	static final String PREFIX = "JsonSerializer";

	/**
	 * Configuration property:  Add <js>"_type"</js> properties when needed.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"JsonSerializer.addBeanTypes.b"</js>
	 * 	<li><b>Data type:</b>  <code>Boolean</code>
	 * 	<li><b>Default:</b>  <jk>false</jk>
	 * 	<li><b>Session property:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link JsonSerializerBuilder#addBeanTypes(boolean)}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * If <jk>true</jk>, then <js>"_type"</js> properties will be added to beans if their type cannot be inferred
	 * through reflection.
	 *
	 * <p>
	 * When present, this value overrides the {@link #SERIALIZER_addBeanTypes} setting and is
	 * provided to customize the behavior of specific serializers in a {@link SerializerGroup}.
	 */
	public static final String JSON_addBeanTypes = PREFIX + ".addBeanTypes.b";

	/**
	 * Configuration property:  Prefix solidus <js>'/'</js> characters with escapes.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"JsonSerializer.escapeSolidus.b"</js>
	 * 	<li><b>Data type:</b>  <code>Boolean</code>
	 * 	<li><b>Default:</b>  <jk>false</jk>
	 * 	<li><b>Session property:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link JsonSerializerBuilder#escapeSolidus(boolean)}
	 * 			<li class='jm'>{@link JsonSerializerBuilder#escapeSolidus()}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * If <jk>true</jk>, solidus (e.g. slash) characters should be escaped.
	 * The JSON specification allows for either format.
	 * <br>However, if you're embedding JSON in an HTML script tag, this setting prevents confusion when trying to serialize
	 * <xt>&lt;\/script&gt;</xt>.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Create a JSON serializer that escapes solidus characters.</jc>
	 * 	WriterSerializer s = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.simple()
	 * 		.escapeSolidus()
	 * 		.build();
	 *
	 * 	<jc>// Same, but use property.</jc>
	 * 	WriterSerializer s = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.simple()
	 * 		.set(<jsf>JSON_escapeSolidus</jsf>, <jk>true</jk>)
	 * 		.build();
	 *
	 * 	<jc>// Produces: "{foo:'&lt;\/bar&gt;'"</jc>
	 * 	String json = s.serialize(<jk>new</jk> ObjectMap().append(<js>"foo"</js>, <js>"&lt;/bar&gt;"</js>);
	 * </p>
	 */
	public static final String JSON_escapeSolidus = PREFIX + ".escapeSolidus.b";

	/**
	 * Configuration property:  Simple JSON mode.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"JsonSerializer.simpleMode.b"</js>
	 * 	<li><b>Data type:</b>  <code>Boolean</code>
	 * 	<li><b>Default:</b>  <jk>false</jk>
	 * 	<li><b>Session property:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link JsonSerializerBuilder#simple(boolean)}
	 * 			<li class='jm'>{@link JsonSerializerBuilder#simple()}
	 * 			<li class='jm'>{@link JsonSerializerBuilder#ssq()}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * If <jk>true</jk>, JSON attribute names will only be quoted when necessary.
	 * <br>Otherwise, they are always quoted.
	 *
	 * <p>
	 * Attributes do not need to be quoted when they conform to the following:
	 * <ol class='spaced-list'>
	 * 	<li>They start with an ASCII character or <js>'_'</js>.
	 * 	<li>They contain only ASCII characters or numbers or <js>'_'</js>.
	 * 	<li>They are not one of the following reserved words:
	 * 		<p class='bcode w800'>
	 * 	arguments, break, case, catch, class, const, continue, debugger, default,
	 * 	delete, do, else, enum, eval, export, extends, false, finally, for, function,
	 * 	if, implements, import, in, instanceof, interface, let, new, null, package,
	 * 	private, protected, public, return, static, super, switch, this, throw,
	 * 	true, try, typeof, var, void, while, with, undefined, yield
	 * 		</p>
	 * </ol>
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Create a JSON serializer in normal mode.</jc>
	 * 	WriterSerializer s1 = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.build();
	 *
	 * 	<jc>// Create a JSON serializer in simple mode.</jc>
	 * 	WriterSerializer s2 = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.simple()
	 * 		.build();
	 *
	 * 	ObjectMap m = <jk>new</jk> ObjectMap()
	 * 		.append(<js>"foo"</js>, <js>"x1"</js>)
	 * 		.append(<js>"_bar"</js>, <js>"x2"</js>)
	 * 		.append(<js>" baz "</js>, <js>"x3"</js>)
	 * 		.append(<js>"123"</js>, <js>"x4"</js>)
	 * 		.append(<js>"return"</js>, <js>"x5"</js>);
	 * 		.append(<js>""</js>, <js>"x6"</js>);
	 *
	 * 	<jc>// Produces:</jc>
	 * 	<jc>// {</jc>
	 * 	<jc>// 	"foo": "x1"</jc>
	 * 	<jc>// 	"_bar": "x2"</jc>
	 * 	<jc>// 	" baz ": "x3"</jc>
	 * 	<jc>// 	"123": "x4"</jc>
	 * 	<jc>// 	"return": "x5"</jc>
	 * 	<jc>// 	"": "x6"</jc>
	 * 	<jc>// }</jc>
	 * 	String json1 = s1.serialize(m);
	 *
	 * 	<jc>// Produces:</jc>
	 * 	<jc>// {</jc>
	 * 	<jc>// 	foo: "x1"</jc>
	 * 	<jc>// 	_bar: "x2"</jc>
	 * 	<jc>// 	" baz ": "x3"</jc>
	 * 	<jc>// 	"123": "x4"</jc>
	 * 	<jc>// 	"return": "x5"</jc>
	 * 	<jc>// 	"": "x6"</jc>
	 * 	<jc>// }</jc>
	 * 	String json2 = s2.serialize(m);
	 * </p>
	 */
	public static final String JSON_simpleMode = PREFIX + ".simpleMode.b";

	//-------------------------------------------------------------------------------------------------------------------
	// Predefined instances
	//-------------------------------------------------------------------------------------------------------------------

	/** Default serializer, all default settings.*/
	public static final JsonSerializer DEFAULT = new JsonSerializer(PropertyStore.DEFAULT);

	/** Default serializer, all default settings.*/
	public static final JsonSerializer DEFAULT_READABLE = new Readable(PropertyStore.DEFAULT);


	//-------------------------------------------------------------------------------------------------------------------
	// Predefined subclasses
	//-------------------------------------------------------------------------------------------------------------------

	/** Default serializer, with whitespace. */
	public static class Readable extends JsonSerializer {

		/**
		 * Constructor.
		 *
		 * @param ps The property store containing all the settings for this object.
		 */
		public Readable(PropertyStore ps) {
			super(
				ps.builder().set(WSERIALIZER_useWhitespace, true).build()
			);
		}
	}

	/**
	 * Default serializer, single quotes, simple mode, with whitespace and recursion detection.
	 * Note that recursion detection introduces a small performance penalty.
	 */
	public static class ReadableSafe extends JsonSerializer {

		/**
		 * Constructor.
		 *
		 * @param ps The property store containing all the settings for this object.
		 */
		public ReadableSafe(PropertyStore ps) {
			super(
				ps.builder()
					.set(JSON_simpleMode, true)
					.set(WSERIALIZER_quoteChar, '\'')
					.set(WSERIALIZER_useWhitespace, true)
					.set(BEANTRAVERSE_detectRecursions, true)
					.build()
			);
		}
	}


	//-------------------------------------------------------------------------------------------------------------------
	// Instance
	//-------------------------------------------------------------------------------------------------------------------

	private final boolean
		simpleMode,
		escapeSolidus,
		addBeanTypes;

	private volatile JsonSchemaSerializer schemaSerializer;

	/**
	 * Constructor.
	 *
	 * @param ps
	 * 	The property store containing all the settings for this object.
	 */
	public JsonSerializer(PropertyStore ps) {
		this(ps, "application/json", "application/json,text/json");
	}

	/**
	 * Constructor.
	 *
	 * @param ps
	 * 	The property store containing all the settings for this object.
	 * @param produces
	 * 	The media type that this serializer produces.
	 * @param accept
	 * 	The accept media types that the serializer can handle.
	 * 	<p>
	 * 	Can contain meta-characters per the <code>media-type</code> specification of {@doc RFC2616.section14.1}
	 * 	<p>
	 * 	If empty, then assumes the only media type supported is <code>produces</code>.
	 * 	<p>
	 * 	For example, if this serializer produces <js>"application/json"</js> but should handle media types of
	 * 	<js>"application/json"</js> and <js>"text/json"</js>, then the arguments should be:
	 * 	<p class='bcode w800'>
	 * 	<jk>super</jk>(ps, <js>"application/json"</js>, <js>"application/json,text/json"</js>);
	 * 	</p>
	 * 	<br>...or...
	 * 	<p class='bcode w800'>
	 * 	<jk>super</jk>(ps, <js>"application/json"</js>, <js>"*&#8203;/json"</js>);
	 * 	</p>
	 * <p>
	 * The accept value can also contain q-values.
	 */
	public JsonSerializer(PropertyStore ps, String produces, String accept) {
		super(ps, produces, accept);

		simpleMode = getBooleanProperty(JSON_simpleMode, false);
		escapeSolidus = getBooleanProperty(JSON_escapeSolidus, false);
		addBeanTypes = getBooleanProperty(JSON_addBeanTypes, getBooleanProperty(SERIALIZER_addBeanTypes, false));
	}

	@Override /* Context */
	public JsonSerializerBuilder builder() {
		return new JsonSerializerBuilder(getPropertyStore());
	}

	/**
	 * Instantiates a new clean-slate {@link JsonSerializerBuilder} object.
	 *
	 * <p>
	 * This is equivalent to simply calling <code><jk>new</jk> JsonSerializerBuilder()</code>.
	 *
	 * @return A new {@link JsonSerializerBuilder} object.
	 */
	public static JsonSerializerBuilder create() {
		return new JsonSerializerBuilder();
	}

	/**
	 * Returns the schema serializer based on the settings of this serializer.
	 *
	 * <p>
	 * Note that this method creates a builder initialized to all default settings, whereas {@link #builder()} copies
	 * the settings of the object called on.
	 *
	 * @return The schema serializer.
	 */
	public JsonSchemaSerializer getSchemaSerializer() {
		if (schemaSerializer == null)
			schemaSerializer = builder().build(JsonSchemaSerializer.class);
		return schemaSerializer;
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Entry point methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* Context */
	public JsonSerializerSession createSession() {
		return createSession(createDefaultSessionArgs());
	}

	@Override /* Serializer */
	public JsonSerializerSession createSession(SerializerSessionArgs args) {
		return new JsonSerializerSession(this, args);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Configuration property:  Add <js>"_type"</js> properties when needed.
	 *
	 * @see #JSON_addBeanTypes
	 * @return
	 * 	<jk>true</jk> if <js>"_type"</js> properties will be added to beans if their type cannot be inferred
	 * 	through reflection.
	 */
	@Override
	protected final boolean isAddBeanTypes() {
		return addBeanTypes;
	}

	/**
	 * Configuration property:  Prefix solidus <js>'/'</js> characters with escapes.
	 *
	 * @see #JSON_escapeSolidus
	 * @return
	 * 	<jk>true</jk> if solidus (e.g. slash) characters should be escaped.
	 */
	protected final boolean isEscapeSolidus() {
		return escapeSolidus;
	}

	/**
	 * Configuration property:  Simple JSON mode.
	 *
	 * @see #JSON_simpleMode
	 * @return
	 * 	<jk>true</jk> if JSON attribute names will only be quoted when necessary.
	 * 	<br>Otherwise, they are always quoted.
	 */
	protected final boolean isSimpleMode() {
		return simpleMode;
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Other methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* Context */
	public ObjectMap asMap() {
		return super.asMap()
			.append("JsonSerializer", new DefaultFilteringObjectMap()
				.append("simpleMode", simpleMode)
				.append("escapeSolidus", escapeSolidus)
				.append("addBeanTypes", addBeanTypes)
			);
	}
}