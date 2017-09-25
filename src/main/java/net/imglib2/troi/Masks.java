package net.imglib2.troi;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.troi.Bounds.BinaryBoundsOperator;
import net.imglib2.troi.Bounds.IntBounds;
import net.imglib2.troi.Bounds.RealBounds;
import net.imglib2.troi.util.AbstractMask;
import net.imglib2.troi.util.AbstractMaskInterval;
import net.imglib2.troi.util.AbstractRealMask;
import net.imglib2.troi.util.AbstractRealMaskRealInterval;

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
		return AND.apply( left, right );
	}

	static MaskInterval and( MaskInterval left, Predicate< ? super Localizable > right )
	{
		return AND.applyInterval( left, right );
	}

	static MaskInterval andMaskInterval( Predicate< ? super Localizable > left, MaskInterval right )
	{
		return AND.applyInterval( left, right );
	}

	static RealMask and( RealMask left, Predicate< ? super RealLocalizable > right )
	{
		return AND.applyReal( left, right );
	}

	static RealMaskRealInterval andMaskInterval( Predicate< ? super RealLocalizable > left, RealMaskRealInterval right )
	{
		return AND.applyRealInterval( left, right );
	}


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








	// ====== Utils ======

	/**
	 * Checks that all {@code args} have same dimensionality.
	 * Those {@code args} that do not implement {@link EuclideanSpace} are ignored (unless none of them does).
	 *
	 * @param args
	 * @return number of dimensions
	 * @throws IllegalArgumentException if no arg has dimensions or two args have incompatible number of dimensions
	 */
	static int checkDimensions( Object... args )
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

	static abstract class BinaryMaskOperator
	{
		BinaryOperator< BoundaryType> boundaryTypeOp;

		BinaryBoundsOperator boundsOp;

		public BinaryMaskOperator(
				BinaryOperator< BoundaryType> boundaryTypeOp,
				BinaryBoundsOperator boundsOp )
		{
			this.boundaryTypeOp = boundaryTypeOp;
			this.boundsOp = boundsOp;
		}

		public Mask apply( Predicate< ? super Localizable > left, Predicate< ? super Localizable > right )
		{
			int n = checkDimensions( left, right );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( left ), BoundaryType.of( right ) );
			final IntBounds bounds = boundsOp.apply( IntBounds.of( left ), IntBounds.of( right ) );
			if ( bounds == IntBounds.EMPTY )
				// TODO
				// EmptyMaskInterval...
				throw new UnsupportedOperationException( "TODO, not yet implemented" );
			else if ( bounds == IntBounds.UNBOUNDED )
				return new AbstractMask( n, boundaryType, predicate( left, right ) );
			else
				return new AbstractMaskInterval( bounds.interval(), boundaryType, predicate( left, right ) );
		}

		public RealMask applyReal( Predicate< ? super RealLocalizable > left, Predicate< ? super RealLocalizable > right )
		{
			int n = checkDimensions( left, right );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( left ), BoundaryType.of( right ) );
			final RealBounds bounds = boundsOp.apply( RealBounds.of( left ), RealBounds.of( right ) );
			if ( bounds == RealBounds.EMPTY )
				// TODO
				// EmptyMaskInterval...
				throw new UnsupportedOperationException( "TODO, not yet implemented" );
			else if ( bounds == RealBounds.UNBOUNDED )
				return new AbstractRealMask( n, boundaryType, predicate( left, right ) );
			else
				return new AbstractRealMaskRealInterval( bounds.interval(), boundaryType, predicate( left, right ) );
		}

		public MaskInterval applyInterval( Predicate< ? super Localizable > left, Predicate< ? super Localizable > right )
		{
			Mask mask = apply( left, right );
			if ( mask instanceof MaskInterval )
				return ( MaskInterval ) mask;
			else
				throw new IllegalArgumentException( "result is not an interval" );
		}

		public RealMaskRealInterval applyRealInterval( Predicate< ? super RealLocalizable > left, Predicate< ? super RealLocalizable > right )
		{
			RealMask mask = applyReal( left, right );
			if ( mask instanceof RealMaskRealInterval )
				return ( RealMaskRealInterval ) mask;
			else
				throw new IllegalArgumentException( "result is not an interval" );
		}

		public abstract < T > Predicate< T > predicate( Predicate< ? super T > left, Predicate< ? super T > right );
	}

	// Operator definitions

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
}
