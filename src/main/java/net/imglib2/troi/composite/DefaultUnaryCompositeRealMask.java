package net.imglib2.troi.composite;

import java.util.function.Predicate;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.Operators;
import net.imglib2.troi.RealMask;

public class DefaultUnaryCompositeRealMask
		extends AbstractEuclideanSpace
		implements UnaryCompositeMaskPredicate< RealLocalizable >, RealMask
{
	private final Operators.UnaryMaskOperator operator;

	private final Predicate< ? super RealLocalizable > arg0;

	private final BoundaryType boundaryType;

	private final Predicate< ? super RealLocalizable > predicate;

	public DefaultUnaryCompositeRealMask(
			Operators.UnaryMaskOperator operator,
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
	public Operators.UnaryMaskOperator operator()
	{
		return operator;
	}

	@Override
	public Predicate< ? super RealLocalizable > arg0()
	{
		return arg0;
	}
}
