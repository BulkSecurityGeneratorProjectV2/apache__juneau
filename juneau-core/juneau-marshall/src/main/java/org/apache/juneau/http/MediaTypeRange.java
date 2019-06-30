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
package org.apache.juneau.http;

import static org.apache.juneau.internal.CollectionUtils.*;

import java.util.*;
import java.util.Map.*;
import java.util.concurrent.*;

import org.apache.juneau.annotation.*;
import org.apache.juneau.internal.*;

/**
 * Describes a single type used in content negotiation between an HTTP client and server, as described in
 * Section 14.1 and 14.7 of RFC2616 (the HTTP/1.1 specification).
 *
 * <h5 class='section'>See Also:</h5>
 * <ul class='doctree'>
 * 	<li class='extlink'>{@doc RFC2616}
 * </ul>
 */
@BeanIgnore
public final class MediaTypeRange implements Comparable<MediaTypeRange>  {

	private static final MediaTypeRange[] DEFAULT = new MediaTypeRange[]{new MediaTypeRange("*/*")};
	private static final boolean NOCACHE = Boolean.getBoolean("juneau.nocache");
	private static final ConcurrentHashMap<String,MediaTypeRange[]> CACHE = new ConcurrentHashMap<>();

	private final MediaType mediaType;
	private final Float qValue;
	private final Map<String,Set<String>> extensions;

	/**
	 * Parses an <c>Accept</c> header value into an array of media ranges.
	 *
	 * <p>
	 * The returned media ranges are sorted such that the most acceptable media is available at ordinal position
	 * <js>'0'</js>, and the least acceptable at position n-1.
	 *
	 * <p>
	 * The syntax expected to be found in the referenced <c>value</c> complies with the syntax described in
	 * RFC2616, Section 14.1, as described below:
	 * <p class='bcode w800'>
	 * 	Accept         = "Accept" ":"
	 * 	                  #( media-range [ accept-params ] )
	 *
	 * 	media-range    = ( "*\/*"
	 * 	                  | ( type "/" "*" )
	 * 	                  | ( type "/" subtype )
	 * 	                  ) *( ";" parameter )
	 * 	accept-params  = ";" "q" "=" qvalue *( accept-extension )
	 * 	accept-extension = ";" token [ "=" ( token | quoted-string ) ]
	 * </p>
	 *
	 * @param value
	 * 	The value to parse.
	 * 	If <jk>null</jk> or empty, returns a single <c>MediaTypeRange</c> is returned that represents all types.
	 * @return
	 * 	The media ranges described by the string.
	 * 	The ranges are sorted such that the most acceptable media is available at ordinal position <js>'0'</js>, and
	 * 	the least acceptable at position n-1.
	 */
	public static MediaTypeRange[] parse(String value) {

		if (value == null || value.length() == 0)
			return DEFAULT;

		MediaTypeRange[] mtr = CACHE.get(value);
		if (mtr != null)
			return mtr;

		if (value.indexOf(',') == -1) {
			mtr = new MediaTypeRange[]{new MediaTypeRange(value)};
		} else {
			Set<MediaTypeRange> ranges = new TreeSet<>();
			for (String r : StringUtils.split(value)) {
				r = r.trim();
				if (r.isEmpty())
					continue;
				ranges.add(new MediaTypeRange(r));
			}
			mtr = ranges.toArray(new MediaTypeRange[ranges.size()]);
		}
		if (NOCACHE)
			return mtr;
		CACHE.putIfAbsent(value, mtr);
		return CACHE.get(value);
	}

	private MediaTypeRange(String token) {
		Builder b = new Builder(token);
		this.mediaType = b.mediaType;
		this.qValue = b.qValue;
		this.extensions = unmodifiableMap(b.extensions);
	}

	static final class Builder {
		MediaType mediaType;
		Float qValue = 1f;
		Map<String,Set<String>> extensions;

