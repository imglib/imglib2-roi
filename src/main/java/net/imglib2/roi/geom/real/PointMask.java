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
import net.imglib2.util.Util;

/**
 * A {@link RealMaskRealInterval} representing a single point in n-d real space.
 *
 * @author Alison Walter
 * @author Curtis Rueden
 */
public interface PointMask extends RealMaskRealInterval, RealLocalizable
{
	@Override
	default Class<?> maskType()
	{
		return PointMask.class;
	}

	@Override
	default BoundaryType boundaryType()
	{
		return BoundaryType.CLOSED;
	}

	@Override
	default double realMin( final int d )
	{
		return getDoublePosition( d );
	}

	@Override
	default double realMax( final int d )
	{
		return getDoublePosition( d );
	}

	/**
	 * Determines whether this point describes the same region as another one.
	 * 
	 * @param obj
	 *            The point to compare with this one.
	 * @return True iff the points describe the same region.
	 * @see MaskPredicate#equals(Object)
	 * @see #equals(PointMask, PointMask)
	 */
	@Override
	boolean equals( Object obj );

	/**
	 * Determines whether two points describe the same region.
	 * <p>
	 * Two points are equal iff they have the same dimensionality and position.
	 * </p>
	 * 
	 * @param pointMask1
	 *            The first point to compare.
	 * @param pointMask2
	 *            The second point to compare.
	 * @return True iff the points describe the same region.
	 */
	public static boolean equals( final PointMask pointMask1, final PointMask pointMask2 )
	{
		if ( pointMask1 == null && pointMask2 == null )
			return true;
		if ( pointMask1 == null || pointMask2 == null || !Masks.sameTypesAndDimensions( pointMask1, pointMask2 ) )
			return false;
		return Util.locationsEqual( pointMask1, pointMask2 );
	}
}
