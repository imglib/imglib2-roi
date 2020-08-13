package net.imglib2.roi.labeling;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
		SortedInts list = SortedInts.create( new int[] { 3, 5, 4 } );
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
		SortedInts a = SortedInts.wrapSortedValues( 1, 4, 7 );
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
