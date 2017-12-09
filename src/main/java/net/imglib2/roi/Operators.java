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

import static net.imglib2.roi.KnownConstant.UNKNOWN;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.roi.composite.CompositeMaskPredicate;
import net.imglib2.roi.composite.DefaultBinaryCompositeMask;
import net.imglib2.roi.composite.DefaultBinaryCompositeMaskInterval;
import net.imglib2.roi.composite.DefaultBinaryCompositeRealMask;
import net.imglib2.roi.composite.DefaultBinaryCompositeRealMaskRealInterval;
import net.imglib2.roi.composite.DefaultUnaryCompositeMask;
import net.imglib2.roi.composite.DefaultUnaryCompositeMaskInterval;
import net.imglib2.roi.composite.DefaultUnaryCompositeRealMask;
import net.imglib2.roi.composite.DefaultUnaryCompositeRealMaskRealInterval;

/**
 * MaskOperator interfaces and instances. The concrete operator instances (e.g.,
 * {@link #AND}) combine the appropriate operations for the
 * {@link BoundaryType}, the {@link Bounds} interval, and the mask predicate.
 * <p>
 * The operator instances are used both, as constants referring to a specific
 * operator (see {@link CompositeMaskPredicate#operator()}), and to combine
 * source masks with the respective operation (e.g., see
 * {@link BinaryMaskOperator#apply(Predicate, Predicate)}).
 * </p>
 *
 * @author Tobias Pietzsch
 */
public class Operators
{
	/*
	 * MaskOperator definitions (for both integer and real masks)
	 * ===========================================================
	 */

