package net.imglib2.troi;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.AbstractWrappedInterval;
import net.imglib2.AbstractWrappedRealInterval;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.troi.Bounds.IntervalOrEmpty;
import net.imglib2.troi.Bounds.RealIntervalOrEmpty;
import net.imglib2.troi.Masks.BinaryMaskOperator;
import net.imglib2.troi.Masks.MaskOperator;
import net.imglib2.troi.Masks.UnaryMaskOperator;

public class MaskTrees
{
	public interface CompositeMaskPredicate< T > extends MaskPredicate< T >
	{
		/** Returns the operation which lead to this mask. */
		MaskOperator operator();

		Predicate< ? super T > operand( int index );

		/**
		 * Returns the list of operands, which were used to compute this Mask.
		 */
		List< Predicate< ? > > operands();
	}

	public interface BinaryCompositeMaskPredicate< T > extends CompositeMaskPredicate< T >
	{
		@Override
		BinaryMaskOperator operator();

		Predicate< ? super T > arg0();

		Predicate< ? super T > arg1();

		@Override
		default Predicate< ? super T > operand( int index )
		{
			switch ( index )
			{
			case 0:
				return arg0();
			case 1:
				return arg1();
			default:
				throw new IllegalArgumentException();
			}
		}

		default List< Predicate< ? > > operands()
		{
			return Arrays.asList( arg0(), arg1() );
		}
	}

	public interface UnaryCompositeMaskPredicate< T > extends CompositeMaskPredicate< T >
	{
		@Override
		UnaryMaskOperator operator();

		Predicate< ? super T > arg0();

		@Override
		default Predicate< ? super T > operand( int index )
		{
			if ( index == 0 )
				return arg0();
			else
				throw new IllegalArgumentException();
		}

		default List< Predicate< ? > > operands()
		{
			return Arrays.asList( arg0() );
		}
	}

	public interface BinaryCompositeMask extends BinaryCompositeMaskPredicate< Localizable >, Mask
	{
	}

	public interface BinaryCompositeMaskInterval extends BinaryCompositeMask, MaskInterval
	{
	}

	public interface UnaryCompositeMask extends UnaryCompositeMaskPredicate< Localizable >, Mask
	{
	}

	public interface UnaryCompositeMaskInterval extends UnaryCompositeMask, MaskInterval
	{
	}

	public interface BinaryCompositeRealMask extends BinaryCompositeMaskPredicate< RealLocalizable >, RealMask
	{
	}

	public interface BinaryCompositeRealMaskRealInterval extends BinaryCompositeRealMask, RealMaskRealInterval
	{
	}

	public interface UnaryCompositeRealMask extends UnaryCompositeMaskPredicate< RealLocalizable >, RealMask
	{
	}

	public interface UnaryCompositeRealMaskRealInterval extends UnaryCompositeRealMask, RealMaskRealInterval
	{
	}

	public static class DefaultBinaryCompositeMask
			extends AbstractEuclideanSpace
			implements BinaryCompositeMask
	{
		private final BinaryMaskOperator operator;

		private final Predicate< ? super Localizable > arg0;

		private final Predicate< ? super Localizable > arg1;

		private final BoundaryType boundaryType;

		private final Predicate< ? super Localizable > predicate;

		public DefaultBinaryCompositeMask(
				BinaryMaskOperator operator,
				final Predicate< ? super Localizable > arg0,
				final Predicate< ? super Localizable > arg1,
				final int numDimensions,
				final BoundaryType boundaryType )
		{
			super( numDimensions );
			this.operator = operator;
			this.arg0 = arg0;
			this.arg1 = arg1;
			this.boundaryType = boundaryType;
			this.predicate = operator.predicate( arg0, arg1 );
		}

		@Override
		public BoundaryType boundaryType()
		{
			return boundaryType;
		}

		@Override
		public boolean test( final Localizable localizable )
		{
			return predicate.test( localizable );
		}

		@Override
		public BinaryMaskOperator operator()
		{
			return operator;
		}

		@Override
		public Predicate< ? super Localizable > arg0()
		{
			return arg0;
		}

		@Override
		public Predicate< ? super Localizable > arg1()
		{
			return arg1;
		}
	}

