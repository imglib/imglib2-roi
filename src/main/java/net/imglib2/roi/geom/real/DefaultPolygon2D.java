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
import net.imglib2.roi.Regions;
import net.imglib2.util.Intervals;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * A {@link DefaultPolygon2D} defined by the given vertices x and y coordinates.
 *
 * @author Tobias Pietzsch
 * @author Daniel Seebacher, University of Konstanz
 * @author Christian Dietz, University of Konstanz
 */
public class DefaultPolygon2D extends AbstractRealInterval
{
	private final TDoubleArrayList x;

	private final TDoubleArrayList y;

	public DefaultPolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		super( Regions.getBoundsReal( vertices ) );

		x = new TDoubleArrayList( vertices.size() );
		y = new TDoubleArrayList( vertices.size() );

		populateXY( vertices );
	}

	public DefaultPolygon2D( final double[] x, final double[] y )
	{
		super( Regions.getBoundsReal( x, y ) );

		this.x = new TDoubleArrayList( x );
		this.y = new TDoubleArrayList( y );
	}

	/**
	 * Return true if the given point is contained inside the boundary. See:
	 * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
	 */
	public boolean contains( final RealLocalizable localizable )
	{
		final double xl = localizable.getDoublePosition( 0 );
		final double yl = localizable.getDoublePosition( 1 );

		if ( Intervals.contains( this, localizable ) )
		{
			int i;
			int j;
			boolean result = false;
			for ( i = 0, j = x.size() - 1; i < y.size(); j = i++ )
			{
				final double xj = x.get( j );
				final double yj = y.get( j );

				final double xi = x.get( i );
				final double yi = y.get( i );

				if ( ( yi > yl ) != ( yj > yl ) && ( xl < ( xj - xi ) * ( yl - yi ) / ( yj - yi ) + xi ) )
				{
					result = !result;
				}
			}
			return result;
		}
		return false;
	}

	/**
	 * Get the vertices defining the {@link DefaultPolygon2D}.
	 */
	public double[][] getVertices()
	{
		final double[][] vert = new double[ x.size() ][ 2 ];

		for ( int i = 0; i < x.size(); i++ )
		{
			vert[ i ][ 0 ] = x.get( i );
			vert[ i ][ 1 ] = y.get( i );
		}

		return vert;
	}

	// -- Helper methods --

	private void populateXY( final List< ? extends RealLocalizable > vertices )
	{
		for ( int i = 0; i < vertices.size(); i++ )
		{
			final RealLocalizable r = vertices.get( i );
			x.add( r.getDoublePosition( 0 ) );
			y.add( r.getDoublePosition( 1 ) );
		}
	}
}
