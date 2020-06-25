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
package org.apache.juneau.utils;

import static org.apache.juneau.assertions.ObjectAssertion.*;
import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

import java.util.*;

import org.apache.juneau.internal.*;
import org.junit.*;

@FixMethodOrder(NAME_ASCENDING)
public class MultiSetTest {

	@Test
	public void doTest() throws Exception {
		List<String> l1, l2;
		MultiSet<String> ms;
		Iterator<String> i;

		l1 = Arrays.asList(new String[]{"1","2"});
		l2 = Arrays.asList(new String[]{"3","4"});
		ms = new MultiSet<>(l1, l2);
		i = ms.iterator();
		assertTrue(i.hasNext());
		assertEquals("1", i.next());
		assertTrue(i.hasNext());
		assertEquals("2", i.next());
		assertTrue(i.hasNext());
		assertEquals("3", i.next());
		assertTrue(i.hasNext());
		assertEquals("4", i.next());
		assertFalse(i.hasNext());
		try {
			i.next();
			fail();
		} catch (NoSuchElementException e) {
		}

		l1 = Arrays.asList(new String[]{"1","2"});
		l2 = Arrays.asList(new String[]{});
		ms = new MultiSet<>(l1, l2);
		i = ms.iterator();
		assertTrue(i.hasNext());
		assertEquals("1", i.next());
		assertTrue(i.hasNext());
		assertEquals("2", i.next());
		assertFalse(i.hasNext());
		try {
			i.next();
			fail();
		} catch (NoSuchElementException e) {
		}

		l1 = Arrays.asList(new String[]{});
		l2 = Arrays.asList(new String[]{"3","4"});
		ms = new MultiSet<>(l1, l2);
		i = ms.iterator();
		assertTrue(i.hasNext());
		assertEquals("3", i.next());
		assertTrue(i.hasNext());
		assertEquals("4", i.next());
		assertFalse(i.hasNext());
		try {
			i.next();
			fail();
		} catch (NoSuchElementException e) {
		}

		l1 = Arrays.asList(new String[]{});
		l2 = Arrays.asList(new String[]{});
		ms = new MultiSet<>(l1, l2);
		i = ms.iterator();
		assertFalse(i.hasNext());
		try {
			i.next();
			fail();
		} catch (NoSuchElementException e) {
		}

		l1 = Arrays.asList(new String[]{"1","2"});
		ms = new MultiSet<>(l1);
		i = ms.iterator();
		assertTrue(i.hasNext());
		assertEquals("1", i.next());
		assertTrue(i.hasNext());
		assertEquals("2", i.next());
		assertFalse(i.hasNext());
		try {
			i.next();
			fail();
		} catch (NoSuchElementException e) {
		}

		l1 = new LinkedList<>(Arrays.asList(new String[]{"1","2"}));
		l2 = new LinkedList<>(Arrays.asList(new String[]{"3","4"}));
		ms = new MultiSet<>(l1).append(l2);
		assertObject(ms).json().is("['1','2','3','4']");
		assertObject(ms.enumerator()).json().is("['1','2','3','4']");
		assertEquals(4, ms.size());

		Iterator<String> t = ms.iterator();
		t.next();
		t.remove();
		assertObject(ms.enumerator()).json().is("['2','3','4']");

		t = ms.iterator();
		t.next();
		t.remove();
		assertObject(ms.enumerator()).json().is("['3','4']");

		t = ms.iterator();
		t.next();
		t.remove();
		assertObject(ms.enumerator()).json().is("['4']");

		t = ms.iterator();
		t.next();
		t.remove();
		assertObject(ms.enumerator()).json().is("[]");
		assertEquals(0, ms.size());

		ms = new MultiSet<>();
		assertObject(ms).json().is("[]");
		assertEquals(0, ms.size());

		try { ms = new MultiSet<>((Collection<String>)null); fail(); } catch (IllegalArgumentException e) {}
		try { new MultiSet<String>().iterator().next(); fail(); } catch (NoSuchElementException e) {}
		try { new MultiSet<String>().iterator().remove(); fail(); } catch (NoSuchElementException e) {}

	}
}
