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
package org.apache.juneau.http.annotation;

/**
 * Static strings used for Swagger parameter format types.
 *
 * <ul class='seealso'>
 * </ul>
 */
public class FormatType {

	@SuppressWarnings("javadoc")
	public static final String
		INT32 = "int32",
		INT64 = "int64",
		FLOAT = "float",
		DOUBLE = "double",
		BYTE = "byte",
		BINARY = "binary",
		DATE = "date",
		DATE_TIME = "date-time",
		PASSWORD = "password",
		UON = "uon";
}
