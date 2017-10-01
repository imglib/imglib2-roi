package net.imglib2.troi.composite;

import java.util.function.Predicate;
import net.imglib2.AbstractWrappedRealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.Bounds;
import net.imglib2.troi.Operators;
import net.imglib2.troi.RealMaskRealInterval;

public class DefaultUnaryCompositeRealMaskRealInterval
		extends AbstractWrappedRealInterval< Bounds.RealIntervalOrEmpty >
		implements UnaryCompositeMaskPredicate< RealLocalizable >, RealMaskRealInterval, Bounds.RealIntervalOrEmpty
{
	private final Operators.UnaryMaskOperator operator;

	private final Predicate< ? super RealLocalizable > arg0;

	private final BoundaryType boundaryType;

	private final Predicate< ? super RealLocalizable > predicate;

	public DefaultUnaryCompositeRealMaskRealInterval(
			Operators.UnaryMaskOperator operator,
			final Predicate< ? super RealLocalizable > arg0,
			final Bounds.RealIntervalOrEmpty interval,
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
	public Operators.UnaryMaskOperator operator()
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
