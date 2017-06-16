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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import net.imglib2.FinalInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.ClosedBox;
import net.imglib2.roi.geom.real.ClosedEllipsoid;
import net.imglib2.roi.geom.real.ClosedSphere;
import net.imglib2.roi.geom.real.ClosedSuperEllipsoid;
import net.imglib2.roi.geom.real.DefaultLine;
import net.imglib2.roi.geom.real.DefaultPointMask;
import net.imglib2.roi.geom.real.DefaultPolygon2D;
import net.imglib2.roi.geom.real.DefaultPolyline;
import net.imglib2.roi.geom.real.DefaultRealPointCollection;
import net.imglib2.roi.geom.real.Ellipsoid;
import net.imglib2.roi.geom.real.Line;
import net.imglib2.roi.geom.real.OpenEllipsoid;
import net.imglib2.roi.geom.real.OpenSphere;
import net.imglib2.roi.geom.real.PointMask;
import net.imglib2.roi.geom.real.Polygon2D;
import net.imglib2.roi.geom.real.Polyline;
import net.imglib2.roi.geom.real.RealPointCollection;
import net.imglib2.roi.geom.real.Sphere;
import net.imglib2.roi.geom.real.SuperEllipsoid;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.Masks;
import net.imglib2.roi.mask.real.MaskRealInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;
import net.imglib2.view.Views;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Display Masks for demo/test purposes.
 *
 * @author Alison Walter
 *
 */
