package net.imglib2.troi;

import java.util.function.Predicate;

import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;

public interface RealMaskRealInterval extends RealMask, RealInterval
{
	@Override
	public default RealMaskRealInterval and( final Predicate< ? super RealLocalizable > other )
	{
		return Masks.and( this, other );
	}

	/*
	 * Note: *NOT* overriding RealMask.or(), just specializing for RealMaskRealInterval
	 * argument.
	 */
	public default RealMaskRealInterval or( final RealMaskRealInterval other )
	{
		return Masks.or( this, other );
	}

	@Override
	public default RealMaskRealInterval minus( final Predicate< ? super RealLocalizable > other )
	{
		return Masks.minus( this, other );
	}

	/*
	 * Note: *NOT* overriding RealMask.xor(), just specializing for RealMaskRealInterval
	 * argument.
	 */
	public default RealMaskRealInterval xor( final RealMaskRealInterval other )
	{
		return Masks.xor( this, other );
	}
}
