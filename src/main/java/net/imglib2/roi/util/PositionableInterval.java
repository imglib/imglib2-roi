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
package net.imglib2.roi.util;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;

/**
 * An interval that can be moved around.
 * <p>
 * It is constructed with an initial source interval. After construction it has
 * position 0, and interval bounds that are identical to the initial source
 * interval.
 * <p>
 * If after that it is moved, both the position and interval bounds move.
 * <p>
 * Additionally, it is possible to move the {@link #origin()}, which is useful
 * for example to make the min corner or the center of the interval coincide
 * with the position.
 *
 * @author Tobias Pietzsch
 */
public class PositionableInterval extends AbstractLocalizable implements Positionable, Interval
{
	protected final long[] currentOffset;

	protected final long[] initialMin;

	protected final long[] initialMax;

	private final PositionableLocalizable origin;

	public PositionableInterval( final Interval initial )
	{
		super( initial.numDimensions() );
		currentOffset = new long[ n ];
		initialMin = new long[ n ];
		initialMax = new long[ n ];
		initial.min( initialMin );
		initial.max( initialMax );
		origin = new Origin();
	}

	/**
	 * Copy constructor.
	 */
	protected PositionableInterval( final PositionableInterval other )
	{
		super( other.numDimensions() );
		currentOffset = other.currentOffset.clone();
		initialMin = other.initialMin.clone();
		initialMax = other.initialMax.clone();
		origin = new Origin();
	}

	/**
	 * Get the {@link Positionable}, {@link Localizable} origin of this
	 * interval.
	 * <p>
	 * The origin is the relative offset of the position to the minimum. For
	 * example if a positionable (bitmask) region is made from a {@code BitType}
	 * image with a circular pattern, then it is more natural if the region
	 * position refers to the center of the pattern instead of the upper left
	 * corner of the {@code BitType} image. This can be achieved by positioning
	 * the origin.
	 * <p>
	 * Assume a region is created from a 9x9 bitmask. The region initially has
	 * min=(0,0), max=(8,8), position=(0,0). Because both position and min are
	 * (0,0), initially origin=(0,0). Now assume the origin is moved to the
	 * center of the bitmask using
	 * <code>origin().setPosition(new int[]{4,4})</code>. After this,
	 * min=(-4,-4), max=(4,4), position=(0,0), and origin=(4,4).
	 *
	 * @return the origin to which the interval is relative.
	 */
	public PositionableLocalizable origin()
	{
		return origin;
	}

	@Override
	public void fwd( final int d )
	{
		++position[ d ];
		++currentOffset[ d ];
	}

	@Override
	public void bck( final int d )
	{
		--position[ d ];
		--currentOffset[ d ];
	}

	@Override
	public void move( final int distance, final int d )
	{
		position[ d ] += distance;
		currentOffset[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		position[ d ] += distance;
		currentOffset[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = localizable.getLongPosition( d );
			position[ d ] += distance;
			currentOffset[ d ] += distance;
		}
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			currentOffset[ d ] += distance[ d ];
		}
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			currentOffset[ d ] += distance[ d ];
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
		}
	}

	@Override
	public void setPosition( final int pos, final int d )
	{
		final long distance = pos - position[ d ];
		position[ d ] += distance;
		currentOffset[ d ] += distance;
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		final long distance = pos - position[ d ];
		position[ d ] += distance;
		currentOffset[ d ] += distance;
	}

	@Override
	public double realMin( final int d )
	{
		return currentOffset[ d ] + initialMin[ d ];
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = currentOffset[ d ] + initialMin[ d ];
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( currentOffset[ d ] + initialMin[ d ], d );
	}

	@Override
	public double realMax( final int d )
	{
		return currentOffset[ d ] + initialMax[ d ];
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = currentOffset[ d ] + initialMax[ d ];
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( currentOffset[ d ] + initialMax[ d ], d );
	}

	@Override
	public long min( final int d )
	{
		return currentOffset[ d ] + initialMin[ d ];
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = currentOffset[ d ] + initialMin[ d ];
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( currentOffset[ d ] + initialMin[ d ], d );
	}

	@Override
	public long max( final int d )
	{
		return currentOffset[ d ] + initialMax[ d ];
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = currentOffset[ d ] + initialMax[ d ];
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( currentOffset[ d ] + initialMax[ d ], d );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = initialMax[ d ] - initialMin[ d ] + 1;
	}

	@Override
	public long dimension( final int d )
	{
		return initialMax[ d ] - initialMin[ d ] + 1;
	}

	private class Origin implements PositionableLocalizable
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
				pos[ d ] = position[ d ] - currentOffset[ d ] - initialMin[ d ] ;
		}

		@Override
		public void localize( final double[] pos )
		{
			for ( int d = 0; d < n; ++d )
				pos[ d ] = position[ d ] - currentOffset[ d ] - initialMin[ d ];
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return position[ d ] - currentOffset[ d ] - initialMin[ d ];
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return position[ d ] - currentOffset[ d ] - initialMin[ d ];
		}

		@Override
		public void localize( final int[] pos )
		{
			for ( int d = 0; d < n; ++d )
				pos[ d ] = ( int ) ( position[ d ] - currentOffset[ d ] - initialMin[ d ] );
		}

		@Override
		public void localize( final long[] pos )
		{
			for ( int d = 0; d < n; ++d )
				pos[ d ] = position[ d ] - currentOffset[ d ] - initialMin[ d ];
		}

		@Override
		public int getIntPosition( final int d )
		{
			return ( int ) ( position[ d ] - currentOffset[ d ] - initialMin[ d ] );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return position[ d ] - currentOffset[ d ] - initialMin[ d ];
		}

		@Override
		public void fwd( final int d )
		{
			currentOffset[ d ]--;
		}

		@Override
		public void bck( final int d )
		{
			currentOffset[ d ]++;
		}

		@Override
		public void move( final int distance, final int d )
		{
			move( ( long ) distance, d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			currentOffset[ d ] -= distance;
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
			final long distance = pos - position[ d ] + currentOffset[ d ] + initialMin[ d ];
			move( distance, d );
		}
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();

		final String className = this.getClass().getSimpleName();
		sb.append( className );

		sb.append( " [(" );
		for ( int d = 0; d < n; d++ )
		{
			sb.append( min( d ) );
			if ( d < n - 1 )
				sb.append( ", " );
		}
		sb.append( ") -- (" );
		for ( int d = 0; d < n; d++ )
		{
			sb.append( max( d ) );
			if ( d < n - 1 )
				sb.append( ", " );
		}
		sb.append( ") = " );
		for ( int d = 0; d < n; d++ )
		{
			sb.append( dimension( d ) );
			if ( d < n - 1 )
				sb.append( "x" );
		}

		sb.append( ", pos=(" );
		for ( int d = 0; d < n; d++ )
		{
			sb.append( getLongPosition( d ) );
			if ( d < n - 1 )
				sb.append( ", " );
		}
		sb.append( "), origin(" );
		for ( int d = 0; d < n; d++ )
		{
			sb.append( origin().getLongPosition( d ) );
			if ( d < n - 1 )
				sb.append( ", " );
		}
		sb.append( ")]" );

		return sb.toString();
	}
}
