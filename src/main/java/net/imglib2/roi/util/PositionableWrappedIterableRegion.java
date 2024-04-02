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

import net.imglib2.AbstractWrappedPositionableLocalizable;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.PositionableIterableInterval;
import net.imglib2.roi.PositionableIterableRegion;
import net.imglib2.type.BooleanType;
import net.imglib2.util.Intervals;

/**
 * Makes a {@link IterableRegion} {@code Positionable} by wrapping its accessors
 * with an offset.
 *
 * @param <T>
 *            pixel type of source
 */
public class PositionableWrappedIterableRegion< T extends BooleanType< T > >
		extends PositionableInterval
		implements PositionableIterableRegion< T >
{
	private final IterableRegion< T > source;

	private final PositionableWrappedIterableRegion< T >.InsideIterable inside;

	public PositionableWrappedIterableRegion( final IterableRegion< T > source )
	{
		super( source );
		this.source = source;
		inside = new InsideIterable();
	}

	private PositionableWrappedIterableRegion( final PositionableWrappedIterableRegion< T > other )
	{
		super( other.source );
		this.source = other.source;
		inside = new InsideIterable();
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return new RA( source.randomAccess(), currentOffset );
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return new RA( source.randomAccess( Intervals.translate( interval, currentOffset ) ), currentOffset );
	}

	private final class RA extends OffsetPositionableLocalizable< RandomAccess< T > > implements RandomAccess< T >
	{
		public RA( final RandomAccess< T > source, final long[] offset )
		{
			super( source, offset );
		}

		@Override
		public T get()
		{
			return source.get();
		}

		@Override
		public RA copy()
		{
			return new RA( source.copy(), offset );
		}
	}

	@Override
	public PositionableWrappedIterableRegion< T > copy()
	{
		return new PositionableWrappedIterableRegion<>( this );
	}

	@Override
	public PositionableIterableInterval< Void > inside()
	{
		return inside;
	}

	private final class InsideIterable extends AbstractWrappedPositionableLocalizable< PositionableWrappedIterableRegion< T > > implements PositionableIterableInterval< Void >
	{
		InsideIterable()
		{
			super( PositionableWrappedIterableRegion.this );
		}

		@Override
		public PositionableLocalizable origin()
		{
			return PositionableWrappedIterableRegion.this.origin();
		}

		@Override
		public PositionableIterableInterval< Void > copy()
		{
			return PositionableWrappedIterableRegion.this.copy().inside();
		}

		@Override
		public Cursor< Void > cursor()
		{
			return new PositionableInsideCursor( inside().cursor() );
		}

		@Override
		public Cursor< Void > localizingCursor()
		{
			return new PositionableInsideCursor( inside().localizingCursor() );
		}

		@Override
		public long size()
		{
			return inside().size();
		}

		@Override
		public Object iterationOrder()
		{
			return this;
		}

		@Override
		public long min( final int d )
		{
			return PositionableWrappedIterableRegion.this.min( d );
		}

		@Override
		public long max( final int d )
		{
			return PositionableWrappedIterableRegion.this.max( d );
		}
	}

	private final class PositionableInsideCursor extends OffsetLocalizable< Cursor< Void > > implements Cursor< Void >
	{
		public PositionableInsideCursor( final Cursor< Void > cursor )
		{
			super( cursor, currentOffset );
		}

		@Override
		public Void get()
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
		public Void next()
		{
			return source.next();
		}

		@Override
		public PositionableInsideCursor copy()
		{
			return new PositionableInsideCursor( source.copy() );
		}
	}
}
