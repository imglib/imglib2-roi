package net.imglib2.troi.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.Bounds.RealIntervalOrEmpty;
import net.imglib2.troi.Operators;
import net.imglib2.troi.RealMask;
import net.imglib2.troi.RealMaskRealInterval;
import net.imglib2.troi.composite.BinaryCompositeMaskPredicate;
import net.imglib2.troi.composite.DefaultBinaryCompositeRealMaskRealInterval;
import net.imglib2.troi.composite.UnaryCompositeMaskPredicate;
import net.imglib2.troi.geom.real.Box;
import net.imglib2.troi.geom.real.ClosedBox;
import net.imglib2.troi.geom.real.ClosedEllipsoid;
import net.imglib2.troi.geom.real.ClosedSphere;
import net.imglib2.troi.geom.real.DefaultPolygon2D;
import net.imglib2.troi.geom.real.Ellipsoid;
import net.imglib2.troi.geom.real.OpenBox;
import net.imglib2.troi.geom.real.OpenEllipsoid;
import net.imglib2.troi.geom.real.OpenSphere;
import net.imglib2.troi.geom.real.Polygon2D;
import net.imglib2.troi.geom.real.Sphere;

import org.junit.Test;

/**
 * Tests for {@link Operators}.
 *
 * @author Alison Walter
 */
public class OperatorsTest
{

	// -- And --

	@Test
	public void testBoundedAndBounded()
	{
		final Box< RealPoint > b1 = new ClosedBox( new double[] { 1, 3 }, new double[] { 7, 10 } );
		final Box< RealPoint > b2 = new OpenBox( new double[] { 3, 3 }, new double[] { 12, 13 } );
		final RealMaskRealInterval rm = b1.and( b2 );

		assertEquals( rm.numDimensions(), 2 );
		assertTrue( rm.test( new RealPoint( new double[] { 4, 5 } ) ) );
		// b1 test boundary points
		assertTrue( rm.test( new RealPoint( new double[] { 7, 10 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 3.1, 9.2 } ) ) );

		// b2 doesn't contain boundary points
		assertFalse( rm.test( new RealPoint( new double[] { 3, 3 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 100, 1 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 5, 3 } ) ) );

		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );

