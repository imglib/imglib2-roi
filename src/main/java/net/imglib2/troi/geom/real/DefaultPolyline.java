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

package net.imglib2.troi.geom.real;

import java.util.List;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.troi.geom.GeomMaths;

/**
 * A polyline, which can be embedded in n-dimensional space.
 *
 * @author Alison Walter
 */
public class DefaultPolyline< T extends RealLocalizable & RealPositionable > extends AbstractEuclideanSpace implements Polyline< T >
{
	private final List< T > vertices;

	/**
	 * Creates a polyline with the specified vertices. The dimensionality of the
	 * space is determined by the dimensionality of the first vertex. This
	 * constructor will check to ensure that all vertices have the same number
	 * of dimensions, and if not an error will be thrown.
	 *
	 * @param vertices
	 *            Vertices which define the polyline in the desired order.
	 */
	public DefaultPolyline( final List< T > vertices )
	{
		super( vertices.get( 0 ).numDimensions() );
		for ( int i = 0; i < vertices.size(); i++ )
			if ( vertices.get( i ).numDimensions() != n )
				throw new IllegalArgumentException( "All vertices must have exactly " + n + " dimensions!" );
		this.vertices = vertices;
	}

	@Override
	public boolean test( final RealLocalizable l )
	{
		final double[] ptOne = new double[ n ];
		final double[] ptTwo = new double[ n ];
		for ( int i = 1; i < vertices.size(); i++ )
		{
			vertices.get( i - 1 ).localize( ptOne );
			vertices.get( i ).localize( ptTwo );
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
	public T vertex( final int pos )
	{
		return vertices.get( pos );
	}

	@Override
	public int numVertices()
	{
		return vertices.size();
	}

	@Override
	public void addVertex( final int index, final T vertex )
	{
		if ( vertex.numDimensions() != n )
			throw new IllegalArgumentException( "Vertex must have " + n + " dimensions" );
		vertices.add( index, vertex );
	}

	@Override
	public void removeVertex( final int index )
	{
		vertices.remove( index );
	}
}
