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
package net.imglib2.roi.geom.real;

import java.util.List;

import net.imglib2.AbstractRealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.geom.GeomMaths;
import net.imglib2.roi.util.AbstractRealMaskPoint;
import net.imglib2.roi.util.RealLocalizableRealPositionable;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * A {@link DefaultWritablePolygon2D} defined by the given vertices. The resulting
 * polygon may contain some edge points, all edge points, or no edge points
 * depending on the specified shape.
 *
 * <p>
 * If consistent edge point inclusion/exclusion is needed see
 * {@link OpenWritablePolygon2D} or {@link ClosedWritablePolygon2D}. These implementations will
 * be less efficient but have consistent edge behavior.
 * </p>
 *
 * @author Tobias Pietzsch
 * @author Daniel Seebacher, University of Konstanz
 * @author Christian Dietz, University of Konstanz
 */
public class DefaultWritablePolygon2D extends AbstractRealInterval implements WritablePolygon2D
{
	protected final TDoubleArrayList x;

	protected final TDoubleArrayList y;

	/**
	 * Creates a 2D polygon with the provided vertices.
	 *
	 * @param vertices
	 *            List of vertices which will be copied. Each vertex should have
	 *            a position in 2D space, positions beyond 2D will be ignored.
	 */
	public DefaultWritablePolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		// Regions.getBoundsReal(...) could create an interval with n > 2, if
		// the first vertex had n > 2. Instead create 2D interval, and then set
		// min/max.
		super( 2 );

		x = new TDoubleArrayList( vertices.size() );
		y = new TDoubleArrayList( vertices.size() );

		populateXY( vertices );
	}

	/**
	 * Creates a 2D polygon with vertices at the provided x, y coordinates. If
	 * the x and y arrays have unequal lengths, the longer array will be
	 * truncated.
	 *
	 * @param x
	 *            X coordinates of the vertices which will be copied
	 * @param y
	 *            Y coordinates of the vertices which will be copied
	 */
	public DefaultWritablePolygon2D( final double[] x, final double[] y )
	{
		super( GeomMaths.getBoundsReal( x, y ) );
		if ( x.length == y.length )
		{
			this.x = new TDoubleArrayList( x );
			this.y = new TDoubleArrayList( y );
		}
		else
		{
			final int l = x.length < y.length ? x.length : y.length;
			this.x = new TDoubleArrayList( l );
			this.x.add( x, 0, l );
			this.y = new TDoubleArrayList( l );
			this.y.add( y, 0, l );
		}
	}

	/**
	 * Return true if the given point is contained inside the boundary. See:
	 * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
	 */
	@Override
	public boolean test( final RealLocalizable localizable )
	{
		return GeomMaths.pnpoly( x, y, localizable );
	}

	/** Return a copy of the vertex */
	@Override
	public RealLocalizableRealPositionable vertex( final int pos )
	{
		return new Polygon2DVertex( pos );
	}

	@Override
	public int numVertices()
	{
		return x.size();
	}

	/**
	 * If the given vertex has more than 2 dimensions, the higher dimensions
	 * will be ignored.
	 */
	@Override
	public void addVertex( final int index, final RealLocalizable vertex )
	{
		final double px = vertex.getDoublePosition( 0 );
		final double py = vertex.getDoublePosition( 1 );
		x.insert( index, px );
		y.insert( index, py );
		expandMinMax(px, py, px, py);
	}

	@Override
	public void removeVertex( final int index )
	{
		x.removeAt( index );
		y.removeAt( index );
		updateMinMax();
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof Polygon2D ) )
			return false;

		final Polygon2D p = ( Polygon2D ) obj;
		if ( numVertices() != p.numVertices() || boundaryType() != p.boundaryType() )
			return false;

		for ( int i = 0; i < numVertices(); i++ )
		{
			if ( x.get( i ) != p.vertex( i ).getDoublePosition( 0 ) || y.get( i ) != p.vertex( i ).getDoublePosition( 1 ) )
				return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = 203;
		int t = 5;
		for ( int i = 0; i < numVertices(); i++ )
		{
			result += ( x.get( i ) * x.get( i ) ) + ( y.get( i ) * t );
			t += 7;
		}

		if ( BoundaryType.CLOSED == boundaryType() )
			result += 21;
		else if ( BoundaryType.OPEN == boundaryType() )
			result += 61;
		else
			result += 3;

		return result;
	}

	// -- Helper methods --

	/**
	 * Populates the x and y arrays, and sets min/max values.
	 *
	 * @param vertices
	 *            Contains the vertices, dimensions beyond two will be ignored.
	 */
	private void populateXY( final List< ? extends RealLocalizable > vertices )
	{
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for ( int i = 0; i < vertices.size(); i++ )
		{
			final double xi = vertices.get( i ).getDoublePosition( 0 );
			final double yi = vertices.get( i ).getDoublePosition( 1 );
			x.add( xi );
			y.add( yi );
			if ( xi > maxX )
				maxX = xi;
			if ( xi < minX )
				minX = xi;
			if ( yi > maxY )
				maxY = yi;
			if ( yi < minY )
				minY = yi;
		}
		max[ 0 ] = maxX;
		max[ 1 ] = maxY;
		min[ 0 ] = minX;
		min[ 1 ] = minY;
	}

	private void updateMinMax()
	{
		min[ 0 ] = min[ 1 ] = Double.POSITIVE_INFINITY;
		max[ 0 ] = max[ 1 ] = Double.NEGATIVE_INFINITY;
		for ( int i = 0; i < numVertices(); i++ )
		{
			final double px = x.get( i );
			final double py = y.get( i );
			expandMinMax( px, py, px, py );
		}
	}

	private void expandMinMax( final double xMin, final double yMin, final double xMax, final double yMax )
	{
		if ( xMax > max[ 0 ] )
			max[ 0 ] = xMax;
		if ( yMax > max[ 1 ] )
			max[ 1 ] = yMax;
		if ( xMin < min[ 0 ] )
			min[ 0 ] = xMin;
		if ( yMin < min[ 1 ] )
			min[ 1 ] = yMin;
	}

	// -- Helper classes --

	private class Polygon2DVertex extends AbstractRealMaskPoint
	{
		private final int pos;

		public Polygon2DVertex( final int pos )
		{
			super( new double[] { x.get( pos ), y.get( pos ) } );
			this.pos = pos;
		}

		@Override
		public void updateBounds()
		{
			x.set( pos, position[ 0 ] );
			y.set( pos, position[ 1 ] );

			updateMinMax();
		}
	}
}