	public static class DefaultUnaryCompositeMask
			extends AbstractEuclideanSpace
			implements UnaryCompositeMask
	{
		private final UnaryMaskOperator operator;

		private final Predicate< ? super Localizable > arg0;

		private final BoundaryType boundaryType;

		private final Predicate< ? super Localizable > predicate;

		public DefaultUnaryCompositeMask(
				UnaryMaskOperator operator,
				final Predicate< ? super Localizable > arg0,
				final int numDimensions,
				final BoundaryType boundaryType )
		{
			super( numDimensions );
			this.operator = operator;
			this.arg0 = arg0;
			this.boundaryType = boundaryType;
			this.predicate = operator.predicate( arg0 );
		}

		@Override
		public BoundaryType boundaryType()
		{
			return boundaryType;
		}

		@Override
		public boolean test( final Localizable localizable )
		{
			return predicate.test( localizable );
		}

		@Override
		public UnaryMaskOperator operator()
		{
			return operator;
		}

		@Override
		public Predicate< ? super Localizable > arg0()
		{
			return arg0;
		}
	}

	public static class DefaultBinaryCompositeMaskInterval
			extends AbstractWrappedInterval< IntervalOrEmpty >
			implements BinaryCompositeMaskInterval, IntervalOrEmpty
	{
		private final BinaryMaskOperator operator;

		private final Predicate< ? super Localizable > arg0;

		private final Predicate< ? super Localizable > arg1;

		private final BoundaryType boundaryType;

		private final Predicate< ? super Localizable > predicate;

		public DefaultBinaryCompositeMaskInterval(
				BinaryMaskOperator operator,
				final Predicate< ? super Localizable > arg0,
				final Predicate< ? super Localizable > arg1,
				final IntervalOrEmpty interval,
				final BoundaryType boundaryType )
		{
			super( interval );
			this.operator = operator;
			this.arg0 = arg0;
			this.arg1 = arg1;
			this.boundaryType = boundaryType;
			this.predicate = operator.predicate( arg0, arg1 );
		}

		@Override
		public BoundaryType boundaryType()
		{
			return boundaryType;
		}

		@Override
		public boolean test( final Localizable localizable )
		{
			return predicate.test( localizable );
		}

		@Override
		public BinaryMaskOperator operator()
		{
			return operator;
		}

		@Override
		public Predicate< ? super Localizable > arg0()
		{
			return arg0;
		}

		@Override
		public Predicate< ? super Localizable > arg1()
		{
			return arg1;
		}

		@Override
		public boolean isEmpty()
		{
			return this.sourceInterval.isEmpty();
		}
	}

	public static class DefaultUnaryCompositeMaskInterval
			extends AbstractWrappedInterval< IntervalOrEmpty >
			implements UnaryCompositeMaskInterval, IntervalOrEmpty
	{
		private final UnaryMaskOperator operator;

		private final Predicate< ? super Localizable > arg0;

		private final BoundaryType boundaryType;

		private final Predicate< ? super Localizable > predicate;

		public DefaultUnaryCompositeMaskInterval(
				UnaryMaskOperator operator,
				final Predicate< ? super Localizable > arg0,
				final IntervalOrEmpty interval,
				final BoundaryType boundaryType )
		{
			super( interval );
			this.operator = operator;
			this.arg0 = arg0;
			this.boundaryType = boundaryType;
			this.predicate = operator.predicate( arg0 );
		}

		@Override
		public BoundaryType boundaryType()
		{
			return boundaryType;
		}

		@Override
		public boolean test( final Localizable localizable )
		{
			return predicate.test( localizable );
		}

		@Override
		public UnaryMaskOperator operator()
		{
			return operator;
		}

		@Override
		public Predicate< ? super Localizable > arg0()
		{
			return arg0;
		}

		@Override
		public boolean isEmpty()
		{
			return this.sourceInterval.isEmpty();
		}
	}

	public static class DefaultBinaryCompositeRealMask
			extends AbstractEuclideanSpace
			implements BinaryCompositeRealMask
	{
		private final BinaryMaskOperator operator;

		private final Predicate< ? super RealLocalizable > arg0;

		private final Predicate< ? super RealLocalizable > arg1;

		private final BoundaryType boundaryType;

		private final Predicate< ? super RealLocalizable > predicate;

		public DefaultBinaryCompositeRealMask(
				BinaryMaskOperator operator,
				final Predicate< ? super RealLocalizable > arg0,
				final Predicate< ? super RealLocalizable > arg1,
				final int numDimensions,
				final BoundaryType boundaryType )
		{
			super( numDimensions );
			this.operator = operator;
			this.arg0 = arg0;
			this.arg1 = arg1;
			this.boundaryType = boundaryType;
			this.predicate = operator.predicate( arg0, arg1 );
		}

		@Override
		public BoundaryType boundaryType()
		{
			return boundaryType;
		}

		@Override
		public boolean test( final RealLocalizable localizable )
		{
			return predicate.test( localizable );
		}

		@Override
		public BinaryMaskOperator operator()
		{
			return operator;
		}

		@Override
		public Predicate< ? super RealLocalizable > arg0()
		{
			return arg0;
		}

		@Override
		public Predicate< ? super RealLocalizable > arg1()
		{
			return arg1;
		}
	}

