package net.imglib2.roi.composite;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.imglib2.AbstractWrappedRealInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.Operators.MaskOperator;
import net.imglib2.roi.Operators.RealTransformMaskOperator;
import net.imglib2.roi.RealMaskRealInterval;

/**
 * A {@link RealMaskRealInterval} which is the result of a transform operation
 * on a {@link RealMaskRealInterval}.
 *
 * @author Tobias Pietzsch
 * @author Alison Walter
 */
public class RealTransformUnaryCompositeRealMaskRealInterval extends
	AbstractWrappedRealInterval< RealInterval > implements
	UnaryCompositeMaskPredicate< RealLocalizable >, RealMaskRealInterval
{

	private final RealTransformMaskOperator operator;

	private final Predicate< ? super RealLocalizable > arg0;

	private final BoundaryType boundaryType;

	private final Predicate< ? super RealLocalizable > predicate;

	private final UnaryOperator< KnownConstant > knownConstantOp;

	public RealTransformUnaryCompositeRealMaskRealInterval(
		final RealTransformMaskOperator operator,
		final Predicate< ? super RealLocalizable > arg0, final RealInterval interval,
		final BoundaryType boundaryType,
		final UnaryOperator< KnownConstant > knownConstantOp )
	{
		super( interval );
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
		if ( obj instanceof RealTransformUnaryCompositeRealMask )
		{
			final RealTransformUnaryCompositeRealMask rtucrm =
				( RealTransformUnaryCompositeRealMask ) obj;
			return operator.equals( rtucrm.operator() ) && arg0.equals( rtucrm.arg0() );
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return ( arg0.hashCode() + operator.hashCode() ) * 33;
	}
}
