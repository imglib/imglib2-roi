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

import java.util.ArrayList;
import java.util.Collection;

import net.imglib2.KDTree;
import net.imglib2.RealLocalizable;
import net.imglib2.neighborsearch.NearestNeighborSearch;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;

/**
 * A {@link RealPointCollection} which checks if points are contained in the
 * collection by performing a {@link NearestNeighborSearch} on the provided
 * {@link KDTree}.
 *
 * @author Alison Walter
 */
public class KDTreeRealPointCollection< L extends RealLocalizable > extends NNSRealPointCollection< L >
{

	/**
	 * Creates a {@link RealPointCollection} with the points in the
	 * {@link Collection}.
	 *
	 * @param points
	 *            Points which should be included in this point collection. This
	 *            Collection will be used to create a KDTree. The first point
	 *            determines the dimensionality of the collection.
	 */
	public KDTreeRealPointCollection( final Collection< L > points )
	{
		this( createKDTree( points ) );
	}

	/**
	 * Creates a {@link RealPointCollection} with the points in the
	 * {@link KDTree}.
	 *
	 * @param tree
	 *            KDTree which all the contains the desired points. The values
	 *            at each node in the tree will be ignored, only the positions
	 *            will be used. The first point determines the dimensionality of
	 *            the collection. Points cannot be added or removed.
	 */
	public KDTreeRealPointCollection( final KDTree< L > tree )
	{
		super( tree, new NearestNeighborSearchOnKDTree<>( tree ) );
	}

	// -- Helper methods --

	/**
	 * Creates a {@link KDTree} from the collection.
	 *
	 * @param points
	 *            Points which will become nodes in the tree.
	 * @return KDTree containing the points
	 */
	private final static < R extends RealLocalizable > KDTree< R > createKDTree( final Collection< R > points )
	{
		final ArrayList< R > pointsList = new ArrayList<>( points );
		return new KDTree<>( pointsList, pointsList );
	}
}
