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

package net.imglib2.roi.geom.real;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.IterableRealInterval;
import net.imglib2.RealLocalizable;
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
	 *            {@link NearestNeighborSearchOnIterableRealInterval}.
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
	 * @param search
	 *            Will be used to check if a point is contained by the
	 *            collection.
	 */
	public NNSRealPointCollection( final IterableRealInterval< L > interval, final NearestNeighborSearch< L > search )
	{
		super( interval.numDimensions() );
		this.interval = interval;
		this.search = search;
	}

	@Override
	public boolean contains( final RealLocalizable l )
	{
		search.search( l );
		return search.getSquareDistance() <= 0;
	}

	@Override
	public Iterable< L > points()
	{
		return interval;
	}
}
