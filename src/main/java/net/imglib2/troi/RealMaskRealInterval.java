package net.imglib2.troi;

import static net.imglib2.troi.Operators.AND;
import static net.imglib2.troi.Operators.MINUS;
import static net.imglib2.troi.Operators.OR;
import static net.imglib2.troi.Operators.XOR;

import java.util.function.Predicate;

import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.realtransform.InvertibleRealTransform;
import net.imglib2.util.Intervals;

/**
 * A bounded {@link RealMask}, that is, the mask predicate evaluates to
 * {@code false} outside the bounds interval. Results of operations are
 * {@code RealMaskRealInterval}s where this is guaranteed. For example
 * {@code and()} of a {@code RealMaskRealInterval} with any predicate will
 * always have bounds (smaller or equal to the {@code RealMaskRealInterval}).
 *
 * @author Tobias Pietzsch
 */
public interface RealMaskRealInterval extends RealMask, RealInterval
{

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * If this {@link RealInterval} is empty (i.e. min &gt; max), then
	 * {@link #test} should always return {@code false}.
	 * </p>
	 */
	@Override
	default boolean isEmpty()
	{
		return Intervals.isEmpty( this );
	}

	@Override
	default RealMaskRealInterval and( final Predicate< ? super RealLocalizable > other )
	{
		return AND.applyRealInterval( this, other );
	}

	/*
	 * Note: *NOT* overriding RealMask.or(), just specializing for
	 * RealMaskRealInterval argument.
	 */
	default RealMaskRealInterval or( final RealMaskRealInterval other )
	{
		return OR.applyRealInterval( this, other );
	}

	@Override
	default RealMaskRealInterval minus( final Predicate< ? super RealLocalizable > other )
	{
		return MINUS.applyRealInterval( this, other );
	}

	/*
	 * Note: *NOT* overriding RealMask.xor(), just specializing for
	 * RealMaskRealInterval argument.
	 */
	default RealMaskRealInterval xor( final RealMaskRealInterval other )
	{
		return XOR.applyRealInterval( this, other );
	}

	/*
	 * Note: *NOT* overriding RealMask.transform()
	 */
	default RealMaskRealInterval transform( final InvertibleRealTransform transformToSource )
	{
		return ( new Operators.RealMaskRealTransformOperator( transformToSource ) ).applyRealInterval( this );
	}

	// -- RealInterval Methods --

	@Override
	default void realMin( final double[] min )
	{
		for ( int i = 0; i < numDimensions(); i++ )
			min[ i ] = realMin( i );
	}

	@Override
	default void realMin( final RealPositionable min )
	{
		for ( int i = 0; i < numDimensions(); i++ )
			min.setPosition( realMin( i ), i );
	}

	@Override
	default void realMax( final double[] max )
	{
		for ( int i = 0; i < numDimensions(); i++ )
			max[ i ] = realMax( i );
	}

	@Override
	default void realMax( final RealPositionable max )
	{
		for ( int i = 0; i < numDimensions(); i++ )
			max.setPosition( realMax( i ), i );
	}
}
