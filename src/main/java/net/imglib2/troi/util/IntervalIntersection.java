package net.imglib2.troi.util;

import net.imglib2.Interval;
import net.imglib2.RealInterval;

public class IntervalIntersection extends IntervalBinaryOperation
{
	public IntervalIntersection( final Interval i1, final Interval i2 )
	{
		super( i1, i2 );
	}

	@Override
	public long min( final int d )
	{
		final long min = Math.max( i1.min( d ), i2.min( d ) );
		final long max = Math.min( i1.max( d ), i2.max( d ) );
		return min <= max ? min : Long.MAX_VALUE;
	}

	@Override
	public long max( final int d )
	{
		final long min = Math.max( i1.min( d ), i2.min( d ) );
		final long max = Math.min( i1.max( d ), i2.max( d ) );
		return min <= max ? max : Long.MIN_VALUE;
	}
}
