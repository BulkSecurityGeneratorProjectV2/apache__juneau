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
package org.apache.juneau.transforms;

import static org.apache.juneau.testutils.TestUtils.*;

import java.lang.reflect.*;
import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.serializer.*;
import org.apache.juneau.testutils.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

/**
 * Exhaustive serialization tests for the CalendarSwap class.
 */
@RunWith(Parameterized.class)
@SuppressWarnings({})
public class CalendarSwapComboTest extends ComboRoundTripTest {

	private static Calendar singleDate = new GregorianCalendar(TimeZone.getTimeZone("PST"));
	static {
		singleDate.setTimeInMillis(0);
		singleDate.set(1901, 2, 3, 10, 11, 12);
	}

	private static Calendar[] dateArray = new Calendar[]{singleDate};

	private static ObjectMap dateMap = new ObjectMap().append("foo", singleDate);


	@Parameterized.Parameters
	public static Collection<Object[]> getParameters() {
		return Arrays.asList(new Object[][] {
			{	/* 0 */
				new ComboInput2<Calendar>(
					"CalendarSwap.ToString/singleDate",
					Calendar.class,
					singleDate,
					CalendarSwap.ToString.class,
					/* Json */		"'Sun Mar 03 10:11:12 PST 1901'",
					/* JsonT */		"'Sun Mar 03 10:11:12 PST 1901'",
					/* JsonR */		"'Sun Mar 03 10:11:12 PST 1901'",
					/* Xml */		"<string>Sun Mar 03 10:11:12 PST 1901</string>",
					/* XmlT */		"<string>Sun Mar 03 10:11:12 PST 1901</string>",
					/* XmlR */		"<string>Sun Mar 03 10:11:12 PST 1901</string>\n",
					/* XmlNs */		"<string>Sun Mar 03 10:11:12 PST 1901</string>",
					/* Html */		"<string>Sun Mar 03 10:11:12 PST 1901</string>",
					/* HtmlT */		"<string>Sun Mar 03 10:11:12 PST 1901</string>",
					/* HtmlR */		"<string>Sun Mar 03 10:11:12 PST 1901</string>",
					/* Uon */		"'Sun Mar 03 10:11:12 PST 1901'",
					/* UonT */		"'Sun Mar 03 10:11:12 PST 1901'",
					/* UonR */		"'Sun Mar 03 10:11:12 PST 1901'",
					/* UrlEnc */	"_value='Sun+Mar+03+10:11:12+PST+1901'",
					/* UrlEncT */	"_value='Sun+Mar+03+10:11:12+PST+1901'",
					/* UrlEncR */	"_value='Sun+Mar+03+10:11:12+PST+1901'",
					/* MsgPack */	"BC53756E204D61722030332031303A31313A3132205053542031393031",
					/* MsgPackT */	"BC53756E204D61722030332031303A31313A3132205053542031393031",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<j:value>Sun Mar 03 10:11:12 PST 1901</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<j:value>Sun Mar 03 10:11:12 PST 1901</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <j:value>Sun Mar 03 10:11:12 PST 1901</j:value>\n  </rdf:Description>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(Calendar o) {
						assertInstanceOf(Calendar.class, o);
					}
				}
			},
			{	/* 1 */
				new ComboInput2<Calendar[]>(
					"CalendarSwap.ToString/dateArray",
					Calendar[].class,
					dateArray,
					CalendarSwap.ToString.class,
					/* Json */		"['Sun Mar 03 10:11:12 PST 1901']",
					/* JsonT */		"['Sun Mar 03 10:11:12 PST 1901']",
					/* JsonR */		"[\n\t'Sun Mar 03 10:11:12 PST 1901'\n]",
					/* Xml */		"<array><string>Sun Mar 03 10:11:12 PST 1901</string></array>",
					/* XmlT */		"<array><string>Sun Mar 03 10:11:12 PST 1901</string></array>",
					/* XmlR */		"<array>\n\t<string>Sun Mar 03 10:11:12 PST 1901</string>\n</array>\n",
					/* XmlNs */		"<array><string>Sun Mar 03 10:11:12 PST 1901</string></array>",
					/* Html */		"<ul><li>Sun Mar 03 10:11:12 PST 1901</li></ul>",
					/* HtmlT */		"<ul><li>Sun Mar 03 10:11:12 PST 1901</li></ul>",
					/* HtmlR */		"<ul>\n\t<li>Sun Mar 03 10:11:12 PST 1901</li>\n</ul>\n",
					/* Uon */		"@('Sun Mar 03 10:11:12 PST 1901')",
					/* UonT */		"@('Sun Mar 03 10:11:12 PST 1901')",
					/* UonR */		"@(\n\t'Sun Mar 03 10:11:12 PST 1901'\n)",
					/* UrlEnc */	"0='Sun+Mar+03+10:11:12+PST+1901'",
					/* UrlEncT */	"0='Sun+Mar+03+10:11:12+PST+1901'",
					/* UrlEncR */	"0='Sun+Mar+03+10:11:12+PST+1901'",
					/* MsgPack */	"91BC53756E204D61722030332031303A31313A3132205053542031393031",
					/* MsgPackT */	"91BC53756E204D61722030332031303A31313A3132205053542031393031",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Seq>\n<rdf:li>Sun Mar 03 10:11:12 PST 1901</rdf:li>\n</rdf:Seq>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Seq>\n<rdf:li>Sun Mar 03 10:11:12 PST 1901</rdf:li>\n</rdf:Seq>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Seq>\n    <rdf:li>Sun Mar 03 10:11:12 PST 1901</rdf:li>\n  </rdf:Seq>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(Calendar[] o) {
						assertInstanceOf(Calendar.class, o[0]);
					}
				}
			},
			{	/* 2 */
				new ComboInput2<ObjectMap>(
					"CalendarSwap.ToString",
					getType(Map.class,String.class,Calendar.class),
					dateMap,
					CalendarSwap.ToString.class,
					/* Json */		"{foo:'Sun Mar 03 10:11:12 PST 1901'}",
					/* JsonT */		"{foo:'Sun Mar 03 10:11:12 PST 1901'}",
					/* JsonR */		"{\n\tfoo: 'Sun Mar 03 10:11:12 PST 1901'\n}",
					/* Xml */		"<object><foo>Sun Mar 03 10:11:12 PST 1901</foo></object>",
					/* XmlT */		"<object><foo>Sun Mar 03 10:11:12 PST 1901</foo></object>",
					/* XmlR */		"<object>\n\t<foo>Sun Mar 03 10:11:12 PST 1901</foo>\n</object>\n",
					/* XmlNs */		"<object><foo>Sun Mar 03 10:11:12 PST 1901</foo></object>",
					/* Html */		"<table><tr><td>foo</td><td>Sun Mar 03 10:11:12 PST 1901</td></tr></table>",
					/* HtmlT */		"<table><tr><td>foo</td><td>Sun Mar 03 10:11:12 PST 1901</td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>foo</td>\n\t\t<td>Sun Mar 03 10:11:12 PST 1901</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(foo='Sun Mar 03 10:11:12 PST 1901')",
					/* UonT */		"(foo='Sun Mar 03 10:11:12 PST 1901')",
					/* UonR */		"(\n\tfoo='Sun Mar 03 10:11:12 PST 1901'\n)",
					/* UrlEnc */	"foo='Sun+Mar+03+10:11:12+PST+1901'",
					/* UrlEncT */	"foo='Sun+Mar+03+10:11:12+PST+1901'",
					/* UrlEncR */	"foo='Sun+Mar+03+10:11:12+PST+1901'",
					/* MsgPack */	"81A3666F6FBC53756E204D61722030332031303A31313A3132205053542031393031",
					/* MsgPackT */	"81A3666F6FBC53756E204D61722030332031303A31313A3132205053542031393031",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:foo>Sun Mar 03 10:11:12 PST 1901</jp:foo>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:foo>Sun Mar 03 10:11:12 PST 1901</jp:foo>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:foo>Sun Mar 03 10:11:12 PST 1901</jp:foo>\n  </rdf:Description>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(ObjectMap o) {
						assertInstanceOf(Calendar.class, o.get("foo"));
					}
				}
			},
			{	/* 3 */
				new ComboInput2<Calendar>(
					"CalendarSwap.ISO8601DT/singleDate",
					Calendar.class,
					singleDate,
					CalendarSwap.ISO8601DT.class,
					/* Json */		"'1901-03-03T10:11:12-08:00'",
					/* JsonT */		"'1901-03-03T10:11:12-08:00'",
					/* JsonR */		"'1901-03-03T10:11:12-08:00'",
					/* Xml */		"<string>1901-03-03T10:11:12-08:00</string>",
					/* XmlT */		"<string>1901-03-03T10:11:12-08:00</string>",
					/* XmlR */		"<string>1901-03-03T10:11:12-08:00</string>\n",
					/* XmlNs */		"<string>1901-03-03T10:11:12-08:00</string>",
					/* Html */		"<string>1901-03-03T10:11:12-08:00</string>",
					/* HtmlT */		"<string>1901-03-03T10:11:12-08:00</string>",
					/* HtmlR */		"<string>1901-03-03T10:11:12-08:00</string>",
					/* Uon */		"1901-03-03T10:11:12-08:00",
					/* UonT */		"1901-03-03T10:11:12-08:00",
					/* UonR */		"1901-03-03T10:11:12-08:00",
					/* UrlEnc */	"_value=1901-03-03T10:11:12-08:00",
					/* UrlEncT */	"_value=1901-03-03T10:11:12-08:00",
					/* UrlEncR */	"_value=1901-03-03T10:11:12-08:00",
					/* MsgPack */	"B9313930312D30332D30335431303A31313A31322D30383A3030",
					/* MsgPackT */	"B9313930312D30332D30335431303A31313A31322D30383A3030",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<j:value>1901-03-03T10:11:12-08:00</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<j:value>1901-03-03T10:11:12-08:00</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <j:value>1901-03-03T10:11:12-08:00</j:value>\n  </rdf:Description>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(Calendar o) {
						assertInstanceOf(Calendar.class, o);
					}
				}
			},
			{	/* 4 */
				new ComboInput2<Calendar[]>(
					"CalendarSwap.ISO8601DT/dateArray",
					Calendar[].class,
					dateArray,
					CalendarSwap.ISO8601DT.class,
					/* Json */		"['1901-03-03T10:11:12-08:00']",
					/* JsonT */		"['1901-03-03T10:11:12-08:00']",
					/* JsonR */		"[\n\t'1901-03-03T10:11:12-08:00'\n]",
					/* Xml */		"<array><string>1901-03-03T10:11:12-08:00</string></array>",
					/* XmlT */		"<array><string>1901-03-03T10:11:12-08:00</string></array>",
					/* XmlR */		"<array>\n\t<string>1901-03-03T10:11:12-08:00</string>\n</array>\n",
					/* XmlNs */		"<array><string>1901-03-03T10:11:12-08:00</string></array>",
					/* Html */		"<ul><li>1901-03-03T10:11:12-08:00</li></ul>",
					/* HtmlT */		"<ul><li>1901-03-03T10:11:12-08:00</li></ul>",
					/* HtmlR */		"<ul>\n\t<li>1901-03-03T10:11:12-08:00</li>\n</ul>\n",
					/* Uon */		"@(1901-03-03T10:11:12-08:00)",
					/* UonT */		"@(1901-03-03T10:11:12-08:00)",
					/* UonR */		"@(\n\t1901-03-03T10:11:12-08:00\n)",
					/* UrlEnc */	"0=1901-03-03T10:11:12-08:00",
					/* UrlEncT */	"0=1901-03-03T10:11:12-08:00",
					/* UrlEncR */	"0=1901-03-03T10:11:12-08:00",
					/* MsgPack */	"91B9313930312D30332D30335431303A31313A31322D30383A3030",
					/* MsgPackT */	"91B9313930312D30332D30335431303A31313A31322D30383A3030",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Seq>\n<rdf:li>1901-03-03T10:11:12-08:00</rdf:li>\n</rdf:Seq>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Seq>\n<rdf:li>1901-03-03T10:11:12-08:00</rdf:li>\n</rdf:Seq>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Seq>\n    <rdf:li>1901-03-03T10:11:12-08:00</rdf:li>\n  </rdf:Seq>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(Calendar[] o) {
						assertInstanceOf(Calendar.class, o[0]);
					}
				}
			},
			{	/* 5 */
				new ComboInput2<ObjectMap>(
					"CalendarSwap.ISO8601DT/dateMap",
					getType(Map.class,String.class,Calendar.class),
					dateMap,
					CalendarSwap.ISO8601DT.class,
					/* Json */		"{foo:'1901-03-03T10:11:12-08:00'}",
					/* JsonT */		"{foo:'1901-03-03T10:11:12-08:00'}",
					/* JsonR */		"{\n\tfoo: '1901-03-03T10:11:12-08:00'\n}",
					/* Xml */		"<object><foo>1901-03-03T10:11:12-08:00</foo></object>",
					/* XmlT */		"<object><foo>1901-03-03T10:11:12-08:00</foo></object>",
					/* XmlR */		"<object>\n\t<foo>1901-03-03T10:11:12-08:00</foo>\n</object>\n",
					/* XmlNs */		"<object><foo>1901-03-03T10:11:12-08:00</foo></object>",
					/* Html */		"<table><tr><td>foo</td><td>1901-03-03T10:11:12-08:00</td></tr></table>",
					/* HtmlT */		"<table><tr><td>foo</td><td>1901-03-03T10:11:12-08:00</td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>foo</td>\n\t\t<td>1901-03-03T10:11:12-08:00</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(foo=1901-03-03T10:11:12-08:00)",
					/* UonT */		"(foo=1901-03-03T10:11:12-08:00)",
					/* UonR */		"(\n\tfoo=1901-03-03T10:11:12-08:00\n)",
					/* UrlEnc */	"foo=1901-03-03T10:11:12-08:00",
					/* UrlEncT */	"foo=1901-03-03T10:11:12-08:00",
					/* UrlEncR */	"foo=1901-03-03T10:11:12-08:00",
					/* MsgPack */	"81A3666F6FB9313930312D30332D30335431303A31313A31322D30383A3030",
					/* MsgPackT */	"81A3666F6FB9313930312D30332D30335431303A31313A31322D30383A3030",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:foo>1901-03-03T10:11:12-08:00</jp:foo>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:foo>1901-03-03T10:11:12-08:00</jp:foo>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:foo>1901-03-03T10:11:12-08:00</jp:foo>\n  </rdf:Description>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(ObjectMap o) {
						assertInstanceOf(Calendar.class, o.get("foo"));
					}
				}
			},
			{	/* 6 */
				new ComboInput2<Calendar>(
					"CalendarSwap.RFC2822DTZ/singleDate",
					Calendar.class,
					singleDate,
					CalendarSwap.RFC2822DTZ.class,
					/* Json */		"'Sun, 03 Mar 1901 18:11:12 GMT'",
					/* JsonT */		"'Sun, 03 Mar 1901 18:11:12 GMT'",
					/* JsonR */		"'Sun, 03 Mar 1901 18:11:12 GMT'",
					/* Xml */		"<string>Sun, 03 Mar 1901 18:11:12 GMT</string>",
					/* XmlT */		"<string>Sun, 03 Mar 1901 18:11:12 GMT</string>",
					/* XmlR */		"<string>Sun, 03 Mar 1901 18:11:12 GMT</string>\n",
					/* XmlNs */		"<string>Sun, 03 Mar 1901 18:11:12 GMT</string>",
					/* Html */		"<string>Sun, 03 Mar 1901 18:11:12 GMT</string>",
					/* HtmlT */		"<string>Sun, 03 Mar 1901 18:11:12 GMT</string>",
					/* HtmlR */		"<string>Sun, 03 Mar 1901 18:11:12 GMT</string>",
					/* Uon */		"'Sun, 03 Mar 1901 18:11:12 GMT'",
					/* UonT */		"'Sun, 03 Mar 1901 18:11:12 GMT'",
					/* UonR */		"'Sun, 03 Mar 1901 18:11:12 GMT'",
					/* UrlEnc */	"_value='Sun,+03+Mar+1901+18:11:12+GMT'",
					/* UrlEncT */	"_value='Sun,+03+Mar+1901+18:11:12+GMT'",
					/* UrlEncR */	"_value='Sun,+03+Mar+1901+18:11:12+GMT'",
					/* MsgPack */	"BD53756E2C203033204D617220313930312031383A31313A313220474D54",
					/* MsgPackT */	"BD53756E2C203033204D617220313930312031383A31313A313220474D54",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<j:value>Sun, 03 Mar 1901 18:11:12 GMT</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<j:value>Sun, 03 Mar 1901 18:11:12 GMT</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <j:value>Sun, 03 Mar 1901 18:11:12 GMT</j:value>\n  </rdf:Description>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(Calendar o) {
						assertInstanceOf(Calendar.class, o);
					}
				}
			},
			{	/* 7 */
				new ComboInput2<Calendar[]>(
					"CalendarSwap.RFC2822DTZ/dateArray",
					Calendar[].class,
					dateArray,
					CalendarSwap.RFC2822DTZ.class,
					/* Json */		"['Sun, 03 Mar 1901 18:11:12 GMT']",
					/* JsonT */		"['Sun, 03 Mar 1901 18:11:12 GMT']",
					/* JsonR */		"[\n\t'Sun, 03 Mar 1901 18:11:12 GMT'\n]",
					/* Xml */		"<array><string>Sun, 03 Mar 1901 18:11:12 GMT</string></array>",
					/* XmlT */		"<array><string>Sun, 03 Mar 1901 18:11:12 GMT</string></array>",
					/* XmlR */		"<array>\n\t<string>Sun, 03 Mar 1901 18:11:12 GMT</string>\n</array>\n",
					/* XmlNs */		"<array><string>Sun, 03 Mar 1901 18:11:12 GMT</string></array>",
					/* Html */		"<ul><li>Sun, 03 Mar 1901 18:11:12 GMT</li></ul>",
					/* HtmlT */		"<ul><li>Sun, 03 Mar 1901 18:11:12 GMT</li></ul>",
					/* HtmlR */		"<ul>\n\t<li>Sun, 03 Mar 1901 18:11:12 GMT</li>\n</ul>\n",
					/* Uon */		"@('Sun, 03 Mar 1901 18:11:12 GMT')",
					/* UonT */		"@('Sun, 03 Mar 1901 18:11:12 GMT')",
					/* UonR */		"@(\n\t'Sun, 03 Mar 1901 18:11:12 GMT'\n)",
					/* UrlEnc */	"0='Sun,+03+Mar+1901+18:11:12+GMT'",
					/* UrlEncT */	"0='Sun,+03+Mar+1901+18:11:12+GMT'",
					/* UrlEncR */	"0='Sun,+03+Mar+1901+18:11:12+GMT'",
					/* MsgPack */	"91BD53756E2C203033204D617220313930312031383A31313A313220474D54",
					/* MsgPackT */	"91BD53756E2C203033204D617220313930312031383A31313A313220474D54",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Seq>\n<rdf:li>Sun, 03 Mar 1901 18:11:12 GMT</rdf:li>\n</rdf:Seq>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Seq>\n<rdf:li>Sun, 03 Mar 1901 18:11:12 GMT</rdf:li>\n</rdf:Seq>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Seq>\n    <rdf:li>Sun, 03 Mar 1901 18:11:12 GMT</rdf:li>\n  </rdf:Seq>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(Calendar[] o) {
						assertInstanceOf(Calendar.class, o[0]);
					}
				}
			},
			{	/* 8 */
				new ComboInput2<ObjectMap>(
					"CalendarSwap.RFC2822DTZ/dateMap",
					getType(Map.class,String.class,Calendar.class),
					dateMap,
					CalendarSwap.RFC2822DTZ.class,
					/* Json */		"{foo:'Sun, 03 Mar 1901 18:11:12 GMT'}",
					/* JsonT */		"{foo:'Sun, 03 Mar 1901 18:11:12 GMT'}",
					/* JsonR */		"{\n\tfoo: 'Sun, 03 Mar 1901 18:11:12 GMT'\n}",
					/* Xml */		"<object><foo>Sun, 03 Mar 1901 18:11:12 GMT</foo></object>",
					/* XmlT */		"<object><foo>Sun, 03 Mar 1901 18:11:12 GMT</foo></object>",
					/* XmlR */		"<object>\n\t<foo>Sun, 03 Mar 1901 18:11:12 GMT</foo>\n</object>\n",
					/* XmlNs */		"<object><foo>Sun, 03 Mar 1901 18:11:12 GMT</foo></object>",
					/* Html */		"<table><tr><td>foo</td><td>Sun, 03 Mar 1901 18:11:12 GMT</td></tr></table>",
					/* HtmlT */		"<table><tr><td>foo</td><td>Sun, 03 Mar 1901 18:11:12 GMT</td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>foo</td>\n\t\t<td>Sun, 03 Mar 1901 18:11:12 GMT</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(foo='Sun, 03 Mar 1901 18:11:12 GMT')",
					/* UonT */		"(foo='Sun, 03 Mar 1901 18:11:12 GMT')",
					/* UonR */		"(\n\tfoo='Sun, 03 Mar 1901 18:11:12 GMT'\n)",
					/* UrlEnc */	"foo='Sun,+03+Mar+1901+18:11:12+GMT'",
					/* UrlEncT */	"foo='Sun,+03+Mar+1901+18:11:12+GMT'",
					/* UrlEncR */	"foo='Sun,+03+Mar+1901+18:11:12+GMT'",
					/* MsgPack */	"81A3666F6FBD53756E2C203033204D617220313930312031383A31313A313220474D54",
					/* MsgPackT */	"81A3666F6FBD53756E2C203033204D617220313930312031383A31313A313220474D54",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:foo>Sun, 03 Mar 1901 18:11:12 GMT</jp:foo>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:foo>Sun, 03 Mar 1901 18:11:12 GMT</jp:foo>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:foo>Sun, 03 Mar 1901 18:11:12 GMT</jp:foo>\n  </rdf:Description>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(ObjectMap o) {
						assertInstanceOf(Calendar.class, o.get("foo"));
					}
				}
			},
			{	/* 9 */
				new ComboInput2<Calendar>(
					"CalendarLongSwap",
					Calendar.class,
					singleDate,
					CalendarLongSwap.class,
					/* Json */		"-2172116928000",
					/* JsonT */		"-2172116928000",
					/* JsonR */		"-2172116928000",
					/* Xml */		"<number>-2172116928000</number>",
					/* XmlT */		"<number>-2172116928000</number>",
					/* XmlR */		"<number>-2172116928000</number>\n",
					/* XmlNs */		"<number>-2172116928000</number>",
					/* Html */		"<number>-2172116928000</number>",
					/* HtmlT */		"<number>-2172116928000</number>",
					/* HtmlR */		"<number>-2172116928000</number>",
					/* Uon */		"-2172116928000",
					/* UonT */		"-2172116928000",
					/* UonR */		"-2172116928000",
					/* UrlEnc */	"_value=-2172116928000",
					/* UrlEncT */	"_value=-2172116928000",
					/* UrlEncR */	"_value=-2172116928000",
					/* MsgPack */	"D3FFFFFE0643BDFA00",
					/* MsgPackT */	"D3FFFFFE0643BDFA00",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<j:value>-2172116928000</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<j:value>-2172116928000</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <j:value>-2172116928000</j:value>\n  </rdf:Description>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(Calendar o) {
						assertInstanceOf(Calendar.class, o);
					}
				}
			},
			{	/* 10 */
				new ComboInput2<Calendar[]>(
					"CalendarLongSwap/dateArray",
					Calendar[].class,
					dateArray,
					CalendarLongSwap.class,
					/* Json */		"[-2172116928000]",
					/* JsonT */		"[-2172116928000]",
					/* JsonR */		"[\n\t-2172116928000\n]",
					/* Xml */		"<array><number>-2172116928000</number></array>",
					/* XmlT */		"<array><number>-2172116928000</number></array>",
					/* XmlR */		"<array>\n\t<number>-2172116928000</number>\n</array>\n",
					/* XmlNs */		"<array><number>-2172116928000</number></array>",
					/* Html */		"<ul><li><number>-2172116928000</number></li></ul>",
					/* HtmlT */		"<ul><li><number>-2172116928000</number></li></ul>",
					/* HtmlR */		"<ul>\n\t<li><number>-2172116928000</number></li>\n</ul>\n",
					/* Uon */		"@(-2172116928000)",
					/* UonT */		"@(-2172116928000)",
					/* UonR */		"@(\n\t-2172116928000\n)",
					/* UrlEnc */	"0=-2172116928000",
					/* UrlEncT */	"0=-2172116928000",
					/* UrlEncR */	"0=-2172116928000",
					/* MsgPack */	"91D3FFFFFE0643BDFA00",
					/* MsgPackT */	"91D3FFFFFE0643BDFA00",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Seq>\n<rdf:li>-2172116928000</rdf:li>\n</rdf:Seq>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Seq>\n<rdf:li>-2172116928000</rdf:li>\n</rdf:Seq>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Seq>\n    <rdf:li>-2172116928000</rdf:li>\n  </rdf:Seq>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(Calendar[] o) {
						assertInstanceOf(Calendar.class, o[0]);
					}
				}
			},
			{	/* 11 */
				new ComboInput2<ObjectMap>(
					"CalendarLongSwap/dateMap",
					getType(Map.class,String.class,Calendar.class),
					dateMap,
					CalendarLongSwap.class,
					/* Json */		"{foo:-2172116928000}",
					/* JsonT */		"{foo:-2172116928000}",
					/* JsonR */		"{\n\tfoo: -2172116928000\n}",
					/* Xml */		"<object><foo _type='number'>-2172116928000</foo></object>",
					/* XmlT */		"<object><foo t='number'>-2172116928000</foo></object>",
					/* XmlR */		"<object>\n\t<foo _type='number'>-2172116928000</foo>\n</object>\n",
					/* XmlNs */		"<object><foo _type='number'>-2172116928000</foo></object>",
					/* Html */		"<table><tr><td>foo</td><td><number>-2172116928000</number></td></tr></table>",
					/* HtmlT */		"<table><tr><td>foo</td><td><number>-2172116928000</number></td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>foo</td>\n\t\t<td><number>-2172116928000</number></td>\n\t</tr>\n</table>\n",
					/* Uon */		"(foo=-2172116928000)",
					/* UonT */		"(foo=-2172116928000)",
					/* UonR */		"(\n\tfoo=-2172116928000\n)",
					/* UrlEnc */	"foo=-2172116928000",
					/* UrlEncT */	"foo=-2172116928000",
					/* UrlEncR */	"foo=-2172116928000",
					/* MsgPack */	"81A3666F6FD3FFFFFE0643BDFA00",
					/* MsgPackT */	"81A3666F6FD3FFFFFE0643BDFA00",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:foo>-2172116928000</jp:foo>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:foo>-2172116928000</jp:foo>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:foo>-2172116928000</jp:foo>\n  </rdf:Description>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(ObjectMap o) {
						assertInstanceOf(Calendar.class, o.get("foo"));
					}
				}
			},
			{	/* 12 */
				new ComboInput2<Calendar>(
					"CalendarMapSwap/singleDate",
					Calendar.class,
					singleDate,
					CalendarMapSwap.class,
					/* Json */		"{time:-2172116928000,timeZone:'PST'}",
					/* JsonT */		"{time:-2172116928000,timeZone:'PST'}",
					/* JsonR */		"{\n\ttime: -2172116928000,\n\ttimeZone: 'PST'\n}",
					/* Xml */		"<object><time _type='number'>-2172116928000</time><timeZone>PST</timeZone></object>",
					/* XmlT */		"<object><time t='number'>-2172116928000</time><timeZone>PST</timeZone></object>",
					/* XmlR */		"<object>\n\t<time _type='number'>-2172116928000</time>\n\t<timeZone>PST</timeZone>\n</object>\n",
					/* XmlNs */		"<object><time _type='number'>-2172116928000</time><timeZone>PST</timeZone></object>",
					/* Html */		"<table><tr><td>time</td><td><number>-2172116928000</number></td></tr><tr><td>timeZone</td><td>PST</td></tr></table>",
					/* HtmlT */		"<table><tr><td>time</td><td><number>-2172116928000</number></td></tr><tr><td>timeZone</td><td>PST</td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>time</td>\n\t\t<td><number>-2172116928000</number></td>\n\t</tr>\n\t<tr>\n\t\t<td>timeZone</td>\n\t\t<td>PST</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(time=-2172116928000,timeZone=PST)",
					/* UonT */		"(time=-2172116928000,timeZone=PST)",
					/* UonR */		"(\n\ttime=-2172116928000,\n\ttimeZone=PST\n)",
					/* UrlEnc */	"time=-2172116928000&timeZone=PST",
					/* UrlEncT */	"time=-2172116928000&timeZone=PST",
					/* UrlEncR */	"time=-2172116928000\n&timeZone=PST",
					/* MsgPack */	"82A474696D65D3FFFFFE0643BDFA00A874696D655A6F6E65A3505354",
					/* MsgPackT */	"82A474696D65D3FFFFFE0643BDFA00A874696D655A6F6E65A3505354",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:time>-2172116928000</jp:time>\n<jp:timeZone>PST</jp:timeZone>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:time>-2172116928000</jp:time>\n<jp:timeZone>PST</jp:timeZone>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:time>-2172116928000</jp:time>\n    <jp:timeZone>PST</jp:timeZone>\n  </rdf:Description>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(Calendar o) {
						assertInstanceOf(Calendar.class, o);
					}
				}
			},
			{	/* 13 */
				new ComboInput2<Calendar[]>(
					"CalendarMapSwap/dateArray",
					Calendar[].class,
					dateArray,
					CalendarMapSwap.class,
					/* Json */		"[{time:-2172116928000,timeZone:'PST'}]",
					/* JsonT */		"[{time:-2172116928000,timeZone:'PST'}]",
					/* JsonR */		"[\n\t{\n\t\ttime: -2172116928000,\n\t\ttimeZone: 'PST'\n\t}\n]",
					/* Xml */		"<array><object><time _type='number'>-2172116928000</time><timeZone>PST</timeZone></object></array>",
					/* XmlT */		"<array><object><time t='number'>-2172116928000</time><timeZone>PST</timeZone></object></array>",
					/* XmlR */		"<array>\n\t<object>\n\t\t<time _type='number'>-2172116928000</time>\n\t\t<timeZone>PST</timeZone>\n\t</object>\n</array>\n",
					/* XmlNs */		"<array><object><time _type='number'>-2172116928000</time><timeZone>PST</timeZone></object></array>",
					/* Html */		"<ul><li><table><tr><td>time</td><td><number>-2172116928000</number></td></tr><tr><td>timeZone</td><td>PST</td></tr></table></li></ul>",
					/* HtmlT */		"<ul><li><table><tr><td>time</td><td><number>-2172116928000</number></td></tr><tr><td>timeZone</td><td>PST</td></tr></table></li></ul>",
					/* HtmlR */		"<ul>\n\t<li>\n\t\t<table>\n\t\t\t<tr>\n\t\t\t\t<td>time</td>\n\t\t\t\t<td><number>-2172116928000</number></td>\n\t\t\t</tr>\n\t\t\t<tr>\n\t\t\t\t<td>timeZone</td>\n\t\t\t\t<td>PST</td>\n\t\t\t</tr>\n\t\t</table>\n\t</li>\n</ul>\n",
					/* Uon */		"@((time=-2172116928000,timeZone=PST))",
					/* UonT */		"@((time=-2172116928000,timeZone=PST))",
					/* UonR */		"@(\n\t(\n\t\ttime=-2172116928000,\n\t\ttimeZone=PST\n\t)\n)",
					/* UrlEnc */	"0=(time=-2172116928000,timeZone=PST)",
					/* UrlEncT */	"0=(time=-2172116928000,timeZone=PST)",
					/* UrlEncR */	"0=(\n\ttime=-2172116928000,\n\ttimeZone=PST\n)",
					/* MsgPack */	"9182A474696D65D3FFFFFE0643BDFA00A874696D655A6F6E65A3505354",
					/* MsgPackT */	"9182A474696D65D3FFFFFE0643BDFA00A874696D655A6F6E65A3505354",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Seq>\n<rdf:li rdf:parseType='Resource'>\n<jp:time>-2172116928000</jp:time>\n<jp:timeZone>PST</jp:timeZone>\n</rdf:li>\n</rdf:Seq>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Seq>\n<rdf:li rdf:parseType='Resource'>\n<jp:time>-2172116928000</jp:time>\n<jp:timeZone>PST</jp:timeZone>\n</rdf:li>\n</rdf:Seq>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Seq>\n    <rdf:li rdf:parseType='Resource'>\n      <jp:time>-2172116928000</jp:time>\n      <jp:timeZone>PST</jp:timeZone>\n    </rdf:li>\n  </rdf:Seq>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(Calendar[] o) {
						assertInstanceOf(Calendar.class, o[0]);
					}
				}
			},
			{	/* 14 */
				new ComboInput2<ObjectMap>(
					"CalendarMapSwap/dateMap",
					getType(Map.class,String.class,Calendar.class),
					dateMap,
					CalendarMapSwap.class,
					/* Json */		"{foo:{time:-2172116928000,timeZone:'PST'}}",
					/* JsonT */		"{foo:{time:-2172116928000,timeZone:'PST'}}",
					/* JsonR */		"{\n\tfoo: {\n\t\ttime: -2172116928000,\n\t\ttimeZone: 'PST'\n\t}\n}",
					/* Xml */		"<object><foo _type='object'><time _type='number'>-2172116928000</time><timeZone>PST</timeZone></foo></object>",
					/* XmlT */		"<object><foo t='object'><time t='number'>-2172116928000</time><timeZone>PST</timeZone></foo></object>",
					/* XmlR */		"<object>\n\t<foo _type='object'>\n\t\t<time _type='number'>-2172116928000</time>\n\t\t<timeZone>PST</timeZone>\n\t</foo>\n</object>\n",
					/* XmlNs */		"<object><foo _type='object'><time _type='number'>-2172116928000</time><timeZone>PST</timeZone></foo></object>",
					/* Html */		"<table><tr><td>foo</td><td><table><tr><td>time</td><td><number>-2172116928000</number></td></tr><tr><td>timeZone</td><td>PST</td></tr></table></td></tr></table>",
					/* HtmlT */		"<table><tr><td>foo</td><td><table><tr><td>time</td><td><number>-2172116928000</number></td></tr><tr><td>timeZone</td><td>PST</td></tr></table></td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>foo</td>\n\t\t<td>\n\t\t\t<table>\n\t\t\t\t<tr>\n\t\t\t\t\t<td>time</td>\n\t\t\t\t\t<td><number>-2172116928000</number></td>\n\t\t\t\t</tr>\n\t\t\t\t<tr>\n\t\t\t\t\t<td>timeZone</td>\n\t\t\t\t\t<td>PST</td>\n\t\t\t\t</tr>\n\t\t\t</table>\n\t\t</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(foo=(time=-2172116928000,timeZone=PST))",
					/* UonT */		"(foo=(time=-2172116928000,timeZone=PST))",
					/* UonR */		"(\n\tfoo=(\n\t\ttime=-2172116928000,\n\t\ttimeZone=PST\n\t)\n)",
					/* UrlEnc */	"foo=(time=-2172116928000,timeZone=PST)",
					/* UrlEncT */	"foo=(time=-2172116928000,timeZone=PST)",
					/* UrlEncR */	"foo=(\n\ttime=-2172116928000,\n\ttimeZone=PST\n)",
					/* MsgPack */	"81A3666F6F82A474696D65D3FFFFFE0643BDFA00A874696D655A6F6E65A3505354",
					/* MsgPackT */	"81A3666F6F82A474696D65D3FFFFFE0643BDFA00A874696D655A6F6E65A3505354",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:foo rdf:parseType='Resource'>\n<jp:time>-2172116928000</jp:time>\n<jp:timeZone>PST</jp:timeZone>\n</jp:foo>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:foo rdf:parseType='Resource'>\n<jp:time>-2172116928000</jp:time>\n<jp:timeZone>PST</jp:timeZone>\n</jp:foo>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:foo rdf:parseType='Resource'>\n      <jp:time>-2172116928000</jp:time>\n      <jp:timeZone>PST</jp:timeZone>\n    </jp:foo>\n  </rdf:Description>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(ObjectMap o) {
						assertInstanceOf(Calendar.class, o.get("foo"));
					}
				}
			},
			{	/* 15 */
				new ComboInput2<Calendar>(
					"CalendarSwap.DateMedium/singleDate",
					Calendar.class,
					singleDate,
					CalendarSwap.DateMedium.class,
					/* Json */		"'Mar 3, 1901'",
					/* JsonT */		"'Mar 3, 1901'",
					/* JsonR */		"'Mar 3, 1901'",
					/* Xml */		"<string>Mar 3, 1901</string>",
					/* XmlT */		"<string>Mar 3, 1901</string>",
					/* XmlR */		"<string>Mar 3, 1901</string>\n",
					/* XmlNs */		"<string>Mar 3, 1901</string>",
					/* Html */		"<string>Mar 3, 1901</string>",
					/* HtmlT */		"<string>Mar 3, 1901</string>",
					/* HtmlR */		"<string>Mar 3, 1901</string>",
					/* Uon */		"'Mar 3, 1901'",
					/* UonT */		"'Mar 3, 1901'",
					/* UonR */		"'Mar 3, 1901'",
					/* UrlEnc */	"_value='Mar+3,+1901'",
					/* UrlEncT */	"_value='Mar+3,+1901'",
					/* UrlEncR */	"_value='Mar+3,+1901'",
					/* MsgPack */	"AB4D617220332C2031393031",
					/* MsgPackT */	"AB4D617220332C2031393031",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<j:value>Mar 3, 1901</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<j:value>Mar 3, 1901</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <j:value>Mar 3, 1901</j:value>\n  </rdf:Description>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(Calendar o) {
						assertInstanceOf(Calendar.class, o);
					}
				}
			},
			{	/* 16 */
				new ComboInput2<Calendar[]>(
					"CalendarSwap.DateMedium/dateArray",
					Calendar[].class,
					dateArray,
					CalendarSwap.DateMedium.class,
					/* Json */		"['Mar 3, 1901']",
					/* JsonT */		"['Mar 3, 1901']",
					/* JsonR */		"[\n\t'Mar 3, 1901'\n]",
					/* Xml */		"<array><string>Mar 3, 1901</string></array>",
					/* XmlT */		"<array><string>Mar 3, 1901</string></array>",
					/* XmlR */		"<array>\n\t<string>Mar 3, 1901</string>\n</array>\n",
					/* XmlNs */		"<array><string>Mar 3, 1901</string></array>",
					/* Html */		"<ul><li>Mar 3, 1901</li></ul>",
					/* HtmlT */		"<ul><li>Mar 3, 1901</li></ul>",
					/* HtmlR */		"<ul>\n\t<li>Mar 3, 1901</li>\n</ul>\n",
					/* Uon */		"@('Mar 3, 1901')",
					/* UonT */		"@('Mar 3, 1901')",
					/* UonR */		"@(\n\t'Mar 3, 1901'\n)",
					/* UrlEnc */	"0='Mar+3,+1901'",
					/* UrlEncT */	"0='Mar+3,+1901'",
					/* UrlEncR */	"0='Mar+3,+1901'",
					/* MsgPack */	"91AB4D617220332C2031393031",
					/* MsgPackT */	"91AB4D617220332C2031393031",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Seq>\n<rdf:li>Mar 3, 1901</rdf:li>\n</rdf:Seq>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Seq>\n<rdf:li>Mar 3, 1901</rdf:li>\n</rdf:Seq>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Seq>\n    <rdf:li>Mar 3, 1901</rdf:li>\n  </rdf:Seq>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(Calendar[] o) {
						assertInstanceOf(Calendar.class, o[0]);
					}
				}
			},
			{	/* 17 */
				new ComboInput2<ObjectMap>(
					"CalendarSwap.DateMedium/dateMap",
					getType(Map.class,String.class,Calendar.class),
					dateMap,
					CalendarSwap.DateMedium.class,
					/* Json */		"{foo:'Mar 3, 1901'}",
					/* JsonT */		"{foo:'Mar 3, 1901'}",
					/* JsonR */		"{\n\tfoo: 'Mar 3, 1901'\n}",
					/* Xml */		"<object><foo>Mar 3, 1901</foo></object>",
					/* XmlT */		"<object><foo>Mar 3, 1901</foo></object>",
					/* XmlR */		"<object>\n\t<foo>Mar 3, 1901</foo>\n</object>\n",
					/* XmlNs */		"<object><foo>Mar 3, 1901</foo></object>",
					/* Html */		"<table><tr><td>foo</td><td>Mar 3, 1901</td></tr></table>",
					/* HtmlT */		"<table><tr><td>foo</td><td>Mar 3, 1901</td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>foo</td>\n\t\t<td>Mar 3, 1901</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(foo='Mar 3, 1901')",
					/* UonT */		"(foo='Mar 3, 1901')",
					/* UonR */		"(\n\tfoo='Mar 3, 1901'\n)",
					/* UrlEnc */	"foo='Mar+3,+1901'",
					/* UrlEncT */	"foo='Mar+3,+1901'",
					/* UrlEncR */	"foo='Mar+3,+1901'",
					/* MsgPack */	"81A3666F6FAB4D617220332C2031393031",
					/* MsgPackT */	"81A3666F6FAB4D617220332C2031393031",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:foo>Mar 3, 1901</jp:foo>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:foo>Mar 3, 1901</jp:foo>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:foo>Mar 3, 1901</jp:foo>\n  </rdf:Description>\n</rdf:RDF>\n"
				)
				{
					@Override
					public void verify(ObjectMap o) {
						assertInstanceOf(Calendar.class, o.get("foo"));
					}
				}
			},
		});
	}

