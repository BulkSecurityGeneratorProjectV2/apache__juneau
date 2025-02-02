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
package org.apache.juneau;

import static org.apache.juneau.internal.CollectionUtils.*;
import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;
import static org.apache.juneau.assertions.Assertions.*;

import java.util.*;

import org.apache.juneau.reflect.ClassInfoTest.*;
import org.apache.juneau.swap.*;
import org.junit.*;

@SuppressWarnings({"rawtypes","serial"})
@FixMethodOrder(NAME_ASCENDING)
public class ClassMetaTest {

	BeanContext bc = BeanContext.DEFAULT;

	//-----------------------------------------------------------------------------------------------------------------
	// Basic tests
	//-----------------------------------------------------------------------------------------------------------------

	public Map<String,String> fa;

	@Test
	public void a01_map() throws Exception {
		ClassMeta t = bc.getClassMeta(this.getClass().getField("fa").getGenericType());
		assertEquals("java.util.Map<java.lang.String,java.lang.String>", t.toString());
		assertTrue(t.isMap());
		assertFalse(t.isCollection());
		assertNull(t.newInstance());
		assertEquals(Map.class, t.getInnerClass());
		assertEquals(String.class, t.getKeyType().getInnerClass());
		assertEquals(String.class, t.getValueType().getInnerClass());
	}

	public String fb;

	@Test
	public void a02_string() throws Exception {
		ClassMeta t = bc.getClassMeta(this.getClass().getField("fb").getGenericType());
		assertEquals(String.class, t.getInnerClass());
		t = bc.getClassMeta(this.getClass().getField("fb").getType());
		assertEquals(String.class, t.getInnerClass());
	}

	public Map<String,Map<String,Integer>> fc;

	@Test
	public void a03_mapWithMapValues() throws Exception {
		ClassMeta t = bc.getClassMeta(this.getClass().getField("fc").getGenericType());
		assertEquals("java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Integer>>", t.toString());
		t = bc.getClassMeta(this.getClass().getField("fc").getType());
		assertEquals("java.util.Map", t.toString());
	}

	public List<Map<String,List>> fd;

	@Test
	public void a04_listWithMapValues() throws Exception {
		ClassMeta t = bc.getClassMeta(this.getClass().getField("fd").getGenericType());
		assertEquals("java.util.List<java.util.Map<java.lang.String,java.util.List>>", t.toString());
	}

	public List<? extends String> fe1;
	public List<? super String> fe2;

	@Test
	public void a05_listWithUpperBoundGenericEntryTypes() throws Exception {
		ClassMeta t = bc.getClassMeta(this.getClass().getField("fe1").getGenericType());
		assertEquals("java.util.List", t.toString());
		t = bc.getClassMeta(this.getClass().getField("fe2").getGenericType());
		assertEquals("java.util.List", t.toString());
	}

	public class G extends HashMap<String,Object> {}
	public G g;

