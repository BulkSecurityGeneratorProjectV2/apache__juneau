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
package org.apache.juneau.rest.client2;

import static org.apache.juneau.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.runners.MethodSorters.*;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import org.apache.http.*;
import org.apache.juneau.collections.*;
import org.apache.juneau.http.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.jsonschema.annotation.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.rest.RestRequest;
import org.apache.juneau.http.remote.*;
import org.apache.juneau.rest.mock2.*;
import org.apache.juneau.rest.testutils.*;
import org.apache.juneau.uon.*;
import org.apache.juneau.utils.*;
import org.junit.*;

@SuppressWarnings({"resource"})
@FixMethodOrder(NAME_ASCENDING)
public class Remote_QueryAnnotation_Test {

	public static class Bean {
		public int f;

		public static Bean create() {
			Bean b = new Bean();
			b.f = 1;
			return b;
		}
	}

	//=================================================================================================================
	// Basic tests
	//=================================================================================================================

	@Rest
	public static class A {
		@RestMethod
		public String getA(@Query("*") OMap m) {
			return m.toString();
		}
	}

	@Remote
	public static interface A1 {
		@RemoteMethod(path="a") String getX1(@Query("x") int b);
		@RemoteMethod(path="a") String getX2(@Query("x") float b);
		@RemoteMethod(path="a") String getX3(@Query("x") Bean b);
		@RemoteMethod(path="a") String getX4(@Query("*") Bean b);
		@RemoteMethod(path="a") String getX5(@Query Bean b);
		@RemoteMethod(path="a") String getX6(@Query("x") Bean[] b);
		@RemoteMethod(path="a") String getX7(@Query(n="x",cf="uon") Bean[] b);
		@RemoteMethod(path="a") String getX8(@Query("x") List<Bean> b);
		@RemoteMethod(path="a") String getX9(@Query(n="x",cf="uon") List<Bean> b);
		@RemoteMethod(path="a") String getX10(@Query("x") Map<String,Bean> b);
		@RemoteMethod(path="a") String getX11(@Query("*") Map<String,Bean> b);
		@RemoteMethod(path="a") String getX12(@Query Map<String,Bean> b);
		@RemoteMethod(path="a") String getX13(@Query(n="x",cf="uon") Map<String,Bean> b);
		@RemoteMethod(path="a") String getX14(@Query(f="uon") Map<String,Bean> b);
		@RemoteMethod(path="a") String getX15(@Query("*") Reader b);
		@RemoteMethod(path="a") String getX16(@Query Reader b);
		@RemoteMethod(path="a") String getX17(@Query("*") InputStream b);
		@RemoteMethod(path="a") String getX18(@Query InputStream b);
		@RemoteMethod(path="a") String getX19(@Query("*") NameValuePairs b);
		@RemoteMethod(path="a") String getX20(@Query NameValuePairs b);
		@RemoteMethod(path="a") String getX21(@Query NameValuePair b);
		@RemoteMethod(path="a") String getX22(@Query NameValuePair[] b);
		@RemoteMethod(path="a") String getX23(@Query BasicNameValuePair[] b);
		@RemoteMethod(path="a") String getX24(@Query String b);
	}

	@Test
	public void a01_objectTypes() throws Exception {
		A1 x = MockRestClient.build(A.class).getRemote(A1.class);
		assertEquals("{x:'1'}", x.getX1(1));
		assertEquals("{x:'1.0'}", x.getX2(1));
		assertEquals("{x:'f=1'}", x.getX3(Bean.create()));
		assertEquals("{f:'1'}", x.getX4(Bean.create()));
		assertEquals("{f:'1'}", x.getX5(Bean.create()));
		assertEquals("{x:'f=1,f=1'}", x.getX6(new Bean[]{Bean.create(),Bean.create()}));
		assertEquals("{x:'@((f=1),(f=1))'}", x.getX7(new Bean[]{Bean.create(),Bean.create()}));
		assertEquals("{x:'f=1,f=1'}", x.getX8(AList.of(Bean.create(),Bean.create())));
		assertEquals("{x:'@((f=1),(f=1))'}", x.getX9(AList.of(Bean.create(),Bean.create())));
		assertEquals("{x:'k1=f\\\\=1'}", x.getX10(AMap.of("k1",Bean.create())));
		assertEquals("{k1:'f=1'}", x.getX11(AMap.of("k1",Bean.create())));
		assertEquals("{k1:'f=1'}", x.getX12(AMap.of("k1",Bean.create())));
		assertEquals("{x:'(k1=(f=1))'}", x.getX13(AMap.of("k1",Bean.create())));
		assertEquals("{k1:'f=1'}", x.getX14(AMap.of("k1",Bean.create())));
		assertEquals("{x:'1'}", x.getX15(new StringReader("x=1")));
		assertEquals("{x:'1'}", x.getX16(new StringReader("x=1")));
		assertEquals("{x:'1'}", x.getX17(new StringInputStream("x=1")));
		assertEquals("{x:'1'}", x.getX18(new StringInputStream("x=1")));
		assertEquals("{foo:'bar'}", x.getX19(new NameValuePairs().append("foo", "bar")));
		assertEquals("{foo:'bar'}", x.getX20(new NameValuePairs().append("foo", "bar")));
		assertEquals("{foo:'bar'}", x.getX21(BasicNameValuePair.of("foo", "bar")));
		assertEquals("{foo:'bar'}", x.getX22(new NameValuePairs().append("foo", "bar").toArray(new NameValuePair[0])));
		assertEquals("{foo:'bar'}", x.getX23(new NameValuePairs().append("foo", "bar").toArray(new BasicNameValuePair[0])));
		assertEquals("{foo:'bar'}", x.getX24("foo=bar"));
		assertEquals("{}", x.getX24(null));
	}

	//=================================================================================================================
	// @Query(_default/allowEmptyValue)
	//=================================================================================================================

	@Rest
	public static class B {
		@RestMethod
		public String get(@Query("*") OMap m) {
			return m.toString();
		}
	}

	@Remote
	public static interface B1 {
		@RemoteMethod(path="/") String getX1(@Query(n="x",df="foo") String b);
		@RemoteMethod(path="/") String getX2(@Query(n="x",df="foo",aev=true) String b);
		@RemoteMethod(path="/") String getX3(@Query(n="x",df="") String b);
		@RemoteMethod(path="/") String getX4(@Query(n="x",df="",aev=true) String b);
	}

