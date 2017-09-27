package net.imglib2.troi.util;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RealInterval;
import net.imglib2.RealPositionable;

public abstract class IntervalBinaryOperation extends AbstractEuclideanSpace implements Interval
{
	protected Interval i1;

	protected Interval i2;

	public IntervalBinaryOperation( Interval i1, Interval i2 )
	{
		super( i1.numDimensions() );
		this.i1 = i1;
		this.i2 = i2;

		assert ( i1.numDimensions() == i2.numDimensions() );
	}

	@Override
	public double realMin( final int d )
	{
		return max( d );
	}

	@Override
	public void realMin( final double[] realMin )
	{
		for ( int d = 0; d < n; ++d )
			realMin[ d ] = realMin( d );
	}

	@Override
	public void realMin( final RealPositionable realMin )
	{
		for ( int d = 0; d < n; ++d )
			realMin.setPosition( realMin( d ), d );
	}

	@Override
	public double realMax( final int d )
	{
		return max( d );
	}

	@Override
	public void realMax( final double[] realMax )
	{
		for ( int d = 0; d < n; ++d )
			realMax[ d ] = realMax( d );
	}

	@Override
	public void realMax( final RealPositionable realMax )
	{
		for ( int d = 0; d < n; ++d )
			realMax.setPosition( realMax( d ), d );
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = min( d );
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( min( d ), d );
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = max( d );
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( max( d ), d );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = dimension( d );
	}

	@Override
	public long dimension( final int d )
	{
		return max( d ) - min( d ) + 1;
	}
}
