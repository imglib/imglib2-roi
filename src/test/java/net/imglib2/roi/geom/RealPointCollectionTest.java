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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.RealPoint;
import net.imglib2.roi.geom.real.DefaultRealPointCollection;
import net.imglib2.roi.geom.real.KDTreeRealPointCollection;
import net.imglib2.roi.geom.real.RealPointCollection;
import net.imglib2.roi.geom.real.RealPointSampleListRealPointCollection;
import net.imglib2.roi.mask.Mask.BoundaryType;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RealPointCollectionTest
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static List< RealPoint > points = new ArrayList<>();

	private static final RealPoint testAddPoint = new RealPoint( new double[] { 10.25, 111 } );

	@Before
	public void setup()
	{
		points.clear();
		points.add( new RealPoint( new double[] { 19, 12.1 } ) );
		points.add( new RealPoint( new double[] { 18.125, 0 } ) );
		points.add( new RealPoint( new double[] { 1.25, 0.0625 } ) );
		points.add( new RealPoint( new double[] { -12, 80 } ) );
		points.add( new RealPoint( new double[] { -13, -13 } ) );
		points.add( new RealPoint( new double[] { 200, 3 } ) );
		points.add( new RealPoint( new double[] { 0.03125, 0.00390625 } ) );
		points.add( new RealPoint( new double[] { -0.25, -0.5 } ) );
	}

	@Test
	public void testDefaultRPC()
	{
		final RealPointCollection< RealPoint > rpc = new DefaultRealPointCollection<>( points );

		// all points within region
		for ( int i = 0; i < rpc.numDimensions(); i++ )
		{
			assertTrue( rpc.test( points.get( i ) ) );
		}

		// outside region
		assertFalse( rpc.test( new RealPoint( new double[] { 19 + 10e-14, 12.1 } ) ) );
		assertFalse( rpc.test( new RealPoint( new double[] { 112, 0.25 } ) ) );
		assertFalse( rpc.test( new RealPoint( new double[] { -0.25, 0.5 } ) ) );

		// RealPointCollection characteristics
		final Iterable< RealPoint > pts = rpc.points();
		final Iterator< RealPoint > itr = pts.iterator();
		while ( itr.hasNext() )
			assertTrue( points.contains( itr.next() ) );
		assertTrue( rpc.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testKDTreeRPC()
	{
		final RealPointCollection< RealPoint > rpc = new KDTreeRealPointCollection<>( points );

		// all points within region
		for ( int i = 0; i < rpc.numDimensions(); i++ )
		{
			assertTrue( rpc.test( points.get( i ) ) );
		}

		// outside region
		assertFalse( rpc.test( new RealPoint( new double[] { 19 + 10e-14, 12.1 } ) ) );
		assertFalse( rpc.test( new RealPoint( new double[] { 112, 0.25 } ) ) );
		assertFalse( rpc.test( new RealPoint( new double[] { -0.25, 0.5 } ) ) );

		// RealPointCollection characteristics
		final Iterable< RealPoint > pts = rpc.points();
		final Iterator< RealPoint > itr = pts.iterator();
		while ( itr.hasNext() )
			assertTrue( points.contains( itr.next() ) );
		assertTrue( rpc.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testRealPointSampleListRPC()
	{
		final RealPointCollection< RealPoint > rpc = new RealPointSampleListRealPointCollection<>( points );

		// all points within region
		for ( int i = 0; i < rpc.numDimensions(); i++ )
		{
			assertTrue( rpc.test( points.get( i ) ) );
		}

		// outside region
		assertFalse( rpc.test( new RealPoint( new double[] { 19 + 10e-14, 12.1 } ) ) );
		assertFalse( rpc.test( new RealPoint( new double[] { 112, 0.25 } ) ) );
		assertFalse( rpc.test( new RealPoint( new double[] { -0.25, 0.5 } ) ) );

		// RealPointCollection characteristics
		final Iterable< RealPoint > pts = rpc.points();
		final Iterator< RealPoint > itr = pts.iterator();
		while ( itr.hasNext() )
			assertTrue( points.contains( itr.next() ) );
		assertTrue( rpc.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testDefaultRPCAddPoint()
	{
		final RealPointCollection< RealPoint > rpc = new DefaultRealPointCollection<>( points );

		assertFalse( rpc.test( testAddPoint ) );

		rpc.addPoint( testAddPoint );
		assertTrue( rpc.test( testAddPoint ) );
	}

	@Test
	public void testDefaultRPCRemovePoint()
	{
		final RealPointCollection< RealPoint > rpc = new DefaultRealPointCollection<>( points );

		assertTrue( rpc.test( new RealPoint( new double[] { -13, -13 } ) ) );

		rpc.removePoint( new RealPoint( new double[] { -13, -13 } ) );
		assertFalse( rpc.test( new RealPoint( new double[] { -13, -13 } ) ) );
	}

	@Test
	public void testKDTreeRPCAddPoint()
	{
		final RealPointCollection< RealPoint > rpc = new KDTreeRealPointCollection<>( points );

		exception.expect( UnsupportedOperationException.class );
		rpc.addPoint( new RealPoint( new double[] { 6, 2 } ) );
	}

	@Test
	public void testKDTreeRPCRemovePoint()
	{
		final RealPointCollection< RealPoint > rpc = new KDTreeRealPointCollection<>( points );

		exception.expect( UnsupportedOperationException.class );
		rpc.removePoint( new RealPoint( new double[] { 0.03125, 0.00390625 } ) );
	}

	@Test
	public void testRealPointSampleListRPCAddPoint()
	{
		final RealPointCollection< RealPoint > rpc = new RealPointSampleListRealPointCollection<>( points );

		assertFalse( rpc.test( testAddPoint ) );

		rpc.addPoint( testAddPoint );
		assertTrue( rpc.test( testAddPoint ) );
	}

	@Test
	public void testRealPointSampleListRPCRemovePoint()
	{
		final RealPointCollection< RealPoint > rpc = new RealPointSampleListRealPointCollection<>( points );

		exception.expect( UnsupportedOperationException.class );
		rpc.removePoint( new RealPoint( new double[] { 0.03125, 0.00390625 } ) );
	}

	@Test
	public void testDefaultRPCBounds()
	{
		final RealPointCollection< RealPoint > rpc = new DefaultRealPointCollection<>( points );
		final double[] max = new double[] { 200, 80 };
		double[] min = new double[] { -13, -13 };
		final double[] rpcMin = new double[ 2 ];
		final double[] rpcMax = new double[ 2 ];
		rpc.realMin( rpcMin );
		rpc.realMax( rpcMax );

		assertArrayEquals( min, rpcMin, 0 );
		assertArrayEquals( max, rpcMax, 0 );

		// Mutate RPC
		rpc.removePoint( new RealPoint( new double[] { -13, -13 } ) );
		min = new double[] { -12, -0.5 };
		rpc.realMin( rpcMin );
		rpc.realMax( rpcMax );
		assertArrayEquals( min, rpcMin, 0 );
		assertArrayEquals( max, rpcMax, 0 );

		rpc.addPoint( new RealPoint( new double[] { 1024.25, -17.125 } ) );
		max[ 0 ] = 1024.25;
		min[ 1 ] = -17.125;
		rpc.realMin( rpcMin );
		rpc.realMax( rpcMax );
		assertArrayEquals( min, rpcMin, 0 );
		assertArrayEquals( max, rpcMax, 0 );
	}

	@Test
	public void testKDTreeRPCBounds()
	{
		final RealPointCollection< RealPoint > rpc = new KDTreeRealPointCollection<>( points );
		final double[] max = new double[] { 200, 80 };
		final double[] min = new double[] { -13, -13 };
		final double[] rpcMin = new double[ 2 ];
		final double[] rpcMax = new double[ 2 ];
		rpc.realMin( rpcMin );
		rpc.realMax( rpcMax );

		assertArrayEquals( min, rpcMin, 0 );
		assertArrayEquals( max, rpcMax, 0 );
	}

	@Test
	public void testRealPointSampleListRPCBounds()
	{
		final RealPointCollection< RealPoint > rpc = new RealPointSampleListRealPointCollection<>( points );
		final double[] max = new double[] { 200, 80 };
		final double[] min = new double[] { -13, -13 };
		final double[] rpcMin = new double[ 2 ];
		final double[] rpcMax = new double[ 2 ];
		rpc.realMin( rpcMin );
		rpc.realMax( rpcMax );

		assertArrayEquals( min, rpcMin, 0 );
		assertArrayEquals( max, rpcMax, 0 );

		// Mutate RPC
		rpc.addPoint( new RealPoint( new double[] { -16.5, 82 } ) );
		min[ 0 ] = -16.5;
		max[ 1 ] = 82;
		rpc.realMin( rpcMin );
		rpc.realMax( rpcMax );
		assertArrayEquals( min, rpcMin, 0 );
		assertArrayEquals( max, rpcMax, 0 );
	}
}