	@Test
	public void b01_default_aev() throws Exception {
		B1 x = MockRestClient.build(B.class).getRemote(B1.class);
		assertEquals("{x:'foo'}", x.getX1(null));
		assertThrown(()->{x.getX1("");}).contains("Empty value not allowed");
		assertEquals("{x:'foo'}", x.getX2(null));
		assertEquals("{x:''}", x.getX2(""));
		assertEquals("{x:''}", x.getX3(null));
		assertThrown(()->{x.getX3("");}).contains("Empty value not allowed");
		assertEquals("{x:''}", x.getX4(null));
		assertEquals("{x:''}", x.getX4(""));
	}

	//=================================================================================================================
	// @Query(collectionFormat)
	//=================================================================================================================

	@Rest
	public static class C {
		@RestMethod
		public String getA(@Query("*") OMap m) {
			return m.toString();
		}
		@RestMethod
		public Reader getB(RestRequest req) {
			return new StringReader(req.getQueryString());
		}
	}

	@Remote
	public static interface C1 {
		@RemoteMethod(path="/a") String getX1(@Query(n="x") String...b);
		@RemoteMethod(path="/b") String getX2(@Query(n="x") String...b);
		@RemoteMethod(path="/a") String getX3(@Query(n="x",cf="csv") String...b);
		@RemoteMethod(path="/b") String getX4(@Query(n="x",cf="csv") String...b);
		@RemoteMethod(path="/a") String getX5(@Query(n="x",cf="ssv") String...b);
		@RemoteMethod(path="/b") String getX6(@Query(n="x",cf="ssv") String...b);
		@RemoteMethod(path="/a") String getX7(@Query(n="x",cf="tsv") String...b);
		@RemoteMethod(path="/b") String getX8(@Query(n="x",cf="tsv") String...b);
		@RemoteMethod(path="/a") String getX9(@Query(n="x",cf="pipes") String...b);
		@RemoteMethod(path="/b") String getX10(@Query(n="x",cf="pipes") String...b);
		@RemoteMethod(path="/a") String getX11(@Query(n="x",cf="multi") String...b); // Not supported, but should be treated as csv.
		@RemoteMethod(path="/b") String getX12(@Query(n="x",cf="multi") String...b); // Not supported, but should be treated as csv.
		@RemoteMethod(path="/a") String getX13(@Query(n="x",cf="uon") String...b);
		@RemoteMethod(path="/b") String getX14(@Query(n="x",cf="uon") String...b);
	}

	@Test
	public void c01_collectionFormat() throws Exception {
		C1 x = MockRestClient.build(C.class).getRemote(C1.class);
		assertEquals("{x:'foo,bar'}", x.getX1("foo","bar"));
		assertEquals("x=foo%2Cbar", x.getX2("foo","bar"));
		assertEquals("{x:'foo,bar'}", x.getX3("foo","bar"));
		assertEquals("x=foo%2Cbar", x.getX4("foo","bar"));
		assertEquals("{x:'foo bar'}", x.getX5("foo","bar"));
		assertEquals("x=foo+bar", x.getX6("foo","bar"));
		assertEquals("{x:'foo\\tbar'}", x.getX7("foo","bar"));
		assertEquals("x=foo%09bar", x.getX8("foo","bar"));
		assertEquals("{x:'foo|bar'}", x.getX9("foo","bar"));
		assertEquals("x=foo%7Cbar", x.getX10("foo","bar"));
		assertEquals("{x:'foo,bar'}", x.getX11("foo","bar"));
		assertEquals("x=foo%2Cbar", x.getX12("foo","bar"));
		assertEquals("{x:'@(foo,bar)'}", x.getX13("foo","bar"));
		assertEquals("x=%40%28foo%2Cbar%29", x.getX14("foo","bar"));
	}

	//=================================================================================================================
	// @Query(maximum,exclusiveMaximum,minimum,exclusiveMinimum)
	//=================================================================================================================

	@Rest
	public static class D {
		@RestMethod
		public String get(@Query("*") OMap m) {
			return m.toString();
		}
	}

