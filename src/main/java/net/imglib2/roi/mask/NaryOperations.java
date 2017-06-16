package net.imglib2.roi.mask;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.mask.Mask.BoundaryType;
import net.imglib2.roi.mask.MaskOperationResult.Operation;
import net.imglib2.roi.mask.integer.MaskIntervalOperationResult;
import net.imglib2.roi.mask.integer.MaskInterval;
import net.imglib2.roi.mask.real.MaskRealIntervalOperationResult;
import net.imglib2.roi.mask.real.MaskRealInterval;

/**
 * Utility class for obtaining {@link Function}s which operate on n &gt; 2
 * {@link Mask}s.
 *
 * @author Alison Walter
 */
public final class NaryOperations
{

	private NaryOperations()
	{
		// NB: Prevent instantiation of utility class.
	}

	// -- OR --

	/**
	 * A function which results in the union of {@code n} discrete space Masks.
	 */
	public static Function< List< Mask< Localizable > >, Mask< Localizable > > or()
	{
		return ( operands ) -> {
			final Predicate< Localizable > p = ( b ) -> {
				boolean in = false;
				for ( int i = 0; i < operands.size(); i++ )
					in |= operands.get( i ).test( b );
				return in;
			};
			return new DefaultMaskOperationResult<>( p, orBoundaryType( operands ), operands, Operation.OR );
		};
	}

	/**
	 * A function which unions {@code n} {@link MaskInterval}s. The result is
	 * also a {@link MaskInterval}.
	 */
	public static Function< List< MaskInterval >, MaskInterval > intervalOr()
	{
		return ( operands ) -> {
			final Predicate< Localizable > p = ( b ) -> {
				boolean in = false;
				for ( int i = 0; i < operands.size(); i++ )
					in |= operands.get( i ).test( b );
				return in;
			};
			return new MaskIntervalOperationResult<>( p, orBoundaryType( operands ), operands, Operation.OR, createOrMinMax( operands ) );
		};
	}

	/** A function which results in the union of {@code n} real space Masks. */
	public static Function< List< Mask< RealLocalizable > >, Mask< RealLocalizable > > realOr()
	{
		return ( operands ) -> {
			final Predicate< RealLocalizable > p = ( b ) -> {
				boolean in = false;
				for ( int i = 0; i < operands.size(); i++ )
					in |= operands.get( i ).test( b );
				return in;
			};
			return new DefaultMaskOperationResult<>( p, orBoundaryType( operands ), operands, Operation.OR );
		};
	}

	/**
	 * A function which unions {@code n} {@link MaskRealInterval}s. The result
	 * is also a {@link MaskRealInterval}.
	 */
	public static Function< List< MaskRealInterval >, MaskRealInterval > realIntervalOr()
	{
		return ( operands ) -> {
			final Predicate< RealLocalizable > p = ( b ) -> {
				boolean in = false;
				for ( int i = 0; i < operands.size(); i++ )
					in |= operands.get( i ).test( b );
				return in;
			};
			return new MaskRealIntervalOperationResult<>( p, orBoundaryType( operands ), operands, Operation.OR, createRealOrMinMax( operands ) );
		};
	}

	// -- Helper methods --

	/**
	 * Computes the boundary type of the mask which results from an OR.
	 *
	 * @param operands
	 *            list containing the operands
	 * @return boundary type of the resulting mask
	 */
	private static < L, M extends Mask< L > > BoundaryType orBoundaryType( final List< M > operands )
	{
		final BoundaryType bt = operands.get( 0 ).boundaryType();
		for ( int i = 1; i < operands.size(); i++ )
		{
			if ( !operands.get( i ).boundaryType().equals( bt ) )
				return BoundaryType.UNSPECIFIED;
		}
		return bt;
	}

	/**
	 * Computes the interval of a mask which results from the union of {@code n}
	 * {@link MaskIntervals}.
	 *
	 * @param operands
	 *            list containing the operands
	 * @return an interval encompassing the resulting mask
	 */
	private static Interval createOrMinMax( final List< MaskInterval > operands )
	{
		final long[] min = new long[ operands.iterator().next().numDimensions() ];
		final long[] max = new long[ operands.iterator().next().numDimensions() ];

		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = operands.get( 0 ).min( d );
			max[ d ] = operands.get( 0 ).max( d );
			for ( int i = 1; i < operands.size(); i++ )
			{
				if ( operands.get( i ).min( d ) < min[ d ] )
					min[ d ] = operands.get( i ).min( d );
				if ( operands.get( i ).max( d ) > max[ d ] )
					max[ d ] = operands.get( i ).max( d );
			}
		}

		return new FinalInterval( min, max );
	}

	/**
	 * Computes the interval of a mask which results from the union of {@code n}
	 * {@link MaskRealIntervals}.
	 *
	 * @param operands
	 *            list containing the operands
	 * @return an interval encompassing the resulting mask
	 */
	private static RealInterval createRealOrMinMax( final List< MaskRealInterval > operands )
	{
		final double[] min = new double[ operands.iterator().next().numDimensions() ];
		final double[] max = new double[ operands.iterator().next().numDimensions() ];

		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = operands.get( 0 ).realMin( d );
			max[ d ] = operands.get( 0 ).realMax( d );
			for ( int i = 1; i < operands.size(); i++ )
			{
				if ( operands.get( i ).realMin( d ) < min[ d ] )
					min[ d ] = operands.get( i ).realMin( d );
				if ( operands.get( i ).realMax( d ) > max[ d ] )
					max[ d ] = operands.get( i ).realMax( d );
			}
		}

		return new FinalRealInterval( min, max );
	}
}
