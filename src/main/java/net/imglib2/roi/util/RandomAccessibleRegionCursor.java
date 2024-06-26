/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.BooleanType;

@Deprecated
public class RandomAccessibleRegionCursor< T extends BooleanType< T > > extends AbstractWrappedInterval< RandomAccessibleInterval< T > > implements Cursor< Void >
{
	private final RandomAccess< T > randomAccess;

	private final int n;

	private long index;

	private final long maxIndex;

	private long lineIndex;

	private final long maxLineIndex;

	private final boolean empty;

	public RandomAccessibleRegionCursor( final RandomAccessibleInterval< T > interval, final long size )
	{
		super( interval );
		randomAccess = interval.randomAccess();
		n = numDimensions();
		maxLineIndex = dimension( 0 ) - 1;
		maxIndex = size;
		empty = size == 0;
		reset();
	}

	protected RandomAccessibleRegionCursor( final RandomAccessibleRegionCursor< T > cursor )
	{
		super( cursor.sourceInterval );
		this.randomAccess = cursor.randomAccess.copyRandomAccess();
		n = cursor.n;
		lineIndex = cursor.lineIndex;
		maxIndex = cursor.maxIndex;
		maxLineIndex = cursor.maxLineIndex;
		empty = cursor.empty;
	}

	@Override
	public Void get()
	{
		return null;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		for ( long i = 0; i < steps; ++i )
			fwd();
	}

	@Override
	public void fwd()
	{
		if ( empty )
			return;
		do
		{
			randomAccess.fwd( 0 );
			if ( ++lineIndex > maxLineIndex )
				nextLine();
		}
		while ( !randomAccess.get().get() );
		++index;
	}

	private void nextLine()
	{
		lineIndex = 0;
		randomAccess.setPosition( min( 0 ), 0 );
		for ( int d = 1; d < n; ++d )
		{
			randomAccess.fwd( d );
			if ( randomAccess.getLongPosition( d ) > max( d ) )
				randomAccess.setPosition( min( d ), d );
			else
				break;
		}
	}

	@Override
	public void reset()
	{
		index = 0;
		lineIndex = -1;
		min( randomAccess );
		randomAccess.bck( 0 );
	}

	@Override
	public boolean hasNext()
	{
		return index < maxIndex;
	}

	@Override
	public Void next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{}

	@Override
	public RandomAccessibleRegionCursor< T > copy()
	{
		return new RandomAccessibleRegionCursor< T >( this );
	}

	@Override
	public RandomAccessibleRegionCursor< T > copyCursor()
	{
		return copy();
	}

	@Override
	public void localize( final float[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return randomAccess.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return randomAccess.getDoublePosition( d );
	}

	@Override
	public void localize( final int[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public void localize( final long[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return randomAccess.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return randomAccess.getLongPosition( d );
	}
}
