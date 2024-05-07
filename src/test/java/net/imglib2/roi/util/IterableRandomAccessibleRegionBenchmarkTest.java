/*
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

import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.Random;

import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.Regions;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.util.IterableRandomAccessibleRegion;
import net.imglib2.type.logic.BitType;

/**
 * Benchmarks for {@link IterableRandomAccessibleRegion} iteration.
 *
 * @author Alison Walter
 * @author Tobias Pietzsch
 */
@Deprecated
public class IterableRandomAccessibleRegionBenchmarkTest
{

	private static ArrayList< IterableRegion< BitType > > regions = new ArrayList<>();

	@Rule
	public TestRule benchmarkRun = new BenchmarkRule();

	@BeforeClass
	public static void setup()
	{
		// comment out assumeTrue to actually run benchmark
		assumeTrue( false );

		final long[] dimensions = { 500, 250, 100 };
		for ( double ratioFalseToTrue = 0.2; ratioFalseToTrue < 1.0; ratioFalseToTrue += 0.2 )
		{
			regions.add( Regions.iterable( createRandomMask( ratioFalseToTrue, dimensions ) ) );
		}
	}

	private static Img< BitType > createRandomMask( final double ratioFalseToTrue, final long... dim )
	{
		final Img< BitType > mask = ArrayImgs.bits( dim );
		final Random rand = new Random( 12 );
		if ( ratioFalseToTrue < 1.0 )
		{
			final Cursor< BitType > c = mask.cursor();
			while ( c.hasNext() )
				c.next().set( rand.nextDouble() > ratioFalseToTrue );
		}
		return mask;
	}

	@BenchmarkOptions( benchmarkRounds = 20, warmupRounds = 20 )
	@Test
	public void testIterating()
	{
		for ( final IterableRegion< BitType > region : regions )
		{
			final Cursor< Void > cursor = region.inside().cursor();
			while ( cursor.hasNext() )
				cursor.fwd();
		}
	}
}
