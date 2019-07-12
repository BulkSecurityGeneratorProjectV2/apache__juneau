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
package org.apache.juneau.rest;


import java.text.*;
import java.util.logging.*;

/**
 * Logging utility class.
 *
 * <p>
 * Disables logging entirely.
 *
 * <h5 class='section'>See Also:</h5>
 * <ul>
 * 	<li class='link'>{@doc juneau-rest-server.LoggingAndDebugging}
 * </ul>
 * @deprecated Use {@link RestCallLogger}
 */
@Deprecated
public class NoOpRestLogger extends BasicRestLogger {

	/**
	 * Constructor.
	 */
	public NoOpRestLogger() {
		super(null);
	}

	/**
	 * Log a message to the logger.
	 *
	 * <p>
	 * Subclasses can override this method if they wish to log messages using a library other than Java Logging
	 * (e.g. Apache Commons Logging).
	 *
	 * @param level The log level.
	 * @param cause The cause.
	 * @param msg The message to log.
	 * @param args Optional {@link MessageFormat}-style arguments.
	 */
	@Override /* RestLogger */
	public void log(Level level, Throwable cause, String msg, Object...args) {}
}
