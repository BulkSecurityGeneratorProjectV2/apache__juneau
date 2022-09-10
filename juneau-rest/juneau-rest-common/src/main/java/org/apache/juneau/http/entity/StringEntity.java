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
package org.apache.juneau.http.entity;

import static org.apache.juneau.internal.ArgUtils.*;
import static org.apache.juneau.internal.IOUtils.*;

import java.io.*;

import org.apache.juneau.http.header.*;
import org.apache.juneau.internal.*;

/**
 * A self contained, repeatable entity that obtains its content from a {@link String}.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc juneau-rest-common}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
@FluentSetters
public class StringEntity extends BasicHttpEntity {

	//-----------------------------------------------------------------------------------------------------------------
	// Static
	//-----------------------------------------------------------------------------------------------------------------

	private static final String EMPTY = "";

	//-----------------------------------------------------------------------------------------------------------------
	// Instance
	//-----------------------------------------------------------------------------------------------------------------

	private byte[] byteCache;

	/**
	 * Constructor.
	 */
	public StringEntity() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param contentType The entity content type.
	 * @param content The entity contents.
	 */
	public StringEntity(ContentType contentType, String content) {
		super(contentType, content);
	}

	/**
	 * Copy constructor.
	 *
	 * @param copyFrom The bean being copied.
	 */
	protected StringEntity(StringEntity copyFrom) {
		super(copyFrom);
	}

	@Override
	public StringEntity copy() {
		return new StringEntity(this);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Other methods
	//-----------------------------------------------------------------------------------------------------------------

	private String content() {
		return contentOrElse(EMPTY);
	}

	@Override /* AbstractHttpEntity */
	public String asString() throws IOException {
		return content();
	}

	@Override /* AbstractHttpEntity */
	public byte[] asBytes() throws IOException {
		if (isCached() && byteCache == null)
			byteCache = content().getBytes(getCharset());
		if (byteCache != null)
			return byteCache;
		return content().getBytes(getCharset());
	}

	@Override /* HttpEntity */
	public boolean isRepeatable() {
		return true;
	}

	@Override /* HttpEntity */
	public long getContentLength() {
		if (isCached())
			return asSafeBytes().length;
		long l = super.getContentLength();
		if (l != -1 || isSupplied())
			return l;
		String s = content();
		if (getCharset() == UTF8)
			for (int i = 0; i < s.length(); i++)
				if (s.charAt(i) > 127)
					return -1;
		return s.length();
	}

	@Override /* HttpEntity */
	public InputStream getContent() throws IOException {
		if (isCached())
			return new ByteArrayInputStream(asBytes());
		return new ReaderInputStream(new StringReader(content()), getCharset());
	}

	@Override /* HttpEntity */
	public void writeTo(OutputStream out) throws IOException {
		assertArgNotNull("out", out);
		if (isCached()) {
			out.write(asBytes());
		} else {
			OutputStreamWriter osw = new OutputStreamWriter(out, getCharset());
			osw.write(content());
			osw.flush();
		}
	}

	@Override /* HttpEntity */
	public boolean isStreaming() {
		return false;
	}

	// <FluentSetters>

	// </FluentSetters>
}

