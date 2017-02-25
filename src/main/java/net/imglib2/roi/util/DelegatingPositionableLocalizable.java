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

import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * A {@link Positionable} {@link Localizable} delegating all calls to a
 * <P> delegate
 *
 * @author Christian Dietz
 *
 * @param <P>
 */
// TODO: move to imglib2-core?!
public class DelegatingPositionableLocalizable< P extends Positionable & Localizable > implements Positionable, Localizable
{
	protected P delegate;

	public DelegatingPositionableLocalizable( final P delegate )
	{
		this.delegate = delegate;
	}

	@Override
	public void localize( int[] position )
	{
		delegate.localize( position );
	}

	@Override
	public void localize( long[] position )
	{
		delegate.localize( position );
	}

	@Override
	public int getIntPosition( int d )
	{
		return delegate.getIntPosition( d );
	}

	@Override
	public long getLongPosition( int d )
	{
		return delegate.getLongPosition( d );
	}

	@Override
	public void localize( float[] position )
	{
		delegate.localize( position );
	}

	@Override
	public void localize( double[] position )
	{
		delegate.localize( position );
	}

	@Override
	public float getFloatPosition( int d )
	{
		return delegate.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( int d )
	{
		return delegate.getDoublePosition( d );
	}

	@Override
	public int numDimensions()
	{
		return delegate.numDimensions();
	}

	@Override
	public void fwd( int d )
	{
		delegate.fwd( d );
	}

	@Override
	public void bck( int d )
	{
		delegate.bck( d );
	}

	@Override
	public void move( int distance, int d )
	{
		delegate.move( distance, d );
	}

	@Override
	public void move( long distance, int d )
	{
		delegate.move( distance, d );
	}

	@Override
	public void move( Localizable localizable )
	{
		delegate.move( localizable );
	}

	@Override
	public void move( int[] distance )
	{
		delegate.move( distance );
	}

	@Override
	public void move( long[] distance )
	{
		delegate.move( distance );
	}

	@Override
	public void setPosition( Localizable localizable )
	{
		delegate.setPosition( localizable );
	}

	@Override
	public void setPosition( int[] position )
	{
		delegate.setPosition( position );
	}

	@Override
	public void setPosition( long[] position )
	{
		delegate.setPosition( position );
	}

	@Override
	public void setPosition( int position, int d )
	{
		delegate.setPosition( position, d );
	}

	@Override
	public void setPosition( long position, int d )
	{
		delegate.setPosition( position, d );
	}

}
