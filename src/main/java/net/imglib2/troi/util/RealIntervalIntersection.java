package net.imglib2.troi.util;

import net.imglib2.RealInterval;

public class RealIntervalIntersection extends RealIntervalBinaryOperation
{
	public RealIntervalIntersection( final RealInterval i1, final RealInterval i2 )
	{
		super( i1, i2 );
	}

	@Override
	public double realMin( final int d )
	{
		final double min = Math.max( i1.realMin( d ), i2.realMin( d ) );
		final double max = Math.min( i1.realMax( d ), i2.realMax( d ) );
		return min <= max ? min : Double.POSITIVE_INFINITY;
	}

	@Override
	public double realMax( final int d )
	{
		final double min = Math.max( i1.realMin( d ), i2.realMin( d ) );
		final double max = Math.min( i1.realMax( d ), i2.realMax( d ) );
		return min <= max ? max : Double.NEGATIVE_INFINITY;
	}
}
