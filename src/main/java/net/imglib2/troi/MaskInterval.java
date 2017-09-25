package net.imglib2.troi;

import java.util.function.Predicate;

import net.imglib2.Interval;
import net.imglib2.Localizable;

public interface MaskInterval extends Mask, Interval
{
	@Override
	public default MaskInterval and( final Predicate< ? super Localizable > other )
	{
		return Masks.and( this, other );
	}

	/*
	 * Note: *NOT* overriding Mask.or(), just specializing for MaskInterval
	 * argument.
	 */
	public default MaskInterval or( final MaskInterval other )
	{
		return Masks.or( this, other );
	}

	@Override
	public default MaskInterval minus( final Predicate< ? super Localizable > other )
	{
		return Masks.minus( this, other );
	}

	/*
	 * Note: *NOT* overriding Mask.xor(), just specializing for MaskInterval
	 * argument.
	 */
	public default MaskInterval xor( final MaskInterval other )
	{
		return Masks.xor( this, other );
	}
}