public class Demo
{
	public static void testBox()
	{
		// Creates a 3D box with center: ( 35, 75.125, -9 ) and edge
		// lengths: ( 30, 49.75, 43 )
		// Constructor takes min and max arrays
		final Box b = new ClosedBox( new double[] { 20, 50.25, -30.5 }, new double[] { 50, 100, 12.5 } );

		// Wraps the b as a RealRandomAccessibleRealInterval of BitType
		// Boxes know their bounds in real space, so when wrapped the bounds are
		// preserved
		final RealRandomAccessibleRealInterval< BitType > rrari = Masks.toRRARI( b, new BitType() );

		// Display
		BdvFunctions.show(
				rrari,
				new FinalInterval(
						new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ), ( long ) rrari.realMin( 2 ) },
						new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ), ( long ) rrari.realMax( 2 ) } ),
				"3D Box",
				Bdv.options() );
	}

	public static void testEllipsoid()
	{
		// Creates a 2D ellipse with center: (20.25, 100) and semi-axes: 30, 5.5
		final Ellipsoid e = new OpenEllipsoid( new double[] { 20.25, 100 }, new double[] { 30, 5.5 } );
		final RealRandomAccessibleRealInterval< BoolType > rrari = Masks.toRRARI( e, new BoolType() );

		// Display
		// The left side is slightly clipped. The bounds in the first dimension
		// of the ellipse are (-9.75, 50.25), and the bounds given to BDV are
		// -10, 50. But the displayed bounds are approx. (-9.5 and 50.4)
		BdvFunctions.show(
				rrari,
				new FinalInterval(
						new long[] { ( long ) Math.floor( rrari.realMin( 0 ) ), ( long ) rrari.realMin( 1 ) },
						new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ) } ),
				"2D Ellipsoid",
				Bdv.options().is2D() );
	}

	public static void testLine()
	{
		// Creates a line in 2D space with endpoints (10, 100) and (100, 200)
		final Line l = new DefaultLine( new double[] { 10, 100 }, new double[] { 100, 200 }, true );
		final RealRandomAccessibleRealInterval< BoolType > rrari = Masks.toRRARI( l, new BoolType() );

		// Only lines with slope = 0, 1 display (slope = -1, undefined, etc. do
		// not display)
		BdvFunctions.show(
				rrari,
				new FinalInterval(
						new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ) },
						new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ) } ),
				"Line",
				Bdv.options().is2D() );

		// Unless they're rasterized, but then there's the raster problem ...
		BdvFunctions.show(
				Views.interval( Views.raster( rrari ),
						new FinalInterval(
								new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ) },
								new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ) } ) ),
				"Rasterized Line",
				Bdv.options().is2D() );
	}

	public static void testPointMask()
	{
		// Creates a point at (100.25, 60)
		final PointMask pm = new DefaultPointMask( new double[] { 100.25, 60 } );
		final RealRandomAccessibleRealInterval< BoolType > rrari = Masks.toRRARI( pm, new BoolType() );

		// PointMasks have the same min/max, which results in a BDV with NaN
		// bounds. So, extend the bounds by -/+5.
		BdvFunctions.show(
				rrari,
				new FinalInterval(
						new long[] { ( long ) rrari.realMin( 0 ) - 5, ( long ) rrari.realMin( 1 ) - 5 },
						new long[] { ( long ) rrari.realMax( 0 ) + 5, ( long ) rrari.realMax( 1 ) + 5 } ),
				"Point",
				Bdv.options().is2D() );

		// Since it isn't at an integer coordinate, it doesn't display. If you
		// change it to an integer coordinate it will display when rasterized.
		BdvFunctions.show(
				Views.interval( Views.raster( rrari ),
						new FinalInterval(
								new long[] { ( long ) rrari.realMin( 0 ) - 5, ( long ) rrari.realMin( 1 ) - 5 },
								new long[] { ( long ) rrari.realMax( 0 ) + 5, ( long ) rrari.realMax( 1 ) + 5 } ) ),
				"Rasterized Point",
				Bdv.options().is2D() );
	}

	public static void testPolygon2D()
	{
		// Create a 2D polygon with vertices: (0, 0), (20, 20), (10, 20),
		// (30, 0), (20, 30), (0, 40), (5, 20)
		final Polygon2D p = new DefaultPolygon2D( new double[] { 0, 20, 10, 30, 20, 0, 5 }, new double[] { 0, 20, 20, 0, 30, 40, 20 } );
		final RealRandomAccessibleRealInterval< BitType > rrari = Masks.toRRARI( p, new BitType() );

		BdvFunctions.show(
				rrari,
				new FinalInterval(
						new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ) },
						new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ) } ),
				"Polygon",
				Bdv.options().is2D() );
	}

	public static void testPolyline()
	{
		// Create a polyline in 2D space with vertices: (0, 0), (100, 100),
		// (200, 100), (300, 0), (300, 150), (500, 200)
		final double[][] points = new double[][] { { 0, 0 }, { 100, 100 }, { 200, 100 }, { 300, 0 }, { 300, 150 }, { 500, 200 } };
		final Polyline pl = new DefaultPolyline( points );
		final RealRandomAccessibleRealInterval< BoolType > rrari = Masks.toRRARI( pl, new BoolType() );

		// BDV can only display some of the portions of the polyline
		BdvFunctions.show(
				rrari,
				new FinalInterval(
						new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ) },
						new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ) } ),
				"Polyline",
				Bdv.options().is2D() );

		// unless it is rasterized
		BdvFunctions.show(
				Views.interval( Views.raster( rrari ),
						new FinalInterval(
								new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ) },
								new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ) } ) ),
				"Rasterized Polyline",
				Bdv.options().is2D() );
	}

	public static void testRealPointCollection()
	{
		final HashMap< TDoubleArrayList, RealPoint > points = new HashMap<>();
		final int seed = -1;
		final Random rand = new Random( seed );
		for ( int i = 0; i < 50; i++ )
		{
			final double[] pt = new double[] { rand.nextDouble() * 100 - 50, rand.nextDouble() * 100 - 50 };
			points.put( new TDoubleArrayList( pt ), new RealPoint( pt ) );
		}
		// Ensure some discrete space points
		for ( int i = 0; i < 50; i++ )
		{
			final double[] pt = new double[] { rand.nextInt( 100 ) - 50, rand.nextInt( 100 ) - 50 };
			points.put( new TDoubleArrayList( pt ), new RealPoint( pt ) );
		}
		final RealPointCollection< RealPoint > rpc = new DefaultRealPointCollection<>( points );
		final RealRandomAccessibleRealInterval< BoolType > rrari = Masks.toRRARI( rpc, new BoolType() );

		BdvFunctions.show(
				rrari,
				new FinalInterval(
						new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ) },
						new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ) } ),
				"RealPointCollection",
				Bdv.options().is2D() );

		BdvFunctions.show(
				Views.interval( Views.raster( rrari ),
						new FinalInterval(
								new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ) },
								new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ) } ) ),
				"Rasterized RealPointCollection",
				Bdv.options().is2D() );
	}

	public static void testSphere()
	{
		final Sphere s = new OpenSphere( new double[] { 0, 0 }, 5 );
		final RealRandomAccessibleRealInterval< BoolType > rrari = Masks.toRRARI( s, new BoolType() );

		// Appears clipped at the top?
		BdvFunctions.show(
				rrari,
				new FinalInterval(
						new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ) },
						new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ) } ),
				"Sphere's Interval",
				Bdv.options().is2D() );

		// Checks if the bounds on the sphere were wrong, but the clipping
		// persists even if the correct bounds are manually entered. With the
		// below interval BDV displays -4.5 to 5.5 in the Y dimensions.
