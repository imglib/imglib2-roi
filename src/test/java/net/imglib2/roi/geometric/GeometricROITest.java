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