	public static final Operators.BinaryMaskOperator AND = new Operators.BinaryMaskOperator( BoundaryType::and, Bounds.and, KnownConstant::and )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			return t -> left.test( t ) && right.test( t );
		}
	};

	public static final Operators.BinaryMaskOperator OR = new Operators.BinaryMaskOperator( BoundaryType::or, Bounds.or, KnownConstant::or )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			return t -> left.test( t ) || right.test( t );
		}
	};

	public static final Operators.BinaryMaskOperator XOR = new Operators.BinaryMaskOperator( BoundaryType::xor, Bounds.xor, KnownConstant::xor )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			if ( left.equals( right ) )
				return t -> false;
			return t -> left.test( t ) ^ right.test( t );
		}
	};

	public static final Operators.BinaryMaskOperator MINUS = new Operators.BinaryMaskOperator( BoundaryType::minus, Bounds.minus, KnownConstant::minus )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			if ( left.equals( right ) )
				return t -> false;
			return t -> left.test( t ) && !right.test( t );
		}
	};

	public static final Operators.UnaryMaskOperator NEGATE = new Operators.UnaryMaskOperator( BoundaryType::negate, Bounds.negate, KnownConstant::negate )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > arg )
		{
			return t -> !arg.test( t );
		}
	};

	/*
	 * MaskOperator interfaces and abstract base classes
	 * =================================================
	 */

	public interface MaskOperator
	{}

	public static abstract class BinaryMaskOperator implements MaskOperator
	{
		BinaryOperator< BoundaryType > boundaryTypeOp;

		Bounds.BinaryBoundsOperator boundsOp;

		BinaryOperator< KnownConstant > knownConstantOp;

		public BinaryMaskOperator(
				final BinaryOperator< BoundaryType > boundaryTypeOp,
				final Bounds.BinaryBoundsOperator boundsOp,
				final BinaryOperator< KnownConstant > knownConstantOp )
		{
			this.boundaryTypeOp = boundaryTypeOp;
			this.boundsOp = boundsOp;
			this.knownConstantOp = knownConstantOp;
		}

		public Mask apply( final Predicate< ? super Localizable > left, final Predicate< ? super Localizable > right )
		{
			final int n = checkDimensions( left, right );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( left ), BoundaryType.of( right ) );
			final Bounds.IntBounds bounds = boundsOp.apply( Bounds.IntBounds.of( left ), Bounds.IntBounds.of( right ) );
			if ( bounds.isUnbounded() )
				return new DefaultBinaryCompositeMask( this, left, right, n, boundaryType, knownConstantOp );
			return new DefaultBinaryCompositeMaskInterval( this, left, right, bounds.interval(), boundaryType, knownConstantOp );
		}

		public RealMask applyReal( final Predicate< ? super RealLocalizable > left, final Predicate< ? super RealLocalizable > right )
		{
			final int n = checkDimensions( left, right );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( left ), BoundaryType.of( right ) );
			final Bounds.RealBounds bounds = boundsOp.apply( Bounds.RealBounds.of( left ), Bounds.RealBounds.of( right ) );
			if ( bounds.isUnbounded() )
				return new DefaultBinaryCompositeRealMask( this, left, right, n, boundaryType, knownConstantOp );
			return new DefaultBinaryCompositeRealMaskRealInterval( this, left, right, bounds.interval(), boundaryType, knownConstantOp );
		}

		public MaskInterval applyInterval( final Predicate< ? super Localizable > left, final Predicate< ? super Localizable > right )
		{
			final Mask mask = apply( left, right );
			if ( mask instanceof MaskInterval )
				return ( MaskInterval ) mask;
			throw new IllegalArgumentException( "result is not an interval" );
		}

		public RealMaskRealInterval applyRealInterval( final Predicate< ? super RealLocalizable > left, final Predicate< ? super RealLocalizable > right )
		{
			final RealMask mask = applyReal( left, right );
			if ( mask instanceof RealMaskRealInterval )
				return ( RealMaskRealInterval ) mask;
			throw new IllegalArgumentException( "result is not an interval" );
		}

		public abstract < T > Predicate< T > predicate( Predicate< ? super T > left, Predicate< ? super T > right );
	}

	public static abstract class UnaryMaskOperator implements MaskOperator
	{
		UnaryOperator< BoundaryType > boundaryTypeOp;

		Bounds.UnaryBoundsOperator boundsOp;

		UnaryOperator< KnownConstant > knownConstantOp;

		public UnaryMaskOperator(
				final UnaryOperator< BoundaryType > boundaryTypeOp,
				final Bounds.UnaryBoundsOperator boundsOp,
				final UnaryOperator< KnownConstant > knownConstantOp )
		{
			this.boundaryTypeOp = boundaryTypeOp;
			this.boundsOp = boundsOp;
			this.knownConstantOp = knownConstantOp;
		}

		public Mask apply( final Predicate< ? super Localizable > arg )
		{
			final int n = checkDimensions( arg );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( arg ) );
			final Bounds.IntBounds bounds = boundsOp.apply( Bounds.IntBounds.of( arg ) );
			if ( bounds.isUnbounded() )
				return new DefaultUnaryCompositeMask( this, arg, n, boundaryType, knownConstantOp );
			return new DefaultUnaryCompositeMaskInterval( this, arg, bounds.interval(), boundaryType, knownConstantOp );
		}

		public RealMask applyReal( final Predicate< ? super RealLocalizable > arg )
		{
			final int n = checkDimensions( arg );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( arg ) );
			final Bounds.RealBounds bounds = boundsOp.apply( Bounds.RealBounds.of( arg ) );
			if ( bounds.isUnbounded() )
				return new DefaultUnaryCompositeRealMask( this, arg, n, boundaryType, knownConstantOp );
			return new DefaultUnaryCompositeRealMaskRealInterval( this, arg, bounds.interval(), boundaryType, knownConstantOp );
		}

		public MaskInterval applyInterval( final Predicate< ? super Localizable > arg )
		{
			final Mask mask = apply( arg );
			if ( mask instanceof MaskInterval )
				return ( MaskInterval ) mask;
			throw new IllegalArgumentException( "result is not an interval" );
		}

		public RealMaskRealInterval applyRealInterval( final Predicate< ? super RealLocalizable > arg )
		{
			final RealMask mask = applyReal( arg );
			if ( mask instanceof RealMaskRealInterval )
				return ( RealMaskRealInterval ) mask;
			throw new IllegalArgumentException( "result is not an interval" );
		}

		public abstract < T > Predicate< T > predicate( Predicate< ? super T > arg );
	}

	/*
	 * Transform
	 * =========
	 */
	/**
	 * Applies transformation to a given real-space {@link Predicate}.
	 */
	public static class RealMaskRealTransformOperator extends UnaryMaskOperator
	{
		private final RealTransform transformToSource;

		private final ThreadLocal< RealPoint > pt;

		/**
		 * Creates a {@link UnaryMaskOperator} which applies
		 * {@code transformToSource} to a given real-space {@link Predicate}.
		 *
		 * @param transformToSource
		 *            {@link RealTransform} for transforming back to source
		 */
		public RealMaskRealTransformOperator( final RealTransform transformToSource )
		{
			super( BoundaryType::transform, new Bounds.TransformBoundsOperator( transformToSource ), t -> UNKNOWN );
			this.transformToSource = transformToSource;
			pt = new ThreadLocal< RealPoint >()
			{
				@Override
				protected RealPoint initialValue()
				{
					return new RealPoint( transformToSource.numTargetDimensions() );
				}
			};
		}

		/**
		 * Returns the {@link RealTransform} associated with this operator.
		 *
		 * @return the RealTransform applied by this operator
		 */
		public RealTransform transformToSource()
		{
			return transformToSource;
		}

		/**
		 * @throws UnsupportedOperationException
		 *             cannot apply {@link RealTransform} to integer predicates
		 */
		@Override
		public Mask apply( final Predicate< ? super Localizable > arg )
		{
			throw new UnsupportedOperationException( "apply" );
		}

		/**
		 * @throws UnsupportedOperationException
		 *             cannot apply {@link RealTransform} to integer predicates
		 */
		@Override
		public MaskInterval applyInterval( final Predicate< ? super Localizable > arg )
		{
			throw new UnsupportedOperationException( "applyInterval" );
		}

		@Override
		@SuppressWarnings( "unchecked" )
		public < T > Predicate< T > predicate( final Predicate< ? super T > arg )
		{
			return t -> {
				final RealPoint rp = pt.get();
				transformToSource.apply( ( RealLocalizable ) t, rp );
				return arg.test( ( T ) rp );
			};
		}
	}

	/*
	 * Utilities
	 * =========
	 */

	/**
	 * Checks that all {@code args} have same dimensionality. Those {@code args}
	 * that do not implement {@link EuclideanSpace} are ignored (unless none of
	 * them does).
	 *
	 * @param args
	 * @return number of dimensions
	 * @throws IllegalArgumentException
	 *             if no arg has dimensions or two args have incompatible number
	 *             of dimensions
	 */
	public static int checkDimensions( final Object... args )
	{
		final int[] dimensionalities = Arrays.stream( args )
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
