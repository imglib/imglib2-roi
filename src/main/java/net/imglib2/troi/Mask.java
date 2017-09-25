package net.imglib2.troi;

import java.util.function.Predicate;

import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.transform.Transform;

public interface Mask extends MaskPredicate< Localizable >, EuclideanSpace
{
	@Override
	public default Mask and( final Predicate< ? super Localizable > other )
	{
		return Masks.and( this, other );
	}

	@Override
	public default Mask or( final Predicate< ? super Localizable > other )
	{
		return Masks.or( this, other );
	}

	@Override
	public default Mask negate()
	{
		return Masks.negate( this );
	}

	@Override
	public default Mask minus( final Predicate< ? super Localizable > other )
	{
		return Masks.minus( this, other );
	}

	@Override
	public default Mask xor( final Predicate< ? super Localizable > other )
	{
		return Masks.xor( this, other );
	}

	/*
	 * TODO: transformFromSource or transformToSource?
	 * TODO: should this really be a method in the interface?
	 */
	public default Mask transform( final Transform transformToSource )
	{
		throw new UnsupportedOperationException( "TODO, not yet implemented" );
	}
}
