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
package net.imglib2.roi;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.util.IterableRandomAccessibleRegion;
import net.imglib2.roi.util.PositionableIterableRegionImp;
import net.imglib2.roi.util.SamplingIterableInterval;
import net.imglib2.roi.util.SamplingPositionableIterableInterval;
import net.imglib2.roi.util.SamplingPositionableIterableIntervalUnsafe;
import net.imglib2.type.BooleanType;

public class Regions
{
	// TODO: bind to (respectively sample from) RandomAccessible
	// TODO: out-of-bounds / clipping

	public static < T > IterableInterval< T > sample( final IterableInterval< Void > region, final RandomAccessible< T > img )
	{
		/*
		 * TODO: this can be made faster in certain cases. For example a
		 * LabelRegion, instead of creating a LabelRegionCursor and then
		 * connecting that to a RA<T> with a SamplingCursor, we could simply
		 * build a Cursor that lets the InterationCode run directly on the
		 * RA<T>. Find out how to do it.
		 */
		return SamplingIterableInterval.create( region, img );
	}

	public static < T > PositionableIterableInterval< T, ? > sample( final PositionableIterableInterval< Void, ? > region, final RandomAccessible< T > img )
	{
		return sample( region, img, false );
	}

	/**
	 * Binds a {@link Void} {@link PositionableIterableInterval} (i.e., a
	 * region) to a target image, such that it iterates over the target pixels
	 * under {@code true} pixels of the region. The source region is
	 * {@link Positionable}, and so is the resulting {@code T}
	 * {@link PositionableIterableInterval}. Setting the position amounts to
	 * shifting the mask region over the target image.
	 * <p>
	 * <em>Note that modifying the position of the
	 * {@link PositionableIterableInterval} invalidates all cursors that
	 * were obtained at an older position.</em>
	 * <p>
	 * This is a <em>unsafe</em> version of {@link SamplingIterableInterval}:
	 * Every time, a {@link Cursor} is requested (using {@link #cursor()} etc)
	 * the same {@link Cursor} instance is re-used. If you require to have more
	 * than one {@link Cursor} at a given time you can {@link Cursor#copy()
	 * copy} the cursor.
	 *
	 * @param region
	 * @param img
	 * @param unsafe
	 * @return
	 */
	public static < T > PositionableIterableInterval< T, ? > sample( final PositionableIterableInterval< Void, ? > region, final RandomAccessible< T > img, final boolean unsafe )
	{
		/*
		 * TODO: this can be made faster in certain cases. For example a
		 * LabelRegion, instead of creating a LabelRegionCursor and then
		 * connecting that to a RA<T> with a SamplingCursor, we could simply
		 * build a Cursor that let's the InterationCode run directly on the
		 * RA<T>. Find out how to do it.
		 */
		return unsafe
				? new SamplingPositionableIterableIntervalUnsafe<>( region, img )
				: new SamplingPositionableIterableInterval<>( region, img );
	}

	public static < B extends BooleanType< B > > IterableRegion< B > iterable( final RandomAccessibleInterval< B > region )
	{
		if ( region instanceof IterableRegion )
			return ( IterableRegion< B > ) region;
		else
			return IterableRandomAccessibleRegion.create( region );
	}

	/*
	 * TODO: It should be somehow possible to shift the origin of every
	 * PositionableIterableRegion. Adding it to the interface is a bit
	 * inconvenient, because it is cumbersome to implement. I would prefer to
	 * add a Regions method to do it. Or a new interface?
	 * PositionableIterableRegionWithOrigin?
	 */

	public static < B extends BooleanType< B > > PositionableIterableRegion< B, ? > positionable( final RandomAccessibleInterval< B > region )
	{
		if ( region instanceof PositionableIterableRegion )
			return ( PositionableIterableRegion< B, ? > ) region;
		else
			return new PositionableIterableRegionImp<>( Regions.iterable( region ) );
	}

}
