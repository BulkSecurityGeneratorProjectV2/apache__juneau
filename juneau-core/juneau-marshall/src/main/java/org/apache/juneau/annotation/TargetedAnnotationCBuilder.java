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
package org.apache.juneau.annotation;

import java.lang.annotation.*;
import java.lang.reflect.*;

import org.apache.juneau.internal.*;
import org.apache.juneau.reflect.*;

/**
 * An implementation of an annotation that has an <code>on</code> value targeting classes/methods/fields/constructors.
 */
@FluentSetters
public class TargetedAnnotationCBuilder extends TargetedAnnotationBuilder {

	/**
	 * Constructor.
	 *
	 * @param annotationType The annotation type of the annotation implementation class.
	 */
	public TargetedAnnotationCBuilder(Class<? extends Annotation> annotationType) {
		super(annotationType);
	}

	/**
	 * Appends the constructors that this annotation applies to.
	 *
	 * @param value The values to append.
	 * @return This object.
	 */
	@FluentSetter
	public TargetedAnnotationCBuilder on(Constructor<?>...value) {
		for (Constructor<?> v : value)
			on(ConstructorInfo.of(v).getFullName());
		return this;
	}

	// <FluentSetters>

	@Override /* GENERATED - org.apache.juneau.annotation.TargetedAnnotationBuilder */
	public TargetedAnnotationCBuilder on(String...values) {
		super.on(values);
		return this;
	}

	// </FluentSetters>
}
