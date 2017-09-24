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
import java.util.function.Predicate;

import net.imglib2.EuclideanSpace;
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
 */
public final class BinaryOperations
{
	private final static BiFunction< Mask< Localizable >, Mask< Localizable >, Predicate< Localizable > > AND = ( left, right ) -> right.and( left );

	private final static BiFunction< Mask< Localizable >, Mask< Localizable >, Mask< Localizable > > ANDX = binOp( ( left, right ) -> right.and( left ), Operation.AND );

	private final static BiFunction< Mask< RealLocalizable >, Mask< RealLocalizable >, Predicate< RealLocalizable > > REAL_AND = ( left, right ) -> right.and( left );

	private final static BiFunction< Mask< Localizable >, Mask< Localizable >, Predicate< Localizable > > OR = ( left, right ) -> right.or( left );

	private final static BiFunction< Mask< RealLocalizable >, Mask< RealLocalizable >, Predicate< RealLocalizable > > REAL_OR = ( left, right ) -> right.or( left );

	private final static BiFunction< Mask< Localizable >, Mask< Localizable >, Predicate< Localizable > > SUBTRACT = ( left, right ) -> ( l ) -> left.test( l ) && !right.test( l );

	private final static BiFunction< Mask< RealLocalizable >, Mask< RealLocalizable >, Predicate< RealLocalizable > > REAL_SUBTRACT = ( left, right ) -> ( l ) -> left.test( l ) && !right.test( l );

	private final static BiFunction< Mask< Localizable >, Mask< Localizable >, Predicate< Localizable > > XOR = ( left, right ) -> ( l ) -> left.test( l ) ^ right.test( l );

