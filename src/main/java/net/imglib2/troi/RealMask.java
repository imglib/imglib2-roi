package net.imglib2.troi;

import java.util.function.Predicate;

import net.imglib2.EuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.realtransform.RealTransform;

import static net.imglib2.troi.Operators.AND;
import static net.imglib2.troi.Operators.MINUS;
import static net.imglib2.troi.Operators.NEGATE;
import static net.imglib2.troi.Operators.OR;
import static net.imglib2.troi.Operators.XOR;

public interface RealMask extends MaskPredicate< RealLocalizable >, EuclideanSpace
{
	@Override
	public default RealMask and( final Predicate< ? super RealLocalizable > other )
	{
		return AND.applyReal( this, other );
	}

	@Override
	public default RealMask or( final Predicate< ? super RealLocalizable > other )
	{
		return OR.applyReal( this, other );
	}

	@Override
	public default RealMask negate()
	{
		return NEGATE.applyReal( this );
	}

	@Override
	public default RealMask minus( final Predicate< ? super RealLocalizable > other )
	{
		return MINUS.applyReal( this, other );
	}

	@Override
	public default RealMask xor( final Predicate< ? super RealLocalizable > other )
	{
		return XOR.applyReal( this, other );
	}

	/*
	 * TODO: transformFromSource or transformToSource?
	 * TODO: should this really be a method in the interface?
	 */
	public default RealMask transform( final RealTransform transformToSource )
	{
		throw new UnsupportedOperationException( "TODO, not yet implemented" );
	}
}
