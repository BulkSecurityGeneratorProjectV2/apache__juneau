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

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * System utilities.
 */
public class SystemUtils {

	static final List<Supplier<String>> SHUTDOWN_MESSAGES = new CopyOnWriteArrayList<>();
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (! Boolean.getBoolean("SystemUtils.quiet"))
				SHUTDOWN_MESSAGES.forEach(x -> System.out.println(x.get()));
			}
		});
	}

	/**
	 * Adds a console message to display when the JVM shuts down.
	 *
	 * @param message The message to display.
	 */
	public static void shutdownMessage(Supplier<String> message) {
		SHUTDOWN_MESSAGES.add(message);
	}

	/**
	 * Returns the first non-<jk>null</jk> system property.
	 *
	 * @param def
	 * 	The default value if none are found.
	 * 	Can be <jk>null</jk>.
	 * @param keys
	 * 	The system properties to look for.
	 * @return
	 * 	The first non-<jk>null</jk> system property, or the default value if non were found.
	 */
	public static String getFirstString(String def, String...keys) {
		for (String key : keys) {
			String v = System.getProperty(key);
			if (v != null)
				return v;
		}
		return def;
	}

	/**
	 * Returns the first non-<jk>null</jk> boolean system property.
	 *
	 * @param def
	 * 	The default value if none are found.
	 * 	Can be <jk>null</jk>.
	 * @param keys
	 * 	The system properties to look for.
	 * @return
	 * 	The first non-<jk>null</jk> system property, or the default value if non were found.
	 */
	public static Boolean getFirstBoolean(Boolean def, String...keys) {
		String s = getFirstString(null, keys);
		return s == null ? def : Boolean.parseBoolean(s);
	}

	/**
	 * Returns the first non-<jk>null</jk> integer system property.
	 *
	 * @param def
	 * 	The default value if none are found.
	 * 	Can be <jk>null</jk>.
	 * @param keys
	 * 	The system properties to look for.
	 * @return
	 * 	The first non-<jk>null</jk> system property, or the default value if non were found.
	 */
	public static Integer getFirstInteger(Integer def, String...keys) {
		String s = getFirstString(null, keys);
		return s == null ? def : Integer.parseInt(s);
	}

	/**
	 * Convenience method for setting a system property value.
	 *
	 * @param key The system property key.
	 * @param value The system property value.
	 * @param overwrite Overwrite the previous value if it exists.
	 */
	public static void setProperty(String key, Object value, boolean overwrite) {
		if (value != null) {
			if (System.getProperty(key) == null || overwrite)
				System.setProperty(key, StringUtils.stringify(value));
		}
	}

	/**
	 * Convenience method for setting a system property value.
	 *
	 * @param key The system property key.
	 * @param value The system property value.
	 */
	public static void setProperty(String key, Object value) {
		setProperty(key, value, true);
	}
}
