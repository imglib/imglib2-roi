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
package net.imglib2.roi.labeling;

import java.util.Arrays;

import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;

/**
 * A bounding box {@link Interval} around a region that is build by
 * {@link #update(Localizable) aggregating} positions contained in the region.
 *
 * @author Lee Kamentsky
 * @author Tobias Pietzsch
 */
public class BoundingBox extends AbstractInterval
{
	public BoundingBox( final int n )
	{
		super( n );
		Arrays.fill( max, Long.MIN_VALUE );
		Arrays.fill( min, Long.MAX_VALUE );
	}

	/**
	 * update the minimum and maximum extents with the given coordinates.
	 *
	 * @param position
	 */
	public void update( final Localizable position )
	{
		for ( int d = 0; d < min.length; d++ )
		{
			final long p = position.getLongPosition( d );
			if ( p < min[ d ] )
				min[ d ] = p;
			if ( p > max[ d ] )
				max[ d ] = p;
		}
	}

	/**
	 * update the minimum and maximum extents with the given coordinates.
	 *
	 * @param position
	 */
	public void update( final long[] position )
	{
		for ( int d = 0; d < min.length; d++ )
		{
			if ( position[ d ] < min[ d ] )
				min[ d ] = position[ d ];
			if ( position[ d ] > max[ d ] )
				max[ d ] = position[ d ];
		}
	}
}
