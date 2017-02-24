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
package net.imglib2.roi;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.util.AbstractPositionableInterval;
import net.imglib2.roi.util.IterableRandomAccessibleRegion;
import net.imglib2.roi.util.PositionableIntervalRandomAccessible;
import net.imglib2.roi.util.SamplingIterableInterval;
import net.imglib2.roi.util.PositionableIntervalRandomAccessible.PositionableIntervalFactory;
import net.imglib2.roi.util.PositionableIterableInterval;
import net.imglib2.roi.util.PositionableIterationCode;
import net.imglib2.roi.util.ROIUtils;
import net.imglib2.roi.util.iterationcode.IterationCode;
import net.imglib2.roi.util.iterationcode.IterationCodeBuilder;
import net.imglib2.type.BooleanType;
import net.imglib2.view.Views;

public class Regions
{
	// TODO: make Positionable and Localizable
	// TODO: bind to (respectively sample from) RandomAccessible
	// TODO: out-of-bounds / clipping

	public static < T > IterableInterval< T > sample( final IterableInterval< Void > region, final RandomAccessible< T > img )
	{
		return SamplingIterableInterval.create( region, img );
	}

	public static < B extends BooleanType< B > > IterableRegion< B > iterable( final RandomAccessibleInterval< B > region )
	{
		if ( region instanceof IterableRegion )
			return ( IterableRegion< B > ) region;
		else
			return IterableRandomAccessibleRegion.create( region );
	}

	public static < B extends BooleanType< B >, T > RandomAccessible< IterableInterval< T > > sample( final RandomAccessibleInterval< B > region, final RandomAccessible< T > img )
	{
		final IterationCode code = ROIUtils.deriveIterationCode( region );
		return new PositionableIntervalRandomAccessible< T, PositionableIterationCode >( new PositionableIntervalFactory< PositionableIterationCode >()
		{

			@Override
			public PositionableIterationCode create()
			{
				return new PositionableIterationCode( code );
			}

			@Override
			public PositionableIterationCode copy( PositionableIterationCode copy )
			{
				return copy.copy();
			}
		}, img );
	}

	// TODO do we still want to copy it over to an iterationcode? if so, when?
	public static < B extends BooleanType< B >, T > RandomAccessible< IterableInterval< T > > sample( final IterableInterval< Void > region, final RandomAccessible< T > img )
	{
		return new PositionableIntervalRandomAccessible< T, PositionableIterableInterval >( new PositionableIntervalFactory< PositionableIterableInterval >()
		{

			@Override
			public PositionableIterableInterval create()
			{
				return new PositionableIterableInterval( region );
			}

			@Override
			public PositionableIterableInterval copy( PositionableIterableInterval source )
			{
				return source.copy();
			}
		}, img );
	}

	public static < T, P extends Positionable & IterableInterval< Void > > RandomAccessible< IterableInterval< T > > sample( final PositionableIntervalFactory< P > fac, final RandomAccessible< T > img )
	{
		return new PositionableIntervalRandomAccessible< T, P >( fac, img );
	}

}
