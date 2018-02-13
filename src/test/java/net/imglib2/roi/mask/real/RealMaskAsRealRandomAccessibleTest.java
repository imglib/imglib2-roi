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
package net.imglib2.roi.mask.real;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.OpenWritableBox;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link RealMaskAsRealRandomAccessible}.
 *
 * @author Alison Walter
 */
public class RealMaskAsRealRandomAccessibleTest
{
	private static Box b;

	private static RealRandomAccessible< BoolType > mrra;

	private static RealRandomAccess< BoolType > rra;

	private static RealRandomAccessibleRealInterval< BitType > mrrari;

	private static RealRandomAccess< BitType > rraInterval;

	@BeforeClass
	public static void setup()
	{
		b = new OpenWritableBox( new double[] { 0, 0 }, new double[] { 6, 6 } );
		mrra = new RealMaskAsRealRandomAccessible<>( b, new BoolType() );
		rra = mrra.realRandomAccess();

		mrrari = new RealMaskRealIntervalAsRealRandomAccessibleRealInterval<>( b, new BitType() );
		rraInterval = mrrari.realRandomAccess();
	}

	@Test
	public void testRealMaskAsRealRandomAccessibleRealRandomAccess()
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
	public void testRealMaskAsRealRandomAccessibleNumDimensions()
	{
		assertEquals( b.numDimensions(), rra.numDimensions() );
	}

	@Test
	public void testRealMaskAsRealRandomAccessibleSource()
	{
		assertTrue( Box.class.isInstance( ( ( RealMaskAsRealRandomAccessible< BoolType > ) mrra ).getSource() ) );
	}

	@Test
	public void testRealMaskRealIntervalAsRealRandomAccessibleRealIntervalRealRandomAccess()
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
	public void testRealMaskRealIntervalAsRealRandomAccessibleRealIntervalNumDimensions()
	{
		assertEquals( b.numDimensions(), rraInterval.numDimensions() );
	}

	@Test
	public void testRealMaskRealIntervalAsRealRandomAccessibleRealIntervalSource()
	{
		assertTrue( Box.class.isInstance( ( ( RealMaskRealIntervalAsRealRandomAccessibleRealInterval< BitType > ) mrrari ).getSource() ) );
	}

	@Test
	public void testRealMaskRealIntervalAsRealRandomAccessibleRealIntervalBounds()
	{
		assertEquals( b.realMax( 0 ), mrrari.realMax( 0 ), 0 );
		assertEquals( b.realMax( 1 ), mrrari.realMax( 1 ), 0 );

		assertEquals( b.realMin( 0 ), mrrari.realMin( 0 ), 0 );
		assertEquals( b.realMin( 1 ), mrrari.realMin( 1 ), 0 );
	}
}
