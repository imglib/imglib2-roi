/*-
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

import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.roi.util.PositionableLocalizable;
import net.imglib2.type.logic.BitType;

/**
 * An {@link IterableInterval} that can be moved around.
 * <p>
 * {@code PositionableIterableInterval} is mainly intended as a return type. It
 * is discouraged to take {@code PositionableIterableInterval} as a method
 * parameter. {@code IterableInterval<T> & Localizable & Positionable} should be
 * preferred where possible.
 *
 * @param <T>
 *            pixel type
 *
 * @author Tobias Pietzsch
 */
public interface PositionableIterableInterval< T > extends IterableInterval< T >, Localizable, Positionable
{
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
	public PositionableLocalizable origin();

	/**
	 * Make a copy of this {@link PositionableIterableInterval} which can be
	 * positioned independently.
	 *
	 * @return a copy with an independent position
	 */
	public PositionableIterableInterval< T > copy();
}
