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
package org.apache.juneau.assertions;

import java.io.*;
import java.util.function.*;

import org.apache.juneau.internal.*;
import org.apache.juneau.serializer.*;

/**
 * Used for assertion calls against throwable objects.
 *
 * <h5 class='section'>Example:</h5>
 * <p class='bjava'>
 * 	<jc>// Validates the throwable message or one of the parent messages contain 'Foobar'.</jc>
 * 	<jsm>assertThrowable</jsm>(<jv>throwable</jv>).contains(<js>"Foobar"</js>);
 * </p>
 *
 *
 * <h5 class='topic'>Test Methods</h5>
 * <p>
 * <ul class='javatree'>
 * 	<li class='jc'>{@link FluentObjectAssertion}
 * 	<ul class='javatreec'>
 * 		<li class='jm'>{@link FluentObjectAssertion#isExists() isExists()}
 * 		<li class='jm'>{@link FluentObjectAssertion#is(Object) is(Object)}
 * 		<li class='jm'>{@link FluentObjectAssertion#is(Predicate) is(Predicate)}
 * 		<li class='jm'>{@link FluentObjectAssertion#isNot(Object) isNot(Object)}
 * 		<li class='jm'>{@link FluentObjectAssertion#isAny(Object...) isAny(Object...)}
 * 		<li class='jm'>{@link FluentObjectAssertion#isNotAny(Object...) isNotAny(Object...)}
 * 		<li class='jm'>{@link FluentObjectAssertion#isNull() isNull()}
 * 		<li class='jm'>{@link FluentObjectAssertion#isNotNull() isNotNull()}
 * 		<li class='jm'>{@link FluentObjectAssertion#isString(String) isString(String)}
 * 		<li class='jm'>{@link FluentObjectAssertion#isJson(String) isJson(String)}
 * 		<li class='jm'>{@link FluentObjectAssertion#isSame(Object) isSame(Object)}
 * 		<li class='jm'>{@link FluentObjectAssertion#isSameJsonAs(Object) isSameJsonAs(Object)}
 * 		<li class='jm'>{@link FluentObjectAssertion#isSameSortedJsonAs(Object) isSameSortedJsonAs(Object)}
 * 		<li class='jm'>{@link FluentObjectAssertion#isSameSerializedAs(Object, WriterSerializer) isSameSerializedAs(Object, WriterSerializer)}
 * 		<li class='jm'>{@link FluentObjectAssertion#isType(Class) isType(Class)}
 * 		<li class='jm'>{@link FluentObjectAssertion#isExactType(Class) isExactType(Class)}
 * 	</ul>
 * </ul>
 *
 * <h5 class='topic'>Transform Methods</h5>
 * <p>
 * <ul class='javatree'>
 * 	<li class='jc'>{@link FluentThrowableAssertion}
 * 	<ul class='javatreec'>
 * 		<li class='jm'>{@link FluentThrowableAssertion#asMessage() asMessage()}
 * 		<li class='jm'>{@link FluentThrowableAssertion#asMessages() asMessages()}
 * 		<li class='jm'>{@link FluentThrowableAssertion#asLocalizedMessage() asLocalizedMessage()}
 * 		<li class='jm'>{@link FluentThrowableAssertion#asLocalizedMessages() asLocalizedMessages()}
 * 		<li class='jm'>{@link FluentThrowableAssertion#asStackTrace() asStackTrace()}
 * 		<li class='jm'>{@link FluentThrowableAssertion#asCausedBy() asCausedBy()}
 * 		<li class='jm'>{@link FluentThrowableAssertion#asCausedBy(Class) asCausedBy(Class)}
 * 		<li class='jm'>{@link FluentThrowableAssertion#asFind(Class) asFind(Class)}
 * 	</ul>
 * 	<li class='jc'>{@link FluentObjectAssertion}
 * 	<ul class='javatreec'>
 * 		<li class='jm'>{@link FluentObjectAssertion#asString() asString()}
 * 		<li class='jm'>{@link FluentObjectAssertion#asString(WriterSerializer) asString(WriterSerializer)}
 * 		<li class='jm'>{@link FluentObjectAssertion#asString(Function) asString(Function)}
 * 		<li class='jm'>{@link FluentObjectAssertion#asJson() asJson()}
 * 		<li class='jm'>{@link FluentObjectAssertion#asJsonSorted() asJsonSorted()}
 * 		<li class='jm'>{@link FluentObjectAssertion#asTransformed(Function) asApplied(Function)}
 * 		<li class='jm'>{@link FluentObjectAssertion#asAny() asAny()}
 *	</ul>
 * </ul>
 *
 * <h5 class='topic'>Configuration Methods</h5>
 * <p>
 * <ul class='javatree'>
 * 	<li class='jc'>{@link Assertion}
 * 	<ul class='javatreec'>
 * 		<li class='jm'>{@link Assertion#setMsg(String, Object...) setMsg(String, Object...)}
 * 		<li class='jm'>{@link Assertion#setOut(PrintStream) setOut(PrintStream)}
 * 		<li class='jm'>{@link Assertion#setSilent() setSilent()}
 * 		<li class='jm'>{@link Assertion#setStdOut() setStdOut()}
 * 		<li class='jm'>{@link Assertion#setThrowable(Class) setThrowable(Class)}
 * 	</ul>
 * </ul>
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jm.FluentAssertions}
 * 	<li class='extlink'>{@source}
 * </ul>
 *
 * @param <T> The throwable type.
 */
@FluentSetters(returns="ThrowableAssertion<T>")
public class ThrowableAssertion<T extends Throwable> extends FluentThrowableAssertion<T,ThrowableAssertion<T>> {

	//-----------------------------------------------------------------------------------------------------------------
	// Static
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Static creator.
	 *
	 * @param <X> The throwable type.
	 * @param value
	 * 	The object being tested.
	 * 	<br>Can be <jk>null</jk>.
	 * @return A new assertion object.
	 */
	public static <X extends Throwable> ThrowableAssertion<X> create(X value) {
		return new ThrowableAssertion<>(value);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Instance
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Constructor.
	 *
	 * @param value
	 * 	The object being tested.
	 * 	<br>Can be <jk>null</jk>.
	 */
	public ThrowableAssertion(T value) {
		super(value, null);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Fluent setters
	//-----------------------------------------------------------------------------------------------------------------

	// <FluentSetters>

	@Override /* GENERATED - org.apache.juneau.assertions.Assertion */
	public ThrowableAssertion<T> setMsg(String msg, Object...args) {
		super.setMsg(msg, args);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.assertions.Assertion */
	public ThrowableAssertion<T> setOut(PrintStream value) {
		super.setOut(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.assertions.Assertion */
	public ThrowableAssertion<T> setSilent() {
		super.setSilent();
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.assertions.Assertion */
	public ThrowableAssertion<T> setStdOut() {
		super.setStdOut();
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.assertions.Assertion */
	public ThrowableAssertion<T> setThrowable(Class<? extends java.lang.RuntimeException> value) {
		super.setThrowable(value);
		return this;
	}

	// </FluentSetters>
}
