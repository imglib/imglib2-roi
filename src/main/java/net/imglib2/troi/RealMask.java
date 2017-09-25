package net.imglib2.troi;

import java.util.function.Predicate;

import net.imglib2.EuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.realtransform.RealTransform;

public interface RealMask extends MaskPredicate< RealLocalizable >, EuclideanSpace
{
	@Override
	public default RealMask and( final Predicate< ? super RealLocalizable > other )
	{
		return Masks.and( this, other );
	}

	@Override
	public default RealMask or( final Predicate< ? super RealLocalizable > other )
	{
		return Masks.or( this, other );
	}

	@Override
	public default RealMask negate()
	{
		return Masks.negate( this );
	}

	@Override
	public default RealMask minus( final Predicate< ? super RealLocalizable > other )
	{
		return Masks.minus( this, other );
	}

	@Override
	public default RealMask xor( final Predicate< ? super RealLocalizable > other )
	{
		return Masks.xor( this, other );
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
