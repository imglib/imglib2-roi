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
package net.imglib2.roi.util;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * A {@link Positionable} {@link Localizable} {@link Interval} delegating all
 * calls to a source.
 *
 * @author Christian Dietz
 * @author Tobias Pietzsch
 */
// TODO: move to imglib2-core?!
public class AbstractWrappedPositionableInterval< P extends Positionable & Localizable & Interval >
	extends AbstractWrappedInterval< P >
	implements Positionable, Localizable
{
	public AbstractWrappedPositionableInterval( final P delegate )
	{
		super( delegate );
	}

	@Override
	public void localize( final int[] position )
	{
		sourceInterval.localize( position );
	}

	@Override
	public void localize( final long[] position )
	{
		sourceInterval.localize( position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return sourceInterval.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return sourceInterval.getLongPosition( d );
	}

	@Override
	public void localize( final float[] position )
	{
		sourceInterval.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		sourceInterval.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return sourceInterval.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return sourceInterval.getDoublePosition( d );
	}

	@Override
	public int numDimensions()
	{
		return sourceInterval.numDimensions();
	}

	@Override
	public void fwd( final int d )
	{
		sourceInterval.fwd( d );
	}

	@Override
	public void bck( final int d )
	{
		sourceInterval.bck( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		sourceInterval.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		sourceInterval.move( distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		sourceInterval.move( localizable );
	}

	@Override
	public void move( final int[] distance )
	{
		sourceInterval.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		sourceInterval.move( distance );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		sourceInterval.setPosition( localizable );
	}

	@Override
	public void setPosition( final int[] position )
	{
		sourceInterval.setPosition( position );
	}

	@Override
	public void setPosition( final long[] position )
	{
		sourceInterval.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		sourceInterval.setPosition( position, d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		sourceInterval.setPosition( position, d );
	}
}
