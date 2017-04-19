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

package net.imglib2.roi.geom.real;

import java.util.Arrays;
import java.util.List;

import net.imglib2.AbstractRealInterval;
import net.imglib2.Interval;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.Regions;
import net.imglib2.roi.geom.GeomMaths;
import net.imglib2.util.Intervals;

/**
 * A polyline, which can be embedded in n-dimensional space.
 *
 * @author Alison Walter
 */
public class DefaultPolyline extends AbstractRealInterval implements Polyline
{
	private final List< double[] > vertices;

	/**
	 * Creates a polyline with the specified vertices. The dimensionality of the
	 * space is determined by the dimensionality of the first vertex. Vertices
	 * with more dimensions will be truncated, if less an error is thrown.
	 *
	 * @param vertices
	 *            Vertices which define the polyline in the desired order.
	 */
	public DefaultPolyline( final List< ? extends RealLocalizable > vertices )
	{
		super( Regions.getBoundsReal( vertices ) );
		this.vertices = GeomMaths.createVerticesList( vertices );
	}

	/**
	 * Creates a polyline with the specified vertices. The dimensionality of the
	 * space is determined by the dimensionality of the first vertex. Vertices
	 * with more dimensions will be truncated, if less an error is thrown.
	 *
	 * @param vertices
	 *            Vertices which define the polyline in the desired order.
	 */
	public DefaultPolyline( final double[][] vertices )
	{
		super( Regions.getBoundsReal( vertices ) );
		this.vertices = GeomMaths.createVerticesList( vertices );
	}

	@Override
	public boolean contains( final RealLocalizable l )
	{
		if ( Intervals.contains( this, l ) )
		{
			for ( int i = 1; i < vertices.size(); i++ )
			{
				final boolean testLineContains = GeomMaths.lineContains( vertices.get( i - 1 ), vertices.get( i ), l, n );
				if ( testLineContains )
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns a copy of the vertex at the specified position. The vertices are
	 * in the same order as when they were passed to the constructor.
	 */
	@Override
	public double[] vertex( final int pos )
	{
		return Arrays.copyOf( vertices.get( pos ), n );
	}

	@Override
	public int numVertices()
	{
		return vertices.size();
	}

	@Override
	public void setVertex( final int index, final double[] vertex )
	{
		if ( vertex.length != n )
			throw new IllegalArgumentException( "Vertex must have " + n + " dimensions" );
		vertices.set( index, vertex );
		updateMinMax();
	}

	@Override
	public void addVertex( final int index, final double[] vertex )
	{
		if ( vertex.length != n )
			throw new IllegalArgumentException( "Vertex must have " + n + " dimensions" );
		vertices.add( index, vertex );
		updateMinMax();
	}

	@Override
	public void removeVertex( final int index )
	{
		vertices.remove( index );
		updateMinMax();
	}

	// -- Helper methods --

	/**
	 * Updates the min and max of the {@link Interval}.
	 */
	private void updateMinMax()
	{
		for ( int d = 0; d < n; d++ )
		{
			double maxVal = vertices.get( 0 )[ d ];
			double minVal = vertices.get( 0 )[ d ];
			for ( int i = 1; i < vertices.size(); i++ )
			{
				final double val = vertices.get( i )[ d ];
				if ( val > maxVal )
					maxVal = val;
				if ( val < minVal )
					minVal = val;
			}
			max[ d ] = maxVal;
			min[ d ] = minVal;
		}
	}
}
