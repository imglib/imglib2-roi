package net.imglib2.troi;

import java.util.function.Predicate;

import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.transform.Transform;

import static net.imglib2.troi.Operators.AND;
import static net.imglib2.troi.Operators.MINUS;
import static net.imglib2.troi.Operators.NEGATE;
import static net.imglib2.troi.Operators.OR;
import static net.imglib2.troi.Operators.XOR;

public interface Mask extends MaskPredicate< Localizable >, EuclideanSpace
{
	@Override
	public default Mask and( final Predicate< ? super Localizable > other )
	{
		return AND.apply( this, other );
	}

	@Override
	public default Mask or( final Predicate< ? super Localizable > other )
	{
		return OR.apply( this, other );
	}

	@Override
	public default Mask negate()
	{
		return NEGATE.apply( this );
	}

	@Override
	public default Mask minus( final Predicate< ? super Localizable > other )
	{
		return MINUS.apply( this, other );
	}

	@Override
	public default Mask xor( final Predicate< ? super Localizable > other )
	{
		return XOR.apply( this, other );
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
