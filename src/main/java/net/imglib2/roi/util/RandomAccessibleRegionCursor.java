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

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.BooleanType;

/**
 * A {@link Cursor} that iterates {@code true} pixels of a {@link BooleanType}
 * {@link RandomAccessibleInterval} by moving a {@link RandomAccess} in flat
 * iteration order.
 *
 * @author Tobias Pietzsch
 */
@Deprecated
public class RandomAccessibleRegionCursor< T extends BooleanType< T > >
		extends DelegatingLocalizable< RandomAccess< T > >
		implements Cursor< Void >
{
	private final FinalInterval interval;

	private final int n;

	private long index;

	private final long maxIndex;

	private long lineIndex;

	private final long maxLineIndex;

	public RandomAccessibleRegionCursor( final RandomAccessibleInterval< T > interval, final long size )
	{
		super( interval.randomAccess() );
		this.interval = new FinalInterval( interval );
		n = interval.numDimensions();
		maxLineIndex = interval.dimension( 0 ) - 1;
		maxIndex = size;
		reset();
	}

	protected RandomAccessibleRegionCursor( final RandomAccessibleRegionCursor< T > other )
	{
		super( other.delegate.copyRandomAccess() );
		interval = other.interval;
		n = other.n;
		index = other.index;
		maxIndex = other.maxIndex;
		lineIndex = other.lineIndex;
		maxLineIndex = other.maxLineIndex;
	}

	@Override
	public Void get()
	{
		return null;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		for ( long i = 0; i < steps; ++i )
			fwd();
	}

	@Override
	public void fwd()
	{
		do
		{
			delegate.fwd( 0 );
			if ( ++lineIndex > maxLineIndex )
				nextLine();
		}
		while ( !delegate.get().get() );
		++index;
	}

	private void nextLine()
	{
		lineIndex = 0;
		delegate.setPosition( interval.min( 0 ), 0 );
		for ( int d = 1; d < n; ++d )
		{
			delegate.fwd( d );
			if ( delegate.getLongPosition( d ) > interval.max( d ) )
				delegate.setPosition( interval.min( d ), d );
			else
				break;
		}
	}

	@Override
	public void reset()
	{
		index = 0;
		lineIndex = -1;
		interval.min( delegate );
		delegate.bck( 0 );
	}

	@Override
	public boolean hasNext()
	{
		return index < maxIndex;
	}

	@Override
	public Void next()
	{
		fwd();
		return get();
	}

	@Override
	public RandomAccessibleRegionCursor< T > copy()
	{
		return new RandomAccessibleRegionCursor<>( this );
	}

	@Override
	public RandomAccessibleRegionCursor< T > copyCursor()
	{
		return copy();
	}
}
