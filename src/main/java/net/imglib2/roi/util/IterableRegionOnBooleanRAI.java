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
package net.imglib2.roi.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.Regions;
import net.imglib2.type.BooleanType;
import net.imglib2.view.Views;

/**
 * Wrap a boolean {@link RandomAccessibleInterval} as a {@link IterableRegion}.
 * Cursors on the result only iterate {@code true} samples of the source interval.
 *
 * {@link Cursor Cursors} are realized by wrapping source cursors (using {@link TrueCursor}).
 *
 * @author Tobias Pietzsch
 */
public class IterableRegionOnBooleanRAI< T extends BooleanType< T > >
		extends AbstractWrappedInterval< RandomAccessibleInterval< T > >
		implements IterableRegion< T >
{
	private final long size;

	private final IterableInterval< T > sourceIterable;

	public IterableRegionOnBooleanRAI( final RandomAccessibleInterval< T > interval )
	{
		this( interval, Regions.countTrue( interval ) );
	}

	public IterableRegionOnBooleanRAI( final RandomAccessibleInterval< T > interval, final long size )
	{
		super( interval );
		this.size = size;
		sourceIterable = Views.iterable( interval );
	}

	@Override
	public long size()
	{
		return size;
	}

	@Override
	public Void firstElement()
	{
		if ( size() == 0 )
			throw new NoSuchElementException();
		return cursor().next();
	}

	@Override
	public Object iterationOrder()
	{
		return this;
	}

	@Override
	public Iterator< Void > iterator()
	{
		return cursor();
	}

	@Override
	public Cursor< Void > cursor()
	{
		return new TrueCursor< T >( sourceIterable.cursor(), size );
	}

	@Override
	public Cursor< Void > localizingCursor()
	{
		return new TrueCursor< T >( sourceIterable.localizingCursor(), size );
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return sourceInterval.randomAccess();
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return sourceInterval.randomAccess( interval );
	}
}
