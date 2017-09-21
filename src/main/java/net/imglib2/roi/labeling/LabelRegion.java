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
package net.imglib2.roi.labeling;

import java.util.ArrayList;

import gnu.trove.list.array.TIntArrayList;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.outofbounds.OutOfBoundsConstantValue;
import net.imglib2.roi.Origin;
import net.imglib2.roi.PositionableIterableRegion;
import net.imglib2.roi.labeling.LabelRegions.LabelRegionProperties;
import net.imglib2.roi.util.PositionableInterval;
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
public class LabelRegion< T > extends PositionableInterval implements PositionableIterableRegion< BoolType >
{
	final LabelRegions< T > regions;

	private final LabelRegionProperties regionProperties;

	private final T label;

	private final ArrayList< TIntArrayList > itcodes;

	private long size;

	private final RealPoint centerOfMass;

	private int expectedGeneration;

	public LabelRegion( final LabelRegions< T > regions, final LabelRegionProperties regionProperties, final T label )
	{
		super( new FinalInterval( regionProperties.getBoundingBoxMin(), regionProperties.getBoundingBoxMax() ) );
		this.regions = regions;
		this.regionProperties = regionProperties;
		this.label = label;

		expectedGeneration = regionProperties.update();
		size = regionProperties.getSize();
		itcodes = regionProperties.getItcodes();
		centerOfMass = RealPoint.wrap( regionProperties.getCenterOfMass() );
	}

	/**
	 * Create a copy of this {@link LabelRegion}. The copy can be independently
	 * positioned and its origin can be independently changed. All copies are
	 * linked to the original Labeling and reflect all changes.
	 *
	 * @return an independent copy of this {@link LabelRegion}.
	 */
	@Override
	public LabelRegion< T > copy()
	{
		final LabelRegion< T > r = new LabelRegion<>( regions, regionProperties, label );
		System.arraycopy( position, 0, r.position, 0, n );
		System.arraycopy( currentOffset, 0, r.currentOffset, 0, n );
		System.arraycopy( currentMin, 0, r.currentMin, 0, n );
		System.arraycopy( currentMax, 0, r.currentMax, 0, n );
		r.expectedGeneration = expectedGeneration;
		return r;
	}

	@Override
	public Origin origin()
	{
		update();
		return super.origin();
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
		return new LabelRegionRandomAccess<>( this, currentOffset );
	}

	@Override
	public RandomAccess< BoolType > randomAccess( final Interval interval )
	{
		update();
		if ( Intervals.contains( this, interval ) )
			return randomAccess();
		else
			return new OutOfBoundsConstantValue<>( this, new BoolType( false ) );
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
	public double realMin( final int d )
	{
		update();
		return super.realMin( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		update();
		super.realMin( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		update();
		super.realMin( min );
	}

	@Override
	public double realMax( final int d )
	{
		update();
		return super.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		update();
		super.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		update();
		super.realMax( max );
	}

	@Override
	public long min( final int d )
	{
		update();
		return super.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		update();
		super.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		update();
		super.min( min );
	}

	@Override
	public long max( final int d )
	{
		update();
		return super.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		update();
		super.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		update();
		super.max( max );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		update();
		super.dimensions( dimensions );
	}

	@Override
	public long dimension( final int d )
	{
		update();
		return super.dimension( d );
	}
}
