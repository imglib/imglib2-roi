package net.imglib2.troi.composite;

import java.util.function.Predicate;

import net.imglib2.AbstractWrappedRealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.Bounds;
import net.imglib2.troi.Operators.BinaryMaskOperator;
import net.imglib2.troi.RealMaskRealInterval;

/**
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

	public DefaultBinaryCompositeRealMaskRealInterval(
			final BinaryMaskOperator operator,
			final Predicate< ? super RealLocalizable > arg0,
			final Predicate< ? super RealLocalizable > arg1,
			final Bounds.RealIntervalOrEmpty interval,
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
