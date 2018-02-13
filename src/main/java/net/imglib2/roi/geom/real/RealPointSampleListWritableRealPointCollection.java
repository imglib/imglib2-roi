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

import java.util.Collection;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.neighborsearch.NearestNeighborSearch;

/**
 * A {@link WritableRealPointCollection} which checks if points are contained in
 * the collection by performing a {@link NearestNeighborSearch} on the provided
 * {@link RealPointSampleList}.
 *
 * <p>
 * Points can be added to this collection, however they cannot yet be removed as
 * {@link RealPointSampleList} does not currently support this.
 * </p>
 *
 * @author Alison Walter
 */
public class RealPointSampleListWritableRealPointCollection< L extends RealLocalizable > extends NNSRealPointCollection< L > implements WritableRealPointCollection< L >
{

	/**
	 * Creates a {@link RealPointCollection} with the points in the
	 * {@link Collection}.
	 *
	 * @param points
	 *            Points which should be included in this point collection. This
	 *            Collection will be used to create a RealPointSampleList.
	 */
	public RealPointSampleListWritableRealPointCollection( final Collection< L > points )
	{
		this( createRPSL( points ) );
	}

	/**
	 * Creates a {@link RealPointCollection} with the points in the
	 * {@link RealPointSampleList}.
	 *
	 * @param points
	 *            Contains the points which will be included in this collection.
	 *            Only the positions of the points will be used, the values at
	 *            those positions will be ignored. The first point determines
	 *            the dimensionality of the collection.
	 */
	public RealPointSampleListWritableRealPointCollection( final RealPointSampleList< L > points )
	{
		super( points );
	}

	@Override
	public void addPoint( final L point )
	{
		if ( point.numDimensions() != n )
			throw new IllegalArgumentException( "Point must have " + n + " dimensions" );
		final double[] pos = new double[ n ];
		point.localize( pos );
		( ( RealPointSampleList< L > ) this.points() ).add( new RealPoint( pos ), point );
	}

	// -- Helper methods --

	/**
	 * Creates a {@link RealPointSampleList} from the collection. The first
	 * point in the collection will determine the number of dimensions of the
	 * {@link RealPointSampleList}. All points in the list are assumed to have
	 * the same number of dimensions.
	 *
	 * @param points
	 *            Points which will become nodes in list.
	 * @return RealPointSampleList containing the points
	 */
	private final static < R extends RealLocalizable > RealPointSampleList< R > createRPSL( final Collection< R > points )
	{
		final RealPointSampleList< R > rpsl = new RealPointSampleList<>( points.iterator().next().numDimensions() );
		for ( final R p : points )
		{
			rpsl.add( new RealPoint( p ), p );
		}
		return rpsl;
	}
}
