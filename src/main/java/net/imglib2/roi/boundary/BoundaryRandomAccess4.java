/*
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
package net.imglib2.roi.boundary;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.AbstractConvertedRandomAccess;
import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BoolType;

/**
 * A {@link BoolType} {@link RandomAccess} on a {@link BooleanType} source
 * {@link RandomAccessibleInterval}. It is {@code true} for pixels that are
 * {@code true} in the source and have at least one {@code false} pixel in their
 * 4-neighborhood (or n-dimensional equivalent).
 *
 * @param <T>
 *
 * @author Tobias Pietzsch
 */
public final class BoundaryRandomAccess4< T extends BooleanType< T > > extends AbstractConvertedRandomAccess< T, BoolType >
{
	private final int n;

	private final long[] min;

	private final long[] max;

	private final BoolType type;

	public BoundaryRandomAccess4( final RandomAccessibleInterval< T > sourceInterval )
	{
		super( sourceInterval.randomAccess() );
		n = sourceInterval.numDimensions();
		min = new long[ n ];
		max = new long[ n ];
		sourceInterval.min( min );
		sourceInterval.max( max );
		type = new BoolType();
	}

	private BoundaryRandomAccess4( final BoundaryRandomAccess4< T > ba )
	{
		super( ba.source.copyRandomAccess() );
		this.n = ba.n;
		this.min = ba.min;
		this.max = ba.max;
		this.type = ba.type.copy();
	}

	@Override
	public BoolType get()
	{
		if ( source.get().get() )
		{
			for ( int d = 0; d < n; ++d )
			{
				final long pos = getLongPosition( d );
				if ( pos <= min[ d ] || pos >= max[ d ] )
				{
					type.set( true );
					return type;
				}
			}
			for ( int d = 0; d < n; ++d )
			{
				bck( d );
				if ( !source.get().get() )
				{
					fwd( d );
					type.set( true );
					return type;
				}
				fwd( d );
				fwd( d );
				if ( !source.get().get() )
				{
					bck( d );
					type.set( true );
					return type;
				}
				bck( d );
			}
		}
		type.set( false );
		return type;
	}

	@Override
	public BoundaryRandomAccess4< T > copy()
	{
		return new BoundaryRandomAccess4< T >( this );
	}
}
