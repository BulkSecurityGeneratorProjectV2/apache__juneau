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
package org.apache.juneau.parser;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.collections.*;

/**
 * Subclass of {@link Parser} for byte-based parsers.
 * {@review}
 *
 * <h5 class='topic'>Description</h5>
 *
 * This class is typically the parent class of all byte-based parsers.
 * It has 1 abstract method to implement...
 * <ul>
 * 	<li><c>parse(InputStream, ClassMeta, Parser)</c>
 * </ul>
  */
@ConfigurableContext
public abstract class InputStreamParser extends Parser {

	//-------------------------------------------------------------------------------------------------------------------
	// Instance
	//-------------------------------------------------------------------------------------------------------------------

	final BinaryFormat binaryFormat;

	/**
	 * Constructor.
	 *
	 * @param builder The builder for this object.
	 */
	protected InputStreamParser(InputStreamParserBuilder builder) {
		super(builder);
		binaryFormat = builder.binaryFormat;
	}

	@Override /* Parser */
	public final boolean isReaderParser() {
		return false;
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Binary input format.
	 *
	 * @see InputStreamParserBuilder#binaryFormat(BinaryFormat)
	 * @return
	 * 	The format to use when converting strings to byte arrays.
	 */
	protected final BinaryFormat getBinaryFormat() {
		return binaryFormat;
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Other methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* Context */
	public OMap toMap() {
		return super.toMap()
			.a(
				"InputStreamParser",
				OMap
					.create()
					.filtered()
					.a("binaryFormat", binaryFormat)
			);
	}
}
