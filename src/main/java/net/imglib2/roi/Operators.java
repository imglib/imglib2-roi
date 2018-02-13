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

import static net.imglib2.roi.BoundaryType.UNSPECIFIED;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.InvertibleRealTransform;
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
import net.imglib2.roi.composite.RealTransformUnaryCompositeRealMask;
import net.imglib2.roi.composite.RealTransformUnaryCompositeRealMaskRealInterval;

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

	public static final Operators.BinaryMaskOperator AND = new Operators.BinaryMaskOperator( BoundaryType::and, Bounds.AND, KnownConstant::and )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			return t -> left.test( t ) && right.test( t );
		}
	};

	public static final Operators.BinaryMaskOperator OR = new Operators.BinaryMaskOperator( BoundaryType::or, Bounds.OR, KnownConstant::or )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			return t -> left.test( t ) || right.test( t );
		}
	};

	public static final Operators.BinaryMaskOperator XOR = new Operators.BinaryMaskOperator( BoundaryType::xor, Bounds.XOR, KnownConstant::xor )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			if ( left.equals( right ) )
				return t -> false;
			return t -> left.test( t ) ^ right.test( t );
		}
	};

	public static final Operators.BinaryMaskOperator MINUS = new Operators.BinaryMaskOperator( BoundaryType::minus, Bounds.MINUS, KnownConstant::minus )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			if ( left.equals( right ) )
				return t -> false;
			return t -> left.test( t ) && !right.test( t );
		}
	};

	public static final Operators.UnaryMaskOperator NEGATE = new Operators.UnaryMaskOperator( BoundaryType::negate, Bounds.NEGATE, KnownConstant::negate )
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
	 * Transforms
	 * ===========
	 */

	/**
	 * {@link MaskOperator} for transforming {@link RealMask}s.
	 *
	 * @author Alison Walter
	 */
	public static class RealTransformMaskOperator implements MaskOperator
	{
		private final RealTransform transformToSource;

		/**
		 * Number of dimensions of the target mask (that this operator creates).
		 */
		private final int n;

		/**
		 * Number of dimensions of the source mask (to which this operator is applied).
		 */
		private final int m;

		private final ThreadLocal< RealPoint > pt;

		private final UnaryOperator< BoundaryType > boundaryTypeOp;

		private final UnaryOperator< KnownConstant > knownConstantOp;

		public RealTransformMaskOperator( final RealTransform transformToSource )
		{
			this.transformToSource = transformToSource;
			n = transformToSource.numSourceDimensions();
			m = transformToSource.numTargetDimensions();
			pt = ThreadLocal.withInitial( () -> new RealPoint( m ) );
			boundaryTypeOp = ( willPreserveBounds( transformToSource ) && isContinuous( transformToSource ) )
					? UnaryOperator.identity()
					: t -> UNSPECIFIED;
			knownConstantOp = UnaryOperator.identity();
		}

		/**
		 * Checks if the given transform is a continuous transform. In other words,
		 * will it preserve the boundary behavior.
		 */
		public static final boolean isContinuous( RealTransform transform )
		{
			return transform instanceof AffineGet; // TODO
		}

		/**
		 * Checks if the given transform will result in a bounded composite.
		 */
		public static final boolean willPreserveBounds( RealTransform transform )
		{
			return transform instanceof AffineGet; // TODO
		}

		public RealTransform getTransformToSource()
		{
			return transformToSource;
		}

		public Predicate< RealLocalizable > predicate( final Predicate< ? super RealLocalizable > arg )
		{
			return pos -> {
				final RealPoint sourcePos = pt.get();
				transformToSource.apply( pos, sourcePos );
				return arg.test( sourcePos );
			};
		}

		public RealMask applyReal( final Predicate< ? super RealLocalizable > arg )
		{
			checkDimensions( arg );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( arg ) );
			if( arg instanceof RealInterval && willPreserveBounds( transformToSource ) )
				return new RealTransformUnaryCompositeRealMaskRealInterval( this, arg,
						new Bounds.RealTransformRealInterval( ( RealInterval ) arg, ( InvertibleRealTransform ) transformToSource ),
						boundaryType, knownConstantOp );
			return new RealTransformUnaryCompositeRealMask( this, arg, n, boundaryType, knownConstantOp );
		}

		public RealMaskRealInterval applyRealInterval( final Predicate< ? super RealLocalizable > arg )
		{
			final RealMask mask = applyReal( arg );
			if ( mask instanceof RealMaskRealInterval )
				return ( RealMaskRealInterval ) mask;
			throw new IllegalArgumentException( "result is not an interval" );
		}

		@Override
		public boolean equals( final Object obj )
		{
			if( obj instanceof RealTransformMaskOperator )
			{
				return transformToSource.equals( ( ( RealTransformMaskOperator ) obj ).getTransformToSource() );
			}
			return false;
		}

		@Override
		public int hashCode()
		{
			return transformToSource.hashCode() * 23;
		}

		private void checkDimensions( Object source )
		{
			if ( source instanceof EuclideanSpace )
			{
				if ( ( ( EuclideanSpace ) source ).numDimensions() != m )
					throw new IllegalArgumentException( "incompatible dimensionalities" );
			}
			else
				throw new IllegalArgumentException( "couldn't find dimensionality" );
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
