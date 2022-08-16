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
package org.apache.juneau.rest.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import javax.servlet.*;

import org.apache.juneau.rest.*;
import org.apache.juneau.rest.servlet.*;

/**
 * Identifies a method that gets called during servlet initialization.
 *
 * <p>
 * This method is called from within the {@link Servlet#init(ServletConfig)} method after the {@link org.apache.juneau.rest.RestContext.Builder}
 * object has been created and initialized with the annotations defined on the class, but before the
 * {@link RestContext} object has been created.
 *
 * <p>
 * The only valid parameter type for this method is {@link org.apache.juneau.rest.RestContext.Builder} which can be used to configure the servlet.
 *
 * <p>
 * An example of this is the <c>PetStoreResource</c> class that uses an init method to perform initialization
 * of an internal data structure.
 *
 * <h5 class='figure'>Example:</h5>
 * <p class='bjava'>
 * 	<ja>@Rest</ja>(...)
 * 	<jk>public class</jk> PetStoreResource <jk>extends</jk> BasicRestServlet <jk>implements</jk> BasicUniversalJenaConfig {
 *
 * 		<jc>// Our database.</jc>
 * 		<jk>private</jk> Map&lt;Integer,Pet&gt; <jf>petDB</jf>;
 *
 * 		<ja>@RestInit</ja>
 * 		<jk>public void</jk> onInit(RestContext.Builder <jv>builder</jv>) <jk>throws</jk> Exception {
 * 			<jc>// Load our database from a local JSON file.</jc>
 * 			<jf>petDB</jf> = JsonParser.<jsf>DEFAULT</jsf>.parse(getClass().getResourceAsStream(<js>"PetStore.json"</js>), LinkedHashMap.<jk>class</jk>, Integer.<jk>class</jk>, Pet.<jk>class</jk>);
 * 		}
 * 	}
 * </p>
 *
 * <ul class='notes'>
 * 	<li class='note'>
 * 		The method should return <jk>void</jk> although if it does return any value, the value will be ignored.
 * 	<li class='note'>
 * 		The method should be <jk>public</jk> although other visibilities are valid if the security manager allows it.
 * 	<li class='note'>
 * 		Static methods can be used.
 * 	<li class='note'>
 * 		Multiple init methods can be defined on a class.
 * 		<br>Init methods on parent classes are invoked before init methods on child classes.
 * 		<br>The order of init method invocations within a class is alphabetical, then by parameter count, then by parameter types.
 * 	<li class='note'>
 * 		The method can throw any exception causing initialization of the servlet to fail.
 * 	<li class='note'>
 * 		Note that if you override a parent method, you probably need to call <code><jk>super</jk>.parentMethod(...)</code>.
 * 		<br>The method is still considered part of the parent class for ordering purposes even though it's
 * 		overridden by the child class.
 * 	<li class='note'>
 * 		The {@link RestServlet} class itself implements the following convenience method annotated with this annotation
 * 		that can be overridden directly:
 * 		<ul class='javatree'>
 * 			<li class='jac'>{@link RestServlet}
 * 			<ul>
 * 				<li class='jm'>{@link RestServlet#onInit(RestContext.Builder) onInit(RestContext.Builder)}
 * 			</ul>
 * 		</ul>
 * </ul>
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jrs.LifecycleHooks}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
@Target({METHOD,TYPE})
@Retention(RUNTIME)
@Inherited
@Repeatable(RestInitAnnotation.Array.class)
public @interface RestInit {

	/**
	 * Dynamically apply this annotation to the specified methods.
	 *
	 * <ul class='seealso'>
	 * 	<li class='link'>{@doc jm.DynamicallyAppliedAnnotations}
	 * </ul>
	 *
	 * @return The annotation value.
	 */
	String[] on() default {};
}