		Builder(String token) {

			token = token.trim();

			int i = token.indexOf(";q=");

			if (i == -1) {
				mediaType = MediaType.forString(token);
				return;
			}

			mediaType = MediaType.forString(token.substring(0, i));

			String[] tokens = token.substring(i+1).split(";");

			// Only the type of the range is specified
			if (tokens.length > 0) {
				boolean isInExtensions = false;
				for (int j = 0; j < tokens.length; j++) {
					String[] parm = tokens[j].split("=");
					if (parm.length == 2) {
						String k = parm[0], v = parm[1];
						if (isInExtensions) {
							if (extensions == null)
								extensions = new TreeMap<>();
							if (! extensions.containsKey(k))
								extensions.put(k, new TreeSet<String>());
							extensions.get(k).add(v);
						} else if (k.equals("q")) {
							qValue = new Float(v);
							isInExtensions = true;
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the media type enclosed by this media range.
	 *
	 * <h5 class='section'>Examples:</h5>
	 * <ul>
	 * 	<li><js>"text/html"</js>
	 * 	<li><js>"text/*"</js>
	 * 	<li><js>"*\/*"</js>
	 * </ul>
	 *
	 * @return The media type of this media range, lowercased, never <jk>null</jk>.
	 */
	public MediaType getMediaType() {
		return mediaType;
	}

	/**
	 * Returns the <js>'q'</js> (quality) value for this type, as described in Section 3.9 of RFC2616.
	 *
	 * <p>
	 * The quality value is a float between <c>0.0</c> (unacceptable) and <c>1.0</c> (most acceptable).
	 *
	 * <p>
	 * If 'q' value doesn't make sense for the context (e.g. this range was extracted from a <js>"content-*"</js>
	 * header, as opposed to <js>"accept-*"</js> header, its value will always be <js>"1"</js>.
	 *
	 * @return The 'q' value for this type, never <jk>null</jk>.
	 */
	public Float getQValue() {
		return qValue;
	}

	/**
	 * Returns the optional set of custom extensions defined for this type.
	 *
	 * <p>
	 * Values are lowercase and never <jk>null</jk>.
	 *
	 * @return The optional list of extensions, never <jk>null</jk>.
	 */
	public Map<String,Set<String>> getExtensions() {
		return extensions;
	}

	/**
	 * Provides a string representation of this media range, suitable for use as an <c>Accept</c> header value.
	 *
	 * <p>
	 * The literal text generated will be all lowercase.
	 *
	 * @return A media range suitable for use as an Accept header value, never <c>null</c>.
	 */
	@Override /* Object */
	public String toString() {
		StringBuffer sb = new StringBuffer().append(mediaType);

		// '1' is equivalent to specifying no qValue. If there's no extensions, then we won't include a qValue.
		if (qValue.floatValue() == 1.0) {
			if (! extensions.isEmpty()) {
				sb.append(";q=").append(qValue);
				for (Entry<String,Set<String>> e : extensions.entrySet()) {
					String k = e.getKey();
					for (String v : e.getValue())
						sb.append(';').append(k).append('=').append(v);
				}
			}
		} else {
			sb.append(";q=").append(qValue);
			for (Entry<String,Set<String>> e : extensions.entrySet()) {
				String k = e.getKey();
				for (String v : e.getValue())
					sb.append(';').append(k).append('=').append(v);
			}
		}
		return sb.toString();
	}

	/**
	 * Returns <jk>true</jk> if the specified object is also a <c>MediaType</c>, and has the same qValue, type,
	 * parameters, and extensions.
	 *
	 * @return <jk>true</jk> if object is equivalent.
	 */
	@Override /* Object */
	public boolean equals(Object o) {

		if (o == null || !(o instanceof MediaTypeRange))
			return false;

		if (this == o)
			return true;

		MediaTypeRange o2 = (MediaTypeRange) o;
		return qValue.equals(o2.qValue)
			&& mediaType.equals(o2.mediaType)
			&& extensions.equals(o2.extensions);
	}

	/**
	 * Returns a hash based on this instance's <c>media-type</c>.
	 *
	 * @return A hash based on this instance's <c>media-type</c>.
	 */
	@Override /* Object */
	public int hashCode() {
		return mediaType.hashCode();
	}

	/**
	 * Compares two MediaRanges for equality.
	 *
	 * <p>
	 * The values are first compared according to <c>qValue</c> values.
	 * Should those values be equal, the <c>type</c> is then lexicographically compared (case-insensitive) in
	 * ascending order, with the <js>"*"</js> type demoted last in that order.
	 * <c>MediaRanges</c> with the same type but different sub-types are compared - a more specific subtype is
	 * promoted over the 'wildcard' subtype.
	 * <c>MediaRanges</c> with the same types but with extensions are promoted over those same types with no
	 * extensions.
	 *
	 * @param o The range to compare to.  Never <jk>null</jk>.
	 */
	@Override /* Comparable */
	public int compareTo(MediaTypeRange o) {

		// Compare q-values.
		int qCompare = Float.compare(o.qValue, qValue);
		if (qCompare != 0)
			return qCompare;

		// Compare media-types.
		// Note that '*' comes alphabetically before letters, so just do a reverse-alphabetical comparison.
		int i = o.mediaType.toString().compareTo(mediaType.toString());
		return i;
	}
}
