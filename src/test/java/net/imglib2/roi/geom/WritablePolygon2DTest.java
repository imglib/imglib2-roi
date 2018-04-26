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

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.geom.real.ClosedWritablePolygon2D;
import net.imglib2.roi.geom.real.DefaultWritablePolygon2D;
import net.imglib2.roi.geom.real.OpenWritablePolygon2D;
import net.imglib2.roi.geom.real.Polygon2D;
import net.imglib2.roi.geom.real.WritablePolygon2D;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests {@link Polygon2D}.
 *
 * @author Alison Walter
 */
public class WritablePolygon2DTest
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static List< RealLocalizable > points = new ArrayList<>();

	private static List< RealLocalizable > edge = new ArrayList<>();

	private static RealPoint inside = new RealPoint( 20, 14 );

	private static RealPoint outside = new RealPoint( 26, 30 );

	@BeforeClass
	public static void initTest()
	{
		points.clear();

		points.add( new RealPoint( 15, 15 ) );
		points.add( new RealPoint( 20, 20 ) );
		points.add( new RealPoint( 25, 15 ) );
		points.add( new RealPoint( 25, 10 ) );
		points.add( new RealPoint( 15, 10 ) );

		edge.clear();
		edge.add( new RealPoint( 17, 17 ) );
		edge.add( new RealPoint( 22, 18 ) );
		edge.add( new RealPoint( 25, 11 ) );
		edge.add( new RealPoint( 19, 10 ) );
		edge.add( new RealPoint( 15, 13 ) );
	}

	@Test
	public void testDefaultPolygon2D()
	{
		// test some edges
		final Polygon2D polygon = new DefaultWritablePolygon2D( points );

		// vertices
		assertTrue( polygon.test( points.get( 0 ) ) );
		assertFalse( polygon.test( points.get( 1 ) ) );
		assertFalse( polygon.test( points.get( 2 ) ) );
		assertFalse( polygon.test( points.get( 3 ) ) );
		assertTrue( polygon.test( points.get( 4 ) ) );

		// edges
		assertTrue( polygon.test( edge.get( 0 ) ) );
		assertFalse( polygon.test( edge.get( 1 ) ) );
		assertFalse( polygon.test( edge.get( 2 ) ) );
		assertTrue( polygon.test( edge.get( 3 ) ) );
		assertTrue( polygon.test( edge.get( 4 ) ) );

		// inside
		assertTrue( polygon.test( inside ) );

		// outside
		assertFalse( polygon.test( outside ) );

		// 2D polygon characteristics
		assertEquals( 5, polygon.numVertices() );
		assertTrue( assertRealLocalizableEquals( polygon.vertex( 0 ), points.get( 0 ) ) );
		assertTrue( assertRealLocalizableEquals( polygon.vertex( 2 ), points.get( 2 ) ) );
		assertTrue( assertRealLocalizableEquals( polygon.vertex( 4 ), points.get( 4 ) ) );
		assertTrue( polygon.boundaryType() == BoundaryType.UNSPECIFIED );
	}

	@Test
	public void testOpenPolygon2D()
	{
		// test no edges
		final Polygon2D polygon = new OpenWritablePolygon2D( points );

		// vertices
		assertFalse( polygon.test( points.get( 0 ) ) );
		assertFalse( polygon.test( points.get( 1 ) ) );
		assertFalse( polygon.test( points.get( 2 ) ) );
		assertFalse( polygon.test( points.get( 3 ) ) );
		assertFalse( polygon.test( points.get( 4 ) ) );

		// edges
		assertFalse( polygon.test( edge.get( 0 ) ) );
		assertFalse( polygon.test( edge.get( 1 ) ) );
		assertFalse( polygon.test( edge.get( 2 ) ) );
		assertFalse( polygon.test( edge.get( 3 ) ) );
		assertFalse( polygon.test( edge.get( 4 ) ) );

		// inside
		assertTrue( polygon.test( inside ) );

		// outside
		assertFalse( polygon.test( outside ) );

		// 2D polygon characteristics
		assertEquals( 5, polygon.numVertices() );
		assertTrue( assertRealLocalizableEquals( polygon.vertex( 1 ), points.get( 1 ) ) );
		assertTrue( assertRealLocalizableEquals( polygon.vertex( 3 ), points.get( 3 ) ) );
		assertTrue( assertRealLocalizableEquals( polygon.vertex( 4 ), points.get( 4 ) ) );
		assertTrue( polygon.boundaryType() == BoundaryType.OPEN );
	}

	@Test
	public void testClosedPolygon2D()
	{
		// test all edges
		final Polygon2D polygon = new ClosedWritablePolygon2D( points );

		// vertices
		assertTrue( polygon.test( points.get( 0 ) ) );
		assertTrue( polygon.test( points.get( 1 ) ) );
		assertTrue( polygon.test( points.get( 2 ) ) );
		assertTrue( polygon.test( points.get( 3 ) ) );
		assertTrue( polygon.test( points.get( 4 ) ) );

		// edges
		assertTrue( polygon.test( edge.get( 0 ) ) );
		assertTrue( polygon.test( edge.get( 1 ) ) );
		assertTrue( polygon.test( edge.get( 2 ) ) );
		assertTrue( polygon.test( edge.get( 3 ) ) );
		assertTrue( polygon.test( edge.get( 4 ) ) );

		// inside
		assertTrue( polygon.test( inside ) );

		// outside
		assertFalse( polygon.test( outside ) );

		// 2D polygon characteristics
		assertEquals( 5, polygon.numVertices() );
		assertTrue( assertRealLocalizableEquals( polygon.vertex( 0 ), points.get( 0 ) ) );
		assertTrue( assertRealLocalizableEquals( polygon.vertex( 1 ), points.get( 1 ) ) );
		assertTrue( assertRealLocalizableEquals( polygon.vertex( 2 ), points.get( 2 ) ) );
		assertTrue( polygon.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testSetVertex()
	{
		final WritablePolygon2D p = new DefaultWritablePolygon2D( points );

		assertFalse( p.test( new RealPoint( 30, 11 ) ) );

		p.vertex( 3 ).setPosition( new double[] { 40, 10 } );
		assertEquals( 5, p.numVertices() );
		assertEquals( 40, p.vertex( 3 ).getDoublePosition( 0 ), 0 );
		assertEquals( 10, p.vertex( 3 ).getDoublePosition( 1 ), 0 );
		assertTrue( p.test( new RealPoint( 30, 11 ) ) );
	}

	@Test
	public void testAddVertex()
	{
		final WritablePolygon2D p = new ClosedWritablePolygon2D( points );

		assertFalse( p.test( new RealPoint( 20, 6.5 ) ) );

		p.addVertex( 4, new RealPoint( 20, 5 ) );
		assertEquals( 6, p.numVertices() );
		assertEquals( p.vertex( 4 ).getDoublePosition( 0 ), 20, 0 );
		assertEquals( p.vertex( 4 ).getDoublePosition( 1 ), 5, 0 );
		assertTrue( p.test( new RealPoint( 20, 6.5 ) ) );
	}

	@Test
	public void testRemoveVertex()
	{
		final WritablePolygon2D p = new OpenWritablePolygon2D( points );

		assertTrue( p.test( new RealPoint( 20.125, 17 ) ) );

		p.removeVertex( 1 );
		assertEquals( 4, p.numVertices(),  0 );
		assertEquals( 25, p.vertex( 1 ).getDoublePosition( 0 ), 0 );
		assertEquals( 15, p.vertex( 1 ).getDoublePosition( 1 ), 0 );
		assertFalse( p.test( new RealPoint( 20.125, 17 ) ) );
	}

	@Test
	public void testFirstRealLocalizableHigherDim()
	{
		final List< RealLocalizable > pts = new ArrayList<>();
		pts.add( new RealPoint( 0, 0, 0 ) );
		pts.add( new RealPoint( 5, 5 ) );
		pts.add( new RealPoint( 10, 10 ) );

		final WritablePolygon2D p = new DefaultWritablePolygon2D( pts );
		assertEquals( 0, p.vertex( 0 ).getDoublePosition( 0 ), 0 );
		assertEquals( 0, p.vertex( 0 ).getDoublePosition( 1 ), 0 );
	}

	@Test
	public void testLaterRealLocalizableHigherDim()
	{
		final List< RealLocalizable > pts = new ArrayList<>();
		pts.add( new RealPoint( 0, 0 ) );
		pts.add( new RealPoint( 5, 5, 5 ) );
		pts.add( new RealPoint( 10, 10 ) );

		final WritablePolygon2D p = new DefaultWritablePolygon2D( pts );

		assertEquals( 3, p.numVertices() );
		assertEquals( 5, p.vertex( 1 ).getDoublePosition( 0 ), 0 );
		assertEquals( 5, p.vertex( 1 ).getDoublePosition( 1 ), 0 );
	}

	@Test
	public void testRealLocalizableSmallerDim()
	{
		final List< RealLocalizable > pts = new ArrayList<>();
		pts.add( new RealPoint( 0, 0 ) );
		pts.add( new RealPoint( 5, 5, 5 ) );
		pts.add( new RealPoint( 10.0 ) );

		exception.expect( IndexOutOfBoundsException.class );
		new DefaultWritablePolygon2D( pts );
	}

	@Test
	public void testUnequalXY()
	{
		final double[] x = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		final double[] y = new double[] { 1, 2, 3, 4, 5, 6, 7 };

		final WritablePolygon2D p = new DefaultWritablePolygon2D( x, y );

		assertEquals( 7, p.numVertices() );
		assertEquals( 7, p.vertex( 6 ).getDoublePosition( 0 ), 0 );
		assertEquals( 7, p.vertex( 6 ).getDoublePosition( 1 ), 0 );

		exception.expect( IndexOutOfBoundsException.class );
		p.vertex( 7 );
	}

	@Test
	public void testDimGreaterThanTwo()
	{
		final List< RealPoint > vertices = new ArrayList<>();
		vertices.add( new RealPoint( 1, 2, 3 ) );
		vertices.add( new RealPoint( -1, -2, -3 ) );
		vertices.add( new RealPoint( 10, 9, 8 ) );

		final WritablePolygon2D p = new DefaultWritablePolygon2D( vertices );
		assertEquals( 3, p.numVertices() );
		assertEquals( 1, p.vertex( 0 ).getDoublePosition( 0 ), 0 );
		assertEquals( 2, p.vertex( 0 ).getDoublePosition( 1 ), 0 );
		assertEquals( -1, p.vertex( 1 ).getDoublePosition( 0 ), 0 );
		assertEquals( -2, p.vertex( 1 ).getDoublePosition( 1 ), 0 );
		assertEquals( 10, p.vertex( 2 ).getDoublePosition( 0 ), 0 );
		assertEquals( 9, p.vertex( 2 ).getDoublePosition( 1 ), 0 );
	}

	@Test
	public void testSetVertexMoreThanTwo()
	{
		final WritablePolygon2D p = new DefaultWritablePolygon2D( points );

		p.vertex( 0 ).setPosition( new double[] { 1, 2, 3 } );
		assertEquals( 1, p.vertex( 0 ).getDoublePosition( 0 ), 0 );
		assertEquals( 2, p.vertex( 0 ).getDoublePosition( 1 ), 0 );
	}

	@Test
	public void testAddVertexMoreThanTwo()
	{
		final WritablePolygon2D p = new DefaultWritablePolygon2D( points );

		p.addVertex( 3, new RealPoint( 1, 2, 3 ) );
		assertEquals( 1, p.vertex( 3 ).getDoublePosition( 0 ), 0 );
		assertEquals( 2, p.vertex( 3 ).getDoublePosition( 1 ), 0 );
	}

	@Test
	public void testSetVertexLessThanTwo()
	{
		final WritablePolygon2D p = new DefaultWritablePolygon2D( points );

		exception.expect( IndexOutOfBoundsException.class );
		p.vertex( 0 ).setPosition( new double[] { 1 } );
	}

	@Test
	public void testAddVertexLessThanTwo()
	{
		final WritablePolygon2D p = new DefaultWritablePolygon2D( points );

		exception.expect( IndexOutOfBoundsException.class );
		p.addVertex( 3, new RealPoint() );
	}

	@Test
	public void testSetVertexInvalidIndex()
	{
		final WritablePolygon2D p = new DefaultWritablePolygon2D( points );

		exception.expect( IndexOutOfBoundsException.class );
		p.vertex( 6 ).setPosition( new double[] { 1, 2 } );
	}

	@Test
	public void testAddVertexInvalidIndex()
	{
		final WritablePolygon2D p = new DefaultWritablePolygon2D( points );

		exception.expect( IndexOutOfBoundsException.class );
		p.addVertex( 6, new RealPoint( 1, 2 ) );
	}

	@Test
	public void testRemoveVertexInvalidIndex()
	{
		final WritablePolygon2D p = new DefaultWritablePolygon2D( points );

		exception.expect( IndexOutOfBoundsException.class );
		p.removeVertex( 6 );
	}

	@Test
	public void testBounds()
	{
		// bounds are the same regardless of boundary type
		final WritablePolygon2D p = new DefaultWritablePolygon2D( points );
		final double[] max = new double[] { 25, 20 };
		final double[] min = new double[] { 15, 10 };
		final double[] pMin = new double[ 2 ];
		final double[] pMax = new double[ 2 ];
		p.realMin( pMin );
		p.realMax( pMax );

		assertArrayEquals( min, pMin, 0 );
		assertArrayEquals( max, pMax, 0 );

		// Mutate polygon
		p.vertex( 2 ).setPosition( new double[] { 30, 15 } );
		max[ 0 ] = 30;
		p.realMin( pMin );
		p.realMax( pMax );
		assertArrayEquals( min, pMin, 0 );
		assertArrayEquals( max, pMax, 0 );

		p.removeVertex( 4 );
		p.realMin( pMin );
		p.realMax( pMax );
		assertArrayEquals( min, pMin, 0 );
		assertArrayEquals( max, pMax, 0 );

		p.addVertex( 1, new RealPoint( -10, 100 ) );
		min[ 0 ] = -10;
		max[ 1 ] = 100;
		p.realMin( pMin );
		p.realMax( pMax );
		assertArrayEquals( min, pMin, 0 );
		assertArrayEquals( max, pMax, 0 );
	}

	@Test
	public void testEquals()
	{
		final Polygon2D p = new DefaultWritablePolygon2D( new double[] { 0, 10, 10, 0 }, new double[] { 0, 0, 10, 10 } );
		final WritablePolygon2D p2 = new DefaultWritablePolygon2D( new double[] { 0, 10, 10, 0 }, new double[] { 0, 0, 10, 10 } );
		final Polygon2D p3 = new DefaultWritablePolygon2D( new double[] { 0, 10, 10, 0 }, new double[] { 0, 10, 0, 10 } );
		final Polygon2D p4 = new DefaultWritablePolygon2D( new double[] { 0, 10, 20 }, new double[] { 0, 20, 0 } );
		final Polygon2D cp = new ClosedWritablePolygon2D( new double[] { 0, 10, 10, 0 }, new double[] { 0, 0, 10, 10 } );

		assertTrue( p.equals( p2 ) );

		p2.vertex( 0 ).move( -1, 1 );
		assertFalse( p.equals( p2 ) );
		assertFalse( p.equals( p3 ) );
		assertFalse( p.equals( p4 ) );
		assertFalse( p.equals( cp ) );
	}

	@Test
	public void testHashCode()
	{
		final Polygon2D p = new DefaultWritablePolygon2D( new double[] { 0, 10, 10, 0 }, new double[] { 0, 0, 10, 10 } );
		final WritablePolygon2D p2 = new DefaultWritablePolygon2D( new double[] { 0, 10, 10, 0 }, new double[] { 0, 0, 10, 10 } );
		final Polygon2D p3 = new DefaultWritablePolygon2D( new double[] { 0, 10, 10, 0 }, new double[] { 0, 10, 0, 10 } );
		final Polygon2D p4 = new DefaultWritablePolygon2D( new double[] { 0, 10, 20 }, new double[] { 0, 20, 0 } );
		final Polygon2D cp = new ClosedWritablePolygon2D( new double[] { 0, 10, 10, 0 }, new double[] { 0, 0, 10, 10 } );

		assertEquals( p.hashCode(), p2.hashCode() );

		p2.vertex( 0 ).move( -1, 1 );
		assertNotEquals( p.hashCode(), p2.hashCode() );
		assertNotEquals( p.hashCode(), p3.hashCode() );
		assertNotEquals( p.hashCode(), p4.hashCode() );
		assertNotEquals( p.hashCode(), cp.hashCode() );
	}

	// -- Helper methods --

	private boolean assertRealLocalizableEquals( final RealLocalizable predicted, final RealLocalizable expected )
	{
		return predicted.getDoublePosition( 0 ) == expected.getDoublePosition( 0 ) &&
				predicted.getDoublePosition( 1 ) == expected.getDoublePosition( 1 );
	}
}
