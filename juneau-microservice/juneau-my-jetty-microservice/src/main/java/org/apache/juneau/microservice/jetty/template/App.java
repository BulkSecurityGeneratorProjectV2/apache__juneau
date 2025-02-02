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
package org.apache.juneau.microservice.jetty.template;

import org.apache.juneau.microservice.jetty.JettyMicroservice;

/**
 * Entry-point for your microservice.
 *
 * <p>
 * The {@link JettyMicroservice} class will locate the <code>my-microservice.cfg</code> file in the home directory and initialize
 * the resources and commands defined in that file.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../../../overview-summary.html#my-jetty-microservice">my-jetty-microservice</a>
 * </ul>
 */
public class App {

	/**
	 * Entry point method.
	 *
	 * @param args Command line arguments.
	 * @throws Exception Error occurred.
	 */
	public static void main(String[] args) throws Exception {
		JettyMicroservice
			.create()
			.args(args)
			.servlet(RootResources.class)
			.build()
			.start()
			.startConsole()
			.join();
	}
}
