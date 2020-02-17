/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.BooleanType;

/**
 * A region that allows to iterate only the pixels contained in the region
 * (instead of all pixels in bounding box).
 * <p>
 * Specifically, a region is a {@code RandomAccessibleInterval} of some
 * {@code BooleanType} having value {@code true} for all pixels contained in the
 * region. The interval is a (not necessarily tight) bounding box of the region,
 * i.e., it is assumed that all pixels outside the interval have value
 * {@code false}.
 * <p>
 * Iterating only the pixels contained in the region is indicated by
 * {@code IterableInterval<Void>}, i.e., when iterating, only the coordinates
 * that are visited are interesting. There is no associated value.
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
public interface IterableRegion< T extends BooleanType< T > > extends IterableInterval< Void >, RandomAccessibleInterval< T >
{}
