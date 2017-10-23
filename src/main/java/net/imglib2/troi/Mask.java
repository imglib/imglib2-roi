package net.imglib2.troi;

import static net.imglib2.troi.Operators.AND;
import static net.imglib2.troi.Operators.MINUS;
import static net.imglib2.troi.Operators.NEGATE;
import static net.imglib2.troi.Operators.OR;
import static net.imglib2.troi.Operators.XOR;

import java.util.function.Predicate;

import net.imglib2.Localizable;
import net.imglib2.transform.Transform;

/**
 * A {@link MaskPredicate} for integral {@link Localizable}. Results of
 * operations ({@code and, or, negate}, etc) are also {@code Mask}s.
 *
 * @author Tobias Pietzsch
 */
public interface Mask extends MaskPredicate< Localizable >
{
	@Override
	default Mask and( final Predicate< ? super Localizable > other )
	{
		return AND.apply( this, other );
	}

	@Override
	default Mask or( final Predicate< ? super Localizable > other )
	{
		return OR.apply( this, other );
	}

	@Override
	default Mask negate()
	{
		return NEGATE.apply( this );
	}

	@Override
	default Mask minus( final Predicate< ? super Localizable > other )
	{
		return MINUS.apply( this, other );
	}

	@Override
	default Mask xor( final Predicate< ? super Localizable > other )
	{
		return XOR.apply( this, other );
	}

	/*
	 * TODO: transformFromSource or transformToSource? TODO: should this really
	 * be a method in the interface?
	 */
	default Mask transform( final Transform transformToSource )
	{
		throw new UnsupportedOperationException( "TODO, not yet implemented" );
	}
}
