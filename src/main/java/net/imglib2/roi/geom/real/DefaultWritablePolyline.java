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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.imglib2.AbstractRealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.geom.GeomMaths;
import net.imglib2.roi.util.AbstractRealMaskPoint;
import net.imglib2.roi.util.RealLocalizableRealPositionable;

/**
 * A polyline, which can be embedded in n-dimensional space.
 *
 * @author Alison Walter
 */
public class DefaultWritablePolyline extends AbstractRealInterval implements WritablePolyline
{
	private final ArrayList< double[] > vertices;

	/**
	 * Creates a polyline with the specified vertices. The dimensionality of the
	 * space is determined by the dimensionality of the first vertex. If a given
	 * vertex has fewer dimensions then an exception will be thrown. However, if
	 * the given vertex has more dimensions it will be truncated.
	 *
	 * @param vertices
	 *            Vertices which define the polyline in the desired order.
	 */
	public DefaultWritablePolyline( final List< ? extends RealLocalizable > vertices )
	{
		super( GeomMaths.getBoundsReal( vertices ) );
		this.vertices = new ArrayList<>( vertices.size() );

		for ( int i = 0; i < vertices.size(); i++ )
		{
			final double[] p = new double[ n ];
			for ( int d = 0; d < n; d++ )
			{
				p[ d ] = vertices.get( i ).getDoublePosition( d );
			}
			this.vertices.add( p );
		}
	}

	@Override
	public boolean test( final RealLocalizable l )
	{
		for ( int i = 1; i < vertices.size(); i++ )
		{
			final double[] ptOne = vertices.get( i - 1 );
			final double[] ptTwo = vertices.get( i );
			final boolean testLineContains = GeomMaths.lineContains( ptOne, ptTwo, l, n );
			if ( testLineContains )
				return true;
		}
		return false;
	}

	/**
	 * Returns the vertex at the specified position. The vertices are in the
	 * same order as when they were passed to the constructor, unless vertices
	 * have been added/removed.
	 */
	@Override
	public RealLocalizableRealPositionable vertex( final int pos )
	{
		return new PolylineVertex( vertices.get( pos ) );
	}

	@Override
	public int numVertices()
	{
		return vertices.size();
	}

	@Override
	public void addVertex( final int index, final RealLocalizable vertex )
	{
		if ( vertex.numDimensions() < n )
			throw new IllegalArgumentException( "Vertex must have at least" + n + " dimensions" );
		final double[] p = new double[ n ];
		for ( int d = 0; d < n; d++ )
			p[ d ] = vertex.getDoublePosition( d );
		vertices.add( index, p );
		expandMinMax( p, p );
	}

	@Override
	public void removeVertex( final int index )
	{
		vertices.remove( index );
		updateMinMax();
	}

	@Override
	public void addVertices( int index, Collection< RealLocalizable > newVertices )
	{
		// add the vertices
		vertices.addAll( index, newVertices.stream().map( vertex -> {
			if ( vertex.numDimensions() < n )
				throw new IllegalArgumentException( "Vertex must have at least" + n + " dimensions" );
			final double[] p = new double[ n ];
			for ( int d = 0; d < n; d++ )
				p[ d ] = vertex.getDoublePosition( d );
			return p;
		} ).collect( Collectors.toList() ) );

		// expand the bounds
		int offset = index;
		for ( int i = 0; i < newVertices.size(); i++ )
		{
			final double[] vertex = vertices.get( offset++ );
			expandMinMax( vertex, vertex );
		}
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof Polyline ) )
			return false;

		final Polyline p = ( Polyline ) obj;
		if ( numVertices() != p.numVertices() || boundaryType() != p.boundaryType() || n != p.numDimensions() )
			return false;

		for ( int i = 0; i < numVertices(); i++ )
		{
			for ( int d = 0; d < n; d++ )
			{
				if ( vertices.get( i )[ d ] != p.vertex( i ).getDoublePosition( d ) )
					return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int result = 777;

		int t = 11;
		for ( int i = 0; i < numVertices(); i++ )
		{
			for ( int d = 0; d < n; d++ )
				result += t * ( vertices.get( i )[ d ] * vertices.get( i )[ d ] );
			t += 3;
		}

		return result;
	}

	// -- Helper methods --

	private void updateMinMax()
	{
		Arrays.fill( min, Double.POSITIVE_INFINITY );
		Arrays.fill( max, Double.NEGATIVE_INFINITY );
		for ( double[] vertex : vertices ) {
			expandMinMax( vertex, vertex );
		}
	}

	private void expandMinMax( final double[] mn, final double[] mx )
	{
		for ( int d = 0; d < numDimensions(); d++ )
		{
			if ( mx[ d ] > max[ d ] )
				max[ d ] = mx[ d ];
			if ( mn[ d ] < min[ d ] )
				min[ d ] = mn[ d ];
		}
	}

	// -- Helper classes --

	private class PolylineVertex extends AbstractRealMaskPoint
	{
		public PolylineVertex( final double[] pos )
		{
			super( pos );
		}

		@Override
		public void updateBounds()
		{
			updateMinMax();
		}

	}
}
