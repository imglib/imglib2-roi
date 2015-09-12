package net.imglib2.roi.geometric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.roi.geometric.twod.Polygon;
import net.imglib2.roi.geometric.twod.PolygonalChain;
import net.imglib2.roi.geometric.twod.RasterizedPolygon;
import net.imglib2.roi.geometric.twod.RasterizedPolygonalChain;
import net.imglib2.type.logic.BoolType;

public class GeometricROITest {

	private static Polygon polygon;
	private static PolygonalChain polygonalChain;

	@BeforeClass
	public static void initTest() {

		final List<RealLocalizable> points = new ArrayList<RealLocalizable>();
		points.add(new RealPoint(10, 10));
		points.add(new RealPoint(20, 10));
		points.add(new RealPoint(20, 20));
		points.add(new RealPoint(10, 20));

		polygon = new Polygon(points);
		polygonalChain = new PolygonalChain(points);
	}

	@Test
	public void testPolygon() {
		// check size
		assertEquals("Polygon Size", 4, polygon.vertices().size());

		// check point in polygon test
		final RealPoint realPoint = new RealPoint(15, 15);
		assertTrue("Point in Polygon Test", polygon.contains(realPoint));
	}

	@Test
	public void testRasterizedPolygon() {
		// check size
		final RasterizedPolygon rp = new RasterizedPolygon(polygon);
		assertEquals("Rasterized Polygon Size", 100, rp.size());

		// check size with cursor
		long sz = 0;
		final Cursor<BoolType> cursor = rp.cursor();
		while (cursor.hasNext()) {
			if (cursor.next().get()) {
				sz++;
			}
		}
		assertEquals("Cursor Size", 100, sz);

		// check random access
		final RandomAccess<BoolType> randomAccess = rp.randomAccess();
		randomAccess.setPosition(new int[] { 15, 15 });
		assertTrue("Random Access Inside", randomAccess.get().get());
		randomAccess.setPosition(new int[] { 5, 5 });
		assertFalse("Random Access Outside", randomAccess.get().get());
	}

	@Test
	public void testPolygonalChain() {
		// check size
		assertEquals("Polygonal Chain Size", 4, polygonalChain.vertices().size());

		// check random access
		final RealRandomAccess<BoolType> realRandomAccess = polygonalChain.realRandomAccess();
		realRandomAccess.setPosition(new RealPoint(15, 10));
		assertTrue("Point On Line ", realRandomAccess.get().get());
		realRandomAccess.setPosition(new int[] { 5, 5 });
		assertFalse("Point Not On Line", realRandomAccess.get().get());
	}

	@Test
	public void testRasterizedPolygonalChain() {
		// check size
		final RasterizedPolygonalChain rpc = new RasterizedPolygonalChain(polygonalChain);
		assertEquals("Rasterized Contour Size", 40, rpc.size());

		// check size with cursor
		long sz = 0;
		final Cursor<BoolType> cursor = rpc.cursor();
		while (cursor.hasNext()) {
			if (cursor.next().get()) {
				sz++;
			}
		}
		assertEquals("Cursor Size", 40, sz);

		// check random access
		final RandomAccess<BoolType> randomAccess = rpc.randomAccess();
		randomAccess.setPosition(new int[] { 12, 10 });
		assertTrue("Random Access On Line", randomAccess.get().get());
		randomAccess.setPosition(new int[] { 5, 5 });
		assertFalse("Random Access Not On Line", randomAccess.get().get());
	}
}
