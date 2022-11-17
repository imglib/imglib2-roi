/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.roi.geom.integer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.roi.Regions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Util;

import org.junit.Test;

public class IterableLineTest
{

	@Test
	public void testVerticalLine()
	{
		final Point P1 = new Point( new long[] { 0, 0 } );
		final Point P2 = new Point( new long[] { 10, 0 } );
		final IterableLine line = new IterableLine( P1, P2 );

		final Img< UnsignedByteType > img = ArrayImgs.unsignedBytes( 100, 100 );
		final IterableInterval< UnsignedByteType > sample = Regions.sample( line, img );
		for ( final UnsignedByteType p : sample )
			p.inc();

		final RandomAccess< UnsignedByteType > ra = img.randomAccess();
		for ( int x = 0; x < P2.getIntPosition( 1 ); x++ )
			assertEquals( "Unexpected pixel value at " + Util.printCoordinates( ra ), 1, ra.get().get() );

		int sum = 0;
		for ( final UnsignedByteType p : img )
			sum += p.get();

		assertEquals( "Iterated over unexpected pixels.", line.size(), sum );
	}

	@Test
	public void testHorizontalLine()
	{
		final Point P1 = new Point( new long[] { 0, 0 } );
		final Point P2 = new Point( new long[] { 0, 10 } );
		final IterableLine line = new IterableLine( P1, P2 );

		final Img< UnsignedByteType > img = ArrayImgs.unsignedBytes( 100, 100 );
		final IterableInterval< UnsignedByteType > sample = Regions.sample( line, img );
		for ( final UnsignedByteType p : sample )
			p.inc();

		final RandomAccess< UnsignedByteType > ra = img.randomAccess();
		for ( int y = 0; y < P2.getIntPosition( 0 ); y++ )
			assertEquals( "Unexpected pixel value at " + Util.printCoordinates( ra ), 1, ra.get().get() );

		int sum = 0;
		for ( final UnsignedByteType p : img )
			sum += p.get();

		assertEquals( "Iterated over unexpected pixels.", line.size(), sum );
	}

	@Test
	public void testEqual()
	{
		// Different concrete class for Localizable but same location.
		final Point s1 = new Point( new long[] { 12, 37, 6 } );
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 50, 50, 50 );
		final RandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new int[] { 12, 37, 6 } );

		final Point e1 = new Point( new long[] { 12, 37, 60 } );
		final Point e2 = new Point( new long[] { 12, 37, 60 } );

		final IterableLine line1 = new IterableLine( s1, e1 );
		final IterableLine line2 = new IterableLine( ra, e2 );
		assertTrue( "The two lines should be equal.", line1.equals( line2 ) );
		assertTrue( "The two lines should be equal.", line2.equals( line1 ) );
		assertTrue( "The line should be equal to itself.", line1.equals( line1 ) );
		assertTrue( "The line should be equal to itself.", line2.equals( line2 ) );

		// Move point. The line instance should not bother
		ra.fwd( 0 );
		assertTrue( "The two lines should still be equal.", line1.equals( line2 ) );
		// But after instantiation with this point, it should.
		assertFalse( "The two lines should not be equal.", line1.equals( new IterableLine( ra, e2 ) ) );

		// Permute order. It matters.
		final IterableLine line1reverse = new IterableLine( e1, s1 );
		assertFalse( "The two lines should not be equal.", line1.equals( line1reverse ) );

		// What about iteration order?
		assertTrue( "The two iteration order should be equal.", line1.iterationOrder().equals( line2.iterationOrder() ) );
		assertFalse( "The two iteration order should be equal.", line1.iterationOrder().equals( line1reverse.iterationOrder() ) );
	}

	@Test
	public void testVoidLine()
	{
		final Point P1 = new Point( new long[] { 12, 37, 6 } );
		final IterableLine line = new IterableLine( P1, P1 );
		int count = 0;
		final Cursor< Void > cursor = line.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			count++;
			for ( int d = 0; d < P1.numDimensions(); d++ )
				assertEquals( "Unexpected position.", P1.getLongPosition( d ), cursor.getLongPosition( d ) );
		}
		assertEquals( "Should have iterated over a single point.", 1, count );
		assertEquals( "Size should be 1.", 1, line.size() );
	}

	@Test
	public void test3DLine()
	{

		// A 3D line between these 2 points:
		final Point P1 = new Point( new long[] { 12, 37, 6 } );
		final Point P2 = new Point( new long[] { 46, 3, 35 } );

		// must iterate through ALL these points exactly, in this order:
		final long[] X = new long[] {
				12, 13, 14, 15, 16, 17, 18, 19, 20,
				21, 22, 23, 24, 25, 26, 27, 28, 29,
				30, 31, 32, 33, 34, 35, 36, 37,
				38, 39, 40, 41, 42, 43, 44, 45, 46 };

		final long[] Y = new long[] {
				37, 36, 35, 34, 33, 32, 31, 30, 29,
				28, 27, 26, 25, 24, 23, 22, 21, 20,
				19, 18, 17, 16, 15, 14, 13, 12, 11,
				10, 9, 8, 7, 6, 5, 4, 3 };

		final long[] Z = new long[] {
				6, 7, 8, 9, 9, 10, 11, 12, 13,
				14, 15, 15, 16, 17, 18, 19, 20, 20,
				21, 22, 23, 24, 25, 26, 26, 27, 28,
				29, 30, 31, 32, 32, 33, 34, 35 };

		// and have this much steps.
		final long nsteps = X.length;

		final ImgFactory< UnsignedByteType > imgFactory = new ArrayImgFactory<>( new UnsignedByteType() );
		final Img< UnsignedByteType > image = imgFactory.create( 50, 50, 50 );

		long count = 0;
		final IterableLine line = new IterableLine( P1, P2 );
		final Cursor< UnsignedByteType > cursorLine = Regions.sample( line, image ).cursor();
		while ( cursorLine.hasNext() )
		{
			cursorLine.next().inc();
			count++;
		}

		// Test if we had the same number of points
		assertEquals( nsteps, count );

		// Test if all the target points are traversed
		final RandomAccess< UnsignedByteType > ra = image.randomAccess();
		int totalIntensity = 0;
		for ( int i = 0; i < Z.length; i++ )
		{
			ra.setPosition( X[ i ], 0 );
			ra.setPosition( Y[ i ], 1 );
			ra.setPosition( Z[ i ], 2 );
			final int val = ra.get().get();
			assertEquals( "Point not iterated: " + Util.printCoordinates( ra ), 1, val );
			totalIntensity += val;
		}

		// Test if no other point is traversed
		int imageSum = 0;
		final Cursor< UnsignedByteType > cursor = image.cursor();
		while ( cursor.hasNext() )
		{
			imageSum += cursor.next().get();
		}

		assertEquals( totalIntensity, imageSum );
		assertEquals( "Unexpected size.", 35, line.size() );
	}

	/*
	 * Test disabled because it takes very long. This one tests whether we do
	 * not loose accuracy when we travel by amounts that larger than e.g. the
	 * max value of an int.
	 */
//	@Test
	public void testVeryFar()
	{
		final Point p1 = new Point( 0l, 0L );
		final Point p2 = new Point( 21474836480L, 1L );
		final IterableLine line = new IterableLine( p1, p2 );
		final Cursor< Void > cursor = line.localizingCursor();
		while ( cursor.hasNext() )
			cursor.fwd();

		for ( int d = 0; d < p2.numDimensions(); d++ )
			assertEquals( "Did not reach the last point.", p2.getLongPosition( d ), cursor.getLongPosition( d ) );
	}
}
