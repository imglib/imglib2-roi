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
package net.imglib2.roi.geom;

import static org.junit.Assume.assumeTrue;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.roi.geom.real.DefaultWritableRealPointCollection;
import net.imglib2.roi.geom.real.KDTreeRealPointCollection;
import net.imglib2.roi.geom.real.RealPointCollection;
import net.imglib2.roi.geom.real.RealPointSampleListWritableRealPointCollection;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

public class RealPointCollectionBenchmarkTest
{
	private static final List< RealLocalizable > points = new ArrayList<>();

	private static final RealPoint testPoint = new RealPoint( new double[] { 0, 0 } );

	private static RealPointCollection< RealLocalizable > drpc;

	private static RealPointCollection< RealLocalizable > tree;

	private static RealPointCollection< RealLocalizable > rpsl;

	@Rule
	public TestRule benchmarkRun = new BenchmarkRule();

	@BeforeClass
	public static void setup()
	{
		// comment out assumeTrue to actually run benchmark
		assumeTrue( false );
		points.clear();
		final int seed = -1024;
		final Random rand = new Random( seed );

		for ( int i = 0; i < 1000000; i++ )
		{
			points.add( new RealPoint( new double[] { rand.nextDouble() * 100 - 50, rand.nextDouble() * 100 - 50 } ) );
		}

		drpc = new DefaultWritableRealPointCollection<>( points );
		tree = new KDTreeRealPointCollection<>( points );
		rpsl = new RealPointSampleListWritableRealPointCollection<>( points );
	}

	@BenchmarkOptions( benchmarkRounds = 20, warmupRounds = 2 )
	@Test
	public void testDefaulttest()
	{
		drpc.test( testPoint );
	}

	@BenchmarkOptions( benchmarkRounds = 20, warmupRounds = 2 )
	@Test
	public void testKDTreetest()
	{
		tree.test( testPoint );
	}

	@BenchmarkOptions( benchmarkRounds = 20, warmupRounds = 2 )
	@Test
	public void testRPSLtest()
	{
		rpsl.test( testPoint );
	}
}
