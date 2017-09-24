package net.imglib2.troi;

import java.util.function.Predicate;
import net.imglib2.Interval;
import net.imglib2.Localizable;

public interface MaskInterval extends Mask, Interval
{
	@Override
	public default MaskInterval and( Predicate< ? super Localizable > other )
	{
		return Masks.and( this, other );
	}
}
