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

package net.imglib2.roi.geom.real;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.neighborsearch.NearestNeighborSearch;
import net.imglib2.neighborsearch.NearestNeighborSearchOnIterableRealInterval;

/**
 * A {@link RealPointCollection} which checks if points are in the collection by
 * performing a {@link NearestNeighborSearch} on the provided
 * {@link IterableRealInterval}.
 *
 * @author Alison Walter
 */
public class NNSRealPointCollection< L extends RealLocalizable > extends AbstractEuclideanSpace implements RealPointCollection< L >
{
	protected IterableRealInterval< L > interval;

	protected NearestNeighborSearch< L > search;

	/**
	 * Creates a {@link RealPointCollection}.
	 *
	 * @param interval
	 *            Contains the points which will be included in this collection.
	 *            This will be used to create a
	 *            {@link NearestNeighborSearchOnIterableRealInterval}.The first
	 *            point determines the dimensionality of the collection.
	 */
	public NNSRealPointCollection( final IterableRealInterval< L > interval )
	{
		this( interval, new NearestNeighborSearchOnIterableRealInterval<>( interval ) );
	}

	/**
	 * Creates a {@link RealPointCollection}.
	 *
	 * @param interval
	 *            Contains the points which will be included in this collection.
	 *            This will be used to create a
	 *            {@link NearestNeighborSearchOnIterableRealInterval}. The first
	 *            point determines the dimensionality of the collection.
	 * @param search
	 *            Will be used to check if a point is contained by the
	 *            collection.
	 *
	 */
	public NNSRealPointCollection( final IterableRealInterval< L > interval, final NearestNeighborSearch< L > search )
	{
		super( interval.numDimensions() );
		this.interval = interval;
		if ( search == null )
			throw new NullPointerException( "search cannot be null" );
		this.search = search;
	}

	@Override
	public boolean test( final RealLocalizable l )
	{
		search.search( l );
		return search.getSquareDistance() <= 0;
	}

	@Override
	public double realMin( final int d )
	{
		return interval.realMax( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		interval.realMin( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		interval.realMin( min );
	}

	@Override
	public double realMax( final int d )
	{
		return interval.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		interval.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		interval.realMax( max );
	}

	@Override
	public Iterable< L > points()
	{
		return interval;
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof RealPointCollection ) )
			return false;

		final RealPointCollection< ? extends RealLocalizable > rpc = ( RealPointCollection< ? > ) obj;
		if ( rpc.numDimensions() != n || boundaryType() != rpc.boundaryType() )
			return false;

		final RealCursor< L > c = interval.cursor();
		for ( final RealLocalizable l : rpc.points() )
		{
			if ( !c.hasNext() || !test( l ) )
				return false;
			c.next();
		}
		return !c.hasNext();
	}

	@Override
	public int hashCode()
	{
		int result = 71;
		for ( RealLocalizable l : interval )
			for ( int d = 0; d < l.numDimensions(); d++ )
				result += 3 * l.getDoublePosition( d );
		return result;
	}
}
