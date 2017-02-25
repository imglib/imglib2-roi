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

import net.imglib2.Cursor;
import net.imglib2.Dimensions;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.Sampler;

/**
 * Provides to sample a source of type {@code T} at random positions. The sampled
 * positions are defined by the provided {@link Localizable}
 * {@link Positionable} {@link IterableInterval}.
 *
 * @param <T>
 *            type of target to sample
 * @param <P>
 *            type of {@link Localizable} {@link Positionable}
 *            {@link IterableInterval} used to sample target
 *
 * @author Christian Dietz
 */
// TODO: rename?
public class PositionableIntervalRandomAccessible< T, P extends Localizable & Positionable & IterableInterval< Void > > implements RandomAccessible< IterableInterval< T > >
{
	private final boolean isSafe;

	private final PositionableIntervalFactory< P > factory;

	private final RandomAccessible< T > target;

	/**
	 *
	 * @param factory
	 *            {@link PositionableIntervalFactory} to create and/or copy {@code P}
	 *
	 * @param target
	 *            the {@link RandomAccessible} which is sampled
	 * @param isSafe
	 *            if true, a new cursor is created for each request on certain
	 *            {@link Sampler#get()}. Otherwise, a single cursor is used for
	 *            each Sampler#get(). This means, after updating the position of
	 *            a RandomAccess created with this class, a formerly created
	 *            Cursor has to be considered invalid.
	 */
	public PositionableIntervalRandomAccessible( final PositionableIntervalFactory< P > factory, final RandomAccessible< T > target, final boolean isSafe )
	{
		this.isSafe = isSafe;
		this.factory = factory;
		this.target = target;
	}

	@Override
	public int numDimensions()
	{
		return target.numDimensions();
	}

	@Override
	public RandomAccess< IterableInterval< T > > randomAccess()
	{
		if ( isSafe ) { return new RASafe(); }
		return new RAUnsafe();
	}

	@Override
	public RandomAccess< IterableInterval< T > > randomAccess( Interval interval )
	{
		return randomAccess();
	}

	/**
	 * @author Christian Dietz
	 */
	class RAUnsafe extends DelegatingPositionableLocalizable< P > implements RandomAccess< IterableInterval< T > >
	{
		private final SamplingIterableInterval< T > samplingII;

		RAUnsafe()
		{
			this( factory.create() );
		}

		private RAUnsafe( P region )
		{
			super( region );

			// TODO Remove workaround
			final RandomAccess< T > targetRA;
			if ( target instanceof Interval )
				targetRA = target.randomAccess( expand( ( Interval ) target, delegate ) );
			else
				targetRA = target.randomAccess();

			final Cursor< T > c = new SamplingCursor<>( delegate.cursor(), targetRA );
			final Cursor< T > cl = new SamplingCursor<>( delegate.localizingCursor(), targetRA );

			samplingII = new SamplingIterableInterval< T >( delegate, target )
			{
				@Override
				public Cursor< T > cursor()
				{
					c.reset();
					return c;
				}

				@Override
				public Cursor< T > localizingCursor()
				{
					cl.reset();
					return cl;
				}
			};
		}

		// TODO remove as soon as method is available in imglib2-core release (see Intervals.expand(..))
		private FinalInterval expand( final Interval interval, final Dimensions border )
		{
			final int n = interval.numDimensions();
			final long[] min = new long[ n ];
			final long[] max = new long[ n ];
			interval.min( min );
			interval.max( max );
			for ( int d = 0; d < n; ++d )
			{
				min[ d ] -= border.dimension( d );
				max[ d ] += border.dimension( d );
			}
			return new FinalInterval( min, max );
		}

		@Override
		public IterableInterval< T > get()
		{
			return samplingII;
		}

		@Override
		public RandomAccess< IterableInterval< T > > copy()
		{
			return new RAUnsafe( factory.copy( delegate ) );
		}

		@Override
		public RandomAccess< IterableInterval< T > > copyRandomAccess()
		{
			return copy();
		}
	}

	/**
	 * @author Christian Dietz
	 */
	class RASafe extends DelegatingPositionableLocalizable< P > implements RandomAccess< IterableInterval< T > >
	{
		RASafe()
		{
			super( factory.create() );
		}

		private RASafe( final P curr )
		{
			super( curr );
		}

		@Override
		public IterableInterval< T > get()
		{
			return new SamplingIterableInterval<>( factory.copy( delegate ), target );
		}

		@Override
		public RandomAccess< IterableInterval< T > > copy()
		{
			return new RASafe( factory.copy( delegate ) );
		}

		@Override
		public RandomAccess< IterableInterval< T > > copyRandomAccess()
		{
			return copy();
		}
	}

	/**
	 * Factory to create and copy a {@code P}
	 *
	 * @param <P>
	 *            the {@link Localizable} {@link Positionable}
	 *            {@link IterableInterval} to be created and/or copied.
	 */
	public interface PositionableIntervalFactory< P extends Localizable & Positionable & IterableInterval< ? > >
	{
		P create();

		P copy( P source );
	}
}
