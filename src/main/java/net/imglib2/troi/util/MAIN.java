package net.imglib2.troi.util;

import java.util.function.Predicate;

import net.imglib2.Localizable;
import net.imglib2.troi.Mask;
import net.imglib2.troi.MaskInterval;
import net.imglib2.troi.Masks;

public class MAIN
{
	public static void main( final String[] args )
	{
		final Mask a = null;
		final Mask b = null;
		final MaskInterval ia = null;
		final MaskInterval ib = null;
		final Predicate< ? super Localizable > pa = null;
		final Predicate< ? super Localizable > pb = null;
		Mask m;
		MaskInterval mi;
		m = Masks.and( a, b );
		m = a.and( b );
		mi = Masks.andMaskInterval( a, ib );
//		mi = a.and( ib ); doesn't work (a is Mask)
		m = Masks.and( a, pb );
		m = a.and( pb );
		mi = Masks.and( ia, b );
		mi = ia.and( b );
		mi = Masks.and( ia, ib );
		mi = ia.and( ib );
		mi = Masks.and( ia, pb );
		mi = ia.and( pb );
//		m = Masks.and( pa, b ); could work if we separate RealMasks
//		m = pa.and( b ); doesn't work (pa is Predicate)
		mi = Masks.andMaskInterval( pa, ib );
//		mi = pa.and( ib ); doesn't work (pa is Predicate)
//		m = Masks.and( pa, pb ); could work if we separate RealMasks
//		m = pa.and( pb ); doesn't work (pa is Predicate)




		mi = ia.or( ib );
		m = ia.or( a );
	}
}
