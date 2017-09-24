package net.imglib2.troi;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import net.imglib2.EuclideanSpace;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RealInterval;
import net.imglib2.troi.MaskPredicate.BoundaryType;
import net.imglib2.troi.util.AbstractMask;
import net.imglib2.troi.util.AbstractMaskInterval;
import net.imglib2.troi.util.BoundaryUtil;
import net.imglib2.util.Intervals;

/**
 * Utility class for working with {@link Mask}s.
 *
 * @author Curtis Rueden
 * @author Alison Walter
 */
public class Masks
{
	static Mask and( Mask left, Predicate< ? super Localizable > right )
	{
		int n = checkDimensions( left, right );
		final BoundaryType boundary = BoundaryUtil.and( left, right );
		final Optional< Interval > interval = intervalIntersection( left, right );
		if ( interval.isPresent() )
			return new AbstractMaskInterval( interval.get(), boundary )
			{
				@Override
				public boolean test( final Localizable pos )
				{
					return left.test( pos ) && right.test( pos );
				}
			};
		else
			return new AbstractMask( n, boundary )
			{
				@Override
				public boolean test( final Localizable pos )
				{
					return left.test( pos ) && right.test( pos );
				}
			};
	}

	static MaskInterval and( MaskInterval left, Predicate< ? super Localizable > right )
	{
		throw new UnsupportedOperationException( "not implemented yet" );
	}

	static MaskInterval and( MaskInterval left, Mask right )
	{
		throw new UnsupportedOperationException( "not implemented yet" );
	}

	static MaskInterval and( Mask left, MaskInterval right )
	{
		throw new UnsupportedOperationException( "not implemented yet" );
	}

//	static RealMask and( RealMask left, RealMask right )
//	{
//		throw new UnsupportedOperationException( "not implemented yet" );
//	}
//
//	static RealMaskRealInterval and( RealMaskRealInterval left, RealMaskRealInterval right )
//	{
//		throw new UnsupportedOperationException( "not implemented yet" );
//	}
//
//	static RealMaskRealInterval and( RealMaskRealInterval left, RealMask right )
//	{
//		throw new UnsupportedOperationException( "not implemented yet" );
//	}
//
//	static RealMaskRealInterval and( RealMask left, RealMaskRealInterval right )
//	{
//		throw new UnsupportedOperationException( "not implemented yet" );
//	}

	static Optional< Interval > intervalIntersection( Object... args )
	{
		return Arrays.stream( args )
				.map( i -> i instanceof Interval ? ( Interval ) i
						: i instanceof RealInterval ? Intervals.smallestContainingInterval( ( RealInterval ) i )
						: null )
				.filter( i -> i != null )
				.reduce( ( i, j ) -> Intervals.intersect( i, j ) );
	}

	static Optional< Interval > intervalUnion( Object... args )
	{
		return Arrays.stream( args )
				.map( i -> i instanceof Interval ? ( Interval ) i
						: i instanceof RealInterval ? Intervals.smallestContainingInterval( ( RealInterval ) i )
						: null )
				.filter( i -> i != null )
				.reduce( ( i, j ) -> Intervals.union( i, j ) );
	}

	private static int checkDimensions( Object... args )
	{
		int[] dimensionalities = Arrays.stream( args )
				.filter( EuclideanSpace.class::isInstance )
				.mapToInt( arg -> ( ( EuclideanSpace ) arg ).numDimensions() )
				.distinct()
				.toArray();
		switch ( dimensionalities.length )
		{
		case 0:
			throw new IllegalArgumentException( "couldn't find dimensionality" );
		case 1:
			return dimensionalities[ 0 ];
		default:
			throw new IllegalArgumentException( "incompatible dimensionalities" );
		}
	}
}
