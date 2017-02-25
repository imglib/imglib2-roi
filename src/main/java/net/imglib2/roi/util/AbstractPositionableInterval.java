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

import net.imglib2.AbstractLocalizable;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;
import net.imglib2.roi.Origin;
import net.imglib2.util.Util;

/**
 * An interval that can be moved around.
 * <p>
 * It is constructed with an initial source interval. After construction it has
 * position 0, and interval bounds that are identical to the initial source
 * interval.
 * <p>
 * If after that it is moved, both the position and interval bounds move.
 * <p>
 * Additionally, it is possible to move the {@link #origin()},
 * which is useful for example to translate the min corner of the interval
 * to the origin.
 *
 * @author Tobias Pietzsch
 */
// TODO: rename? PositionableInterval?
public class AbstractPositionableInterval extends AbstractLocalizable implements Positionable, Interval
{
	protected final long[] currentOffset;

	protected final long[] currentMin;

	protected final long[] currentMax;

	private final Origin origin;

	public AbstractPositionableInterval( final Interval initial )
	{
		super( initial.numDimensions() );
		currentOffset = new long[ n ];
		currentMin = new long[ n ];
		currentMax = new long[ n ];
		initial.min( currentMin );
		initial.max( currentMax );
		origin = new TheOrigin();
	}

	protected AbstractPositionableInterval( final AbstractPositionableInterval other )
	{
		super( other.numDimensions() );
		currentOffset = other.currentOffset.clone();
		currentMin = other.currentMin.clone();
		currentMax = other.currentMax.clone();
		origin = new TheOrigin();
	}

	public Origin origin()
	{
		return origin;
	}

	@Override
	public void fwd( final int d )
	{
		++position[ d ];
		++currentOffset[ d ];
		++currentMin[ d ];
		++currentMax[ d ];
	}

	@Override
	public void bck( final int d )
	{
		--position[ d ];
		--currentOffset[ d ];
		--currentMin[ d ];
		--currentMax[ d ];
	}

	@Override
	public void move( final int distance, final int d )
	{
		position[ d ] += distance;
		currentOffset[ d ] += distance;
		currentMin[ d ] += distance;
		currentMax[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		position[ d ] += distance;
		currentOffset[ d ] += distance;
		currentMin[ d ] += distance;
		currentMax[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = localizable.getLongPosition( d );
			position[ d ] += distance;
			currentOffset[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			currentOffset[ d ] += distance[ d ];
			currentMin[ d ] += distance[ d ];
			currentMax[ d ] += distance[ d ];
		}
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			currentOffset[ d ] += distance[ d ];
			currentMin[ d ] += distance[ d ];
			currentMax[ d ] += distance[ d ];
		}
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = localizable.getLongPosition( d ) - position[ d ];
			position[ d ] += distance;
			currentOffset[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}
	}

	@Override
	public void setPosition( final int[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = pos[ d ] - position[ d ];
			position[ d ] += distance;
			currentOffset[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}
	}

	@Override
	public void setPosition( final long[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = pos[ d ] - position[ d ];
			position[ d ] += distance;
			currentOffset[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}
	}

	@Override
	public void setPosition( final int pos, final int d )
	{
		final long distance = pos - position[ d ];
		position[ d ] += distance;
		currentOffset[ d ] += distance;
		currentMin[ d ] += distance;
		currentMax[ d ] += distance;
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		final long distance = pos - position[ d ];
		position[ d ] += distance;
		currentOffset[ d ] += distance;
		currentMin[ d ] += distance;
		currentMax[ d ] += distance;
	}

	@Override
	public double realMin( final int d )
	{
		return currentMin[ d ];
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = currentMin[ d ];
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( currentMin[ d ], d );
	}

	@Override
	public double realMax( final int d )
	{
		return currentMax[ d ];
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = currentMax[ d ];
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( currentMax[ d ], d );
	}

	@Override
	public long min( final int d )
	{
		return currentMin[ d ];
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = currentMin[ d ];
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( currentMin[ d ], d );
	}

	@Override
	public long max( final int d )
	{
		return currentMax[ d ];
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = currentMax[ d ];
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( currentMax[ d ], d );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = currentMax[ d ] - currentMin[ d ] + 1;
	}

	@Override
	public long dimension( final int d )
	{
		return currentMax[ d ] - currentMin[ d ] + 1;
	}

	public class TheOrigin implements Origin
	{
		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public void localize( final float[] pos )
		{
			for ( int d = 0; d < n; ++d )
				pos[ d ] = position[ d ] - currentOffset[ d ];
		}

		@Override
		public void localize( final double[] pos )
		{
			for ( int d = 0; d < n; ++d )
				pos[ d ] = position[ d ] - currentOffset[ d ];
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return position[ d ] - currentOffset[ d ];
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return position[ d ] - currentOffset[ d ];
		}

		@Override
		public void localize( final int[] pos )
		{
			for ( int d = 0; d < n; ++d )
				pos[ d ] = ( int ) ( position[ d ] - currentOffset[ d ] );
		}

		@Override
		public void localize( final long[] pos )
		{
			for ( int d = 0; d < n; ++d )
				pos[ d ] = position[ d ] - currentOffset[ d ];
		}

		@Override
		public int getIntPosition( final int d )
		{
			return ( int ) ( position[ d ] - currentOffset[ d ] );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return position[ d ] - currentOffset[ d ];
		}

		@Override
		public void fwd( final int d )
		{
			currentOffset[ d ]++;
			currentMin[ d ]++;
			currentMax[ d ]++;
		}

		@Override
		public void bck( final int d )
		{
			currentOffset[ d ]--;
			currentMin[ d ]--;
			currentMax[ d ]--;
		}

		@Override
		public void move( final int distance, final int d )
		{
			move( ( long ) distance, d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			currentOffset[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}

		@Override
		public void move( final Localizable localizable )
		{
			for ( int d = 0; d < n; ++d )
				move( localizable.getLongPosition( d ), d );
		}

		@Override
		public void move( final int[] distance )
		{
			for ( int d = 0; d < n; ++d )
				move( distance[ d ], d );
		}

		@Override
		public void move( final long[] distance )
		{
			for ( int d = 0; d < n; ++d )
				move( distance[ d ], d );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			for ( int d = 0; d < n; ++d )
				setPosition( localizable.getLongPosition( d ), d );
		}

		@Override
		public void setPosition( final int[] pos )
		{
			for ( int d = 0; d < n; ++d )
				setPosition( pos[ d ], d );
		}

		@Override
		public void setPosition( final long[] pos )
		{
			for ( int d = 0; d < n; ++d )
				setPosition( pos[ d ], d );
		}

		@Override
		public void setPosition( final int pos, final int d )
		{
			setPosition( ( long ) pos, d );
		}

		@Override
		public void setPosition( final long pos, final int d )
		{
			final long distance = position[ d ] - currentOffset[ d ] - pos;
			move( distance, d );
		}
	}

	@Override
	public String toString()
	{
		return "AbstractPositionableInterval( offset = "
			+ Util.printCoordinates( currentOffset )
			+ " min = "
			+ Util.printCoordinates( currentMin )
			+ " max = "
			+ Util.printCoordinates( currentMax )
			+ " )";
	}
}
