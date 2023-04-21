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

package net.imglib2.roi;

import static net.imglib2.roi.BoundaryType.UNSPECIFIED;
import static net.imglib2.roi.KnownConstant.ALL_FALSE;
import static net.imglib2.roi.KnownConstant.ALL_TRUE;
import static net.imglib2.roi.KnownConstant.UNKNOWN;

import java.util.function.Predicate;

import net.imglib2.EuclideanSpace;

/**
 * Base interface for all things that divide an N-space into two parts.
 *
 * @param <T>
 *            location in N-space; typically a {@code RealLocalizable} or
 *            {@code Localizable}).
 *
 * @author Alison Walter
 * @author Curtis Rueden
 * @author Tobias Pietzsch
 * @author Christian Dietz
 */
public interface MaskPredicate< T > extends Predicate< T >, EuclideanSpace
{

	/**
	 * Returns the type of the mask. Each type of mask has distinct behavior
	 * regarding characteristics like {@link #equals(Object) equality} and
	 * {@link Object#hashCode() hashing}.
	 */
	default Class< ? > maskType()
	{
		return getClass();
	}

	/** Returns the boundary behavior of this Mask. */
	default BoundaryType boundaryType()
	{
		return UNSPECIFIED;
	}

	default KnownConstant knownConstant()
	{
		return UNKNOWN;
	}

	/**
	 * Returns true if {@link MaskPredicate#test} is known to always return
	 * false.
	 */
	default boolean isEmpty() // TODO: remove ???
	{
		return knownConstant() == ALL_FALSE;
	}

	/**
	 * Returns true if {@link MaskPredicate#test} is known to always return
	 * true.
	 */
	default boolean isAll() // TODO: remove ???
	{
		return knownConstant() == ALL_TRUE;
	}

	/**
	 * Determines whether two masks describe the same region in the same way.
	 * Each {@link #maskType() kind} of mask has its own semantics:
	 * <ul>
	 * <li>Two masks of the same kind describe the same region when they have
	 * the same attributes; for example, two
	 * {@link net.imglib2.roi.geom.real.Polygon2D polygon} objects are equal if
	 * they have the same vertices in the same order at the same locations.</li>
	 * <li>Two masks of different kinds are never considered to be describing
	 * the same region. For example, a {@link net.imglib2.roi.geom.real.Box box}
	 * is not considered "equal" to a {@link net.imglib2.roi.geom.real.Polygon2D
	 * polygon} even if the polygon's vertices are the four corners of the box,
	 * and the two masks have the same boundary type.</li>
	 * </ul>
	 */
	@Override
	boolean equals( Object obj );

	@Override
	public MaskPredicate< T > and( Predicate< ? super T > other );

	@Override
	public MaskPredicate< T > or( Predicate< ? super T > other );

	@Override
	public MaskPredicate< T > negate();

	public MaskPredicate< T > minus( Predicate< ? super T > other );

	public MaskPredicate< T > xor( Predicate< ? super T > other );
}
