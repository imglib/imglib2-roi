/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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
package net.imglib2.roi.geometric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.imglib2.AbstractCursor;
import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.KDTree;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RealCursor;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.util.Contains;
import net.imglib2.roi.util.ContainsRandomAccess;
import net.imglib2.roi.util.ROIUtils;
import net.imglib2.type.logic.BoolType;

/**
 * {@link Collection} of {@link Localizable}s as {@link IterableRegion}.
 *
 * @author Tobias Pietzsch
 * @author Christian Dietz, University of Konstanz.
 * @author Daniel Seebacher, University of Konstanz.
 */
public class KDTreePointCollection extends AbstractInterval implements IterableRegion< BoolType >
{
	private final KDTree< ? extends Localizable > kdtree;

	private final NearestNeighborSearchOnKDTree< ? extends Localizable > search;

	public < T extends Localizable > KDTreePointCollection( final Collection< T > vertices )
	{
		super( ROIUtils.getBounds( vertices ) );
		final List< T > vertexList =
			( vertices instanceof List ) ?
					( List< T > ) vertices :	new ArrayList< T >( vertices );
		final KDTree< T > typedKDTree = new KDTree< T >( vertexList, vertexList );
		kdtree = typedKDTree;
		search = new NearestNeighborSearchOnKDTree< T >( typedKDTree );
	}

	@Override
	public Cursor< Void > cursor()
	{
		return new PointCollectionCursor();
	}

	@Override
	public Cursor< Void > localizingCursor()
	{
		return cursor();
	}

	@Override
	public long size()
	{
		return kdtree.size();
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
	public RandomAccess< BoolType > randomAccess()
	{
		return new ContainsRandomAccess( new KDTreeContains() );
	}

	@Override
	public RandomAccess< BoolType > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	private class PointCollectionCursor extends AbstractCursor< Void >
	{
		private Localizable currentPos;

		private final RealCursor< ? extends Localizable > iterator;

		public PointCollectionCursor()
		{
			super( KDTreePointCollection.this.numDimensions() );
			iterator = kdtree.cursor();
		}

		private PointCollectionCursor( final PointCollectionCursor c )
		{
			super( c.numDimensions() );
			currentPos = c.currentPos;
			iterator = c.iterator.copyCursor();
		}

		@Override
		public Void get()
		{
			return null;
		}

		@Override
		public void fwd()
		{
			currentPos = iterator.next();
		}

		@Override
		public void reset()
		{
			iterator.reset();;
		}

		@Override
		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		@Override
		public void localize( final long[] position )
		{
			currentPos.localize( position );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return currentPos.getLongPosition( d );
		}

		@Override
		public PointCollectionCursor copy()
		{
			return new PointCollectionCursor( this );
		}

		@Override
		public PointCollectionCursor copyCursor()
		{
			return copy();
		}
	}
	
	private class KDTreeContains implements Contains< Localizable >
	{
		private final NearestNeighborSearchOnKDTree< ? extends Localizable > s = search.copy();

		@Override
		public int numDimensions()
		{
			return kdtree.numDimensions();
		}

		@Override
		public boolean contains( final Localizable o )
		{
			s.search( o );
			return s.getSquareDistance() < 0.5;
		}

		@Override
		public KDTreeContains copyContains() 
		{
			return new KDTreeContains();
		}
	}
}
