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
package net.imglib2.roi.util;

import java.util.Random;
import net.imglib2.Cursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.BooleanArray;
import net.imglib2.roi.Regions;
import net.imglib2.type.logic.NativeBoolType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * Benchmark comparing cursors on IterableRandomAccessibleRegion and IterableRegionOnBooleanRAI.
 *
 * @author Tobias Pietzsch
 */
public class IterableRegionOnBooleanRAIBenchmark
{
	@State( Scope.Benchmark )
	public static class MyState
	{
		IterableRandomAccessibleRegion< NativeBoolType > irOld;
		IterableRegionOnBooleanRAI< NativeBoolType > ir;
		IterableRandomAccessibleRegion< NativeBoolType > irViewOld;
		IterableRegionOnBooleanRAI< NativeBoolType > irView;

		@Setup( Level.Trial )
		public void doSetup()
		{
			final ArrayImg< NativeBoolType, BooleanArray > img = ArrayImgs.booleans( 100, 100, 100 );
			final Random rand = new Random( 12 );
			img.forEach( t -> t.set( rand.nextDouble() < 0.1 ) );

			final long countTrue = Regions.countTrue( img );

			irOld = new IterableRandomAccessibleRegion<>( img, countTrue );
			ir = new IterableRegionOnBooleanRAI<>( img, countTrue );
			irViewOld = new IterableRandomAccessibleRegion<>( Views.interval( img, Intervals.expand( img, -1 ) ), countTrue );
			irView = new IterableRegionOnBooleanRAI<>( Views.interval( img, Intervals.expand( img, -1 ) ), countTrue );
		}
	}

	@Benchmark
	public void testIterableRandomAccessibleRegion( MyState state )
	{
		final Cursor< Void > c = state.irOld.inside().cursor();
		while ( c.hasNext() )
		{
			c.next();
		}
	}

	@Benchmark
	public void testIterableRegionOnBooleanRAI( MyState state )
	{
		final Cursor< Void > c = state.ir.inside().cursor();
		while ( c.hasNext() )
		{
			c.next();
		}
	}

	@Benchmark
	public void testIterableRandomAccessibleRegionForNonIterableView( MyState state )
	{
		final Cursor< Void > c = state.irViewOld.inside().cursor();
		while ( c.hasNext() )
		{
			c.next();
		}
	}

	@Benchmark
	public void testIterableRegionOnBooleanRAIForNonIterableView( MyState state )
	{
		final Cursor< Void > c = state.irView.inside().cursor();
		while ( c.hasNext() )
		{
			c.next();
		}
	}

	public static void main( String[] args ) throws RunnerException
	{
		Options opt = new OptionsBuilder()
				.include( IterableRegionOnBooleanRAIBenchmark.class.getSimpleName() )
				.forks( 1 )
				.warmupIterations( 8 )
				.measurementIterations( 8 )
				.warmupTime( TimeValue.milliseconds( 200 ) )
				.measurementTime( TimeValue.milliseconds( 200 ) )
				.build();
		new Runner( opt ).run();
	}
}
