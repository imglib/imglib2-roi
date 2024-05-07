/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.util.Util;

/**
 * A {@link RealMaskRealInterval} which defines an n-d box, cuboid,
 * hyperrectangle, etc.
 *
 * @author Alison Walter
 * @author Curtis Rueden
 */
public interface Box extends RealMaskRealInterval
{
	/** Returns side length of Box in dimension d. */
	double sideLength( int d );

	/** Returns the center of the Box. */
	RealLocalizable center();

	@Override
	default Class<?> maskType()
	{
		return Box.class;
	}

	/**
	 * Determines whether this box describes the same region as another one.
	 * 
	 * @param obj
	 *            The box to compare with this one.
	 * @return True iff the boxes describe the same region.
	 * @see MaskPredicate#equals(Object)
	 * @see #equals(Box, Box)
	 */
	@Override
	boolean equals( Object obj );

	/**
	 * Computes a hash code for a box. The hash code value is based on the
	 * position, lengths and boundary type.
	 * 
	 * @param box
	 *            The box for which to compute the hash code.
	 * @return Hash code of the box.
	 */
	static int hashCode( final Box box )
	{
		int result = 17;
		for ( int d = 0; d < box.numDimensions(); d++ )
			result += 31 * box.realMin( d ) + 31 * box.realMax( d );
		if ( box.boundaryType() == BoundaryType.CLOSED )
			result += 5;
		else if ( box.boundaryType() == BoundaryType.OPEN )
			result += 8;
		return result;
	}

	/**
	 * Determines whether two boxes describe the same region.
	 * <p>
	 * Two boxes are equal iff they have the same dimensionality, boundary type,
	 * lengths and position.
	 * </p>
	 * 
	 * @param box1
	 *            The first box to compare.
	 * @param box2
	 *            The second box to compare.
	 * @return True iff the boxes describe the same region.
	 */
	static boolean equals( final Box box1, final Box box2 )
	{
		if ( box1 == null && box2 == null )
			return true;
		if ( box1 == null || box2 == null || !Masks.sameTypesAndDimensions( box1, box2 ) )
			return false;
		for ( int d = 0; d < box1.numDimensions(); d++ )
			if ( box1.sideLength( d ) != box2.sideLength( d ) )
				return false;
		return Util.locationsEqual( box1.center(), box2.center() );
	}
}
