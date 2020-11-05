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
package org.apache.juneau.csv.annotation;

import java.lang.annotation.*;
import java.lang.reflect.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;

/**
 * Builder class for the {@link Csv} annotation.
 *
 * <ul class='seealso'>
 * 	<li class='jm'>{@link BeanContextBuilder#annotations(Annotation...)}
 * </ul>
 */
public class CsvBuilder extends TargetedAnnotationTMFBuilder {

	/** Default value */
	public static final Csv DEFAULT = create().build();

	/**
	 * Instantiates a new builder for this class.
	 *
	 * @return A new builder object.
	 */
	public static CsvBuilder create() {
		return new CsvBuilder();
	}

	/**
	 * Instantiates a new builder for this class.
	 *
	 * @param on The targets this annotation applies to.
	 * @return A new builder object.
	 */
	public static CsvBuilder create(Class<?>...on) {
		return create().on(on);
	}

	/**
	 * Instantiates a new builder for this class.
	 *
	 * @param on The targets this annotation applies to.
	 * @return A new builder object.
	 */
	public static CsvBuilder create(String...on) {
		return create().on(on);
	}

	private static class Impl extends TargetedAnnotationTImpl implements Csv {

		Impl(CsvBuilder b) {
			super(b);
			postConstruct();
		}
	}


	/**
	 * Constructor.
	 */
	public CsvBuilder() {
		super(Csv.class);
	}

	/**
	 * Instantiates a new {@link Csv @Csv} object initialized with this builder.
	 *
	 * @return A new {@link Csv @Csv} object.
	 */
	public Csv build() {
		return new Impl(this);
	}

	// <FluentSetters>

	@Override /* GENERATED - TargetedAnnotationBuilder */
	public CsvBuilder on(String...values) {
		super.on(values);
		return this;
	}

	@Override /* GENERATED - TargetedAnnotationTBuilder */
	public CsvBuilder on(java.lang.Class<?>...value) {
		super.on(value);
		return this;
	}

	@Override /* GENERATED - TargetedAnnotationTBuilder */
	public CsvBuilder onClass(java.lang.Class<?>...value) {
		super.onClass(value);
		return this;
	}

	@Override /* GENERATED - TargetedAnnotationTMFBuilder */
	public CsvBuilder on(Field...value) {
		super.on(value);
		return this;
	}

	@Override /* GENERATED - TargetedAnnotationTMFBuilder */
	public CsvBuilder on(Method...value) {
		super.on(value);
		return this;
	}

	// </FluentSetters>
}
