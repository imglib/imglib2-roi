package net.imglib2.roi.geom;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.imglib2.RealPoint;
import net.imglib2.roi.geom.real.DefaultPointMask;
import net.imglib2.roi.geom.real.PointMask;
import net.imglib2.roi.mask.Mask.BoundaryType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PointMaskTest
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testDoubleArrayConstructor()
	{
		final PointMask pt = new DefaultPointMask( new double[] { 10.25, -3, 6, 0.01 } );

		assertTrue( pt.contains( new RealPoint( new double[] { 10.25, -3, 6, 0.01 } ) ) );
		assertFalse( pt.contains( new RealPoint( new double[] { 10.15, -3, 6, 0.02 } ) ) );
		assertArrayEquals( pt.location(), new double[] { 10.25, -3, 6, 0.01 }, 0 );

		assertEquals( pt.boundaryType(), BoundaryType.CLOSED );
	}

	@Test
	public void testRealLocalizableConstructor()
	{
		final PointMask pt = new DefaultPointMask( new RealPoint( new double[] { -12.125, 6, 0 } ) );

		assertTrue( pt.contains( new RealPoint( new double[] { -12.125, 6, 0 } ) ) );
		assertFalse( pt.contains( new RealPoint( new double[] { -12.125, 6.001, 0 } ) ) );
		assertArrayEquals( pt.location(), new double[] { -12.125, 6, 0 }, 0 );

		assertEquals( pt.boundaryType(), BoundaryType.CLOSED );
	}

	@Test
	public void testSetLocation()
	{
		final PointMask pt = new DefaultPointMask( new double[] { 0.5, -7.125 } );

		assertTrue( pt.contains( new RealPoint( new double[] { 0.5, -7.125 } ) ) );
		assertFalse( pt.contains( new RealPoint( new double[] { 12, 64 } ) ) );

		pt.setLocation( new double[] { 12, 64 } );

		assertFalse( pt.contains( new RealPoint( new double[] { 0.5, -7.125 } ) ) );
		assertTrue( pt.contains( new RealPoint( new double[] { 12, 64 } ) ) );
	}

	@Test
	public void testSetLocationTooLong()
	{
		final PointMask pt = new DefaultPointMask( new double[] { 0.5, -7.125 } );

		assertTrue( pt.contains( new RealPoint( new double[] { 0.5, -7.125 } ) ) );
		assertFalse( pt.contains( new RealPoint( new double[] { 12, 64 } ) ) );

		pt.setLocation( new double[] { 12, 64, 11 } );

		assertArrayEquals( pt.location(), new double[] { 12, 64 }, 0 );
		assertFalse( pt.contains( new RealPoint( new double[] { 0.5, -7.125 } ) ) );
		assertTrue( pt.contains( new RealPoint( new double[] { 12, 64 } ) ) );
	}

	@Test
	public void testSetLocationTooShort()
	{
		final PointMask pt = new DefaultPointMask( new double[] { 0.5, -7.125 } );

		assertTrue( pt.contains( new RealPoint( new double[] { 0.5, -7.125 } ) ) );
		assertFalse( pt.contains( new RealPoint( new double[] { -3, 9 } ) ) );

		exception.expect( IllegalArgumentException.class );
		pt.setLocation( new double[] { -3 } );
	}
}
