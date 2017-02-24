package net.imglib2.roi.util;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;

/**
 *
 */
public class PositionableIntervalRandomAccessible< T, P extends Positionable & IterableInterval< Void > > implements RandomAccessible< IterableInterval< T > >
{

	private final int n;

	private final P region;

	private final RandomAccessible< T > source;

	private final PositionableIntervalFactory< P > fac;

	public PositionableIntervalRandomAccessible( final PositionableIntervalFactory< P > fac, final RandomAccessible< T > source )
	{
		this.region = fac.create();
		this.fac = fac;
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
	public RandomAccess< IterableInterval< T > > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	class PositionableIterableRegionRandomAccessSafe implements RandomAccess< IterableInterval< T > >
	{
		private final SamplingIterableInterval< T > samplingInterval;

		private final SamplingCursor< T > theCursor;

		private final SamplingCursor< T > theLocalizingCursor;

		private final long[] position;

		public PositionableIterableRegionRandomAccessSafe( final RandomAccessible< T > source, final P region )
		{
			final RandomAccess< T > theTarget = source.randomAccess();

			position = new long[ region.numDimensions() ];
			theCursor = new SamplingCursor< T >( region.cursor(), theTarget );
			theLocalizingCursor = new SamplingCursor< T >( region.localizingCursor(), theTarget );

			samplingInterval = new SamplingIterableInterval< T >( region, source )
			{
				@Override
				public Cursor< T > cursor()
				{
					return theCursor;
				}

				@Override
				public Cursor< T > localizingCursor()
				{
					return theLocalizingCursor;
				}
			};
		}

		@Override
		public void localize( int[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				position[ i ] = ( int ) this.position[ i ];
			}
		}

		@Override
		public void localize( long[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				position[ i ] = ( int ) this.position[ i ];
			}
		}

		@Override
		public int getIntPosition( int d )
		{
			return ( int ) position[ d ];
		}

		@Override
		public long getLongPosition( int d )
		{
			return position[ d ];
		}

		@Override
		public void localize( float[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				position[ i ] = ( int ) this.position[ i ];
			}
		}

		@Override
		public void localize( double[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				position[ i ] = ( int ) this.position[ i ];
			}
		}

		@Override
		public float getFloatPosition( int d )
		{
			return position[ d ];
		}

		@Override
		public double getDoublePosition( int d )
		{
			return position[ d ];
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
			return new PositionableIterableRegionRandomAccessSafe( source, fac.copy( region ) );
		}

		@Override
		public RandomAccess< IterableInterval< T > > copyRandomAccess()
		{
			return copy();
		}

	}

	// workaround for copy
	public interface PositionableIntervalFactory< P extends Positionable & IterableInterval< Void > >
	{
		P create();

		P copy( P source );
	}

}
