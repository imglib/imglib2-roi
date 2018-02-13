/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2017 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.roi.mask.integer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.Mask;
import net.imglib2.roi.MaskInterval;
import net.imglib2.roi.mask.integer.DefaultMask;
import net.imglib2.roi.mask.integer.DefaultMaskInterval;
import net.imglib2.roi.mask.integer.MaskAsRandomAccessible;
import net.imglib2.roi.mask.integer.MaskIntervalAsRandomAccessibleInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link MaskAsRandomAccessible}.
 *
 * @author Alison Walter
 */
public class MaskAsRandomAccessibleTest
{
	private static Mask m;

	private static RandomAccessible< BoolType > ra;

	private static RandomAccess< BoolType > access;

	private static MaskInterval mi;

	private static RandomAccessibleInterval< BitType > rai;

	private static RandomAccess< BitType > accessInterval;

	@BeforeClass
	public static void setup()
	{
		m = new DefaultMask( 2, BoundaryType.UNSPECIFIED, l -> ( l.getDoublePosition( 0 ) * l.getDoublePosition( 1 ) ) % 2 == 0 ? true : false, KnownConstant.UNKNOWN );
		ra = new MaskAsRandomAccessible<>( m, new BoolType() );
		access = ra.randomAccess();

		mi = new DefaultMaskInterval( new FinalInterval( new long[] { 0, 0 }, new long[] { 10, 10 } ), BoundaryType.UNSPECIFIED, l -> ( l.getDoublePosition( 0 ) * l.getDoublePosition( 1 ) ) % 2 == 0 ? true : false, KnownConstant.UNKNOWN );
		rai = new MaskIntervalAsRandomAccessibleInterval<>( mi, new BitType() );
		accessInterval = rai.randomAccess();
	}

	@Test
	public void testMaskAsRandomAccessibleRandomAccess()
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
	public void testMaskAsRandomAccessibleNumDimensions()
	{
		assertEquals( m.numDimensions(), access.numDimensions() );
	}

	@Test
	public void testMaskAsRandomAccessibleSource()
	{
		assertTrue( Mask.class.isInstance( ( ( MaskAsRandomAccessible< BoolType > ) ra ).getSource() ) );
	}

	@Test
	public void testMaskIntervalAsRandomAccessibleIntervalRandomAccess()
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
	public void testMaskIntervalAsRandomAccessibleIntervalBounds()
	{
		assertEquals( mi.dimension( 0 ), rai.dimension( 0 ) );
		assertEquals( mi.dimension( 1 ), rai.dimension( 1 ) );

		assertEquals( mi.max( 0 ), rai.max( 0 ) );
		assertEquals( mi.max( 1 ), rai.max( 1 ) );

		assertEquals( mi.min( 0 ), rai.min( 0 ) );
		assertEquals( mi.min( 1 ), rai.min( 1 ) );
	}

	@Test
	public void testMaskIntervalAsRandomAccessibleIntervalNumDimensions()
	{
		assertEquals( mi.numDimensions(), accessInterval.numDimensions() );
	}

	@Test
	public void testMaskIntervalAsRandomAccessibleIntervalSource()
	{
		assertTrue( MaskInterval.class.isInstance( ( ( MaskIntervalAsRandomAccessibleInterval< BitType > ) rai ).getSource() ) );
	}
}
