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
package org.apache.juneau.http.response;

import static org.apache.juneau.http.response.MultipleChoices.*;

import java.net.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.Header;
import org.apache.juneau.annotation.*;
import org.apache.juneau.http.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.http.header.*;
import org.apache.juneau.internal.*;

/**
 * Represents an <c>HTTP 300 Multiple Choices</c> response.
 *
 * <p>
 * Indicates multiple options for the resource from which the client may choose (via agent-driven content negotiation).
 * For example, this code could be used to present multiple video format options, to list files with different filename extensions, or to suggest word-sense disambiguation.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../../overview-summary.html#juneau-rest-common">juneau-rest-common</a>
 * </ul>
 */
@Response
@StatusCode(STATUS_CODE)
@Schema(description=REASON_PHRASE)
@FluentSetters
public class MultipleChoices extends BasicHttpResponse {

	/** HTTP status code */
	public static final int STATUS_CODE = 300;

	/** Reason phrase */
	public static final String REASON_PHRASE = "Multiple Choices";

	/** Default status line */
	private static final BasicStatusLine STATUS_LINE = BasicStatusLine.create(STATUS_CODE, REASON_PHRASE);

	/** Default unmodifiable instance */
	public static final MultipleChoices INSTANCE = new MultipleChoices().setUnmodifiable();

	/**
	 * Constructor.
	 */
	public MultipleChoices() {
		super(STATUS_LINE);
	}

	/**
	 * Copy constructor.
	 *
	 * @param copyFrom The bean to copy from.
	 */
	public MultipleChoices(MultipleChoices copyFrom) {
		super(copyFrom);
	}

	/**
	 * Constructor.
	 *
	 * <p>
	 * This is the constructor used when parsing an HTTP response.
	 *
	 * @param response The HTTP response to copy from.  Must not be <jk>null</jk>.
	 * @throws AssertionError If HTTP response status code does not match what was expected.
	 */
	public MultipleChoices(HttpResponse response) {
		super(response);
		assertStatusCode(response);
	}

	/**
	 * Creates a builder for this class initialized with the contents of this bean.
	 *
	 * @return A new builder bean.
	 */
	public MultipleChoices copy() {
		return new MultipleChoices(this);
	}

	// <FluentSetters>

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setContent(String value) {
		super.setContent(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setContent(HttpEntity value) {
		super.setContent(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setHeader2(Header value) {
		super.setHeader2(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setHeader2(String name, String value) {
		super.setHeader2(name, value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setHeaders(List<Header> values) {
		super.setHeaders(values);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setHeaders(HeaderList value) {
		super.setHeaders(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setHeaders2(Header...values) {
		super.setHeaders2(values);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setLocale2(Locale value) {
		super.setLocale2(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setLocation(String value) {
		super.setLocation(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setLocation(URI value) {
		super.setLocation(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setProtocolVersion(ProtocolVersion value) {
		super.setProtocolVersion(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setReasonPhrase2(String value) {
		super.setReasonPhrase2(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setReasonPhraseCatalog(ReasonPhraseCatalog value) {
		super.setReasonPhraseCatalog(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setStatusCode2(int value) {
		super.setStatusCode2(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setStatusLine(BasicStatusLine value) {
		super.setStatusLine(value);
		return this;
	}

	@Override /* GENERATED - org.apache.juneau.http.response.BasicHttpResponse */
	public MultipleChoices setUnmodifiable() {
		super.setUnmodifiable();
		return this;
	}

	// </FluentSetters>
}