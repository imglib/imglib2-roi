package net.imglib2.roi;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.util.IterableRandomAccessibleRegion;
import net.imglib2.roi.util.SamplingIterableInterval;
import net.imglib2.type.BooleanType;

public class Regions
{
	// TODO: make Positionable and Localizable
	// TODO: bind to (respectively sample from) RandomAccessible
	// TODO: out-of-bounds / clipping

	public static < B extends BooleanType< B >, T > IterableInterval< T > sample( final IterableInterval< B > region, final RandomAccessible< T > img )
	{
		return SamplingIterableInterval.create( region, img );
	}

	@SuppressWarnings( "unchecked" )
	public static < B extends BooleanType< B > > IterableRegion< B > iterable( final RandomAccessibleInterval< B > region )
	{
		if ( region instanceof IterableRegion )
			return ( IterableRegion< B > ) region;
		else
			return IterableRandomAccessibleRegion.create( region );
	}
}
