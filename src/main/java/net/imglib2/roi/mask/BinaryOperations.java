/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.roi.mask;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.roi.Regions;
import net.imglib2.roi.mask.Mask.BoundaryType;
import net.imglib2.roi.mask.MaskOperationResult.Operation;
import net.imglib2.roi.mask.integer.MaskInterval;
import net.imglib2.roi.mask.integer.MaskIntervalOperationResult;
import net.imglib2.roi.mask.real.MaskRealInterval;
import net.imglib2.roi.mask.real.MaskRealIntervalOperationResult;

/**
 * Utility class for obtaining {@link Function}s which operate on two
 * {@link Mask}s.
 *
 * @author Alison Walter
 *
 */
public final class BinaryOperations
{

	private BinaryOperations()
	{
		// NB: Prevent instantiation of utility class.
	}

	// -- AFFINE TRANSFORM --

	/**
	 * Transforms a given {@link Mask} with a given {@link AffineGet}.
	 *
	 * @param numDimensions
	 *            number of dimensions of the {@link Mask}
	 * @return A function which will apply a given affine transform to a given
	 *         mask
	 */
	public static BiFunction< Mask< RealLocalizable >, AffineGet, Mask< RealLocalizable > > transform( final int numDimensions )
	{
		final ThreadLocal< RealPoint > pt = new ThreadLocal< RealPoint >()
		{
			@Override
			protected RealPoint initialValue()
			{
				return new RealPoint( numDimensions );
			}
		};
		return ( left, right ) -> new DefaultMaskOperationResult<>( t -> {
			final RealPoint rp = pt.get();
			right.apply( t, rp );
			return left.test( rp );
		}, left.boundaryType(), Arrays.asList( left, right ), Operation.TRANSFORM );
	}

	/**
	 * Transforms a given {@link MaskRealInterval} with a given
	 * {@link AffineGet}. The result will also be a {@link MaskRealInterval},
	 * but the interval bounds are <strong>not</strong> guaranteed to represent
	 * the minimal bounding box.
	 *
	 * @param numDimensions
	 *            number of dimensions of the {@link Mask}
	 * @return A function which will apply a given affine transform to a given
	 *         mask
	 */
	public static BiFunction< MaskRealInterval, AffineGet, MaskRealInterval > intervalTransform( final int numDimensions )
	{
		final ThreadLocal< RealPoint > pt = new ThreadLocal< RealPoint >()
		{
			@Override
			protected RealPoint initialValue()
			{
				return new RealPoint( numDimensions );
			}
		};
		return ( left, right ) -> new MaskRealIntervalOperationResult<>( t -> {
			final RealPoint rp = pt.get();
			right.apply( t, rp );
			return left.test( rp );
		}, left.boundaryType(), Arrays.asList( left, right ), Operation.TRANSFORM, createTransformMinMax( left, right ) );
	}

	// -- AND --

	/** The intersection of two discrete space Masks. */
	public static BinaryOperator< Mask< Localizable > > and()
	{
		return ( left, right ) -> new DefaultMaskOperationResult<>( right.and( left ), andOrBoundaryType( left, right ), Arrays.asList( left, right ), Operation.AND );
	}

	/**
	 * The intersection of two discrete space {@link MaskInterval}s. The
	 * resulting mask is also a {@link MaskInterval}.
	 */
	public static BinaryOperator< MaskInterval > intervalAnd()
	{
		return ( left, right ) -> new MaskIntervalOperationResult<>( right.and( left ), andOrBoundaryType( left, right ), Arrays.asList( left, right ), Operation.AND, createAndMinMax( left, right ) );
	}

	/** The intersection of two real space Masks. */
	public static BinaryOperator< Mask< RealLocalizable > > realAnd()
	{
		return ( left, right ) -> new DefaultMaskOperationResult<>( right.and( left ), andOrBoundaryType( left, right ), Arrays.asList( left, right ), Operation.AND );
	}

