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
package org.apache.juneau.uon.annotation;

import org.apache.juneau.*;
import org.apache.juneau.reflect.*;
import org.apache.juneau.svl.*;
import org.apache.juneau.uon.*;

/**
 * Utility classes and methods for the {@link UonConfig @UonConfig} annotation.
 */
public class UonConfigAnnotation {

	/**
	 * Applies {@link UonConfig} annotations to a {@link UonSerializerBuilder}.
	 */
	public static class SerializerApply extends AnnotationApplier<UonConfig,UonSerializerBuilder> {

		/**
		 * Constructor.
		 *
		 * @param vr The resolver for resolving values in annotations.
		 */
		public SerializerApply(VarResolverSession vr) {
			super(UonConfig.class, UonSerializerBuilder.class, vr);
		}

		@Override
		public void apply(AnnotationInfo<UonConfig> ai, UonSerializerBuilder b) {
			UonConfig a = ai.getAnnotation();

			bool(a.addBeanTypes()).ifPresent(x -> b.addBeanTypesUon(x));
			bool(a.encoding()).ifPresent(x -> b.encoding(x));
			string(a.paramFormat()).map(ParamFormat::valueOf).ifPresent(x -> b.paramFormat(x));
		}
	}

	/**
	 * Applies {@link UonConfig} annotations to a {@link UonParserBuilder}.
	 */
	public static class ParserApply extends AnnotationApplier<UonConfig,UonParserBuilder> {

		/**
		 * Constructor.
		 *
		 * @param vr The resolver for resolving values in annotations.
		 */
		public ParserApply(VarResolverSession vr) {
			super(UonConfig.class, UonParserBuilder.class, vr);
		}

		@Override
		public void apply(AnnotationInfo<UonConfig> ai, UonParserBuilder b) {
			UonConfig a = ai.getAnnotation();

			bool(a.decoding()).ifPresent(x -> b.decoding(x));
			bool(a.validateEnd()).ifPresent(x -> b.validateEnd(x));
		}
	}
}