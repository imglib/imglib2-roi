package net.imglib2.roi.geom;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class GeomPrimitivesLineTest
{

	@Test
	public void testVoidLine()
	{
		final Point P1 = new Point( new long[] { 12, 37, 6 } );
		final Iterable< Localizable > line = GeomPrimitives.line( P1, P1 );
		int count = 0;
		for ( final Localizable p : line )
		{
			count++;
			for ( int d = 0; d < P1.numDimensions(); d++ )
				assertEquals( "Unexpected position.", P1.getLongPosition( d ), p.getLongPosition( d ) );
		}
		assertEquals( "Should have iterated over a single point.", 1, count );
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
		final long nsteps = 35;

		final int targetIntensity = 1;

		final ImgFactory< UnsignedByteType > imgFactory = new ArrayImgFactory<>( new UnsignedByteType() );
		final Img< UnsignedByteType > image = imgFactory.create( 50, 50, 50 );

		long count = 0;
		final Iterable< Localizable > line = GeomPrimitives.line( P1, P2 );
		final Cursor< UnsignedByteType > cursorLine = GeomPrimitives.cursor( image, line );
		while ( cursorLine.hasNext() )
		{
			cursorLine.next().set( targetIntensity );
			count++;
		}

		// Test if we had the same number of points
		assertEquals( nsteps, count );

		// Test if all the target points are traversed
		final RandomAccess< UnsignedByteType > ra = image.randomAccess();
		int totalIntensity = 0;
		int val;
		for ( int i = 0; i < Z.length; i++ )
		{
			ra.setPosition( X[ i ], 0 );
			ra.setPosition( Y[ i ], 1 );
			ra.setPosition( Z[ i ], 2 );
			val = ra.get().get();
			assertEquals( targetIntensity, val );
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
	}
}
