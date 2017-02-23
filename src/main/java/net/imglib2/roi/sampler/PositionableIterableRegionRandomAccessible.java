package net.imglib2.roi.sampler;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.Sampler;
import net.imglib2.roi.PositionableIterableRegion;
import net.imglib2.roi.util.SamplingIterableInterval;

/**
 * Get an {@link IterableInterval} that contains all possible
 * {@link PositionableIterableRegion} on the source {@link RandomAccessible}.
 *
 * <p>
 * A {@link Cursor} on the resulting accessible
 * {@link PositionableIterableRegion}s. As usual, when the cursor is moved, an
 * iterable interval {@link Sampler#get() obtained} previously from the cursor
 * should be considered invalid.
 * </p>
 *
 * @author Christian Dietz
 *
 * @param <T>
 */
public class PositionableIterableRegionRandomAccessible< T > implements RandomAccessible< IterableInterval< T > >
{

	private final int n;

	private final PositionableIterableRegion< ? > region;

	private final RandomAccessible< T > source;

	public PositionableIterableRegionRandomAccessible( final PositionableIterableRegion< ? > region, final RandomAccessible< T > source )
	{
		assert ( source.numDimensions() == region.numDimensions() );

		this.region = region;
		this.source = source;
		this.n = region.numDimensions();
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public RandomAccess< IterableInterval< T > > randomAccess()
	{
		return new PositionableIterableRegionRandomAccessSafe( source, region );
	}

	@Override
	public RandomAccess< IterableInterval< T > > randomAccess( Interval interval )
	{
		return randomAccess();
	}

	class PositionableIterableRegionRandomAccessSafe implements RandomAccess< IterableInterval< T > >
	{

		private SamplingIterableInterval< T > samplingInterval;

		public PositionableIterableRegionRandomAccessSafe( final RandomAccessible< T > source, final PositionableIterableRegion< ? > neighborhood )
		{
			samplingInterval = new SamplingIterableInterval< T >( neighborhood, source );
		}

		@Override
		public void localize( int[] position )
		{
			region.localize( position );
		}

		@Override
		public void localize( long[] position )
		{
			region.localize( position );
		}

		@Override
		public int getIntPosition( int d )
		{
			return region.getIntPosition( d );
		}

		@Override
		public long getLongPosition( int d )
		{
			return region.getLongPosition( d );
		}

		@Override
		public void localize( float[] position )
		{
			region.localize( position );
		}

		@Override
		public void localize( double[] position )
		{
			region.localize( position );
		}

		@Override
		public float getFloatPosition( int d )
		{
			return region.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( int d )
		{
			return region.getDoublePosition( d );
		}

		@Override
		public int numDimensions()
		{
			return region.numDimensions();
		}

		@Override
		public void fwd( int d )
		{
			region.fwd( d );
		}

		@Override
		public void bck( int d )
		{
			region.bck( d );
		}

		@Override
		public void move( int distance, int d )
		{
			region.move( distance, d );
		}

		@Override
		public void move( long distance, int d )
		{
			region.move( distance, d );
		}

		@Override
		public void move( Localizable localizable )
		{
			region.move( localizable );
		}

		@Override
		public void move( int[] distance )
		{
			region.move( distance );
		}

		@Override
		public void move( long[] distance )
		{
			region.move( distance );
		}

		@Override
		public void setPosition( Localizable localizable )
		{
			region.setPosition( localizable );
		}

		@Override
		public void setPosition( int[] position )
		{
			region.setPosition( position );
		}

		@Override
		public void setPosition( long[] position )
		{
			region.setPosition( position );
		}

		@Override
		public void setPosition( int position, int d )
		{
			region.setPosition( position, d );
		}

		@Override
		public void setPosition( long position, int d )
		{
			region.setPosition( position, d );
		}

		@Override
		public IterableInterval< T > get()
		{
			return samplingInterval;
		}

		@Override
		public RandomAccess< IterableInterval< T > > copy()
		{
			return new PositionableIterableRegionRandomAccessSafe( source, region.copy() );
		}

		@Override
		public RandomAccess< IterableInterval< T > > copyRandomAccess()
		{
			return copy();
		}

	}

}
