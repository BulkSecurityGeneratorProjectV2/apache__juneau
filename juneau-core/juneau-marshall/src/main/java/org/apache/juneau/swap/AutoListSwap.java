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
package org.apache.juneau.swap;

import static org.apache.juneau.internal.ClassUtils.*;
import static org.apache.juneau.internal.CollectionUtils.*;

import java.lang.reflect.*;
import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.reflect.*;
import org.apache.juneau.serializer.*;

/**
 * A dynamic object swap based on reflection of a Java class that converts objects to Lists.
 *
 * <p>
 * Looks for methods on the class that can be called to swap-in surrogate List objects before serialization and swap-out
 * surrogate List objects after parsing.
 *
 * <h5 class='figure'>Valid surrogate objects</h5>
 * <ul>
 * 	<li class='jc'>Any subclass of {@link List}
 * </ul>
 *
 * <h5 class='figure'>Valid swap methods (S = Swapped type)</h5>
 * <ul>
 * 	<li class='jm'><c><jk>public</jk> S toList()</c>
 * 	<li class='jm'><c><jk>public</jk> S toList(BeanSession)</c>
 * 	<li class='jm'><c><jk>public</jk> S toJsonList()</c>
 * 	<li class='jm'><c><jk>public</jk> S toJsonList(BeanSession)</c>
 * </ul>
 *
 * <h5 class='figure'>Valid unswap methods (N = Normal type, S = Swapped type)</h5>
 * <ul>
 * 	<li class='jm'><c><jk>public static</jk> N fromList(S)</c>
 * 	<li class='jm'><c><jk>public static</jk> N fromList(BeanSession, S)</c>
 * 	<li class='jm'><c><jk>public static</jk> N fromJsonList(S)</c>
 * 	<li class='jm'><c><jk>public static</jk> N fromJsonList(BeanSession, S)</c>
 * 	<li class='jm'><c><jk>public static</jk> N create(S)</c>
 * 	<li class='jm'><c><jk>public static</jk> N create(BeanSession, S)</c>
 * 	<li class='jm'><c><jk>public static</jk> N valueOf(S)</c>
 * 	<li class='jm'><c><jk>public static</jk> N valueOf(BeanSession, S)</c>
 * 	<li class='jm'><c><jk>public</jk> N(S)</c>
 * </ul>
 *
 * <p>
 * Classes are ignored if any of the following are true:
 * <ul>
 * 	<li>Classes annotated with {@link BeanIgnore @BeanIgnore}.
 * 	<li>Non-static member classes.
 * </ul>
 *
 * <p>
 * Members/constructors are ignored if any of the following are true:
 * <ul>
 * 	<li>Members/constructors annotated with {@link BeanIgnore @BeanIgnore}.
 * 	<li>Deprecated members/constructors.
 * </ul>
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-marshall.jm.Swaps">Swaps</a>
 * </ul>
 *
 * @param <T> The normal class type.
 */
public class AutoListSwap<T> extends ObjectSwap<T,List<?>> {

	private static final Set<String>
		SWAP_METHOD_NAMES = uset("toList", "toJsonList"),
		UNSWAP_METHOD_NAMES = uset("fromList", "fromJsonList", "create", "valueOf");

	/**
	 * Look for constructors and methods on this class and construct a dynamic swap if it's possible to do so.
	 *
	 * @param bc The bean context to use for looking up annotations.
	 * @param ci The class to try to constructor a dynamic swap on.
	 * @return An object swap instance, or <jk>null</jk> if one could not be created.
	 */
	@SuppressWarnings({ "rawtypes" })
	public static ObjectSwap<?,?> find(BeanContext bc, ClassInfo ci) {

		if (shouldIgnore(bc, ci))
			return null;

		// Find swap() method if present.
		for (MethodInfo m : ci.getMethods()) {
			if (isSwapMethod(bc, m)) {

				ClassInfo rt = m.getReturnType();

				MethodInfo mi = ci.getMethod(x -> isUnswapMethod(bc, x, ci, rt));
				if (mi != null)
					return new AutoListSwap(bc, ci, m, mi, null);

				ConstructorInfo cs = ci.getDeclaredConstructor(x -> isUnswapConstructor(bc, x, rt));
				if (cs != null)
					return new AutoListSwap(bc, ci, m, null, cs);

				return new AutoListSwap(bc, ci, m, null, null);
			}
		}

		return null;
	}

	private static boolean shouldIgnore(BeanContext bc, ClassInfo ci) {
		return
			ci.hasAnnotation(bc, BeanIgnore.class)
			|| ci.isNonStaticMemberClass();
	}

	private static boolean isSwapMethod(BeanContext bc, MethodInfo mi) {
		return
			mi.isNotDeprecated()
			&& mi.isNotStatic()
			&& mi.isVisible(bc.getBeanMethodVisibility())
			&& mi.hasName(SWAP_METHOD_NAMES)
			&& mi.hasReturnTypeParent(List.class)
			&& mi.hasFuzzyParamTypes(BeanSession.class)
			&& mi.hasNoAnnotation(bc, BeanIgnore.class);
	}

	private static boolean isUnswapMethod(BeanContext bc, MethodInfo mi, ClassInfo ci, ClassInfo rt) {
		return
			mi.isNotDeprecated()
			&& mi.isStatic()
			&& mi.isVisible(bc.getBeanMethodVisibility())
			&& mi.hasName(UNSWAP_METHOD_NAMES)
			&& mi.hasFuzzyParamTypes(BeanSession.class, rt.inner())
			&& mi.hasReturnTypeParent(ci)
			&& mi.hasNoAnnotation(bc, BeanIgnore.class);
	}

	private static boolean isUnswapConstructor(BeanContext bc, ConstructorInfo cs, ClassInfo rt) {
		return
			cs.isNotDeprecated()
			&& cs.isVisible(bc.getBeanConstructorVisibility())
			&& cs.hasMatchingParamTypes(rt)
			&& cs.hasNoAnnotation(bc, BeanIgnore.class);
	}

	//------------------------------------------------------------------------------------------------------------------

	private final Method swapMethod, unswapMethod;
	private final Constructor<?> unswapConstructor;

	private AutoListSwap(BeanContext bc, ClassInfo ci, MethodInfo swapMethod, MethodInfo unswapMethod, ConstructorInfo unswapConstructor) {
		super(ci.inner(), swapMethod.getReturnType().inner());
		this.swapMethod = bc.getBeanMethodVisibility().transform(swapMethod.inner());
		this.unswapMethod = unswapMethod == null ? null : bc.getBeanMethodVisibility().transform(unswapMethod.inner());
		this.unswapConstructor = unswapConstructor == null ? null : bc.getBeanConstructorVisibility().transform(unswapConstructor.inner());
	}

	@Override /* ObjectSwap */
	public List<?> swap(BeanSession session, Object o) throws SerializeException {
		try {
			return (List<?>)swapMethod.invoke(o, getMatchingArgs(swapMethod.getParameterTypes(), session));
		} catch (Exception e) {
			throw SerializeException.create(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override /* ObjectSwap */
	public T unswap(BeanSession session, List<?> o, ClassMeta<?> hint) throws ParseException {
		try {
			if (unswapMethod != null)
				return (T)unswapMethod.invoke(null, getMatchingArgs(unswapMethod.getParameterTypes(), session, o));
			if (unswapConstructor != null)
				return (T)unswapConstructor.newInstance(o);
			return super.unswap(session, o, hint);
		} catch (Exception e) {
			throw ParseException.create(e);
		}
	}
}
