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
package org.apache.juneau.examples.parser;

import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

import org.apache.juneau.*;
import org.apache.juneau.parser.*;

/**
 * Example parser that converts byte streams to {@link BufferedImage} objects.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../../overview-summary.html#juneau-rest-server.jrs.Marshalling">REST Marshalling</a>
 * </ul>
 */
@SuppressWarnings("javadoc")
public class ImageParser extends InputStreamParser {

	public ImageParser() {
		super(create().consumes("image/png,image/jpg"));
	}

	@Override /* Parser */
	@SuppressWarnings("unchecked")
	public <T> T doParse(ParserSession session, ParserPipe pipe, ClassMeta<T> type) throws IOException, ParseException {
		try (InputStream is = pipe.getInputStream()) {
			BufferedImage image = ImageIO.read(is);
			return (T)image;
		}
	}
}