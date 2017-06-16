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
package net.imglib2.roi.mask.real;

import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.mask.Mask;
import net.imglib2.type.BooleanType;

/**
 * This class takes a {@link Mask} of {@link RealLocalizable} and wraps it as a
 * {@link RealRandomAccessibleRealInterval}.
 *
 * @author Alison Walter
 *
 * @param <B>
 *            {@link BooleanType} of RealRandomAccessibleRealInterval
 */
public class MaskAsRealRandomAccessibleRealInterval< B extends BooleanType< B > > implements RealRandomAccessibleRealInterval< B >
{

	private final MaskRealInterval mask;

	private final B type;

	public MaskAsRealRandomAccessibleRealInterval( final MaskRealInterval mask, final B type )
	{
		this.mask = mask;
		this.type = type;
	}

	public MaskRealInterval source()
	{
		return mask;
	}

	@Override
	public int numDimensions()
	{
		return mask.numDimensions();
	}

	@Override
	public RealRandomAccess< B > realRandomAccess()
	{
		return new MaskRealRandomAccess<>( mask, type );
	}

	@Override
	public RealRandomAccess< B > realRandomAccess( final RealInterval interval )
	{
		return realRandomAccess();
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

}
