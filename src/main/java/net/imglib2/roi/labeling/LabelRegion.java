package net.imglib2.roi.labeling;

import java.util.Iterator;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.converter.AbstractConvertedRandomAccess;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.outofbounds.OutOfBoundsConstantValue;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.util.RandomAccessibleRegionCursor;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

/**
 * Present pixels of a given label in a {@link Labeling} as a boolean mask
 * {@link RandomAccessibleInterval}. The interval bounds represent the bounding
 * box of all pixels having the label. If a {@link RandomAccess} is
 * {@link #randomAccess(Interval) requested} for an interval not fully contained
 * in the bounding box, an {@link OutOfBounds} access is created with the value
 * false for pixels outside the bounding box.
 *
 * @param <T>
 *            the label type
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class LabelRegion< T > extends AbstractEuclideanSpace implements RandomAccessibleInterval< BoolType >, IterableRegion< BoolType >
{
	private final T label;

	private final LabelRegions< T > labelRegions;

	public LabelRegion( final LabelRegions< T > labelRegions, final T label )
	{
		super( labelRegions.labeling.numDimensions() );
		this.labelRegions = labelRegions;
		this.label = label;
	}

	public T getLabel()
	{
		return label;
	}

	public long getArea()
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		return labelRegions.getStatistics( label ).getArea();
	}

	public Localizable getCenterOfMass()
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		return labelRegions.getStatistics( label ).getCenterOfMass();
	}

	protected class LabelRegionRandomAccess extends AbstractConvertedRandomAccess< LabelingType< T >, BoolType >
	{
		protected final BoolType type;

		protected LabelRegionRandomAccess()
		{
			super( labelRegions.labeling.randomAccess( LabelRegion.this ) );
			type = new BoolType();
		}

		protected LabelRegionRandomAccess( final LabelRegionRandomAccess a )
		{
			super( a.source.copyRandomAccess() );
			type = a.type.copy();
		}

		@Override
		public BoolType get()
		{
			type.set( source.get().contains( getLabel() ) );
			return type;
		}

		@Override
		public LabelRegionRandomAccess copy()
		{
			return new LabelRegionRandomAccess( this );
		}

		@Override
		public LabelRegionRandomAccess copyRandomAccess()
		{
			return copy();
		}
	}

	@Override
	public RandomAccess< BoolType > randomAccess()
	{
		return new LabelRegionRandomAccess();
	}

	@Override
	public RandomAccess< BoolType > randomAccess( final Interval interval )
	{
		if ( Intervals.contains( this, interval ) )
			return randomAccess();
		else
			return new OutOfBoundsConstantValue< BoolType >( this, new BoolType( false ) );
	}

	@Override
	public Cursor< BoolType > cursor()
	{
		return new RandomAccessibleRegionCursor< BoolType >( this, size() );
	}

	@Override
	public Cursor< BoolType > localizingCursor()
	{
		return cursor();
	}

	@Override
	public long size()
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		return labelRegions.getStatistics( label ).getArea();
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
	public Iterator< BoolType > iterator()
	{
		return cursor();
	}

	@Override
	public long min( final int d )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		return bb.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		bb.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		bb.min( min );
	}

	@Override
	public long max( final int d )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		return bb.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		bb.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		bb.max( max );
	}

	@Override
	public double realMin( final int d )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		return bb.realMin( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		bb.realMin( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		bb.realMin( min );
	}

	@Override
	public double realMax( final int d )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		return bb.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		bb.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		bb.realMax( max );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		bb.dimensions( dimensions );
	}

	@Override
	public long dimension( final int d )
	{
		// TODO: speed up. We don't want a HashMap lookup on every call.
		final BoundingBox bb = labelRegions.getStatistics( label ).getBoundingBox();
		return bb.dimension( d );
	}
}
