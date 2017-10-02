package net.imglib2.troi;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.troi.composite.CompositeMaskPredicate;
import net.imglib2.troi.composite.DefaultBinaryCompositeMask;
import net.imglib2.troi.composite.DefaultBinaryCompositeMaskInterval;
import net.imglib2.troi.composite.DefaultBinaryCompositeRealMask;
import net.imglib2.troi.composite.DefaultBinaryCompositeRealMaskRealInterval;
import net.imglib2.troi.composite.DefaultUnaryCompositeMask;
import net.imglib2.troi.composite.DefaultUnaryCompositeMaskInterval;
import net.imglib2.troi.composite.DefaultUnaryCompositeRealMask;
import net.imglib2.troi.composite.DefaultUnaryCompositeRealMaskRealInterval;

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
	 * MaskOperator definitions (for both integral and real masks)
	 * ===========================================================
	 */

	public static final Operators.BinaryMaskOperator AND = new Operators.BinaryMaskOperator( BoundaryType::and, Bounds.and )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			return t -> left.test( t ) && right.test( t );
		}
	};

	public static final Operators.BinaryMaskOperator OR = new Operators.BinaryMaskOperator( BoundaryType::or, Bounds.or )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			return t -> left.test( t ) || right.test( t );
		}
	};

	public static final Operators.BinaryMaskOperator XOR = new Operators.BinaryMaskOperator( BoundaryType::xor, Bounds.xor )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			return t -> left.test( t ) ^ right.test( t );
		}
	};

	public static final Operators.BinaryMaskOperator MINUS = new Operators.BinaryMaskOperator( BoundaryType::minus, Bounds.minus )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			return t -> left.test( t ) && !right.test( t );
		}
	};

	public static final Operators.UnaryMaskOperator NEGATE = new Operators.UnaryMaskOperator( BoundaryType::negate, Bounds.negate )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > arg )
		{
			return t -> ! arg.test( t );
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

		public BinaryMaskOperator(
				final BinaryOperator< BoundaryType> boundaryTypeOp,
				final Bounds.BinaryBoundsOperator boundsOp )
		{
			this.boundaryTypeOp = boundaryTypeOp;
			this.boundsOp = boundsOp;
		}

		public Mask apply( final Predicate< ? super Localizable > left, final Predicate< ? super Localizable > right )
		{
			final int n = checkDimensions( left, right );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( left ), BoundaryType.of( right ) );
			final Bounds.IntBounds bounds = boundsOp.apply( Bounds.IntBounds.of( left ), Bounds.IntBounds.of( right ) );
			if ( bounds.isUnbounded() )
				return new DefaultBinaryCompositeMask( this, left, right, n, boundaryType );
			else
				return new DefaultBinaryCompositeMaskInterval( this, left, right, bounds.interval(), boundaryType );
		}

		public RealMask applyReal( final Predicate< ? super RealLocalizable > left, final Predicate< ? super RealLocalizable > right )
		{
			final int n = checkDimensions( left, right );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( left ), BoundaryType.of( right ) );
			final Bounds.RealBounds bounds = boundsOp.apply( Bounds.RealBounds.of( left ), Bounds.RealBounds.of( right ) );
			if ( bounds.isUnbounded() )
				return new DefaultBinaryCompositeRealMask( this, left, right, n, boundaryType );
			else
				return new DefaultBinaryCompositeRealMaskRealInterval( this, left, right, bounds.interval(), boundaryType );
		}

		public MaskInterval applyInterval( final Predicate< ? super Localizable > left, final Predicate< ? super Localizable > right )
		{
			final Mask mask = apply( left, right );
			if ( mask instanceof MaskInterval )
				return ( MaskInterval ) mask;
			else
				throw new IllegalArgumentException( "result is not an interval" );
		}

		public RealMaskRealInterval applyRealInterval( final Predicate< ? super RealLocalizable > left, final Predicate< ? super RealLocalizable > right )
		{
			final RealMask mask = applyReal( left, right );
			if ( mask instanceof RealMaskRealInterval )
				return ( RealMaskRealInterval ) mask;
			else
				throw new IllegalArgumentException( "result is not an interval" );
		}

		public abstract < T > Predicate< T > predicate( Predicate< ? super T > left, Predicate< ? super T > right );
	}

	public static abstract class UnaryMaskOperator implements MaskOperator
	{
		UnaryOperator< BoundaryType > boundaryTypeOp;

		Bounds.UnaryBoundsOperator boundsOp;

		public UnaryMaskOperator(
				final UnaryOperator< BoundaryType> boundaryTypeOp,
				final Bounds.UnaryBoundsOperator boundsOp )
		{
			this.boundaryTypeOp = boundaryTypeOp;
			this.boundsOp = boundsOp;
		}

		public Mask apply( final Predicate< ? super Localizable > arg )
		{
			final int n = checkDimensions( arg );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( arg ) );
			final Bounds.IntBounds bounds = boundsOp.apply( Bounds.IntBounds.of( arg ) );
			if ( bounds.isUnbounded() )
				return new DefaultUnaryCompositeMask( this, arg, n, boundaryType );
			else
				return new DefaultUnaryCompositeMaskInterval( this, arg, bounds.interval(), boundaryType );
		}

		public RealMask applyReal( final Predicate< ? super RealLocalizable > arg )
		{
			final int n = checkDimensions( arg );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( arg ) );
			final Bounds.RealBounds bounds = boundsOp.apply( Bounds.RealBounds.of( arg ) );
			if ( bounds.isUnbounded() )
				return new DefaultUnaryCompositeRealMask( this, arg, n, boundaryType );
			else
				return new DefaultUnaryCompositeRealMaskRealInterval( this, arg, bounds.interval(), boundaryType );
		}

		public MaskInterval applyInterval( final Predicate< ? super Localizable > arg )
		{
			final Mask mask = apply( arg );
			if ( mask instanceof MaskInterval )
				return ( MaskInterval ) mask;
			else
				throw new IllegalArgumentException( "result is not an interval" );
		}

		public RealMaskRealInterval applyRealInterval( final Predicate< ? super RealLocalizable > arg )
		{
			final RealMask mask = applyReal( arg );
			if ( mask instanceof RealMaskRealInterval )
				return ( RealMaskRealInterval ) mask;
			else
				throw new IllegalArgumentException( "result is not an interval" );
		}

		public abstract < T > Predicate< T > predicate( Predicate< ? super T > arg );
	}

	/*
	 * Utilities
	 * =========
	 */

	/**
	 * Checks that all {@code args} have same dimensionality.
	 * Those {@code args} that do not implement {@link EuclideanSpace} are ignored (unless none of them does).
	 *
	 * @param args
	 * @return number of dimensions
	 * @throws IllegalArgumentException if no arg has dimensions or two args have incompatible number of dimensions
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
