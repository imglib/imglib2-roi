/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.roi.geom.integer;

import java.util.Iterator;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RealPoint;
import net.imglib2.Sampler;

/**
 * An immutable line sampled on integer position.
 * <p>
 * The cursors generated by the {@link IterableInterval} will iterate exactly
 * once over all the integer locations on a line between in proper order from
 * the specified start to the specified end points, included.
 * <p>
 * Two lines are considered equal iff their starting points and ending points
 * are at to the same locations. This is reflected in the
 * {@link #equals(Object)}, {@link #hashCode()} and {@link #iterationOrder()}
 * methods.
 * <p>
 * This implementation uses floating-point logic instead of the pure integer
 * logic of Bresenham line (<a href=
 * "https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm">Wikipedia</a>)
 * but the results are quasi identical and the performance penalty small.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class IterableLine extends AbstractInterval implements IterableInterval< Void >
{

	/**
	 * Number of points this line will be sampled on.
	 */
	private final long nPoints;

	/**
	 * The starting point of the line.
	 */
	private final Localizable start;

	/**
	 * End point of the line.
	 */
	private final Localizable end;

	/**
	 * Instantiates a new line that goes from start to end points.
	 *
	 * @param start
	 *            the location of the start point.
	 * @param end
	 *            the location of the end point.
	 */
	public IterableLine( final Localizable start, final Localizable end )
	{
		super( start.numDimensions() );
		this.start = new Point( start );
		this.end = new Point( end );

		final Point diff = new Point( n );
		long maxDiff = -1;
		for ( int d = 0; d < n; d++ )
		{
			min[ d ] = Math.min( start.getLongPosition( d ), end.getLongPosition( d ) );
			max[ d ] = Math.max( start.getLongPosition( d ), end.getLongPosition( d ) );
			final long dx = end.getLongPosition( d ) - start.getLongPosition( d );
			diff.setPosition( dx, d );
			if ( Math.abs( dx ) > maxDiff )
				maxDiff = Math.abs( dx );
		}
		this.nPoints = maxDiff;
	}

	@Override
	public boolean equals( final Object o )
	{
		if ( o == this ) { return true; }
		if ( !( o instanceof IterableLine ) ) { return false; }
		final IterableLine ol = ( IterableLine ) o;
		return locationsEqual( start, ol.start ) && locationsEqual( end, ol.end );
	}

	@Override
	public int hashCode()
	{
		int hash = 17;
		hash = 31 * hash + hash( start );
		hash = 31 * hash + hash( end );
		return hash;
	}

	@Override
	public long size()
	{
		// Include start and end points in the count.
		return 1 + nPoints;
	}

	@Override
	public Void firstElement()
	{
		return cursor().next();
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
		return localizingCursor();
	}

	@Override
	public Cursor< Void > localizingCursor()
	{
		return new LineCursor();
	}

	private final class LineCursor implements Cursor< Void >
	{

		private final RealPoint increment;

		private final RealPoint current;

		private long index;

		public LineCursor()
		{
			this.current = new RealPoint( n );

			final Point diff = new Point( n );
			long maxDiff = -1;
			for ( int d = 0; d < n; d++ )
			{
				final long dx = end.getLongPosition( d ) - start.getLongPosition( d );
				diff.setPosition( dx, d );
				if ( Math.abs( dx ) > maxDiff )
					maxDiff = Math.abs( dx );
			}

			this.increment = new RealPoint( n );
			if ( maxDiff != 0 )
				for ( int d = 0; d < n; d++ )
					increment.setPosition( diff.getDoublePosition( d ) / maxDiff, d );

			reset();
		}

		public LineCursor( final LineCursor c )
		{
			this();
			index = c.index;
			current.setPosition( c );
		}

		@Override
		public boolean hasNext()
		{
			return index < nPoints;
		}

		@Override
		public Void get()
		{
			return null;
		}

		@Override
		public void fwd()
		{
			index++;
			for ( int d = 0; d < n; d++ )
				current.move( increment.getDoublePosition( d ), d );
		}

		@Override
		public void jumpFwd( final long steps )
		{
			if ( steps < 0 || steps > nPoints )
				throw new IllegalArgumentException( "Cannot jump by " + steps + " points." );

			index += steps;
			for ( int d = 0; d < n; d++ )
				current.move( steps * increment.getDoublePosition( d ), d );
		}

		@Override
		public void reset()
		{
			index = -1;
			current.setPosition( start );
			for ( int d = 0; d < n; d++ )
				current.move( -increment.getDoublePosition( d ), d );
		}

		@Override
		public Void next()
		{
			fwd();
			return null;
		}

		@Override
		public void localize( final long[] position )
		{
			for ( int d = 0; d < n; d++ )
				position[ d ] = getLongPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return Math.round( current.getDoublePosition( d ) );
		}

		@Override
		public void localize( final int[] position )
		{
			for ( int d = 0; d < n; d++ )
				position[ d ] = getIntPosition( d );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return ( int ) getLongPosition( d );
		}

		@Override
		public Sampler< Void > copy()
		{
			return new LineCursor( this );
		}

		@Override
		public Cursor< Void > copyCursor()
		{
			return new LineCursor();
		}

		@Override
		public void localize( final float[] position )
		{
			for ( int d = 0; d < position.length; d++ )
				position[ d ] = getLongPosition( d );
		}

		@Override
		public void localize( final double[] position )
		{
			for ( int d = 0; d < position.length; d++ )
				position[ d ] = getLongPosition( d );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return Math.round( current.getFloatPosition( d ) );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return Math.round( current.getFloatPosition( d ) );
		}

		@Override
		public int numDimensions()
		{
			return current.numDimensions();
		}
	}

	// TODO: Replace by the method in net.imglib2.util.Util when the version is
	// released.
	private static final boolean locationsEqual( final Localizable l1, final Localizable l2 )
	{
		final int numDims = l1.numDimensions();
		if ( l2.numDimensions() != numDims )
			return false;
		for ( int d = 0; d < numDims; d++ )
		{
			if ( l1.getLongPosition( d ) != l2.getLongPosition( d ) )
				return false;
		}
		return true;
	}

	private static final int hash( final Localizable l )
	{
		int hash = 17;
		for ( int d = 0; d < l.numDimensions(); d++ )
			hash = 31 * hash + Long.hashCode( l.getLongPosition( d ) );
		return hash;
	}
}
