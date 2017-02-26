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
package net.imglib2.roi.util;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessible;
import net.imglib2.roi.Origin;
import net.imglib2.roi.PositionableIterableInterval;

/**
 * Binds a {@link Void} {@link PositionableIterableInterval} (i.e., a region) to
 * a target image, such that it iterates over the target pixels under
 * {@code true} pixels of the region. The source region is positionable, and so
 * is the resulting {@link SamplingPositionableIterableInterval}. Setting the
 * position amounts to shifting the mask region over the target image.
 * <p>
 * <em>Note that modifying the position of the
 * {@link SamplingPositionableIterableInterval} invalidates all cursors that
 * were obtained at an older position.</em>
 *
 * @param <T>
 *            target image type
 *
 * @author Christian Dietz
 * @author Tobias Pietzsch
 */
public class SamplingPositionableIterableInterval< T >
	extends AbstractWrappedPositionableInterval< PositionableIterableInterval< Void > >
	implements PositionableIterableInterval< T >
{
	final RandomAccessible< T > target;

	public  SamplingPositionableIterableInterval(
			final PositionableIterableInterval< Void > region,
			final RandomAccessible< T > target )
	{
		super( region );
		this.target = target;
	}

	@Override
	public Cursor< T > cursor()
	{
		return new SamplingCursor<>( sourceInterval.cursor(), target.randomAccess( sourceInterval ) );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return new SamplingCursor<>( sourceInterval.localizingCursor(), target.randomAccess( sourceInterval ) );
	}

	@Override
	public long size()
	{
		return sourceInterval.size();
	}

	@Override
	public T firstElement()
	{
		return cursor().next();
	}

	@Override
	public Object iterationOrder()
	{
		return sourceInterval.iterationOrder();
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public Origin origin()
	{
		return sourceInterval.origin();
	}
}
