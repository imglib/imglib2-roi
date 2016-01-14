package net.imglib2.roi.geometric;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IntervalView;
import net.imglib2.view.RandomAccessibleOnRealRandomAccessible;
import net.imglib2.view.Views;

import org.junit.Test;

public class GeometricShapeTest {

	//@Test
	public void testHighDimensionalHypercube() {
		
		AbstractGeometricShape hc = new HyperRectangle(new RealPoint( new double[]{5, 5, 5, 5} ), new double[]{2,2,2,2});
		
		// 9
		// 8
		// 7   #####
		// 6   #####
		// 5   ##x## 
		// 4   #####
		// 3   #####
		// 2
		// 1
		// 0
		//  0123456789

		// should contain:
		assertTrue(hc.contains(new RealPoint(new double[]{3,3,3,3})));
		assertTrue(hc.contains(new RealPoint(new double[]{7,3,3,3})));
		assertTrue(hc.contains(new RealPoint(new double[]{3,7,3,3})));
		assertTrue(hc.contains(new RealPoint(new double[]{3,3,7,3})));
		assertTrue(hc.contains(new RealPoint(new double[]{3,3,3,7})));
		
		// should not contain:
		assertTrue(!hc.contains(new RealPoint(new double[]{2,3,3,3})));
		assertTrue(!hc.contains(new RealPoint(new double[]{3,2,3,3})));
	}
	
	
	@Test
	public void testTwoDimensionalRectangle()
	{
		double angle = 270.0 / 180.0 * Math.PI;
		
		double[][] rotationMatrix = {
						{Math.cos(angle), -Math.sin(angle) },
						{Math.sin(angle) , Math.cos(angle)}
		};

		System.out.println(Arrays.toString(rotationMatrix[0]));
		System.out.println(Arrays.toString(rotationMatrix[1]));
		
		
		AbstractGeometricShape hc = new HyperRectangle(new RealPoint( new double[]{4.5, 4.5} ), new double[]{2,3}, rotationMatrix);
		
		long[] minmax = new long[] { 0,0,10,10 };
		Interval interval = Intervals.createMinMax(minmax);
		System.out.println("min/max y: " + hc.realMin(0) + "/" + hc.realMax(0));
		System.out.println(getGeometricShapeAsAsciiArt(hc));

		// shift it by 0.5 pixel to the right
		hc.move(0.5, 0);
		System.out.println("min/max y: " + hc.realMin(0) + "/" + hc.realMax(0));
		
		System.out.println(getGeometricShapeAsAsciiArt(hc));
		
		
		
		
		RealRandomAccess<BoolType> rra = hc.realRandomAccess();
		RandomAccessible<BoolType> ra = Views.raster(hc);
		RandomAccessibleInterval<BoolType> rai = Views.interval(ra, interval);
	}

	@Test
	public void testHyperCubeWithoutVolume()
	{
		HyperRectangle hc = new HyperRectangle(new RealPoint( new double[]{5, 5} ), new double[]{0,0});
		String result = getGeometricShapeAsAsciiArt(hc);
		String reference =  " _ _ _ _ _\n" + 
							" _ _ _ _ _\n" + 
							" _ _ # _ _\n" + 
							" _ _ _ _ _\n" + 
							" _ _ _ _ _\n";

		System.out.println(result);
		assertTrue(reference.compareTo(result) == 0);
		System.out.println("\n");
	}
	
