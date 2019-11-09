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
package net.imglib2.roi;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.util.IterableRandomAccessibleRegion;
import net.imglib2.roi.util.SamplingIterableInterval;
import net.imglib2.type.BooleanType;
import net.imglib2.view.Views;

public class Regions
{

	/**
	 * Given a region and an image, return an {@link IterableInterval}
	 * over the pixels of the image inside the mask.
	 * 
	 * @param region The region that defines which pixels in {@code img} to iterate over.
	 * @param img The source from which to grab the pixels inside the region.
	 * @return An IterableInterval over the samples of img inside the region.
	 */
	public static < T > IterableInterval< T > sample( final IterableInterval< Void > region, final RandomAccessible< T > img )
	{
		return SamplingIterableInterval.create( region, img );
	}
	
	/**
	 * Given a mask and an image, return an {@link IterableInterval}
	 * over the pixels of the image inside the mask.
	 * 
	 * @param mask The mask that defines which pixels in {@code img} to iterate over.
	 * @param img The source from which to grab the pixels inside the mask.
	 * @return An IterableInterval over the samples of img inside the mask.
	 */
	public static < T > IterableInterval< T > sample( final RealMaskRealInterval mask, final RandomAccessible< T > img )
	{
		return Regions.sample( Masks.toIterableRegion( mask ), img );
	}

	/**
	 * Obtains an {@link IterableRegion} whose iteration consists of only the
	 * true pixels of a region (instead of all pixels in bounding box).
	 * 
	 * @param <B>
	 *            The {@link BooleanType} of the region.
	 * @param region
	 *            The region to filter by its true values.
	 * @return An {@link IterableRegion} consisting of true values of the input
	 *         region.
	 */
	public static < B extends BooleanType< B > > IterableRegion< B > iterable( final RandomAccessibleInterval< B > region )
	{
		if ( region instanceof IterableRegion )
			return ( IterableRegion< B > ) region;
		else
			return IterableRandomAccessibleRegion.create( region );
	}

	/**
	 * Counts the number of true pixels in the given region.
	 * 
	 * @param <B>
	 *            The {@link BooleanType} of the region.
	 * @param interval
	 *            The region whose true values should be counted.
	 * @return The number of true values in the region.
	 */
	public static < T extends BooleanType< T > > long countTrue( final RandomAccessibleInterval< T > interval )
	{
		long sum = 0;
		for ( final T t : Views.iterable( interval ) )
			if ( t.get() )
				++sum;
		return sum;
	}
}
