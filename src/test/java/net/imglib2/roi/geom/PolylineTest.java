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
package net.imglib2.roi.geom;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.roi.geom.real.DefaultPolyline;
import net.imglib2.roi.geom.real.Polyline;
import net.imglib2.roi.mask.Mask.BoundaryType;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests {@link Polyline}.
 *
 * @author Alison Walter
 */
public class PolylineTest
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static List< RealLocalizable > simple;

	private static List< RealLocalizable > fourD;

	private static List< RealLocalizable > intersect;

	private static double[][] doubleVertices;

	@BeforeClass
	public static void setup()
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

		doubleVertices = new double[][] { { 2, 3 }, { 0.25, -12.125 }, { 200, 5.25 } };
	}

	@Test
	public void testSimplePolyline()
	{
		final Polyline pl = new DefaultPolyline( simple );

		// check all vertices
		for ( int i = 0; i < simple.size(); i++ )
			assertTrue( pl.contains( simple.get( i ) ) );

		// on polyline
		assertTrue( pl.contains( new RealPoint( new double[] { 7, 7 } ) ) );
		assertTrue( pl.contains( new RealPoint( new double[] { 23, 3 } ) ) );
		assertTrue( pl.contains( new RealPoint( new double[] { 17, 3 } ) ) );

		// off polyline
		assertFalse( pl.contains( new RealPoint( new double[] { 31, 11 } ) ) );
		assertFalse( pl.contains( new RealPoint( new double[] { -0.01, -0.01 } ) ) );
		assertFalse( pl.contains( new RealPoint( new double[] { 12, 11 } ) ) );

		// polyline characteristics
		assertEquals( simple.size(), pl.numVertices() );
		double[] vert;
		for ( int i = 0; i < simple.size(); i++ )
		{
			vert = pl.vertex( i );
			assertEquals( vert[ 0 ], simple.get( i ).getDoublePosition( 0 ), 0 );
			assertEquals( vert[ 1 ], simple.get( i ).getDoublePosition( 1 ), 0 );
		}
		assertTrue( pl.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void test4DPolyline()
	{
		final Polyline pl = new DefaultPolyline( fourD );

		// check all vertices
		for ( int i = 0; i < fourD.size(); i++ )
			assertTrue( pl.contains( fourD.get( i ) ) );

		// on polyline
		assertTrue( pl.contains( new RealPoint( new double[] { 3, 3, 3, 3 } ) ) );
		assertTrue( pl.contains( new RealPoint( new double[] { 3.5, 4, 4, 3.5 } ) ) );

		// off polyline
		assertFalse( pl.contains( new RealPoint( new double[] { 6, 7, 8, 9 } ) ) );

		// polyline characteristics
		assertEquals( fourD.size(), pl.numVertices() );
		double[] vert;
		for ( int i = 0; i < fourD.size(); i++ )
		{
			vert = pl.vertex( i );
			assertEquals( vert[ 0 ], fourD.get( i ).getDoublePosition( 0 ), 0 );
			assertEquals( vert[ 1 ], fourD.get( i ).getDoublePosition( 1 ), 0 );
			assertEquals( vert[ 2 ], fourD.get( i ).getDoublePosition( 2 ), 0 );
			assertEquals( vert[ 3 ], fourD.get( i ).getDoublePosition( 3 ), 0 );
		}
		assertTrue( pl.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testSelfIntersectingPolyline()
	{
		final Polyline pl = new DefaultPolyline( intersect );

		// check all vertices
		for ( int i = 0; i < intersect.size(); i++ )
			assertTrue( pl.contains( intersect.get( i ) ) );

		// on polyline
		assertTrue( pl.contains( new RealPoint( new double[] { 9, 9 } ) ) );
		assertTrue( pl.contains( new RealPoint( new double[] { 4, 4 } ) ) );
		assertTrue( pl.contains( new RealPoint( new double[] { 7, 17 } ) ) );
		assertTrue( pl.contains( new RealPoint( new double[] { 15, 3 } ) ) );
		assertTrue( pl.contains( new RealPoint( new double[] { 21, 2 } ) ) );

		// off polyline
		assertFalse( pl.contains( new RealPoint( new double[] { 40, 3 } ) ) );
		assertFalse( pl.contains( new RealPoint( new double[] { 18, 18 } ) ) );
		assertFalse( pl.contains( new RealPoint( new double[] { 7, 17.1 } ) ) );

		// polyline characteristics
		assertEquals( intersect.size(), pl.numVertices() );
		double[] vert;
		for ( int i = 0; i < intersect.size(); i++ )
		{
			vert = pl.vertex( i );
			assertEquals( vert[ 0 ], intersect.get( i ).getDoublePosition( 0 ), 0 );
			assertEquals( vert[ 1 ], intersect.get( i ).getDoublePosition( 1 ), 0 );
		}
		assertTrue( pl.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testDoubleVertices()
	{
		final Polyline pl = new DefaultPolyline( doubleVertices );

		// check all vertices
		for ( int i = 0; i < doubleVertices.length; i++ )
			assertTrue( pl.contains( new RealPoint( doubleVertices[ i ] ) ) );

		// on polyline
		assertTrue( pl.contains( new RealPoint( new double[] { 1.3, -3.05 } ) ) );
		assertTrue( pl.contains( new RealPoint( new double[] { 15, -10.84199 } ) ) );

		// off polyline
		assertFalse( pl.contains( new RealPoint( new double[] { 1, -3.4 } ) ) );
		assertFalse( pl.contains( new RealPoint( new double[] { 2, 4 } ) ) );
		assertFalse( pl.contains( new RealPoint( new double[] { 0.18, -12.73 } ) ) );

		// polyline characteristics
		assertEquals( doubleVertices.length, pl.numVertices() );
		for ( int i = 0; i < doubleVertices.length; i++ )
			assertArrayEquals( pl.vertex( i ), doubleVertices[ i ], 0 );
		assertTrue( pl.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testSetVertex()
	{
		final Polyline pl = new DefaultPolyline( doubleVertices );

		assertArrayEquals( pl.vertex( 1 ), doubleVertices[ 1 ], 0 );
		assertTrue( pl.contains( new RealPoint( new double[] { 1.3, -3.05 } ) ) );
		assertFalse( pl.contains( new RealPoint( new double[] { 5, 9 } ) ) );

		pl.setVertex( 1, new double[] { 10, 19 } );

		assertArrayEquals( pl.vertex( 1 ), new double[] { 10, 19 }, 0 );
		assertFalse( pl.contains( new RealPoint( new double[] { 1.3, -3.05 } ) ) );
		assertTrue( pl.contains( new RealPoint( new double[] { 5, 9 } ) ) );
	}

	@Test
	public void testAddVertex()
	{
		final Polyline pl = new DefaultPolyline( doubleVertices );

		assertEquals( pl.numVertices(), 3 );
		assertFalse( pl.contains( new RealPoint( new double[] { 207, 2.45 } ) ) );

		pl.addVertex( 3, new double[] { 212.5, 0.25 } );

		assertEquals( pl.numVertices(), 4 );
		assertArrayEquals( pl.vertex( 3 ), new double[] { 212.5, 0.25 }, 0 );
		assertTrue( pl.contains( new RealPoint( new double[] { 207, 2.45 } ) ) );
	}

	@Test
	public void testRemoveVertex()
	{
		final Polyline pl = new DefaultPolyline( doubleVertices );

		assertEquals( pl.numVertices(), 3 );
		assertArrayEquals( pl.vertex( 1 ), doubleVertices[ 1 ], 0 );
		assertFalse( pl.contains( new RealPoint( new double[] { 13, 3.125 } ) ) );

		pl.removeVertex( 1 );

		assertEquals( pl.numVertices(), 2 );
		assertArrayEquals( pl.vertex( 1 ), doubleVertices[ 2 ], 0 );
		assertTrue( pl.contains( new RealPoint( new double[] { 13, 3.125 } ) ) );
	}

	@Test
	public void testFirstRealLocalizableHigherDim()
	{
		final List< RealLocalizable > pts = new ArrayList<>();
		pts.add( new RealPoint( new double[] { 1, 1, 1, 1 } ) );
		pts.add( new RealPoint( new double[] { 5, 5, 5 } ) );
		pts.add( new RealPoint( new double[] { 9, 9, 9, 9 } ) );

		exception.expect( ArrayIndexOutOfBoundsException.class );
		new DefaultPolyline( pts );
	}

	@Test
	public void testLaterRealLocalizableHigherDim()
	{
		final List< RealLocalizable > pts = new ArrayList<>();
		pts.add( new RealPoint( new double[] { 1, 1 } ) );
		pts.add( new RealPoint( new double[] { 5, 5, 5 } ) );
		pts.add( new RealPoint( new double[] { 9, 9, 9, 9 } ) );

		final Polyline p = new DefaultPolyline( pts );

		assertEquals( p.numDimensions(), 2 );
		assertArrayEquals( p.vertex( 1 ), new double[] { 5, 5 }, 0 );
		assertArrayEquals( p.vertex( 2 ), new double[] { 9, 9 }, 0 );
	}

	@Test
	public void testSetVertexNotN()
	{
		final Polyline p = new DefaultPolyline( simple );

		exception.expect( IllegalArgumentException.class );
		p.setVertex( 0, new double[] { 1, 2, 3 } );
	}

	@Test
	public void testAddVertexNotN()
	{
		final Polyline p = new DefaultPolyline( fourD );

		exception.expect( IllegalArgumentException.class );
		p.addVertex( 3, new double[] { 1, 2, 3 } );
	}

	@Test
	public void testSetVertexInvalidIndex()
	{
		final Polyline p = new DefaultPolyline( simple );

		exception.expect( IndexOutOfBoundsException.class );
		p.setVertex( 6, new double[] { 1, 2 } );
	}

	@Test
	public void testAddVertexInvalidIndex()
	{
		final Polyline p = new DefaultPolyline( simple );

		exception.expect( IndexOutOfBoundsException.class );
		p.addVertex( 6, new double[] { 1, 2 } );
	}

	@Test
	public void testRemoveVertexInvalidIndex()
	{
		final Polyline p = new DefaultPolyline( simple );

		exception.expect( IndexOutOfBoundsException.class );
		p.removeVertex( 6 );
	}
}
