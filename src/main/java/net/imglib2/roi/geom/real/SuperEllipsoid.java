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
 * A {@link RealMaskRealInterval} which defines an n-d superellipsoid.
 *
 * @author Alison Walter
 * @author Curtis Rueden
 */
public interface SuperEllipsoid extends RealMaskRealInterval
{
	/** Returns the exponent of this superellipsoid */
	double exponent();

	/**
	 * Returns the semi-axis length of the superellipsoid in a given dimension d
	 */
	double semiAxisLength( int d );

	/** Returns the center of the superellipsoid */
	RealLocalizable center();

	@Override
	default Class<?> maskType()
	{
		return SuperEllipsoid.class;
	}

	/**
	 * Determines whether this superellipsoid describes the same region as
	 * another one.
	 * 
	 * @param obj
	 *            The superellipsoid to compare with this one.
	 * @return True iff the superellipsoids describe the same region.
	 * @see MaskPredicate#equals(Object)
	 * @see #equals(SuperEllipsoid, SuperEllipsoid)
	 */
	@Override
	boolean equals( Object obj );

	/**
	 * Computes a hash code for a superellipsoid. The hash code value is based
	 * on the superellipsoid's position, semi-axis lengths, exponent and
	 * boundary type.
	 * 
	 * @param ellipsoid
	 *            The superellipsoid for which to compute the hash code.
	 * @return Hash code of the superellipsoid.
	 */
	static int hashCode( final SuperEllipsoid ellipsoid )
	{
		int result = 22;
		for ( int i = 0; i < ellipsoid.numDimensions(); i++ )
			result += 13 * ellipsoid.center().getDoublePosition( i ) + 13 * ellipsoid.semiAxisLength( i );
		result += ellipsoid.exponent();
		if ( ellipsoid.boundaryType() == BoundaryType.CLOSED )
			result += 5;
		else if ( ellipsoid.boundaryType() == BoundaryType.OPEN )
			result += 8;
		return result;
	}

	/**
	 * Determines whether two superellipsoid describe the same region.
	 * <p>
	 * Two superellipsoids are equal iff they have the same dimensionality,
	 * boundary type, exponent, semi-axis lengths and positions.
	 * </p>
	 * 
	 * @param ellipsoid1
	 *            The first superellipsoid to compare.
	 * @param ellipsoid2
	 *            The second superellipsoid to compare.
	 * @return True iff the superellipsoids describe the same region.
	 */
	static boolean equals( final SuperEllipsoid ellipsoid1, final SuperEllipsoid ellipsoid2 )
	{
		if ( ellipsoid1 == null && ellipsoid2 == null )
			return true;
		if ( ellipsoid1 == null || ellipsoid2 == null || !Masks.sameTypesAndDimensions( ellipsoid1, ellipsoid2 ) || //
				ellipsoid1.exponent() != ellipsoid2.exponent() )
			return false;
		for ( int d = 0; d < ellipsoid1.numDimensions(); d++ )
			if ( ellipsoid1.semiAxisLength( d ) != ellipsoid2.semiAxisLength( d ) )
				return false;
		return Util.locationsEqual( ellipsoid1.center(), ellipsoid2.center() );
	}
}
