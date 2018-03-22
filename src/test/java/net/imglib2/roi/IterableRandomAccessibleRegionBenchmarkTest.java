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

import static org.junit.Assume.assumeTrue;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.util.IterableRandomAccessibleRegion;
import net.imglib2.type.logic.BitType;
import net.imglib2.util.ConstantUtils;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * Benchmarks for {@link IterableRandomAccessibleRegion} iteration.
 *
 * @author Alison Walter
 *
 */
public class IterableRandomAccessibleRegionBenchmarkTest
{

	private static IterableRegion< BitType > emptyII;

	private static IterableRegion< BitType > notEmptyII;

	@Rule
	public TestRule benchmarkRun = new BenchmarkRule();

	@BeforeClass
	public static void setup()
	{
		// comment out assumeTrue to actually run benchmark
		assumeTrue( false );

		final int maxOne = 100;
		final int maxTwo = 500;
		final RandomAccessibleInterval< BitType > empty = ConstantUtils.constantRandomAccessibleInterval( new BitType( false ), 2, new FinalInterval( new long[] { 0, 0 }, new long[] { maxOne, maxTwo } ) );

		final Img< BitType > notEmpty = ArrayImgs.bits( maxOne, maxTwo );
		final Random rand = new Random( 12 );
		final Cursor< BitType > c = notEmpty.cursor();
		while ( c.hasNext() )
			c.next().set( rand.nextBoolean() );

		emptyII = Regions.iterable( empty );
		notEmptyII = Regions.iterable( notEmpty );
	}

//	@BenchmarkOptions( benchmarkRounds = 20, warmupRounds = 2 )
//	@Test
//	public void testIteratingEmpty()
//	{
//		final Cursor< Void > c = emptyII.cursor();
//		for ( int i = 0; i < emptyII.dimension( 0 ) * emptyII.dimension( 1 ); i++ )
//			c.fwd();
//	}

	@BenchmarkOptions( benchmarkRounds = 20, warmupRounds = 2 )
	@Test
	public void testIteratingNotEmpty()
	{
		final Cursor< Void > c = notEmptyII.cursor();
		for ( int i = 0; i < notEmptyII.dimension( 0 ) * notEmptyII.dimension( 1 ); i++ )
			c.fwd();
	}
}
