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
package org.apache.juneau.transform;

import static org.apache.juneau.internal.ClassUtils.*;

import java.lang.reflect.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.reflection.*;

/**
 * Specialized transform for builder classes.
 *
 * <h5 class='section'>See Also:</h5>
 * <ul>
 * 	<li class='link'>{@doc juneau-marshall.Transforms.PojoBuilders}
 * </ul>
 *
 * @param <T> The bean class.
 * @param <B> The builder class.
 */
@SuppressWarnings("unchecked")
public class BuilderSwap<T,B> {

	private final Class<T> pojoClass;
	private final Class<B> builderClass;
	private final Constructor<T> pojoConstructor;      // public Pojo(Builder);
	private final Constructor<B> builderConstructor;   // public Builder();
	private final MethodInfo createBuilderMethod;          // Builder create();
	private final MethodInfo createPojoMethod;             // Pojo build();
	private ClassMeta<?> builderClassMeta;

	/**
	 * Constructor.
	 *
	 * @param pojoClass The POJO class created by the builder class.
	 * @param builderClass The builder class.
	 * @param pojoConstructor The POJO constructor that takes in a builder as a parameter.
	 * @param builderConstructor The builder no-arg constructor.
	 * @param createBuilderMethod The static create() method on the POJO class.
	 * @param createPojoMethod The build() method on the builder class.
	 */
	protected BuilderSwap(Class<T> pojoClass, Class<B> builderClass, Constructor<T> pojoConstructor, Constructor<B> builderConstructor, MethodInfo createBuilderMethod, MethodInfo createPojoMethod) {
		this.pojoClass = pojoClass;
		this.builderClass = builderClass;
		this.pojoConstructor = pojoConstructor;
		this.builderConstructor = builderConstructor;
		this.createBuilderMethod = createBuilderMethod;
		this.createPojoMethod = createPojoMethod;
	}

	/**
	 * The POJO class.
	 *
	 * @return The POJO class.
	 */
	public Class<T> getPojoClass() {
		return pojoClass;
	}

	/**
	 * The builder class.
	 *
	 * @return The builder class.
	 */
	public Class<B> getBuilderClass() {
		return builderClass;
	}

	/**
	 * Returns the {@link ClassMeta} of the transformed class type.
	 *
	 * <p>
	 * This value is cached for quick lookup.
	 *
	 * @param session
	 * 	The bean context to use to get the class meta.
	 * 	This is always going to be the same bean context that created this swap.
	 * @return The {@link ClassMeta} of the transformed class type.
	 */
	public ClassMeta<?> getBuilderClassMeta(BeanSession session) {
		if (builderClassMeta == null)
			builderClassMeta = session.getClassMeta(getBuilderClass());
		return builderClassMeta;
	}

	/**
	 * Creates a new builder object.
	 *
	 * @param session The current bean session.
	 * @param hint A hint about the class type.
	 * @return A new POJO.
	 * @throws Exception
	 */
	public B create(BeanSession session, ClassMeta<?> hint) throws Exception {
		if (createBuilderMethod != null)
			return (B)createBuilderMethod.invoke(null);
		return builderConstructor.newInstance();
	}

	/**
	 * Creates a new POJO from the specified builder.
	 *
	 * @param session The current bean session.
	 * @param builder The POJO builder.
	 * @param hint A hint about the class type.
	 * @return A new POJO.
	 * @throws Exception
	 */
	public T build(BeanSession session, B builder, ClassMeta<?> hint) throws Exception {
		if (createPojoMethod != null)
			return (T)createPojoMethod.invoke(builder);
		return pojoConstructor.newInstance(builder);
	}

	/**
	 * Creates a BuilderSwap from the specified builder class if it qualifies as one.
	 *
	 * @param builderClass The potential builder class.
	 * @param cVis Minimum constructor visibility.
	 * @param mVis Minimum method visibility.
	 * @return A new swap instance, or <jk>null</jk> if class wasn't a builder class.
	 */
	@SuppressWarnings("rawtypes")
	public static BuilderSwap<?,?> findSwapFromBuilderClass(Class<?> builderClass, Visibility cVis, Visibility mVis) {
		ClassInfo bci = ClassInfo.lookup(builderClass);
		if (bci.isNotPublic())
			return null;

		Class<?> pojoClass = resolveParameterType(Builder.class, 0, builderClass);

		MethodInfo createPojoMethod, createBuilderMethod;
		Constructor<?> pojoConstructor;
		ConstructorInfo builderConstructor;

		createPojoMethod = bci.findCreatePojoMethod();
		if (createPojoMethod != null)
			pojoClass = createPojoMethod.getReturnType().getInnerClass();

		if (pojoClass == null)
			return null;

		ClassInfo pci = ClassInfo.lookup(pojoClass);

		pojoConstructor = pci.findConstructor(cVis, false, builderClass);
		if (pojoConstructor == null)
			return null;

		builderConstructor = bci.findNoArgConstructor(cVis);
		createBuilderMethod = pci.findBuilderCreateMethod();
		if (builderConstructor == null && createBuilderMethod == null)
			return null;

		return new BuilderSwap(pojoClass, builderClass, pojoConstructor, builderConstructor == null ? null : builderConstructor.getInner(), createBuilderMethod, createPojoMethod);
	}


	/**
	 * Creates a BuilderSwap from the specified POJO class if it has one.
	 *
	 * @param pojoClass The POJO class to check.
	 * @param cVis Minimum constructor visibility.
	 * @param mVis Minimum method visibility.
	 * @return A new swap instance, or <jk>null</jk> if class didn't have a builder class.
	 */
	@SuppressWarnings("rawtypes")
	public static BuilderSwap<?,?> findSwapFromPojoClass(Class<?> pojoClass, Visibility cVis, Visibility mVis) {
		Class<?> builderClass = null;
		MethodInfo pojoCreateMethod, builderCreateMethod;
		Constructor<?> pojoConstructor = null;
		ConstructorInfo builderConstructor;

		org.apache.juneau.annotation.Builder b = pojoClass.getAnnotation(org.apache.juneau.annotation.Builder.class);

		if (b != null && b.value() != Null.class)
			builderClass = b.value();

		ClassInfo pci = ClassInfo.lookup(pojoClass);

		builderCreateMethod = pci.findBuilderCreateMethod();

		if (builderClass == null && builderCreateMethod != null)
			builderClass = builderCreateMethod.getReturnType().getInnerClass();

		if (builderClass == null) {
			for (ConstructorInfo cc : pci.getPublicConstructors()) {
				if (cc.isVisible(cVis) && cc.hasNumArgs(1)) {
					Class<?>[] pt = cc.getParameterTypes();
					if (isParentClass(Builder.class, pt[0])) {
						pojoConstructor = cc.getInner();
						builderClass = pt[0];
					}
				}
			}
		}

		if (builderClass == null)
			return null;

		ClassInfo bci = ClassInfo.lookup(builderClass);
		builderConstructor = bci.findNoArgConstructor(cVis);
		if (builderConstructor == null && builderCreateMethod == null)
			return null;

		pojoCreateMethod = bci.findCreatePojoMethod();
		if (pojoConstructor == null)
			pojoConstructor = pci.findConstructor(cVis, false, builderClass);

		if (pojoConstructor == null && pojoCreateMethod == null)
			return null;

		return new BuilderSwap(pojoClass, builderClass, pojoConstructor, builderConstructor == null ? null : builderConstructor.getInner(), builderCreateMethod, pojoCreateMethod);
	}
}