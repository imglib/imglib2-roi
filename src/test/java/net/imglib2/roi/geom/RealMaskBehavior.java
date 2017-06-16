/**
 *
 */
package net.imglib2.roi.geom;

import net.imglib2.FinalInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.ClosedWritablePolygon2D;
import net.imglib2.roi.geom.real.ClosedWritableSuperEllipsoid;
import net.imglib2.roi.geom.real.OpenWritableBox;
import net.imglib2.roi.geom.real.Polygon2D;
import net.imglib2.roi.geom.real.SuperEllipsoid;
import net.imglib2.roi.mask.real.RealMaskAsRealRandomAccessible;
import net.imglib2.type.logic.BoolType;
import net.imglib2.view.Views;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;

/**
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 *
 */
public class RealMaskBehavior
{
	/* c. elegans */
	final static private double[] x = new double[] { 15.0, 20.0, 56.0, 101.0, 116.0, 128.0, 147.0, 161.0, 175.0, 189.0, 205.0, 225.0, 242.0, 258.0, 274.0, 289.0, 305.0, 314.0, 324.0, 346.0, 360.0, 371.0, 382.0, 397.0, 417.0, 434.0, 455.0, 474.0, 496.0, 522.0, 539.0, 554.0, 569.0, 582.0, 589.0, 593.0, 594.0, 593.0, 591.0, 587.0, 590.0, 592.0, 591.0, 590.0, 585.0, 575.0, 565.0, 538.0, 514.0, 493.0, 475.0, 451.0, 434.0, 418.0, 402.0, 384.0, 367.0, 349.0, 333.0, 319.0, 309.0, 304.0, 302.0, 302.0, 280.0, 268.0, 257.0, 246.0, 234.0, 221.0, 211.0, 203.0, 197.0, 189.0, 172.0, 162.0, 148.0, 128.0, 110.0, 84.0, 56.0, 37.0, 24.0, 14.0, 12.0 };

	final static private double[] y = new double[] { 173.0, 173.0, 152.0, 123.0, 111.0, 98.0, 62.0, 42.0, 26.0, 16.0, 10.0, 7.0, 7.0, 9.0, 15.0, 23.0, 37.0, 47.0, 63.0, 100.0, 121.0, 135.0, 144.0, 148.0, 148.0, 145.0, 138.0, 130.0, 115.0, 97.0, 89.0, 86.0, 84.0, 87.0, 91.0, 96.0, 103.0, 107.0, 111.0, 112.0, 110.0, 106.0, 99.0, 95.0, 91.0, 89.0, 94.0, 120.0, 142.0, 160.0, 172.0, 184.0, 189.0, 192.0, 192.0, 189.0, 183.0, 173.0, 159.0, 141.0, 129.0, 125.0, 122.0, 115.0, 80.0, 67.0, 58.0, 53.0, 52.0, 52.0, 56.0, 62.0, 67.0, 79.0, 106.0, 124.0, 140.0, 155.0, 163.0, 172.0, 181.0, 185.0, 185.0, 181.0, 177.0 };

	public static void test3D()
	{
		final Box rect = new OpenWritableBox(
				new double[] { 10, 20, 30 },
				new double[] { 100, 200, 300 } );

		final AffineTransform3D affine = new AffineTransform3D();
		affine.set(
				1.0, 0.0, 0.0, 50.0,
				0.5, 1.0, 0.0, 0.0,
				0.0, 0.0, 1.0, 0.0 );

		final RealMask affineRect = rect.transform( affine );

		final SuperEllipsoid ellipse = new ClosedWritableSuperEllipsoid(
				new double[] { 50, 100, 150 },
				new double[] { 50, 100, 150 },
				4 );

		final RealMask xorCombined = affineRect.xor( ellipse );

		final RealRandomAccessible< BoolType > rra =
				new RealMaskAsRealRandomAccessible<>( xorCombined, new BoolType() );

		BdvFunctions.show(
				rra,
				new FinalInterval(
						new long[] { -10, -20, -30 },
						new long[] { 300, 400, 500 } ),
				"mask 3D",
				Bdv.options() );
	}

	public static void test2D()
	{
		final Polygon2D polygon = new ClosedWritablePolygon2D(
				x,
				y );

		final RealRandomAccessible< BoolType > rra =
				new RealMaskAsRealRandomAccessible<>( polygon, new BoolType() );

		BdvFunctions.show(
				rra,
				new FinalInterval(
						new long[] { -10, -10 },
						new long[] { 610, 206 } ),
				"mask 2D",
				Bdv.options().is2D() );

		BdvFunctions.show(
				Views.interval(
						Views.raster( rra ),
						new long[] { -10, -10 },
						new long[] { 610, 206 } ),
				"mask 2D",
				Bdv.options().is2D() );
	}

	public static final void main( final String... args )
	{
//		test3D();
		test2D();
	}
}
