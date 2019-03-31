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
package org.apache.juneau.rest;

import static org.apache.juneau.internal.ObjectUtils.*;
import static org.apache.juneau.internal.ClassUtils.*;
import static org.apache.juneau.rest.RestParamType.*;

import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.Method;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.juneau.*;
import org.apache.juneau.config.*;
import org.apache.juneau.dto.swagger.*;
import org.apache.juneau.http.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.http.annotation.Body;
import org.apache.juneau.http.annotation.FormData;
import org.apache.juneau.http.annotation.HasFormData;
import org.apache.juneau.http.annotation.HasQuery;
import org.apache.juneau.http.annotation.Header;
import org.apache.juneau.http.annotation.Path;
import org.apache.juneau.http.annotation.Query;
import org.apache.juneau.httppart.*;
import org.apache.juneau.httppart.bean.*;
import org.apache.juneau.internal.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.reflection.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.rest.exception.*;
import org.apache.juneau.rest.util.UrlPathPattern;
import org.apache.juneau.serializer.*;
import org.apache.juneau.utils.*;

/**
 * Default REST method parameter resolvers.
 *
 * <p>
 * Contains the default set of parameter resolvers for REST resource methods (i.e methods annotated with {@link RestResource @RestResource}).
 *
 * <h5 class='section'>See Also:</h5>
 * <ul>
 * 	<li class='link'>{@doc juneau-rest-server.RestMethod.MethodParameters}
 * </ul>
 */
class RestParamDefaults {

	/**
	 * Standard set of method parameter resolvers.
	 */
	static final Map<Class<?>,RestMethodParam> STANDARD_RESOLVERS;

