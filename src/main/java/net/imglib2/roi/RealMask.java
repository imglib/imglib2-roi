package net.imglib2.roi;

import static net.imglib2.roi.Operators.AND;
import static net.imglib2.roi.Operators.MINUS;
import static net.imglib2.roi.Operators.NEGATE;
import static net.imglib2.roi.Operators.OR;
import static net.imglib2.roi.Operators.XOR;

import java.util.function.Predicate;

import net.imglib2.RealLocalizable;
import net.imglib2.realtransform.RealTransform;

/**
 * A {@link MaskPredicate} for {@link RealLocalizable}. Results of operations
 * ({@code and, or, negate}, etc) are also {@code RealMask}s.
 *
 * @author Tobias Pietzsch
 */
public interface RealMask extends MaskPredicate< RealLocalizable >
{
	@Override
	default RealMask and( final Predicate< ? super RealLocalizable > other )
	{
		return AND.applyReal( this, other );
	}

	@Override
	default RealMask or( final Predicate< ? super RealLocalizable > other )
	{
		return OR.applyReal( this, other );
	}

	@Override
	default RealMask negate()
	{
		return NEGATE.applyReal( this );
	}

	@Override
	default RealMask minus( final Predicate< ? super RealLocalizable > other )
	{
		return MINUS.applyReal( this, other );
	}

	@Override
	default RealMask xor( final Predicate< ? super RealLocalizable > other )
	{

		return XOR.applyReal( this, other );
	}

	default RealMask transform( final RealTransform transformToSource )
	{
		return ( new Operators.RealMaskRealTransformOperator( transformToSource ) ).applyReal( this );
	}
}
