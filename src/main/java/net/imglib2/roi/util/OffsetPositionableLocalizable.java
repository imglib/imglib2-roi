/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
 * A {@link Localizable} {@link Positionable} that is the position of another
 * {@link Localizable} {@link Positionable} with an offset.
 *
 * @author Tobias Pietzsch
 */
public class OffsetPositionableLocalizable< P extends Positionable & Localizable >
		extends OffsetLocalizable< P >
		implements Positionable
{
	public OffsetPositionableLocalizable( final P source, final long[] offset )
	{
		super( source, offset );
	}

	@Override
	public void fwd( final int d )
	{
		source.fwd( d );
	}

	@Override
	public void bck( final int d )
	{
		source.bck( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		source.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		source.move( distance, d );
	}

	@Override
	public void move( final Localizable distance )
	{
		source.move( distance );
	}

	@Override
	public void move( final int[] distance )
	{
		source.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		source.move( distance );
	}

	@Override
	public void setPosition( final Localizable position )
	{
		for ( int d = 0; d < n; ++d )
			source.setPosition( position.getLongPosition( d ) - offset[ d ], d );
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			source.setPosition( position[ d ] - offset[ d ], d );
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			source.setPosition( position[ d ] - offset[ d ], d );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		source.setPosition( position - offset[ d ], d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		source.setPosition( position - offset[ d ], d );
	}
}
