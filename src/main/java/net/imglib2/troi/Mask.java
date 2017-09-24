package net.imglib2.troi;

import java.util.function.Predicate;
import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;

public interface Mask extends MaskPredicate< Localizable >, EuclideanSpace
{
	@Override
	public default Mask and( Predicate< ? super Localizable > other )
	{
		return Masks.and( this, other );
	}

	@Override
	public default Mask or( Predicate< ? super Localizable > other )
	{
		throw new UnsupportedOperationException( "TODO" );
	}

	@Override
	public default Mask negate()
	{
		throw new UnsupportedOperationException( "TODO" );
	}
}
