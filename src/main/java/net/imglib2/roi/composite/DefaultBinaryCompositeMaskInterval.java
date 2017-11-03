package net.imglib2.roi.composite;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.MaskInterval;
import net.imglib2.roi.Operators.BinaryMaskOperator;
import net.imglib2.util.Intervals;

/**
 * A {@link MaskInterval} which results from an operation on two
 * {@link Predicate}s.
 *
 * @author Tobias Pietzsch
 */
public class DefaultBinaryCompositeMaskInterval
		extends AbstractWrappedInterval< Interval >
		implements BinaryCompositeMaskPredicate< Localizable >, MaskInterval
{
	private final BinaryMaskOperator operator;

	private final Predicate< ? super Localizable > arg0;

	private final Predicate< ? super Localizable > arg1;

	private final BoundaryType boundaryType;

	private final Predicate< ? super Localizable > predicate;

	private final BiPredicate< Predicate< ? >, Predicate< ? > > emptyOp;

	private final boolean isAll;

	public DefaultBinaryCompositeMaskInterval(
			final BinaryMaskOperator operator,
			final Predicate< ? super Localizable > arg0,
			final Predicate< ? super Localizable > arg1,
			final Interval interval,
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
		return Intervals.isEmpty( sourceInterval ) || emptyOp.test( arg0, arg1 );
	}

	@Override
	public boolean isAll()
	{
		return isAll;
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof BinaryCompositeMaskPredicate ) || !( obj instanceof MaskInterval ) )
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
