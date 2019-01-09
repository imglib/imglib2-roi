package net.imglib2.roi.sparse;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.roi.IterableRegion;
import net.imglib2.type.logic.NativeBoolType;
import net.imglib2.util.Localizables;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SparseBitmaskTest
{
	@Test
	public void testCreate() {
		new SparseBitmask( 3 );
	}

	@Test
	public void testRandomAccess() {
		SparseBitmask bitmask = new SparseBitmask( 3 );
		RandomAccess< NativeBoolType > ra = bitmask.randomAccess();
		ra.get().set( true );
		assertTrue( ra.get().get() );
		ra.get().set( false );
		assertFalse( ra.get().get() );
	}


	@Test
	public void testRegion() {
		SparseBitmask bitmask = new SparseBitmask( 3 );
		RandomAccess< NativeBoolType > ra = bitmask.randomAccess();
		final long[] position = { 1, 2, 3 };
		ra.setPosition( position );
		ra.get().set( true );
		final IterableRegion< NativeBoolType > region = bitmask.region();
		Cursor< Void > cursor = region.cursor();
		assertEquals(1, region.size());
		assertTrue( cursor.hasNext() );
		cursor.next();
		assertArrayEquals( position, Localizables.asLongArray( cursor ) );
		assertFalse( cursor.hasNext() );
	}


}
