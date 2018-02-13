/*-
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.realtransform.DeformationFieldTransform;
import net.imglib2.realtransform.InvertibleRealTransform;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.Masks;
import net.imglib2.roi.Operators;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.RealMaskRealInterval;
import net.imglib2.roi.composite.BinaryCompositeMaskPredicate;
import net.imglib2.roi.composite.DefaultBinaryCompositeRealMaskRealInterval;
import net.imglib2.roi.composite.UnaryCompositeMaskPredicate;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.ClosedWritableBox;
import net.imglib2.roi.geom.real.ClosedWritableEllipsoid;
import net.imglib2.roi.geom.real.ClosedWritableSphere;
import net.imglib2.roi.geom.real.DefaultWritablePolygon2D;
import net.imglib2.roi.geom.real.Ellipsoid;
import net.imglib2.roi.geom.real.OpenWritableBox;
import net.imglib2.roi.geom.real.OpenWritableEllipsoid;
import net.imglib2.roi.geom.real.OpenWritableSphere;
import net.imglib2.roi.geom.real.Polygon2D;
import net.imglib2.roi.geom.real.Sphere;
import net.imglib2.roi.geom.real.WritableBox;
import net.imglib2.roi.geom.real.WritableEllipsoid;
import net.imglib2.roi.geom.real.WritablePolygon2D;
import net.imglib2.roi.geom.real.WritableSphere;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.ConstantUtils;

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
		final Box b1 = new ClosedWritableBox( new double[] { 1, 3 }, new double[] { 7, 10 } );
		final Box b2 = new OpenWritableBox( new double[] { 3, 3 }, new double[] { 12, 13 } );
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
		final Box b1 = new ClosedWritableBox( new double[] { 1, 3 }, new double[] { 7, 10 } );
		final Box b2 = new OpenWritableBox( new double[] { 3, 3 }, new double[] { 12, 13 } );
		final RealMaskRealInterval rm = b1.and( b2.negate() );

		assertTrue( rm.boundaryType() == BoundaryType.CLOSED );
		assertEquals( rm.numDimensions(), 2 );
		assertFalse( rm.isEmpty() );

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
		final Box b1 = new ClosedWritableBox( new double[] { 1, 3 }, new double[] { 7, 10 } );
		final Box b2 = new OpenWritableBox( new double[] { 3, 3 }, new double[] { 12, 13 } );
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
	public void testAndMovingOperands()
	{
		final WritableBox b1 = new ClosedWritableBox( new double[] { 5, 7.5 }, new double[] { 12, 20 } );
		final WritableBox b2 = new ClosedWritableBox( new double[] { 5.5, 10 }, new double[] { 11.25, 30.25 } );
		final RealMaskRealInterval rm = b1.and( b2 );

		assertTrue( rm.test( new RealPoint( new double[] { 6, 11 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 7, 22 } ) ) );

		assertFalse( rm.isEmpty() );
		assertEquals( rm.realMax( 0 ), 11.25, 0 );
		assertEquals( rm.realMax( 1 ), 20, 0 );
		assertEquals( rm.realMin( 0 ), 5.5, 0 );
		assertEquals( rm.realMin( 1 ), 10, 0 );

		// Move
		b1.center().move( new double[] { 1.5, 3 } );

		assertTrue( rm.test( new RealPoint( new double[] { 7, 22 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 6, 11 } ) ) );

		assertFalse( rm.isEmpty() );
		assertEquals( rm.realMax( 0 ), 11.25, 0 );
		assertEquals( rm.realMax( 1 ), 23, 0 );
		assertEquals( rm.realMin( 0 ), 6.5, 0 );
		assertEquals( rm.realMin( 1 ), 10.5, 0 );

		// Make Empty
		b2.center().setPosition( new double[] { 100, 100 } );

		assertFalse( rm.test( new RealPoint( new double[] { 8, 16.5 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 100, 100 } ) ) );

		assertTrue( rm.isEmpty() );
		assertEquals( rm.realMax( 0 ), 13.5, 0 );
		assertEquals( rm.realMax( 1 ), 23, 0 );
		assertEquals( rm.realMin( 0 ), 97.125, 0 );
		assertEquals( rm.realMin( 1 ), 89.875, 0 );
	}

	@Test
	public void testAndResultingInEmpty()
	{
		final Box b1 = new ClosedWritableBox( new double[] { 1.25, 0.5 }, new double[] { 3.125, 7.5 } );
		final Box b2 = new OpenWritableBox( new double[] { 1, 8.5 }, new double[] { 4, 10 } );
		final RealMaskRealInterval rm = b1.and( b2 );

		assertFalse( rm.test( new RealPoint( new double[] { 2, 5 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 2, 9 } ) ) );

		// Empty since the operands do not overlap
		assertTrue( rm.isEmpty() );
		assertEquals( rm.realMax( 0 ), 3.125, 0 );
		assertEquals( rm.realMax( 1 ), 7.5, 0 );
		assertEquals( rm.realMin( 0 ), 1.25, 0 );
		assertEquals( rm.realMin( 1 ), 8.5, 0 );
	}

	@Test
	public void testAndWithEmpty()
	{
		final Box b1 = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 12, 12 } );
		final Box b2 = new ClosedWritableBox( new double[] { 10, 10 }, new double[] { 12, 12 } );
		final RealMaskRealInterval empty1 = Masks.emptyRealMaskRealInterval( 2 );
		final RealMaskRealInterval empty2 = Masks.emptyRealMaskRealInterval( 2 );

		// Both Empty
		final RealMaskRealInterval rm1 = empty1.and( empty2 );
		assertTrue( rm1.isEmpty() );

		// First Empty
		final RealMaskRealInterval rm2 = empty1.and( b1 );
		assertTrue( rm2.isEmpty() );

		// Second Empty
		final RealMaskRealInterval rm3 = b1.and( empty2 );
		assertTrue( rm3.isEmpty() );

		// Neither Empty
		final RealMaskRealInterval rm4 = b1.and( b2 );
		assertFalse( rm4.isEmpty() );
	}

	@Test
	public void testAndWithAll()
	{
		final Box b1 = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 12, 12 } );
		final Box b2 = new ClosedWritableBox( new double[] { 10, 10 }, new double[] { 12, 12 } );
		final RealMask all1 = Masks.allRealMask( 2 );
		final RealMask all2 = Masks.allRealMask( 2 );

		// Both All
		final RealMask rm1 = all1.and( all2 );
		assertTrue( rm1.isAll() );

		// First All
		final RealMask rm2 = all1.and( b1 );
		assertFalse( rm2.isAll() );

		// Second All
		final RealMask rm3 = b1.and( all1 );
		assertFalse( rm3.isAll() );

		// Neither All
		final RealMaskRealInterval rm4 = b1.and( b2 );
		assertFalse( rm4.isAll() );
	}

	// -- Minus --

	@Test
	public void testBoundedMinusBounded()
	{
		final Box b1 = new OpenWritableBox( new double[] { 1, 4 }, new double[] { 10, 11 } );
		final Box b2 = new OpenWritableBox( new double[] { 2, 3 }, new double[] { 9, 16 } );
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
		final Box b = new ClosedWritableBox( new double[] { 18.25, -6 }, new double[] { 35, 15.5 } );
		final Sphere s = new ClosedWritableSphere( new double[] { 25, 0 }, 4 );
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
		final Sphere s = new ClosedWritableSphere( new double[] { 25, 0 }, 4 );
		final Polygon2D p = new DefaultWritablePolygon2D( new double[] { 5, 10, 15 }, new double[] { 0, 5, 0 } );
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
		final Sphere s1 = new ClosedWritableSphere( new double[] { 6, 7 }, 2 );
		final Sphere s2 = new OpenWritableSphere( new double[] { 0, 0 }, 5 );
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
		final WritableSphere s = new ClosedWritableSphere( new double[] { 10, 2 }, 5.5 );
		final WritablePolygon2D p = new DefaultWritablePolygon2D( new double[] { 5, 10, 15 }, new double[] { 0, 5, 0 } );
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

	@Test
	public void testMinusWithEmpty()
	{
		final Box b1 = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 12, 12 } );
		final Box b2 = new ClosedWritableBox( new double[] { 10, 10 }, new double[] { 12, 12 } );
		final RealMaskRealInterval empty1 = Masks.emptyRealMaskRealInterval( 2 );
		final RealMaskRealInterval empty2 = Masks.emptyRealMaskRealInterval( 2 );

		// Both Empty
		final RealMaskRealInterval rm1 = empty1.minus( empty2 );
		assertTrue( rm1.isEmpty() );

		// First Empty
		final RealMaskRealInterval rm2 = empty1.minus( b1 );
		assertTrue( rm2.isEmpty() );

		// Second Empty
		final RealMaskRealInterval rm3 = b1.minus( empty1 );
		assertFalse( rm3.isEmpty() );

		// Neither Empty
		final RealMaskRealInterval rm4 = b1.minus( b2 );
		assertFalse( rm4.isEmpty() );
	}

	@Test
	public void testMinusWithAll()
	{
		final Box b1 = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 12, 12 } );
		final Box b2 = new ClosedWritableBox( new double[] { 10, 10 }, new double[] { 12, 12 } );
		final RealMask all1 = Masks.allRealMask( 2 );
		final RealMask all2 = Masks.allRealMask( 2 );

		// Both All
		final RealMask rm1 = all1.minus( all2 );
		assertFalse( rm1.isAll() );
		assertTrue( rm1.isEmpty() );
		assertFalse( rm1 instanceof RealInterval );

		// First All
		final RealMask rm2 = all1.minus( b1 );
		assertFalse( rm2.isAll() );
		assertFalse( rm2.isEmpty() );
		assertFalse( rm2 instanceof RealInterval );

		// Second All
		final RealMask rm3 = b1.minus( all1 );
		assertFalse( rm3.isAll() );
		assertTrue( rm3.isEmpty() );
		assertTrue( rm3 instanceof RealInterval );

		assertEquals( ( ( RealInterval ) rm3 ).realMax( 0 ), 12, 0 );
		assertEquals( ( ( RealInterval ) rm3 ).realMax( 1 ), 12, 0 );
		assertEquals( ( ( RealInterval ) rm3 ).realMin( 0 ), 0, 0 );
		assertEquals( ( ( RealInterval ) rm3 ).realMin( 1 ), 0, 0 );

		// Neither All
		final RealMaskRealInterval rm4 = b1.minus( b2 );
		assertFalse( rm4.isAll() );
		assertFalse( rm4.isEmpty() );

		// All minus Empty
		final RealMask rm5 = all1.minus( Masks.emptyRealMaskRealInterval( 2 ) );
		assertTrue( rm5.isAll() );
		assertFalse( rm5.isEmpty() );
	}

	// -- Negate --

	@Test
	public void testNegate()
	{
		final Box b = new OpenWritableBox( new double[] { 1, 1 }, new double[] { 19, 19 } );
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

	@Test
	public void testNegateEmpty()
	{
		final RealMaskRealInterval empty = Masks.emptyRealMaskRealInterval( 2 );
		final RealMask rm = empty.negate();

		assertTrue( rm instanceof UnaryCompositeMaskPredicate );
		assertTrue( rm.isAll() );
		assertFalse( rm.isEmpty() );
	}

	@Test
	public void testNegateAll()
	{
		final RealMask all = Masks.allRealMask( 2 );
		final RealMask rm = all.negate();

		assertTrue( rm instanceof UnaryCompositeMaskPredicate );
		assertFalse( rm.isAll() );
		assertTrue( rm.isEmpty() );
	}

	// -- Or --

	@Test
	public void testBoundedOrBounded()
	{
		final Box b = new ClosedWritableBox( new double[] { 3, 3 }, new double[] { 7, 7 } );
		final Box b2 = new ClosedWritableBox( new double[] { 4, 4 }, new double[] { 8, 8 } );
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
		final Box b = new ClosedWritableBox( new double[] { 3, 3 }, new double[] { 7, 7 } );
		final Box b2 = new ClosedWritableBox( new double[] { 4, 4 }, new double[] { 8, 8 } );
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
		final Polygon2D t = new DefaultWritablePolygon2D( new double[] { -1, 3, 12 }, new double[] { 5, -10, 5 } );
		final Ellipsoid e = new OpenWritableEllipsoid( new double[] { 3, -5.5 }, new double[] { 2, 3 } );
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
		final WritableEllipsoid e = new OpenWritableEllipsoid( new double[] { 6, 4 }, new double[] { 5, 2 } );
		final WritableSphere s = new OpenWritableSphere( new double[] { -2, 4.5 }, 2.5 );
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

	@Test
	public void testOrWithEmpty()
	{
		final Box b1 = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 12, 12 } );
		final Box b2 = new ClosedWritableBox( new double[] { 10, 10 }, new double[] { 12, 12 } );
		final RealMaskRealInterval empty1 = Masks.emptyRealMaskRealInterval( 2 );
		final RealMaskRealInterval empty2 = Masks.emptyRealMaskRealInterval( 2 );

		// Both Empty
		final RealMaskRealInterval rm1 = empty1.or( empty2 );
		assertTrue( rm1.isEmpty() );

		// First Empty
		final RealMaskRealInterval rm2 = empty1.or( b1 );
		assertFalse( rm2.isEmpty() );

		// Second Empty
		final RealMaskRealInterval rm3 = b1.or( empty1 );
		assertFalse( rm3.isEmpty() );

		// Neither Empty
		final RealMaskRealInterval rm4 = b1.or( b2 );
		assertFalse( rm4.isEmpty() );
	}

	@Test
	public void testOrWithAll()
	{
		final Box b1 = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 12, 12 } );
		final Box b2 = new ClosedWritableBox( new double[] { 10, 10 }, new double[] { 12, 12 } );
		final RealMask all1 = Masks.allRealMask( 2 );
		final RealMask all2 = Masks.allRealMask( 2 );

		// Both All
		final RealMask rm1 = all1.or( all2 );
		assertTrue( rm1.isAll() );
		assertFalse( rm1.isEmpty() );

		// First All
		final RealMask rm2 = all1.or( b1 );
		assertTrue( rm2.isAll() );
		assertFalse( rm2.isEmpty() );

		// Second All
		final RealMask rm3 = b1.or( all1 );
		assertTrue( rm3.isAll() );
		assertFalse( rm3.isEmpty() );

		// Neither All
		final RealMaskRealInterval rm4 = b1.or( b2 );
		assertFalse( rm4.isAll() );
		assertFalse( rm4.isEmpty() );
	}

	// -- Transform --

	@Test
	public void test2DRotatedBox()
	{
		final double angle = 45.0 / 180.0 * Math.PI;

		final double[][] rotationMatrix = { { Math.cos( angle ), -Math.sin( angle ) }, { Math.sin( angle ), Math.cos( angle ) } };

		final Box b = new ClosedWritableBox( new double[] { 2.5, 1.5 }, new double[] { 6.5, 7.5 } );
		final AffineGet transformToSource = createAffineRotationMatrix( new double[] { 4.5, 4.5 }, rotationMatrix, 2 );
		final RealMaskRealInterval rm = b.transform( transformToSource );

		assertTrue( rm.boundaryType() == BoundaryType.CLOSED );

		final RealPoint test = new RealPoint( new double[] { 3, 4 } );
		final RealPoint testTrans = new RealPoint( 2 );
		final AffineGet transformFromSource = transformToSource.inverse();
		transformFromSource.apply( test, testTrans );
		assertTrue( b.test( test ) );
		assertTrue( rm.test( testTrans ) );

		test.setPosition( new double[] { 4, 0 } );
		transformFromSource.apply( test, testTrans );
		assertFalse( b.test( test ) );
		assertFalse( rm.test( testTrans ) );

		test.setPosition( new double[] { 6.5, 1.5 } );
		transformFromSource.apply( test, testTrans );
		final double maxZero = testTrans.getDoublePosition( 0 );
		test.setPosition( new double[] { 6.5, 7.5 } );
		transformFromSource.apply( test, testTrans );
		final double maxOne = testTrans.getDoublePosition( 1 );
		test.setPosition( new double[] { 2.5, 7.5 } );
		transformFromSource.apply( test, testTrans );
		final double minZero = testTrans.getDoublePosition( 0 );
		test.setPosition( new double[] { 2.5, 1.5 } );
		transformFromSource.apply( test, testTrans );
		final double minOne = testTrans.getDoublePosition( 1 );

		assertEquals( rm.realMax( 0 ), maxZero, 0 );
		assertEquals( rm.realMax( 1 ), maxOne, 0 );
		assertEquals( rm.realMin( 0 ), minZero, 0 );
		assertEquals( rm.realMin( 1 ), minOne, 0 );
	}

	@Test
	public void testTranslate()
	{
		final WritableSphere s = new OpenWritableSphere( new double[] { -2.5, 6, 80 }, 2 );
		final AffineTransform3D transformFromSource = new AffineTransform3D();
		transformFromSource.translate( new double[] { 5, 6.25, -63 } );
		final AffineTransform3D transformToSource = transformFromSource.inverse();
		final RealMaskRealInterval rm = s.transform( transformToSource );

		assertTrue( rm.boundaryType() == BoundaryType.OPEN );

		final RealPoint test = new RealPoint( new double[] { -2.5, 6, 80 } );
		assertTrue( s.test( test ) );
		assertFalse( rm.test( test ) );

		test.setPosition( new double[] { 2.5, 12.25, 17 } );
		assertFalse( s.test( test ) );
		assertTrue( rm.test( test ) );

		assertEquals( rm.realMax( 0 ), 4.5, 0 );
		assertEquals( rm.realMax( 1 ), 14.25, 0 );
		assertEquals( rm.realMax( 2 ), 19, 0 );
		assertEquals( rm.realMin( 0 ), 0.5, 0 );
		assertEquals( rm.realMin( 1 ), 10.25, 0 );
		assertEquals( rm.realMin( 2 ), 15, 0 );

		// Move s
		s.center().setPosition( new double[] { -10, -0.25, -0.5 } );

		test.setPosition( s.center() );
		assertTrue( s.test( test ) );
		assertFalse( rm.test( test ) );

		test.setPosition( new double[] { -5, 6, -63.5 } );
		assertFalse( s.test( test ) );
		assertTrue( rm.test( test ) );

		assertEquals( rm.realMax( 0 ), -3, 0 );
		assertEquals( rm.realMax( 1 ), 8, 0 );
		assertEquals( rm.realMax( 2 ), -61.5, 0 );
		assertEquals( rm.realMin( 0 ), -7, 0 );
		assertEquals( rm.realMin( 1 ), 4, 0 );
		assertEquals( rm.realMin( 2 ), -65.5, 0 );
	}

	@Test
	public void test3DRotatedBox()
	{
		final double angle = 30.0 / 180.0 * Math.PI;

		final double[][] rotationMatrix = { { Math.cos( angle ), 0, Math.sin( angle ) }, { 0, 1, 0 }, { -Math.sin( angle ), 0, Math.cos( angle ) } };

		final Box b = new ClosedWritableBox( new double[] { 1, 5.75, -4 }, new double[] { 5, 8.25, 6 } );
		final RealMaskRealInterval rm = b.transform( createAffineRotationMatrix( new double[] { 3, 7, 1 }, rotationMatrix, 3 ) );

		// inside both
		assertTrue( b.test( new RealPoint( new double[] { 3.5, 6.1, 2 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 3.5, 6.1, 2 } ) ) );

		// inside original only
		assertTrue( b.test( new RealPoint( new double[] { 4.99, 8, 5.93 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 4.99, 8, 5.93 } ) ) );

		// inside rotated only
		assertFalse( b.test( new RealPoint( new double[] { 7.15374953738, 8, 4.29450524066 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 7.15374953738, 8, 4.29450524066 } ) ) );

		assertTrue( rm.boundaryType() == BoundaryType.CLOSED );

		// Test Interval
		assertEquals( rm.realMin( 0 ), ( 1 - 3 ) * Math.cos( angle ) + ( -4 - 1 ) * Math.sin( angle ) + 3, 1e-15 );
		assertEquals( rm.realMin( 1 ), 5.75, 0 );
		assertEquals( rm.realMin( 2 ), ( 5 - 3 ) * -Math.sin( angle ) + ( -4 - 1 ) * Math.cos( angle ) + 1, 1e-15 );
		assertEquals( rm.realMax( 0 ), ( 5 - 3 ) * Math.cos( angle ) + ( 6 - 1 ) * Math.sin( angle ) + 3, 1e-15 );
		assertEquals( rm.realMax( 1 ), 8.25, 0 );
		assertEquals( rm.realMax( 2 ), ( 1 - 3 ) * -Math.sin( angle ) + ( 6 - 1 ) * Math.cos( angle ) + 1, 1e-15 );
	}

	@Test
	public void test2DShearedBox()
	{
		final Box b = new ClosedWritableBox( new double[] { 1, 3 }, new double[] { 4, 9 } );
		final AffineTransform2D transform = new AffineTransform2D();
		transform.set( 1, 2, 0, 0, 1, 0 );

		final RealMaskRealInterval rm = b.transform( transform.inverse() );

		// inside original only
		assertTrue( b.test( new RealPoint( new double[] { 1, 9 } ) ) );
		assertFalse( rm.test( new RealPoint( new double[] { 1, 9 } ) ) );

		// inside transformed only
		assertFalse( b.test( new RealPoint( new double[] { 22, 9 } ) ) );
		assertTrue( rm.test( new RealPoint( new double[] { 22, 9 } ) ) );

		assertTrue( rm.boundaryType() == BoundaryType.CLOSED );

		// Test Interval
		assertEquals( rm.realMin( 0 ), 7, 0 );
		assertEquals( rm.realMin( 1 ), 3, 0 );
		assertEquals( rm.realMax( 0 ), 22, 0 );
		assertEquals( rm.realMax( 1 ), 9, 0 );
	}

	@Test
	public void testNonInvertibleTransform()
	{
		final Ellipsoid e = new OpenWritableEllipsoid( new double[] { 10, -6.5 }, new double[] { 2.5, 4 } );
		final RandomAccessibleInterval< DoubleType > def = ConstantUtils.constantRandomAccessibleInterval( new DoubleType( -10.0 ), 3, new FinalInterval( 4, 4, 2 ) );
		final DeformationFieldTransform< DoubleType > transformToSource = new DeformationFieldTransform<>( def );
		final RealMask rm = e.transform( transformToSource );

		// Since the transform is not continuous, the boundary behavior may not
		// be maintained
		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );

		final RealPoint test = new RealPoint( new double[] { 10, -6.5 } );
		assertTrue( e.test( test ) );
		assertFalse( rm.test( test ) );

		test.setPosition( new double[] { 20, 3.5 } );
		assertFalse( e.test( test ) );
		assertTrue( rm.test( test ) );

		assertFalse( rm instanceof RealInterval );
	}

	/**
	 * At this time (13.Dec.2017), the only scenario in which the bounds are
	 * preserved is when an AffineGet is used. The AffineGet used here is
	 * artificial and not technically affine. But is sufficient for the purpose
	 * of this test.
	 */
	@Test
	public void testTransformDecreaseDimsandBounded()
	{
		final Box b = new OpenWritableBox( new double[] { 0.5, 1.5, 1 }, new double[] { 8, 7, 6 } );
		final AffineGet transformToSource = new TestTransform( 2 );
		final RealMaskRealInterval rmri = b.transform( transformToSource );

		assertEquals( 3, b.numDimensions() );
		assertEquals( 2, rmri.numDimensions() );

		final double[] mx = new double[ 2 ];
		final double[] mn = new double[ 2 ];
		rmri.realMax( mx );
		rmri.realMin( mn );
		assertEquals( b.realMax( 0 ), mx[ 0 ], 0 );
		assertEquals( b.realMax( 1 ), mx[ 1 ], 0 );
		assertEquals( b.realMin( 0 ), mn[ 0 ], 0 );
		assertEquals( b.realMin( 1 ), mn[ 1 ], 0 );
	}

	@Test
	public void testTransformIncreaseDimsandBounded()
	{
		final Box b = new OpenWritableBox( new double[] { 0.5, 1 }, new double[] { 8, 7 } );
		final AffineGet transformToSource = new TestTransformInverse( 3 );
		final RealMaskRealInterval rmri = b.transform( transformToSource );

		assertEquals( 2, b.numDimensions() );
		assertEquals( 3, rmri.numDimensions() );

		final double[] mx = new double[ 3 ];
		final double[] mn = new double[ 3 ];
		rmri.realMax( mx );
		rmri.realMin( mn );
		assertEquals( b.realMax( 0 ), mx[ 0 ], 0 );
		assertEquals( b.realMax( 1 ), mx[ 1 ], 0 );
		assertEquals( 2, mx[ 2 ], 0 );
		assertEquals( b.realMin( 0 ), mn[ 0 ], 0 );
		assertEquals( b.realMin( 1 ), mn[ 1 ], 0 );
		assertEquals( 2, mn[ 2 ], 0 );
	}

	// -- Xor --

	@Test
	public void testBoundedXorBounded()
	{
		final Box b1 = new ClosedWritableBox( new double[] { 3, 3 }, new double[] { 10, 10 } );
		final Box b2 = new ClosedWritableBox( new double[] { 4, 4 }, new double[] { 8, 7 } );
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
		final Sphere s1 = new OpenWritableSphere( new double[] { 0, -1.75, 84 }, 2.25 );
		final Sphere s2 = new OpenWritableSphere( new double[] { 0, -1.75, 84 }, 5 );
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
		final Sphere s1 = new OpenWritableSphere( new double[] { 0, -1.75, 84 }, 2.25 );
		final Sphere s2 = new ClosedWritableSphere( new double[] { 0, -1.75, 84 }, 5 );
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
		final WritableEllipsoid e1 = new ClosedWritableEllipsoid( new double[] { 0, 0 }, new double[] { 2.5, 6.25 } );
		final WritableEllipsoid e2 = new ClosedWritableEllipsoid( new double[] { 2, 5 }, new double[] { 2.5, 6.25 } );
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

	@Test
	public void testXorWithEmpty()
	{
		final Box b1 = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 12, 12 } );
		final Box b2 = new ClosedWritableBox( new double[] { 10, 10 }, new double[] { 12, 12 } );
		final RealMaskRealInterval empty1 = Masks.emptyRealMaskRealInterval( 2 );
		final RealMaskRealInterval empty2 = Masks.emptyRealMaskRealInterval( 2 );

		// Both Empty
		final RealMaskRealInterval rm1 = empty1.xor( empty2 );
		assertTrue( rm1.isEmpty() );

		// First Empty
		final RealMaskRealInterval rm2 = empty1.xor( b1 );
		assertFalse( rm2.isEmpty() );

		// Second Empty
		final RealMaskRealInterval rm3 = b1.xor( empty1 );
		assertFalse( rm3.isEmpty() );

		// Neither Empty
		final RealMaskRealInterval rm4 = b1.xor( b2 );
		assertFalse( rm4.isEmpty() );

		// Both are all
		final RealMask rm5 = Masks.allRealMask( 2 ).xor( Masks.allRealMask( 2 ) );
		assertTrue( rm5.isEmpty() );
	}

	@Test
	public void testXorWithAll()
	{
		final Box b1 = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 12, 12 } );
		final Box b2 = new ClosedWritableBox( new double[] { 10, 10 }, new double[] { 12, 12 } );
		final RealMask all1 = Masks.allRealMask( 2 );
		final RealMask all2 = Masks.allRealMask( 2 );

		// Both All
		final RealMask rm1 = all1.xor( all2 );
		assertFalse( rm1.isAll() );
		assertTrue( rm1.isEmpty() );

		// First All
		final RealMask rm2 = all1.xor( b1 );
		assertFalse( rm2.isAll() );
		assertFalse( rm2.isEmpty() );

		// Second All
		final RealMask rm3 = b1.xor( all1 );
		assertFalse( rm3.isAll() );
		assertFalse( rm3.isEmpty() );

		// Neither All
		final RealMaskRealInterval rm4 = b1.xor( b2 );
		assertFalse( rm4.isAll() );

		// First all, second empty
		final RealMask rm5 = all1.xor( Masks.emptyRealMask( 2 ) );
		assertTrue( rm5.isAll() );
		assertFalse( rm5.isEmpty() );
	}

	// -- Test Operand/Operation retrieval --

	@Test
	public void testBinaryCompositeMaskPredicate()
	{
		final Box b1 = new ClosedWritableBox( new double[] { 1, 3 }, new double[] { 7, 10 } );
		final Box b2 = new OpenWritableBox( new double[] { 3, 3 }, new double[] { 12, 13 } );
		final RealMaskRealInterval rm = b1.and( b2 );

		assertTrue( rm instanceof BinaryCompositeMaskPredicate );
		final BinaryCompositeMaskPredicate< ? > bc = ( BinaryCompositeMaskPredicate< ? > ) rm;
		assertTrue( bc.operand( 0 ) instanceof ClosedWritableBox );
		assertTrue( bc.operand( 1 ) instanceof OpenWritableBox );
		assertTrue( bc.operator() == Operators.AND );
	}

	@Test
	public void testUnaryCompositeMaskPredicate()
	{
		final Box b = new OpenWritableBox( new double[] { 1, 1 }, new double[] { 19, 19 } );
		final RealMask rm = b.negate();

		assertTrue( rm instanceof UnaryCompositeMaskPredicate );
		assertTrue( ( ( UnaryCompositeMaskPredicate< ? > ) rm ).operator() == Operators.NEGATE );
		assertTrue( ( ( UnaryCompositeMaskPredicate< ? > ) rm ).operands().get( 0 ) instanceof OpenWritableBox );
	}

	@Test
	public void testUnaryCompositeMaskPredicateTransform()
	{
		final Box b = new OpenWritableBox( new double[] { 0, 1 }, new double[] { 12, 19 } );
		final AffineTransform2D t = new AffineTransform2D();
		t.translate( 1, 5 );
		final AffineTransform2D i = t.inverse();
		final RealMaskRealInterval rm = b.transform( i );

		assertTrue( rm instanceof UnaryCompositeMaskPredicate );
		assertTrue( ( ( UnaryCompositeMaskPredicate< ? > ) rm ).operator() instanceof Operators.RealTransformMaskOperator );
		assertTrue( ( ( UnaryCompositeMaskPredicate< ? > ) rm ).operands().get( 0 ) instanceof OpenWritableBox );

		final RealTransform r = ( ( Operators.RealTransformMaskOperator ) ( ( UnaryCompositeMaskPredicate< ? > ) rm ).operator() ).getTransformToSource();
		assertTrue( r instanceof AffineTransform2D );
		final AffineTransform2D ar = ( AffineTransform2D ) r;

		assertEquals( i.get( 0, 0 ), ar.get( 0, 0 ), 0 );
		assertEquals( i.get( 0, 1 ), ar.get( 0, 1 ), 0 );
		assertEquals( i.get( 1, 0 ), ar.get( 1, 0 ), 0 );
		assertEquals( i.get( 1, 1 ), ar.get( 1, 1 ), 0 );
		assertEquals( i.get( 0, 2 ), ar.get( 0, 2 ), 0 );
		assertEquals( i.get( 1, 2 ), ar.get( 1, 2 ), 0 );
	}

	// -- Test equals --

	@Test
	public void testSimpleCompositeEquals()
	{
		final Box b = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 6, 4 } );
		final Box b2 = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 6, 4 } );

		final Sphere s = new ClosedWritableSphere( new double[] { 6, 4 }, 5 );
		final Sphere s2 = new ClosedWritableSphere( new double[] { 6, 4 }, 5 );

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
		final Box cb = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 6, 4 } );
		final Box cb2 = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 6, 4 } );
		final Sphere cs = new ClosedWritableSphere( new double[] { 6, 4 }, 5 );
		final Sphere cs2 = new ClosedWritableSphere( new double[] { 6, 4 }, 5 );
		final Ellipsoid oe = new OpenWritableEllipsoid( new double[] { 10, 10 }, new double[] { 2.5, 7 } );
		final Ellipsoid oe2 = new OpenWritableEllipsoid( new double[] { 10, 10 }, new double[] { 2.5, 7 } );
		final Box ob = new OpenWritableBox( new double[] { 7, -5 }, new double[] { 13.5, 0.5 } );
		final Box ob2 = new OpenWritableBox( new double[] { 7, -5 }, new double[] { 13.5, 0.5 } );

		final RealMask rm = ob.xor( oe.or( cb.and( cs ) ).negate() );
		final RealMask rm2 = ob2.xor( oe2.or( cb2.and( cs2 ) ).negate() );
		final RealMask rm3 = ob2.xor( oe2.or( cb2.xor( cs2 ) ).negate() );
		final RealMask rm4 = ob2.xor( ob2.or( cb2.and( cs2 ) ).negate() );

		assertTrue( rm.equals( rm2 ) );

		assertFalse( rm.equals( rm3 ) );
		assertFalse( rm.equals( rm4 ) );
	}

	@Test
	public void testSimpleCompositeHashCode()
	{
		final Box b = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 6, 4 } );
		final Box b2 = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 6, 4 } );

		final Sphere s = new ClosedWritableSphere( new double[] { 6, 4 }, 5 );
		final Sphere s2 = new ClosedWritableSphere( new double[] { 6, 4 }, 5 );

		final RealMaskRealInterval a = b.and( s );
		final RealMaskRealInterval a2 = b2.and( s2 );
		final RealMaskRealInterval a3 = s.and( b );
		final RealMaskRealInterval o = b.or( s );

		assertEquals( a.hashCode(), a2.hashCode() );

		// order of operations matters
		assertNotEquals( a.hashCode(), a3.hashCode() );
		assertNotEquals( a.hashCode(), o.hashCode() );
	}

	@Test
	public void testCompositeHashCode()
	{
		final Box cb = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 6, 4 } );
		final Box cb2 = new ClosedWritableBox( new double[] { 0, 0 }, new double[] { 6, 4 } );
		final Sphere cs = new ClosedWritableSphere( new double[] { 6, 4 }, 5 );
		final Sphere cs2 = new ClosedWritableSphere( new double[] { 6, 4 }, 5 );
		final Ellipsoid oe = new OpenWritableEllipsoid( new double[] { 10, 10 }, new double[] { 2.5, 7 } );
		final Ellipsoid oe2 = new OpenWritableEllipsoid( new double[] { 10, 10 }, new double[] { 2.5, 7 } );
		final Box ob = new OpenWritableBox( new double[] { 7, -5 }, new double[] { 13.5, 0.5 } );
		final Box ob2 = new OpenWritableBox( new double[] { 7, -5 }, new double[] { 13.5, 0.5 } );

		final RealMask rm = ob.xor( oe.or( cb.and( cs ) ).negate() );
		final RealMask rm2 = ob2.xor( oe2.or( cb2.and( cs2 ) ).negate() );
		final RealMask rm3 = ob2.xor( oe2.or( cb2.xor( cs2 ) ).negate() );
		final RealMask rm4 = ob2.xor( ob2.or( cb2.and( cs2 ) ).negate() );

		assertEquals( rm.hashCode(), rm2.hashCode() );

		assertNotEquals( rm.hashCode(), rm3.hashCode() );
		assertNotEquals( rm.hashCode(), rm4.hashCode() );
	}

	// -- Helper methods --

	private static AffineGet createAffineRotationMatrix( final double[] center, final double[][] rotationMatrix, final int dim )
	{
		assert rotationMatrix.length == dim;
		assert rotationMatrix[ 0 ].length == dim;

		final AffineTransform affine = new AffineTransform( dim );
		final double[][] transform = new double[ dim ][ dim + 1 ];
		assert rotationMatrix[ 0 ].length == dim;

		for ( int i = 0; i < dim; i++ )
		{
			double translate = 0;
			for ( int j = 0; j < dim + 1; j++ )
			{
				if ( i < rotationMatrix.length && j < rotationMatrix[ i ].length )
				{
					transform[ i ][ j ] = rotationMatrix[ i ][ j ];
					translate += transform[ i ][ j ] * -center[ j ];
				}
				if ( j == dim )
				{
					transform[ i ][ j ] = translate;
				}
			}
		}

		for ( int n = 0; n < dim; n++ )
		{
			transform[ n ][ dim ] += center[ n ];
		}

		affine.set( transform );
		return affine.inverse();
	}

	// -- Test class --

	/**
	 * Currently only AffineGet transforms preserve the bounds. So this test
	 * transform must be an AffineGet, even though it isn't actually an affine
	 * transform.
	 */
	// TransformToSource i.e. 2D to 3D
	private final class TestTransform implements AffineGet
	{

		private final int sourceDim;

		public TestTransform( final int sourceDim )
		{
			this.sourceDim = sourceDim;
		}

		@Override
		public void applyInverse( final double[] source, final double[] target )
		{}

		@Override
		public void applyInverse( final float[] source, final float[] target )
		{}

		@Override
		public void applyInverse( final RealPositionable source, final RealLocalizable target )
		{}

		@Override
		public InvertibleRealTransform copy()
		{
			return null;
		}

		@Override
		public int numSourceDimensions()
		{
			return sourceDim;
		}

		@Override
		public int numTargetDimensions()
		{
			return sourceDim + 1;
		}

		@Override
		public void apply( final double[] source, final double[] target )
		{
			for ( int i = 0; i < numSourceDimensions(); i++ )
				target[ i ] = source[ i ];
			target[ numSourceDimensions() ] = 2;
		}

		@Override
		public void apply( final float[] source, final float[] target )
		{
			for ( int i = 0; i < numSourceDimensions(); i++ )
				target[ i ] = source[ i ];
			target[ numSourceDimensions() ] = 2;
		}

		@Override
		public void apply( final RealLocalizable source, final RealPositionable target )
		{
			for ( int i = 0; i < numSourceDimensions(); i++ )
				target.setPosition( source.getDoublePosition( i ), i );
			target.setPosition( 2, numSourceDimensions() );
		}

		@Override
		public int numDimensions()
		{
			return 0;
		}

		@Override
		public double get( final int row, final int column )
		{
			return 0;
		}

		@Override
		public double[] getRowPackedCopy()
		{
			return null;
		}

		@Override
		public RealLocalizable d( final int d )
		{
			return null;
		}

		@Override
		public AffineGet inverse()
		{
			return new TestTransformInverse( numTargetDimensions() );
		}
	}

	// TransformFromSource i.e. 3D to 2D
	private final class TestTransformInverse implements AffineGet
	{

		private final int sourceDim;

		public TestTransformInverse( final int sourceDim )
		{
			this.sourceDim = sourceDim;
		}

		@Override
		public void applyInverse( final double[] source, final double[] target )
		{}

		@Override
		public void applyInverse( final float[] source, final float[] target )
		{}

		@Override
		public void applyInverse( final RealPositionable source, final RealLocalizable target )
		{}

		@Override
		public InvertibleRealTransform copy()
		{
			return null;
		}

		@Override
		public int numSourceDimensions()
		{
			return sourceDim;
		}

		@Override
		public int numTargetDimensions()
		{
			return sourceDim - 1;
		}

		@Override
		public void apply( final double[] source, final double[] target )
		{
			for ( int i = 0; i < numTargetDimensions(); i++ )
				target[ i ] = source[ i ];
		}

		@Override
		public void apply( final float[] source, final float[] target )
		{
			for ( int i = 0; i < numTargetDimensions(); i++ )
				target[ i ] = source[ i ];
		}

		@Override
		public void apply( final RealLocalizable source, final RealPositionable target )
		{
			for ( int i = 0; i < numTargetDimensions(); i++ )
				target.setPosition( source.getDoublePosition( i ), i );
		}

		@Override
		public int numDimensions()
		{
			return 0;
		}

		@Override
		public double get( final int row, final int column )
		{
			return 0;
		}

		@Override
		public double[] getRowPackedCopy()
		{
			return null;
		}

		@Override
		public RealLocalizable d( final int d )
		{
			return null;
		}

		@Override
		public AffineGet inverse()
		{
			return new TestTransform( numTargetDimensions() );
		}
	}

}
