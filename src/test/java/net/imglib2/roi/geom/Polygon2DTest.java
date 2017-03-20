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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.roi.geom.real.ClosedPolygon2D;
import net.imglib2.roi.geom.real.DefaultPolygon2D;
import net.imglib2.roi.geom.real.OpenPolygon2D;
import net.imglib2.roi.geom.real.Polygon2D;

import org.junit.BeforeClass;
import org.junit.Test;

public class Polygon2DTest
{
	private static List< RealLocalizable > points = new ArrayList<>();

	private static List< RealLocalizable > edge = new ArrayList<>();

	private static RealPoint inside = new RealPoint( new double[] { 20, 14 } );

	private static RealPoint outside = new RealPoint( new double[] { 26, 30 } );

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
		// contains some edges
		final Polygon2D polygon = new DefaultPolygon2D( points );

		// vertices
		assertTrue( polygon.contains( points.get( 0 ) ) );
		assertFalse( polygon.contains( points.get( 1 ) ) );
		assertFalse( polygon.contains( points.get( 2 ) ) );
		assertFalse( polygon.contains( points.get( 3 ) ) );
		assertTrue( polygon.contains( points.get( 4 ) ) );

		// edges
		assertTrue( polygon.contains( edge.get( 0 ) ) );
		assertFalse( polygon.contains( edge.get( 1 ) ) );
		assertFalse( polygon.contains( edge.get( 2 ) ) );
		assertTrue( polygon.contains( edge.get( 3 ) ) );
		assertTrue( polygon.contains( edge.get( 4 ) ) );

		// inside
		assertTrue( polygon.contains( inside ) );

		// outside
		assertFalse( polygon.contains( outside ) );

		// 2D polygon characteristics
		assertEquals( polygon.numVertices(), 5 );
		assertEquals( polygon.vertex( 0 )[ 0 ], points.get( 0 ).getDoublePosition( 0 ), 0 );
		assertEquals( polygon.vertex( 0 )[ 1 ], points.get( 0 ).getDoublePosition( 1 ), 0 );
		assertEquals( polygon.vertex( 2 )[ 0 ], points.get( 2 ).getDoublePosition( 0 ), 0 );
		assertEquals( polygon.vertex( 2 )[ 1 ], points.get( 2 ).getDoublePosition( 1 ), 0 );
		assertEquals( polygon.vertex( 4 )[ 0 ], points.get( 4 ).getDoublePosition( 0 ), 0 );
		assertEquals( polygon.vertex( 4 )[ 1 ], points.get( 4 ).getDoublePosition( 1 ), 0 );
	}

	@Test
	public void testOpenPolygon2D()
	{
		// contains no edges
		final Polygon2D polygon = new OpenPolygon2D( points );

		// vertices
		assertFalse( polygon.contains( points.get( 0 ) ) );
		assertFalse( polygon.contains( points.get( 1 ) ) );
		assertFalse( polygon.contains( points.get( 2 ) ) );
		assertFalse( polygon.contains( points.get( 3 ) ) );
		assertFalse( polygon.contains( points.get( 4 ) ) );

		// edges
		assertFalse( polygon.contains( edge.get( 0 ) ) );
		assertFalse( polygon.contains( edge.get( 1 ) ) );
		assertFalse( polygon.contains( edge.get( 2 ) ) );
		assertFalse( polygon.contains( edge.get( 3 ) ) );
		assertFalse( polygon.contains( edge.get( 4 ) ) );

		// inside
		assertTrue( polygon.contains( inside ) );

		// outside
		assertFalse( polygon.contains( outside ) );

		// 2D polygon characteristics
		assertEquals( polygon.numVertices(), 5 );
		assertEquals( polygon.vertex( 1 )[ 0 ], points.get( 1 ).getDoublePosition( 0 ), 0 );
		assertEquals( polygon.vertex( 1 )[ 1 ], points.get( 1 ).getDoublePosition( 1 ), 0 );
		assertEquals( polygon.vertex( 3 )[ 0 ], points.get( 3 ).getDoublePosition( 0 ), 0 );
		assertEquals( polygon.vertex( 3 )[ 1 ], points.get( 3 ).getDoublePosition( 1 ), 0 );
		assertEquals( polygon.vertex( 4 )[ 0 ], points.get( 4 ).getDoublePosition( 0 ), 0 );
		assertEquals( polygon.vertex( 4 )[ 1 ], points.get( 4 ).getDoublePosition( 1 ), 0 );
	}

	@Test
	public void testClosedPolygon2D()
	{
		// contains all edges
		final Polygon2D polygon = new ClosedPolygon2D( points );

		// vertices
		assertTrue( polygon.contains( points.get( 0 ) ) );
		assertTrue( polygon.contains( points.get( 1 ) ) );
		assertTrue( polygon.contains( points.get( 2 ) ) );
		assertTrue( polygon.contains( points.get( 3 ) ) );
		assertTrue( polygon.contains( points.get( 4 ) ) );

		// edges
		assertTrue( polygon.contains( edge.get( 0 ) ) );
		assertTrue( polygon.contains( edge.get( 1 ) ) );
		assertTrue( polygon.contains( edge.get( 2 ) ) );
		assertTrue( polygon.contains( edge.get( 3 ) ) );
		assertTrue( polygon.contains( edge.get( 4 ) ) );

		// inside
		assertTrue( polygon.contains( inside ) );

		// outside
		assertFalse( polygon.contains( outside ) );

		// 2D polygon characteristics
		assertEquals( polygon.numVertices(), 5 );
		assertEquals( polygon.vertex( 0 )[ 0 ], points.get( 0 ).getDoublePosition( 0 ), 0 );
		assertEquals( polygon.vertex( 0 )[ 1 ], points.get( 0 ).getDoublePosition( 1 ), 0 );
		assertEquals( polygon.vertex( 1 )[ 0 ], points.get( 1 ).getDoublePosition( 0 ), 0 );
		assertEquals( polygon.vertex( 1 )[ 1 ], points.get( 1 ).getDoublePosition( 1 ), 0 );
		assertEquals( polygon.vertex( 2 )[ 0 ], points.get( 2 ).getDoublePosition( 0 ), 0 );
		assertEquals( polygon.vertex( 2 )[ 1 ], points.get( 2 ).getDoublePosition( 1 ), 0 );
	}
}
