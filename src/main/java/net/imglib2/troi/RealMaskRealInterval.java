package net.imglib2.troi;

import java.util.function.Predicate;

import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;

import static net.imglib2.troi.Operators.AND;
import static net.imglib2.troi.Operators.MINUS;
import static net.imglib2.troi.Operators.OR;
import static net.imglib2.troi.Operators.XOR;

public interface RealMaskRealInterval extends RealMask, RealInterval
{
	@Override
	public default RealMaskRealInterval and( final Predicate< ? super RealLocalizable > other )
	{
		return AND.applyRealInterval( this, other );
	}

	/*
	 * Note: *NOT* overriding RealMask.or(), just specializing for RealMaskRealInterval
	 * argument.
	 */
	public default RealMaskRealInterval or( final RealMaskRealInterval other )
	{
		return OR.applyRealInterval( this, other );
	}

	@Override
	public default RealMaskRealInterval minus( final Predicate< ? super RealLocalizable > other )
	{
		return MINUS.applyRealInterval( this, other );
	}

	/*
	 * Note: *NOT* overriding RealMask.xor(), just specializing for RealMaskRealInterval
	 * argument.
	 */
	public default RealMaskRealInterval xor( final RealMaskRealInterval other )
	{
		return XOR.applyRealInterval( this, other );
	}
}