	@Test
	public void testHyperEllipsoid()
	{
		HyperEllipsoid hc = new HyperEllipsoid(new RealPoint( new double[]{5, 5} ), new double[]{0,0});
		String result = getGeometricShapeAsAsciiArt(hc);
		String reference =  " _ _ _ _ _\n" + 
							" _ _ _ _ _\n" +
							" _ _ _ _ _\n" +
							" _ _ _ _ _\n" +
							" _ _ _ _ _\n";

		System.out.println(result);
		assertTrue(reference.compareTo(result) == 0);
		System.out.println("\n");

		hc = new HyperEllipsoid(new RealPoint( new double[]{10, 10} ), new double[]{8,8});
		result = getGeometricShapeAsAsciiArt(hc);
		reference = " _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ # _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ # # # # # # # _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ # # # # # # # # # # # _ _ _ _ _\n" +
					" _ _ _ _ # # # # # # # # # # # # # _ _ _ _\n" +
					" _ _ _ _ # # # # # # # # # # # # # _ _ _ _\n" +
					" _ _ _ # # # # # # # # # # # # # # # _ _ _\n" +
					" _ _ _ # # # # # # # # # # # # # # # _ _ _\n" +
					" _ _ _ # # # # # # # # # # # # # # # _ _ _\n" +
					" _ _ # # # # # # # # # # # # # # # # # _ _\n" +
					" _ _ _ # # # # # # # # # # # # # # # _ _ _\n" +
					" _ _ _ # # # # # # # # # # # # # # # _ _ _\n" +
					" _ _ _ # # # # # # # # # # # # # # # _ _ _\n" +
					" _ _ _ _ # # # # # # # # # # # # # _ _ _ _\n" +
					" _ _ _ _ # # # # # # # # # # # # # _ _ _ _\n" +
					" _ _ _ _ _ # # # # # # # # # # # _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ # # # # # # # _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ # _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n";
		System.out.println(result);
		assertTrue(reference.compareTo(result) == 0);
		System.out.println("\n");
		
		
		hc = new HyperEllipsoid(new RealPoint( new double[]{10, 10} ), new double[]{0,3});
		result = getGeometricShapeAsAsciiArt(hc);
		reference = " _ _ _ _ _\n" +
					" _ _ _ _ _\n" +
					" _ _ _ _ _\n" +
					" _ _ _ _ _\n" +
					" _ _ _ _ _\n" +
					" _ _ _ _ _\n" +
					" _ _ _ _ _\n" +
					" _ _ _ _ _\n" +
					" _ _ _ _ _\n" +
					" _ _ _ _ _\n" +
					" _ _ _ _ _\n";
		System.out.println(result);
		assertTrue(reference.compareTo(result) == 0);
		System.out.println("\n");
		
		
		hc = new HyperEllipsoid(new RealPoint( new double[]{10, 10} ), new double[]{8,8}, new double[][]{{1,0},{0,1}}, 1.0);
		result = getGeometricShapeAsAsciiArt(hc);
		reference = " _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ # _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ # # # _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ # # # # # _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ # # # # # # # _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ # # # # # # # # # _ _ _ _ _ _\n" +
					" _ _ _ _ _ # # # # # # # # # # # _ _ _ _ _\n" +
					" _ _ _ _ # # # # # # # # # # # # # _ _ _ _\n" +
					" _ _ _ # # # # # # # # # # # # # # # _ _ _\n" +
					" _ _ # # # # # # # # # # # # # # # # # _ _\n" +
					" _ _ _ # # # # # # # # # # # # # # # _ _ _\n" +
					" _ _ _ _ # # # # # # # # # # # # # _ _ _ _\n" +
					" _ _ _ _ _ # # # # # # # # # # # _ _ _ _ _\n" +
					" _ _ _ _ _ _ # # # # # # # # # _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ # # # # # # # _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ # # # # # _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ # # # _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ # _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n";
		
		System.out.println(result);
		assertTrue(reference.compareTo(result) == 0);
		System.out.println("\n");

		double angle = Math.PI * 45.0 / 180.0;
		hc = new HyperEllipsoid(new RealPoint( new double[]{10, 10} ), new double[]{4,4}, new double[][]{{Math.cos(angle),Math.sin(angle)},{-Math.sin(angle),Math.cos(angle)}}, 0.5);
		result = getGeometricShapeAsAsciiArt(hc);
		reference = " _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ # _ _ _ # _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ # # # _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ # # # _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ # # # _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ # _ _ _ # _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n" +
					" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n";
		System.out.println(result);
		assertTrue(reference.compareTo(result) == 0);
		System.out.println("\n");
	}
	
	private String getGeometricShapeAsAsciiArt(AbstractGeometricShape ags)
	{
		//System.out.println("min/max x: " + ags.realMin(0) + "/" + ags.realMax(0));
		//System.out.println("min/max y: " + ags.realMin(1) + "/" + ags.realMax(1));
		
		String res = "";
		for (int x = (int) (ags.realMin(1)) - 2; x <= (int)ags.realMax(1) + 2; x++)
		{
			for (int y = (int) (ags.realMin(0)) - 2; y <= (int)ags.realMax(0) + 2; y++)
			{
				if (ags.contains(new RealPoint(new double[]{x, y})))
				{
					res += " #";
				}
				else
				{
					res += " _";
				}
			}
			res += "\n";
		}
		return res;
	}
	
	public static void main(final String... args) throws IOException {
		new GeometricShapeTest().testHyperEllipsoid();
		new GeometricShapeTest().testHyperCubeWithoutVolume();
		new GeometricShapeTest().testTwoDimensionalRectangle();
		
	}
}
