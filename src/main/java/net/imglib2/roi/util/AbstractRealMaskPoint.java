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
package net.imglib2.roi.util;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;

public abstract class AbstractRealMaskPoint extends RealPoint implements RealLocalizableRealPositionable
{
	public AbstractRealMaskPoint( final int n )
	{
		super( n );
	}

	public AbstractRealMaskPoint( final double[] pos )
	{
		super( pos, false );
	}

	public AbstractRealMaskPoint( final RealLocalizable pos )
	{
		super( pos );
	}

	public abstract void updateBounds();

	@Override
	public void move( final float distance, final int d )
	{
		super.move( distance, d );
		updateBounds();
	}

	@Override
	public void move( final double distance, final int d )
	{
		super.move( distance, d );
		updateBounds();
	}

	@Override
	public void move( final RealLocalizable distance )
	{
		super.move( distance );
		updateBounds();
	}

	@Override
	public void move( final float[] distance )
	{
		super.move( distance );
		updateBounds();
	}

	@Override
	public void move( final double[] distance )
	{
		super.move( distance );
		updateBounds();
	}

	@Override
	public void setPosition( final RealLocalizable position )
	{
		super.setPosition( position );
		updateBounds();
	}

	@Override
	public void setPosition( final float[] position )
	{
		super.setPosition( position );
		updateBounds();
	}

	@Override
	public void setPosition( final double[] position )
	{
		super.setPosition( position );
		updateBounds();
	}

	@Override
	public void setPosition( final float position, final int d )
	{
		super.setPosition( position, d );
		updateBounds();
	}

	@Override
	public void setPosition( final double position, final int d )
	{
		super.setPosition( position, d );
		updateBounds();
	}

	@Override
	public void fwd( final int d )
	{
		super.fwd( d );
		updateBounds();
	}

	@Override
	public void bck( final int d )
	{
		super.bck( d );
		updateBounds();
	}

	@Override
	public void move( final int distance, final int d )
	{
		super.move( distance, d );
		updateBounds();
	}

	@Override
	public void move( final long distance, final int d )
	{
		super.move( distance, d );
		updateBounds();
	}

	@Override
	public void move( final Localizable localizable )
	{
		super.move( localizable );
		updateBounds();
	}

	@Override
	public void move( final int[] distance )
	{
		super.move( distance );
		updateBounds();
	}

	@Override
	public void move( final long[] distance )
	{
		super.move( distance );
		updateBounds();
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		super.setPosition( localizable );
		updateBounds();
	}

	@Override
	public void setPosition( final int[] position )
	{
		super.setPosition( position );
		updateBounds();
	}

	@Override
	public void setPosition( final long[] position )
	{
		super.setPosition( position );
		updateBounds();
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		super.setPosition( position, d );
		updateBounds();
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		super.setPosition( position, d );
		updateBounds();
	}
}
