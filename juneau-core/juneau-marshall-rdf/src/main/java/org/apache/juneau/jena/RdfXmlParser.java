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
package org.apache.juneau.jena;

/**
 * Subclass of {@link RdfParser} for parsing RDF in standard XML notation.
 *
 * <ul class='notes'>
 * 	<li class='note'>This class is thread safe and reusable.
 * </ul>
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jmr.RdfDetails}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
public class RdfXmlParser extends RdfParser {

	//-------------------------------------------------------------------------------------------------------------------
	// Static
	//-------------------------------------------------------------------------------------------------------------------

	/** Default XML parser, all default settings.*/
	public static final RdfXmlParser DEFAULT = new RdfXmlParser(create());

	/**
	 * Creates a new builder for this object.
	 *
	 * @return A new builder.
	 */
	public static RdfParser.Builder create() {
		return RdfParser.create().xml();
	}

	//-------------------------------------------------------------------------------------------------------------------
	// Instance
	//-------------------------------------------------------------------------------------------------------------------


	/**
	 * Constructor.
	 *
	 * @param builder The builder for this object.
	 */
	public RdfXmlParser(RdfParser.Builder builder) {
		super(builder.xml().consumes("text/xml+rdf"));
	}
}