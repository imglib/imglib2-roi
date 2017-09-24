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

package net.imglib2.troi;

import java.util.Objects;
import java.util.function.Predicate;
import net.imglib2.troi.MaskPredicate.BoundaryType;

import static net.imglib2.troi.MaskPredicate.BoundaryType.CLOSED;
import static net.imglib2.troi.MaskPredicate.BoundaryType.OPEN;
import static net.imglib2.troi.MaskPredicate.BoundaryType.UNSPECIFIED;

/**
 * Base interface for all things that divide an N-space into two parts.
 *
 * @author Alison Walter
 * @author Curtis Rueden
 * @author Tobias Pietzsch
 * @author Christian Dietz, University of Konstanz
 * @param <T>
 *            location in N-space; typically a {@code RealLocalizable} or {@code Localizable}).
 */
public interface MaskPredicate< T > extends Predicate< T >
{
	/**
	 * Defines the edge behavior of the Mask.
	 * <ul>
	 * <li>CLOSED: contains all points on the boundary</li>
	 * <li>OPEN: contains no points on the boundary</li>
	 * <li>UNSPECIFIED: boundary behavior is unclear</li>
	 * </ul>
	 */
	enum BoundaryType
	{
		CLOSED, OPEN, UNSPECIFIED;
	}

	/** Returns the boundary behavior of this Mask. */
	default BoundaryType boundaryType()
	{
		return UNSPECIFIED;
	}

	@Override
	public MaskPredicate< T > and( Predicate< ? super T > other );

	@Override
	public MaskPredicate< T > or( Predicate< ? super T > other );

	@Override
	public MaskPredicate< T > negate();
}
