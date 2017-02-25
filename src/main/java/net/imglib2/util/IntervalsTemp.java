package net.imglib2.util;

import net.imglib2.Dimensions;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;

// TODO remove as soon as method is available in imglib2-core release (see Intervals.expand(..))
@Deprecated
public class IntervalsTemp
{
	public static FinalInterval expand( final Interval interval, final Dimensions border )
	{
		final int n = interval.numDimensions();
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		interval.min( min );
		interval.max( max );
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] -= border.dimension( d );
			max[ d ] += border.dimension( d );
		}
		return new FinalInterval( min, max );
	}
}
