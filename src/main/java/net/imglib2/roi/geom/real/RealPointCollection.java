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

import net.imglib2.RealLocalizable;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.MaskPredicate;
import net.imglib2.roi.Masks;
import net.imglib2.roi.RealMaskRealInterval;

/**
 * A {@link RealMaskRealInterval} which defines a collection of real space points in n-d space.
 *
 * @author Alison Walter
 * @author Curtis Rueden
 */
public interface RealPointCollection< L extends RealLocalizable > extends RealMaskRealInterval
{
	/** Returns the points in the collection. */
	Iterable< L > points();

	/** Returns the number of points in the collection. */
	long size();

	@Override
	default Class<?> maskType()
	{
		return RealPointCollection.class;
	}

	@Override
	default BoundaryType boundaryType()
	{
		return BoundaryType.CLOSED;
	}

	/**
	 * Determines whether this point collection describes the same region as
	 * another one.
	 * 
	 * @param obj
	 *            The point collection to compare with this one.
	 * @return True iff the point collections describe the same region.
	 * @see MaskPredicate#equals(Object)
	 * @see #equals(RealPointCollection, RealPointCollection)
	 */
	@Override
	boolean equals( Object obj );

	/**
	 * Computes a hash code for a point collection. The hash code value is based
	 * on the point positions.
	 * 
	 * @param points
	 *            The point collection for which to compute the hash code.
	 * @return Hash code of the point collection.
	 */
	static int hashCode( final RealPointCollection< ? > points )
	{
		int result = 71;
		for ( RealLocalizable l : points.points() )
			for ( int d = 0; d < l.numDimensions(); d++ )
				result += 3 * l.getDoublePosition( d );
		return result;
	}

	/**
	 * Determines whether two point collections describe the same region.
	 * <p>
	 * Two point collections are equal iff they have the same dimensionality and
	 * vertices.
	 * </p>
	 * 
	 * @param points1
	 *            The first point collection to compare.
	 * @param points2
	 *            The second point collection to compare.
	 * @return True iff the point collections describe the same region.
	 */
	static boolean equals( final RealPointCollection< ? > points1, final RealPointCollection< ? > points2 )
	{
		if ( points1 == null && points2 == null )
			return true;
		if ( points1 == null || points2 == null || !Masks.sameTypesAndDimensions( points1, points2 ) || points1.size() != points2.size() )
			return false;
		for ( final RealLocalizable p : points1.points() )
		{
			if ( !points2.test( p ) )
				return false;
		}
		return true;
	}
}
