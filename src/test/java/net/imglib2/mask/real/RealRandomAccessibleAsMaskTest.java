/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.mask.real;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.geom.real.Ellipsoid;
import net.imglib2.roi.geom.real.OpenEllipsoid;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.real.MaskAsRealRandomAccessible;
import net.imglib2.roi.mask.real.MaskAsRealRandomAccessibleRealInterval;
import net.imglib2.roi.mask.real.MaskRealInterval;
import net.imglib2.roi.mask.real.RealRandomAccessibleAsMask;
import net.imglib2.roi.mask.real.RealRandomAccessibleRealIntervalAsMask;
import net.imglib2.type.logic.BoolType;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link RealRandomAccessibleAsMask}.
 *
 * @author Alison Walter
 */
public class RealRandomAccessibleAsMaskTest
{
	private static RealRandomAccessible< BoolType > rra;

	private static RealRandomAccess< BoolType > access;

	private static Mask< RealLocalizable > m;

	private static RealRandomAccessibleRealInterval< BoolType > rrari;

	private static RealRandomAccess< BoolType > accessInterval;

	private static MaskRealInterval mri;

	@BeforeClass
	public static void setup()
	{
		final Ellipsoid e = new OpenEllipsoid( new double[] { 10, 10 }, new double[] { 4, 7 } );
		rra = new MaskAsRealRandomAccessible<>( e, new BoolType() );
		access = rra.realRandomAccess();
		m = new RealRandomAccessibleAsMask<>( rra );

		rrari = new MaskAsRealRandomAccessibleRealInterval<>( e, new BoolType() );
		accessInterval = rrari.realRandomAccess();
		mri = new RealRandomAccessibleRealIntervalAsMask<>( rrari );
	}

	@Test
	public void testTest()
	{
		final long seed = -22;
		final Random rand = new Random( seed );

		for ( int i = 0; i < 200; i++ )
		{
			final double x = rand.nextDouble();
			final double y = rand.nextDouble();

			access.setPosition( new double[] { x, y } );
			assertEquals( access.get().get(), m.test( new RealPoint( new double[] { x, y } ) ) );
		}
	}

	@Test
	public void testNumDimensions()
	{
		assertEquals( access.numDimensions(), m.numDimensions() );
	}

	@Test
	public void testsource()
	{
		assertTrue( MaskAsRealRandomAccessible.class.isInstance( ( ( RealRandomAccessibleAsMask< BoolType > ) m ).source() ) );
	}

	@Test
	public void testRealRandomAccessibleRealIntervalAsMaskTest()
	{
		final long seed = 24;
		final Random rand = new Random( seed );

		for ( int i = 0; i < 200; i++ )
		{
			final double x = rand.nextDouble();
			final double y = rand.nextDouble();

			accessInterval.setPosition( new double[] { x, y } );
			assertEquals( accessInterval.get().get(), mri.test( new RealPoint( new double[] { x, y } ) ) );
		}
	}

	@Test
	public void testRealRandomAccessibleRealIntervalAsMaskNumDimensions()
	{
		assertEquals( accessInterval.numDimensions(), mri.numDimensions() );
	}

	@Test
	@SuppressWarnings( "unchecked" )
	public void testRealRandomAccessibleRealIntervalAsMaskSource()
	{
		assertTrue( RealRandomAccessibleRealInterval.class.isInstance( ( ( RealRandomAccessibleRealIntervalAsMask< BoolType > ) mri ).source() ) );
	}

	@Test
	public void testRealRandomAccessibleRealIntervalAsMaskBounds()
	{
		assertEquals( rrari.realMax( 0 ), mri.realMax( 0 ), 0 );
		assertEquals( rrari.realMax( 1 ), mri.realMax( 1 ), 0 );

		assertEquals( rrari.realMin( 0 ), mri.realMin( 0 ), 0 );
		assertEquals( rrari.realMin( 1 ), mri.realMin( 1 ), 0 );
	}
}