		assertTrue( rm instanceof DefaultBinaryCompositeRealMaskRealInterval );
		final DefaultBinaryCompositeRealMaskRealInterval bc = ( DefaultBinaryCompositeRealMaskRealInterval ) rm;
		assertFalse( bc.isEmpty() );
		assertEquals( bc.realMax( 0 ), 7, 0 );
		assertEquals( bc.realMax( 1 ), 10, 0 );
		assertEquals( bc.realMin( 0 ), 3, 0 );
		assertEquals( bc.realMin( 1 ), 3, 0 );
	}

	@Test
	public void testBoundedAndUnbounded()
	{
		final Box< RealPoint > b1 = new ClosedBox( new double[] { 1, 3 }, new double[] { 7, 10 } );
		final Box< RealPoint > b2 = new OpenBox( new double[] { 3, 3 }, new double[] { 12, 13 } );
		final RealMaskRealInterval rm = b1.and( b2.negate() );

		assertTrue( rm.boundaryType() == BoundaryType.CLOSED );
		assertEquals( rm.numDimensions(), 2 );
		assertFalse( ( ( RealIntervalOrEmpty ) rm ).isEmpty() );

		// True for x: [1, 3] y: [3, 10] AND x: [1,7] y: 3
		assertTrue( rm.test( new RealPoint( new double[] { 2, 6.5 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 3, 6.5 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 6, 3 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 6, 3.125 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 4, 7 } ) ) );

		assertEquals( rm.realMax( 0 ), 7, 0 );
		assertEquals( rm.realMax( 1 ), 10, 0 );
		assertEquals( rm.realMin( 0 ), 1, 0 );
		assertEquals( rm.realMin( 1 ), 3, 0 );
	}

	@Test
	public void testUnboundedAndUnbounded()
	{
		final Box< RealPoint > b1 = new ClosedBox( new double[] { 1, 3 }, new double[] { 7, 10 } );
		final Box< RealPoint > b2 = new OpenBox( new double[] { 3, 3 }, new double[] { 12, 13 } );
		final RealMask rm = b1.negate().and( b2.negate() );

		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );
		assertEquals( rm.numDimensions(), 2 );
		assertFalse( rm instanceof RealInterval );

		assertTrue( rm.test( new RealPoint( new double[] { -20, 60.5 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 2.5, 5 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 4, 8 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 5, 10 } ) ) );
	}

	@Test
	public void testAndEmpty()
	{
		final Box< RealPoint > b1 = new ClosedBox( new double[] { 1.25, 0.5 }, new double[] { 3.125, 7.5 } );
		final Box< RealPoint > b2 = new OpenBox( new double[] { 1, 8.5 }, new double[] { 4, 10 } );
		final RealMaskRealInterval rm = b1.and( b2 );

		assertFalse( rm.test( new RealPoint( new double[] { 2, 5 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 2, 9 } ) ) );

		assertTrue( rm instanceof RealIntervalOrEmpty );
		assertTrue( ( ( RealIntervalOrEmpty ) rm ).isEmpty() );
		assertEquals( rm.realMax( 0 ), 3.125, 0 );
		assertEquals( rm.realMax( 1 ), 7.5, 0 );
		assertEquals( rm.realMin( 0 ), 1.25, 0 );
		assertEquals( rm.realMin( 1 ), 8.5, 0 );
	}

	@Test
	public void testAndMovingOperands()
	{
		final Box< RealPoint > b1 = new ClosedBox( new double[] { 5, 7.5 }, new double[] { 12, 20 } );
		final Box< RealPoint > b2 = new ClosedBox( new double[] { 5.5, 10 }, new double[] { 11.25, 30.25 } );
		final RealMaskRealInterval rm = b1.and( b2 );

		assertTrue( rm instanceof RealIntervalOrEmpty );
		final RealIntervalOrEmpty rioe = ( RealIntervalOrEmpty ) rm;

		assertTrue( rm.test( new RealPoint( new double[] { 6, 11 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 7, 22 } ) ) );

		assertFalse( rioe.isEmpty() );
		assertEquals( rioe.realMax( 0 ), 11.25, 0 );
		assertEquals( rioe.realMax( 1 ), 20, 0 );
		assertEquals( rioe.realMin( 0 ), 5.5, 0 );
		assertEquals( rioe.realMin( 1 ), 10, 0 );

		// Move
		b1.center().move( new double[] { 1.5, 3 } );

		assertTrue( rm.test( new RealPoint( new double[] { 7, 22 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 6, 11 } ) ) );

		assertFalse( rioe.isEmpty() );
		assertEquals( rioe.realMax( 0 ), 11.25, 0 );
		assertEquals( rioe.realMax( 1 ), 23, 0 );
		assertEquals( rioe.realMin( 0 ), 6.5, 0 );
		assertEquals( rioe.realMin( 1 ), 10.5, 0 );

		// Make Empty
		b2.center().setPosition( new double[] { 100, 100 } );

		assertFalse( rm.test( new RealPoint( new double[] { 8, 16.5 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 100, 100 } ) ) );

		assertTrue( rioe.isEmpty() );
		assertEquals( rioe.realMax( 0 ), 13.5, 0 );
		assertEquals( rioe.realMax( 1 ), 23, 0 );
		assertEquals( rioe.realMin( 0 ), 97.125, 0 );
		assertEquals( rioe.realMin( 1 ), 89.875, 0 );
	}

	// -- Minus --

	@Test
	public void testBoundedMinusBounded()
	{
		final Box< RealPoint > b1 = new OpenBox( new double[] { 1, 4 }, new double[] { 10, 11 } );
		final Box< RealPoint > b2 = new OpenBox( new double[] { 2, 3 }, new double[] { 9, 16 } );
		final RealMaskRealInterval rm = b1.minus( b2 );

		assertTrue( rm.test( new RealPoint( new double[] { 2, 5 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 1.5, 10 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 9.8, 8 } ) ) );
		// b2 doesn't contain boundary points
		assertTrue( rm.test( new RealPoint( new double[] { 9, 4.1 } ) ) );

		assertFalse( rm.test( new RealPoint( new double[] { 15, 7 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 3, 4 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 6, 7 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 8, 15 } ) ) );

		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );

		assertEquals( b1.realMin( 0 ), rm.realMin( 0 ), 0 );
		assertEquals( b1.realMin( 1 ), rm.realMin( 1 ), 0 );
		assertEquals( b1.realMax( 0 ), rm.realMax( 0 ), 0 );
		assertEquals( b1.realMax( 1 ), rm.realMax( 1 ), 0 );
	}

	@Test
	public void testBoundedMinusUnbounded()
	{
		final Box< RealPoint > b = new ClosedBox( new double[] { 18.25, -6 }, new double[] { 35, 15.5 } );
		final Sphere< RealPoint > s = new ClosedSphere( new double[] { 25, 0 }, 4 );
		final RealMaskRealInterval rm = b.minus( s.negate() );

		assertTrue( rm.boundaryType() == BoundaryType.CLOSED );

		assertTrue( rm.test( new RealPoint( new double[] { 23, -0.5 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 25, 4 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 20, 10 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 120.5, 95 } ) ) );

		assertEquals( b.realMin( 0 ), rm.realMin( 0 ), 0 );
		assertEquals( b.realMin( 1 ), rm.realMin( 1 ), 0 );
		assertEquals( b.realMax( 0 ), rm.realMax( 0 ), 0 );
		assertEquals( b.realMax( 1 ), rm.realMax( 1 ), 0 );
	}

	@Test
	public void testUnboundedMinusBounded()
	{
		final Sphere< RealPoint > s = new ClosedSphere( new double[] { 25, 0 }, 4 );
		final Polygon2D< RealPoint > p = new DefaultPolygon2D( new double[] { 5, 10, 15 }, new double[] { 0, 5, 0 } );
		final RealMask rm = s.negate().minus( p );

		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );
		assertFalse( rm instanceof RealInterval );

		assertTrue( rm.test( new RealPoint( new double[] { 52.25, 6 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 24, -2 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 10, 3 } ) ) );
	}

	@Test
	public void testUnboundedMinusUnbounded()
	{
		final Sphere< RealPoint > s1 = new ClosedSphere( new double[] { 6, 7 }, 2 );
		final Sphere< RealPoint > s2 = new OpenSphere( new double[] { 0, 0 }, 5 );
		final RealMask rm = s1.negate().minus( s2.negate() );

		assertTrue( rm.boundaryType() == BoundaryType.OPEN );
		assertFalse( rm instanceof RealInterval );

		assertTrue( rm.test( new RealPoint( new double[] { -4, 0 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 100, 130 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 6, 7 } ) ) );
	}

	@Test
	public void testMinusMovingOperands()
	{
		final Sphere< RealPoint > s = new ClosedSphere( new double[] { 10, 2 }, 5.5 );
		final Polygon2D< RealPoint > p = new DefaultPolygon2D( new double[] { 5, 10, 15 }, new double[] { 0, 5, 0 } );
		final RealMaskRealInterval rm = s.minus( p );

		assertTrue( rm.test( new RealPoint( new double[] { 14, 1.5 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 10, 2 } ) ) );

		assertEquals( rm.realMin( 0 ), 4.5, 0 );
		assertEquals( rm.realMin( 1 ), -3.5, 0 );
		assertEquals( rm.realMax( 0 ), 15.5, 0 );
		assertEquals( rm.realMax( 1 ), 7.5, 0 );

		// Moving the polygon shouldn't affect the bounds

		for ( int v = 0; v < p.numVertices(); v++ )
		{
			p.vertex( v ).move( new double[] { 0, -5 } );
		}

		assertTrue( rm.test( new RealPoint( new double[] { 10, 2 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 10, -3 } ) ) );

		assertEquals( rm.realMin( 0 ), 4.5, 0 );
		assertEquals( rm.realMin( 1 ), -3.5, 0 );
		assertEquals( rm.realMax( 0 ), 15.5, 0 );
		assertEquals( rm.realMax( 1 ), 7.5, 0 );

		// Moving the sphere does affect the bounds

		s.center().setPosition( new double[] { 26, 3 } );

		assertTrue( rm.test( new RealPoint( new double[] { 26, 3 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 10, -3 } ) ) );

		assertEquals( rm.realMin( 0 ), 20.5, 0 );
		assertEquals( rm.realMin( 1 ), -2.5, 0 );
		assertEquals( rm.realMax( 0 ), 31.5, 0 );
		assertEquals( rm.realMax( 1 ), 8.5, 0 );
	}

	// -- Negate --

	@Test
	public void testNegate()
	{
		final Box< RealPoint > b = new OpenBox( new double[] { 1, 1 }, new double[] { 19, 19 } );
		final RealMask rm = b.negate();

		assertTrue( rm.test( new RealPoint( new double[] { 19, 19 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 111, -4 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 1.1, 2 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 10, 10 } ) ) );

		// There is still a specific behavior at the boundary between inside and
		// outside, but there is no bounding box
		assertTrue( rm.boundaryType() == BoundaryType.CLOSED );
		assertFalse( rm instanceof RealInterval );
	}

	// -- Or --

	@Test
	public void testBoundedOrBounded()
	{
		final Box< RealPoint > b = new ClosedBox( new double[] { 3, 3 }, new double[] { 7, 7 } );
		final Box< RealPoint > b2 = new ClosedBox( new double[] { 4, 4 }, new double[] { 8, 8 } );
		final RealMaskRealInterval rm = b.or( b2 );

		assertTrue( rm.test( new RealPoint( new double[] { 4, 8 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 6, 5 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 7.5, 4.3 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 8, 7 } ) ) );

		assertFalse( rm.test( new RealPoint( new double[] { 3, 8 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 10, 10 } ) ) );

		assertTrue( rm.boundaryType() == BoundaryType.CLOSED );
		assertEquals( 3, rm.realMin( 0 ), 0 );
		assertEquals( 3, rm.realMin( 1 ), 0 );
		assertEquals( 8, rm.realMax( 0 ), 0 );
		assertEquals( 8, rm.realMax( 1 ), 0 );
	}

	@Test
	public void testBoundedOrUnbounded()
	{
		final Box< RealPoint > b = new ClosedBox( new double[] { 3, 3 }, new double[] { 7, 7 } );
		final Box< RealPoint > b2 = new ClosedBox( new double[] { 4, 4 }, new double[] { 8, 8 } );
		final RealMask rm = b.or( b2.negate() );

		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );
		assertFalse( rm instanceof RealInterval );

		assertTrue( rm.test( new RealPoint( new double[] { 6, 6.5 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 1200, -60.25 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 7.5, 5 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 6, 7.25 } ) ) );
	}

	@Test
	public void testUnboundedOrUnbounded()
	{
		final Polygon2D< RealPoint > t = new DefaultPolygon2D( new double[] { -1, 3, 12 }, new double[] { 5, -10, 5 } );
		final Ellipsoid< RealPoint > e = new OpenEllipsoid( new double[] { 3, -5.5 }, new double[] { 2, 3 } );
		final RealMask rm = t.negate().or( e.negate() );

		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );
		assertFalse( rm instanceof RealInterval );

		assertTrue( rm.test( new RealPoint( new double[] { 5, -5.5 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { -1, 4.9 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { -5, 100 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 3, -7.5 } ) ) );
	}

	@Test
	public void testOrMovingOperands()
	{
		final Ellipsoid< RealPoint > e = new OpenEllipsoid( new double[] { 6, 4 }, new double[] { 5, 2 } );
		final Sphere< RealPoint > s = new OpenSphere( new double[] { -2, 4.5 }, 2.5 );
		final RealMaskRealInterval rm = e.or( s );

		assertTrue( rm.boundaryType() == BoundaryType.OPEN );

		assertEquals( -4.5, rm.realMin( 0 ), 0 );
		assertEquals( 2, rm.realMin( 1 ), 0 );
		assertEquals( 11, rm.realMax( 0 ), 0 );
		assertEquals( 7, rm.realMax( 1 ), 0 );

		assertTrue( rm.test( new RealPoint( new double[] { -4.25, 4.5 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 0.5, 4.5 } ) ) );

		s.center().move( new double[] { 1, -0.5 } );

		assertEquals( -3.5, rm.realMin( 0 ), 0 );
		assertEquals( 1.5, rm.realMin( 1 ), 0 );
		assertEquals( 11, rm.realMax( 0 ), 0 );
		assertEquals( 6.5, rm.realMax( 1 ), 0 );

		assertFalse( rm.test( new RealPoint( new double[] { -4.25, 4.5 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 0.5, 4.5 } ) ) );

		e.center().setPosition( new double[] { 90.5, -105 } );

		assertEquals( -3.5, rm.realMin( 0 ), 0 );
		assertEquals( -107, rm.realMin( 1 ), 0 );
		assertEquals( 95.5, rm.realMax( 0 ), 0 );
		assertEquals( 6.5, rm.realMax( 1 ), 0 );

		assertTrue( rm.test( new RealPoint( new double[] { 0.5, 4.5 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 90.25, -104 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 6, 9 } ) ) );
	}

	// -- Transform --

	// -- Xor --

	@Test
	public void testBoundedXorBounded()
	{
		final Box< RealPoint > b1 = new ClosedBox( new double[] { 3, 3 }, new double[] { 10, 10 } );
		final Box< RealPoint > b2 = new ClosedBox( new double[] { 4, 4 }, new double[] { 8, 7 } );
		final RealMaskRealInterval rm = b1.xor( b2 );

		assertTrue( rm.test( new RealPoint( new double[] { 3, 8 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 9, 4 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 5, 8 } ) ) );

		assertFalse( rm.test( new RealPoint( new double[] { 5, 5 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 20, 1 } ) ) );

		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );

		assertEquals( 3, rm.realMin( 0 ), 0 );
		assertEquals( 3, rm.realMin( 1 ), 0 );
		assertEquals( 10, rm.realMax( 0 ), 0 );
		assertEquals( 10, rm.realMax( 1 ), 0 );
	}

	@Test
	public void testBoundedXorUnbounded()
	{
		final Sphere< RealPoint > s1 = new OpenSphere( new double[] { 0, -1.75, 84 }, 2.25 );
		final Sphere< RealPoint > s2 = new OpenSphere( new double[] { 0, -1.75, 84 }, 5 );
		final RealMask rm = s1.xor( s2.negate() );

		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );
		assertFalse( rm instanceof RealInterval );

		assertTrue( rm.test( new RealPoint( new double[] { 2, -1.75, 84 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 451.25, -7981.125, 92 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 0, -1.75, 86.5 } ) ) );
	}

	@Test
	public void testUnboundedXorUnbounded()
	{
		final Sphere< RealPoint > s1 = new OpenSphere( new double[] { 0, -1.75, 84 }, 2.25 );
		final Sphere< RealPoint > s2 = new ClosedSphere( new double[] { 0, -1.75, 84 }, 5 );
		final RealMask rm = s1.negate().xor( s2.negate() );

		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );
		assertFalse( rm instanceof RealInterval );

		assertFalse( rm.test( new RealPoint( new double[] { 2, -1.75, 84 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 451.25, -7981.125, 92 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 0, -1.75, 86.5 } ) ) );
	}

	@Test
	public void testXorMovingOperands()
	{
		final Ellipsoid< RealPoint > e1 = new ClosedEllipsoid( new double[] { 0, 0 }, new double[] { 2.5, 6.25 } );
		final Ellipsoid< RealPoint > e2 = new ClosedEllipsoid( new double[] { 2, 5 }, new double[] { 2.5, 6.25 } );
		final RealMaskRealInterval rm = e1.xor( e2 );

		assertEquals( -2.5, rm.realMin( 0 ), 0 );
		assertEquals( -6.25, rm.realMin( 1 ), 0 );
		assertEquals( 4.5, rm.realMax( 0 ), 0 );
		assertEquals( 11.25, rm.realMax( 1 ), 0 );

		assertTrue( rm.test( new RealPoint( new double[] { 0, -6 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 2, -1 } ) ) );

		e1.center().setPosition( new double[] { 122, 36 } );

		assertEquals( -0.5, rm.realMin( 0 ), 0 );
		assertEquals( -1.25, rm.realMin( 1 ), 0 );
		assertEquals( 124.5, rm.realMax( 0 ), 0 );
		assertEquals( 42.25, rm.realMax( 1 ), 0 );

		assertFalse( rm.test( new RealPoint( new double[] { 0, -6 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 2, -1 } ) ) );

		e2.center().setPosition( new double[] { 120.5, 36 } );

		assertEquals( 118, rm.realMin( 0 ), 0 );
		assertEquals( 29.75, rm.realMin( 1 ), 0 );
		assertEquals( 124.5, rm.realMax( 0 ), 0 );
		assertEquals( 42.25, rm.realMax( 1 ), 0 );

		assertTrue( rm.test( new RealPoint( new double[] { 118, 36 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 124.5, 36 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 122, 36 } ) ) );
	}

	// -- Test Operand/Operation retrieval --

	@Test
	public void testBinaryCompositeMaskPredicate()
	{
		final Box< RealPoint > b1 = new ClosedBox( new double[] { 1, 3 }, new double[] { 7, 10 } );
		final Box< RealPoint > b2 = new OpenBox( new double[] { 3, 3 }, new double[] { 12, 13 } );
		final RealMaskRealInterval rm = b1.and( b2 );

		assertTrue( rm instanceof BinaryCompositeMaskPredicate );
		final BinaryCompositeMaskPredicate< ? > bc = ( BinaryCompositeMaskPredicate< ? > ) rm;
		assertTrue( bc.operand( 0 ) instanceof ClosedBox );
		assertTrue( bc.operand( 1 ) instanceof OpenBox );
		assertTrue( bc.operator() == Operators.AND );
	}

	@Test
	public void testUnaryCompositeMaskPredicate()
	{
		final Box< RealPoint > b = new OpenBox( new double[] { 1, 1 }, new double[] { 19, 19 } );
		final RealMask rm = b.negate();

		assertTrue( rm instanceof UnaryCompositeMaskPredicate );
		assertTrue( ( ( UnaryCompositeMaskPredicate< ? > ) rm ).operator() == Operators.NEGATE );
		assertTrue( ( ( UnaryCompositeMaskPredicate< ? > ) rm ).operands().get( 0 ) instanceof OpenBox );
	}

	// -- Test empty propagation --

	@Test
	public void testAndWithEmpty()
	{
		final Box< RealPoint > b1 = new ClosedBox( new double[] { 0, 0 }, new double[] { 12, 12 } );
		final Box< RealPoint > b2 = new ClosedBox( new double[] { 10, 10 }, new double[] { 12, 12 } );
		final RealMaskRealInterval empty1 = new EmptyMask( 2 );
		final RealMaskRealInterval empty2 = new EmptyMask( 2 );

		// Both Empty
		final RealMaskRealInterval rm1 = empty1.and( empty2 );
		assertTrue( ( ( RealIntervalOrEmpty ) rm1 ).isEmpty() );

		// First Empty
		final RealMaskRealInterval rm2 = empty1.and( b1 );
		assertTrue( ( ( RealIntervalOrEmpty ) rm2 ).isEmpty() );

		// Second Empty
		final RealMaskRealInterval rm3 = b1.and( empty2 );
		assertTrue( ( ( RealIntervalOrEmpty ) rm3 ).isEmpty() );

		// Neither Empty
		final RealMaskRealInterval rm4 = b1.and( b2 );
		assertFalse( ( ( RealIntervalOrEmpty ) rm4 ).isEmpty() );
	}

	@Test
	public void testMinusWithEmpty()
	{
		final Box< RealPoint > b1 = new ClosedBox( new double[] { 0, 0 }, new double[] { 12, 12 } );
		final Box< RealPoint > b2 = new ClosedBox( new double[] { 10, 10 }, new double[] { 12, 12 } );
		final RealMaskRealInterval empty1 = new EmptyMask( 2 );
		final RealMaskRealInterval empty2 = new EmptyMask( 2 );

		// Both Empty
		final RealMaskRealInterval rm1 = empty1.minus( empty2 );
		assertTrue( ( ( RealIntervalOrEmpty ) rm1 ).isEmpty() );

		// First Empty
		final RealMaskRealInterval rm2 = empty1.minus( b1 );
		assertTrue( ( ( RealIntervalOrEmpty ) rm2 ).isEmpty() );

		// Second Empty
		final RealMaskRealInterval rm3 = b1.minus( empty1 );
		assertFalse( ( ( RealIntervalOrEmpty ) rm3 ).isEmpty() );

		// Neither Empty
		final RealMaskRealInterval rm4 = b1.minus( b2 );
		assertFalse( ( ( RealIntervalOrEmpty ) rm4 ).isEmpty() );
	}

	@Test
	public void testOrWithEmpty()
	{
		final Box< RealPoint > b1 = new ClosedBox( new double[] { 0, 0 }, new double[] { 12, 12 } );
		final Box< RealPoint > b2 = new ClosedBox( new double[] { 10, 10 }, new double[] { 12, 12 } );
		final RealMaskRealInterval empty1 = new EmptyMask( 2 );
		final RealMaskRealInterval empty2 = new EmptyMask( 2 );

		// Both Empty
		final RealMaskRealInterval rm1 = empty1.or( empty2 );
		assertTrue( ( ( RealIntervalOrEmpty ) rm1 ).isEmpty() );

		// First Empty
		final RealMaskRealInterval rm2 = empty1.or( b1 );
		assertFalse( ( ( RealIntervalOrEmpty ) rm2 ).isEmpty() );

		// Second Empty
		final RealMaskRealInterval rm3 = b1.or( empty1 );
		assertFalse( ( ( RealIntervalOrEmpty ) rm3 ).isEmpty() );

		// Neither Empty
		final RealMaskRealInterval rm4 = b1.or( b2 );
		assertFalse( ( ( RealIntervalOrEmpty ) rm4 ).isEmpty() );
	}

	@Test
	public void testXorWithEmpty()
	{
		final Box< RealPoint > b1 = new ClosedBox( new double[] { 0, 0 }, new double[] { 12, 12 } );
		final Box< RealPoint > b2 = new ClosedBox( new double[] { 10, 10 }, new double[] { 12, 12 } );
		final RealMaskRealInterval empty1 = new EmptyMask( 2 );
		final RealMaskRealInterval empty2 = new EmptyMask( 2 );

		// Both Empty
		final RealMaskRealInterval rm1 = empty1.xor( empty2 );
		assertTrue( ( ( RealIntervalOrEmpty ) rm1 ).isEmpty() );

		// First Empty
		final RealMaskRealInterval rm2 = empty1.xor( b1 );
		assertFalse( ( ( RealIntervalOrEmpty ) rm2 ).isEmpty() );

		// Second Empty
		final RealMaskRealInterval rm3 = b1.xor( empty1 );
		assertFalse( ( ( RealIntervalOrEmpty ) rm3 ).isEmpty() );

		// Neither Empty
		final RealMaskRealInterval rm4 = b1.xor( b2 );
		assertFalse( ( ( RealIntervalOrEmpty ) rm4 ).isEmpty() );
	}

	// -- Test equals --

	@Test
	public void testSimpleCompositeEquals()
	{
		final Box< RealPoint > b = new ClosedBox( new double[] { 0, 0 }, new double[] { 6, 4 } );
		final Box< RealPoint > b2 = new ClosedBox( new double[] { 0, 0 }, new double[] { 6, 4 } );

		final Sphere< RealPoint > s = new ClosedSphere( new double[] { 6, 4 }, 5 );
		final Sphere< RealPoint > s2 = new ClosedSphere( new double[] { 6, 4 }, 5 );

		final RealMaskRealInterval a = b.and( s );
		final RealMaskRealInterval a2 = b2.and( s2 );
		final RealMaskRealInterval a3 = s.and( b );
		final RealMaskRealInterval o = b.or( s );

		assertTrue( a.equals( a2 ) );

		// order of operations matters
		assertFalse( a.equals( a3 ) );
		assertFalse( a.equals( o ) );
	}

	@Test
	public void testCompositeEquals()
	{
		final Box< RealPoint > cb = new ClosedBox( new double[] { 0, 0 }, new double[] { 6, 4 } );
		final Box< RealPoint > cb2 = new ClosedBox( new double[] { 0, 0 }, new double[] { 6, 4 } );
		final Sphere< RealPoint > cs = new ClosedSphere( new double[] { 6, 4 }, 5 );
		final Sphere< RealPoint > cs2 = new ClosedSphere( new double[] { 6, 4 }, 5 );
		final Ellipsoid< RealPoint > oe = new OpenEllipsoid( new double[] { 10, 10 }, new double[] { 2.5, 7 } );
		final Ellipsoid< RealPoint > oe2 = new OpenEllipsoid( new double[] { 10, 10 }, new double[] { 2.5, 7 } );
		final Box< RealPoint > ob = new OpenBox( new double[] { 7, -5 }, new double[] { 13.5, 0.5 } );
		final Box< RealPoint > ob2 = new OpenBox( new double[] { 7, -5 }, new double[] { 13.5, 0.5 } );

		final RealMask rm = ob.xor( oe.or( cb.and( cs ) ).negate() );
		final RealMask rm2 = ob2.xor( oe2.or( cb2.and( cs2 ) ).negate() );
		final RealMask rm3 = ob2.xor( oe2.or( cb2.xor( cs2 ) ).negate() );
		final RealMask rm4 = ob2.xor( ob2.or( cb2.and( cs2 ) ).negate() );

		assertTrue( rm.equals( rm2 ) );

		assertFalse( rm.equals( rm3 ) );
		assertFalse( rm.equals( rm4 ) );
	}

	// -- Helper classes --

	private static class EmptyMask extends AbstractEuclideanSpace implements RealMaskRealInterval
	{

		public EmptyMask( final int dims )
		{
			super( dims );
		}

		@Override
		public boolean test( final RealLocalizable t )
		{
			return false;
		}

		@Override
		public double realMin( final int d )
		{
			return Double.POSITIVE_INFINITY;
		}

		@Override
		public double realMax( final int d )
		{
			return Double.NEGATIVE_INFINITY;
		}

	}

}
