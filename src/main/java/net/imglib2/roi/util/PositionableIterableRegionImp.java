package net.imglib2.roi.util;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.PositionableIterableRegion;
import net.imglib2.type.BooleanType;

public class PositionableIterableRegionImp< T extends BooleanType< T > >
		extends PositionableIterableInterval< Void, IterableRegion< T > >
		implements PositionableIterableRegion< T >
{
	public PositionableIterableRegionImp( final IterableRegion< T > source )
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
		return randomAccess();
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
}
