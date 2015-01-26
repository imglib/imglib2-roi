package net.imglib2.roi.labeling;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.outofbounds.OutOfBoundsConstantValue;
import net.imglib2.roi.PositionableIterableRegion;
import net.imglib2.roi.labeling.LabelRegions.LabelRegionProperties;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;

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
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
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

	public LabelRegion( final LabelRegions< T > regions, final LabelRegionProperties regionProperties, final T label )
	{
		super( regions.numDimensions() );
		this.regions = regions;
		this.regionProperties = regionProperties;
		this.label = label;

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
		size = 0;
		itcodes = regionProperties.getItcodes();
		centerOfMass = RealPoint.wrap( regionProperties.getCenterOfMass() );
	}

	public void printOrigin()
	{
		final long[] origin = new long[ n ];
		for ( int d = 0; d < n; ++d )
			origin[ d ] = position[ d ] - currentOffset[ d ];
		System.out.println( Util.printCoordinates( origin ) );
	}

	public void setOrigin( final long[] origin )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = position[ d ] - currentOffset[ d ] - origin[ d ];
			currentOffset[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}
	}

	public T getLabel()
	{
		return label;
	}

	// TODO: add mechanism to detect when label has been completely removed from labeling. Then, this LabelRegion should become empty.
	private void update()
	{
		if ( regionProperties.updateIfNecessary() )
		{
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
		return new LabelRegionRandomAccess< T >( this );
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
	public BoolType firstElement()
	{
		return cursor().next();
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
}
