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

import java.util.List;

import gnu.trove.list.array.TIntArrayList;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Iterator;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.roi.labeling.LabelRegion;

/**
 * Iterates a bitmask that is encoded by a list of {@link IterationCode}s. (This
 * is used to iterate a {@link LabelRegion} which is the union of fragments
 * which are encoded by {@link IterationCode}s.) It is constructed with a
 * {@link Positionable} {@link Localizable}, that represents the position and is
 * moved around while iterating.
 *
 * @param <P>
 *            type of the position field.
 *
 * @author Tobias Pietzsch
 */
public class IterationCodeListIterator< P extends Positionable & Localizable > extends AbstractEuclideanSpace implements Iterator
{
	private final List< TIntArrayList > itcodesList;

	private final long[] offset;

	private final P position;

	private int itcodesListIndex;

	private TIntArrayList itcode;

	private int itcodeIndex;

	private int itcodeOffsetX;

	private long maxX;

	private boolean hasNextRaster;

	public IterationCodeListIterator( final List< TIntArrayList  > itcodesList, final long[] offset, final P position )
	{
		super( position.numDimensions() );
		this.position = position;
		this.itcodesList = itcodesList;
		this.offset = offset;
		reset();
	}

	public IterationCodeListIterator( final IterationCodeListIterator< ? > copyFrom, final P position )
	{
		super( position.numDimensions() );
		this.itcodesList = copyFrom.itcodesList;
		this.offset = copyFrom.offset;
		this.position = position;
		this.position.setPosition( copyFrom.position );
		this.itcodesListIndex = copyFrom.itcodesListIndex;
		this.itcode = copyFrom.itcode;
		this.itcodeIndex = copyFrom.itcodeIndex;
		this.itcodeOffsetX = copyFrom.itcodeOffsetX;
		this.maxX = copyFrom.maxX;
		this.hasNextRaster = copyFrom.hasNextRaster;
	}

	private boolean probeNextItcode()
	{
		while ( itcodesListIndex < itcodesList.size() )
		{
			if ( itcodesList.get( itcodesListIndex ).isEmpty() )
				++itcodesListIndex;
			else
				return true;
		}
		return false;
	}

	private void initNextItcode()
	{
		itcode = itcodesList.get( itcodesListIndex++ );
		itcodeIndex = 0;
		itcodeOffsetX = itcode.get( itcodeIndex++ );
		for ( int d = 1; d < n; ++d )
			position.setPosition( itcode.get( itcodeIndex++ ) + offset[ d ], d );
		nextRasterStretch();
	}

	private void nextRasterStretch()
	{
		if ( itcodeIndex >= itcode.size() )
			initNextItcode();
		else
		{
			int minItcodeX = itcode.get( itcodeIndex++ );
			if ( minItcodeX < 0 )
			{
				for ( int d = 1; d <= -minItcodeX; ++d )
					position.setPosition( itcode.get( itcodeIndex++ ) + offset[ d ], d );
				minItcodeX = itcode.get( itcodeIndex++ );
			}
			position.setPosition( minItcodeX + itcodeOffsetX + offset[ 0 ], 0 );
			maxX = itcode.get( itcodeIndex++ ) + itcodeOffsetX + offset[ 0 ];
			hasNextRaster = ( itcodeIndex < itcode.size() ) || probeNextItcode();
		}
	}

	@Override
	public void jumpFwd( final long steps )
	{
		for ( long j = 0; j < steps; ++j )
			fwd();
	}

	@Override
	public void fwd()
	{
		position.fwd( 0 );
		if ( position.getLongPosition( 0 ) > maxX )
			nextRasterStretch();
	}

	@Override
	public void reset()
	{
		itcodesListIndex = 0;
		if ( probeNextItcode() )
		{
			initNextItcode();
			position.bck( 0 );
		}
		else
		{
			hasNextRaster = false;
			position.setPosition( 0, 0 );
			maxX = 0;
		}
	}

	@Override
	public boolean hasNext()
	{
		return hasNextRaster || ( position.getLongPosition( 0 ) < maxX );
	}
}
