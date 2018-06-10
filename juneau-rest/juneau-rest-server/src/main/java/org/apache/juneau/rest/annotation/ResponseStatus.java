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

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import org.apache.juneau.utils.*;

/**
 * Annotation that can be applied to parameters and types to denote them as an HTTP response status.
 * 
 * <p>
 * This can only be applied to parameters and subclasses of the {@link Value} class with an {@link Integer} type.
 * <br>The {@link Value} object is mean to be a place-holder for the set value.
 * 
 * <p class='bcode'>
 * 	<ja>@RestMethod</ja>(name=<js>"GET"</js>, path=<js>"/user/login"</js>)
 * 	<jk>public void</jk> login(String username, String password, 
 * 			<ja>@ResponseStatus</ja>(code=401, description=<js>"Invalid user/pw"</js>) Value&lt;Integer&gt; status) {
 * 		<jk>if</jk> (! isValid(username, password))
 * 			status.set(401);
 * 	}
 * </p>
 * 
 * <p>
 * The {@link Responses @Responses} annotation can be used to represent multiple possible response types.
 * 
 * <p class='bcode'>
 * 	<ja>@RestMethod</ja>(name=<js>"GET"</js>, path=<js>"/user/login"</js>)
 * 	<jk>public void</jk> login(String username, String password, 
 * 			<ja>@ResponseStatuses</ja>{
 * 				<ja>@ResponseStatus</ja>(200)
 * 				<ja>@ResponseStatus</ja>(code=401, description=<js>"Invalid user/pw"</js>)
 *			}
 * 			Value&lt;Integer&gt; status) {
 * 
 * 		<jk>if</jk> (! isValid(username, password))
 * 			status.set(401);
 * 		<jk>else</jk>
 * 			status.set(200);
 * 	}
 * </p>
 * 
 * <p>
 * The other option is to apply this annotation to a subclass of {@link Value} which often leads to a cleaner
 * REST method:
 * 
 * <p class='bcode'>
 * 	<ja>@ResponseStatuses</ja>{
 * 		<ja>@ResponseStatus</ja>(200)
 * 		<ja>@ResponseStatus</ja>(code=401, description=<js>"Invalid user/pw"</js>)
 *	}
 * 	<jk>public class</jk> LoginStatus <jk>extends</jk> Value&lt;Integer&gt; {}
 * 	
 * 	<ja>@RestMethod</ja>(name=<js>"GET"</js>, path=<js>"/user/login"</js>)
 * 	<jk>public void</jk> login(String username, String password, LoginStatus status) { 
 * 		<jk>if</jk> (! isValid(username, password))
 * 			status.set(401);
 * 		<jk>else</jk>
 * 			status.set(200);
 * 	}
 * </p>
 * 
 * <p>
 * The attributes on this annotation are used to populate the generated Swagger for the method.
 * <br>In this case, the Swagger is populated with the following:
 * 
 * <p class='bcode'>
 * 	<js>'/user/login'</js>: {
 * 		get: {
 * 			responses: {
 * 				200: {
 * 					description: <js>'OK'</js>
 * 				},
 * 				401: {
 * 					description: <js>'Invalid user/pw'</js>
 * 				}
 * 			}
 * 		}
 * 	}
 * </p>
 * 
 * <h5 class='section'>See Also:</h5>
 * <ul>
 * 	<li class='link'><a class="doclink" href="https://swagger.io/specification/v2/#responseObject">Swagger Specification &gt; Response Object</a>
 * </ul>
 */
@Documented
@Target({})
@Retention(RUNTIME)
@Inherited
public @interface ResponseStatus {
	
	/**
	 * The HTTP status of the response.
	 */
	int code() default 0;
	
	/**
	 * A synonym to {@link #code()}.
	 * 
	 * <p>
	 * Useful if you only want to specify a code only.
	 * 
	 * <p class='bcode'>
	 * 	<ja>@ResponseStatus</ja>(200)
	 * </p>
	 */
	int value() default 0;

	/**
	 * Defines the swagger field <code>/paths/{path}/{method}/responses/{status-code}/description</code>.
	 * 
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The format is plain text.
	 * 		<br>Multiple lines are concatenated with newlines.
	 * 	<li>
	 * 		Supports <a class="doclink" href="../../../../../overview-summary.html#DefaultRestSvlVariables">initialization-time and request-time variables</a> 
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * </ul>
	 */
	String[] description() default {};
	
	/**
	 * Free-form value for the swagger field <code>/paths/{path}/{method}/responses/{response}</code>
	 * 
	 * <p>
	 * This is a JSON object that makes up the swagger information for this Response object.
	 * 
	 * <p>
	 * The following are completely equivalent ways of defining the swagger description of the Response object:
	 * <p class='bcode w800'>
	 * 	<jc>// Normal</jc>
	 * 	<ja>@ResponseStatus</ja>(
	 * 		code=401, 
	 * 		description=<js>"Invalid user/pw"</js>
	 * 	)
	 * </p>
	 * <p class='bcode w800'>
	 * 	<jc>// Free-form</jc>
	 * 	<ja>@ResponseStatus</ja>(
	 * 		code=401,
	 * 		api={
	 * 			<js>"description: 'Invalid user/pw'"</js>
	 * 		}
	 * 	)
	 * </p>
	 * <p class='bcode w800'>
	 * 	<jc>// Free-form using variables</jc>
	 * 	<ja>@ResponseStatus</ja>(
	 * 		code=401,
	 * 		api=<js>"$L{unauthorizedSwagger}"</js>
	 * 	)
	 * </p>
	 * <p class='bcode w800'>
	 * 	<mc>// Contents of MyResource.properties</mc>
	 * 	<mk>unauthorizedSwagger</mk> = <mv>{ description: "Invalid user/pw" }</mv>
	 * </p>
	 * 
	 * <p>
	 * 	The reasons why you may want to use this field include:
	 * <ul>
	 * 	<li>You want to pull in the entire Swagger JSON definition for this body from an external source such as a properties file.
	 * 	<li>You want to add extra fields to the Swagger documentation that are not officially part of the Swagger specification.
	 * </ul>
	 * 
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		Note that the only swagger field you can't specify using this value is <js>"code"</js> whose value needs to be known during servlet initialization.
	 * 	<li>
	 * 		The format is a Simplified JSON object.
	 * 	<li>
	 * 		The leading/trailing <code>{ }</code> characters are optional.
	 * 		<br>The following two example are considered equivalent:
	 * 		<p class='bcode w800'>
	 * 	<ja>@ResponseStatus</ja>(<js>"{description:'Invalid user/pw'}"</js>)
	 * 		</p>
	 * 		<p class='bcode w800'>
	 * 	<ja>@ResponseStatus</ja>(<js>"description:'Invalid user/pw'"</js>)
	 * 		</p>
	 * 	<li>
	 * 		Supports <a class="doclink" href="../../../../../overview-summary.html#DefaultRestSvlVariables">initialization-time and request-time variables</a> 
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * 	<li>
	 * 		Values defined in this field supersede values pulled from the Swagger JSON file and are superseded by individual values defined on this annotation.
	 * </ul>
	 */
	String[] api() default {};
}
