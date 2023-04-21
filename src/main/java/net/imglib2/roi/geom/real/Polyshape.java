/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.AbstractList;
import java.util.List;

import net.imglib2.RealLocalizable;
import net.imglib2.roi.Masks;
import net.imglib2.roi.RealMaskRealInterval;
import net.imglib2.util.IterablePair;
import net.imglib2.util.Pair;
import net.imglib2.util.Util;

/**
 * A {@link RealMaskRealInterval} which defines a polygonal shape in n-d space.
 *
 * @author Alison Walter
 * @author Curtis Rueden
 */
public interface Polyshape extends RealMaskRealInterval
{
	/** Returns the vertex at the specified position. */
	RealLocalizable vertex( final int pos );

	/** Returns the number of vertices in the shape. */
	int numVertices();

	default List<RealLocalizable> vertices() {
		return new AbstractList<RealLocalizable>() {

			@Override
			public RealLocalizable get(int index) {
				return vertex(index);
			}

			@Override
			public int size() {
				return numVertices();
			}
		};
	}

	/**
	 * Determines whether two polyshapes describe the same region.
	 * <p>
	 * Two polyshapes are equal iff they have the same mask type, boundary type,
	 * dimensions and vertices.
	 * </p>
	 * 
	 * @param polyshape1
	 *            The first polyshape to compare.
	 * @param polyshape2
	 *            The second polyshape to compare.
	 * @return True iff the polyshapes describe the same region.
	 */
	static boolean equals( final Polyshape polyshape1, final Polyshape polyshape2 )
	{
		if ( polyshape1 == null && polyshape2 == null )
			return true;
		if ( polyshape1 == null || polyshape2 == null || !Masks.sameTypesAndDimensions( polyshape1, polyshape2 ) )
			return false;

		// Ensure same vertices in same order.
		final List< RealLocalizable > vertices1 = polyshape1.vertices();
		final List< RealLocalizable > vertices2 = polyshape2.vertices();
		for ( final Pair< RealLocalizable, RealLocalizable > pair : new IterablePair<>( vertices1, vertices2 ) )
			if ( !Util.locationsEqual( pair.getA(), pair.getB() ) )
				return false;

		return true;
	}
}
