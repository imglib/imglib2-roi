package net.imglib2.troi;

import java.util.Arrays;
import java.util.function.Predicate;

import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.troi.mask.integer.DefaultMask;
import net.imglib2.troi.mask.integer.DefaultMaskInterval;
import net.imglib2.troi.mask.integer.MaskAsRandomAccessible;
import net.imglib2.troi.mask.integer.MaskIntervalAsRandomAccessibleInterval;
import net.imglib2.troi.mask.integer.RandomAccessibleAsMask;
import net.imglib2.troi.mask.integer.RandomAccessibleIntervalAsMaskInterval;
import net.imglib2.troi.mask.real.DefaultRealMask;
import net.imglib2.troi.mask.real.DefaultRealMaskRealInterval;
import net.imglib2.troi.mask.real.RealMaskAsRealRandomAccessible;
import net.imglib2.troi.mask.real.RealMaskRealIntervalAsRealRandomAccessibleRealInterval;
import net.imglib2.troi.mask.real.RealRandomAccessibleAsRealMask;
import net.imglib2.troi.mask.real.RealRandomAccessibleRealIntervalAsRealMaskRealInterval;
import net.imglib2.type.BooleanType;

/**
 * Utility class for working with {@link Mask}s.
 *
 * @author Curtis Rueden
 * @author Alison Walter
 * @author Tobias Pietzsch
 */
public class Masks
{
	/*
	 * Methods for integer masks
	 * ===============================================================
	 */

	public static Mask and( final Mask left, final Predicate< ? super Localizable > right )
	{
		return left.and( right );
	}

	public static MaskInterval and( final MaskInterval left, final Predicate< ? super Localizable > right )
	{
		return left.and( right );
	}

	public static Mask or( final Mask left, final Predicate< ? super Localizable > right )
	{
		return left.or( right );
	}

	public static MaskInterval or( final MaskInterval left, final MaskInterval right )
	{
		return left.or( right );
	}

	public static Mask xor( final Mask left, final Predicate< ? super Localizable > right )
	{
		return left.xor( right );
	}

	public static MaskInterval xor( final MaskInterval left, final MaskInterval right )
	{
		return left.xor( right );
	}

	public static Mask minus( final Mask left, final Predicate< ? super Localizable > right )
	{
		return left.minus( right );
	}

	public static MaskInterval minus( final MaskInterval left, final Predicate< ? super Localizable > right )
	{
		return left.minus( right );
	}

	public static Mask negate( final Mask arg )
	{
		return arg.negate();
	}

	/*
	 * Methods for real masks
	 * ===============================================================
	 */

	public static RealMask and( final RealMask left, final Predicate< ? super RealLocalizable > right )
	{
		return left.and( right );
	}

	public static RealMaskRealInterval and( final RealMaskRealInterval left, final Predicate< ? super RealLocalizable > right )
	{
		return left.and( right );
	}

	public static RealMask or( final RealMask left, final Predicate< ? super RealLocalizable > right )
	{
		return left.or( right );
	}

	public static RealMaskRealInterval or( final RealMaskRealInterval left, final RealMaskRealInterval right )
	{
		return left.or( right );
	}

	public static RealMask xor( final RealMask left, final Predicate< ? super RealLocalizable > right )
	{
		return left.xor( right );
	}

	public static RealMaskRealInterval xor( final RealMaskRealInterval left, final RealMaskRealInterval right )
	{
		return left.xor( right );
	}

	public static RealMask minus( final RealMask left, final Predicate< ? super RealLocalizable > right )
	{
		return left.minus( right );
	}

	public static RealMaskRealInterval minus( final RealMaskRealInterval left, final Predicate< ? super RealLocalizable > right )
	{
		return left.minus( right );
	}

	public static RealMask negate( final RealMask arg )
	{
		return arg.negate();
	}

	/*
	 * RandomAccessible Wrappers
	 * ===============================================================
	 */

	/**
	 * Wraps the given {@link Mask} as a {@link RandomAccessible} of
	 * {@link BooleanType}. If the given Mask is a {@link MaskInterval}, a
	 * {@link RandomAccessibleInterval} is returned.
	 *
	 * @param mask
	 *            {@link Mask} to be wrapped
	 * @param type
	 *            {@link BooleanType} of the resulting RandomAccessible
	 * @return a RandomAccessible equivalent to the given Mask
	 */
	public static < B extends BooleanType< B > > RandomAccessible< B > toRandomAccessible( final Mask mask, final B type )
	{
		if ( mask instanceof MaskInterval )
			return toRandomAccessibleInterval( ( MaskInterval ) mask, type );
		return new MaskAsRandomAccessible<>( mask, type );
	}

