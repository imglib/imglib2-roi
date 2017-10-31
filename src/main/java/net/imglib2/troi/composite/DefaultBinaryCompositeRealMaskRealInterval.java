package net.imglib2.troi.composite;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.imglib2.AbstractWrappedRealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.Bounds;
import net.imglib2.troi.Operators.BinaryMaskOperator;
import net.imglib2.troi.RealMaskRealInterval;

/**
 * A {@link RealMaskRealInterval} which results from an operation on two
 * {@link Predicate}s.
 *
 * @author Tobias Pietzsch
 */
public class DefaultBinaryCompositeRealMaskRealInterval
		extends AbstractWrappedRealInterval< Bounds.RealIntervalOrEmpty >
		implements BinaryCompositeMaskPredicate< RealLocalizable >, RealMaskRealInterval, Bounds.RealIntervalOrEmpty
{
	private final BinaryMaskOperator operator;

	private final Predicate< ? super RealLocalizable > arg0;

	private final Predicate< ? super RealLocalizable > arg1;

	private final BoundaryType boundaryType;

	private final Predicate< ? super RealLocalizable > predicate;

	private final BiPredicate< Predicate< ? >, Predicate< ? > > emptyOp;

	private final boolean isAll;

	public DefaultBinaryCompositeRealMaskRealInterval(
			final BinaryMaskOperator operator,
			final Predicate< ? super RealLocalizable > arg0,
			final Predicate< ? super RealLocalizable > arg1,
			final Bounds.RealIntervalOrEmpty interval,
			final BoundaryType boundaryType,
			final BiPredicate< Predicate< ? >, Predicate< ? > > emptyOp,
			final boolean isAll )
	{
		super( interval );
		this.operator = operator;
		this.arg0 = arg0;
		this.arg1 = arg1;
		this.boundaryType = boundaryType;
		this.predicate = operator.predicate( arg0, arg1 );
		this.emptyOp = emptyOp;
		this.isAll = isAll;
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
		return this.sourceInterval.isEmpty() || emptyOp.test( arg0, arg1 );
	}

	@Override
	public boolean isAll()
	{
		return isAll;
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof BinaryCompositeMaskPredicate ) || !( obj instanceof RealMaskRealInterval ) )
			return false;

		final BinaryCompositeMaskPredicate< ? > b = ( BinaryCompositeMaskPredicate< ? > ) obj;
		return b.operator() == operator && arg0.equals( b.arg0() ) && arg1.equals( b.arg1() );
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}
