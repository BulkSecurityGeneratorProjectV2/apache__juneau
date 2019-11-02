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
package org.apache.juneau.rest.reshandlers;

import static org.apache.juneau.internal.ObjectUtils.*;
import static org.apache.juneau.internal.StringUtils.*;
import static org.apache.juneau.httppart.HttpPartType.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.juneau.http.*;
import org.apache.juneau.httppart.*;
import org.apache.juneau.httppart.bean.*;
import org.apache.juneau.internal.*;
import org.apache.juneau.rest.*;
import org.apache.juneau.http.exception.*;
import org.apache.juneau.rest.util.FinishablePrintWriter;
import org.apache.juneau.rest.util.FinishableServletOutputStream;
import org.apache.juneau.serializer.*;

/**
 * Response handler for POJOs not handled by other handlers.
 *
 * <p>
 * This uses the serializers defined on the response to serialize the POJO.
 *
 * <p>
 * The {@link Serializer} used is based on the <c>Accept</c> header on the request.
 *
 * <p>
 * The <c>Content-Type</c> header is set to the mime-type defined on the selected serializer based on the
 * <c>produces</c> value passed in through the constructor.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc juneau-rest-server.RestMethod.MethodReturnTypes}
 * </ul>
 */
public class DefaultHandler implements ResponseHandler {

	@SuppressWarnings("resource")
	@Override /* ResponseHandler */
	public boolean handle(RestRequest req, RestResponse res) throws IOException, InternalServerError, NotAcceptable {
		SerializerGroup g = res.getSerializers();
		String accept = req.getHeaders().getString("Accept", "");
		SerializerMatch sm = g.getSerializerMatch(accept);
		HttpPartSchema schema = null;

		Object o = res.getOutput();

		ResponseBeanMeta rm = res.getResponseMeta();
		if (rm == null)
			rm = req.getResponseBeanMeta(o);

		if (rm != null) {

			boolean isThrowable = rm.getClassMeta().isType(Throwable.class);
			if (isThrowable) {
				res.setHeaderSafe("Exception-Name", rm.getClassMeta().getName());
				res.setHeaderSafe("Exception-Message", ((Throwable)o).getMessage());
			}

			ResponseBeanPropertyMeta stm = rm.getStatusMethod();
			if (stm != null) {
				try {
					res.setStatus((int)stm.getGetter().invoke(o));
				} catch (Exception e) {
					throw new InternalServerError(e, "Could not get status.");
				}
			} else if (rm.getCode() != 0) {
				res.setStatus(rm.getCode());
			}

			for (ResponseBeanPropertyMeta hm : rm.getHeaderMethods()) {
				try {
					Object ho = hm.getGetter().invoke(o);
					String n = hm.getPartName();
					if ("*".equals(n) && ho instanceof Map) {
						@SuppressWarnings("rawtypes") Map m = (Map)ho;
						for (Object key : m.keySet()) {
							String k = stringify(key);
							Object v = m.get(key);
							HttpPartSchema s = hm.getSchema().getProperty(k);
							res.setHeader(new HttpPart(k, RESPONSE_HEADER, s, hm.getSerializer(req.getPartSerializer()), req.getSerializerSessionArgs(), v));
						}
					} else {
						res.setHeader(new HttpPart(n, RESPONSE_HEADER, hm.getSchema(), hm.getSerializer(req.getPartSerializer()), req.getSerializerSessionArgs(), ho));
					}
				} catch (Exception e) {
					throw new InternalServerError(e, "Could not set header ''{0}''", hm.getPartName());
				}
			}

			ResponseBeanPropertyMeta bm = rm.getBodyMethod();

			if (bm != null) {
				Method m = bm.getGetter();
				try {
					Class<?>[] pt = m.getParameterTypes();
					if (pt.length == 1) {
						Class<?> ptt = pt[0];
						if (ptt == OutputStream.class)
							m.invoke(o, res.getOutputStream());
						else if (ptt == Writer.class)
							m.invoke(o, res.getWriter());
						return true;
					}
					o = m.invoke(o);
				} catch (Exception e) {
					throw new InternalServerError(e, "Could not get body.");
				}
			}

			schema = rm.getSchema();
		}

		if (sm != null) {
			Serializer s = sm.getSerializer();
			MediaType mediaType = res.getMediaType();
			if (mediaType == null)
				mediaType = sm.getMediaType();

			MediaType responseType = s.getResponseContentType();
			if (responseType == null)
				responseType = mediaType;

			res.setContentType(responseType.toString());

			try {
				if (req.isPlainText())
					res.setContentType("text/plain");
				SerializerSession session = s.createSession(
					SerializerSessionArgs
						.create()
						.properties(req.getAttributes())
						.javaMethod(req.getJavaMethod())
						.locale(req.getLocale())
						.timeZone(req.getHeaders().getTimeZone())
						.mediaType(mediaType)
						.streamCharset(res.getCharset())
						.schema(schema)
						.debug(req.isDebug() ? true : null)
						.uriContext(req.getUriContext())
						.useWhitespace(req.isPlainText() ? true : null)
						.resolver(req.getVarResolverSession())
				);

				for (Map.Entry<String,String> h : session.getResponseHeaders().entrySet())
					res.setHeaderSafe(h.getKey(), h.getValue());

				if (! session.isWriterSerializer()) {
					if (req.isPlainText()) {
						FinishablePrintWriter w = res.getNegotiatedWriter();
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						session.serialize(o, baos);
						w.write(StringUtils.toSpacedHex(baos.toByteArray()));
						w.flush();
						w.finish();
					} else {
						FinishableServletOutputStream os = res.getNegotiatedOutputStream();
						session.serialize(o, os);
						os.flush();
						os.finish();
					}
				} else {
					FinishablePrintWriter w = res.getNegotiatedWriter();
					session.serialize(o, w);
					w.flush();
					w.finish();
				}
			} catch (SerializeException e) {
				throw new InternalServerError(e);
			}
			return true;
		}

		// Non-existent Accept or plain/text can just be serialized as-is.
		if (o != null && (isEmpty(accept) || accept.startsWith("text/plain"))) {
			String out = null;
			if (isEmpty(res.getContentType()))
				res.setContentType("text/plain");
			out = req.getBeanSession().getClassMetaForObject(o).toString(o);
			FinishablePrintWriter w = res.getNegotiatedWriter();
			w.append(out);
			w.flush();
			w.finish();
			return true;
		}

		throw new NotAcceptable(
			"Unsupported media-type in request header ''Accept'': ''{0}''\n\tSupported media-types: {1}",
			req.getHeaders().getString("Accept", ""), g.getSupportedMediaTypes()
		);
	}
}