	static {
		Map<Class<?>,RestMethodParam> m = new HashMap<>();

		@SuppressWarnings("rawtypes")
		Class[] r = new Class[] {

			// Standard top-level objects
			HttpServletRequestObject.class,
			RestRequestObject.class,
			HttpServletResponseObject.class,
			RestResponseObject.class,

			// Headers
			TimeZoneHeader.class,

			// Other objects
			ResourceBundleObject.class,
			MessageBundleObject.class,
			InputStreamObject.class,
			ServletInputStreamObject.class,
			ReaderObject.class,
			OutputStreamObject.class,
			ServletOutputStreamObject.class,
			WriterObject.class,
			RequestHeadersObject.class,
			RequestQueryObject.class,
			RequestFormDataObject.class,
			HttpMethodObject.class,
			RestLoggerObject.class,
			RestContextObject.class,
			ParserObject.class,
			ReaderParserObject.class,
			InputStreamParserObject.class,
			LocaleObject.class,
			SwaggerObject.class,
			RequestPathMatchObject.class,
			RequestBodyObject.class,
			ConfigObject.class,
			UriContextObject.class,
			UriResolverObject.class,
			RestRequestPropertiesObject.class
		};

		for (Class<?> c : r) {
			try {
				RestMethodParam mpr = (RestMethodParam)c.newInstance();
				m.put(mpr.forClass(), mpr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		STANDARD_RESOLVERS = Collections.unmodifiableMap(m);
	}

	//-------------------------------------------------------------------------------------------------------------------
	// Request / Response retrievers
	//-------------------------------------------------------------------------------------------------------------------

	static final class HttpServletRequestObject extends RestMethodParam {

		protected HttpServletRequestObject() {
			super(OTHER, HttpServletRequest.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) {
			return req;
		}
	}

	static final class HttpServletResponseObject extends RestMethodParam {

		protected HttpServletResponseObject() {
			super(OTHER, HttpServletResponse.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) {
			return res;
		}
	}

	static final class RestRequestObject extends RestMethodParam {

		protected RestRequestObject() {
			super(OTHER, RestRequest.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) {
			return req;
		}
	}

	static final class RestResponseObject extends RestMethodParam {

		protected RestResponseObject() {
			super(OTHER, RestResponse.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) {
			return res;
		}
	}

	//-------------------------------------------------------------------------------------------------------------------
	// Header retrievers
	//-------------------------------------------------------------------------------------------------------------------

	static final class TimeZoneHeader extends RestMethodParam {

		protected TimeZoneHeader() {
			super(HEADER, "Time-Zone", TimeZone.class);
		}

		@Override
		public TimeZone resolve(RestRequest req, RestResponse res) {
			return req.getHeaders().getTimeZone();
		}
	}

	//-------------------------------------------------------------------------------------------------------------------
	// Annotated retrievers
	//-------------------------------------------------------------------------------------------------------------------

	static final class PathObject extends RestMethodParam {
		private final HttpPartParser partParser;
		private final HttpPartSchema schema;

		protected PathObject(MethodParamInfo mpi, PropertyStore ps, UrlPathPattern pathPattern) {
			super(PATH, mpi, getName(mpi, pathPattern));
			this.schema = HttpPartSchema.create(Path.class, mpi);
			this.partParser = createPartParser(schema.getParser(), ps);
		}

		private static String getName(MethodParamInfo mpi, UrlPathPattern pathPattern) {
			for (Path h : mpi.getAnnotations(Path.class)) {
				if (! h.name().isEmpty())
					return h.name();
				if (! h.value().isEmpty())
					return h.value();
			}
			if (pathPattern != null) {
				int idx = 0;
				int i = mpi.getIndex();
				Method m = mpi.getMethod();

				for (int j = 0; j < i; j++)
					if (getMethodInfo(m).getParam(i).getAnnotation(Path.class) != null)
						idx++;

				String[] vars = pathPattern.getVars();
				if (vars.length <= idx)
					throw new InternalServerError("Number of attribute parameters in method ''{0}'' exceeds the number of URL pattern variables.", m);

				// Check for {#} variables.
				String idxs = String.valueOf(idx);
				for (int j = 0; j < vars.length; j++)
					if (StringUtils.isNumeric(vars[j]) && vars[j].equals(idxs))
						return vars[j];

				return pathPattern.getVars()[idx];
			}
			throw new InternalServerError("@Path used without name or value on method parameter ''{0}''.", mpi);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getPathMatch().get(partParser, schema, name, type);
		}
	}

	static final class BodyObject extends RestMethodParam {
		private final HttpPartSchema schema;

		protected BodyObject(MethodParamInfo mpi, PropertyStore ps) {
			super(BODY, mpi);
			this.schema = HttpPartSchema.create(Body.class, mpi);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getBody().schema(schema).asType(type);
		}
	}

	static final class HeaderObject extends RestMethodParam {
		private final HttpPartParser partParser;
		private final HttpPartSchema schema;

		protected HeaderObject(MethodParamInfo mpi, PropertyStore ps) {
			super(HEADER, mpi, getName(mpi));
			this.schema = HttpPartSchema.create(Header.class, mpi);
			this.partParser = createPartParser(schema.getParser(), ps);
		}

		private static String getName(MethodParamInfo mpi) {
			for (Header h : mpi.getAnnotations(Header.class)) {
				if (! h.name().isEmpty())
					return h.name();
				if (! h.value().isEmpty())
					return h.value();
			}
			throw new InternalServerError("@Header used without name or value on method parameter ''{0}''.", mpi);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getHeaders().get(partParser, schema, name, type);
		}
	}

	static final class RequestObject extends RestMethodParam {
		private final RequestBeanMeta meta;

		protected RequestObject(MethodParamInfo mpi, PropertyStore ps) {
			super(RESPONSE_BODY, mpi);
			this.meta = RequestBeanMeta.create(mpi, ps);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getRequest(meta);
		}
	}

	static final class ResponseHeaderObject extends RestMethodParam {
		final ResponsePartMeta meta;

		protected ResponseHeaderObject(MethodParamInfo mpi, PropertyStore ps) {
			super(RESPONSE_HEADER, mpi, getName(mpi));
			HttpPartSchema schema = HttpPartSchema.create(ResponseHeader.class, mpi);
			this.meta = new ResponsePartMeta(HttpPartType.HEADER, schema, createPartSerializer(schema.getSerializer(), ps));

			if (getTypeClass() != Value.class)
				throw new InternalServerError("Invalid type {0} specified with @ResponseHeader annotation.  It must be Value.", type);
		}

		private static String getName(MethodParamInfo mpi) {
			for (ResponseHeader h : mpi.getAnnotations(ResponseHeader.class)) {
				if (! h.name().isEmpty())
					return h.name();
				if (! h.value().isEmpty())
					return h.value();
			}
			throw new InternalServerError("@ResponseHeader used without name or value on method parameter ''{0}''.", mpi);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override /* RestMethodParam */
		public Object resolve(final RestRequest req, final RestResponse res) throws Exception {
			Value<Object> v = (Value<Object>)getTypeClass().newInstance();
			v.listener(new ValueListener() {
				@Override
				public void onSet(Object o) {
					try {
						ResponsePartMeta rpm = req.getResponseHeaderMeta(o);
						if (rpm == null)
							rpm = ResponseHeaderObject.this.meta;
						res.setHeader(new HttpPart(name, HttpPartType.HEADER, rpm.getSchema(), firstNonNull(rpm.getSerializer(), req.getPartSerializer()), req.getSerializerSessionArgs(), o));
					} catch (SerializeException | SchemaValidationException e) {
						throw new RuntimeException(e);
					}
				}
			});
			return v;
		}
	}

	static final class ResponseObject extends RestMethodParam {
		final ResponseBeanMeta meta;

		protected ResponseObject(MethodParamInfo mpi, PropertyStore ps) {
			super(RESPONSE, mpi);
			this.meta = ResponseBeanMeta.create(mpi, ps);
			if (getTypeClass() != Value.class)
				throw new InternalServerError("Invalid type {0} specified with @Response annotation.  It must be Value.", type);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override /* RestMethodParam */
		public Object resolve(final RestRequest req, final RestResponse res) throws Exception {
			Value<Object> v = (Value<Object>)c.newInstance();
			v.listener(new ValueListener() {
				@Override
				public void onSet(Object o) {
					ResponseBeanMeta meta = req.getResponseBeanMeta(o);
					if (meta == null)
						meta = ResponseObject.this.meta;
					res.setResponseMeta(meta);
					res.setOutput(o);
				}
			});
			return v;
		}
	}

	static class ResponseStatusObject extends RestMethodParam {

		protected ResponseStatusObject(ClassInfo t) {
			super(RESPONSE_STATUS, t);
			if (getTypeClass() != Value.class || Value.getParameterType(t.innerType()) != Integer.class)
				throw new InternalServerError("Invalid type {0} specified with @ResponseStatus annotation.  It must Value<Integer>.", type);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, final RestResponse res) throws Exception {
			Value<Object> v = (Value<Object>)c.newInstance();
			v.listener(new ValueListener() {
				@Override
				public void onSet(Object o) {
					res.setStatus(Integer.parseInt(o.toString()));
				}
			});
			return v;
		}
	}

	static final class MethodObject extends RestMethodParam {

		protected MethodObject(MethodInfo m, ClassInfo t) throws ServletException {
			super(OTHER, (MethodParamInfo)null);
			if (! t.is(String.class))
				throw new RestServletException("Use of @Method annotation on parameter that is not a String on method ''{0}''", m.inner());
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getMethod();
		}
	}

	static final class FormDataObject extends RestMethodParam {
		private final boolean multiPart;
		private final HttpPartParser partParser;
		private final HttpPartSchema schema;

		protected FormDataObject(MethodParamInfo mpi, PropertyStore ps) {
			super(FORM_DATA, mpi, getName(mpi));
			this.schema = HttpPartSchema.create(FormData.class, mpi);
			this.partParser = createPartParser(schema.getParser(), ps);
			this.multiPart = schema.getCollectionFormat() == HttpPartSchema.CollectionFormat.MULTI;

			if (multiPart && ! isCollection(type))
				throw new InternalServerError("Use of multipart flag on @FormData parameter that's not an array or Collection on method ''{0}''", mpi.getMethod());
		}

		private static String getName(MethodParamInfo mpi) {
			for (FormData h : mpi.getAnnotations(FormData.class)) {
				if (! h.name().isEmpty())
					return h.name();
				if (! h.value().isEmpty())
					return h.value();
			}
			throw new InternalServerError("@FormData used without name or value on method parameter ''{0}''.", mpi);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			if (multiPart)
				return req.getFormData().getAll(partParser, schema, name, type);
			return req.getFormData().get(partParser, schema, name, type);
		}
	}

	static final class QueryObject extends RestMethodParam {
		private final boolean multiPart;
		private final HttpPartParser partParser;
		private final HttpPartSchema schema;

		protected QueryObject(MethodParamInfo mpi, PropertyStore ps) {
			super(QUERY, mpi, getName(mpi));
			this.schema = HttpPartSchema.create(Query.class, mpi);
			this.partParser = createPartParser(schema.getParser(), ps);
			this.multiPart = schema.getCollectionFormat() == HttpPartSchema.CollectionFormat.MULTI;

			if (multiPart && ! isCollection(type))
				throw new InternalServerError("Use of multipart flag on @Query parameter that's not an array or Collection on method ''{0}''", mpi.getMethod());
		}

		private static String getName(MethodParamInfo mpi) {
			for (Query h : mpi.getAnnotations(Query.class)) {
				if (! h.name().isEmpty())
					return h.name();
				if (! h.value().isEmpty())
					return h.value();
			}
			throw new InternalServerError("@Query used without name or value on method param ''{0}''.", mpi);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			if (multiPart)
				return req.getQuery().getAll(partParser, schema, name, type);
			return req.getQuery().get(partParser, schema, name, type);
		}
	}

	static final class HasFormDataObject extends RestMethodParam {

		protected HasFormDataObject(MethodParamInfo mpi) throws ServletException {
			super(FORM_DATA, mpi, getName(mpi));
			if (getType() != Boolean.class && getType() != boolean.class)
				throw new RestServletException("Use of @HasForm annotation on parameter that is not a boolean on method ''{0}''", mpi.getMethod());
		}

		private static String getName(MethodParamInfo mpi) {
			for (HasFormData h : mpi.getAnnotations(HasFormData.class)) {
				if (! h.name().isEmpty())
					return h.name();
				if (! h.value().isEmpty())
					return h.value();
			}
			throw new InternalServerError("@HasFormData used without name or value on method parameter ''{o}''.", mpi);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			BeanSession bs = req.getBeanSession();
			return bs.convertToType(req.getFormData().containsKey(name), bs.getClassMeta(type));
		}
	}

	static final class HasQueryObject extends RestMethodParam {

		protected HasQueryObject(MethodParamInfo mpi) throws ServletException {
			super(QUERY, mpi, getName(mpi));
			if (getType() != Boolean.class && getType() != boolean.class)
				throw new RestServletException("Use of @HasQuery annotation on parameter that is not a boolean on method ''{0}''", mpi.getMethod());
		}

		private static String getName(MethodParamInfo mpi) {
			for (HasQuery h : mpi.getAnnotations(HasQuery.class)) {
				if (! h.name().isEmpty())
					return h.name();
				if (! h.value().isEmpty())
					return h.value();
			}
			throw new InternalServerError("@HasQuery used without name or value on method parameter ''{0}''.", mpi);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			BeanSession bs = req.getBeanSession();
			return bs.convertToType(req.getQuery().containsKey(name), bs.getClassMeta(type));
		}
	}

	static final class RestRequestPropertiesObject extends RestMethodParam {

		protected RestRequestPropertiesObject() {
			super(OTHER, RequestProperties.class);
		}

		@Override /* RestMethodParam */
		public RequestProperties resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getProperties();
		}
	}

	//-------------------------------------------------------------------------------------------------------------------
	// Other retrievers
	//-------------------------------------------------------------------------------------------------------------------

	static final class ResourceBundleObject extends RestMethodParam {

		protected ResourceBundleObject() {
			super(OTHER, ResourceBundle.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getMessageBundle();
		}
	}

	static final class MessageBundleObject extends RestMethodParam {

		protected MessageBundleObject() {
			super(OTHER, MessageBundle.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getMessageBundle();
		}
	}

	static final class InputStreamObject extends RestMethodParam {

		protected InputStreamObject() {
			super(OTHER, InputStream.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getInputStream();
		}
	}

	static final class ServletInputStreamObject extends RestMethodParam {

		protected ServletInputStreamObject() {
			super(OTHER, ServletInputStream.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getInputStream();
		}
	}

	static final class ReaderObject extends RestMethodParam {

		protected ReaderObject() {
			super(OTHER, Reader.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getReader();
		}
	}

	static final class OutputStreamObject extends RestMethodParam {

		protected OutputStreamObject() {
			super(OTHER, OutputStream.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return res.getOutputStream();
		}
	}

	static final class ServletOutputStreamObject extends RestMethodParam {

		protected ServletOutputStreamObject() {
			super(OTHER, ServletOutputStream.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return res.getOutputStream();
		}
	}

	static final class WriterObject extends RestMethodParam {

		protected WriterObject() {
			super(OTHER, Writer.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return res.getWriter();
		}
	}

	static final class RequestHeadersObject extends RestMethodParam {

		protected RequestHeadersObject() {
			super(OTHER, RequestHeaders.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getHeaders();
		}
	}

	static final class RequestQueryObject extends RestMethodParam {

		protected RequestQueryObject() {
			super(OTHER, RequestQuery.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getQuery();
		}
	}

	static final class RequestFormDataObject extends RestMethodParam {

		protected RequestFormDataObject() {
			super(OTHER, RequestFormData.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getFormData();
		}
	}

	static final class HttpMethodObject extends RestMethodParam {

		protected HttpMethodObject() {
			super(OTHER, HttpMethod.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getHttpMethod();
		}
	}

	static final class RestLoggerObject extends RestMethodParam {

		protected RestLoggerObject() {
			super(OTHER, RestLogger.class);
		}

		@Override /* RestMethodParam */
		public RestLogger resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getContext().getLogger();
		}
	}

	static final class RestContextObject extends RestMethodParam {

		protected RestContextObject() {
			super(OTHER, RestContext.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getContext();
		}
	}

	static final class ParserObject extends RestMethodParam {

		protected ParserObject() {
			super(OTHER, Parser.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getBody().getParser();
		}
	}

	static final class ReaderParserObject extends RestMethodParam {

		protected ReaderParserObject() {
			super(OTHER, ReaderParser.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getBody().getReaderParser();
		}
	}

	static final class InputStreamParserObject extends RestMethodParam {

		protected InputStreamParserObject() {
			super(OTHER, InputStreamParser.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getBody().getInputStreamParser();
		}
	}

	static final class LocaleObject extends RestMethodParam {

		protected LocaleObject() {
			super(OTHER, Locale.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getLocale();
		}
	}

	static final class SwaggerObject extends RestMethodParam {

		protected SwaggerObject() {
			super(OTHER, Swagger.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getSwagger();
		}
	}

	static final class RequestPathMatchObject extends RestMethodParam {

		protected RequestPathMatchObject() {
			super(OTHER, RequestPath.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getPathMatch();
		}
	}

	static final class RequestBodyObject extends RestMethodParam {

		protected RequestBodyObject() {
			super(OTHER, RequestBody.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getBody();
		}
	}

	static final class ConfigObject extends RestMethodParam {

		protected ConfigObject() {
			super(OTHER, Config.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getConfig();
		}
	}

	static final class UriContextObject extends RestMethodParam {

		protected UriContextObject() {
			super(OTHER, UriContext.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getUriContext();
		}
	}

	static final class UriResolverObject extends RestMethodParam {

		protected UriResolverObject() {
			super(OTHER, UriResolver.class);
		}

		@Override /* RestMethodParam */
		public Object resolve(RestRequest req, RestResponse res) throws Exception {
			return req.getUriResolver();
		}
	}

	//=================================================================================================================
	// Utility methods
	//=================================================================================================================

	static final boolean isCollection(Type t) {
		return BeanContext.DEFAULT.getClassMeta(t).isCollectionOrArray();
	}

	static final HttpPartParser createPartParser(Class<? extends HttpPartParser> p, PropertyStore ps) {
		return castOrCreate(HttpPartParser.class, p, true, ps);
	}

	static final HttpPartSerializer createPartSerializer(Class<? extends HttpPartSerializer> s, PropertyStore ps) {
		return castOrCreate(HttpPartSerializer.class, s, true, ps);
	}
}
