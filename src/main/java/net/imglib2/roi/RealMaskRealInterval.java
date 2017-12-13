/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2017 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.roi;

import static net.imglib2.roi.Operators.AND;
import static net.imglib2.roi.Operators.MINUS;
import static net.imglib2.roi.Operators.OR;
import static net.imglib2.roi.Operators.XOR;

import java.util.function.Predicate;

import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.realtransform.AffineGet;
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
		return Intervals.isEmpty( this ) || knownConstant() == KnownConstant.ALL_FALSE;
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
	default RealMaskRealInterval transform( final AffineGet transformToSource )
	{
		return new Operators.RealTransformMaskOperator( transformToSource ).applyRealInterval( this );
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
