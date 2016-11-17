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
package net.imglib2.roi.geometric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.logic.BoolType;

import org.junit.BeforeClass;
import org.junit.Test;

public class GeometricROITest
{

	private static Polygon polygon;

	private static RasterizedPolygonalChain polygonalChain;

	@BeforeClass
	public static void initTest()
	{

		final List< RealLocalizable > points = new ArrayList< RealLocalizable >();
		points.add( new RealPoint( 10d, 10d ) );
		points.add( new RealPoint( 20d, 10d ) );
		points.add( new RealPoint( 20d, 20d ) );
		points.add( new RealPoint( 10d, 20d ) );

		polygon = new Polygon( points );
		polygonalChain = new RasterizedPolygonalChain( points );
	}

	@Test
	public void testPolygon()
	{
		// check size
		assertEquals( "Polygon Size", 4, polygon.getVertices().size() );

		// check point in polygon test
		final RealPoint realPoint = new RealPoint( 15d, 15d );
		assertTrue( "Point in Polygon Test", polygon.contains( realPoint ) );
	}

	@Test
	public void testRasterizedPolygon()
	{
		// check size
		final RasterizedPolygon rp = new RasterizedPolygon( polygon );
		assertEquals( "Rasterized Polygon Size", 100, rp.size() );

		// check size with cursor
		long sz = 0;
		final Cursor< Void > cursor = rp.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			sz++;
		}
		assertEquals( "Cursor Size", 100, sz );

		// check random access
		final RandomAccess< BoolType > randomAccess = rp.randomAccess();
		randomAccess.setPosition( new int[] { 15, 15 } );
		assertTrue( "Random Access Inside", randomAccess.get().get() );
		randomAccess.setPosition( new int[] { 5, 5 } );
		assertFalse( "Random Access Outside", randomAccess.get().get() );
	}

	@Test
	public void testRasterizedPolygonalChain()
	{
		// check size
		assertEquals( "Rasterized Contour Size", 40, polygonalChain.size() );

		// check size with cursor
		long sz = 0;
		final Cursor< Void > cursor = polygonalChain.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			sz++;
		}
		assertEquals( "Cursor Size", 40, sz );

		// check random access
		final RandomAccess< BoolType > randomAccess = polygonalChain.randomAccess();
		randomAccess.setPosition( new int[] { 12, 10 } );
		assertTrue( "Random Access On Line", randomAccess.get().get() );
		randomAccess.setPosition( new int[] { 5, 5 } );
		assertFalse( "Random Access Not On Line", randomAccess.get().get() );
	}
}
