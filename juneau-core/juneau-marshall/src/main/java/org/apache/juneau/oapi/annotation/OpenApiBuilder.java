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
package org.apache.juneau.oapi.annotation;

import java.lang.annotation.*;
import java.lang.reflect.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;

/**
 * Builder class for the {@link OpenApi} annotation.
 *
 * <ul class='seealso'>
 * 	<li class='jm'>{@link BeanContextBuilder#annotations(Annotation...)}
 * </ul>
 */
public class OpenApiBuilder extends TargetedAnnotationTMFBuilder {

	/** Default value */
	public static final OpenApi DEFAULT = create().build();

	/**
	 * Instantiates a new builder for this class.
	 *
	 * @return A new builder object.
	 */
	public static OpenApiBuilder create() {
		return new OpenApiBuilder();
	}

	/**
	 * Instantiates a new builder for this class.
	 *
	 * @param on The targets this annotation applies to.
	 * @return A new builder object.
	 */
	public static OpenApiBuilder create(Class<?>...on) {
		return create().on(on);
	}

	/**
	 * Instantiates a new builder for this class.
	 *
	 * @param on The targets this annotation applies to.
	 * @return A new builder object.
	 */
	public static OpenApiBuilder create(String...on) {
		return new OpenApiBuilder().on(on);
	}

	private static class Impl extends TargetedAnnotationTImpl implements OpenApi {

		Impl(OpenApiBuilder b) {
			super(b);
			postConstruct();
		}
	}


	/**
	 * Constructor.
	 */
	public OpenApiBuilder() {
		super(OpenApi.class);
	}

	/**
	 * Instantiates a new {@link OpenApi @OpenApi} object initialized with this builder.
	 *
	 * @return A new {@link OpenApi @OpenApi} object.
	 */
	public OpenApi build() {
		return new Impl(this);
	}

	// <FluentSetters>

	@Override /* GENERATED - TargetedAnnotationBuilder */
	public OpenApiBuilder on(String...values) {
		super.on(values);
		return this;
	}

	@Override /* GENERATED - TargetedAnnotationTBuilder */
	public OpenApiBuilder on(java.lang.Class<?>...value) {
		super.on(value);
		return this;
	}

	@Override /* GENERATED - TargetedAnnotationTBuilder */
	public OpenApiBuilder onClass(java.lang.Class<?>...value) {
		super.onClass(value);
		return this;
	}

	@Override /* GENERATED - TargetedAnnotationTMFBuilder */
	public OpenApiBuilder on(Field...value) {
		super.on(value);
		return this;
	}

	@Override /* GENERATED - TargetedAnnotationTMFBuilder */
	public OpenApiBuilder on(Method...value) {
		super.on(value);
		return this;
	}

	// </FluentSetters>
}
