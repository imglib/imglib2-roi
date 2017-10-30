package net.imglib2.troi.composite;

import java.util.function.Predicate;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.Operators.BinaryMaskOperator;
import net.imglib2.troi.RealMask;

/**
 * @author Tobias Pietzsch
 */
public class DefaultBinaryCompositeRealMask
		extends AbstractEuclideanSpace
		implements BinaryCompositeMaskPredicate< RealLocalizable >, RealMask
{
	private final BinaryMaskOperator operator;

	private final Predicate< ? super RealLocalizable > arg0;

	private final Predicate< ? super RealLocalizable > arg1;

	private final BoundaryType boundaryType;

	private final Predicate< ? super RealLocalizable > predicate;

	public DefaultBinaryCompositeRealMask(
			final BinaryMaskOperator operator,
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

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof BinaryCompositeMaskPredicate ) || !( obj instanceof RealMask ) )
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
