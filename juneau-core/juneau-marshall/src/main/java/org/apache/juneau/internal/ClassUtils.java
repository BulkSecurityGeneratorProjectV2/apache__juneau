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
package org.apache.juneau.internal;

import static org.apache.juneau.internal.ThrowableUtils.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.collections.*;
import org.apache.juneau.reflect.*;

/**
 * Class-related utility methods.
 *
 * <ul class='seealso'>
 * 	<li class='extlink'>{@source}
 * </ul>
 */
public final class ClassUtils {

	/**
	 * Returns the class types for the specified arguments.
	 *
	 * @param args The objects we're getting the classes of.
	 * @return The classes of the arguments.
	 */
	public static Class<?>[] getClasses(Object...args) {
		Class<?>[] pt = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++)
			pt[i] = args[i] == null ? null : args[i].getClass();
		return pt;
	}

	/**
	 * Creates an instance of the specified class.
	 *
	 * @param c
	 * 	The class to cast to.
	 * @param c2
	 * 	The class to instantiate.
	 * 	Can also be an instance of the class.
	 * @return
	 * 	The new class instance, or <jk>null</jk> if the class was <jk>null</jk> or is abstract or an interface.
	 * @throws
	 * 	RuntimeException if constructor could not be found or called.
	 */
	public static <T> T castOrCreate(Class<T> c, Object c2) {
		return castOrCreateFromOuter(null, c, c2, false);
	}

	/**
	 * Creates an instance of the specified class.
	 *
	 * @param c
	 * 	The class to cast to.
	 * @param c2
	 * 	The class to instantiate.
	 * 	Can also be an instance of the class.
	 * @param fuzzyArgs
	 * 	Use fuzzy constructor arg matching.
	 * 	<br>When <jk>true</jk>, constructor args can be in any order and extra args are ignored.
	 * 	<br>No-arg constructors are also used if no other constructors are found.
	 * @param args
	 * 	The arguments to pass to the constructor.
	 * @return
	 * 	The new class instance, or <jk>null</jk> if the class was <jk>null</jk> or is abstract or an interface.
	 * @throws
	 * 	RuntimeException if constructor could not be found or called.
	 */
	public static <T> T castOrCreate(Class<T> c, Object c2, boolean fuzzyArgs, Object...args) {
		return castOrCreateFromOuter(null, c, c2, fuzzyArgs, args);
	}

	/**
	 * Creates an instance of the specified class from within the context of another object.
	 *
	 * @param outer
	 * 	The outer object.
	 * 	Can be <jk>null</jk>.
	 * @param c
	 * 	The class to cast to.
	 * @param c2
	 * 	The class to instantiate.
	 * 	Can also be an instance of the class.
	 * @param fuzzyArgs
	 * 	Use fuzzy constructor arg matching.
	 * 	<br>When <jk>true</jk>, constructor args can be in any order and extra args are ignored.
	 * 	<br>No-arg constructors are also used if no other constructors are found.
	 * @param args
	 * 	The arguments to pass to the constructor.
	 * @return
	 * 	The new class instance, or <jk>null</jk> if the class was <jk>null</jk> or is abstract or an interface.
	 * @throws
	 * 	RuntimeException if constructor could not be found or called.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T castOrCreateFromOuter(Object outer, Class<T> c, Object c2, boolean fuzzyArgs, Object...args) {
		if (c2 == null)
			return null;
		if (c2 instanceof Class) {
			try {
				ClassInfo c3 = ClassInfo.of((Class<?>)c2);

				MethodInfo mi = fuzzyArgs ? getStaticCreatorFuzzy(c3, args) : getStaticCreator(c3, args);

				if (mi != null)
					return fuzzyArgs ? (T)mi.invokeFuzzy(null, args) : mi.invoke(null, args);

				if (c3.isInterface() || c3.isAbstract())
					return null;

				// First look for an exact match.
				Object[] args2 = args;
				ConstructorInfo con = c3.getPublicConstructor(x -> x.canAccept(args2));
				if (con != null)
					return con.<T>invoke(args);

				// Next look for an exact match including the outer.
				if (outer != null) {
					args = AList.of(outer).append(args).toArray();
					Object[] args3 = args;
					con = c3.getPublicConstructor(x -> x.canAccept(args3));
					if (con != null)
						return con.<T>invoke(args);
				}

				// Finally use fuzzy matching.
				if (fuzzyArgs) {
					mi = getStaticCreatorFuzzy(c3, args);
					if (mi != null)
						return mi.invoke(null, getMatchingArgs(mi.getParamTypes(), args));

					con = getPublicConstructorFuzzy(c3, args);
					if (con != null)
						return con.<T>invokeFuzzy(args);
				}

				throw runtimeException("Could not instantiate class {0}/{1}.  Constructor not found.", className(c), c2);
			} catch (Exception e) {
				throw runtimeException(e, "Could not instantiate class {0}", className(c));
			}
		} else if (ClassInfo.of(c).isParentOf(c2.getClass())) {
			return (T)c2;
		} else {
			throw runtimeException("Object of type {0} found but was expecting {1}.", className(c2), className(c));
		}
	}

	private static ConstructorInfo getPublicConstructorFuzzy(ClassInfo c, Object...args) {
		int bestCount = -1;
		ConstructorInfo bestMatch = null;
		for (ConstructorInfo n : c.getPublicConstructors()) {
			int m = n.fuzzyArgsMatch(args);
			if (m > bestCount) {
				bestCount = m;
				bestMatch = n;
			}
		}
		return bestMatch;
	}

	private static MethodInfo getStaticCreatorFuzzy(ClassInfo c, Object...args) {
		int bestCount = -1;
		MethodInfo bestMatch = null;
		for (MethodInfo m : c.getPublicMethods()) {
			if (m.matches(x -> x.isStatic() && x.isNotDeprecated() && x.hasReturnType(c) && x.hasName("create","getInstance"))) {
				int mn = m.canAcceptFuzzy(args);
				if (mn > bestCount) {
					bestCount = mn;
					bestMatch = m;
				}
			}
		}
		return bestMatch;
	}

	private static MethodInfo getStaticCreator(ClassInfo c, Object...args) {
		return c.getPublicMethod(
			x -> x.isStatic()
			&& x.isNotDeprecated()
			&& x.hasReturnType(c)
			&& x.hasName("create","getInstance")
			&& x.canAccept(args)
		);
	}

	/**
	 * Matches arguments to a list of parameter types.
	 *
	 * <p>
	 * Extra parameters are ignored.
	 * <br>Missing parameters are left null.
	 *
	 * @param paramTypes The parameter types.
	 * @param args The arguments to match to the parameter types.
	 * @return
	 * 	An array of parameters.
	 */
	public static Object[] getMatchingArgs(Class<?>[] paramTypes, Object... args) {
		boolean needsShuffle = paramTypes.length != args.length;
		if (! needsShuffle) {
			for (int i = 0; i < paramTypes.length; i++) {
				if (! paramTypes[i].isInstance(args[i]))
					needsShuffle = true;
			}
		}
		if (! needsShuffle)
			return args;
		Object[] params = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			ClassInfo pt = ClassInfo.of(paramTypes[i]).getWrapperInfoIfPrimitive();
			for (int j = 0; j < args.length; j++) {
				if (args[j] != null && pt.isParentOf(args[j].getClass())) {
					params[i] = args[j];
					break;
				}
			}
		}
		return params;
	}

	private static Object[] getMatchingArgs(List<ClassInfo> paramTypes, Object... args) {
		Object[] params = new Object[paramTypes.size()];
		for (int i = 0; i < paramTypes.size(); i++) {
			ClassInfo pt = paramTypes.get(i).getWrapperInfoIfPrimitive();
			for (int j = 0; j < args.length; j++) {
				if (pt.isParentOf(args[j].getClass())) {
					params[i] = args[j];
					break;
				}
			}
		}
		return params;
	}

	/**
	 * Attempts to call <code>x.setAccessible(<jk>true</jk>)</code> and quietly ignores security exceptions.
	 *
	 * @param x The constructor.
	 * @return <jk>true</jk> if call was successful.
	 */
	public static boolean setAccessible(Constructor<?> x) {
		try {
			if (! (x == null || x.isAccessible()))
				x.setAccessible(true);
			return true;
		} catch (SecurityException e) {
			return false;
		}
	}

	/**
	 * Attempts to call <code>x.setAccessible(<jk>true</jk>)</code> and quietly ignores security exceptions.
	 *
	 * @param x The method.
	 * @return <jk>true</jk> if call was successful.
	 */
	public static boolean setAccessible(Method x) {
		try {
			if (! (x == null || x.isAccessible()))
				x.setAccessible(true);
			return true;
		} catch (SecurityException e) {
			return false;
		}
	}

	/**
	 * Attempts to call <code>x.setAccessible(<jk>true</jk>)</code> and quietly ignores security exceptions.
	 *
	 * @param x The field.
	 * @return <jk>true</jk> if call was successful.
	 */
	public static boolean setAccessible(Field x) {
		try {
			if (! (x == null || x.isAccessible()))
				x.setAccessible(true);
			return true;
		} catch (SecurityException e) {
			return false;
		}
	}

	/**
	 * Returns the specified type as a <c>Class</c>.
	 *
	 * <p>
	 * If it's already a <c>Class</c>, it just does a cast.
	 * <br>If it's a <c>ParameterizedType</c>, it returns the raw type.
	 *
	 * @param t The type to convert.
	 * @return The type converted to a <c>Class</c>, or <jk>null</jk> if it could not be converted.
	 */
	public static Class<?> toClass(Type t) {
		if (t instanceof Class)
			return (Class<?>)t;
		if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType)t;
			// The raw type should always be a class (right?)
			return (Class<?>)pt.getRawType();
		}
		return null;
	}

	/**
	 * Returns the fully-qualified class name for the specified object.
	 *
	 * @param value The object to get the class name for.
	 * @return The name of the class or <jk>null</jk> if the value was null.
	 */
	public static String className(Object value) {
		return value == null ? null : value instanceof Class<?> ? ((Class<?>)value).getName() : value.getClass().getName();
	}

	/**
	 * Returns the simple class name for the specified object.
	 *
	 * @param value The object to get the class name for.
	 * @return The name of the class or <jk>null</jk> if the value was null.
	 */
	public static String simpleClassName(Object value) {
		if (value == null)
			return null;
		if (value instanceof ClassInfo)
			return ((ClassInfo)value).getSimpleName();
		if (value instanceof ClassMeta)
			return ((ClassMeta<?>)value).getSimpleName();
		if (value instanceof Class)
			return ((Class<?>)value).getSimpleName();
		return value.getClass().getSimpleName();
	}
}