	@Test
	public void a06_beanExtendsMap() throws Exception {
		ClassMeta t = bc.getClassMeta(this.getClass().getField("g").getGenericType());
		assertEquals("org.apache.juneau.ClassMetaTest$G<java.lang.String,java.lang.Object>", t.toString());
		assertTrue(t.isMap());
		assertFalse(t.isCollection());
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Swaps
	// Ensure swaps on parent and child classes are properly detected.
	//-----------------------------------------------------------------------------------------------------------------

	public interface BI1 {}
	public class BC1 implements BI1 {}
	public interface BI2 extends BI1 {}
	public class BC2 extends BC1 implements BI2 {}
	public static class BC1Swap extends ObjectSwap<BC1,Map> {}
	public static class BI1Swap extends ObjectSwap<BI1,Map> {}
	public static class BC2Swap extends ObjectSwap<BC2,Map> {}
	public static class BI2Swap extends ObjectSwap<BI2,Map> {}

	@Test
	public void b01_swaps() throws Exception {
		BeanContext bc;
		ClassMeta<?> ooo, hi1, hc1, hi2, hc2;
		BeanSession bs;

		bc = BeanContext.DEFAULT;
		bs = bc.getSession();
		ooo = bc.getClassMeta(Object.class);
		hi1 = bc.getClassMeta(BI1.class);
		hc1 = bc.getClassMeta(BC1.class);
		hi2 = bc.getClassMeta(BI2.class);
		hc2 = bc.getClassMeta(BC2.class);
		assertFalse(ooo.hasChildSwaps());
		assertFalse(hi1.hasChildSwaps());
		assertFalse(hc1.hasChildSwaps());
		assertFalse(hi2.hasChildSwaps());
		assertFalse(hc2.hasChildSwaps());
		assertNull(ooo.getSwap(bs));
		assertNull(hi1.getSwap(bs));
		assertNull(hc1.getSwap(bs));
		assertNull(hi2.getSwap(bs));
		assertNull(hc2.getSwap(bs));
		assertEquals(ooo.getSerializedClassMeta(bs).getInnerClass(), Object.class);
		assertEquals(hi1.getSerializedClassMeta(bs).getInnerClass(), BI1.class);
		assertEquals(hc1.getSerializedClassMeta(bs).getInnerClass(), BC1.class);
		assertEquals(hi2.getSerializedClassMeta(bs).getInnerClass(), BI2.class);
		assertEquals(hc2.getSerializedClassMeta(bs).getInnerClass(), BC2.class);

		bc = BeanContext.create().swaps(BI1Swap.class).build();
		bs = bc.getSession();
		ooo = bc.getClassMeta(Object.class);
		hi1 = bc.getClassMeta(BI1.class);
		hc1 = bc.getClassMeta(BC1.class);
		hi2 = bc.getClassMeta(BI2.class);
		hc2 = bc.getClassMeta(BC2.class);
		assertTrue(ooo.hasChildSwaps());
		assertTrue(hi1.hasChildSwaps());
		assertFalse(hc1.hasChildSwaps());
		assertFalse(hi2.hasChildSwaps());
		assertFalse(hc2.hasChildSwaps());
		assertNull(ooo.getSwap(bs));
		assertEquals(hi1.getSwap(bs).getClass(), BI1Swap.class);
		assertEquals(hc1.getSwap(bs).getClass(), BI1Swap.class);
		assertEquals(hi2.getSwap(bs).getClass(), BI1Swap.class);
		assertEquals(hc2.getSwap(bs).getClass(), BI1Swap.class);
		assertEquals(ooo.getSerializedClassMeta(bs).getInnerClass(), Object.class);
		assertEquals(hi1.getSerializedClassMeta(bs).getInnerClass(), Map.class);
		assertEquals(hc1.getSerializedClassMeta(bs).getInnerClass(), Map.class);
		assertEquals(hi2.getSerializedClassMeta(bs).getInnerClass(), Map.class);
		assertEquals(hc2.getSerializedClassMeta(bs).getInnerClass(), Map.class);

		bc = BeanContext.create().swaps(BC1Swap.class).build();
		bs = bc.getSession();
		ooo = bc.getClassMeta(Object.class);
		hi1 = bc.getClassMeta(BI1.class);
		hc1 = bc.getClassMeta(BC1.class);
		hi2 = bc.getClassMeta(BI2.class);
		hc2 = bc.getClassMeta(BC2.class);
		assertTrue(ooo.hasChildSwaps());
		assertTrue(hi1.hasChildSwaps());
		assertTrue(hc1.hasChildSwaps());
		assertFalse(hi2.hasChildSwaps());
		assertFalse(hc2.hasChildSwaps());
		assertNull(ooo.getSwap(bs));
		assertNull(hi1.getSwap(bs));
		assertEquals(hc1.getSwap(bs).getClass(), BC1Swap.class);
		assertNull(hi2.getSwap(bs));
		assertEquals(hc2.getSwap(bs).getClass(), BC1Swap.class);
		assertEquals(ooo.getSerializedClassMeta(bs).getInnerClass(), Object.class);
		assertEquals(hi1.getSerializedClassMeta(bs).getInnerClass(), BI1.class);
		assertEquals(hc1.getSerializedClassMeta(bs).getInnerClass(), Map.class);
		assertEquals(hi2.getSerializedClassMeta(bs).getInnerClass(), BI2.class);
		assertEquals(hc2.getSerializedClassMeta(bs).getInnerClass(), Map.class);

		bc = BeanContext.create().swaps(BI2Swap.class).build();
		bs = bc.getSession();
		ooo = bc.getClassMeta(Object.class);
		hi1 = bc.getClassMeta(BI1.class);
		hc1 = bc.getClassMeta(BC1.class);
		hi2 = bc.getClassMeta(BI2.class);
		hc2 = bc.getClassMeta(BC2.class);
		assertTrue(ooo.hasChildSwaps());
		assertTrue(hi1.hasChildSwaps());
		assertFalse(hc1.hasChildSwaps());
		assertTrue(hi2.hasChildSwaps());
		assertFalse(hc2.hasChildSwaps());
		assertNull(ooo.getSwap(bs));
		assertNull(hi1.getSwap(bs));
		assertNull(hc1.getSwap(bs));
		assertEquals(hi2.getSwap(bs).getClass(), BI2Swap.class);
		assertEquals(hc2.getSwap(bs).getClass(), BI2Swap.class);
		assertEquals(ooo.getSerializedClassMeta(bs).getInnerClass(), Object.class);
		assertEquals(hi1.getSerializedClassMeta(bs).getInnerClass(), BI1.class);
		assertEquals(hc1.getSerializedClassMeta(bs).getInnerClass(), BC1.class);
		assertEquals(hi2.getSerializedClassMeta(bs).getInnerClass(), Map.class);
		assertEquals(hc2.getSerializedClassMeta(bs).getInnerClass(), Map.class);

		bc = BeanContext.create().swaps(BC2Swap.class).build();
		bs = bc.getSession();
		ooo = bc.getClassMeta(Object.class);
		hi1 = bc.getClassMeta(BI1.class);
		hc1 = bc.getClassMeta(BC1.class);
		hi2 = bc.getClassMeta(BI2.class);
		hc2 = bc.getClassMeta(BC2.class);
		assertTrue(ooo.hasChildSwaps());
		assertTrue(hi1.hasChildSwaps());
		assertTrue(hc1.hasChildSwaps());
		assertTrue(hi2.hasChildSwaps());
		assertTrue(hc2.hasChildSwaps());
		assertNull(ooo.getSwap(bs));
		assertNull(hi1.getSwap(bs));
		assertNull(hc1.getSwap(bs));
		assertNull(hi2.getSwap(bs));
		assertEquals(hc2.getSwap(bs).getClass(), BC2Swap.class);
		assertEquals(ooo.getSerializedClassMeta(bs).getInnerClass(), Object.class);
		assertEquals(hi1.getSerializedClassMeta(bs).getInnerClass(), BI1.class);
		assertEquals(hc1.getSerializedClassMeta(bs).getInnerClass(), BC1.class);
		assertEquals(hi2.getSerializedClassMeta(bs).getInnerClass(), BI2.class);
		assertEquals(hc2.getSerializedClassMeta(bs).getInnerClass(), Map.class);

		bc = BeanContext.create().swaps(BI1Swap.class,BC1Swap.class,BI2Swap.class, BC2Swap.class).build();
		bs = bc.getSession();
		ooo = bc.getClassMeta(Object.class);
		hi1 = bc.getClassMeta(BI1.class);
		hc1 = bc.getClassMeta(BC1.class);
		hi2 = bc.getClassMeta(BI2.class);
		hc2 = bc.getClassMeta(BC2.class);
		assertTrue(ooo.hasChildSwaps());
		assertTrue(hi1.hasChildSwaps());
		assertTrue(hc1.hasChildSwaps());
		assertTrue(hi2.hasChildSwaps());
		assertTrue(hc2.hasChildSwaps());
		assertNull(ooo.getSwap(bs));
		assertEquals(hi1.getSwap(bs).getClass(), BI1Swap.class);
		assertEquals(hc1.getSwap(bs).getClass(), BI1Swap.class);
		assertEquals(hi2.getSwap(bs).getClass(), BI1Swap.class);
		assertEquals(hc2.getSwap(bs).getClass(), BI1Swap.class);
		assertEquals(ooo.getSerializedClassMeta(bs).getInnerClass(), Object.class);
		assertEquals(hi1.getSerializedClassMeta(bs).getInnerClass(), Map.class);
		assertEquals(hc1.getSerializedClassMeta(bs).getInnerClass(), Map.class);
		assertEquals(hi2.getSerializedClassMeta(bs).getInnerClass(), Map.class);
		assertEquals(hc2.getSerializedClassMeta(bs).getInnerClass(), Map.class);

		bc = BeanContext.create().swaps(BC2Swap.class,BI2Swap.class,BC1Swap.class, BI1Swap.class).build();
		bs = bc.getSession();
		ooo = bc.getClassMeta(Object.class);
		hi1 = bc.getClassMeta(BI1.class);
		hc1 = bc.getClassMeta(BC1.class);
		hi2 = bc.getClassMeta(BI2.class);
		hc2 = bc.getClassMeta(BC2.class);
		assertTrue(ooo.hasChildSwaps());
		assertTrue(hi1.hasChildSwaps());
		assertTrue(hc1.hasChildSwaps());
		assertTrue(hi2.hasChildSwaps());
		assertTrue(hc2.hasChildSwaps());
		assertNull(ooo.getSwap(bs));
		assertEquals(hi1.getSwap(bs).getClass(), BI1Swap.class);
		assertEquals(hc1.getSwap(bs).getClass(), BC1Swap.class);
		assertEquals(hi2.getSwap(bs).getClass(), BI2Swap.class);
		assertEquals(hc2.getSwap(bs).getClass(), BC2Swap.class);
		assertEquals(ooo.getSerializedClassMeta(bs).getInnerClass(), Object.class);
		assertEquals(hi1.getSerializedClassMeta(bs).getInnerClass(), Map.class);
		assertEquals(hc1.getSerializedClassMeta(bs).getInnerClass(), Map.class);
		assertEquals(hi2.getSerializedClassMeta(bs).getInnerClass(), Map.class);
		assertEquals(hc2.getSerializedClassMeta(bs).getInnerClass(), Map.class);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Annotations
	//-----------------------------------------------------------------------------------------------------------------

	@A(1) static interface CI1 {}
	@A(2) static interface CI2 extends CI1 {}
	@A(3) static interface CI3 {}
	@A(4) static interface CI4 {}
	@A(5) static class C1 implements CI1, CI2 {}
	@A(6) static class C2 extends C1 implements CI3 {}
	@A(7) static class C3 extends C2 {}
	static class C4 extends C3 {}
	static class C5 implements CI3 {}

	@Test
	public void forEachAnnotation() {
		ClassMeta<?> c3 = bc.getClassMeta(C3.class);
		ClassMeta<?> c4 = bc.getClassMeta(C4.class);
		ClassMeta<?> c5 = bc.getClassMeta(C5.class);

		List<Integer> l1 = list();
		c3.forEachAnnotation(A.class, null, x -> l1.add(x.value()));
		assertList(l1).asCdl().isString("2,1,3,5,6,7");

		List<Integer> l2 = list();
		c4.forEachAnnotation(A.class, null, x -> l2.add(x.value()));
		assertList(l2).asCdl().isString("2,1,3,5,6,7");

		List<Integer> l3 = list();
		c5.forEachAnnotation(A.class, null, x -> l3.add(x.value()));
		assertList(l3).asCdl().isString("3");

		List<Integer> l4 = list();
		c3.forEachAnnotation(A.class, x -> x.value() == 5, x -> l4.add(x.value()));
		assertList(l4).asCdl().isString("5");
	}

	@Test
	public void firstAnnotation() {
		ClassMeta<?> c3 = bc.getClassMeta(C3.class);
		ClassMeta<?> c4 = bc.getClassMeta(C4.class);
		ClassMeta<?> c5 = bc.getClassMeta(C5.class);
		assertInteger(c3.firstAnnotation(A.class, null).get().value()).is(2);
		assertInteger(c4.firstAnnotation(A.class, null).get().value()).is(2);
		assertInteger(c5.firstAnnotation(A.class, null).get().value()).is(3);
		assertInteger(c3.firstAnnotation(A.class, x -> x.value() == 5).get().value()).is(5);
	}
	@Test
	public void lastAnnotation() {
		ClassMeta<?> c3 = bc.getClassMeta(C3.class);
		ClassMeta<?> c4 = bc.getClassMeta(C4.class);
		ClassMeta<?> c5 = bc.getClassMeta(C5.class);
		assertInteger(c3.lastAnnotation(A.class, null).get().value()).is(7);
		assertInteger(c4.lastAnnotation(A.class, null).get().value()).is(7);
		assertInteger(c5.lastAnnotation(A.class, null).get().value()).is(3);
		assertInteger(c3.lastAnnotation(A.class, x -> x.value() == 5).get().value()).is(5);
	}
}
