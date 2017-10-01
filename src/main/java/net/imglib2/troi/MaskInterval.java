package net.imglib2.troi;

import java.util.function.Predicate;

import net.imglib2.Interval;
import net.imglib2.Localizable;

import static net.imglib2.troi.Operators.AND;
import static net.imglib2.troi.Operators.MINUS;
import static net.imglib2.troi.Operators.OR;
import static net.imglib2.troi.Operators.XOR;

public interface MaskInterval extends Mask, Interval
{
	@Override
	public default MaskInterval and( final Predicate< ? super Localizable > other )
	{
		return AND.applyInterval( this, other );
	}

	/*
	 * Note: *NOT* overriding Mask.or(), just specializing for MaskInterval
	 * argument.
	 */
	public default MaskInterval or( final MaskInterval other )
	{
		return OR.applyInterval( this, other );
	}

	@Override
	public default MaskInterval minus( final Predicate< ? super Localizable > other )
	{
		return MINUS.applyInterval( this, other );
	}

	/*
	 * Note: *NOT* overriding Mask.xor(), just specializing for MaskInterval
	 * argument.
	 */
	public default MaskInterval xor( final MaskInterval other )
	{
		return XOR.applyInterval( this, other );
	}
}
