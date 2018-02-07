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
import net.imglib2.roi.geom.real.ClosedWritableEllipsoid;
import net.imglib2.roi.geom.real.ClosedWritableSphere;
import net.imglib2.roi.geom.real.ClosedWritableSuperEllipsoid;
import net.imglib2.roi.geom.real.Ellipsoid;
import net.imglib2.roi.geom.real.OpenWritableSphere;
import net.imglib2.roi.geom.real.Sphere;
import net.imglib2.roi.geom.real.SuperEllipsoid;
import net.imglib2.roi.geom.real.WritableSphere;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link Sphere}.
 *
 * @author Alison Walter
 */
public class WritableSphereTest
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testOpenCircle()
	{
		final WritableSphere s = new OpenWritableSphere( new double[] { 10, 10 }, 8 );

		// vertices
		assertFalse( s.test( new RealPoint( new double[] { 2, 10 } ) ) );
		assertFalse( s.test( new RealPoint( new double[] { 18, 10 } ) ) );
		assertFalse( s.test( new RealPoint( new double[] { 10, 2 } ) ) );
		assertFalse( s.test( new RealPoint( new double[] { 10, 18 } ) ) );

		// inside
		assertTrue( s.test( new RealPoint( new double[] { 12, 9 } ) ) );

		// outside
		assertFalse( s.test( new RealPoint( new double[] { 20, 1 } ) ) );

		// sphere characteristics
		assertEquals( s.exponent(), 2, 0 );
		assertEquals( s.center().getDoublePosition( 0 ), 10, 0 );
		assertEquals( s.center().getDoublePosition( 1 ), 10, 0 );
		assertEquals( s.semiAxisLength( 0 ), 8, 0 );
		assertEquals( s.semiAxisLength( 1 ), 8, 0 );
		assertEquals( s.radius(), 8, 0 );
		assertTrue( s.boundaryType() == BoundaryType.OPEN );
	}

	@Test
	public void testClosedCircle()
	{
		final WritableSphere s = new ClosedWritableSphere( new double[] { 10, 10 }, 8 );

		// vertices
		assertTrue( s.test( new RealPoint( new double[] { 2, 10 } ) ) );
		assertTrue( s.test( new RealPoint( new double[] { 18, 10 } ) ) );
		assertTrue( s.test( new RealPoint( new double[] { 10, 2 } ) ) );
		assertTrue( s.test( new RealPoint( new double[] { 10, 18 } ) ) );

		// inside
		assertTrue( s.test( new RealPoint( new double[] { 12, 9 } ) ) );

		// outside
		assertFalse( s.test( new RealPoint( new double[] { 20, 1 } ) ) );

		// sphere characteristics
		assertEquals( s.exponent(), 2, 0 );
		assertEquals( s.center().getDoublePosition( 0 ), 10, 0 );
		assertEquals( s.center().getDoublePosition( 1 ), 10, 0 );
		assertEquals( s.semiAxisLength( 0 ), 8, 0 );
		assertEquals( s.semiAxisLength( 1 ), 8, 0 );
		assertEquals( s.radius(), 8, 0 );
		assertTrue( s.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testSphereSetExponent()
	{
		final WritableSphere s = new OpenWritableSphere( new double[] { 1, 1 }, 4 );

		exception.expect( UnsupportedOperationException.class );
		s.setExponent( 0.25 );
	}

	@Test
	public void testMutateOpenSphere()
	{
		final WritableSphere s = new OpenWritableSphere( new double[] { 3, 2 }, 5 );

		assertEquals( s.center().getDoublePosition( 0 ), 3, 0 );
		assertEquals( s.center().getDoublePosition( 1 ), 2, 0 );
		assertEquals( s.radius(), 5, 0 );
		assertTrue( s.test( new RealPoint( new double[] { 6.5, 2.25 } ) ) );
		assertFalse( s.test( new RealPoint( new double[] { -9.5, 11.125 } ) ) );
		assertFalse( s.test( new RealPoint( new double[] { -17.5, 10.25 } ) ) );

		// change center
		s.center().setPosition( new double[] { -10, 10 } );

		assertEquals( s.center().getDoublePosition( 0 ), -10, 0 );
		assertEquals( s.center().getDoublePosition( 1 ), 10, 0 );
		assertFalse( s.test( new RealPoint( new double[] { 6.5, 2.25 } ) ) );
		assertTrue( s.test( new RealPoint( new double[] { -9.5, 11.125 } ) ) );
		assertFalse( s.test( new RealPoint( new double[] { -17.5, 10.25 } ) ) );

		// change radius, via change semi-axis length
		s.setSemiAxisLength( 1, 8 );
		assertEquals( s.radius(), 8, 0 );
		assertEquals( s.semiAxisLength( 0 ), 8, 0 );
		assertEquals( s.semiAxisLength( 1 ), 8, 0 );
		assertFalse( s.test( new RealPoint( new double[] { 6.5, 2.25 } ) ) );
		assertTrue( s.test( new RealPoint( new double[] { -9.5, 11.125 } ) ) );
		assertTrue( s.test( new RealPoint( new double[] { -17.5, 10.25 } ) ) );
	}

	@Test
	public void testMutateClosedSphere()
	{
		final WritableSphere s = new ClosedWritableSphere( new double[] { 3, 2 }, 5 );

		assertEquals( s.center().getDoublePosition( 0 ), 3, 0 );
		assertEquals( s.center().getDoublePosition( 1 ), 2, 0 );
		assertEquals( s.radius(), 5, 0 );
		assertTrue( s.test( new RealPoint( new double[] { 6.5, 2.25 } ) ) );
		assertFalse( s.test( new RealPoint( new double[] { -9.5, 11.125 } ) ) );
		assertFalse( s.test( new RealPoint( new double[] { -17.5, 10.25 } ) ) );

		// change center
		s.center().setPosition( new double[] { -10, 10 } );

		assertEquals( s.center().getDoublePosition( 0 ), -10, 0 );
		assertEquals( s.center().getDoublePosition( 1 ), 10, 0 );
		assertFalse( s.test( new RealPoint( new double[] { 6.5, 2.25 } ) ) );
		assertTrue( s.test( new RealPoint( new double[] { -9.5, 11.125 } ) ) );
		assertFalse( s.test( new RealPoint( new double[] { -17.5, 10.25 } ) ) );

		// change radius, via change semi-axis length
		s.setSemiAxisLength( 1, 8 );
		assertEquals( s.radius(), 8, 0 );
		assertEquals( s.semiAxisLength( 0 ), 8, 0 );
		assertEquals( s.semiAxisLength( 1 ), 8, 0 );
		assertFalse( s.test( new RealPoint( new double[] { 6.5, 2.25 } ) ) );
		assertTrue( s.test( new RealPoint( new double[] { -9.5, 11.125 } ) ) );
		assertTrue( s.test( new RealPoint( new double[] { -17.5, 10.25 } ) ) );
	}

	@Test
	public void testNegativeRadius()
	{
		exception.expect( IllegalArgumentException.class );
		new OpenWritableSphere( new double[] { 3, 2 }, -5 );
	}

	@Test
	public void testSetNegativeRadius()
	{
		final WritableSphere cs = new ClosedWritableSphere( new double[] { 3, 2 }, 5 );

		exception.expect( IllegalArgumentException.class );
		cs.setRadius( -2 );
	}

	@Test
	public void testSetTooShortCenter()
	{
		final WritableSphere cs = new ClosedWritableSphere( new double[] { 3, 2, 1 }, 5 );

		exception.expect( IndexOutOfBoundsException.class );
		cs.center().setPosition( new double[] { 1, 1 } );
	}

	@Test
	public void testSetTooLongCenter()
	{
		final WritableSphere os = new OpenWritableSphere( new double[] { 3, 2, 1 }, 5 );

		os.center().setPosition( new double[] { 1, 2, 3, 4 } );

		final double[] c = new double[ os.numDimensions() ];
		os.center().localize( c );
		assertEquals( c.length, os.numDimensions() );
		assertEquals( c[ 0 ], 1, 0 );
		assertEquals( c[ 1 ], 2, 0 );
		assertEquals( c[ 2 ], 3, 0 );
	}

	@Test
	public void testBounds()
	{
		// Bounds should be the same for open and closed spheres
		final WritableSphere s = new ClosedWritableSphere( new double[] { 3, 2, 1 }, 5 );
		double[] min = new double[] { 3 - 5, 2 - 5, 1 - 5 };
		double[] max = new double[] { 3 + 5, 2 + 5, 1 + 5 };
		final double[] sMin = new double[ 3 ];
		final double[] sMax = new double[ 3 ];
		s.realMin( sMin );
		s.realMax( sMax );

		assertArrayEquals( min, sMin, 0 );
		assertArrayEquals( max, sMax, 0 );

		// Mutate sphere
		s.setRadius( 2 );
		min = new double[] { 3 - 2, 2 - 2, 1 - 2 };
		max = new double[] { 3 + 2, 2 + 2, 1 + 2 };
		s.realMin( sMin );
		s.realMax( sMax );
		assertArrayEquals( min, sMin, 0 );
		assertArrayEquals( max, sMax, 0 );

		s.center().setPosition( new double[] { 0, 0, 0 } );
		min = new double[] { -2, -2, -2 };
		max = new double[] { 2, 2, 2 };
		s.realMin( sMin );
		s.realMax( sMax );
		assertArrayEquals( min, sMin, 0 );
		assertArrayEquals( max, sMax, 0 );
	}

	@Test
	public void testEquals()
	{
		final WritableSphere cs = new ClosedWritableSphere( new double[] { 10, -5, 6 }, 2.5 );
		final WritableSphere cs2 = new ClosedWritableSphere( new double[] { 10, -5, 6 }, 2.5 );
		final SuperEllipsoid cse = new ClosedWritableSuperEllipsoid( new double[] { 10, -5, 6 }, new double[] { 2.5, 2.5, 2.5 }, 2 );
		final Ellipsoid ce = new ClosedWritableEllipsoid( new double[] { 10, -5, 6 }, new double[] { 2.5, 2.5, 2.5 } );
		final WritableSphere os = new OpenWritableSphere( new double[] { 10, -5, 6 }, 2.5 );
		final WritableSphere cs3 = new ClosedWritableSphere( new double[] { 10, -5 }, 2.5 );

		assertTrue( cs.equals( cs2 ) );
		assertTrue( cs.equals( cse ) );
		assertTrue( cs.equals( ce ) );

		cs2.setRadius( 3 );
		assertFalse( cs.equals( cs2 ) );
		assertFalse( cs.equals( cs3 ) );
		assertFalse( cs.equals( os ) );
	}

	@Test
	public void testHashCode()
	{
		final WritableSphere cs = new ClosedWritableSphere( new double[] { 10, -5, 6 }, 2.5 );
		final WritableSphere cs2 = new ClosedWritableSphere( new double[] { 10, -5, 6 }, 2.5 );
		final SuperEllipsoid cse = new ClosedWritableSuperEllipsoid( new double[] { 10, -5, 6 }, new double[] { 2.5, 2.5, 2.5 }, 2 );
		final Ellipsoid ce = new ClosedWritableEllipsoid( new double[] { 10, -5, 6 }, new double[] { 2.5, 2.5, 2.5 } );
		final WritableSphere os = new OpenWritableSphere( new double[] { 10, -5, 6 }, 2.5 );
		final WritableSphere cs3 = new ClosedWritableSphere( new double[] { 10, -5 }, 2.5 );

		assertEquals( cs.hashCode(), cs2.hashCode() );
		assertEquals( cs.hashCode(), cse.hashCode() );
		assertEquals( cs.hashCode(), ce.hashCode() );

		cs2.setRadius( 3 );
		assertNotEquals( cs.hashCode(), cs2.hashCode() );
		assertNotEquals( cs.hashCode(), cs3.hashCode() );
		assertNotEquals( cs.hashCode(), os.hashCode() );
	}
}
