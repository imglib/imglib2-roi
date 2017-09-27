package net.imglib2.troi.util;

import net.imglib2.RealInterval;

public class RealIntervalUnion extends RealIntervalBinaryOperation
{
	public RealIntervalUnion( final RealInterval i1, final RealInterval i2 )
	{
		super( i1, i2 );
	}

	@Override
	public double realMin( final int d )
	{
		return Math.min( i1.realMin( d ), i2.realMin( d ) );
	}

	@Override
	public double realMax( final int d )
	{
		return Math.max( i1.realMax( d ), i2.realMax( d ) );
	}
}