	public static class DefaultUnaryCompositeRealMask
			extends AbstractEuclideanSpace
			implements UnaryCompositeRealMask
	{
		private final UnaryMaskOperator operator;

		private final Predicate< ? super RealLocalizable > arg0;

		private final BoundaryType boundaryType;

		private final Predicate< ? super RealLocalizable > predicate;

		public DefaultUnaryCompositeRealMask(
				UnaryMaskOperator operator,
				final Predicate< ? super RealLocalizable > arg0,
				final int numDimensions,
				final BoundaryType boundaryType )
		{
			super( numDimensions );
			this.operator = operator;
			this.arg0 = arg0;
			this.boundaryType = boundaryType;
			this.predicate = operator.predicate( arg0 );
		}

		@Override
		public BoundaryType boundaryType()
		{
			return boundaryType;
		}

		@Override
		public boolean test( final RealLocalizable localizable )
		{
			return predicate.test( localizable );
		}

		@Override
		public UnaryMaskOperator operator()
		{
			return operator;
		}

		@Override
		public Predicate< ? super RealLocalizable > arg0()
		{
			return arg0;
		}
	}

	public static class DefaultBinaryCompositeRealMaskRealInterval
			extends AbstractWrappedRealInterval< RealIntervalOrEmpty >
			implements BinaryCompositeRealMaskRealInterval, RealIntervalOrEmpty
	{
		private final BinaryMaskOperator operator;

		private final Predicate< ? super RealLocalizable > arg0;

		private final Predicate< ? super RealLocalizable > arg1;

		private final BoundaryType boundaryType;

		private final Predicate< ? super RealLocalizable > predicate;

		public DefaultBinaryCompositeRealMaskRealInterval(
				BinaryMaskOperator operator,
				final Predicate< ? super RealLocalizable > arg0,
				final Predicate< ? super RealLocalizable > arg1,
				final RealIntervalOrEmpty interval,
				final BoundaryType boundaryType )
		{
			super( interval );
			this.operator = operator;
			this.arg0 = arg0;
			this.arg1 = arg1;
			this.boundaryType = boundaryType;
			this.predicate = operator.predicate( arg0, arg1 );
		}

		@Override
		public BoundaryType boundaryType()
		{
			return boundaryType;
		}

		@Override
		public boolean test( final RealLocalizable localizable )
		{
			return predicate.test( localizable );
		}

		@Override
		public BinaryMaskOperator operator()
		{
			return operator;
		}

		@Override
		public Predicate< ? super RealLocalizable > arg0()
		{
			return arg0;
		}

		@Override
		public Predicate< ? super RealLocalizable > arg1()
		{
			return arg1;
		}

		@Override
		public boolean isEmpty()
		{
			return this.sourceInterval.isEmpty();
		}
	}

	public static class DefaultUnaryCompositeRealMaskRealInterval
			extends AbstractWrappedRealInterval< RealIntervalOrEmpty >
			implements UnaryCompositeRealMaskRealInterval, RealIntervalOrEmpty
	{
		private final UnaryMaskOperator operator;

		private final Predicate< ? super RealLocalizable > arg0;

		private final BoundaryType boundaryType;

		private final Predicate< ? super RealLocalizable > predicate;

		public DefaultUnaryCompositeRealMaskRealInterval(
				UnaryMaskOperator operator,
				final Predicate< ? super RealLocalizable > arg0,
				final RealIntervalOrEmpty interval,
				final BoundaryType boundaryType )
		{
			super( interval );
			this.operator = operator;
			this.arg0 = arg0;
			this.boundaryType = boundaryType;
			this.predicate = operator.predicate( arg0 );
		}

		@Override
		public BoundaryType boundaryType()
		{
			return boundaryType;
		}

		@Override
		public boolean test( final RealLocalizable localizable )
		{
			return predicate.test( localizable );
		}

		@Override
		public UnaryMaskOperator operator()
		{
			return operator;
		}

		@Override
		public Predicate< ? super RealLocalizable > arg0()
		{
			return arg0;
		}

		@Override
		public boolean isEmpty()
		{
			return this.sourceInterval.isEmpty();
		}
	}
}
