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
package net.imglib2.roi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.labeling.LabelingMapping;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.BooleanType;
import net.imglib2.view.Views;

public class ROIUtils
{
	public static < T extends BooleanType< T > > long countTrue( final RandomAccessibleInterval< T > interval )
	{
		long sum = 0;
		for ( final T t : Views.iterable( interval ) )
			if ( t.get() )
				++sum;
		return sum;
	}

	public static < T > LabelingMapping< T > getLabelingMapping( final RandomAccessibleInterval< LabelingType< T > > labeling )
	{
		return Views.iterable( labeling ).firstElement().getMapping();
	}

	public static Interval getBounds( final Collection< ? extends Localizable > vertices )
	{
		assert( vertices.size() != 0 );

		final int numDims = vertices.iterator().next().numDimensions();
		final long[] min = new long[ numDims ];
		Arrays.fill( min, Long.MAX_VALUE );

		final long[] max = new long[ numDims ];
		Arrays.fill( max, Long.MIN_VALUE );

		for ( final Localizable l : vertices )
		{
			for ( int d = 0; d < numDims; d++ )
			{
				long pos = l.getLongPosition( d );
				if ( pos < min[ d ] )
					min[ d ] = pos;
				else if ( pos > max[ d ] )
					max[ d ] = pos;
			}
		}

		return new FinalInterval( min, max );
	}

	public static RealInterval getBoundsReal( final Collection< ? extends RealLocalizable > vertices )
	{
		assert( vertices.size() != 0 );

		final int numDims = vertices.iterator().next().numDimensions();

		final double[] min = new double[ numDims ];
		Arrays.fill( min, Double.POSITIVE_INFINITY );

		final double[] max = new double[ numDims ];
		Arrays.fill( max, Double.NEGATIVE_INFINITY );

		for ( final RealLocalizable l : vertices )
		{
			for ( int d = 0; d < numDims; d++ )
			{
				double pos = l.getDoublePosition( d );
				if ( pos < min[ d ] )
					min[ d ] = pos;
				else if ( pos > max[ d ] )
					max[ d ] = pos;
			}
		}

		return new FinalRealInterval( min, max );
	}

	public static List< Localizable > bresenham( final List< ? extends RealLocalizable > vertices )
	{

		assert( vertices.size() > 1 );
		assert( vertices.iterator().next().numDimensions() == 2 );

		final ArrayList< Localizable > tmp = new ArrayList< Localizable >();
		for ( int i = 0; i < vertices.size(); i++ )
		{

			long x0 = Math.round( vertices.get( i ).getDoublePosition( 0 ) );
			long y0 = Math.round( vertices.get( i ).getDoublePosition( 1 ) );
			final long x1 = Math.round( vertices.get( ( i + 1 ) % vertices.size() ).getDoublePosition( 0 ) );
			final long y1 = Math.round( vertices.get( ( i + 1 ) % vertices.size() ).getDoublePosition( 1 ) );

			final long dx = Math.abs( x1 - x0 ), sx = x0 < x1 ? 1 : -1;
			final long dy = -Math.abs( y1 - y0 ), sy = y0 < y1 ? 1 : -1;

			long err = dx + dy, e2; /* error value e_xy */

			while ( true )
			{
				tmp.add( new Point( x0, y0 ) );
				if ( x0 == x1 && y0 == y1 )
					break;
				e2 = 2 * err;
				if ( e2 > dy )
				{
					err += dy;
					x0 += sx;
				} /* e_xy+e_x > 0 */
				if ( e2 < dx )
				{
					err += dx;
					y0 += sy;
				} /* e_xy+e_y < 0 */
			}

			// remove last point, because the last point is the identical to the
			// first point of the next edge
			tmp.remove( tmp.size() - 1 );
		}

		return tmp;
	}
}
