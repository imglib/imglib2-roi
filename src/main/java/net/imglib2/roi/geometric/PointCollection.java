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
public class PointCollection extends AbstractInterval implements IterableRegion< BoolType >
{
	private final Collection< ? extends Localizable > vertices;

	private NearestNeighborSearchOnKDTree< ? extends Localizable > search;

	public PointCollection( final Collection< ? extends Localizable > vertices )
	{
		super( ROIUtils.getBounds( vertices ) );
		this.vertices = vertices;
	}

	@Override
	public Cursor< Void > cursor()
	{
		return new PointCollectionCursor( vertices );
	}

	@Override
	public Cursor< Void > localizingCursor()
	{
		return cursor();
	}

	@Override
	public long size()
	{
		return vertices.size();
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

	public Collection< ? extends Localizable > getVertices()
	{
		return vertices;
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

	private static class PointCollectionCursor extends AbstractCursor< Void >
	{
		private final Collection< ? extends Localizable > collection;

		private Iterator< ? extends Localizable > currentIt;

		private Localizable currentPos;

		private int idx;

		public PointCollectionCursor( final Collection< ? extends Localizable > collection )
		{
			super( collection.iterator().next().numDimensions() );
			this.collection = collection;
			reset();
		}

		private PointCollectionCursor( final Collection< ? extends Localizable > collection, final int idx )
		{
			this( collection );
			jumpFwd( idx );
		}

		@Override
		public Void get()
		{
			return null;
		}

		@Override
		public void fwd()
		{
			++idx;
			currentPos = currentIt.next();
		}

		@Override
		public void reset()
		{
			currentIt = collection.iterator();
			idx = 0;
		}

		@Override
		public boolean hasNext()
		{
			return currentIt.hasNext();
		}

		// FIXME: Should I add null checks for the case where currentPos was not
		// set? If so, shouldn't we set currentPos to something?

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
		public AbstractCursor< Void > copy()
		{
			return new PointCollectionCursor( collection, idx );
		}

		@Override
		public AbstractCursor< Void > copyCursor()
		{
			return copy();
		}
	}

	private synchronized < T extends Localizable > void initSearch( final Collection< T > v )
	{
		if ( search != null )
			return;
		final List< T > vertexList = new ArrayList< T >( v );
		search = new NearestNeighborSearchOnKDTree< T >( new KDTree< T >( vertexList, vertexList ) );
	}

	private class KDTreeContains implements Contains< Localizable >
	{
		private final NearestNeighborSearchOnKDTree< ? extends Localizable > s;

		public KDTreeContains()
		{
			if ( search == null )
				initSearch( vertices );
			s = search.copy();
		}

		@Override
		public int numDimensions()
		{
			return PointCollection.this.numDimensions();
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
