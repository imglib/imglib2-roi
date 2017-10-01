package net.imglib2.troi.composite;

import java.util.function.Predicate;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.Mask;
import net.imglib2.troi.Masks;

public class DefaultUnaryCompositeMask
		extends AbstractEuclideanSpace
		implements UnaryCompositeMaskPredicate< Localizable >, Mask
{
	private final Masks.UnaryMaskOperator operator;

	private final Predicate< ? super Localizable > arg0;

	private final BoundaryType boundaryType;

	private final Predicate< ? super Localizable > predicate;

	public DefaultUnaryCompositeMask(
			Masks.UnaryMaskOperator operator,
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
	public Masks.UnaryMaskOperator operator()
	{
		return operator;
	}

	@Override
	public Predicate< ? super Localizable > arg0()
	{
		return arg0;
	}
}
