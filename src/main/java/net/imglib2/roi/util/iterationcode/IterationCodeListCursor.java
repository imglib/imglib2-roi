/*
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
package net.imglib2.roi.util.iterationcode;

import java.util.ArrayList;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.roi.labeling.LabelRegion;

import gnu.trove.list.array.TIntArrayList;

/**
 * A {@code Cursor<Void>} that visits all positions in the bitmask encoded by a
 * given list of {@link IterationCode}s. (This is used to iterate a
 * {@link LabelRegion} which is the union of fragments which are encoded by
 * {@link IterationCode}s.)
 * <p>
 * It is constructed with a {@code long[]} offset which is not copied, so it can
 * be used to shift the bitmask and reuse this cursor.
 *
 * @author Tobias Pietzsch
 */
public class IterationCodeListCursor extends AbstractLocalizable implements Cursor< Void >
{
	private final IterationCodeListIterator< Point > iter;

	public IterationCodeListCursor( final ArrayList< TIntArrayList > itcodesList, final long[] offset )
	{
		super( offset.length );
		iter = new IterationCodeListIterator<>( itcodesList, offset, Point.wrap( position ) );
	}

	protected IterationCodeListCursor( final IterationCodeListCursor c )
	{
		super( c.position );
		iter = new IterationCodeListIterator<>( c.iter, Point.wrap( position ) );
	}

	@Override
	public Void get()
	{
		return null;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		iter.jumpFwd( steps );
	}

	@Override
	public void fwd()
	{
		iter.fwd();
	}

	@Override
	public void reset()
	{
		iter.reset();
	}

	@Override
	public boolean hasNext()
	{
		return iter.hasNext();
	}

	@Override
	public Void next()
	{
		fwd();
		return null;
	}

	@Override
	public IterationCodeListCursor copy()
	{
		return new IterationCodeListCursor( this );
	}

	@Override
	public IterationCodeListCursor copyCursor()
	{
		return copy();
	}
}