//		BdvFunctions.show(
//				rrari,
//				new FinalInterval(
//						new long[] { -5, -5 },
//						new long[] { 5, 5 } ),
//				"Actual Interval",
//				Bdv.options().is2D() );
	}

	public static void testSuperEllipsoid()
	{
		// Creates a 3D super ellipsoid with center (100, 120, 10), semi-axes:
		// 20, 3.5, 6.25, and exponent = 7
		final SuperEllipsoid se = new ClosedSuperEllipsoid( new double[] { 100, 120, 10 }, new double[] { 20, 3.5, 6.25 }, 7 );
		final RealRandomAccessibleRealInterval< BoolType > rrari = Masks.toRRARI( se, new BoolType() );

		BdvFunctions.show(
				rrari,
				new FinalInterval(
						new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ), ( long ) rrari.realMin( 2 ) },
						new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ), ( long ) rrari.realMax( 2 ) } ),
				"SuperEllipsoid",
				Bdv.options() );
	}

	public static void testAnd()
	{
		// Create two Masks
		final Box b = new ClosedBox( new double[] { 10, 10, 10 }, new double[] { 20, 20, 20 } );
		final Sphere s = new ClosedSphere( new double[] { 10.5, 10.5, 10.5 }, 3.5 );

		// Create a new Mask which is the intersection of the two Masks
		final Mask< RealLocalizable > and = Masks.realAnd( b, s );

		// Since both operands knew their bounds, it is possible that the
		// resulting Mask also knows its bounds. These bounds are not guaranteed
		// to represent the minimal bounding box.
		if ( and instanceof MaskRealInterval )
		{
			final MaskRealInterval intervalAnd = ( MaskRealInterval ) and;

			// Wrap the intersection as a RealRandomAccessibleRealInterval
			final RealRandomAccessibleRealInterval< BoolType > rrari = Masks.toRRARI( intervalAnd, new BoolType() );

			BdvFunctions.show(
					rrari,
					new FinalInterval(
							new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ), ( long ) rrari.realMin( 2 ) },
							new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ), ( long ) rrari.realMax( 2 ) } ),
					"Box AND Sphere",
					Bdv.options() );
		}
	}

	public static void testNot()
	{
		// Create a Mask
		final SuperEllipsoid se = new ClosedSuperEllipsoid( new double[] { 11, 11, 11 }, new double[] { 4, 10, 3 }, 1 );

		// Create a new Mask which is !se
		// Not results in an unbounded Mask
		final Mask< RealLocalizable > not = Masks.realNot( se );

		// Wrap the NOT as a RealRandomAccessible
		final RealRandomAccessible< BitType > rra = Masks.toRRA( not, new BitType() );

		BdvFunctions.show(
				rra,
				new FinalInterval(
						new long[] { 0, 0, 0 },
						new long[] { 25, 25, 25 } ),
				"Not super ellipsoid",
				Bdv.options() );
	}

	public static void testOr()
	{
		// Create two Masks
		final Ellipsoid e = new OpenEllipsoid( new double[] { 30.25, 40.5, 51 }, new double[] { 6, 11, 5.25 } );
		final Ellipsoid eTwo = new ClosedEllipsoid( new double[] { 35, 42.25, 49 }, new double[] { 15.5, 3, 9 } );

		// Create a new mask which is the union of e and eTwo. Since it is
		// impossible for an OR to result in empty space, the OR of two bounded
		// Masks is guaranteed to be a bounded Mask. Though, the bounds may not
		// be the minimal bounding box.
		final MaskRealInterval or = Masks.realIntervalOr( e, eTwo );

		// Wrap as a RealRandomAccessibleRealInterval
		final RealRandomAccessibleRealInterval< BitType > rrari = Masks.toRRARI( or, new BitType() );

		BdvFunctions.show(
				rrari,
				new FinalInterval(
						new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ), ( long ) rrari.realMin( 2 ) },
						new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ), ( long ) rrari.realMax( 2 ) } ),
				"Or two ellipsoids",
				Bdv.options() );
	}

	public static void testSubtract()
	{
		// Create two Masks
		final SuperEllipsoid se = new ClosedSuperEllipsoid( new double[] { 0, 0 }, new double[] { 10, 16 }, 0.4 );
		final Polygon2D p = new DefaultPolygon2D( new double[] { -5, 0, 5 }, new double[] { -2, 2, -2 } );

		// Create a new Mask which is se - p
		final Mask< RealLocalizable > sub = Masks.realSubtract( se, p );

		// The subtraction of two bounded Masks is not guaranteed to result in a
		// bounded Mask.
		if ( sub instanceof MaskRealInterval )
		{
			final MaskRealInterval intervalSub = ( MaskRealInterval ) sub;
			final RealRandomAccessibleRealInterval< BoolType > rrari = Masks.toRRARI( intervalSub, new BoolType() );

			BdvFunctions.show(
					rrari,
					new FinalInterval(
							new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ) },
							new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ) } ),
					"Super ellipsoid - triangle",
					Bdv.options().is2D() );
		}
	}

	public static void testTransform()
	{
		// Create shapes
		final Ellipsoid bOne = new ClosedEllipsoid( new double[] { 20, 20 }, new double[] { 40, 5 } );
		final Box bTwo = new ClosedBox( new double[] { -15, -15 }, new double[] { 15, 15 } );
		final Box bThree = new ClosedBox( new double[] { 40, 40 }, new double[] { 50, 50 } );

		// Create transformation matrices
		final AffineTransform2D translate = new AffineTransform2D();
		translate.translate( new double[] { -20, -20 } );
		final AffineTransform2D rotate = new AffineTransform2D();
		rotate.rotate( Math.PI / 4 );

		// Translate bOne to the center
		// Since it was bounded prior to translation, it should result in a Mask
		// which knows its bounds
		final MaskRealInterval transBOne = ( MaskRealInterval ) Masks.affineTransform( bOne, translate.copy() );

		// Or bOne, bTwo, and bThree together
		final MaskRealInterval or = Masks.realIntervalOr( Arrays.asList( transBOne, bTwo, bThree ) );

		// Rotate the or-d Mask
		// Since the Mask is being rotated, it should result in a Mask which
		// knows its bounds
		final MaskRealInterval rotOr = ( MaskRealInterval ) Masks.affineTransform( or, rotate.copy() );

		// Wrap both the Or and the rotated Or as
		// RealRandomAccessibleRealIntervals
		final RealRandomAccessibleRealInterval< BoolType > orRRARI = Masks.toRRARI( or, new BoolType() );
		final RealRandomAccessibleRealInterval< BoolType > rotOrRRARI = Masks.toRRARI( rotOr, new BoolType() );

		// Display
		BdvFunctions.show(
				orRRARI,
				new FinalInterval(
						new long[] { ( long ) orRRARI.realMin( 0 ), ( long ) orRRARI.realMin( 1 ) },
						new long[] { ( long ) orRRARI.realMax( 0 ), ( long ) orRRARI.realMax( 1 ) } ),
				"Or",
				Bdv.options().is2D() );
		BdvFunctions.show(
				rotOrRRARI,
				new FinalInterval(
						new long[] { ( long ) rotOrRRARI.realMin( 0 ), ( long ) rotOrRRARI.realMin( 1 ) },
						new long[] { ( long ) rotOrRRARI.realMax( 0 ), ( long ) rotOrRRARI.realMax( 1 ) } ),
				"Transformed Or",
				Bdv.options().is2D() );
	}

	public static void testXor()
	{
		// Create two Masks
		final Ellipsoid e = new ClosedEllipsoid( new double[] { 10, 10, 10 }, new double[] { 2, 20, 5 } );
		final Sphere s = new ClosedSphere( new double[] { 10, 5, 2 }, 15 );

		// Create a new Mask which is the exclusive or of e and s
		final Mask< RealLocalizable > xor = Masks.realXor( e, s );

		// The exclusive or of two bounded Masks is not guaranteed to result in
		// a bounded Mask.
		if ( xor instanceof MaskRealInterval )
		{
			final MaskRealInterval intervalXor = ( MaskRealInterval ) xor;
			final RealRandomAccessibleRealInterval< BitType > rrari = Masks.toRRARI( intervalXor, new BitType() );

			BdvFunctions.show(
					rrari,
					new FinalInterval(
							new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ), ( long ) rrari.realMin( 2 ) },
							new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ), ( long ) rrari.realMax( 2 ) } ),
					"Ellipsoid xor Sphere",
					Bdv.options() );
		}
	}

	public static void testMultipleOperations()
	{
		// Since operations on Masks result in new Masks, they can be used to
		// build up more complex regions. Note, some of the below operations
		// could result in unbounded Masks but I have left off the checks for
		// succinctness.

		// XOr a polygon and two ellipses, this requires two calls to Xor since
		// there currently isn't an xor which takes a list
		final Polygon2D p = new DefaultPolygon2D( new double[] { 0, 0, 50, 100, 150, 150, 125, 25, 0 }, new double[] { 100, 75, 100, 100, 75, 150, 200, 200, 150 } );
		final Ellipsoid e = new ClosedEllipsoid( new double[] { 45, 140 }, new double[] { 12, 5 } );
		final Ellipsoid eTwo = new ClosedEllipsoid( new double[] { 105, 140 }, new double[] { 12, 5 } );
		final MaskRealInterval xor = ( MaskRealInterval ) Masks.realXor( Masks.realXor( p, e ), eTwo );

		// Or with three operands
		final Ellipsoid eThree = new ClosedEllipsoid( new double[] { 45, 140 }, new double[] { 2.5, 5 } );
		final Ellipsoid eFour = new ClosedEllipsoid( new double[] { 105, 140 }, new double[] { 2.5, 5 } );
		final MaskRealInterval or = Masks.realIntervalOr( Arrays.asList( xor, eThree, eFour ) );

		// Or with six operands then xor two operation results
		final Box b = new ClosedBox( new double[] { 120, 160 }, new double[] { 180, 161 } );
		final Box bTwo = new ClosedBox( new double[] { 118, 167 }, new double[] { 178, 168 } );
		final Box bThree = new ClosedBox( new double[] { 116, 174 }, new double[] { 176, 175 } );
		final Box bFour = new ClosedBox( new double[] { -30, 160 }, new double[] { 30, 161 } );
		final Box bFive = new ClosedBox( new double[] { -28, 167 }, new double[] { 32, 168 } );
		final Box bSix = new ClosedBox( new double[] { -26, 174 }, new double[] { 34, 175 } );
		final MaskRealInterval bOr = Masks.realIntervalOr( Arrays.asList( b, bTwo, bThree, bFour, bFive, bSix ) );
		final MaskRealInterval xorTwo = ( MaskRealInterval ) Masks.realXor( or, bOr );

		// Subtract
		final Polygon2D pTwo = new DefaultPolygon2D( new double[] { 65, 85, 75 }, new double[] { 160, 160, 170 } );
		final MaskRealInterval sub = ( MaskRealInterval ) Masks.realSubtract( xorTwo, pTwo );
		final RealRandomAccessibleRealInterval< BitType > rrari = Masks.toRRARI( sub, new BitType() );

		BdvFunctions.show(
				rrari,
				new FinalInterval(
						new long[] { ( long ) rrari.realMin( 0 ), ( long ) rrari.realMin( 1 ) },
						new long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ) } ),
				"Multiple Operations",
				Bdv.options().is2D() );
	}

	public static final void main( final String... args )
	{
		// -- Shapes --

//		testBox();
//		testEllipsoid();
//		testLine();
//		testPointMask();
//		testPolygon2D();
//		testPolyline();
//		testRealPointCollection();
//		testSphere();
//		testSuperEllipsoid();

		// -- Operations --

		testAnd();
//		testNot();
//		testOr();
//		testSubtract();
//		testTransform();
//		testXor();
//		testMultipleOperations();
	}

}