	private final static BiFunction< Mask< RealLocalizable >, Mask< RealLocalizable >, Predicate< RealLocalizable > > REAL_XOR = ( left, right ) -> ( l ) -> left.test( l ) ^ right.test( l );

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
		return binOp( createAffinePredicate( numDimensions ), Operation.TRANSFORM );
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
		return ( left, right ) -> new MaskRealIntervalOperationResult<>(
				createAffinePredicate( numDimensions ).apply( left, right ),
				left.boundaryType(),
				Arrays.asList( left, right ),
				Operation.TRANSFORM,
				realInterval( left, right ) );
	}

	// -- AND --

	/** The intersection of two discrete space Masks. */
	public static BiFunction< Mask< Localizable >, Mask< Localizable >, Mask< Localizable > > and()
	{
		return binOp( AND, Operation.AND );
	}

	/** The intersection of two discrete space Masks. */
	public static Mask< Localizable > andx( Mask< Localizable > left, Mask< Localizable > right )
	{
		return ANDX.apply( left, right );
	}

	/**
	 * The intersection of two discrete space {@link MaskInterval}s. The
	 * resulting mask is also a {@link MaskInterval}.
	 */
	public static BinaryOperator< MaskInterval > intervalAnd()
	{
		return intervalBinOp( AND, Operation.AND );
	}

	/** The intersection of two real space Masks. */
	public static BiFunction< Mask< RealLocalizable >, Mask< RealLocalizable >, Mask< RealLocalizable > > realAnd()
	{
		return binOp( REAL_AND, Operation.AND );
	}

	/**
	 * The intersection of two real space {@link MaskRealInterval}s. The
	 * resulting mask is also a {@link MaskRealInterval}.
	 */
	public static BinaryOperator< MaskRealInterval > realIntervalAnd()
	{
		return realIntervalBinOp( REAL_AND, Operation.AND );
	}

	// -- OR --

	/** The union of two discrete space Masks. */
	public static BiFunction< Mask< Localizable >, Mask< Localizable >, Mask< Localizable > > or()
	{
		return binOp( OR, Operation.OR );
	}

	/**
	 * The union of two discrete space {@link MaskInterval}s. The resulting mask
	 * is also a {@link MaskInterval}.
	 */
	public static BinaryOperator< MaskInterval > intervalOr()
	{
		return intervalBinOp( OR, Operation.OR );
	}

	/** The union of two real space Masks. */
	public static BiFunction< Mask< RealLocalizable >, Mask< RealLocalizable >, Mask< RealLocalizable > > realOr()
	{
		return binOp( REAL_OR, Operation.OR );
	}

	/**
	 * The union of two real space {@link MaskRealInterval}s. The resulting mask
	 * is also a {@link MaskRealInterval}.
	 */
	public static BinaryOperator< MaskRealInterval > realIntervalOr()
	{
		return realIntervalBinOp( REAL_OR, Operation.OR );
	}

	// -- SUBTRACT --

	/**
	 * The subtraction of two masks in discrete space. Subtraction is defined as
	 * inside the left operand AND NOT inside the right operand.
	 */
	public static BiFunction< Mask< Localizable >, Mask< Localizable >, Mask< Localizable > > subtract()
	{
		return binOp( SUBTRACT, Operation.SUBTRACT );
	}

	/**
	 * The subtraction of two {@link MaskInterval}s. Subtraction is defined as
	 * inside the left operand AND NOT inside the right operand. The result is
	 * also a {@link MaskInterval}. This method does not guarantee that the
	 * returned interval is the minimum bounding box.
	 */
	public static BinaryOperator< MaskInterval > intervalSubtract()
	{
		return intervalBinOp( SUBTRACT, Operation.SUBTRACT );
	}

	/**
	 * The subtraction of two masks in real space. Subtraction is defined as
	 * inside the left operand AND NOT inside the right operand.
	 */
	public static BiFunction< Mask< RealLocalizable >, Mask< RealLocalizable >, Mask< RealLocalizable > > realSubtract()
	{
		return binOp( REAL_SUBTRACT, Operation.SUBTRACT );
	}

	/**
	 * The subtraction of two {@link MaskRealInterval}s. Subtraction is defined
	 * as inside the left operand AND NOT inside the right operand. The result
	 * is also a {@link MaskRealInterval}. This method does not guarantee that
	 * the returned interval is the minimum bounding box.
	 */
	public static BinaryOperator< MaskRealInterval > realIntervalSubtract()
	{
		return realIntervalBinOp( REAL_SUBTRACT, Operation.SUBTRACT );
	}

	// -- XOR --

	/** An exclusive or between two discrete space masks. */
	public static BiFunction< Mask< Localizable >, Mask< Localizable >, Mask< Localizable > > xor()
	{
		return binOp( XOR, Operation.XOR );
	}

	/**
	 * An exclusive or between two discrete space {@link MaskInterval}s. A mask
	 * resulting from this function also knows its interval.
	 */
	public static BinaryOperator< MaskInterval > intervalXor()
	{
		return intervalBinOp( XOR, Operation.XOR );
	}

	/** An exclusive or between two real space masks. */
	public static BiFunction< Mask< RealLocalizable >, Mask< RealLocalizable >, Mask< RealLocalizable > > realXor()
	{
		return binOp( REAL_XOR, Operation.XOR );
	}

	/**
	 * An exclusive or between two real space {@link MaskRealInterval}s. A mask
	 * resulting from this function also knows its interval.
	 */
	public static BinaryOperator< MaskRealInterval > realIntervalXor()
	{
		return realIntervalBinOp( REAL_XOR, Operation.XOR );
	}

	// -- Helper methods --

	/**
	 * Create a {@link BiFunction} which performs the given operation {@code op}
	 * on two masks and results in a mask.
	 *
	 * @param func
	 *            a {@link BiFunction} which takes two Masks and produces a
	 *            predicate with the desired behavior (i.e. performs and, or,
	 *            etc.)
	 * @param op
	 *            the operation to be performed
	 * @return a function which performs the operation and produces a Mask
	 */
	@SuppressWarnings( "unchecked" )
	private static < L, T extends EuclideanSpace > BiFunction< Mask< L >, T, Mask< L > > binOp( final BiFunction< Mask< L >, T, Predicate< L > > func, final Operation op )
	{
		return ( left, right ) -> {
			BoundaryType bt;
			if ( op == Operation.TRANSFORM )
				bt = left.boundaryType();
			else
				bt = boundaryType( left, ( Mask< L > ) right, op );
			return new DefaultMaskOperationResult<>( func.apply( left, right ), bt, Arrays.asList( left, right ), op );
		};
	}

	/**
	 * Create a {@link BinaryOperator} which performs the given operation
	 * {@code op} on two {@link MaskInterval}s and results in a
	 * {@link MaskInterval}.
	 *
	 * @param func
	 *            a {@link BiFunction} which takes two Masks and produces a
	 *            predicate with the desired behavior (i.e. performs and, or,
	 *            etc.)
	 * @param op
	 *            the operation to be performed
	 * @return a function which performs the operation and produces a
	 *         MaskInterval
	 */
	private static BinaryOperator< MaskInterval > intervalBinOp( final BiFunction< Mask< Localizable >, Mask< Localizable >, Predicate< Localizable > > func, final Operation op )
	{
		if ( op == Operation.TRANSFORM )
			throw new IllegalArgumentException( "Cannot transform integer space Masks" );
		return ( left, right ) -> {
			return new MaskIntervalOperationResult<>( func.apply( left, right ), boundaryType( left, right, op ), Arrays.asList( left, right ), op, interval( left, right, op ) );
		};
	}

	/**
	 * Create a {@link BinaryOperator} which performs the given operation
	 * {@code op} on two {@link MaskRealInterval}s and results in a
	 * {@link MaskRealInterval}.
	 *
	 * @param func
	 *            a {@link BiFunction} which takes two Masks and produces a
	 *            predicate with the desired behavior (i.e. performs and, or,
	 *            etc.)
	 * @param op
	 *            the operation to be performed
	 * @return a function which performs the operation and produces a
	 *         MaskRealInterval
	 */
	private static BinaryOperator< MaskRealInterval > realIntervalBinOp( final BiFunction< Mask< RealLocalizable >, Mask< RealLocalizable >, Predicate< RealLocalizable > > func, final Operation op )
	{
		return ( left, right ) -> {
			return new MaskRealIntervalOperationResult<>( func.apply( left, right ), boundaryType( left, right, op ), Arrays.asList( left, right ), op, realInterval( left, right, op ) );
		};
	}

	/**
	 * Creates a {@link BiFunction} which returns the {@link Predicate} for a
	 * {@code Mask<RealLocalizable>} transformed by an {@link AffineGet}.
	 *
	 * @param numDimensions
	 *            number of dimensions of the Mask which this will operate on
	 * @return a BiFunction which produces the affine transform predicate
	 */
	private static BiFunction< Mask< RealLocalizable >, AffineGet, Predicate< RealLocalizable > > createAffinePredicate( final int numDimensions )
	{
		final ThreadLocal< RealPoint > pt = new ThreadLocal< RealPoint >()
		{
			@Override
			protected RealPoint initialValue()
			{
				return new RealPoint( numDimensions );
			}
		};
		return ( left, right ) -> ( l ) -> {
			final RealPoint rp = pt.get();
			right.apply( l, rp );
			return left.test( rp );
		};
	}

	/**
	 * Determines the boundary type of the Mask resulting from the operation.
	 *
	 * @param left
	 *            a Mask which is the left operand
	 * @param right
	 *            a Mask which is the right operand
	 * @param op
	 *            the operation being performed
	 * @return the boundary type of the resulting Mask
	 */
	private static < L > BoundaryType boundaryType( final Mask< L > left, final Mask< L > right, final Operation op )
	{
		if ( op == Operation.AND || op == Operation.OR )
			return right.boundaryType() == left.boundaryType() ? left.boundaryType() : BoundaryType.UNSPECIFIED;
		else if ( op == Operation.SUBTRACT )
		{
			BoundaryType b = BoundaryType.UNSPECIFIED;
			if ( left.boundaryType() != right.boundaryType() && left.boundaryType() != BoundaryType.UNSPECIFIED && right.boundaryType() != BoundaryType.UNSPECIFIED )
				b = left.boundaryType();
			return b;
		}
		else if ( op == Operation.XOR )
			return BoundaryType.UNSPECIFIED;
		else
			throw new IllegalArgumentException( "No such operation " + op );
	}

	/**
	 * Determines the interval of the {@link MaskInterval} which results from
	 * the operation {@code op}.
	 *
	 * @param left
	 *            MaskInterval left operand
	 * @param right
	 *            MaskInterval right operand
	 * @param op
	 *            operation being performed on {@code left} and {@code right}
	 * @return an {@link Interval} which covers the entire result, this is
	 *         <strong>not</strong> guaranteed to be the minimal interval
	 */
	private static Interval interval( final MaskInterval left, final MaskInterval right, final Operation op )
	{
		final long[] min = new long[ left.numDimensions() ];
		final long[] max = new long[ left.numDimensions() ];

		if ( op == Operation.OR || op == Operation.XOR )
		{
			for ( int d = 0; d < min.length; d++ )
			{
				min[ d ] = left.min( d ) < right.min( d ) ? left.min( d ) : right.min( d );
				max[ d ] = left.max( d ) > right.max( d ) ? left.max( d ) : right.max( d );
			}
		}
		else if ( op == Operation.AND )
		{
			for ( int d = 0; d < min.length; d++ )
			{
				min[ d ] = left.min( d ) > right.min( d ) ? left.min( d ) : right.min( d );
				max[ d ] = left.max( d ) < right.max( d ) ? left.max( d ) : right.max( d );
			}
		}
		else if ( op == Operation.SUBTRACT )
		{
			left.min( min );
			left.max( max );
		}
		else
			throw new IllegalArgumentException( "No such operation " + op );
		return new FinalInterval( min, max );
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
	private static RealInterval realInterval( final MaskRealInterval m, final AffineGet a )
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

	/**
	 * Determines the interval of the {@link MaskRealInterval} which results
	 * from the operation {@code op}.
	 *
	 * @param left
	 *            MaskRealInterval left operand
	 * @param right
	 *            MaskRealInterval right operand
	 * @param op
	 *            operation being performed on {@code left} and {@code right}
	 * @return a {@link RealInterval} which covers the entire result, this is
	 *         <strong>not</strong> guaranteed to be the minimal interval
	 */
	private static RealInterval realInterval( final MaskRealInterval left, final MaskRealInterval right, final Operation op )
	{
		final double[] min = new double[ left.numDimensions() ];
		final double[] max = new double[ left.numDimensions() ];
		if ( op == Operation.OR || op == Operation.XOR )
		{
			for ( int d = 0; d < min.length; d++ )
			{
				min[ d ] = left.realMin( d ) < right.realMin( d ) ? left.realMin( d ) : right.realMin( d );
				max[ d ] = left.realMax( d ) > right.realMax( d ) ? left.realMax( d ) : right.realMax( d );
			}
		}
		else if ( op == Operation.AND )
		{
			for ( int d = 0; d < min.length; d++ )
			{
				min[ d ] = left.realMin( d ) > right.realMin( d ) ? left.realMin( d ) : right.realMin( d );
				max[ d ] = left.realMax( d ) < right.realMax( d ) ? left.realMax( d ) : right.realMax( d );
			}
		}
		else if ( op == Operation.SUBTRACT )
		{
			left.realMin( min );
			left.realMax( max );
		}
		else
			throw new IllegalArgumentException( "No such operation " + op );
		return new FinalRealInterval( min, max );
	}
}
