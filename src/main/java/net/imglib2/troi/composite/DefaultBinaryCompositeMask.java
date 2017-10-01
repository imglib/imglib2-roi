package net.imglib2.troi.composite;

import java.util.function.Predicate;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.Mask;
import net.imglib2.troi.Masks;

public class DefaultBinaryCompositeMask
		extends AbstractEuclideanSpace
		implements BinaryCompositeMaskPredicate< Localizable >, Mask
{
	private final Masks.BinaryMaskOperator operator;

	private final Predicate< ? super Localizable > arg0;

	private final Predicate< ? super Localizable > arg1;

	private final BoundaryType boundaryType;

	private final Predicate< ? super Localizable > predicate;

	public DefaultBinaryCompositeMask(
			Masks.BinaryMaskOperator operator,
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
	public Masks.BinaryMaskOperator operator()
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
