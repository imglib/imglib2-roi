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

import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;
import net.imglib2.roi.mask.Mask;
import net.imglib2.type.BooleanType;

/**
 * This class takes a {@link MaskInterval} and wraps it as a
 * {@link RandomAccessibleInterval}.
 *
 * @author Alison Walter
 *
 * @param <B>
 *            {@link BooleanType} of RealRandomAccessibleInterval
 */
public class MaskAsRandomAccessibleInterval< B extends BooleanType< B > > implements RandomAccessibleInterval< B >
{
	private final MaskInterval mask;

	private final B type;

	/**
	 * Wraps a {@link Mask} as a {@link RandomAccessibleInterval}.
	 *
	 * @param mask
	 *            Mask to be wrapped
	 * @param type
	 *            {@link BooleanType} the RandomAccess should return
	 */
	public MaskAsRandomAccessibleInterval( final MaskInterval mask, final B type )
	{
		this.mask = mask;
		this.type = type;
	}

	public MaskInterval source()
	{
		return mask;
	}

	@Override
	public int numDimensions()
	{
		return mask.numDimensions();
	}

	@Override
	public RandomAccess< B > randomAccess()
	{
		return new MaskRandomAccess<>( mask, type );
	}

	@Override
	public RandomAccess< B > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	@Override
	public long min( final int d )
	{
		return mask.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		mask.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		mask.min( min );
	}

	@Override
	public long max( final int d )
	{
		return mask.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		mask.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		mask.max( max );
	}

	@Override
	public double realMin( final int d )
	{
		return mask.realMin( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		mask.realMin( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		mask.realMin( min );
	}

	@Override
	public double realMax( final int d )
	{
		return mask.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		mask.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		mask.realMax( max );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		mask.dimensions( dimensions );
	}

	@Override
	public long dimension( final int d )
	{
		return mask.dimension( d );
	}

}
