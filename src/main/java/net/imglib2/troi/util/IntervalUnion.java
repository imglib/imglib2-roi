package net.imglib2.troi.util;

import net.imglib2.Interval;
import net.imglib2.RealInterval;

public class IntervalUnion extends IntervalBinaryOperation
{
	public IntervalUnion( final Interval i1, final Interval i2 )
	{
		super( i1, i2 );
	}

	@Override
	public long min( final int d )
	{
		return Math.min( i1.min( d ), i2.min( d ) );
	}

	@Override
	public long max( final int d )
	{
		return Math.max( i1.max( d ), i2.max( d ) );
	}
}