	@Remote
	public static interface D1 {
		@RemoteMethod(path="/") String getX1(@Query(n="x",min="1",max="10") int b);
		@RemoteMethod(path="/") String getX2(@Query(n="x",min="1",max="10",emin=false,emax=false) int b);
		@RemoteMethod(path="/") String getX3(@Query(n="x",min="1",max="10",emin=true,emax=true) int b);
		@RemoteMethod(path="/") String getX4(@Query(n="x",min="1",max="10") short b);
		@RemoteMethod(path="/") String getX5(@Query(n="x",min="1",max="10",emin=false,emax=false) short b);
		@RemoteMethod(path="/") String getX6(@Query(n="x",min="1",max="10",emin=true,emax=true) short b);
		@RemoteMethod(path="/") String getX7(@Query(n="x",min="1",max="10") long b);
		@RemoteMethod(path="/") String getX8(@Query(n="x",min="1",max="10",emin=false,emax=false) long b);
		@RemoteMethod(path="/") String getX9(@Query(n="x",min="1",max="10",emin=true,emax=true) long b);
		@RemoteMethod(path="/") String getX10(@Query(n="x",min="1",max="10") float b);
		@RemoteMethod(path="/") String getX11(@Query(n="x",min="1",max="10",emin=false,emax=false) float b);
		@RemoteMethod(path="/") String getX12(@Query(n="x",min="1",max="10",emin=true,emax=true) float b);
		@RemoteMethod(path="/") String getX13(@Query(n="x",min="1",max="10") double b);
		@RemoteMethod(path="/") String getX14(@Query(n="x",min="1",max="10",emin=false,emax=false) double b);
		@RemoteMethod(path="/") String getX15(@Query(n="x",min="1",max="10",emin=true,emax=true) double b);
		@RemoteMethod(path="/") String getX16(@Query(n="x",min="1",max="10") byte b);
		@RemoteMethod(path="/") String getX17(@Query(n="x",min="1",max="10",emin=false,emax=false) byte b);
		@RemoteMethod(path="/") String getX18(@Query(n="x",min="1",max="10",emin=true,emax=true) byte b);
		@RemoteMethod(path="/") String getX19(@Query(n="x",min="1",max="10") AtomicInteger b);
		@RemoteMethod(path="/") String getX20(@Query(n="x",min="1",max="10",emin=false,emax=false) AtomicInteger b);
		@RemoteMethod(path="/") String getX21(@Query(n="x",min="1",max="10",emin=true,emax=true) AtomicInteger b);
		@RemoteMethod(path="/") String getX22(@Query(n="x",min="1",max="10") BigDecimal b);
		@RemoteMethod(path="/") String getX23(@Query(n="x",min="1",max="10",emin=false,emax=false) BigDecimal b);
		@RemoteMethod(path="/") String getX24(@Query(n="x",min="1",max="10",emin=true,emax=true) BigDecimal b);
		@RemoteMethod(path="/") String getX25(@Query(n="x",min="1",max="10") Integer b);
		@RemoteMethod(path="/") String getX26(@Query(n="x",min="1",max="10",emin=false,emax=false) Integer b);
		@RemoteMethod(path="/") String getX27(@Query(n="x",min="1",max="10",emin=true,emax=true) Integer b);
		@RemoteMethod(path="/") String getX28(@Query(n="x",min="1",max="10") Short b);
		@RemoteMethod(path="/") String getX29(@Query(n="x",min="1",max="10",emin=false,emax=false) Short b);
		@RemoteMethod(path="/") String getX30(@Query(n="x",min="1",max="10",emin=true,emax=true) Short b);
		@RemoteMethod(path="/") String getX31(@Query(n="x",min="1",max="10") Long b);
		@RemoteMethod(path="/") String getX32(@Query(n="x",min="1",max="10",emin=false,emax=false) Long b);
		@RemoteMethod(path="/") String getX33(@Query(n="x",min="1",max="10",emin=true,emax=true) Long b);
		@RemoteMethod(path="/") String getX34(@Query(n="x",min="1",max="10") Float b);
		@RemoteMethod(path="/") String getX35(@Query(n="x",min="1",max="10",emin=false,emax=false) Float b);
		@RemoteMethod(path="/") String getX36(@Query(n="x",min="1",max="10",emin=true,emax=true) Float b);
		@RemoteMethod(path="/") String getX37(@Query(n="x",min="1",max="10") Double b);
		@RemoteMethod(path="/") String getX38(@Query(n="x",min="1",max="10",emin=false,emax=false) Double b);
		@RemoteMethod(path="/") String getX39(@Query(n="x",min="1",max="10",emin=true,emax=true) Double b);
		@RemoteMethod(path="/") String getX40(@Query(n="x",min="1",max="10") Byte b);
		@RemoteMethod(path="/") String getX41(@Query(n="x",min="1",max="10",emin=false,emax=false) Byte b);
		@RemoteMethod(path="/") String getX42(@Query(n="x",min="1",max="10",emin=true,emax=true) Byte b);
	}