	/**
	 * The intersection of two real space {@link MaskRealInterval}s. The
	 * resulting mask is also a {@link MaskRealInterval}.
	 */
	public static BinaryOperator< MaskRealInterval > realIntervalAnd()
	{
		final BinaryOperator< MaskRealInterval > and = ( left, right ) -> {
			return new MaskRealIntervalOperationResult<>( right.and( left ), andOrBoundaryType( left, right ), Arrays.asList( left, right ), Operation.AND, createAndMinMax( left, right ) );
		};
		return and;
	}

	// -- OR --

	/** The union of two discrete space Masks. */
	public static BinaryOperator< Mask< Localizable > > or()
	{
		return ( left, right ) -> new DefaultMaskOperationResult<>( right.or( left ), andOrBoundaryType( left, right ), Arrays.asList( left, right ), Operation.OR );
	}

	/**
	 * The union of two discrete space {@link MaskInterval}s. The resulting mask
	 * is also a {@link MaskInterval}.
	 */
	public static BinaryOperator< MaskInterval > intervalOr()
	{
		return ( left, right ) -> new MaskIntervalOperationResult<>( right.or( left ), andOrBoundaryType( left, right ), Arrays.asList( left, right ), Operation.OR, createOrMinMax( left, right ) );
	}

	/** The union of two real space Masks. */
	public static BinaryOperator< Mask< RealLocalizable > > realOr()
	{
		return ( left, right ) -> new DefaultMaskOperationResult<>( right.or( left ), andOrBoundaryType( left, right ), Arrays.asList( left, right ), Operation.OR );
	}

	/**
	 * The union of two real space {@link MaskRealInterval}s. The resulting mask
	 * is also a {@link MaskRealInterval}.
	 */
	public static BinaryOperator< MaskRealInterval > realIntervalOr()
	{
		return ( left, right ) -> new MaskRealIntervalOperationResult<>( right.or( left ), andOrBoundaryType( left, right ), Arrays.asList( left, right ), Operation.OR, createOrMinMax( left, right ) );
	}

	// -- SUBTRACT --

	/**
	 * The subtraction of two masks in discrete space. Subtraction is defined as
	 * inside the left operand AND NOT inside the right operand.
	 */
	public static BinaryOperator< Mask< Localizable > > subtract()
	{
		return ( left, right ) -> new DefaultMaskOperationResult<>( ( l ) -> left.test( l ) && !right.test( l ), subtractBoundaryType( left, right ), Arrays.asList( left, right ), Operation.SUBTRACT );
	}

	/**
	 * The subtraction of two {@link MaskInterval}s. Subtraction is defined as
	 * inside the left operand AND NOT inside the right operand. The result is
	 * also a {@link MaskInterval}. This method does not guarantee that the
	 * returned interval is the minimum bounding box.
	 */
	public static BinaryOperator< MaskInterval > intervalSubtract()
	{
		return ( left, right ) -> new MaskIntervalOperationResult<>( ( l ) -> left.test( l ) && !right.test( l ), subtractBoundaryType( left, right ), Arrays.asList( left, right ), Operation.SUBTRACT, createSubtractMinMax( left ) );
	}

	/**
	 * The subtraction of two masks in real space. Subtraction is defined as
	 * inside the left operand AND NOT inside the right operand.
	 */
	public static BinaryOperator< Mask< RealLocalizable > > realSubtract()
	{
		return ( left, right ) -> new DefaultMaskOperationResult<>( ( l ) -> left.test( l ) && !right.test( l ), subtractBoundaryType( left, right ), Arrays.asList( left, right ), Operation.SUBTRACT );
	}

	/**
	 * The subtraction of two {@link MaskRealInterval}s. Subtraction is defined
	 * as inside the left operand AND NOT inside the right operand. The result
	 * is also a {@link MaskRealInterval}. This method does not guarantee that
	 * the returned interval is the minimum bounding box.
	 */
	public static BinaryOperator< MaskRealInterval > realIntervalSubtract()
	{
		return ( left, right ) -> new MaskRealIntervalOperationResult<>( ( l ) -> left.test( l ) && !right.test( l ), subtractBoundaryType( left, right ), Arrays.asList( left, right ), Operation.SUBTRACT, createSubtractMinMax( left ) );
	}

