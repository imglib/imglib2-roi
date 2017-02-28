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

import java.util.Iterator;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.Sampler;
import net.imglib2.roi.PositionableIterableInterval;
import net.imglib2.roi.util.iterationcode.IterationCode;
import net.imglib2.roi.util.iterationcode.IterationCodeIterator;

/**
 * {@link IterationCode} which can start at arbitrary positions.
 *
 * @author Christian Dietz
 *
 */
// TODO: rename!!!
// TODO: add Unsafe version?
public class PositionableIterationCode
		extends AbstractPositionableInterval
		implements PositionableIterableInterval< Void, PositionableIterationCode >
{
	private final IterationCode code;

	public PositionableIterationCode( final IterationCode source )
	{
		super( new FinalInterval( source.getBoundingBoxMin(), source.getBoundingBoxMax() ) );
		this.code = source;
	}

	private PositionableIterationCode( final PositionableIterationCode other )
	{
		super( other );
		this.code = other.code;
	}

	@Override
	public PositionableIterationCode copy()
	{
		return new PositionableIterationCode( this );
	}

	@Override
	public long size()
	{
		return code.getSize();
	}

	@Override
	public Void firstElement()
	{
		return null;
	}

	@Override
	public Object iterationOrder()
	{
		return this;
	}

	@Override
	public Iterator< Void > iterator()
	{
		return cursor();
	}

	@Override
	public Cursor< Void > cursor()
	{
		return new PositionableIterableIntervalCursor();
	}

	@Override
	public Cursor< Void > localizingCursor()
	{
		return cursor();
	}

	class PositionableIterableIntervalCursor extends AbstractLocalizable implements Cursor< Void >
	{
		private final IterationCodeIterator< Point > iterator;

		public PositionableIterableIntervalCursor()
		{
			super(code.numDimensions());
			this.iterator = new IterationCodeIterator<>( code, currentOffset, Point.wrap( position ) );
		}

		private PositionableIterableIntervalCursor( final PositionableIterableIntervalCursor other )
		{
			super( other.position );
			this.iterator = new IterationCodeIterator<>( other.iterator, Point.wrap( position ) );
		}

		@Override
		public Void get()
		{
			return null;
		}

		@Override
		public Sampler< Void > copy()
		{
			return copyCursor();
		}

		@Override
		public void jumpFwd( final long steps )
		{
			iterator.jumpFwd( steps );
		}

		@Override
		public void fwd()
		{
			iterator.fwd();
		}

		@Override
		public void reset()
		{
			iterator.reset();
		}

		@Override
		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		@Override
		public Void next()
		{
			iterator.fwd();
			return null;
		}

		@Override
		public Cursor< Void > copyCursor()
		{
			return new PositionableIterableIntervalCursor( this );
		}
	}
}
