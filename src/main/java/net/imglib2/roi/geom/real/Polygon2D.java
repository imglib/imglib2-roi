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
import net.imglib2.roi.RealMaskRealInterval;

/**
 * A {@link RealMaskRealInterval} which defines a real space 2D polygon.
 *
 * @author Alison Walter
 * @author Curtis Rueden
 */
public interface Polygon2D extends Polyshape
{
	@Override
	default Class<?> maskType()
	{
		return Polygon2D.class;
	}

	@Override
	default int numDimensions()
	{
		return 2;
	}

	/**
	 * Determines whether this polygon describes the same region as another one.
	 * 
	 * @param obj
	 *            The polygon to compare with this one.
	 * @return True iff the polygons describe the same region.
	 * @see MaskPredicate#equals(Object)
	 * @see Polyshape#equals(Polyshape, Polyshape)
	 */
	@Override
	boolean equals( Object obj );

	/**
	 * Computes a hash code for a polygon. The hash code value is based on the
	 * vertex positions and boundary type.
	 * 
	 * @param polygon
	 *            The polygon for which to compute the hash code.
	 * @return Hash code of the polygon.
	 */
	static int hashCode( final Polygon2D polygon )
	{
		int result = 203;
		int t = 5;
		for (final RealLocalizable v : polygon.vertices()) {
			final double x = v.getDoublePosition( 0 );
			final double y = v.getDoublePosition( 1 );
			result += ( x * x ) + ( y * t );
			t += 7;
		}
		if ( polygon.boundaryType() == BoundaryType.CLOSED )
			result += 21;
		else if ( polygon.boundaryType() == BoundaryType.OPEN )
			result += 61;
		else
			result += 3;
		return result;
	}
}