	// -- XOR --

	/** An exclusive or between two discrete space masks. */
	public static BinaryOperator< Mask< Localizable > > xor()
	{
		return ( left, right ) -> new DefaultMaskOperationResult<>( ( l ) -> left.test( l ) ^ right.test( l ), BoundaryType.UNSPECIFIED, Arrays.asList( left, right ), Operation.XOR );
	}

	/**
	 * An exclusive or between two discrete space {@link MaskInterval}s. A mask
	 * resulting from this function also knows its interval.
	 */
	public static BinaryOperator< MaskInterval > intervalXor()
	{
		return ( left, right ) -> new MaskIntervalOperationResult<>( ( l ) -> left.test( l ) ^ right.test( l ), BoundaryType.UNSPECIFIED, Arrays.asList( left, right ), Operation.XOR, createOrMinMax( left, right ) );
	}

	/** An exclusive or between two real space masks. */
	public static BinaryOperator< Mask< RealLocalizable > > realXor()
	{
		return ( left, right ) -> new DefaultMaskOperationResult<>( ( l ) -> left.test( l ) ^ right.test( l ), BoundaryType.UNSPECIFIED, Arrays.asList( left, right ), Operation.XOR );
	}

	/**
	 * An exclusive or between two real space {@link MaskRealInterval}s. A mask
	 * resulting from this function also knows its interval.
	 */
	public static BinaryOperator< MaskRealInterval > realIntervalXor()
	{
		return ( left, right ) -> new MaskRealIntervalOperationResult<>( ( l ) -> left.test( l ) ^ right.test( l ), BoundaryType.UNSPECIFIED, Arrays.asList( left, right ), Operation.XOR, createOrMinMax( left, right ) );
	}

	// -- Helper methods --

	/**
	 * Computes the boundary behavior for AND and OR of Masks.
	 *
	 * @param left
	 *            left operand
	 * @param right
	 *            right operand
	 * @return if the two masks have the same boundary behavior then that is
	 *         returned, otherwise the boundary behavior is unspecified
	 */
	private static < L > BoundaryType andOrBoundaryType( final Mask< L > left, final Mask< L > right )
	{
		return right.boundaryType() == left.boundaryType() ? right.boundaryType() : BoundaryType.UNSPECIFIED;
	}

	/**
	 * Computes the boundary behavior for subtraction of two Masks.
	 *
	 * @param left
	 *            left operand
	 * @param right
	 *            right operand
	 * @return the boundary type of the resulting mask
	 */
	private static < L > BoundaryType subtractBoundaryType( final Mask< L > left, final Mask< L > right )
	{
		BoundaryType b = BoundaryType.UNSPECIFIED;
		if ( left.boundaryType() != right.boundaryType() && left.boundaryType() != BoundaryType.UNSPECIFIED && right.boundaryType() != BoundaryType.UNSPECIFIED )
			b = left.boundaryType();
		return b;
	}

	/**
	 * Computes the interval which results from the intersection of two
	 * {@link MaskInterval}s.
	 *
	 * @param left
	 *            left operand
	 * @param right
	 *            right operand
	 * @return an {@link Interval} which encompasses the resulting Mask
	 */
	private static Interval createAndMinMax( final MaskInterval left, final MaskInterval right )
	{
		final long[] min = new long[ left.numDimensions() ];
		final long[] max = new long[ left.numDimensions() ];
		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = left.min( d ) > right.min( d ) ? left.min( d ) : right.min( d );
			max[ d ] = left.max( d ) < right.max( d ) ? left.max( d ) : right.max( d );
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Computes the interval which results from the intersection of two
	 * {@link MaskRealInterval}s.
	 *
	 * @param left
	 *            left operand
	 * @param right
	 *            right operand
	 * @return a {@link RealInterval} which encompasses the resulting Mask
	 */
	private static RealInterval createAndMinMax( final MaskRealInterval left, final MaskRealInterval right )
	{
		final double[] min = new double[ left.numDimensions() ];
		final double[] max = new double[ left.numDimensions() ];
		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = left.realMin( d ) > right.realMin( d ) ? left.realMin( d ) : right.realMin( d );
			max[ d ] = left.realMax( d ) < right.realMax( d ) ? left.realMax( d ) : right.realMax( d );
		}
		return new FinalRealInterval( min, max );
	}

