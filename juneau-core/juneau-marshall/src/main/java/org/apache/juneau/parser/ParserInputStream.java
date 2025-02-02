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

import java.io.*;

/**
 * Input stream meant to be used as input for stream-based parsers.
 *
 * <p>
 * Keeps track of current byte position.
 *
 * <ul class='notes'>
 * 	<li class='warn'>This class is not thread safe.
 * </ul>
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-marshall.jm.SerializersAndParsers">Serializers and Parsers</a>
 * </ul>
 */
public class ParserInputStream extends InputStream implements Positionable {

	private final InputStream is;
	int pos = 0;

	/**
	 * Constructor.
	 *
	 * @param pipe The parser input.
	 * @throws IOException Thrown by underlying stream.
	 */
	protected ParserInputStream(ParserPipe pipe) throws IOException {
		this.is = pipe.getInputStream();
		pipe.setPositionable(this);
	}

	@Override /* InputStream */
	public int read() throws IOException {
		int i = is.read();
		if (i > 0)
			pos++;
		return i;
	}

	@Override /* Positionable */
	public Position getPosition() {
		return new Position(pos);
	}
}
