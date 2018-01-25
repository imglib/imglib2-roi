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

import net.imglib2.RealLocalizable;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.geom.GeomMaths;

/**
 * A {@link Polygon2D} which contains no boundary points, and is defined by the
 * provided vertices.
 *
 * <p>
 * This implementation of a polygon does not support creating a single polygon
 * object which is actually multiple polygons. It does support self-intersecting
 * polygons with even-odd winding.
 * </p>
 *
 * @author Alison Walter
 */
public class OpenWritablePolygon2D extends DefaultWritablePolygon2D
{
	public OpenWritablePolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		super( vertices );
	}

	public OpenWritablePolygon2D( final double[] x, final double[] y )
	{
		super( x, y );
	}

	@Override
	public boolean test( final RealLocalizable localizable )
	{
		// check edges, this needs to be done first because pnpoly has
		// unknown edge behavior
		boolean edge = false;
		final double[] pt1 = new double[ 2 ];
		final double[] pt2 = new double[ 2 ];
		for ( int i = 0; i < x.size(); i++ )
		{
			pt1[ 0 ] = x.get( i );
			pt1[ 1 ] = y.get( i );

			if ( i == x.size() - 1 )
			{
				pt2[ 0 ] = x.get( 0 );
				pt2[ 1 ] = y.get( 0 );
			}
			else
			{
				pt2[ 0 ] = x.get( i + 1 );
				pt2[ 1 ] = y.get( i + 1 );
			}

			edge = GeomMaths.lineContains( pt1, pt2, localizable, 2 );

			if ( edge )
				return false;
		}

		// not on edge, check inside
		return GeomMaths.pnpoly( x, y, localizable );
	}

	@Override
	public BoundaryType boundaryType()
	{
		return BoundaryType.OPEN;
	}
}
