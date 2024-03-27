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

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.roi.util.PositionableLocalizable;
import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BitType;

/**
 * An {@link IterableRegion} that can be moved around.
 * <p>
 * The iterable view of {@link #inside()} pixels can also be moved around. Its
 * position is the same as the position of this {@code
 * PositionableIterableRegion}. Moving one will also move the other.
 * <p>
 * We put interfaces {@code RandomAccessibleInterval<BooleanType>}, extended by
 * {@code IterableRegion<BooleanType>}, extended by
 * {@code PositionableIterableRegion<BooleanType>} into this sequence such that
 * the {@link Regions} methods that "add capabilities" (being iterable,
 * positionable) can have appropriate result types.
 *
 * @param <T>
 *            some {@code BooleanType} indicating containment in region
 *
 * @author Tobias Pietzsch
 */
public interface PositionableIterableRegion< T extends BooleanType< T > > extends IterableRegion< T >, Localizable, Positionable
{
	/**
	 * Get an {@code PositionableIterableInterval} view of only the pixels contained in the
	 * region (having value {@code true}).
	 * <p>
	 * The position of the {@link #inside()} view is the same as the position of
	 * this {@code PositionableIterableRegion}. Moving one will also move the
	 * other.
	 *
	 * @return iterable of the pixels in the region
	 */
	@Override
	PositionableIterableInterval< Void > inside();

	/**
	 * Get the {@link Positionable}, {@link Localizable} origin of this
	 * interval.
	 * <p>
	 * The origin is the relative offset of the position to the minimum. For
	 * example if a positionable (bitmask) region is made from a {@link BitType}
	 * image with a circular pattern, then it is more natural if the region
	 * position refers to the center of the pattern instead of the upper left
	 * corner of the {@link BitType} image. This can be achieved by positioning
	 * the origin.
	 * <p>
	 * Assume a region is created from a 9x9 bitmask. The region initially has
	 * min=(0,0), max=(8,8), position=(0,0). Because both position and min are
	 * (0,0), initially origin=(0,0). Now assume the origin is moved to the
	 * center of the bitmask using
	 * <code>origin().setPosition(new int[]{4,4})</code>. After this,
	 * min=(-4,-4), max=(4,4), position=(0,0), and origin=(4,4).
	 *
	 * @return the origin to which the interval is relative.
	 */
	PositionableLocalizable origin();

	/**
	 * Make a copy of this {@link PositionableIterableInterval} which can be
	 * positioned independently.
	 *
	 * @return a copy with an independent position
	 */
	PositionableIterableRegion< T > copy();
}
