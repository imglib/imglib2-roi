/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2017 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.roi.labeling;

import java.util.ArrayList;

import gnu.trove.list.array.TIntArrayList;
import net.imglib2.AbstractLocalizable;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;
import net.imglib2.labeling.Labeling;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.outofbounds.OutOfBoundsConstantValue;
import net.imglib2.roi.PositionableIterableRegion;
import net.imglib2.roi.labeling.LabelRegions.LabelRegionProperties;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

/**
 * Present pixels of a given label in a {@link Labeling} as a
 * {@link PositionableIterableRegion}. The interval bounds represent the
 * bounding box of all pixels having the label. If a {@link RandomAccess} is
 * {@link #randomAccess(Interval) requested} for an interval not fully contained
 * in the bounding box, an {@link OutOfBounds} access is created with the value
 * false for pixels outside the bounding box.
 *
 * @param <T>
 *            the label type
 *
 * @author Tobias Pietzsch
 */
public class LabelRegion< T > extends AbstractLocalizable implements PositionableIterableRegion< BoolType >
{
	final LabelRegions< T > regions;

	private final LabelRegionProperties regionProperties;

	private final T label;

	private final ArrayList< TIntArrayList > itcodes;

	private final long[] currentOffset;

	private final long[] currentMin;

	private final long[] currentMax;

	private long size;

	private final RealPoint centerOfMass;

	private final Origin origin;

	private int expectedGeneration;

	public LabelRegion( final LabelRegions< T > regions, final LabelRegionProperties regionProperties, final T label )
	{
		super( regions.numDimensions() );
		this.regions = regions;
		this.regionProperties = regionProperties;
		this.label = label;

		expectedGeneration = regionProperties.update();
		currentOffset = new long[ n ];
		currentMin = new long[ n ];
		currentMax = new long[ n ];
		final long[] bbmin = regionProperties.getBoundingBoxMin();
		final long[] bbmax = regionProperties.getBoundingBoxMax();
		for ( int d = 0; d < n; ++d )
		{
			currentMin[ d ] = currentOffset[ d ] + bbmin[ d ];
			currentMax[ d ] = currentOffset[ d ] + bbmax[ d ];
		}
		size = regionProperties.getSize();
		itcodes = regionProperties.getItcodes();
		centerOfMass = RealPoint.wrap( regionProperties.getCenterOfMass() );
		origin = new Origin();
	}

	/**
	 * Create a copy of this {@link LabelRegion}. The copy can be independently
	 * positioned and its origin can be independently changed. All copies are
	 * linked to the original Labeling and reflect all changes.
	 *
	 * @return an independent copy of this {@link LabelRegion}.
	 */
	public LabelRegion< T > copy()
	{
		final LabelRegion< T > r = new LabelRegion< T >( regions, regionProperties, label );
		System.arraycopy( position, 0, r.position, 0, n );
		System.arraycopy( currentOffset, 0, r.currentOffset, 0, n );
		System.arraycopy( currentMin, 0, r.currentMin, 0, n );
		System.arraycopy( currentMax, 0, r.currentMax, 0, n );
		r.expectedGeneration = expectedGeneration;
		return r;
	}

	public Origin origin()
	{
		update();
		return origin;
	}

	public T getLabel()
	{
		return label;
	}

	private void update()
	{
		final int generation = regionProperties.update();
		if ( generation != expectedGeneration )
		{
			expectedGeneration = generation;
			final long[] bbmin = regionProperties.getBoundingBoxMin();
			final long[] bbmax = regionProperties.getBoundingBoxMax();
			for ( int d = 0; d < n; ++d )
			{
				currentMin[ d ] = currentOffset[ d ] + bbmin[ d ];
				currentMax[ d ] = currentOffset[ d ] + bbmax[ d ];
			}
			size = regionProperties.getSize();
		}
	}

	public RealLocalizable getCenterOfMass()
	{
		update();
		return centerOfMass;
	}

	@Override
	public LabelRegionRandomAccess< T > randomAccess()
	{
		update();
		return new LabelRegionRandomAccess< T >( this, currentOffset );
	}

	@Override
	public RandomAccess< BoolType > randomAccess( final Interval interval )
	{
		update();
		if ( Intervals.contains( this, interval ) )
			return randomAccess();
		else
			return new OutOfBoundsConstantValue< BoolType >( this, new BoolType( false ) );
	}

	@Override
	public LabelRegionCursor cursor()
	{
		update();
		return new LabelRegionCursor( itcodes, currentOffset );
	}

	@Override
	public LabelRegionCursor localizingCursor()
	{
		return cursor();
	}

	@Override
	public long size()
	{
		update();
		return size;
	}

	@Override
	public Void firstElement()
	{
		return null;
	}

	@Override
	public Object iterationOrder()
	{
		return this;
	}

	@Override
	public LabelRegionCursor iterator()
	{
		return cursor();
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
		update();
		return currentMin[ d ];
	}

	@Override
	public void realMin( final double[] min )
	{
		update();
		for ( int d = 0; d < n; ++d )
			min[ d ] = currentMin[ d ];
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		update();
		for ( int d = 0; d < n; ++d )
			min.setPosition( currentMin[ d ], d );
	}

	@Override
	public double realMax( final int d )
	{
		update();
		return currentMax[ d ];
	}

	@Override
	public void realMax( final double[] max )
	{
		update();
		for ( int d = 0; d < n; ++d )
			max[ d ] = currentMax[ d ];
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		update();
		for ( int d = 0; d < n; ++d )
			max.setPosition( currentMax[ d ], d );
	}

	@Override
	public long min( final int d )
	{
		update();
		return currentMin[ d ];
	}

	@Override
	public void min( final long[] min )
	{
		update();
		for ( int d = 0; d < n; ++d )
			min[ d ] = currentMin[ d ];
	}

	@Override
	public void min( final Positionable min )
	{
		update();
		for ( int d = 0; d < n; ++d )
			min.setPosition( currentMin[ d ], d );
	}

	@Override
	public long max( final int d )
	{
		update();
		return currentMax[ d ];
	}

	@Override
	public void max( final long[] max )
	{
		update();
		for ( int d = 0; d < n; ++d )
			max[ d ] = currentMax[ d ];
	}

	@Override
	public void max( final Positionable max )
	{
		update();
		for ( int d = 0; d < n; ++d )
			max.setPosition( currentMax[ d ], d );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		update();
		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = currentMax[ d ] - currentMin[ d ] + 1;
	}

	@Override
	public long dimension( final int d )
	{
		update();
		return currentMax[ d ] - currentMin[ d ] + 1;
	}

	public class Origin implements Localizable, Positionable
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
}
