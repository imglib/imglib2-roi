package net.imglib2.troi;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.troi.Bounds.BinaryBoundsOperator;
import net.imglib2.troi.Bounds.IntBounds;
import net.imglib2.troi.Bounds.RealBounds;
import net.imglib2.troi.Bounds.UnaryBoundsOperator;
import net.imglib2.troi.util.DefaultMask;
import net.imglib2.troi.util.DefaultMaskInterval;
import net.imglib2.troi.util.DefaultRealMask;
import net.imglib2.troi.util.DefaultRealMaskRealInterval;
import net.imglib2.troi.util.RealMaskRealIntervalAsRRARI;
import net.imglib2.type.logic.BoolType;

/**
 * Utility class for working with {@link Mask}s.
 *
 * @author Curtis Rueden
 * @author Alison Walter
 */
public class Masks
{
	/*
	 * MaskOperator definitions (for both integral and real masks)
	 * ===============================================================
	 * TODO: should these be public?
	 */

	static final BinaryMaskOperator AND = new BinaryMaskOperator( BoundaryType::and, Bounds.and )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			return t -> left.test( t ) && right.test( t );
		}
	};

	static final BinaryMaskOperator OR = new BinaryMaskOperator( BoundaryType::or, Bounds.or )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			return t -> left.test( t ) || right.test( t );
		}
	};

	static final BinaryMaskOperator XOR = new BinaryMaskOperator( BoundaryType::xor, Bounds.xor )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			return t -> left.test( t ) ^ right.test( t );
		}
	};

	static final BinaryMaskOperator MINUS = new BinaryMaskOperator( BoundaryType::minus, Bounds.minus )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > left, final Predicate< ? super T > right )
		{
			return t -> left.test( t ) && !right.test( t );
		}
	};

	static final UnaryMaskOperator NEGATE = new UnaryMaskOperator( BoundaryType::negate, Bounds.negate )
	{
		@Override
		public < T > Predicate< T > predicate( final Predicate< ? super T > arg )
		{
			return t -> ! arg.test( t );
		}
	};

	/*
	 * Methods for integer masks
	 * ===============================================================
	 */

	public static Mask and( final Mask left, final Predicate< ? super Localizable > right )
	{
		return AND.apply( left, right );
	}

	public static MaskInterval and( final MaskInterval left, final Predicate< ? super Localizable > right )
	{
		return AND.applyInterval( left, right );
	}

	// TODO: do we need/want this:
	public static MaskInterval andMaskInterval( final Predicate< ? super Localizable > left, final MaskInterval right )
	{
		return AND.applyInterval( left, right );
	}

	public static Mask or( final Mask left, final Predicate< ? super Localizable > right )
	{
		return OR.apply( left, right );
	}

	public static MaskInterval or( final MaskInterval left, final MaskInterval right )
	{
		return OR.applyInterval( left, right );
	}

	public static Mask xor( final Mask left, final Predicate< ? super Localizable > right )
	{
		return XOR.apply( left, right );
	}

	public static MaskInterval xor( final MaskInterval left, final MaskInterval right )
	{
		return XOR.applyInterval( left, right );
	}

	public static Mask minus( final Mask left, final Predicate< ? super Localizable > right )
	{
		return MINUS.apply( left, right );
	}

	public static MaskInterval minus( final MaskInterval left, final Predicate< ? super Localizable > right )
	{
		return MINUS.applyInterval( left, right );
	}

	public static Mask negate( final Mask arg )
	{
		return NEGATE.apply( arg );
	}


	/*
	 * Methods for real masks
	 * ===============================================================
	 */

	static RealMask and( final RealMask left, final Predicate< ? super RealLocalizable > right )
	{
		return AND.applyReal( left, right );
	}

	static RealMaskRealInterval and( final RealMaskRealInterval left, final Predicate< ? super RealLocalizable > right )
	{
		return AND.applyRealInterval( left, right );
	}

	// TODO: do we need/want this:
	static RealMaskRealInterval andMaskInterval( final Predicate< ? super RealLocalizable > left, final RealMaskRealInterval right )
	{
		return AND.applyRealInterval( left, right );
	}

	public static RealMask or( final RealMask left, final Predicate< ? super RealLocalizable > right )
	{
		return OR.applyReal( left, right );
	}

	public static RealMaskRealInterval or( final RealMaskRealInterval left, final RealMaskRealInterval right )
	{
		return OR.applyRealInterval( left, right );
	}

	public static RealMask xor( final RealMask left, final Predicate< ? super RealLocalizable > right )
	{
		return XOR.applyReal( left, right );
	}

	public static RealMaskRealInterval xor( final RealMaskRealInterval left, final RealMaskRealInterval right )
	{
		return XOR.applyRealInterval( left, right );
	}

	public static RealMask minus( final RealMask left, final Predicate< ? super RealLocalizable > right )
	{
		return MINUS.applyReal( left, right );
	}

	public static RealMaskRealInterval minus( final RealMaskRealInterval left, final Predicate< ? super RealLocalizable > right )
	{
		return MINUS.applyRealInterval( left, right );
	}

	public static RealMask negate( final RealMask arg )
	{
		return NEGATE.applyReal( arg );
	}

	/*
	 * RandomAccessible Wrappers
	 */
	public static RealRandomAccessibleRealInterval< BoolType > toRRARI( final RealMaskRealInterval mask )
	{
		return new RealMaskRealIntervalAsRRARI<>( mask, new BoolType() );
	}


	/*
	 * private utilities
	 * ===============================================================
	 */

	/**
	 * Checks that all {@code args} have same dimensionality.
	 * Those {@code args} that do not implement {@link EuclideanSpace} are ignored (unless none of them does).
	 *
	 * @param args
	 * @return number of dimensions
	 * @throws IllegalArgumentException if no arg has dimensions or two args have incompatible number of dimensions
	 */
	static int checkDimensions( final Object... args )
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

	static abstract class BinaryMaskOperator
	{
		BinaryOperator< BoundaryType> boundaryTypeOp;

		BinaryBoundsOperator boundsOp;

		public BinaryMaskOperator(
				final BinaryOperator< BoundaryType> boundaryTypeOp,
				final BinaryBoundsOperator boundsOp )
		{
			this.boundaryTypeOp = boundaryTypeOp;
			this.boundsOp = boundsOp;
		}

		public Mask apply( final Predicate< ? super Localizable > left, final Predicate< ? super Localizable > right )
		{
			final int n = checkDimensions( left, right );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( left ), BoundaryType.of( right ) );
			final IntBounds bounds = boundsOp.apply( IntBounds.of( left ), IntBounds.of( right ) );
			if ( bounds == IntBounds.EMPTY )
				// TODO
				// EmptyMaskInterval...
				throw new UnsupportedOperationException( "TODO, not yet implemented" );
			else if ( bounds == IntBounds.UNBOUNDED )
				return new DefaultMask( n, boundaryType, predicate( left, right ) );
			else
				return new DefaultMaskInterval( bounds.interval(), boundaryType, predicate( left, right ) );
		}

		public RealMask applyReal( final Predicate< ? super RealLocalizable > left, final Predicate< ? super RealLocalizable > right )
		{
			final int n = checkDimensions( left, right );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( left ), BoundaryType.of( right ) );
			final RealBounds bounds = boundsOp.apply( RealBounds.of( left ), RealBounds.of( right ) );
			if ( bounds == RealBounds.EMPTY )
				// TODO
				// EmptyMaskInterval...
				throw new UnsupportedOperationException( "TODO, not yet implemented" );
			else if ( bounds == RealBounds.UNBOUNDED )
				return new DefaultRealMask( n, boundaryType, predicate( left, right ) );
			else
				return new DefaultRealMaskRealInterval( bounds.interval(), boundaryType, predicate( left, right ) );
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

	static abstract class UnaryMaskOperator
	{
		UnaryOperator< BoundaryType> boundaryTypeOp;

		UnaryBoundsOperator boundsOp;

		public UnaryMaskOperator(
				final UnaryOperator< BoundaryType> boundaryTypeOp,
				final UnaryBoundsOperator boundsOp )
		{
			this.boundaryTypeOp = boundaryTypeOp;
			this.boundsOp = boundsOp;
		}

		public Mask apply( final Predicate< ? super Localizable > arg )
		{
			final int n = checkDimensions( arg );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( arg ) );
			final IntBounds bounds = boundsOp.apply( IntBounds.of( arg ) );
			if ( bounds == IntBounds.EMPTY )
				// TODO
				// EmptyMaskInterval...
				throw new UnsupportedOperationException( "TODO, not yet implemented" );
			else if ( bounds == IntBounds.UNBOUNDED )
				return new DefaultMask( n, boundaryType, predicate( arg ) );
			else
				return new DefaultMaskInterval( bounds.interval(), boundaryType, predicate( arg ) );
		}

		public RealMask applyReal( final Predicate< ? super RealLocalizable > arg )
		{
			final int n = checkDimensions( arg );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( arg ) );
			final RealBounds bounds = boundsOp.apply( RealBounds.of( arg ) );
			if ( bounds == RealBounds.EMPTY )
				// TODO
				// EmptyMaskInterval...
				throw new UnsupportedOperationException( "TODO, not yet implemented" );
			else if ( bounds == RealBounds.UNBOUNDED )
				return new DefaultRealMask( n, boundaryType, predicate( arg ) );
			else
				return new DefaultRealMaskRealInterval( bounds.interval(), boundaryType, predicate( arg ) );
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
}
