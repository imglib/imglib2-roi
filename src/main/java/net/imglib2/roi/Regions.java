/*
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
package net.imglib2.roi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.util.IterableRandomAccessibleRegion;
import net.imglib2.roi.util.SamplingIterableInterval;
import net.imglib2.type.BooleanType;
import net.imglib2.view.Views;

public class Regions
{
	// TODO: make Positionable and Localizable
	// TODO: bind to (respectively sample from) RandomAccessible
	// TODO: out-of-bounds / clipping

	public static < T > IterableInterval< T > sample( final IterableInterval< Void > region, final RandomAccessible< T > img )
	{
		return SamplingIterableInterval.create( region, img );
	}

	public static < B extends BooleanType< B > > IterableRegion< B > iterable( final RandomAccessibleInterval< B > region )
	{
		if ( region instanceof IterableRegion )
			return ( IterableRegion< B > ) region;
		else
			return IterableRandomAccessibleRegion.create( region );
	}

	public static < T extends BooleanType< T > > long countTrue( final RandomAccessibleInterval< T > interval )
	{
		long sum = 0;
		for ( final T t : Views.iterable( interval ) )
			if ( t.get() )
				++sum;
		return sum;
	}

	public static Interval getBounds( final Collection< ? extends Localizable > vertices )
	{
		assert ( vertices.size() != 0 );

		final int numDims = vertices.iterator().next().numDimensions();
		final long[] min = new long[ numDims ];
		Arrays.fill( min, Long.MAX_VALUE );

		final long[] max = new long[ numDims ];
		Arrays.fill( max, Long.MIN_VALUE );

		for ( final Localizable l : vertices )
		{
			for ( int d = 0; d < numDims; d++ )
			{
				final long pos = l.getLongPosition( d );
				if ( pos < min[ d ] )
					min[ d ] = pos;
				if ( pos > max[ d ] )
					max[ d ] = pos;
			}
		}

		return new FinalInterval( min, max );
	}

	public static RealInterval getBoundsReal( final Collection< ? extends RealLocalizable > vertices )
	{
		assert ( vertices.size() != 0 );

		final int numDims = vertices.iterator().next().numDimensions();

		final double[] min = new double[ numDims ];
		Arrays.fill( min, Double.POSITIVE_INFINITY );

		final double[] max = new double[ numDims ];
		Arrays.fill( max, Double.NEGATIVE_INFINITY );

		for ( final RealLocalizable l : vertices )
		{
			for ( int d = 0; d < numDims; d++ )
			{
				final double pos = l.getDoublePosition( d );
				if ( pos < min[ d ] )
					min[ d ] = pos;
				if ( pos > max[ d ] )
					max[ d ] = pos;
			}
		}

		return new FinalRealInterval( min, max );
	}

	/**
	 * Finds the smallest {@link RealInterval} which contains all vertices.
	 *
	 * @param vertices
	 *            A 2D array containing all the vertices, where the first index
	 *            indicates the vertex and the second indicates the dimension.
	 *            So {@code vertices[ 0 ][ 2 ]} is the value of the first vertex
	 *            in the third dimension.
	 * @return A {@link RealInterval} which contains all the vertices
	 */
	public static RealInterval getBoundsReal( final double[][] vertices )
	{
		final int dims = vertices[ 0 ].length;
		final double[] min = new double[ dims ];
		final double[] max = new double[ dims ];

		double maxValue;
		double minValue;
		for ( int i = 0; i < dims; i++ )
		{
			maxValue = vertices[ 0 ][ i ];
			minValue = vertices[ 0 ][ i ];
			for ( int j = 1; j < vertices.length; j++ )
			{
				if ( vertices[ j ][ i ] < minValue )
					minValue = vertices[ j ][ i ];
				if ( vertices[ j ][ i ] > maxValue )
					maxValue = vertices[ j ][ i ];
			}
			min[ i ] = minValue;
			max[ i ] = maxValue;
		}

		return new FinalRealInterval( min, max );
	}

	/**
	 * Finds the smallest {@link RealInterval} which contains all x, y
	 * coordinates. If the x and y arrays are not equal in length the larger
	 * array will be truncated.
	 *
	 * @param x
	 *            x coordinates of the vertices
	 * @param y
	 *            y coordinates of the vertices
	 * @return A {@link RealInterval} which contains all the vertices
	 */
	public static RealInterval getBoundsReal( final double[] x, final double[] y )
	{
		final int l = x.length <= y.length ? x.length : y.length;
		final double[] min = new double[] { x[ 0 ], y[ 0 ] };
		final double[] max = new double[] { x[ 0 ], y[ 0 ] };

		for ( int i = 1; i < l; i++ )
		{
			final double xi = x[ i ];
			final double yi = y[ i ];

			if ( xi < min[ 0 ] )
				min[ 0 ] = xi;
			if ( xi > max[ 0 ] )
				max[ 0 ] = xi;
			if ( yi < min[ 1 ] )
				min[ 1 ] = yi;
			if ( yi > max[ 1 ] )
				max[ 1 ] = yi;
		}
		return new FinalRealInterval( min, max );
	}

	// TODO: bresenham(vertices) assumes closed loop of vertices (first vertex
	// is repeated after last vertex). It would be useful to have a version that
	// doesn't assume that, and a version that just takes 2 points.
	public static List< Localizable > bresenham( final List< ? extends RealLocalizable > vertices )
	{

		assert ( vertices.size() > 1 );
		assert ( vertices.iterator().next().numDimensions() == 2 );

		final ArrayList< Localizable > tmp = new ArrayList< Localizable >();
		for ( int i = 0; i < vertices.size(); i++ )
		{

			long x0 = Math.round( vertices.get( i ).getDoublePosition( 0 ) );
			long y0 = Math.round( vertices.get( i ).getDoublePosition( 1 ) );
			final long x1 = Math.round( vertices.get( ( i + 1 ) % vertices.size() ).getDoublePosition( 0 ) );
			final long y1 = Math.round( vertices.get( ( i + 1 ) % vertices.size() ).getDoublePosition( 1 ) );

			final long dx = Math.abs( x1 - x0 ), sx = x0 < x1 ? 1 : -1;
			final long dy = -Math.abs( y1 - y0 ), sy = y0 < y1 ? 1 : -1;

			long err = dx + dy, e2; /* error value e_xy */

			while ( true )
			{
				tmp.add( new Point( x0, y0 ) );
				if ( x0 == x1 && y0 == y1 )
					break;
				e2 = 2 * err;
				if ( e2 > dy )
				{
					err += dy;
					x0 += sx;
				} /* e_xy+e_x > 0 */
				if ( e2 < dx )
				{
					err += dx;
					y0 += sy;
				} /* e_xy+e_y < 0 */
			}

			// remove last point, because the last point is the identical to the
			// first point of the next edge
			tmp.remove( tmp.size() - 1 );
		}

		return tmp;
	}
}
