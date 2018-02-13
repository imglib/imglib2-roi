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

package net.imglib2.roi.geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * Utility class for operations involving geometric shapes.
 *
 * @author Alison Walter
 */
public class GeomMaths
{
	/**
	 * Tests if the point given point is on a line.
	 *
	 * @param endpointOne
	 *            Line segment endpoint
	 * @param endpointTwo
	 *            Line segment endpoint
	 * @param testPoint
	 *            Point to test
	 */
	public static boolean lineContains( final double[] endpointOne, final double[] endpointTwo, final RealLocalizable testPoint, final int dims )
	{
		final double[] directionVector = computeDirectionVector( endpointOne, endpointTwo, dims );
		final double[] testVector = new double[ dims ];
		double projection = 0;

		// compute a vector from point1 to the test point l
		// compute the dot product of this new vector and the direction
		// vector of this line
		for ( int d = 0; d < dims; d++ )
		{
			testVector[ d ] = testPoint.getDoublePosition( d ) - endpointOne[ d ];
			projection += testVector[ d ] * directionVector[ d ];
		}

		final double[] point = new double[ dims ];
		double squaredDistance = 0;

		// Compute the corresponding point on the line nearest to the test
		// point. If this point is greater than or less than either of the
		// endpoints, the point becomes the endpoint.
		// Compute the distance between this point and the test point l
		for ( int d = 0; d < dims; d++ )
		{
			point[ d ] = endpointOne[ d ] + ( projection * directionVector[ d ] );
			if ( point[ d ] > Math.max( endpointOne[ d ], endpointTwo[ d ] ) )
				point[ d ] = Math.max( endpointOne[ d ], endpointTwo[ d ] );
			if ( point[ d ] < Math.min( endpointOne[ d ], endpointTwo[ d ] ) )
				point[ d ] = Math.min( endpointOne[ d ], endpointTwo[ d ] );
			squaredDistance += ( point[ d ] - testPoint.getDoublePosition( d ) ) * ( point[ d ] - testPoint.getDoublePosition( d ) );
		}

		// Needs 1e-15 for double precision errors
		return squaredDistance <= 1e-15;
	}

	/**
	 * Return true if the given point is contained inside the boundary. See:
	 * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
	 */
	public static boolean pnpoly( final TDoubleArrayList x, final TDoubleArrayList y, final RealLocalizable localizable )
	{
		final double xl = localizable.getDoublePosition( 0 );
		final double yl = localizable.getDoublePosition( 1 );

		int i;
		int j;
		boolean result = false;
		for ( i = 0, j = x.size() - 1; i < x.size(); j = i++ )
		{
			final double xj = x.get( j );
			final double yj = y.get( j );

			final double xi = x.get( i );
			final double yi = y.get( i );

			if ( ( yi > yl ) != ( yj > yl ) && ( xl < ( xj - xi ) * ( yl - yi ) / ( yj - yi ) + xi ) )
			{
				result = !result;
			}
		}
		return result;
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

		final ArrayList< Localizable > tmp = new ArrayList<>();
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

	// -- Helper methods --

	/**
	 * Creates a direction vector from pointOne to pointTwo.
	 */
	private static double[] computeDirectionVector( final double[] pointOne, final double[] pointTwo, final int dims )
	{
		final double[] directionVector = new double[ dims ];
		double magnitude = 0;
		for ( int d = 0; d < dims; d++ )
		{
			directionVector[ d ] = pointTwo[ d ] - pointOne[ d ];
			magnitude += directionVector[ d ] * directionVector[ d ];
		}

		magnitude = Math.sqrt( magnitude );

		for ( int d = 0; d < dims; d++ )
		{
			directionVector[ d ] = directionVector[ d ] / magnitude;
		}

		return directionVector;
	}
}
