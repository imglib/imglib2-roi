/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.roi.labeling;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link SortedInts}.
 */
public class SortedIntsTest
{
	private final SortedInts example = SortedInts.wrapSortedValues( 1, 4, 7 );

	@Test
	public void testSize()
	{
		assertEquals( 3, example.size() );
	}

	@Test
	public void testIsEmpty()
	{
		assertFalse( example.isEmpty() );
		assertTrue( SortedInts.emptyList().isEmpty() );
	}

	@Test
	public void testGet()
	{
		assertEquals( 4, example.get( 1 ) );
	}

	@Test
	public void testContains()
	{
		assertTrue( example.contains( 1 ) );
		assertTrue( example.contains( 4 ) );
		assertTrue( example.contains( 7 ) );
		assertFalse( example.contains( 0 ) );
		assertFalse( example.contains( 8 ) );
	}

	@Test
	public void testCreate()
	{
		final SortedInts list = SortedInts.create( 3, 5, 4 );
		assertArrayEquals( new int[] { 3, 4, 5 }, list.toArray() );
	}

	@Test
	public void testEquals()
	{
		final SortedInts same = SortedInts.wrapSortedValues( 1, 4, 7 );
		final SortedInts different = SortedInts.wrapSortedValues( 1, 4, 8 );
		assertEquals( example, example );
		assertEquals( example, same );
		assertNotEquals( example, different );
	}

	@Test
	public void testHashCode()
	{
		final SortedInts same = SortedInts.wrapSortedValues( 1, 4, 7 );
		final SortedInts different = SortedInts.wrapSortedValues( 1, 4, 8 );
		assertEquals( example.hashCode(), same.hashCode() );
		assertNotEquals( example.hashCode(), different.hashCode() );
	}

	@Test
	public void testToString()
	{
		final SortedInts a = SortedInts.wrapSortedValues( 1, 4, 7 );
		assertEquals( "[1, 4, 7]", a.toString() );
	}

	@Test
	public void testAdd()
	{
		assertEquals( SortedInts.wrapSortedValues( 0, 1, 4, 7 ), example.copyAndAdd( 0 ) );
		assertEquals( SortedInts.wrapSortedValues( 1, 2, 4, 7 ), example.copyAndAdd( 2 ) );
		assertEquals( SortedInts.wrapSortedValues( 1, 4, 7, 8 ), example.copyAndAdd( 8 ) );
		assertSame( example, example.copyAndAdd( 1 ) );
		assertEquals( SortedInts.wrapSortedValues( 1, 4, 7 ), example );
	}

	@Test
	public void testRemove()
	{
		assertEquals( SortedInts.wrapSortedValues( 4, 7 ), example.copyAndRemove( 1 ) );
		assertEquals( SortedInts.wrapSortedValues( 1, 7 ), example.copyAndRemove( 4 ) );
		assertEquals( SortedInts.wrapSortedValues( 1, 4 ), example.copyAndRemove( 7 ) );
		assertSame( example, example.copyAndRemove( 5 ) );
	}
}
