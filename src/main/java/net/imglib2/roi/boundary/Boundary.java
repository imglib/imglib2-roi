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

import static net.imglib2.roi.boundary.Boundary.StructuringElement.FOUR_CONNECTED;

import gnu.trove.list.array.TIntArrayList;
import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.Regions;
import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BoolType;

/**
 * An {@link IterableRegion} of the boundary pixels of a source boolean
 * {@link RandomAccessibleInterval}. A pixel is a boundary pixel if
 * <ol>
 * <li>the corresponding source pixel is {@code true} (in the source region),
 * and
 * <li>at least one of its neighbors is {@code false} (not in the source
 * region).
 * </ol>
 * Neighbors are defined by 4-neighborhood or 8-neighborhood (or n-dimensional
 * equivalent) according to the {@link StructuringElement} given in the
 * constructor. Note, that a {@link Boundary} constructed with 4-neighborhood
 * {@link StructuringElement} is 8-connected, and vice versa.
 *
 * @param <T>
 *            BooleanType of the source {@link IterableRegion}.
 *
 * @author Tobias Pietzsch
 */
public final class Boundary< T extends BooleanType< T > >
	extends AbstractWrappedInterval< RandomAccessibleInterval< T > > implements IterableRegion< BoolType >
{
	public static enum StructuringElement
	{
		FOUR_CONNECTED,
		EIGHT_CONNECTED
	}

	private StructuringElement structuringElement;

	private final int n;

	private final TIntArrayList coords;

	private final int size;

	public Boundary( final RandomAccessibleInterval< T > region )
	{
		this( region, FOUR_CONNECTED );
	}

	public Boundary( final RandomAccessibleInterval< T > region, final StructuringElement structuringElement )
	{
		super( region );
		this.structuringElement = structuringElement;
		n = region.numDimensions();
		coords = new TIntArrayList();
		final BoundaryConstructor< T > c = new BoundaryConstructor< T >( region, structuringElement );
		while( true )
		{
			c.fwd();
			if ( c.isValid() )
			{
				for ( int d = 0; d < n; ++d )
					coords.add( c.getIntPosition( d ) );
			}
			else
				break;
		}
		size = coords.size() / n;
	}

	@Override
	public long size()
	{
		return size;
	}

	@Override
	public Object iterationOrder()
	{
		return this;
	}

	@Override
	public BoundaryCursor cursor()
	{
		return new BoundaryCursor();
	}

	@Override
	public BoundaryCursor localizingCursor()
	{
		return cursor();
	}

	@Override
	public BoundaryCursor iterator()
	{
		return cursor();
	}

	@Override
	public Void firstElement()
	{
		return cursor().next();
	}

	@Override
	public RandomAccess< BoolType > randomAccess()
	{
		return structuringElement == FOUR_CONNECTED
				? new BoundaryRandomAccess4< T >( sourceInterval )
				: new BoundaryRandomAccess8< T >( sourceInterval );
	}

	@Override
	public RandomAccess< BoolType > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	final class BoundaryCursor extends Point implements Cursor< Void >
	{
		private int i;

		private final int size = coords.size();

		BoundaryCursor()
		{
			super( Boundary.this.numDimensions() );
			i = 0;
		}

		private BoundaryCursor( final BoundaryCursor c )
		{
			i = c.i;
			setPosition( c );
		}

		@Override
		public Void get()
		{
			return null;
		}

		@Override
		public Void next()
		{
			fwd();
			return null;
		}

		@Override
		public boolean hasNext()
		{
			return i < size;
		}

		@Override
		public void fwd()
		{
			for ( int d = 0; d < n; ++d, ++i )
				position[ d ] = coords.getQuick( i );
		}

		@Override
		public void jumpFwd( final long steps )
		{
			i += n * ( steps - 1 );
			fwd();
		}

		@Override
		public void reset()
		{
			i = 0;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public BoundaryCursor copy()
		{
			return new BoundaryCursor( this );
		}

		@Override
		public BoundaryCursor copyCursor()
		{
			return copy();
		}
	}

	static final class BoundaryConstructor< T extends BooleanType< T > > implements Localizable
	{
		private final Cursor< Void > c;

		private final RandomAccess< BoolType > a;

		private boolean valid;

		public BoundaryConstructor( final RandomAccessibleInterval< T > region, final StructuringElement structuringElement )
		{
			c = Regions.iterable( region ).localizingCursor();
			a = structuringElement == FOUR_CONNECTED
					? new BoundaryRandomAccess4< T >( region )
					: new BoundaryRandomAccess8< T >( region );
		}

		public void fwd()
		{
			while ( c.hasNext() )
			{
				c.fwd();
				a.setPosition( c );
				if ( a.get().get() )
				{
					valid = true;
					return;
				}
			}
			valid = false;
		}

		public boolean isValid()
		{
			return valid;
		}

		@Override
		public void localize( final float[] position )
		{
			c.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			c.localize( position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return c.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return c.getDoublePosition( d );
		}

		@Override
		public int numDimensions()
		{
			return c.numDimensions();
		}

		@Override
		public void localize( final int[] position )
		{
			c.localize( position );
		}

		@Override
		public void localize( final long[] position )
		{
			c.localize( position );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return c.getIntPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return c.getLongPosition( d );
		}
	}
}
