/*-
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
package net.imglib2.roi;

import java.util.function.Predicate;

/**
 * Defines the edge behavior of the Mask.
 * <ul>
 * <li>CLOSED: contains all points on the boundary</li>
 * <li>OPEN: contains no points on the boundary</li>
 * <li>UNSPECIFIED: boundary behavior is unclear</li>
 * </ul>
 *
 * Also provides unary and binary operations on (masks having specific) edge
 * behaviours.
 *
 * @author Tobias Pietzsch
 * @author Alison Walter
 */
public enum BoundaryType
{
	CLOSED, OPEN, UNSPECIFIED;

	public BoundaryType and( final BoundaryType that )
	{
		return this == that ? this : UNSPECIFIED;
	}

	public BoundaryType or( final BoundaryType that )
	{
		return this == that ? this : UNSPECIFIED;
	}

	public BoundaryType negate()
	{
		return this == OPEN ? CLOSED : this == CLOSED ? OPEN : UNSPECIFIED;
	}

	public BoundaryType minus( final BoundaryType that )
	{
		return this != that && that != UNSPECIFIED ? this : UNSPECIFIED;
	}

	public BoundaryType xor( final BoundaryType that )
	{
		return UNSPECIFIED;
	}

	public static BoundaryType of( final Predicate< ? > predicate )
	{
		if ( predicate instanceof MaskPredicate )
			return ( (net.imglib2.roi.MaskPredicate< ? > ) predicate ).boundaryType();
		return UNSPECIFIED;
	}
}
