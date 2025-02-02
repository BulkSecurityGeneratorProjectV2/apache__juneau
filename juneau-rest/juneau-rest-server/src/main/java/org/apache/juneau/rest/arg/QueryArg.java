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
package org.apache.juneau.rest.arg;

import static org.apache.juneau.internal.CollectionUtils.*;
import static org.apache.juneau.internal.StringUtils.*;
import static org.apache.juneau.http.annotation.QueryAnnotation.*;

import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.collections.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.httppart.*;
import org.apache.juneau.internal.*;
import org.apache.juneau.reflect.*;
import org.apache.juneau.rest.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.rest.httppart.*;

/**
 * Resolves method parameters and parameter types annotated with {@link Query} on {@link RestOp}-annotated Java methods.
 *
 * <p>
 * The parameter value is resolved using:
 * <p class='bjava'>
 * 	<jv>opSession</jv>
 * 		.{@link RestOpSession#getRequest() getRequest}()
 * 		.{@link RestRequest#getQueryParams() getQueryParams}()
 * 		.{@link RequestQueryParams#get(String) get}(<jv>name</jv>)
 * 		.{@link RequestQueryParam#as(Class) as}(<jv>type</jv>);
 * </p>
 *
 * <p>
 * {@link HttpPartSchema schema} is derived from the {@link Query} annotation.
 *
 * <p>
 * If the {@link Schema#collectionFormat()} value is {@link HttpPartCollectionFormat#MULTI}, then the data type can be a {@link Collection} or array.
 *
 * <ul class='seealso'>
 * 	<li class='link'><a class="doclink" href="../../../../../overview-summary.html#juneau-rest-server.jrs.RestOpAnnotatedMethods.jrs.JavaMethodParameters">Java Method Parameters</a>
 * </ul>
 */
public class QueryArg implements RestOpArg {
	private final boolean multi;
	private final HttpPartParser partParser;
	private final HttpPartSchema schema;
	private final String name, def;
	private final ClassInfo type;

	/**
	 * Static creator.
	 *
	 * @param paramInfo The Java method parameter being resolved.
	 * @param annotations The annotations to apply to any new part parsers.
	 * @return A new {@link QueryArg}, or <jk>null</jk> if the parameter is not annotated with {@link Query}.
	 */
	public static QueryArg create(ParamInfo paramInfo, AnnotationWorkList annotations) {
		if (paramInfo.hasAnnotation(Query.class) || paramInfo.getParameterType().hasAnnotation(Query.class))
			return new QueryArg(paramInfo, annotations);
		return null;
	}

	/**
	 * Constructor.
	 *
	 * @param pi The Java method parameter being resolved.
	 * @param annotations The annotations to apply to any new part parsers.
	 */
	protected QueryArg(ParamInfo pi, AnnotationWorkList annotations) {
		this.name = findName(pi).orElseThrow(() -> new ArgException(pi, "@Query used without name or value"));
		this.def = findDef(pi).orElse(null);
		this.type = pi.getParameterType();
		this.schema = HttpPartSchema.create(Query.class, pi);
		Class<? extends HttpPartParser> pp = schema.getParser();
		this.partParser = pp != null ? HttpPartParser.creator().type(pp).apply(annotations).create() : null;
		this.multi = schema.getCollectionFormat() == HttpPartCollectionFormat.MULTI;

		if (multi && ! type.isCollectionOrArray())
			throw new ArgException(pi, "Use of multipart flag on @Query parameter that is not an array or Collection");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override /* RestOpArg */
	public Object resolve(RestOpSession opSession) throws Exception {
		RestRequest req = opSession.getRequest();
		HttpPartParserSession ps = partParser == null ? req.getPartParserSession() : partParser.getPartSession();
		RequestQueryParams rh = req.getQueryParams();
		BeanSession bs = req.getBeanSession();
		ClassMeta<?> cm = bs.getClassMeta(type.innerType());

		if (multi) {
			Collection c = cm.isArray() ? list() : (Collection)(cm.canCreateNewInstance() ? cm.newInstance() : new JsonList());
			rh.getAll(name).stream().map(x -> x.parser(ps).schema(schema).as(cm.getElementType()).orElse(null)).forEach(x -> c.add(x));
			return cm.isArray() ? ArrayUtils.toArray(c, cm.getElementType().getInnerClass()) : c;
		}

		if (cm.isMapOrBean() && isOneOf(name, "*", "")) {
			JsonMap m = new JsonMap();
			rh.forEach(e -> m.put(e.getName(), e.parser(ps).schema(schema == null ? null : schema.getProperty(e.getName())).as(cm.getValueType()).orElse(null)));
			return req.getBeanSession().convertToType(m, cm);
		}

		return rh.getLast(name).parser(ps).schema(schema).def(def).as(type.innerType()).orElse(null);
	}
}
