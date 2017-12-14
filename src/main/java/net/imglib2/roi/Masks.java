/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2017 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.roi;

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
import net.imglib2.roi.mask.integer.DefaultMask;
import net.imglib2.roi.mask.integer.DefaultMaskInterval;
import net.imglib2.roi.mask.integer.MaskAsRandomAccessible;
import net.imglib2.roi.mask.integer.MaskIntervalAsRandomAccessibleInterval;
import net.imglib2.roi.mask.integer.RandomAccessibleAsMask;
import net.imglib2.roi.mask.integer.RandomAccessibleIntervalAsMaskInterval;
import net.imglib2.roi.mask.real.DefaultRealMask;
import net.imglib2.roi.mask.real.DefaultRealMaskRealInterval;
import net.imglib2.roi.mask.real.RealMaskAsRealRandomAccessible;
import net.imglib2.roi.mask.real.RealMaskRealIntervalAsRealRandomAccessibleRealInterval;
import net.imglib2.roi.mask.real.RealRandomAccessibleAsRealMask;
import net.imglib2.roi.mask.real.RealRandomAccessibleRealIntervalAsRealMaskRealInterval;
import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BoolType;

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
	 * {@link BoolType}. If the given Mask is a {@link MaskInterval}, a
	 * {@link RandomAccessibleInterval} is returned.
	 *
	 * @param mask {@link Mask} to be wrapped
	 * @return a RandomAccessible equivalent to the given Mask
	 */
	public static RandomAccessible< BoolType > toRandomAccessible( final Mask mask )
	{
		if ( mask instanceof MaskInterval )
			return toRandomAccessibleInterval( ( MaskInterval ) mask );
		return new MaskAsRandomAccessible<>( mask, new BoolType() );
	}

	/**
	 * Wraps the given {@link MaskInterval} as a {@link RandomAccessibleInterval}
	 * of {@link BoolType}.
	 *
	 * @param mask {@link MaskInterval} to be wrapped
	 * @return RandomAccessibleInterval equivalent to given MaskInterval
	 */
	public static RandomAccessibleInterval< BoolType > toRandomAccessibleInterval( final MaskInterval mask )
	{
		return new MaskIntervalAsRandomAccessibleInterval<>( mask, new BoolType() );
	}

	/**
	 * Wraps the given {@link RealMask} as a {@link RealRandomAccessible} of
	 * {@link BoolType}. If the given RealMask is a {@link RealMaskRealInterval},
	 * a {@link RealRandomAccessibleRealInterval} is returned.
	 *
	 * @param mask {@link RealMask} to be wrapped
	 * @return RealRandomAccessible equivalent to the given RealMask
	 */
	public static RealRandomAccessible< BoolType > toRealRandomAccessible( final RealMask mask )
	{
		if ( mask instanceof RealMaskRealInterval )
			return toRealRandomAccessibleRealInterval( ( RealMaskRealInterval ) mask );
		return new RealMaskAsRealRandomAccessible<>( mask, new BoolType() );
	}

	/**
	 * Wraps the given {@link RealMaskRealInterval} as a
	 * {@link RealRandomAccessibleRealInterval} of {@link BoolType}.
	 *
	 * @param mask
	 *            {@link RealMaskRealInterval} to be wrapped
	 * @return RealRandomAccessibleRealInterval equivalent to given
	 *         RealMaskRealInterval
	 */
	public static RealRandomAccessibleRealInterval< BoolType > toRealRandomAccessibleRealInterval( final RealMaskRealInterval mask )
	{
		return new RealMaskRealIntervalAsRealRandomAccessibleRealInterval<>( mask, new BoolType() );
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
		return new DefaultMask( numDims, BoundaryType.UNSPECIFIED, t -> false, KnownConstant.ALL_FALSE );
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
		return new DefaultMaskInterval( emptyInterval( numDims ), BoundaryType.UNSPECIFIED, t -> false, KnownConstant.ALL_FALSE );
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
		return new DefaultRealMask( numDims, BoundaryType.UNSPECIFIED, t -> false, KnownConstant.ALL_FALSE );
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
		return new DefaultRealMaskRealInterval( emptyRealInterval( numDims ), BoundaryType.UNSPECIFIED, t -> false, KnownConstant.ALL_FALSE );
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
		return new DefaultMask( numDims, BoundaryType.UNSPECIFIED, t -> true, KnownConstant.ALL_TRUE );
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
		return new DefaultRealMask( numDims, BoundaryType.UNSPECIFIED, t -> true, KnownConstant.ALL_TRUE );
	}
}