	private final Class<?> swapClass;

	public CalendarSwapComboTest(ComboInput2<?> comboInput) {
		super(comboInput);
		this.swapClass = comboInput.swapClass;
	}

	public static class ComboInput2<T> extends ComboInput<T> {
		private final Class<?> swapClass;

		public ComboInput2(
				String label,
				Type type,
				T in,
				Class<?> swapClass,
				String json,
				String jsonT,
				String jsonR,
				String xml,
				String xmlT,
				String xmlR,
				String xmlNs,
				String html,
				String htmlT,
				String htmlR,
				String uon,
				String uonT,
				String uonR,
				String urlEncoding,
				String urlEncodingT,
				String urlEncodingR,
				String msgPack,
				String msgPackT,
				String rdfXml,
				String rdfXmlT,
				String rdfXmlR
			) {
			super(label, type, in, json, jsonT, jsonR, xml, xmlT, xmlR, xmlNs, html, htmlT, htmlR, uon, uonT, uonR, urlEncoding, urlEncodingT, urlEncodingR, msgPack, msgPackT, rdfXml, rdfXmlT, rdfXmlR);
			this.swapClass = swapClass;
		}
	}

	@BeforeClass
	public static void beforeClass() {
		TestUtils.setTimeZone("PST");
		TestUtils.setLocale(Locale.US);
	}

	@AfterClass
	public static void afterClass() {
		TestUtils.unsetTimeZone();
		TestUtils.unsetLocale();
	}

	@Override
	protected Serializer applySettings(Serializer s) throws Exception {
		return s.builder().pojoSwaps(swapClass).build();
	}

	@Override
	protected Parser applySettings(Parser p) throws Exception {
		return p.builder().pojoSwaps(swapClass).build();
	}
}
