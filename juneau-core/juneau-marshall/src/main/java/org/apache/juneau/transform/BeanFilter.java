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

import java.util.*;

import static org.apache.juneau.internal.ClassUtils.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;

/**
 * Parent class for all bean filters.
 *
 * <p>
 * Bean filters are used to control aspects of how beans are handled during serialization and parsing.
 *
 * <p>
 * Bean filters are created by {@link BeanFilterBuilder} which is the programmatic equivalent to the {@link Bean @Bean}
 * annotation.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc juneau-marshall.Transforms.BeanFilters}
 * </ul>
 */
public final class BeanFilter {

	private final Class<?> beanClass;
	private final Set<String> bpi, bpx, bpro, bpwo;
	private final PropertyNamer propertyNamer;
	private final Class<?> interfaceClass, stopClass;
	private final boolean sortProperties, fluentSetters;
	private final String typeName;
	private final Class<?>[] beanDictionary;
	private final PropertyFilter propertyFilter;

	/**
	 * Constructor.
	 */
	BeanFilter(BeanFilterBuilder<?> builder) {
		this.beanClass = builder.beanClass;
		this.typeName = builder.typeName;
		this.bpi = new LinkedHashSet<>(builder.bpi);
		this.bpx = new LinkedHashSet<>(builder.bpx);
		this.bpro = new LinkedHashSet<>(builder.bpro);
		this.bpwo = new LinkedHashSet<>(builder.bpwo);
		this.interfaceClass = builder.interfaceClass;
		this.stopClass = builder.stopClass;
		this.sortProperties = builder.sortProperties;
		this.fluentSetters = builder.fluentSetters;
		this.propertyNamer = castOrCreate(PropertyNamer.class, builder.propertyNamer);
		this.beanDictionary =
			builder.dictionary == null
			? null
			: builder.dictionary.toArray(new Class<?>[builder.dictionary.size()]);
		this.propertyFilter =
			builder.propertyFilter == null
			? PropertyFilter.DEFAULT
			: castOrCreate(PropertyFilter.class, builder.propertyFilter);
	}

	/**
	 * Returns the bean class that this filter applies to.
	 *
	 * @return The bean class that this filter applies to.
	 */
	public Class<?> getBeanClass() {
		return beanClass;
	}

	/**
	 * Returns the dictionary name associated with this bean.
	 *
	 * @return The dictionary name associated with this bean, or <jk>null</jk> if no name is defined.
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Returns the bean dictionary defined on this bean.
	 *
	 * @return The bean dictionary defined on this bean, or <jk>null</jk> if no bean dictionary is defined.
	 */
	public Class<?>[] getBeanDictionary() {
		return beanDictionary;
	}

	/**
	 * Returns the set and order of names of properties associated with a bean class.
	 *
	 * @return
	 * 	The name of the properties associated with a bean class, or <jk>null</jk> if all bean properties should
	 * 	be used.
	 */
	public Set<String> getBpi() {
		return bpi;
	}

	/**
	 * Returns the list of properties to ignore on a bean.
	 *
	 * @return The name of the properties to ignore on a bean, or <jk>null</jk> to not ignore any properties.
	 */
	public Set<String> getBpx() {
		return bpx;
	}

	public Set<String> getBpro() {
		return bpro;
	}

	public Set<String> getBpwo() {
		return bpwo;
	}

	/**
	 * Returns <jk>true</jk> if the properties defined on this bean class should be ordered alphabetically.
	 *
	 * <p>
	 * This method is only used when the {@link #getBpi()} method returns <jk>null</jk>.
	 * Otherwise, the ordering of the properties in the returned value is used.
	 *
	 * @return <jk>true</jk> if bean properties should be sorted.
	 */
	public boolean isSortProperties() {
		return sortProperties;
	}

	/**
	 * Returns <jk>true</jk> if we should find fluent setters.
	 *
	 * @return <jk>true</jk> if fluent setters should be found.
	 */
	public boolean isFluentSetters() {
		return fluentSetters;
	}

	/**
	 * Returns the {@link PropertyNamer} associated with the bean to tailor the names of bean properties.
	 *
	 * @return The property namer class, or <jk>null</jk> if no property namer is associated with this bean property.
	 */
	public PropertyNamer getPropertyNamer() {
		return propertyNamer;
	}

	/**
	 * Returns the interface class associated with this class.
	 *
	 * @return The interface class associated with this class, or <jk>null</jk> if no interface class is associated.
	 */
	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	/**
	 * Returns the stop class associated with this class.
	 *
	 * @return The stop class associated with this class, or <jk>null</jk> if no stop class is associated.
	 */
	public Class<?> getStopClass() {
		return stopClass;
	}

	/**
	 * Calls the {@link PropertyFilter#readProperty(Object, String, Object)} method on the registered property filters.
	 *
	 * @param bean The bean from which the property was read.
	 * @param name The property name.
	 * @param value The value just extracted from calling the bean getter.
	 * @return The value to serialize.  Default is just to return the existing value.
	 */
	public Object readProperty(Object bean, String name, Object value) {
		return propertyFilter.readProperty(bean, name, value);
	}

	/**
	 * Calls the {@link PropertyFilter#writeProperty(Object, String, Object)} method on the registered property filters.
	 *
	 * @param bean The bean from which the property was read.
	 * @param name The property name.
	 * @param value The value just parsed.
	 * @return The value to serialize.  Default is just to return the existing value.
	 */
	public Object writeProperty(Object bean, String name, Object value) {
		return propertyFilter.writeProperty(bean, name, value);
	}
}
