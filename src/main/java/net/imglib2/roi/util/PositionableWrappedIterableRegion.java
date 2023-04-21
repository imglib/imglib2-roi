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

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.roi.IterableRegion;
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
		extends PositionableWrappedIterableInterval< Void, IterableRegion< T > >
		implements PositionableIterableRegion< T >
{
	public PositionableWrappedIterableRegion( final IterableRegion< T > source )
	{
		super( source );
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

	class RA extends OffsetPositionableLocalizable< RandomAccess< T > > implements RandomAccess< T >
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
			return new RA( source.copyRandomAccess(), offset );
		}

		@Override
		public RA copyRandomAccess()
		{
			return copy();
		}
	}

	@Override
	public PositionableWrappedIterableRegion< T > copy()
	{
		return new PositionableWrappedIterableRegion<>( this );
	}
}
