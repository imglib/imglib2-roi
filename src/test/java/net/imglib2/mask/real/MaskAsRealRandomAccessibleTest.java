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
package net.imglib2.mask.real;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.OpenBox;
import net.imglib2.roi.mask.real.MaskAsRealRandomAccessible;
import net.imglib2.roi.mask.real.MaskAsRealRandomAccessibleRealInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link MaskAsRealRandomAccessible}.
 *
 * @author Alison Walter
 */
public class MaskAsRealRandomAccessibleTest
{
	private static Box b;

	private static RealRandomAccessible< BoolType > mrra;

	private static RealRandomAccess< BoolType > rra;

	private static RealRandomAccessibleRealInterval< BitType > mrrari;

	private static RealRandomAccess< BitType > rraInterval;

	@BeforeClass
	public static void setup()
	{
		b = new OpenBox( new double[] { 0, 0 }, new double[] { 6, 6 } );
		mrra = new MaskAsRealRandomAccessible<>( b, new BoolType() );
		rra = mrra.realRandomAccess();

		mrrari = new MaskAsRealRandomAccessibleRealInterval<>( b, new BitType() );
		rraInterval = mrrari.realRandomAccess();
	}

	@Test
	public void testRealRandomAccess()
	{
		final long seed = -22;
		final Random rand = new Random( seed );

		for ( int i = 0; i < 200; i++ )
		{
			final double x = rand.nextDouble();
			final double y = rand.nextDouble();

			rra.setPosition( new double[] { x, y } );
			assertEquals( b.test( new RealPoint( new double[] { x, y } ) ), rra.get().get() );
		}
	}

	@Test
	public void testNumDimensions()
	{
		assertEquals( b.numDimensions(), rra.numDimensions() );
	}

	@Test
	public void testsource()
	{
		assertTrue( Box.class.isInstance( ( ( MaskAsRealRandomAccessible< BoolType > ) mrra ).source() ) );
	}

	@Test
	public void testMaskAsRealRandomAccessibleRealIntervalRealRandomAccess()
	{
		final long seed = 39567;
		final Random rand = new Random( seed );

		for ( int i = 0; i < 200; i++ )
		{
			final double x = rand.nextDouble();
			final double y = rand.nextDouble();

			rraInterval.setPosition( new double[] { x, y } );
			assertEquals( b.test( new RealPoint( new double[] { x, y } ) ), rraInterval.get().get() );
		}
	}

	@Test
	public void testMaskAsRealRandomAccessibleRealIntervalNumDimensions()
	{
		assertEquals( b.numDimensions(), rraInterval.numDimensions() );
	}

	@Test
	public void testMaskAsRealRandomAccessibleRealIntervalSource()
	{
		assertTrue( Box.class.isInstance( ( ( MaskAsRealRandomAccessibleRealInterval< BitType > ) mrrari ).source() ) );
	}

	@Test
	public void testMaskAsRealRandomAccessibleRealIntervalBounds()
	{
		assertEquals( b.realMax( 0 ), mrrari.realMax( 0 ), 0 );
		assertEquals( b.realMax( 1 ), mrrari.realMax( 1 ), 0 );

		assertEquals( b.realMin( 0 ), mrrari.realMin( 0 ), 0 );
		assertEquals( b.realMin( 1 ), mrrari.realMin( 1 ), 0 );
	}
}
