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
package net.imglib2.roi.geom.real;

import java.util.Collection;
import java.util.HashMap;

import net.imglib2.AbstractRealInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.geom.GeomMaths;
import net.imglib2.util.Intervals;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * {@link RealPointCollection} backed by a {@code HashMap}.
 *
 * @author Alison Walter
 */
public class DefaultWritableRealPointCollection< L extends RealLocalizable > extends AbstractRealInterval implements WritableRealPointCollection< L >
{
	private final HashMap< TDoubleArrayList, L > points;

	/**
	 * Creates a point collection which includes points in the given
	 * {@code HashMap}.
	 *
	 * @param points
	 *            points to include in the collection, the first point
	 *            determines the dimensionality of the collection. The keys in
	 *            the map should be {@code TDoubleArrayList}s which correspond
	 *            to the position of the points.
	 */
	public DefaultWritableRealPointCollection( final HashMap< TDoubleArrayList, L > points )
	{
		super( GeomMaths.getBoundsReal( points.values() ) );
		this.points = points;
	}

	/**
	 * Creates a point collection which includes points in the given
	 * {@code Collection}.
	 *
	 * @param points
	 *            points to include in the collection, the first point
	 *            determines the dimensionality of the collection
	 */
	public DefaultWritableRealPointCollection( final Collection< L > points )
	{
		this( createHashMap( points ) );
	}

	@Override
	public boolean test( final RealLocalizable l )
	{
		if ( Intervals.contains( this, l ) )
		{
			double bestDistance = Double.POSITIVE_INFINITY;
			for ( final L pt : points.values() )
			{
				final double distance = squareDistance( pt, l );
				if ( distance < bestDistance )
					bestDistance = distance;
			}

			return bestDistance <= 0;
		}
		return false;
	}

	@Override
	public Iterable< L > points()
	{
		return points.values();
	}

	@Override
	public void addPoint( final L point )
	{
		if ( point.numDimensions() != n )
			throw new IllegalArgumentException( "Point must have " + n + " dimensions" );

		final double[] l = new double[ point.numDimensions() ];
		point.localize( l );
		points.put( new TDoubleArrayList( l ), point );

		// Update bounds.
		for ( int d = 0; d < numDimensions(); d++ )
		{
			if ( l[ d ] > max[ d ] )
				max[ d ] = l[ d ];
			if ( l[ d ] < min[ d ] )
				min[ d ] = l[ d ];
		}
	}

	/**
	 * Removes the given point from the set, if the point is found in the set.
	 *
	 * @param point
	 *            point to be removed, it must have the same hash as a point in
	 *            the set in order to be removed
	 */
	@Override
	public void removePoint( final L point )
	{
		final double[] l = new double[ point.numDimensions() ];
		point.localize( l );
		points.remove( new TDoubleArrayList( l ) );

		updateMinMax();
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof RealPointCollection ) )
			return false;

		final RealPointCollection< ? extends RealLocalizable > rpc = ( RealPointCollection< ? > ) obj;
		if ( n != rpc.numDimensions() || boundaryType() != rpc.boundaryType() )
			return false;

		int count = 0;
		for ( final RealLocalizable l : rpc.points() )
		{
			final double[] t = new double[ l.numDimensions() ];
			l.localize( t );
			if ( points.get( new TDoubleArrayList( t ) ) == null )
				return false;
			count++;
		}
		return count == points.size();
	}

	@Override
	public int hashCode()
	{
		int result = 71;
		for ( final L l : points.values() )
			for ( int d = 0; d < l.numDimensions(); d++ )
				result += 3 * l.getDoublePosition( d );
		return result;
	}

	// -- Helper methods --

	private double squareDistance( final L ptOne, final RealLocalizable ptTwo )
	{
		double distance = 0;
		for ( int i = 0; i < n; i++ )
			distance += ( ptOne.getDoublePosition( i ) - ptTwo.getDoublePosition( i ) ) * ( ptOne.getDoublePosition( i ) - ptTwo.getDoublePosition( i ) );
		return distance;
	}

	private static < L extends RealLocalizable > HashMap< TDoubleArrayList, L > createHashMap( final Collection< L > points )
	{
		final HashMap< TDoubleArrayList, L > map = new HashMap<>();

		for ( final L p : points )
		{
			final double[] l = new double[ p.numDimensions() ];
			p.localize( l );
			map.put( new TDoubleArrayList( l ), p );
		}
		return map;
	}

	private void updateMinMax()
	{
		final RealInterval interval = GeomMaths.getBoundsReal( points.values() );
		interval.realMin( min );
		interval.realMax( max );
	}
}
