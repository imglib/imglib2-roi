/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.roi.mask.integer;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.roi.mask.Mask;
import net.imglib2.type.BooleanType;
import net.imglib2.util.Intervals;

/**
 * Wraps a {@link RandomAccessibleInterval} as a {@link Mask}.
 *
 * @author Alison Walter
 */
public class RandomAccessibleIntervalAsMask< B extends BooleanType< B > > implements MaskInterval
{
	private final RandomAccessibleInterval< B > rai;

	private final RandomAccess< B > accessor;

	public RandomAccessibleIntervalAsMask( final RandomAccessibleInterval< B > rai )
	{
		this.rai = rai;
		accessor = rai.randomAccess();
	}

	public RandomAccessibleInterval< B > source()
	{
		return rai;
	}

	@Override
	public int numDimensions()
	{
		return accessor.numDimensions();
	}

	@Override
	public boolean test( final Localizable l )
	{
		if ( Intervals.contains( this, l ) )
		{
			accessor.setPosition( l );
			return accessor.get().get();
		}
		return false;
	}

	@Override
	public long min( final int d )
	{
		return rai.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		rai.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		rai.min( min );
	}

	@Override
	public long max( final int d )
	{
		return rai.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		rai.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		rai.max( max );
	}

	@Override
	public double realMin( final int d )
	{
		return rai.realMin( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		rai.realMin( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		rai.realMin( min );
	}

	@Override
	public double realMax( final int d )
	{
		return rai.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		rai.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		rai.realMax( max );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		rai.dimensions( dimensions );
	}

	@Override
	public long dimension( final int d )
	{
		return rai.dimension( d );
	}

}
