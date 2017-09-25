package net.imglib2.troi.util;

import net.imglib2.FinalRealInterval;
import net.imglib2.RealInterval;

// TODO: the following methods should move to net.imglib2.util.Intervals
public class TODO_Intervals
{
	/**
	 * Check whether the given interval is empty, that is, the maximum is
	 * smaller than the minimum in some dimension.
	 *
	 * @param interval
	 *            interval to check
	 * @return true when the interval is empty, that is, the maximum is smaller
	 *         than the minimum in some dimension.
	 */
	public static boolean isEmpty( final RealInterval interval )
	{
		final int n = interval.numDimensions();
		for ( int d = 0; d < n; ++d )
			if ( interval.realMin( d ) > interval.realMax( d ) )
				return true;
		return false;
	}

	/**
	 * Compute the intersection of two intervals.
	 *
	 * Create a {@link RealInterval} , which is the intersection of the input
	 * intervals (i.e., the area contained in both input intervals).
	 *
	 * @param intervalA
	 *            input interval
	 * @param intervalB
	 *            input interval
	 * @return intersection of input intervals
	 */
	public static FinalRealInterval intersect( final RealInterval intervalA, final RealInterval intervalB )
	{
		assert intervalA.numDimensions() == intervalB.numDimensions();

		final int n = intervalA.numDimensions();
		final double[] min = new double[ n ];
		final double[] max = new double[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = Math.max( intervalA.realMin( d ), intervalB.realMin( d ) );
			max[ d ] = Math.min( intervalA.realMax( d ), intervalB.realMax( d ) );
		}
		return new FinalRealInterval( min, max );
	}

	/**
	 * Compute the smallest interval that contains both input intervals.
	 *
	 * Create a {@link RealInterval} that represents that interval.
	 *
	 * @param intervalA
	 *            input interval
	 * @param intervalB
	 *            input interval
	 * @return union of input intervals
	 */
	public static FinalRealInterval union( final RealInterval intervalA, final RealInterval intervalB )
	{
		assert intervalA.numDimensions() == intervalB.numDimensions();

		final int n = intervalA.numDimensions();
		final double[] min = new double[ n ];
		final double[] max = new double[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = Math.min( intervalA.realMin( d ), intervalB.realMin( d ) );
			max[ d ] = Math.max( intervalA.realMax( d ), intervalB.realMax( d ) );
		}
		return new FinalRealInterval( min, max );
	}
}
