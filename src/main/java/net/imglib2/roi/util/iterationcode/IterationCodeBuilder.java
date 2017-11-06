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
package net.imglib2.roi.util.iterationcode;

import java.util.Arrays;

import gnu.trove.list.array.TIntArrayList;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;

/**
 * Create an {@link IterationCode} by {@link #add(Localizable)  accumulating} coordinates.
 *
 * @author Tobias Pietzsch
 */
public class IterationCodeBuilder extends AbstractEuclideanSpace implements IterationCode
{
	private final TIntArrayList itcode;

	private final long itcodeOffsetX;

	private long size;

	protected long[] prev;

	protected long[] curr;

	private final long[] bbmin;

	private final long[] bbmax;

	private boolean startedRasterization;

	private long rasterBegin;

	public IterationCodeBuilder( final int numDimensions, final long minX )
	{
		super( numDimensions );
		itcode = new TIntArrayList();
		itcodeOffsetX = minX;
		size = 0;
		prev = new long[ n ];
		curr = new long[ n ];
		bbmin = new long[ n ];
		bbmax = new long[ n ];
		Arrays.fill( bbmin, Long.MAX_VALUE );
		Arrays.fill( bbmax, Long.MIN_VALUE );
		startedRasterization = false;
		rasterBegin = 0;
	}

	/**
	 * Accumulate the given coordinates. Assumes that the positions come in in
	 * flat iteration order. (It also works otherwise, but the generated
	 * {@link IterationCode} will not be optimal...).
	 */
	public void add( final Localizable pos )
	{
		++size;
		pos.localize( curr );
		if ( startedRasterization )
		{
			for ( int d = n - 1; d >= 0; --d )
			{
				if ( d == 0 )
				{
					if ( curr[ 0 ] != prev[ 0 ] + 1 )
					{
						endRaster( 0 );
						break;
					}
				}
				else if ( curr[ d ] != prev[ d ] )
				{
					endRaster( d );
					break;
				}
			}
			for ( int d = 0; d < n; d++ )
			{
				if ( curr[ d ] < bbmin[ d ] )
					bbmin[ d ] = curr[ d ];
				else if ( curr[ d ] > bbmax[ d ] )
					bbmax[ d ] = curr[ d ];
			}
		}
		else
		{
			itcode.add( ( int ) itcodeOffsetX );
			for ( int d = 1; d < n; ++d )
				itcode.add( ( int ) curr[ d ] );
			rasterBegin = curr[ 0 ];
			System.arraycopy( curr, 0, bbmin, 0, n );
			System.arraycopy( curr, 0, bbmax, 0, n );
			startedRasterization = true;
		}
		// swap prev and curr pos arrays
		final long[] tmp = prev;
		prev = curr;
		curr = tmp;
	}

	private void endRaster( final int badDimension )
	{
		itcode.add( ( int ) ( rasterBegin - itcodeOffsetX ) );
		itcode.add( ( int ) ( prev[ 0 ] - itcodeOffsetX ) );
		if ( badDimension > 0 )
		{
			itcode.add( -badDimension );
			for ( int d = 1; d <= badDimension; ++d )
				itcode.add( ( int )curr[ d ] );
		}
		rasterBegin = curr[ 0 ];
	}

	public void finish()
	{
		if ( startedRasterization )
		{
			itcode.add( ( int ) ( rasterBegin - itcodeOffsetX ) );
			itcode.add( ( int ) ( prev[ 0 ] - itcodeOffsetX ) );
		}
		itcode.trimToSize();
		prev = null;
		curr = null;
	}

	@Override
	public TIntArrayList getItcode()
	{
		return itcode;
	}

	@Override
	public long getSize()
	{
		return size;
	}

	@Override
	public long[] getBoundingBoxMin()
	{
		return bbmin;
	}

	@Override
	public long[] getBoundingBoxMax()
	{
		return bbmax;
	}
}
