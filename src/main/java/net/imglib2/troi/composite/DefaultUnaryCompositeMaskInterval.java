package net.imglib2.troi.composite;

import java.util.function.Predicate;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Localizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.Bounds;
import net.imglib2.troi.MaskInterval;
import net.imglib2.troi.Operators.UnaryMaskOperator;

/**
 * @author Tobias Pietzsch
 */
public class DefaultUnaryCompositeMaskInterval
		extends AbstractWrappedInterval< Bounds.IntervalOrEmpty >
		implements UnaryCompositeMaskPredicate< Localizable >, MaskInterval, Bounds.IntervalOrEmpty
{
	private final UnaryMaskOperator operator;

	private final Predicate< ? super Localizable > arg0;

	private final BoundaryType boundaryType;

	private final Predicate< ? super Localizable > predicate;

	public DefaultUnaryCompositeMaskInterval(
			final UnaryMaskOperator operator,
			final Predicate< ? super Localizable > arg0,
			final Bounds.IntervalOrEmpty interval,
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

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof UnaryCompositeMaskPredicate ) || !( obj instanceof MaskInterval ) )
			return false;

		final UnaryCompositeMaskPredicate< ? > u = ( UnaryCompositeMaskPredicate< ? > ) obj;
		return u.operator() == operator && arg0.equals( u.arg0() );
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}
