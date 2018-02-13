package net.imglib2.roi.composite;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.Operators.MaskOperator;
import net.imglib2.roi.Operators.RealTransformMaskOperator;
import net.imglib2.roi.RealMask;

/**
 * A {@link RealMask} which is the result of a transform operation on a
 * {@link Predicate}.
 *
 * @author Tobias Pietzsch
 */
public class RealTransformUnaryCompositeRealMask
		extends AbstractEuclideanSpace
		implements UnaryCompositeMaskPredicate< RealLocalizable >, RealMask
{
	private final RealTransformMaskOperator operator;

	private final Predicate< ? super RealLocalizable > arg0;

	private final BoundaryType boundaryType;

	private final Predicate< ? super RealLocalizable > predicate;

	private final UnaryOperator< KnownConstant > knownConstantOp;

	public RealTransformUnaryCompositeRealMask(
			final RealTransformMaskOperator operator,
			final Predicate< ? super RealLocalizable > arg0,
			final int numDimensions,
			final BoundaryType boundaryType,
			final UnaryOperator< KnownConstant > knownConstantOp )
	{
		super( numDimensions );
		this.operator = operator;
		this.arg0 = arg0;
		this.boundaryType = boundaryType;
		this.predicate = operator.predicate( arg0 );
		this.knownConstantOp = knownConstantOp;
	}

	@Override
	public BoundaryType boundaryType()
	{
		return boundaryType;
	}

	@Override
	public KnownConstant knownConstant()
	{
		return knownConstantOp.apply( KnownConstant.of( arg0 ) );
	}

	@Override
	public boolean test( final RealLocalizable localizable )
	{
		return predicate.test( localizable );
	}

	@Override
	public MaskOperator operator()
	{
		return operator;
	}

	@Override
	public Predicate< ? super RealLocalizable > arg0()
	{
		return arg0;
	}

	@Override
	public boolean equals( final Object obj )
	{
		if( obj instanceof RealTransformUnaryCompositeRealMask )
		{
			RealTransformUnaryCompositeRealMask rtucrm = ( RealTransformUnaryCompositeRealMask ) obj;
			return operator.equals( rtucrm.operator() ) && arg0.equals( rtucrm.arg0() );
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return arg0.hashCode() + operator.hashCode();
	}
}
