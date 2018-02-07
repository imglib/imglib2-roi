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
package net.imglib2.roi.geom;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import net.imglib2.RealPoint;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.geom.real.DefaultWritableLine;
import net.imglib2.roi.geom.real.DefaultWritablePointMask;
import net.imglib2.roi.geom.real.Line;
import net.imglib2.roi.geom.real.PointMask;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link PointMask}.
 *
 * @author Alison Walter
 */
public class WritablePointMaskTest
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testDoubleArrayConstructor()
	{
		final PointMask pt = new DefaultWritablePointMask( new double[] { 10.25, -3, 6, 0.01 } );

		assertTrue( pt.test( new RealPoint( new double[] { 10.25, -3, 6, 0.01 } ) ) );
		assertFalse( pt.test( new RealPoint( new double[] { 10.15, -3, 6, 0.02 } ) ) );

		final double[] pos = new double[ pt.numDimensions() ];
		pt.localize( pos );
		assertArrayEquals( pos, new double[] { 10.25, -3, 6, 0.01 }, 0 );

		assertEquals( pt.boundaryType(), BoundaryType.CLOSED );
	}

	@Test
	public void testRealLocalizableConstructor()
	{
		final PointMask pt = new DefaultWritablePointMask( new RealPoint( new double[] { -12.125, 6, 0 } ) );

		assertTrue( pt.test( new RealPoint( new double[] { -12.125, 6, 0 } ) ) );
		assertFalse( pt.test( new RealPoint( new double[] { -12.125, 6.001, 0 } ) ) );

		final double[] pos = new double[ pt.numDimensions() ];
		pt.localize( pos );
		assertArrayEquals( pos, new double[] { -12.125, 6, 0 }, 0 );

		assertEquals( pt.boundaryType(), BoundaryType.CLOSED );
	}

	@Test
	public void testSetLocation()
	{
		final DefaultWritablePointMask pt = new DefaultWritablePointMask( new double[] { 0.5, -7.125 } );

		assertTrue( pt.test( new RealPoint( new double[] { 0.5, -7.125 } ) ) );
		assertFalse( pt.test( new RealPoint( new double[] { 12, 64 } ) ) );

		pt.setPosition( new double[] { 12, 64 } );

		assertFalse( pt.test( new RealPoint( new double[] { 0.5, -7.125 } ) ) );
		assertTrue( pt.test( new RealPoint( new double[] { 12, 64 } ) ) );
	}

	@Test
	public void testSetLocationTooLong()
	{
		final DefaultWritablePointMask pt = new DefaultWritablePointMask( new double[] { 0.5, -7.125 } );

		assertTrue( pt.test( new RealPoint( new double[] { 0.5, -7.125 } ) ) );
		assertFalse( pt.test( new RealPoint( new double[] { 12, 64 } ) ) );

		pt.setPosition( new double[] { 12, 64, 11 } );

		final double[] pos = new double[ pt.numDimensions() ];
		pt.localize( pos );
		assertArrayEquals( pos, new double[] { 12, 64 }, 0 );
		assertFalse( pt.test( new RealPoint( new double[] { 0.5, -7.125 } ) ) );
		assertTrue( pt.test( new RealPoint( new double[] { 12, 64 } ) ) );
	}

	@Test
	public void testSetLocationTooShort()
	{
		final DefaultWritablePointMask pt = new DefaultWritablePointMask( new double[] { 0.5, -7.125 } );

		assertTrue( pt.test( new RealPoint( new double[] { 0.5, -7.125 } ) ) );
		assertFalse( pt.test( new RealPoint( new double[] { -3, 9 } ) ) );

		exception.expect( IndexOutOfBoundsException.class );
		pt.setPosition( new double[] { -3 } );
	}

	@Test
	public void testBounds()
	{
		double[] loc = new double[] { 0.5, -7.125 };
		final DefaultWritablePointMask pt = new DefaultWritablePointMask( loc );
		final double[] ptMin = new double[ 2 ];
		final double[] ptMax = new double[ 2 ];
		pt.realMin( ptMin );
		pt.realMax( ptMax );

		assertArrayEquals( loc, ptMin, 0 );
		assertArrayEquals( loc, ptMax, 0 );

		// Mutate point
		pt.setPosition( new double[] { 0.25, 83 } );
		loc = new double[] { 0.25, 83 };
		pt.realMin( ptMin );
		pt.realMax( ptMax );
		assertArrayEquals( loc, ptMin, 0 );
		assertArrayEquals( loc, ptMax, 0 );
	}

	@Test
	public void testEquals()
	{
		final PointMask pm = new DefaultWritablePointMask( new double[] { 1.5, -12.125 } );
		final DefaultWritablePointMask pm2 = new DefaultWritablePointMask( new double[] { 1.5, -12.125 } );
		final PointMask pm3 = new DefaultWritablePointMask( new double[] { 1.5, -12.25, 82 } );
		final Line l = new DefaultWritableLine( new double[] { 1.25, -12.5 }, new double[] { 1.5, -12.125 }, false );

		assertTrue( pm.equals( pm2 ) );

		pm2.move( 0.5, 1 );
		assertFalse( pm.equals( pm2 ) );
		assertFalse( pm.equals( pm3 ) );
		assertFalse( pm.equals( l ) );
	}

	@Test
	public void testHashCode()
	{
		final PointMask pm = new DefaultWritablePointMask( new double[] { 1.5, -12.125 } );
		final DefaultWritablePointMask pm2 = new DefaultWritablePointMask( new double[] { 1.5, -12.125 } );
		final PointMask pm3 = new DefaultWritablePointMask( new double[] { 1.5, -12.25, 82 } );
		final Line l = new DefaultWritableLine( new double[] { 1.25, -12.5 }, new double[] { 1.5, -12.125 }, false );

		assertEquals( pm.hashCode(), pm2.hashCode() );

		pm2.move( 0.5, 1 );
		assertNotEquals( pm.hashCode(), pm2.hashCode() );
		assertNotEquals( pm.hashCode(), pm3.hashCode() );
		assertNotEquals( pm.hashCode(), l.hashCode() );
	}
}
