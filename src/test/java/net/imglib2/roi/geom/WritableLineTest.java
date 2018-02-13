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
import net.imglib2.roi.geom.real.WritableLine;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class WritableLineTest
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testLine()
	{
		final Line l = new DefaultWritableLine( new double[] { 1, 3 }, new double[] { 6, 0 }, false );

		assertEquals( l.numDimensions(), 2 );

		// On line segment
		assertTrue( l.test( new RealPoint( new double[] { 4.5, 0.9 } ) ) );
		assertTrue( l.test( new RealPoint( new double[] { 2, 2.4 } ) ) );

		// On line, but not within interval
		assertFalse( l.test( new RealPoint( new double[] { 0, 3.6 } ) ) );
		assertFalse( l.test( new RealPoint( new double[] { 9, -1.8 } ) ) );

		// Off line
		assertFalse( l.test( new RealPoint( new double[] { 1, 1 } ) ) );
		assertFalse( l.test( new RealPoint( new double[] { 4.5, 1 } ) ) );

		// Check if endpoints on line
		assertTrue( l.test( new RealPoint( new double[] { 1, 3 } ) ) );
		assertTrue( l.test( new RealPoint( new double[] { 6, 0 } ) ) );

		// line characteristics
		assertEquals( l.endpointOne().getDoublePosition( 0 ), 1, 0 );
		assertEquals( l.endpointOne().getDoublePosition( 1 ), 3, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 0 ), 6, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 1 ), 0, 0 );
		assertTrue( l.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testHigherDimSpaceLine()
	{
		final Line l = new DefaultWritableLine( new double[] { 1, 1, 1, 1, 1 }, new double[] { 10, 10, 10, 10, 10 }, false );

		assertEquals( l.numDimensions(), 5 );

		// On line
		assertTrue( l.test( new RealPoint( new double[] { 6, 6, 6, 6, 6 } ) ) );

		// Off line
		assertFalse( l.test( new RealPoint( new double[] { 0, 0, 0, 0, 0 } ) ) );
		assertFalse( l.test( new RealPoint( new double[] { 2, 2, 2.0001, 2, 2 } ) ) );

		// line characteristics
		assertEquals( l.endpointOne().getDoublePosition( 0 ), 1, 0 );
		assertEquals( l.endpointOne().getDoublePosition( 1 ), 1, 0 );
		assertEquals( l.endpointOne().getDoublePosition( 2 ), 1, 0 );
		assertEquals( l.endpointOne().getDoublePosition( 3 ), 1, 0 );
		assertEquals( l.endpointOne().getDoublePosition( 4 ), 1, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 0 ), 10, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 1 ), 10, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 2 ), 10, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 3 ), 10, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 4 ), 10, 0 );
		assertTrue( l.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testHorizontalLine()
	{
		final Line l = new DefaultWritableLine( new double[] { 1, 1 }, new double[] { 12, 1 }, false );

		// On line
		assertTrue( l.test( new RealPoint( new double[] { 6.25, 1 } ) ) );

		// Off line
		assertFalse( l.test( new RealPoint( new double[] { 10, 1.01 } ) ) );
		assertFalse( l.test( new RealPoint( new double[] { 12.5, 1 } ) ) );

		// Endpoints
		assertTrue( l.test( new RealPoint( new double[] { 1, 1 } ) ) );
		assertTrue( l.test( new RealPoint( new double[] { 12, 1 } ) ) );

		// line characteristics
		assertEquals( l.endpointOne().getDoublePosition( 0 ), 1, 0 );
		assertEquals( l.endpointOne().getDoublePosition( 1 ), 1, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 0 ), 12, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 1 ), 1, 0 );
		assertTrue( l.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testVerticalLine()
	{
		final Line l = new DefaultWritableLine( new double[] { 1, 1 }, new double[] { 1, 17 }, false );

		// On line
		assertTrue( l.test( new RealPoint( new double[] { 1, 14.125 } ) ) );

		// Off line
		assertFalse( l.test( new RealPoint( new double[] { 1.0625, 4 } ) ) );
		assertFalse( l.test( new RealPoint( new double[] { 1, 0.99 } ) ) );

		// Endpoints
		assertTrue( l.test( new RealPoint( new double[] { 1, 1 } ) ) );
		assertTrue( l.test( new RealPoint( new double[] { 1, 17 } ) ) );

		// line characteristics
		assertEquals( l.endpointOne().getDoublePosition( 0 ), 1, 0 );
		assertEquals( l.endpointOne().getDoublePosition( 1 ), 1, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 0 ), 1, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 1 ), 17, 0 );
		assertTrue( l.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testMutatedLine()
	{
		final WritableLine l = new DefaultWritableLine( new double[] { 2, 3 }, new double[] { 4, 7 }, false );

		assertEquals( l.endpointOne().getDoublePosition( 0 ), 2, 0 );
		assertEquals( l.endpointOne().getDoublePosition( 1 ), 3, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 0 ), 4, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 1 ), 7, 0 );
		assertTrue( l.test( new RealPoint( new double[] { 3, 5 } ) ) );
		assertFalse( l.test( new RealPoint( new double[] { 1.5, 2 } ) ) );
		assertFalse( l.test( new RealPoint( new double[] { 4, 4 } ) ) );
		assertFalse( l.test( new RealPoint( new double[] { 3.5, 3.55 } ) ) );

		// change first endpoint
		l.endpointOne().setPosition( new double[] { 1, 1 } );

		assertEquals( l.endpointOne().getDoublePosition( 0 ), 1, 0 );
		assertEquals( l.endpointOne().getDoublePosition( 1 ), 1, 0 );
		assertTrue( l.test( new RealPoint( new double[] { 3, 5 } ) ) );
		assertTrue( l.test( new RealPoint( new double[] { 1.5, 2 } ) ) );
		assertFalse( l.test( new RealPoint( new double[] { 4, 4 } ) ) );
		assertFalse( l.test( new RealPoint( new double[] { 3.5, 3.55 } ) ) );

		// change second endpoint
		l.endpointTwo().setPosition( new double[] { 7, 7 } );

		assertEquals( l.endpointTwo().getDoublePosition( 0 ), 7, 0 );
		assertEquals( l.endpointTwo().getDoublePosition( 1 ), 7, 0 );
		assertFalse( l.test( new RealPoint( new double[] { 3, 5 } ) ) );
		assertFalse( l.test( new RealPoint( new double[] { 1.5, 2 } ) ) );
		assertTrue( l.test( new RealPoint( new double[] { 4, 4 } ) ) );
		assertFalse( l.test( new RealPoint( new double[] { 3.5, 3.55 } ) ) );

	}

	@Test
	public void testPointOneArraysLonger()
	{
		final Line l = new DefaultWritableLine( new double[] { 1, 1, 1, 1 }, new double[] { 10, 10 }, false );

		assertEquals( l.numDimensions(), 2 );

		final double[] e1 = new double[ l.endpointOne().numDimensions() ];
		l.endpointOne().localize( e1 );
		final double[] e2 = new double[ l.endpointTwo().numDimensions() ];
		l.endpointTwo().localize( e2 );

		assertArrayEquals( e1, new double[] { 1, 1 }, 0 );
		assertArrayEquals( e2, new double[] { 10, 10 }, 0 );
	}

	@Test
	public void testPointTwoArraysLonger()
	{
		final Line l = new DefaultWritableLine( new double[] { 1 }, new double[] { 101, 1, 2, 3 }, true );

		assertEquals( l.numDimensions(), 1 );

		final double[] e1 = new double[ l.endpointOne().numDimensions() ];
		l.endpointOne().localize( e1 );
		final double[] e2 = new double[ l.endpointTwo().numDimensions() ];
		l.endpointTwo().localize( e2 );

		assertArrayEquals( e1, new double[] { 1 }, 0 );
		assertArrayEquals( e2, new double[] { 101 }, 0 );
	}

	@Test
	public void testUnequalLengthRealLocalizables()
	{
		final Line l = new DefaultWritableLine( new RealPoint( new double[] { 1, 1, 1 } ), new RealPoint( new double[] { 20, 20 } ) );

		assertEquals( l.numDimensions(), 2 );

		final double[] e1 = new double[ l.endpointOne().numDimensions() ];
		l.endpointOne().localize( e1 );
		final double[] e2 = new double[ l.endpointTwo().numDimensions() ];
		l.endpointTwo().localize( e2 );

		assertArrayEquals( e1, new double[] { 1, 1 }, 0 );
		assertArrayEquals( e2, new double[] { 20, 20 }, 0 );
	}

	@Test
	public void testSetFirstEndPointTooShort()
	{
		final WritableLine l = new DefaultWritableLine( new double[] { 1, 10.125, -6, 8.5 }, new double[] { 101, 1, 2, 3 }, true );

		exception.expect( IndexOutOfBoundsException.class );
		l.endpointOne().setPosition( new double[] { 0.0625, -5, 0 } );
	}

	@Test
	public void testSetSecondEndPointTooLong()
	{
		final WritableLine l = new DefaultWritableLine( new double[] { 1, 10.125, -6, 8.5 }, new double[] { 101, 1, 2, 3 }, true );

		l.endpointTwo().setPosition( new double[] { 1.0625, -0.0325, 10.5, 12.25, 5 } );
		final double[] ptTwo = new double[ l.endpointTwo().numDimensions() ];
		l.endpointTwo().localize( ptTwo );
		assertEquals( ptTwo.length, 4 );
		assertArrayEquals( ptTwo, new double[] { 1.0625, -0.0325, 10.5, 12.25 }, 0 );
	}

	@Test
	public void testBounds()
	{
		final WritableLine l = new DefaultWritableLine( new double[] { 1, 3 }, new double[] { 6, 0 }, false );
		double[] min = new double[] { 1, 0 };
		double[] max = new double[] { 6, 3 };
		final double[] lMin = new double[ 2 ];
		final double[] lMax = new double[ 2 ];
		l.realMin( lMin );
		l.realMax( lMax );

		assertArrayEquals( min, lMin, 0 );
		assertArrayEquals( max, lMax, 0 );

		// Mutate line
		l.endpointOne().setPosition( new double[] { -1, -3.25 } );
		min = new double[] { -1, -3.25 };
		max = new double[] { 6, 0 };
		l.realMin( lMin );
		l.realMax( lMax );
		assertArrayEquals( min, lMin, 0 );
		assertArrayEquals( max, lMax, 0 );

		l.endpointTwo().setPosition( new double[] { 10, 1 } );
		max = new double[] { 10, 1 };
		l.realMin( lMin );
		l.realMax( lMax );
		assertArrayEquals( min, lMin, 0 );
		assertArrayEquals( max, lMax, 0 );
	}

	@Test
	public void testEquals()
	{
		final Line l = new DefaultWritableLine( new double[] { 1, 1, 1 }, new double[] { 9, 9, 9 }, false );
		final WritableLine l2 = new DefaultWritableLine( new double[] { 1, 1, 1 }, new double[] { 9, 9, 9 }, false );
		final Line l3 = new DefaultWritableLine( new double[] { 1, 1 }, new double[] { 9, 9 }, false );
		final Line l4 = new DefaultWritableLine( new double[] { 9, 9, 9 }, new double[] { 1, 1, 1 }, false );
		final PointMask p = new DefaultWritablePointMask( new double[] { 1, 1, 1 } );

		assertTrue( l.equals( l2 ) );

		l2.endpointOne().move( -1, 0 );
		assertFalse( l.equals( l2 ) );
		assertFalse( l.equals( l3 ) );
		assertFalse( l.equals( l4 ) );
		assertFalse( l.equals( p ) );
	}

	@Test
	public void testHashCode()
	{
		final Line l = new DefaultWritableLine( new double[] { 1, 1, 1 }, new double[] { 9, 9, 9 }, false );
		final WritableLine l2 = new DefaultWritableLine( new double[] { 1, 1, 1 }, new double[] { 9, 9, 9 }, false );
		final Line l3 = new DefaultWritableLine( new double[] { 1, 1 }, new double[] { 9, 9 }, false );
		final Line l4 = new DefaultWritableLine( new double[] { 9, 9, 9 }, new double[] { 1, 1, 1 }, false );
		final PointMask p = new DefaultWritablePointMask( new double[] { 1, 1, 1 } );

		assertEquals( l.hashCode(), l2.hashCode() );

		l2.endpointOne().move( -1, 0 );
		assertNotEquals( l.hashCode(), l2.hashCode() );
		assertNotEquals( l.hashCode(), l3.hashCode() );
		assertNotEquals( l.hashCode(), l4.hashCode() );
		assertNotEquals( l.hashCode(), p.hashCode() );
	}
}
