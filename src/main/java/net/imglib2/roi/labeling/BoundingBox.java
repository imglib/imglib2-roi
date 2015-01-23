package net.imglib2.roi.labeling;

import java.util.Arrays;

import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;

/**
 * A bounding box {@link Interval} around a region that is build by
 * {@link #update(Localizable) aggregating} positions contained in the region.
 *
 * @author Lee Kamentsky
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class BoundingBox extends AbstractInterval
{
	public BoundingBox( final int n )
	{
		super( n );
		Arrays.fill( max, Long.MIN_VALUE );
		Arrays.fill( min, Long.MAX_VALUE );
	}

	/**
	 * update the minimum and maximum extents with the given coordinates.
	 *
	 * @param position
	 */
	public void update( final Localizable position )
	{
		for ( int d = 0; d < min.length; d++ )
		{
			final long p = position.getLongPosition( d );
			if ( p < min[ d ] )
				min[ d ] = p;
			if ( p > max[ d ] )
				max[ d ] = p;
		}
	}

	/**
	 * update the minimum and maximum extents with the given coordinates.
	 *
	 * @param position
	 */
	public void update( final long[] position )
	{
		for ( int d = 0; d < min.length; d++ )
		{
			if ( position[ d ] < min[ d ] )
				min[ d ] = position[ d ];
			if ( position[ d ] > max[ d ] )
				max[ d ] = position[ d ];
		}
	}
}