	/**
	 * Wraps the given {@link MaskInterval} as a
	 * {@link RandomAccessibleInterval} of {@link BooleanType}.
	 *
	 * @param mask
	 *            {@link MaskInterval} to be wrapped
	 * @param type
	 *            {@link BooleanType} of the resulting RandomAccessibleInterval
	 * @return RandomAccessibleInterval equivalent to given MaskInterval
	 */
	public static < B extends BooleanType< B > > RandomAccessibleInterval< B > toRandomAccessibleInterval( final MaskInterval mask, final B type )
	{
		return new MaskIntervalAsRandomAccessibleInterval<>( mask, type );
	}

	/**
	 * Wraps the given {@link RealMask} as a {@link RealRandomAccessible} of
	 * {@link BooleanType}. If the given RealMask is a
	 * {@link RealMaskRealInterval}, a {@link RealRandomAccessibleRealInterval}
	 * is returned.
	 *
	 * @param mask
	 *            {@link RealMask} to be wrapped
	 * @param type
	 *            {@link BooleanType} of the resulting RealRandomAccessible
	 * @return RealRandomAccessible equivalent to the given RealMask
	 */
	public static < B extends BooleanType< B > > RealRandomAccessible< B > toRealRandomAccessible( final RealMask mask, final B type )
	{
		if ( mask instanceof RealMaskRealInterval )
			return toRealRandomAccessibleRealInterval( ( RealMaskRealInterval ) mask, type );
		return new RealMaskAsRealRandomAccessible<>( mask, type );
	}

	/**
	 * Wraps the given {@link RealMaskRealInterval} as a
	 * {@link RealRandomAccessibleRealInterval} of {@link BooleanType}.
	 *
	 * @param mask
	 *            {@link RealMaskRealInterval} to be wrapped
	 * @param type
	 *            {@link BooleanType} of the resulting
	 *            RealRandomAccessibleRealInterval
	 * @return RealRandomAccessibleRealInterval equivalent to given
	 *         RealMaskRealInterval
	 */
	public static < B extends BooleanType< B > > RealRandomAccessibleRealInterval< B > toRealRandomAccessibleRealInterval( final RealMaskRealInterval mask, final B type )
	{
		return new RealMaskRealIntervalAsRealRandomAccessibleRealInterval<>( mask, type );
	}

	/*
	 * Mask Wrappers
	 * ===============================================================
	 */

	/**
	 * Wraps the given {@link RandomAccessible} as a {@link Mask}. If the given
	 * RandomAccessible is a {@link RandomAccessibleInterval} a
	 * {@link MaskInterval} is returned.
	 *
	 * @param ra
	 *            {@link RandomAccessible} to be wrapped
	 * @return {@link Mask} equivalent to the given {@link RandomAccessible}
	 */
	public static < B extends BooleanType< B > > Mask toMask( final RandomAccessible< B > ra )
	{
		if ( ra instanceof RandomAccessibleInterval )
			return toMaskInterval( ( RandomAccessibleInterval< B > ) ra );
		return new RandomAccessibleAsMask<>( ra );
	}

	/**
	 * Wraps the given {@link RandomAccessibleInterval} as a
	 * {@link MaskInterval}.
	 *
	 * @param rai
	 *            {@link RandomAccessibleInterval} to be wrapped
	 * @return {@link MaskInterval} equivalent to the given
	 *         {@link RandomAccessibleInterval}
	 */
	public static < B extends BooleanType< B > > MaskInterval toMaskInterval( final RandomAccessibleInterval< B > rai )
	{
		return new RandomAccessibleIntervalAsMaskInterval<>( rai );
	}

	/**
	 * Wraps the given {@link RealRandomAccessible} as a {@link RealMask}. If
	 * the given RealRandomAccessible is a
	 * {@link RealRandomAccessibleRealInterval} a {@link RealMaskRealInterval}
	 * is returned.
	 *
	 * @param rra
	 *            {@link RealRandomAccessible} to be wrapped
	 * @return {@link RealMask} equivalent to the given
	 *         {@link RealRandomAccessible}
	 */
	public static < B extends BooleanType< B > > RealMask toRealMask( final RealRandomAccessible< B > rra )
	{
		if ( rra instanceof RealRandomAccessibleRealInterval )
			return toRealMaskRealInterval( ( RealRandomAccessibleRealInterval< B > ) rra );
		return new RealRandomAccessibleAsRealMask<>( rra );
	}

