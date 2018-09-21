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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.geom.real.DefaultWritableLine;
import net.imglib2.roi.geom.real.DefaultWritablePolyline;
import net.imglib2.roi.geom.real.Line;
import net.imglib2.roi.geom.real.Polyline;
import net.imglib2.roi.geom.real.WritablePolyline;
import net.imglib2.util.Util;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests {@link Polyline}.
 *
 * @author Alison Walter
 */
public class WritablePolylineTest
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static List< RealPoint > simple;

	private static List< RealPoint > fourD;

	private static List< RealPoint > intersect;

	private static List< RealPoint > polyline;

	@Before
	public void setup()
	{
		simple = new ArrayList<>();
		simple.add( new RealPoint( new double[] { 0, 0 } ) );
		simple.add( new RealPoint( new double[] { 10, 10 } ) );
		simple.add( new RealPoint( new double[] { 20, 0 } ) );
		simple.add( new RealPoint( new double[] { 30, 10 } ) );

		fourD = new ArrayList<>();
		fourD.add( new RealPoint( new double[] { 1, 1, 1, 1 } ) );
		fourD.add( new RealPoint( new double[] { 5, 5, 5, 5 } ) );
		fourD.add( new RealPoint( new double[] { 2, 3, 3, 2 } ) );

		intersect = new ArrayList<>();
		intersect.add( new RealPoint( new double[] { 1, 1 } ) );
		intersect.add( new RealPoint( new double[] { 17, 17 } ) );
		intersect.add( new RealPoint( new double[] { 1, 17 } ) );
		intersect.add( new RealPoint( new double[] { 17, 1 } ) );
		intersect.add( new RealPoint( new double[] { 33, 5 } ) );

		polyline = new ArrayList<>();
		polyline.add( new RealPoint( new double[] { 2, 3 } ) );
		polyline.add( new RealPoint( new double[] { 0.25, -12.125 } ) );
		polyline.add( new RealPoint( new double[] { 200, 5.25 } ) );
	}

	@Test
	public void testSimplePolyline()
	{
		final WritablePolyline pl = new DefaultWritablePolyline( simple );

		// check all vertices
		for ( int i = 0; i < simple.size(); i++ )
			assertTrue( pl.test( simple.get( i ) ) );

		// on polyline
		assertTrue( pl.test( new RealPoint( new double[] { 7, 7 } ) ) );
		assertTrue( pl.test( new RealPoint( new double[] { 23, 3 } ) ) );
		assertTrue( pl.test( new RealPoint( new double[] { 17, 3 } ) ) );

		// off polyline
		assertFalse( pl.test( new RealPoint( new double[] { 31, 11 } ) ) );
		assertFalse( pl.test( new RealPoint( new double[] { -0.01, -0.01 } ) ) );
		assertFalse( pl.test( new RealPoint( new double[] { 12, 11 } ) ) );

		// polyline characteristics
		assertEquals( simple.size(), pl.numVertices() );
		RealLocalizable vert;
		for ( int i = 0; i < simple.size(); i++ )
		{
			vert = pl.vertex( i );
			assertTrue( assertRealLocalizableEquals( vert, simple.get( i ) ) );
		}
		assertTrue( pl.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void test4DPolyline()
	{
		final WritablePolyline pl = new DefaultWritablePolyline( fourD );

		// check all vertices
		for ( int i = 0; i < fourD.size(); i++ )
			assertTrue( pl.test( fourD.get( i ) ) );

		// on polyline
		assertTrue( pl.test( new RealPoint( new double[] { 3, 3, 3, 3 } ) ) );
		assertTrue( pl.test( new RealPoint( new double[] { 3.5, 4, 4, 3.5 } ) ) );

		// off polyline
		assertFalse( pl.test( new RealPoint( new double[] { 6, 7, 8, 9 } ) ) );

		// polyline characteristics
		assertEquals( fourD.size(), pl.numVertices() );
		RealLocalizable vert;
		for ( int i = 0; i < fourD.size(); i++ )
		{
			vert = pl.vertex( i );
			assertTrue( assertRealLocalizableEquals( vert, fourD.get( i ) ) );
		}
		assertTrue( pl.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testSelfIntersectingPolyline()
	{
		final WritablePolyline pl = new DefaultWritablePolyline( intersect );

		// check all vertices
		for ( int i = 0; i < intersect.size(); i++ )
			assertTrue( pl.test( intersect.get( i ) ) );

		// on polyline
		assertTrue( pl.test( new RealPoint( new double[] { 9, 9 } ) ) );
		assertTrue( pl.test( new RealPoint( new double[] { 4, 4 } ) ) );
		assertTrue( pl.test( new RealPoint( new double[] { 7, 17 } ) ) );
		assertTrue( pl.test( new RealPoint( new double[] { 15, 3 } ) ) );
		assertTrue( pl.test( new RealPoint( new double[] { 21, 2 } ) ) );

		// off polyline
		assertFalse( pl.test( new RealPoint( new double[] { 40, 3 } ) ) );
		assertFalse( pl.test( new RealPoint( new double[] { 18, 18 } ) ) );
		assertFalse( pl.test( new RealPoint( new double[] { 7, 17.1 } ) ) );

		// polyline characteristics
		assertEquals( intersect.size(), pl.numVertices() );
		RealLocalizable vert;
		for ( int i = 0; i < intersect.size(); i++ )
		{
			vert = pl.vertex( i );
			assertTrue( assertRealLocalizableEquals( vert, intersect.get( i ) ) );
		}
		assertTrue( pl.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testSetVertex()
	{
		final WritablePolyline pl = new DefaultWritablePolyline( polyline );

		assertTrue( assertRealLocalizableEquals( pl.vertex( 1 ), polyline.get( 1 ) ) );
		assertTrue( pl.test( new RealPoint( new double[] { 1.3, -3.05 } ) ) );
		assertFalse( pl.test( new RealPoint( new double[] { 5, 9 } ) ) );

		pl.vertex( 1 ).setPosition( new double[] { 10, 19 } );

		assertTrue( assertRealLocalizableEquals( pl.vertex( 1 ), new double[] { 10, 19 } ) );
		assertFalse( pl.test( new RealPoint( new double[] { 1.3, -3.05 } ) ) );
		assertTrue( pl.test( new RealPoint( new double[] { 5, 9 } ) ) );
	}

	@Test
	public void testAddVertex()
	{
		final WritablePolyline pl = new DefaultWritablePolyline( polyline );

		assertEquals( pl.numVertices(), 3 );
		assertFalse( pl.test( new RealPoint( new double[] { 207, 2.45 } ) ) );

		pl.addVertex( 3, new RealPoint( new double[] { 212.5, 0.25 } ) );

		assertEquals( pl.numVertices(), 4 );
		assertTrue( assertRealLocalizableEquals( pl.vertex( 3 ), new double[] { 212.5, 0.25 } ) );
		assertTrue( pl.test( new RealPoint( new double[] { 207, 2.45 } ) ) );
	}

	@Test
	public void testRemoveVertex()
	{
		final WritablePolyline pl = new DefaultWritablePolyline( polyline );

		assertEquals( pl.numVertices(), 3 );
		assertTrue( assertRealLocalizableEquals( pl.vertex( 1 ), polyline.get( 1 ) ) );
		assertFalse( pl.test( new RealPoint( new double[] { 13, 3.125 } ) ) );

		final double[] beforeRemove = new double[ pl.numDimensions() ];
		pl.vertex( 2 ).localize( beforeRemove );
		pl.removeVertex( 1 );

		assertEquals( pl.numVertices(), 2 );
		assertTrue( assertRealLocalizableEquals( pl.vertex( 1 ), beforeRemove ) );
		assertTrue( pl.test( new RealPoint( new double[] { 13, 3.125 } ) ) );
	}

	@Test
	public void testAddVertices()
	{
		final WritablePolyline pl = new DefaultWritablePolyline( polyline );

		// Generate a bunch of random points.
		final Random r = new Random(0xdeadbeef);
		final int extraCount = 99;
		final List < RealLocalizable > extra = new ArrayList<>();
		for ( int i = 0; i < extraCount; i++ )
			extra.add( new RealPoint( r.nextDouble(), r.nextDouble() ) );

		// Add the random points.
		final int offset = 1;
		pl.addVertices( offset, extra );

		// Check that they match.
		assertEquals( 3 + extraCount, pl.numVertices() );
		int index = offset;
		for ( final RealLocalizable expected : extra )
		{
			final RealLocalizable actual = pl.vertex( index );
			assertTrue( "Index #" + index++, Util.locationsEqual( expected, actual ) );
		}
	}

	@Test
	public void testFirstRealLocalizableHigherDim()
	{
		final List< RealPoint > pts = new ArrayList<>();
		pts.add( new RealPoint( new double[] { 1, 1, 1, 1 } ) );
		pts.add( new RealPoint( new double[] { 5, 5, 5 } ) );
		pts.add( new RealPoint( new double[] { 9, 9, 9, 9 } ) );

		exception.expect( ArrayIndexOutOfBoundsException.class );
		new DefaultWritablePolyline( pts );
	}

	@Test
	public void testLaterRealLocalizableHigherDim()
	{
		final List< RealPoint > pts = new ArrayList<>();
		pts.add( new RealPoint( new double[] { 1, 1 } ) );
		pts.add( new RealPoint( new double[] { 5, 5, 5 } ) );
		pts.add( new RealPoint( new double[] { 9, 9, 9, 9 } ) );

		final WritablePolyline p = new DefaultWritablePolyline( pts );
		assertRealLocalizableEquals( p.vertex( 0 ), new double[] { 1, 1 } );
		assertRealLocalizableEquals( p.vertex( 1 ), new double[] { 5, 5 } );
		assertRealLocalizableEquals( p.vertex( 2 ), new double[] { 9, 9 } );
	}

	@Test
	public void testSetVertexNotN()
	{
		final WritablePolyline p = new DefaultWritablePolyline( simple );

		p.vertex( 0 ).setPosition( new double[] { 1, 2, 3 } );
		assertEquals( p.vertex( 0 ).numDimensions(), 2 );
		assertEquals( p.vertex( 0 ).getDoublePosition( 0 ), 1, 0 );
		assertEquals( p.vertex( 0 ).getDoublePosition( 1 ), 2, 0 );

		exception.expect( IndexOutOfBoundsException.class );
		assertEquals( p.vertex( 0 ).getDoublePosition( 2 ), 3, 0 );
	}

	@Test
	public void testAddVertexNotN()
	{
		final WritablePolyline p = new DefaultWritablePolyline( fourD );

		exception.expect( IllegalArgumentException.class );
		p.addVertex( 3, new RealPoint( new double[] { 1, 2, 3 } ) );
	}

	@Test
	public void testSetVertexInvalidIndex()
	{
		final WritablePolyline p = new DefaultWritablePolyline( simple );

		exception.expect( IndexOutOfBoundsException.class );
		p.vertex( 6 ).setPosition( new double[] { 1, 2 } );
	}

	@Test
	public void testAddVertexInvalidIndex()
	{
		final WritablePolyline p = new DefaultWritablePolyline( simple );

		exception.expect( IndexOutOfBoundsException.class );
		p.addVertex( 6, new RealPoint( new double[] { 1, 2 } ) );
	}

	@Test
	public void testRemoveVertexInvalidIndex()
	{
		final WritablePolyline p = new DefaultWritablePolyline( simple );

		exception.expect( IndexOutOfBoundsException.class );
		p.removeVertex( 6 );
	}

	@Test
	public void testBounds()
	{
		final WritablePolyline pl = new DefaultWritablePolyline( simple );
		final double[] max = new double[] { 30, 10 };
		final double[] min = new double[] { 0, 0 };
		final double[] plMin = new double[ 2 ];
		final double[] plMax = new double[ 2 ];
		pl.realMin( plMin );
		pl.realMax( plMax );

		assertArrayEquals( min, plMin, 0 );
		assertArrayEquals( max, plMax, 0 );

		pl.vertex( 0 ).setPosition( new double[] { 1, 1 } );
		min[ 0 ] = 1;
		pl.realMin( plMin );
		pl.realMax( plMax );
		assertArrayEquals( min, plMin, 0 );
		assertArrayEquals( max, plMax, 0 );

		pl.addVertex( 3, new RealPoint( new double[] { 5, -1 } ) );
		min[ 1 ] = -1;
		pl.realMin( plMin );
		pl.realMax( plMax );
		assertArrayEquals( min, plMin, 0 );
		assertArrayEquals( max, plMax, 0 );

		pl.removeVertex( 4 );
		max[ 0 ] = 20;
		pl.realMin( plMin );
		pl.realMax( plMax );
		assertArrayEquals( min, plMin, 0 );
		assertArrayEquals( max, plMax, 0 );
	}

	@Test
	public void testEquals()
	{
		final RealPoint rp = new RealPoint( new double[] { 0, 0, 0 } );
		final RealPoint rp2 = new RealPoint( new double[] { 12, 12, 12 } );
		final RealPoint rp3 = new RealPoint( new double[] { 4, 4, 4 } );

		final ArrayList< RealPoint > a = new ArrayList<>();
		a.add( rp );
		a.add( rp2 );
		a.add( rp3 );

		final ArrayList< RealPoint > a2 = new ArrayList<>();
		a2.add( rp );
		a2.add( rp3 );
		a2.add( rp2 );

		final WritablePolyline p = new DefaultWritablePolyline( a );
		WritablePolyline o = new DefaultWritablePolyline( a );

		assertTrue( p.equals( o ) );

		o = new DefaultWritablePolyline( a2 );
		final Line l = new DefaultWritableLine( new double[] { 0, 0, 0 }, new double[] { 4, 4, 4 }, false );
		assertFalse( p.equals( o ) );
		assertFalse( p.equals( l ) );
	}

	@Test
	public void testHashCode()
	{
		final RealPoint rp = new RealPoint( new double[] { 0, 0, 0 } );
		final RealPoint rp2 = new RealPoint( new double[] { 12, 12, 12 } );
		final RealPoint rp3 = new RealPoint( new double[] { 4, 4, 4 } );

		final ArrayList< RealPoint > a = new ArrayList<>();
		a.add( rp );
		a.add( rp2 );
		a.add( rp3 );

		final ArrayList< RealPoint > a2 = new ArrayList<>();
		a2.add( rp );
		a2.add( rp3 );
		a2.add( rp2 );

		final WritablePolyline p = new DefaultWritablePolyline( a );
		WritablePolyline o = new DefaultWritablePolyline( a );

		assertEquals( p.hashCode(), o.hashCode() );

		o = new DefaultWritablePolyline( a2 );
		final Line l = new DefaultWritableLine( new double[] { 0, 0, 0 }, new double[] { 4, 4, 4 }, false );
		assertNotEquals( p.hashCode(), o.hashCode() );
		assertNotEquals( p.hashCode(), l.hashCode() );
	}

	// -- Helper methods --

	private boolean assertRealLocalizableEquals( final RealLocalizable predicted, final RealLocalizable expected )
	{
		if ( predicted.numDimensions() != expected.numDimensions() )
			return false;

		for ( int i = 0; i < predicted.numDimensions(); i++ )
		{
			if ( predicted.getDoublePosition( i ) != expected.getDoublePosition( i ) )
				return false;
		}

		return true;
	}

	private boolean assertRealLocalizableEquals( final RealLocalizable predicted, final double[] expected )
	{
		if ( predicted.numDimensions() != expected.length )
			return false;

		for ( int i = 0; i < predicted.numDimensions(); i++ )
		{
			if ( predicted.getDoublePosition( i ) != expected[ i ] )
				return false;
		}

		return true;
	}

}