	/**
	 * Computes the interval which results from the union and exclusive or of
	 * two {@link MaskRealInterval}s.
	 *
	 * @param left
	 *            left operand
	 * @param right
	 *            right operand
	 * @return a {@link RealInterval} which encompasses the resulting Mask
	 */
	private static Interval createOrMinMax( final MaskInterval left, final MaskInterval right )
	{
		final long[] min = new long[ left.numDimensions() ];
		final long[] max = new long[ left.numDimensions() ];
		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = left.min( d ) < right.min( d ) ? left.min( d ) : right.min( d );
			max[ d ] = left.max( d ) > right.max( d ) ? left.max( d ) : right.max( d );
		}
		return new FinalInterval( min, max );
	}

	/**
	 * Computes the interval which results from the union and exclusive or of
	 * two {@link MaskInterval}s.
	 *
	 * @param left
	 *            left operand
	 * @param right
	 *            right operand
	 * @return an {@link Interval} which encompasses the resulting Mask
	 */
	private static RealInterval createOrMinMax( final MaskRealInterval left, final MaskRealInterval right )
	{
		final double[] min = new double[ left.numDimensions() ];
		final double[] max = new double[ left.numDimensions() ];
		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = left.realMin( d ) < right.realMin( d ) ? left.realMin( d ) : right.realMin( d );
			max[ d ] = left.realMax( d ) > right.realMax( d ) ? left.realMax( d ) : right.realMax( d );
		}
		return new FinalRealInterval( min, max );
	}

	/**
	 * Returns the given operands interval. In this case of subtraction, this
	 * returns an interval which will contain the entire result. However, it may
	 * not be the minimal interval.
	 *
	 * @param left
	 *            the left operand in the subtraction
	 * @return the left operands interval
	 */
	private static Interval createSubtractMinMax( final MaskInterval left )
	{
		final long[] min = new long[ left.numDimensions() ];
		final long[] max = new long[ left.numDimensions() ];
		left.min( min );
		left.max( max );
		return new FinalInterval( min, max );
	}

	/**
	 * Returns the given operands interval. In this case of subtraction, this
	 * returns an interval which will contain the entire result. However, it may
	 * not be the minimal interval.
	 *
	 * @param left
	 *            the left operand in the subtraction
	 * @return the left operands interval
	 */
	private static RealInterval createSubtractMinMax( final MaskRealInterval left )
	{
		final double[] min = new double[ left.numDimensions() ];
		final double[] max = new double[ left.numDimensions() ];
		left.realMin( min );
		left.realMax( max );
		return new FinalRealInterval( min, max );
	}

	/**
	 * Returns an interval which encompasses the transformed mask. The returned
	 * interval may not be the minimal interval.
	 *
	 * @param m
	 *            {@link MaskRealInterval} being transformed
	 * @param a
	 *            {@link AffineGet} being applied to the mask
	 * @return transformed interval
	 */
	private static RealInterval createTransformMinMax( final MaskRealInterval m, final AffineGet a )
	{
		// create corners
		final double[][] corners = new double[ ( int ) Math.pow( 2, m.numDimensions() ) ][ m.numDimensions() ];
		int s = corners.length / 2;
		boolean min = false;
		for ( int d = 0; d < m.numDimensions(); d++ )
		{
			for ( int i = 0; i < corners.length; i++ )
			{
				if ( i % s == 0 )
				{
					min = !min;
				}
				if ( min )
					corners[ i ][ d ] = m.realMin( d );
				else
					corners[ i ][ d ] = m.realMax( d );
			}
			s = s / 2;
		}

		for ( int i = 0; i < corners.length; i++ )
			a.inverse().apply( corners[ i ], corners[ i ] );

		return Regions.getBoundsReal( corners );
	}
}
