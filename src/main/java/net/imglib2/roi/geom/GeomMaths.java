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
