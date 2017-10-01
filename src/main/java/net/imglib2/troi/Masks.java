package net.imglib2.troi;

import java.util.function.Predicate;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.troi.util.RealMaskRealIntervalAsRRARI;
import net.imglib2.type.logic.BoolType;

import static net.imglib2.troi.Operators.AND;

/**
 * Utility class for working with {@link Mask}s.
 *
 * @author Curtis Rueden
 * @author Alison Walter
 * @author Tobias Pietzsch
 */
public class Masks
{
	/*
	 * Methods for integer masks
	 * ===============================================================
	 */

	public static Mask and( final Mask left, final Predicate< ? super Localizable > right )
	{
		return left.and( right );
	}

	public static MaskInterval and( final MaskInterval left, final Predicate< ? super Localizable > right )
	{
		return left.and( right );
	}

	// TODO: do we need/want this:
	public static MaskInterval andMaskInterval( final Predicate< ? super Localizable > left, final MaskInterval right )
	{
		return AND.applyInterval( left, right );
	}

	public static Mask or( final Mask left, final Predicate< ? super Localizable > right )
	{
		return left.or( right );
	}

	public static MaskInterval or( final MaskInterval left, final MaskInterval right )
	{
		return left.or( right );
	}

	public static Mask xor( final Mask left, final Predicate< ? super Localizable > right )
	{
		return left.xor( right );
	}

	public static MaskInterval xor( final MaskInterval left, final MaskInterval right )
	{
		return left.xor( right );
	}

	public static Mask minus( final Mask left, final Predicate< ? super Localizable > right )
	{
		return left.minus( right );
	}

	public static MaskInterval minus( final MaskInterval left, final Predicate< ? super Localizable > right )
	{
		return left.minus( right );
	}

	public static Mask negate( final Mask arg )
	{
		return arg.negate();
	}


	/*
	 * Methods for real masks
	 * ===============================================================
	 */

	static RealMask and( final RealMask left, final Predicate< ? super RealLocalizable > right )
	{
		return left.and( right );
	}

	static RealMaskRealInterval and( final RealMaskRealInterval left, final Predicate< ? super RealLocalizable > right )
	{
		return left.and( right );
	}

	// TODO: do we need/want this:
	static RealMaskRealInterval andMaskInterval( final Predicate< ? super RealLocalizable > left, final RealMaskRealInterval right )
	{
		return AND.applyRealInterval( left, right );
	}

	public static RealMask or( final RealMask left, final Predicate< ? super RealLocalizable > right )
	{
		return left.or( right );
	}

	public static RealMaskRealInterval or( final RealMaskRealInterval left, final RealMaskRealInterval right )
	{
		return left.or( right );
	}

	public static RealMask xor( final RealMask left, final Predicate< ? super RealLocalizable > right )
	{
		return left.xor( right );
	}

	public static RealMaskRealInterval xor( final RealMaskRealInterval left, final RealMaskRealInterval right )
	{
		return left.xor( right );
	}

	public static RealMask minus( final RealMask left, final Predicate< ? super RealLocalizable > right )
	{
		return left.minus( right );
	}

	public static RealMaskRealInterval minus( final RealMaskRealInterval left, final Predicate< ? super RealLocalizable > right )
	{
		return left.minus( right );
	}

	public static RealMask negate( final RealMask arg )
	{
		return arg.negate();
	}

	/*
	 * RandomAccessible Wrappers
	 */
	public static RealRandomAccessibleRealInterval< BoolType > toRRARI( final RealMaskRealInterval mask )
	{
		return new RealMaskRealIntervalAsRRARI<>( mask, new BoolType() );
	}
}
