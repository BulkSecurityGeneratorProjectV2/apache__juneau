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
package org.apache.juneau.rest.jaxrs;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import org.apache.juneau.parser.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.serializer.*;
import org.apache.juneau.transform.*;

/**
 * Annotations applicable to subclasses of {@link BaseProvider}.
 *
 * <h5 class='topic'>Description</h5>
 *
 * Used to associate serializers, parsers, filters, and properties with instances of {@link BaseProvider}.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc juneau-rest-server-jaxrs}
 * </ul>
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Inherited
public @interface JuneauProvider {

	/**
	 * Provider-level bean filters.
	 *
	 * <p>
	 * These filters are applied to all serializers and parsers being used by the provider.
	 *
	 * <p>
	 * If the specified class is an instance of {@link BeanFilter}, then a filter built from that builder is added.
	 * Any other classes are wrapped in a {@link BeanFilter} with {@link BeanFilter#interfaceClass(Class)} to indicate that subclasses should
	 * be treated as the specified class type.
	 */
	Class<?>[] beanFilters() default {};

	/**
	 * Provider-level properties.
	 *
	 * <p>
	 * Property values will be converted to the appropriate type.
	 *
	 * <p>
	 * These properties can be augmented/overridden through the {@link RestMethod#properties() @RestMethod(properties)} annotation on the REST method.
	 */
	Property[] properties() default {};

	/**
	 * Provider-level POJO swaps.
	 *
	 * <p>
	 * These POJO swaps are applied to all serializers and parsers being used by the provider.
	 *
	 * <p>
	 * If the specified class is an instance of {@link PojoSwap}, then that swap is added.
	 * Any other classes are wrapped in a {@link SurrogateSwap}.
	 */
	Class<?>[] swaps() default {};

	/**
	 * Shortcut for setting {@link #properties()} of boolean types.
	 *
	 * <p>
	 * Setting a flag is the equivalent to setting the same property to <js>"true"</js>.
	 */
	String[] flags() default {};

	/**
	 * Specifies a list of {@link Serializer} classes to add to the list of serializers available for this provider.
	 *
	 * <p>
	 * This annotation can only be used on {@link Serializer} classes that have no-arg constructors.
	 */
	Class<? extends Serializer>[] serializers() default {};

	/**
	 * Specifies a list of {@link Parser} classes to add to the list of parsers available for this provider.
	 *
	 * <p>
	 * This annotation can only be used on {@link Parser} classes that have no-arg constructors.
	 */
	Class<? extends Parser>[] parsers() default {};
}
