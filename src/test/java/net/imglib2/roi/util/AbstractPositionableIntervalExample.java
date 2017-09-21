package net.imglib2.roi.util;

import net.imglib2.FinalInterval;
import net.imglib2.util.Intervals;

public class AbstractPositionableIntervalExample
{
	public static void main( final String[] args )
	{
		final FinalInterval source = Intervals.createMinMax( 10, 10, 20, 20 );
		PositionableInterval interval = new PositionableInterval( source );

		System.out.println( interval );
		System.out.println( "min0 = " + interval.min( 0 ) );
		System.out.println( "pos0 = " + interval.getLongPosition( 0 ) );
		System.out.println( "orig0 = " + interval.origin().getLongPosition( 0 ) );
		System.out.println();

		source.min( interval.origin() );
		System.out.println( "source.min( interval.origin() );" );
		System.out.println( interval );
		System.out.println( "min0 = " + interval.min( 0 ) );
		System.out.println( "pos0 = " + interval.getLongPosition( 0 ) );
		System.out.println( "orig0 = " + interval.origin().getLongPosition( 0 ) );
		System.out.println();

		interval.fwd( 0 );
		System.out.println( "interval.fwd( 0 );" );
		System.out.println( interval );
		System.out.println( "min0 = " + interval.min( 0 ) );
		System.out.println( "pos0 = " + interval.getLongPosition( 0 ) );
		System.out.println( "orig0 = " + interval.origin().getLongPosition( 0 ) );
		System.out.println();

		System.out.println( " -------------------- ");
		System.out.println();
		interval = new PositionableInterval( source );

		System.out.println( interval );
		System.out.println( "min0 = " + interval.min( 0 ) );
		System.out.println( "pos0 = " + interval.getLongPosition( 0 ) );
		System.out.println( "orig0 = " + interval.origin().getLongPosition( 0 ) );
		System.out.println();

		interval.fwd( 0 );
		System.out.println( "interval.fwd( 0 );" );
		System.out.println( interval );
		System.out.println( "min0 = " + interval.min( 0 ) );
		System.out.println( "pos0 = " + interval.getLongPosition( 0 ) );
		System.out.println( "orig0 = " + interval.origin().getLongPosition( 0 ) );
		System.out.println();

		source.min( interval.origin() );
		System.out.println( "source.min( interval.origin() );" );
		System.out.println( interval );
		System.out.println( "min0 = " + interval.min( 0 ) );
		System.out.println( "pos0 = " + interval.getLongPosition( 0 ) );
		System.out.println( "orig0 = " + interval.origin().getLongPosition( 0 ) );
		System.out.println();
	}
}
