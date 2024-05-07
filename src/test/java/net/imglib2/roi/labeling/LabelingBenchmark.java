/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.MaskInterval;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.StopWatch;
import net.imglib2.view.Views;

/**
 * Draws 100,000 spheres in a ImgLabeling of size 500 * 500 * 500. Measures
 * performance & memory usage.
 *
 * @author Matthias Arzt
 */
public class LabelingBenchmark
{

	public static void main( final String... args )
	{
		final Img< IntType > image = ArrayImgs.ints( 500, 500, 500 );
		final ImgLabeling< Integer, ? > labeling = new ImgLabeling<>( image );
		final RandomSphere shape = new RandomSphere();
		final int count = 100000;
		final StopWatch watch = StopWatch.createAndStart();
		for ( int j = 0; j < 10; j++ )
		{
			final StopWatch subwatch = StopWatch.createAndStart();
			for ( int i = count / 10 * j; i < count / 10 * ( j + 1 ); i++ )
			{
				shape.randomize();
				draw( labeling, shape, i );
			}
			System.out.println( subwatch );
		}
		System.out.println( watch );
		System.out.println( ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / 1024 / 1024 + " MB" );
		System.gc();
		System.gc();
		System.out.println( ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / 1024 / 1024 + " MB" );
		System.out.println( "Number of label sets: " + labeling.getMapping().getLabelSets().size() );
	}

	private static < T > void draw( final ImgLabeling< T, ? > image,
			final MaskInterval shape, final T b )
	{
		final Cursor< LabelingType< T > > cursor =
				Views.interval( image, shape ).cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			if ( shape.test( cursor ) )
				cursor.get().add( b );
		}
	}

	static class RandomSphere implements MaskInterval
	{

		private final Point center = new Point( 3 );

		private long r, r2;

		private final Random random = new Random( 1 );

		RandomSphere()
		{
			randomize();
		}

		public void randomize()
		{
			center.setPosition( random.nextInt( 500 ), 0 );
			center.setPosition( random.nextInt( 500 ), 1 );
			center.setPosition( random.nextInt( 500 ), 2 );
			r = random.nextInt( 5 ) + 7;
			r2 = r * r;
		}

		@Override
		public long min( final int d )
		{
			return Math.max( 0, center.getLongPosition( d ) - 10 );
		}

		@Override
		public long max( final int d )
		{
			return Math.min( 499, center.getLongPosition( d ) + 10 );
		}

		@Override
		public boolean test( final Localizable localizable )
		{
			final long x = localizable.getLongPosition( 0 ) - center.getLongPosition( 0 );
			final long y = localizable.getLongPosition( 1 ) - center.getLongPosition( 1 );
			final long z = localizable.getLongPosition( 2 ) - center.getLongPosition( 2 );
			return 0 > x * x + y * y + z * z - r2;
		}

		@Override
		public int numDimensions()
		{
			return 3;
		}
	}
}
