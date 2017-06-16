/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Asbias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUAsRS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED As, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUAsRS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED As, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR AsRT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.mask.integer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.integer.MaskAsRandomAccessible;
import net.imglib2.roi.mask.integer.MaskAsRandomAccessibleInterval;
import net.imglib2.roi.mask.integer.MaskInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link MaskAsRandomAccessible}.
 *
 * @author Alison Walter
 */
public class MaskAsRandomAccessibleTest
{
	private static Mask< Localizable > m;

	private static RandomAccessible< BoolType > ra;

	private static RandomAccess< BoolType > access;

	private static MaskInterval mi;

	private static RandomAccessibleInterval< BitType > rai;

	private static RandomAccess< BitType > accessInterval;

	@BeforeClass
	public static void setup()
	{
		m = new TestMask();
		ra = new MaskAsRandomAccessible<>( m, new BoolType() );
		access = ra.randomAccess();

		mi = new TestMaskInterval();
		rai = new MaskAsRandomAccessibleInterval<>( mi, new BitType() );
		accessInterval = rai.randomAccess();
	}

	@Test
	public void testRandomAccess()
	{
		final long seed = 16;
		final Random rand = new Random( seed );

		for ( int i = 0; i < 200; i++ )
		{
			final long x = rand.nextLong();
			final long y = rand.nextLong();

			access.setPosition( new long[] { x, y } );
			assertEquals( m.test( new Point( new long[] { x, y } ) ), access.get().get() );
		}
	}

	@Test
	public void testNumDimensions()
	{
		assertEquals( m.numDimensions(), access.numDimensions() );
	}

	@Test
	public void testsource()
	{
		assertTrue( Mask.class.isInstance( ( ( MaskAsRandomAccessible< BoolType > ) ra ).source() ) );
	}

	@Test
	public void testMaskAsRandomAccessibleIntervalRandomAccess()
	{
		final long seed = -12;
		final Random rand = new Random( seed );

		for ( int i = 0; i < 200; i++ )
		{
			final long x = rand.nextLong();
			final long y = rand.nextLong();

			accessInterval.setPosition( new long[] { x, y } );
			assertEquals( mi.test( new Point( new long[] { x, y } ) ), accessInterval.get().get() );
		}
	}

	@Test
	public void testMaskAsRandomAccessibleIntervalBounds()
	{
		assertEquals( mi.dimension( 0 ), rai.dimension( 0 ) );
		assertEquals( mi.dimension( 1 ), rai.dimension( 1 ) );

		assertEquals( mi.max( 0 ), rai.max( 0 ) );
		assertEquals( mi.max( 1 ), rai.max( 1 ) );

		assertEquals( mi.min( 0 ), rai.min( 0 ) );
		assertEquals( mi.min( 1 ), rai.min( 1 ) );
	}

	@Test
	public void testMaskAsRandomAccessibleIntervalNumDimensions()
	{
		assertEquals( mi.numDimensions(), accessInterval.numDimensions() );
	}

	@Test
	public void testMaskAsRandomAccessibleIntervalSource()
	{
		assertTrue( MaskInterval.class.isInstance( ( ( MaskAsRandomAccessibleInterval< BitType > ) rai ).source() ) );
	}

	// -- Test classes --

	private static class TestMask implements Mask< Localizable >
	{

		public TestMask()
		{}

		@Override
		public int numDimensions()
		{
			return 2;
		}

		@Override
		public boolean test( final Localizable l )
		{
			return ( l.getDoublePosition( 0 ) * l.getDoublePosition( 1 ) ) % 2 == 0 ? true : false;
		}

	}

	private static class TestMaskInterval implements MaskInterval
	{

		public TestMaskInterval()
		{}

		@Override
		public int numDimensions()
		{
			return 2;
		}

		@Override
		public boolean test( final Localizable l )
		{
			if ( Intervals.contains( this, l ) )
				return ( l.getDoublePosition( 0 ) * l.getDoublePosition( 1 ) ) % 2 == 0 ? true : false;
			return false;
		}

		@Override
		public long min( final int d )
		{
			return 0;
		}

		@Override
		public void min( final long[] min )
		{
			min[ 0 ] = 0;
			min[ 1 ] = 0;
		}

		@Override
		public void min( final Positionable min )
		{
			min.setPosition( 0, 0 );
			min.setPosition( 0, 1 );
		}

		@Override
		public long max( final int d )
		{
			return 10;
		}

		@Override
		public void max( final long[] max )
		{
			max[ 0 ] = 10;
			max[ 1 ] = 10;
		}

		@Override
		public void max( final Positionable max )
		{
			max.setPosition( 10, 0 );
			max.setPosition( 10, 1 );
		}

		@Override
		public double realMin( final int d )
		{
			return min( d );
		}

		@Override
		public void realMin( final double[] min )
		{
			min[ 0 ] = 0;
			min[ 1 ] = 0;
		}

		@Override
		public void realMin( final RealPositionable min )
		{
			min.setPosition( 0, 0 );
			min.setPosition( 0, 1 );
		}

		@Override
		public double realMax( final int d )
		{
			return max( d );
		}

		@Override
		public void realMax( final double[] max )
		{
			max[ 0 ] = 10;
			max[ 1 ] = 10;
		}

		@Override
		public void realMax( final RealPositionable max )
		{
			max.setPosition( 10, 0 );
			max.setPosition( 10, 1 );
		}

		@Override
		public void dimensions( final long[] dimensions )
		{
			dimensions[ 0 ] = 10;
			dimensions[ 1 ] = 10;
		}

		@Override
		public long dimension( final int d )
		{
			return 10;
		}

	}
}
