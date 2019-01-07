package net.imglib2.roi.sparse.util;

import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;

/**
 * @author Tobias Pietzsch
 */
public interface DefaultInterval extends Interval
{
	@Override
	default double realMin( final int d )
	{
		return min( d );
	}

	@Override
	default void realMin( final double[] min )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			min[ d ] = min( d );
	}

	@Override
	default void realMin( final RealPositionable min )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			min.setPosition( min( d ), d );
	}

	@Override
	default double realMax( final int d )
	{
		return max( d );
	}

	@Override
	default void realMax( final double[] max )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			max[ d ] = max( d );
	}

	@Override
	default void realMax( final RealPositionable max )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			max.setPosition( max( d ), d );
	}

	@Override
	default void dimensions( final long[] dimensions )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			dimensions[ d ] = dimension( d );
	}

	@Override
	default void min( final long[] min )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			min[ d ] = min( d );
	}

	@Override
	default void min( final Positionable min )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			min.setPosition( min( d ), d );
	}

	@Override
	default void max( final long[] max )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			max[ d ] = max( d );
	}

	@Override
	default void max( final Positionable max )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			max.setPosition( max( d ), d );
	}
}
