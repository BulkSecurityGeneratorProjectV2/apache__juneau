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
package org.apache.juneau.examples.rest.springboot;

import java.util.function.*;

/**
 * An example of a Spring bean that can be used for injecting messages into {@link HelloWorldResource}.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../../../overview-summary.html#juneau-rest-server-springboot.jrss.Overview">juneau-rest-server-springboot &gt; Overview</a>
 * </ul>
 */
public class HelloWorldMessageProvider implements Supplier<String> {

	private final String message;

	/**
	 * Constructor.
	 *
	 * @param message The message to display.
	 */
	public HelloWorldMessageProvider(String message) {
		this.message = message;
	}

	@Override
	public String get() {
		return message;
	}

}
