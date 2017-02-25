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
import net.imglib2.IterableInterval;

/**
 * {@link IterableInterval} which can be positioned at arbitrary positions.
 * 
 * @author Tobias Pietzsch
 * @author Christian Dietz
 *
 * @param <T>
 * @param <I>
 */
//TODO: rename
public class PositionableIterableInterval< T, I extends IterableInterval< T > >
		extends AbstractPositionableInterval
		implements IterableInterval< T >
{
	protected final I source;

	public PositionableIterableInterval( final I source )
	{
		super( source );
		this.source = source;
	}

	protected PositionableIterableInterval( final PositionableIterableInterval< T, I > other )
	{
		super( other );
		this.source = other.source;
	}

	@Override
	public long size()
	{
		return source.size();
	}

	@Override
	public T firstElement()
	{
		return source.firstElement();
	}

	@Override
	public Object iterationOrder()
	{
		return this;
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public Cursor< T > cursor()
	{
		return new PositionableIterableIntervalCursor( source.cursor() );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return new PositionableIterableIntervalCursor( source.localizingCursor() );
	}

	class PositionableIterableIntervalCursor extends OffsetLocalizable< Cursor< T > > implements Cursor< T >
	{
		public PositionableIterableIntervalCursor( final Cursor< T > cursor )
		{
			super( cursor, currentOffset );
		}

		@Override
		public T get()
		{
			return null;
		}

		@Override
		public void jumpFwd( final long steps )
		{
			source.jumpFwd( steps );
		}

		@Override
		public void fwd()
		{
			source.fwd();
		}

		@Override
		public void reset()
		{
			source.reset();
		}

		@Override
		public boolean hasNext()
		{
			return source.hasNext();
		}

		@Override
		public T next()
		{
			return source.next();
		}

		@Override
		public PositionableIterableIntervalCursor copy()
		{
			return new PositionableIterableIntervalCursor( source.copyCursor() );
		}

		@Override
		public PositionableIterableIntervalCursor copyCursor()
		{
			return copy();
		}
	}

	public PositionableIterableInterval< T, I > copy()
	{
		return new PositionableIterableInterval<>( this );
	}
}
