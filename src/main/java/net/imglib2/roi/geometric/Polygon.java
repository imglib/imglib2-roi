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
import java.util.Collections;
import java.util.List;

import net.imglib2.AbstractRealInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.util.Contains;
import net.imglib2.roi.util.ContainsRealRandomAccess;
import net.imglib2.roi.util.IterableRandomAccessibleRegion;
import net.imglib2.roi.util.ROIUtils;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

/**
 * A {@link Polygon} defined by a {@link List} of {@link RealLocalizable}s.
 *
 * @author Tobias Pietzsch
 * @author Daniel Seebacher, University of Konstanz
 * @author Christian Dietz, University of Konstanz
 */
public class Polygon extends AbstractRealInterval implements RealRandomAccessibleRealInterval< BoolType >, Contains< RealLocalizable >
{
	private final List< ? extends RealLocalizable > vertices;

	private final List< ? extends RealLocalizable > unmodifiableVertices;

	public Polygon( final List< ? extends RealLocalizable > vertices )
	{
		super( ROIUtils.getBoundsReal( vertices ) );
		assert( this.n == 2 );
		this.vertices = new ArrayList<>( vertices );
		unmodifiableVertices = Collections.unmodifiableList( this.vertices );
	}

	/**
	 * Return true if the given point is contained inside the boundary. See:
	 * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
	 */
	@Override
	public boolean contains( final RealLocalizable localizable )
	{
		if ( Intervals.contains( this, localizable ) )
		{
			int i;
			int j;
			boolean result = false;
			for ( i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++ )
			{
				final double j1 = vertices.get( j ).getDoublePosition( 1 );
				final double j0 = vertices.get( j ).getDoublePosition( 0 );

				final double i0 = vertices.get( i ).getDoublePosition( 0 );
				final double i1 = vertices.get( i ).getDoublePosition( 1 );

				final double l1 = localizable.getDoublePosition( 1 );
				final double l0 = localizable.getDoublePosition( 0 );

				if ( ( i1 > l1 ) != ( j1 > l1 ) && ( l0 < ( j0 - i0 ) * ( l1 - i1 ) / ( j1 - i1 ) + i0 ) )
				{
					result = !result;
				}
			}

			return result;
		}
		return false;
	}

	@Override
	public RealRandomAccess< BoolType > realRandomAccess()
	{
		return new ContainsRealRandomAccess( this );
	}

	@Override
	public RealRandomAccess< BoolType > realRandomAccess( final RealInterval interval )
	{
		return realRandomAccess();
	}

	/**
	 * Get vertices defining the {@link Polygon}
	 * 
	 * @return {@link List} of {@link RealLocalizable}
	 */
	public List< ? extends RealLocalizable > getVertices()
	{
		return unmodifiableVertices;
	}

	public IterableRandomAccessibleRegion< BoolType > rasterize()
	{
		return new RasterizedPolygon( this );
	}

	@Override
	public Polygon copyContains() 
	{
		return this;
	}
}