	/**
	 * Wraps the given {@link RealRandomAccessibleRealInterval} as a
	 * {@link RealMaskRealInterval}.
	 *
	 * @param rrari
	 *            {@link RealRandomAccessibleRealInterval} to be wrapped
	 * @return {@link RealMaskRealInterval} equivalent to the given
	 *         {@link RealRandomAccessibleRealInterval}
	 */
	public static < B extends BooleanType< B > > RealMaskRealInterval toRealMaskRealInterval( final RealRandomAccessibleRealInterval< B > rrari )
	{
		return new RealRandomAccessibleRealIntervalAsRealMaskRealInterval<>( rrari );
	}

	/*
	 * Empty Masks
	 * ===============================================================
	 */

	/**
	 * Creates a {@link Mask} which returns {@code false} for every location.
	 *
	 * @param numDims
	 *            number of dimensions the resulting Mask should have
	 * @return {@link Mask} which returns false for all locations
	 */
	public static Mask emptyMask( final int numDims )
	{
		return new DefaultMask( numDims, BoundaryType.UNSPECIFIED, t -> false )
		{
			@Override
			public boolean isEmpty()
			{
				return true;
			}
		};
	}

	/**
	 * Creates a {@link MaskInterval} which returns {@code false} for every
	 * location and has empty interval bounds (i.e. min &gt; max).
	 *
	 * @param numDims
	 *            number of dimensions the resulting MaskInterval should have
	 * @return {@link MaskInterval} which returns false for all locations
	 */
	public static MaskInterval emptyMaskInterval( final int numDims )
	{
		return new DefaultMaskInterval( emptyInterval( numDims ), BoundaryType.UNSPECIFIED, t -> false );
	}

	/**
	 * Creates a {@link RealMask} which returns {@code false} for every
	 * location.
	 *
	 * @param numDims
	 *            number of dimensions the resulting RealMask should have
	 * @return {@link RealMask} which returns false for all locations
	 */
	public static RealMask emptyRealMask( final int numDims )
	{
		return new DefaultRealMask( numDims, BoundaryType.UNSPECIFIED, t -> false )
		{
			@Override
			public boolean isEmpty()
			{
				return true;
			}
		};
	}

	/**
	 * Creates a {@link RealMaskRealInterval} which returns {@code false} for
	 * every location and has empty interval bounds (i.e. min &gt; max).
	 *
	 * @param numDims
	 *            number of dimensions the resulting RealMaskRealInterval should
	 *            have
	 * @return {@link RealMaskRealInterval} which returns false for all
	 *         locations
	 */
	public static RealMaskRealInterval emptyRealMaskRealInterval( final int numDims )
	{
		return new DefaultRealMaskRealInterval( emptyRealInterval( numDims ), BoundaryType.UNSPECIFIED, t -> false );
	}

	/*
	 * Empty Intervals
	 * ===============================================================
	 */

	/**
	 * Creates an {@link Interval} which has min &gt; max for all dimensions.
	 *
	 * @param numDims
	 *            dimensions of the resulting {@link Interval}
	 * @return {@link Interval} with min &gt; max
	 */
	public static Interval emptyInterval( final int numDims )
	{
		final long[] min = new long[ numDims ];
		Arrays.fill( min, Long.MAX_VALUE );
		final long[] max = new long[ numDims ];
		Arrays.fill( max, Long.MIN_VALUE );
		return new FinalInterval( min, max );
	}

	/**
	 * Creates an {@link RealInterval} which has min &gt; max for all
	 * dimensions.
	 *
	 * @param numDims
	 *            dimensions of the resulting {@link RealInterval}
	 * @return {@link RealInterval} with min &gt; max
	 */
	public static RealInterval emptyRealInterval( final int numDims )
	{
		final double[] min = new double[ numDims ];
		Arrays.fill( min, Double.POSITIVE_INFINITY );
		final double[] max = new double[ numDims ];
		Arrays.fill( max, Double.NEGATIVE_INFINITY );
		return new FinalRealInterval( min, max );
	}

	/*
	 * All space Masks
	 * ===============================================================
	 */

	/**
	 * Creates a {@link Mask} which returns {@code true} for all locations.
	 *
	 * @param numDims
	 *            number of dimensions of the {@link Mask}
	 * @return {@link Mask} which is true for all locations
	 */
	public static Mask allMask( final int numDims )
	{
		return new DefaultMask( numDims, BoundaryType.UNSPECIFIED, t -> true )
		{
			@Override
			public boolean isAll()
			{
				return true;
			}
		};
	}

	/**
	 * Creates a {@link RealMask} which returns {@code true} for all locations.
	 *
	 * @param numDims
	 *            number of dimensions of the {@link RealMask}
	 * @return {@link RealMask} which is true for all locations
	 */
	public static RealMask allRealMask( final int numDims )
	{
		return new DefaultRealMask( numDims, BoundaryType.UNSPECIFIED, t -> true )
		{
			@Override
			public boolean isAll()
			{
				return true;
			}
		};
	}
}