	@Test
	public void d01_min_max_emin_emax() throws Exception {
		D1 x = MockRestClient.build(D.class).getRemote(D1.class);
		assertEquals("{x:'1'}", x.getX1(1));
		assertEquals("{x:'10'}", x.getX1(10));
		assertThrown(()->{x.getX1(0);}).contains("Minimum value not met");
		assertThrown(()->{x.getX1(11);}).contains("Maximum value exceeded");
		assertEquals("{x:'1'}", x.getX2(1));
		assertEquals("{x:'10'}", x.getX2(10));
		assertThrown(()->{x.getX2(0);}).contains("Minimum value not met");
		assertThrown(()->{x.getX2(11);}).contains("Maximum value exceeded");
		assertEquals("{x:'2'}", x.getX3(2));
		assertEquals("{x:'9'}", x.getX3(9));
		assertThrown(()->{x.getX3(1);}).contains("Minimum value not met");
		assertThrown(()->{x.getX3(10);}).contains("Maximum value exceeded");
		assertEquals("{x:'1'}", x.getX4((short)1));
		assertEquals("{x:'10'}", x.getX4((short)10));
		assertThrown(()->{x.getX4((short)0);}).contains("Minimum value not met");
		assertThrown(()->{x.getX4((short)11);}).contains("Maximum value exceeded");
		assertEquals("{x:'1'}", x.getX5((short)1));
		assertEquals("{x:'10'}", x.getX5((short)10));
		assertThrown(()->{x.getX5((short)0);}).contains("Minimum value not met");
		assertThrown(()->{x.getX5((short)11);}).contains("Maximum value exceeded");
		assertEquals("{x:'2'}", x.getX6((short)2));
		assertEquals("{x:'9'}", x.getX6((short)9));
		assertThrown(()->{x.getX6((short)1);}).contains("Minimum value not met");
		assertThrown(()->{x.getX6((short)10);}).contains("Maximum value exceeded");
		assertEquals("{x:'1'}", x.getX7(1l));
		assertEquals("{x:'10'}", x.getX7(10l));
		assertThrown(()->{x.getX7(0l);}).contains("Minimum value not met");
		assertThrown(()->{x.getX7(11l);}).contains("Maximum value exceeded");
		assertEquals("{x:'1'}", x.getX8(1l));
		assertEquals("{x:'10'}", x.getX8(10l));
		assertThrown(()->{x.getX8(0l);}).contains("Minimum value not met");
		assertThrown(()->{x.getX8(11l);}).contains("Maximum value exceeded");
		assertEquals("{x:'2'}", x.getX9(2l));
		assertEquals("{x:'9'}", x.getX9(9l));
		assertThrown(()->{x.getX9(1l);}).contains("Minimum value not met");
		assertThrown(()->{x.getX9(10l);}).contains("Maximum value exceeded");
		assertEquals("{x:'1.0'}", x.getX10(1f));
		assertEquals("{x:'10.0'}", x.getX10(10f));
		assertThrown(()->{x.getX10(0.9f);}).contains("Minimum value not met");
		assertThrown(()->{x.getX10(10.1f);}).contains("Maximum value exceeded");
		assertEquals("{x:'1.0'}", x.getX11(1f));
		assertEquals("{x:'10.0'}", x.getX11(10f));
		assertThrown(()->{x.getX11(0.9f);}).contains("Minimum value not met");
		assertThrown(()->{x.getX11(10.1f);}).contains("Maximum value exceeded");
		assertEquals("{x:'1.1'}", x.getX12(1.1f));
		assertEquals("{x:'9.9'}", x.getX12(9.9f));
		assertThrown(()->{x.getX12(1f);}).contains("Minimum value not met");
		assertThrown(()->{x.getX12(10f);}).contains("Maximum value exceeded");
		assertEquals("{x:'1.0'}", x.getX13(1d));
		assertEquals("{x:'10.0'}", x.getX13(10d));
		assertThrown(()->{x.getX13(0.9d);}).contains("Minimum value not met");
		assertThrown(()->{x.getX13(10.1d);}).contains("Maximum value exceeded");
		assertEquals("{x:'1.0'}", x.getX14(1d));
		assertEquals("{x:'10.0'}", x.getX14(10d));
		assertThrown(()->{x.getX14(0.9d);}).contains("Minimum value not met");
		assertThrown(()->{x.getX14(10.1d);}).contains("Maximum value exceeded");
		assertEquals("{x:'1.1'}", x.getX15(1.1d));
		assertEquals("{x:'9.9'}", x.getX15(9.9d));
		assertThrown(()->{x.getX15(1d);}).contains("Minimum value not met");
		assertThrown(()->{x.getX15(10d);}).contains("Maximum value exceeded");
		assertEquals("{x:'1'}", x.getX16((byte)1));
		assertEquals("{x:'10'}", x.getX16((byte)10));
		assertThrown(()->{x.getX16((byte)0);}).contains("Minimum value not met");
		assertThrown(()->{x.getX16((byte)11);}).contains("Maximum value exceeded");
		assertEquals("{x:'1'}", x.getX17((byte)1));
		assertEquals("{x:'10'}", x.getX17((byte)10));
		assertThrown(()->{x.getX17((byte)0);}).contains("Minimum value not met");
		assertThrown(()->{x.getX17((byte)11);}).contains("Maximum value exceeded");
		assertEquals("{x:'2'}", x.getX18((byte)2));
		assertEquals("{x:'9'}", x.getX18((byte)9));
		assertThrown(()->{x.getX18((byte)1);}).contains("Minimum value not met");
		assertThrown(()->{x.getX18((byte)10);}).contains("Maximum value exceeded");
		assertEquals("{x:'1'}", x.getX19(new AtomicInteger(1)));
		assertEquals("{x:'10'}", x.getX19(new AtomicInteger(10)));
		assertThrown(()->{x.getX19(new AtomicInteger(0));}).contains("Minimum value not met");
		assertThrown(()->{x.getX19(new AtomicInteger(11));}).contains("Maximum value exceeded");
		assertEquals("{x:'1'}", x.getX20(new AtomicInteger(1)));
		assertEquals("{x:'10'}", x.getX20(new AtomicInteger(10)));
		assertThrown(()->{x.getX20(new AtomicInteger(0));}).contains("Minimum value not met");
		assertThrown(()->{x.getX20(new AtomicInteger(11));}).contains("Maximum value exceeded");
		assertEquals("{x:'2'}", x.getX21(new AtomicInteger(2)));
		assertEquals("{x:'9'}", x.getX21(new AtomicInteger(9)));
		assertThrown(()->{x.getX21(new AtomicInteger(1));}).contains("Minimum value not met");
		assertThrown(()->{x.getX21(new AtomicInteger(10));}).contains("Maximum value exceeded");
		assertEquals("{x:'1'}", x.getX22(new BigDecimal(1)));
		assertEquals("{x:'10'}", x.getX22(new BigDecimal(10)));
		assertThrown(()->{x.getX22(new BigDecimal(0));}).contains("Minimum value not met");
		assertThrown(()->{x.getX22(new BigDecimal(11));}).contains("Maximum value exceeded");
		assertEquals("{x:'1'}", x.getX23(new BigDecimal(1)));
		assertEquals("{x:'10'}", x.getX23(new BigDecimal(10)));
		assertThrown(()->{x.getX23(new BigDecimal(0));}).contains("Minimum value not met");
		assertThrown(()->{x.getX23(new BigDecimal(11));}).contains("Maximum value exceeded");
		assertEquals("{x:'2'}", x.getX24(new BigDecimal(2)));
		assertEquals("{x:'9'}", x.getX24(new BigDecimal(9)));
		assertThrown(()->{x.getX24(new BigDecimal(1));}).contains("Minimum value not met");
		assertThrown(()->{x.getX24(new BigDecimal(10));}).contains("Maximum value exceeded");
		assertEquals("{x:'1'}", x.getX25(1));
		assertEquals("{x:'10'}", x.getX25(10));
		assertThrown(()->{x.getX25(0);}).contains("Minimum value not met");
		assertThrown(()->{x.getX25(11);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX25(null));
		assertEquals("{x:'1'}", x.getX26(1));
		assertEquals("{x:'10'}", x.getX26(10));
		assertThrown(()->{x.getX26(0);}).contains("Minimum value not met");
		assertThrown(()->{x.getX26(11);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX26(null));
		assertEquals("{x:'2'}", x.getX27(2));
		assertEquals("{x:'9'}", x.getX27(9));
		assertThrown(()->{x.getX27(1);}).contains("Minimum value not met");
		assertThrown(()->{x.getX27(10);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX27(null));
		assertEquals("{x:'1'}", x.getX28((short)1));
		assertEquals("{x:'10'}", x.getX28((short)10));
		assertThrown(()->{x.getX28((short)0);}).contains("Minimum value not met");
		assertThrown(()->{x.getX28((short)11);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX28(null));
		assertEquals("{x:'1'}", x.getX29((short)1));
		assertEquals("{x:'10'}", x.getX29((short)10));
		assertThrown(()->{x.getX29((short)0);}).contains("Minimum value not met");
		assertThrown(()->{x.getX29((short)11);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX29(null));
		assertEquals("{x:'2'}", x.getX30((short)2));
		assertEquals("{x:'9'}", x.getX30((short)9));
		assertThrown(()->{x.getX30((short)1);}).contains("Minimum value not met");
		assertThrown(()->{x.getX30((short)10);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX30(null));
		assertEquals("{x:'1'}", x.getX31(1l));
		assertEquals("{x:'10'}", x.getX31(10l));
		assertThrown(()->{x.getX31(0l);}).contains("Minimum value not met");
		assertThrown(()->{x.getX31(11l);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX31(null));
		assertEquals("{x:'1'}", x.getX32(1l));
		assertEquals("{x:'10'}", x.getX32(10l));
		assertThrown(()->{x.getX32(0l);}).contains("Minimum value not met");
		assertThrown(()->{x.getX32(11l);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX32(null));
		assertEquals("{x:'2'}", x.getX33(2l));
		assertEquals("{x:'9'}", x.getX33(9l));
		assertThrown(()->{x.getX33(1l);}).contains("Minimum value not met");
		assertThrown(()->{x.getX33(10l);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX33(null));
		assertEquals("{x:'1.0'}", x.getX34(1f));
		assertEquals("{x:'10.0'}", x.getX34(10f));
		assertThrown(()->{x.getX34(0.9f);}).contains("Minimum value not met");
		assertThrown(()->{x.getX34(10.1f);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX34(null));
		assertEquals("{x:'1.0'}", x.getX35(1f));
		assertEquals("{x:'10.0'}", x.getX35(10f));
		assertThrown(()->{x.getX35(0.9f);}).contains("Minimum value not met");
		assertThrown(()->{x.getX35(10.1f);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX35(null));
		assertEquals("{x:'1.1'}", x.getX36(1.1f));
		assertEquals("{x:'9.9'}", x.getX36(9.9f));
		assertThrown(()->{x.getX36(1f);}).contains("Minimum value not met");
		assertThrown(()->{x.getX36(10f);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX36(null));
		assertEquals("{x:'1.0'}", x.getX37(1d));
		assertEquals("{x:'10.0'}", x.getX37(10d));
		assertThrown(()->{x.getX37(0.9d);}).contains("Minimum value not met");
		assertThrown(()->{x.getX37(10.1d);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX37(null));
		assertEquals("{x:'1.0'}", x.getX38(1d));
		assertEquals("{x:'10.0'}", x.getX38(10d));
		assertThrown(()->{x.getX38(0.9d);}).contains("Minimum value not met");
		assertThrown(()->{x.getX38(10.1d);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX38(null));
		assertEquals("{x:'1.1'}", x.getX39(1.1d));
		assertEquals("{x:'9.9'}", x.getX39(9.9d));
		assertThrown(()->{x.getX39(1d);}).contains("Minimum value not met");
		assertThrown(()->{x.getX39(10d);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX39(null));
		assertEquals("{x:'1'}", x.getX40((byte)1));
		assertEquals("{x:'10'}", x.getX40((byte)10));
		assertThrown(()->{x.getX40((byte)0);}).contains("Minimum value not met");
		assertThrown(()->{x.getX40((byte)11);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX40(null));
		assertEquals("{x:'1'}", x.getX41((byte)1));
		assertEquals("{x:'10'}", x.getX41((byte)10));
		assertThrown(()->{x.getX41((byte)0);}).contains("Minimum value not met");
		assertThrown(()->{x.getX41((byte)11);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX41(null));
		assertEquals("{x:'2'}", x.getX42((byte)2));
		assertEquals("{x:'9'}", x.getX42((byte)9));
		assertThrown(()->{x.getX42((byte)1);}).contains("Minimum value not met");
		assertThrown(()->{x.getX42((byte)10);}).contains("Maximum value exceeded");
		assertEquals("{}", x.getX42(null));
	}

	//=================================================================================================================
	// @Query(maxItems,minItems,uniqueItems)
	//=================================================================================================================

	@Rest
	public static class E {
		@RestMethod
		public String get(@Query("*") OMap m) {
			return m.toString();
		}
	}

	@Remote
	public static interface E1 {
		@RemoteMethod(path="/") String getX1(@Query(n="x",cf="pipes",mini=1,maxi=2) String...b);
		@RemoteMethod(path="/") String getX2(@Query(n="x",items=@Items(cf="pipes",mini=1,maxi=2)) String[]...b);
		@RemoteMethod(path="/") String getX3(@Query(n="x",cf="pipes",ui=false) String...b);
		@RemoteMethod(path="/") String getX4(@Query(n="x",items=@Items(cf="pipes",ui=false)) String[]...b);
		@RemoteMethod(path="/") String getX5(@Query(n="x",cf="pipes",ui=true) String...b);
		@RemoteMethod(path="/") String getX6(@Query(n="x",items=@Items(cf="pipes",ui=true)) String[]...b);
	}

	@Test
	public void e01_mini_maxi_ui() throws Exception {
		E1 x = MockRestClient.build(E.class).getRemote(E1.class);
		assertEquals("{x:'1'}", x.getX1("1"));
		assertEquals("{x:'1|2'}", x.getX1("1","2"));
		assertThrown(()->{x.getX1();}).contains("Minimum number of items not met");
		assertThrown(()->{x.getX1("1","2","3");}).contains("Maximum number of items exceeded");
		assertEquals("{x:null}", x.getX1((String)null));
		assertEquals("{x:'1'}", x.getX2(new String[]{"1"}));
		assertEquals("{x:'1|2'}", x.getX2(new String[]{"1","2"}));
		assertThrown(()->{x.getX2(new String[]{});}).contains("Minimum number of items not met");
		assertThrown(()->{x.getX2(new String[]{"1","2","3"});}).contains("Maximum number of items exceeded");
		assertEquals("{x:null}", x.getX2(new String[]{null}));
		assertEquals("{x:'1|1'}", x.getX3("1","1"));
		assertEquals("{x:'1|1'}", x.getX4(new String[]{"1","1"}));
		assertEquals("{x:'1|2'}", x.getX5("1","2"));
		assertThrown(()->{x.getX5("1","1");}).contains("Duplicate items not allowed");
		assertEquals("{x:'1|2'}", x.getX6(new String[]{"1","2"}));
		assertThrown(()->{x.getX6(new String[]{"1","1"});}).contains("Duplicate items not allowed");
	}

	//=================================================================================================================
	// @Query(maxLength,minLength,enum)
	//=================================================================================================================

	@Rest
	public static class F {
		@RestMethod
		public String get(@Query("*") OMap m) {
			return m.toString();
		}
	}

	@Remote
	public static interface F1 {
		@RemoteMethod(path="/") String getX1(@Query(n="x",minl=2,maxl=3) String b);
		@RemoteMethod(path="/") String getX2(@Query(n="x",cf="pipes",items=@Items(minl=2,maxl=3)) String...b);
		@RemoteMethod(path="/") String getX3(@Query(n="x",e={"foo"}) String b);
		@RemoteMethod(path="/") String getX4(@Query(n="x",cf="pipes",items=@Items(e={"foo"})) String...b);
		@RemoteMethod(path="/") String getX5(@Query(n="x",p="foo\\d{1,3}") String b);
		@RemoteMethod(path="/") String getX6(@Query(n="x",cf="pipes",items=@Items(p="foo\\d{1,3}")) String...b);
	}

	@Test
	public void f01_minl_maxl_enum() throws Exception {
		F1 x = MockRestClient.build(F.class).getRemote(F1.class);
		assertEquals("{x:'12'}", x.getX1("12"));
		assertEquals("{x:'123'}", x.getX1("123"));
		assertThrown(()->{x.getX1("1");}).contains("Minimum length of value not met");
		assertThrown(()->{x.getX1("1234");}).contains("Maximum length of value exceeded");
		assertEquals("{}", x.getX1(null));
		assertEquals("{x:'12|34'}", x.getX2("12","34"));
		assertEquals("{x:'123|456'}", x.getX2("123","456"));
		assertThrown(()->{x.getX2("1","2");}).contains("Minimum length of value not met");
		assertThrown(()->{x.getX2("1234","5678");}).contains("Maximum length of value exceeded");
		assertEquals("{x:'12|null'}", x.getX2("12",null));
		assertEquals("{x:'foo'}", x.getX3("foo"));
		assertThrown(()->{x.getX3("bar");}).contains("Value does not match one of the expected values.  Must be one of the following: ['foo']");
		assertEquals("{}", x.getX3(null));
		assertEquals("{x:'foo'}", x.getX4("foo"));
		assertThrown(()->{x.getX4("bar");}).contains("Value does not match one of the expected values.  Must be one of the following: ['foo']");
		assertEquals("{x:null}", x.getX4((String)null));
		assertEquals("{x:'foo123'}", x.getX5("foo123"));
		assertThrown(()->{x.getX5("bar");}).contains("Value does not match expected pattern");
		assertEquals("{}", x.getX5(null));
		assertEquals("{x:'foo123'}", x.getX6("foo123"));
		assertThrown(()->{x.getX6("foo");}).contains("Value does not match expected pattern");
		assertEquals("{x:null}", x.getX6((String)null));
	}

	//=================================================================================================================
	// @Query(multipleOf)
	//=================================================================================================================

	@Rest
	public static class G {
		@RestMethod
		public String get(@Query("*") OMap m) {
			return m.toString();
		}
	}

	@Remote
	public static interface G1 {
		@RemoteMethod(path="/") String getX1(@Query(n="x",mo="2") int b);
		@RemoteMethod(path="/") String getX2(@Query(n="x",mo="2") short b);
		@RemoteMethod(path="/") String getX3(@Query(n="x",mo="2") long b);
		@RemoteMethod(path="/") String getX4(@Query(n="x",mo="2") float b);
		@RemoteMethod(path="/") String getX5(@Query(n="x",mo="2") double b);
		@RemoteMethod(path="/") String getX6(@Query(n="x",mo="2") byte b);
		@RemoteMethod(path="/") String getX7(@Query(n="x",mo="2") AtomicInteger b);
		@RemoteMethod(path="/") String getX8(@Query(n="x",mo="2") BigDecimal b);
		@RemoteMethod(path="/") String getX9(@Query(n="x",mo="2") Integer b);
		@RemoteMethod(path="/") String getX10(@Query(n="x",mo="2") Short b);
		@RemoteMethod(path="/") String getX11(@Query(n="x",mo="2") Long b);
		@RemoteMethod(path="/") String getX12(@Query(n="x",mo="2") Float b);
		@RemoteMethod(path="/") String getX13(@Query(n="x",mo="2") Double b);
		@RemoteMethod(path="/") String getX14(@Query(n="x",mo="2") Byte b);
	}

	@Test
	public void g01_multipleOf() throws Exception {
		G1 x = MockRestClient.build(G.class).getRemote(G1.class);
		assertEquals("{x:'4'}", x.getX1(4));
		assertThrown(()->{x.getX1(5);}).contains("Multiple-of not met");
		assertEquals("{x:'4'}", x.getX2((short)4));
		assertThrown(()->{x.getX2((short)5);}).contains("Multiple-of not met");
		assertEquals("{x:'4'}", x.getX3(4));
		assertThrown(()->{x.getX3(5);}).contains("Multiple-of not met");
		assertEquals("{x:'4.0'}", x.getX4(4));
		assertThrown(()->{x.getX4(5);}).contains("Multiple-of not met");
		assertEquals("{x:'4.0'}", x.getX5(4));
		assertThrown(()->{x.getX5(5);}).contains("Multiple-of not met");
		assertEquals("{x:'4'}", x.getX6((byte)4));
		assertThrown(()->{x.getX6((byte)5);}).contains("Multiple-of not met");
		assertEquals("{x:'4'}", x.getX7(new AtomicInteger(4)));
		assertThrown(()->{x.getX7(new AtomicInteger(5));}).contains("Multiple-of not met");
		assertEquals("{x:'4'}", x.getX8(new BigDecimal(4)));
		assertThrown(()->{x.getX8(new BigDecimal(5));}).contains("Multiple-of not met");
		assertEquals("{x:'4'}", x.getX9(4));
		assertThrown(()->{x.getX9(5);}).contains("Multiple-of not met");
		assertEquals("{x:'4'}", x.getX10((short)4));
		assertThrown(()->{x.getX10((short)5);}).contains("Multiple-of not met");
		assertEquals("{x:'4'}", x.getX11(4l));
		assertThrown(()->{x.getX11(5l);}).contains("Multiple-of not met");
		assertEquals("{x:'4.0'}", x.getX12(4f));
		assertThrown(()->{x.getX12(5f);}).contains("Multiple-of not met");
		assertEquals("{x:'4.0'}", x.getX13(4d));
		assertThrown(()->{x.getX13(5d);}).contains("Multiple-of not met");
		assertEquals("{x:'4'}", x.getX14((byte)4));
		assertThrown(()->{x.getX14((byte)5);}).contains("Multiple-of not met");
	}

	//=================================================================================================================
	// @Query(required)
	//=================================================================================================================

	@Rest
	public static class H {
		@RestMethod
		public String get(@Query("*") OMap m) {
			return m.toString();
		}
	}

	@Remote
	public static interface H1 {
		@RemoteMethod(path="/") String getX1(@Query(n="x") String b);
		@RemoteMethod(path="/") String getX2(@Query(n="x",r=false) String b);
		@RemoteMethod(path="/") String getX3(@Query(n="x",r=true) String b);
	}

	@Test
	public void h01_required() throws Exception {
		H1 x = MockRestClient.build(H.class).getRemote(H1.class);
		assertEquals("{}", x.getX1(null));
		assertEquals("{}", x.getX2(null));
		assertEquals("{x:'1'}", x.getX3("1"));
		assertThrown(()->{x.getX3(null);}).contains("Required value not provided.");
	}

	//=================================================================================================================
	// @Query(skipIfEmpty)
	//=================================================================================================================

	@Rest
	public static class I {
		@RestMethod
		public String get(@Query("*") OMap m) {
			return m.toString();
		}
	}

	@Remote
	public static interface I1 {
		@RemoteMethod(path="/") String getX1(@Query(n="x",aev=true) String b);
		@RemoteMethod(path="/") String getX2(@Query(n="x",aev=true,sie=false) String b);
		@RemoteMethod(path="/") String getX3(@Query(n="x",sie=true) String b);
	}

	@Test
	public void i01_skipIfEmpty() throws Exception {
		I1 x = MockRestClient.build(I.class).getRemote(I1.class);
		assertEquals("{x:''}", x.getX1(""));
		assertEquals("{x:''}", x.getX2(""));
		assertEquals("{}", x.getX3(""));
	}

	//=================================================================================================================
	// @Query(serializer)
	//=================================================================================================================

	@Rest
	public static class J {
		@RestMethod
		public String get(@Query("*") OMap m) {
			return m.toString();
		}
	}

	@Remote
	public static interface J1 {
		@RemoteMethod(path="/") String getX1(@Query(n="x",serializer=XPartSerializer.class) String b);
	}

	@Test
	public void j01_serializer() throws Exception {
		J1 x = MockRestClient.build(J.class).getRemote(J1.class);
		assertEquals("{x:'xXx'}", x.getX1("X"));
	}

	//=================================================================================================================
	// RequestBean @Query - Return types
	//=================================================================================================================

	@Rest
	public static class K {
		@RestMethod
		public String get(RestRequest req) throws Exception {
			return req.getQuery().toString(true);
		}
	}

	//=================================================================================================================
	// RequestBean @Query - Simple values
	//=================================================================================================================

	@Remote(path="/")
	public static interface K1 {
		@RemoteMethod(path="/") String getX1(@Request K1b rb);
		@RemoteMethod(path="/") String getX2(@Request(partSerializer=XSerializer.class) K1b rb);
	}

	public static interface K1a {
		@Query String getA();
		@Query("b") String getX1();
		@Query(n="c") String getX2();
		@Query(n="e",allowEmptyValue=true) String getX4();
		@Query("f") String getX5();
		@Query("g") String getX6();
		@Query("h") String getX7();
		@Query(n="i1",sie=true) String getX8();
		@Query(n="i2",sie=true) String getX9();
		@Query(n="i3",sie=true) String getX10();
	}

	public static class K1b implements K1a {
		@Override public String getA() { return "a1"; }
		@Override public String getX1() { return "b1"; }
		@Override public String getX2() { return "c1"; }
		@Override public String getX4() { return ""; }
		@Override public String getX5() { return null; }
		@Override public String getX6() { return "true"; }
		@Override public String getX7() { return "123"; }
		@Override public String getX8() { return "foo"; }
		@Override public String getX9() { return ""; }
		@Override public String getX10() { return null; }
	}

	@Test
	public void k01_requestBean_simpleVals() throws Exception {
		K1 x1 = MockRestClient.build(K.class).getRemote(K1.class);
		K1 x2 = MockRestClient.create(K.class).partSerializer(UonSerializer.class).build().getRemote(K1.class);
		assertEquals("{a:'a1',b:'b1',c:'c1',e:'',g:'true',h:'123',i1:'foo'}", x1.getX1(new K1b()));
		assertEquals("{a:'a1',b:'b1',c:'c1',e:'',g:'\\'true\\'',h:'\\'123\\'',i1:'foo'}", x2.getX1(new K1b()));
		assertEquals("{a:'xa1x',b:'xb1x',c:'xc1x',e:'xx',g:'xtruex',h:'x123x',i1:'xfoox'}", x2.getX2(new K1b()));
	}

	//=================================================================================================================
	// RequestBean @Query - Maps
	//=================================================================================================================

	@Remote(path="/")
	public static interface K2 {
		@RemoteMethod(path="/") String getX1(@Request K2a rb);
		@RemoteMethod(path="/") String getX2(@Request(partSerializer=XSerializer.class) K2a rb);
	}

	public static class K2a {
		@Query
		public Map<String,Object> getA() {
			return AMap.of("a1","v1","a2",123,"a3",null,"a4","");
		}
		@Query("*")
		public Map<String,Object> getB() {
			return AMap.of("b1","true","b2","123","b3","null");
		}
		@Query(n="*",allowEmptyValue=true)
		public Map<String,Object> getC() {
			return AMap.of("c1","v1","c2",123,"c3",null,"c4","");
		}
		@Query("*")
		public Map<String,Object> getD() {
			return null;
		}
	}

	@Test
	public void k02_requestBean_maps() throws Exception {
		K2 x1 = MockRestClient.build(K.class).getRemote(K2.class);
		K2 x2 = MockRestClient.create(K.class).partSerializer(UonSerializer.class).build().getRemote(K2.class);
		assertEquals("{a:'a1=v1,a2=123,a3=null,a4=',b1:'true',b2:'123',b3:'null',c1:'v1',c2:'123',c4:''}", x1.getX1(new K2a()));
		assertEquals("{a:'(a1=v1,a2=123,a3=null,a4=\\'\\')',b1:'\\'true\\'',b2:'\\'123\\'',b3:'\\'null\\'',c1:'v1',c2:'123',c4:''}", x2.getX1(new K2a()));
		assertEquals("{a:'x{a1:\\'v1\\',a2:123,a3:null,a4:\\'\\'}x',b1:'xtruex',b2:'x123x',b3:'xnullx',c1:'xv1x',c2:'x123x',c4:'xx'}", x2.getX2(new K2a()));
	}

	//=================================================================================================================
	// RequestBean @Query - NameValuePairs
	//=================================================================================================================

	@Remote(path="/")
	public static interface K3 {
		@RemoteMethod(path="/") String getX1(@Request K3a rb);
		@RemoteMethod(path="/") String getX2(@Request(partSerializer=XSerializer.class) K3a rb);
	}

	public static class K3a {
		@Query(aev=true)
		public NameValuePairs getA() {
			return new NameValuePairs().append("a1","v1").append("a2", 123).append("a3", null).append("a4", "");
		}
		@Query("*")
		public NameValuePairs getB() {
			return new NameValuePairs().append("b1","true").append("b2", "123").append("b3", "null");
		}
		@Query(n="*",aev=true)
		public NameValuePairs getC() {
			return new NameValuePairs().append("c1","v1").append("c2", 123).append("c3", null).append("c4", "");
		}
		@Query("*")
		public NameValuePairs getD() {
			return null;
		}
		@Query(aev=true)
		public NameValuePair[] getE() {
			return new NameValuePairs().append("e1","v1").append("e2", 123).append("e3", null).append("e4", "").toArray(new NameValuePair[0]);
		}
		@Query(aev=true)
		public BasicNameValuePair[] getF() {
			return new NameValuePairs().append("f1","v1").append("f2", 123).append("f3", null).append("f4", "").toArray(new BasicNameValuePair[0]);
		}
	}

	@Test
	public void k03_requestBean_nameValuePairs() throws Exception {
		K3 x1 = MockRestClient.build(K.class).getRemote(K3.class);
		K3 x2 = MockRestClient.create(K.class).partSerializer(UonSerializer.class).build().getRemote(K3.class);
		assertEquals("{a1:'v1',a2:'123',a4:'',b1:'true',b2:'123',b3:'null',c1:'v1',c2:'123',c4:'',e1:'v1',e2:'123',e4:'',f1:'v1',f2:'123',f4:''}", x1.getX1(new K3a()));
		assertEquals("{a1:'v1',a2:'123',a4:'',b1:'true',b2:'123',b3:'null',c1:'v1',c2:'123',c4:'',e1:'v1',e2:'123',e4:'',f1:'v1',f2:'123',f4:''}", x2.getX1(new K3a()));
		assertEquals("{a1:'v1',a2:'123',a4:'',b1:'true',b2:'123',b3:'null',c1:'v1',c2:'123',c4:'',e1:'v1',e2:'123',e4:'',f1:'v1',f2:'123',f4:''}", x2.getX2(new K3a()));
	}

	//=================================================================================================================
	// RequestBean @Query - CharSequence
	//=================================================================================================================

	@Remote(path="/")
	public static interface K4 {
		String get(@Request K4a rb);
	}

	public static class K4a {
		@Query("*")
		public StringBuilder getA() {
			return new StringBuilder("foo=bar&baz=qux");
		}
	}

	@Test
	public void k04_requestBean_charSequence() throws Exception {
		K4 x = MockRestClient.build(K.class).getRemote(K4.class);
		assertEquals("{baz:'qux',foo:'bar'}", x.get(new K4a()));
	}

	//=================================================================================================================
	// RequestBean @Query - Reader
	//=================================================================================================================

	@Remote(path="/")
	public static interface K5 {
		String get(@Request K5a rb);
	}

	public static class K5a {
		@Query("*")
		public Reader getA() {
			return new StringReader("foo=bar&baz=qux");
		}
	}

	@Test
	public void k05_requestBean_reader() throws Exception {
		K5 x = MockRestClient.build(K.class).getRemote(K5.class);
		assertEquals("{baz:'qux',foo:'bar'}", x.get(new K5a()));
	}

	//=================================================================================================================
	// RequestBean @Query - Collections
	//=================================================================================================================

	@Remote(path="/")
	public static interface K6 {
		@RemoteMethod(path="/") String getX1(@Request K6a rb);
		@RemoteMethod(path="/") String getX2(@Request(partSerializer=XSerializer.class) K6a rb);
	}

	public static class K6a {
		@Query
		public List<Object> getA() {
			return AList.of("foo","","true","123","null",true,123,null);
		}
		@Query("b")
		public List<Object> getX1() {
			return AList.of("foo","","true","123","null",true,123,null);
		}
		@Query(n="c", serializer=ListSerializer.class)
		public List<Object> getX2() {
			return AList.of("foo","","true","123","null",true,123,null);
		}
		@Query(n="d",allowEmptyValue=true)
		public List<Object> getX3() {
			return AList.of();
		}
		@Query("e")
		public List<Object> getX4() {
			return null;
		}
		@Query("f")
		public Object[] getX5() {
			return new Object[]{"foo", "", "true", "123", "null", true, 123, null};
		}
		@Query(n="g", serializer=ListSerializer.class)
		public Object[] getX6() {
			return new Object[]{"foo", "", "true", "123", "null", true, 123, null};
		}
		@Query(n="h",allowEmptyValue=true)
		public Object[] getX7() {
			return new Object[]{};
		}
		@Query("i")
		public Object[] getX8() {
			return null;
		}
	}

	@Test
	public void k06_requestBean_collections() throws Exception {
		K6 x1 = MockRestClient.build(K.class).getRemote(K6.class);
		K6 x2 = MockRestClient.create(K.class).partSerializer(UonSerializer.class).build().getRemote(K6.class);
		assertEquals("{a:'foo,,true,123,null,true,123,null',b:'foo,,true,123,null,true,123,null',c:'foo||true|123|null|true|123|null',d:'',f:'foo,,true,123,null,true,123,null',g:'foo||true|123|null|true|123|null',h:''}", x1.getX1(new K6a()));
		assertEquals("{a:'@(foo,\\'\\',\\'true\\',\\'123\\',\\'null\\',true,123,null)',b:'@(foo,\\'\\',\\'true\\',\\'123\\',\\'null\\',true,123,null)',c:'foo||true|123|null|true|123|null',d:'@()',f:'@(foo,\\'\\',\\'true\\',\\'123\\',\\'null\\',true,123,null)',g:'foo||true|123|null|true|123|null',h:'@()'}", x2.getX1(new K6a()));
		assertEquals("{a:'fooXXtrueX123XnullXtrueX123Xnull',b:'fooXXtrueX123XnullXtrueX123Xnull',c:'foo||true|123|null|true|123|null',d:'',f:'fooXXtrueX123XnullXtrueX123Xnull',g:'foo||true|123|null|true|123|null',h:''}", x2.getX2(new K6a()));
	}
}
