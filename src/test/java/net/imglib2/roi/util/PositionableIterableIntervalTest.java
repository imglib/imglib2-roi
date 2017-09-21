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

package net.imglib2.roi.util;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.roi.PositionableIterableInterval;
import net.imglib2.roi.PositionableIterableRegion;
import net.imglib2.roi.Regions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class PositionableIterableIntervalTest
{
	private static final long SEED = 42l;

	private PositionableIterableInterval< Void > code;

	private IntervalView< FloatType > target;

	private PositionableIterableRegion< BitType > region;

	@Before
	public void testPositionables()
	{
		final ArrayImg< BitType, ? > bits = ArrayImgs.bits( new long[] { 3, 3 } );

		final Random r = new Random( SEED );
		for ( final BitType bit : bits )
		{
			bit.set( r.nextBoolean() );
		}

		region = Regions.positionable( bits );
		code = new PositionableIterationCode( ROIUtils.iterationCode( bits ) );

		final Img< FloatType > rnd = creatRandomImg( 10, 10 );
		target = Views.interval( Views.extendBorder( rnd ), rnd );
	}

	@Test
	public void testSafety()
	{
//		final RandomAccess< IterableInterval< FloatType > > ra = create( region, target, true ).randomAccess();
		final RandomAccess< IterableInterval< FloatType > > ra = create( code, target, true ).randomAccess();
		ra.setPosition( new long[] { 5, 5 } );
		final Cursor< FloatType > first = ra.get().cursor();
		first.fwd();
		final float curr = first.next().get();

		final Cursor< FloatType > second = ra.get().cursor();
		second.reset();

		assert ( curr == first.get().get() );
	}

	@Test
	public void raConsistencyTests()
	{

		final RandomAccess< IterableInterval< FloatType > > codeRA = create( code, target, true ).randomAccess();
		final RandomAccess< IterableInterval< FloatType > > regionRA = create( region, target, true ).randomAccess();

		codeRA.setPosition( new long[] { 52, 52 } );
		regionRA.setPosition( codeRA );

		testSum( codeRA.get().cursor(), regionRA.get().cursor() );

	}

	@Test
	public void safeUnsafeConsistencyTest()
	{
		final Cursor< IterableInterval< FloatType > > cc = create( code, target, true ).cursor();
		final Cursor< IterableInterval< FloatType > > rc = create( code, target, false ).cursor();

		while ( cc.hasNext() && rc.hasNext() )
		{
			testSum( cc.next().cursor(), rc.next().cursor() );
		}

	}

	@Test
	public void regionNonRegionConsistencyTest()
	{
		final Cursor< IterableInterval< FloatType > > cc = create( code, target, true ).cursor();
		final Cursor< IterableInterval< FloatType > > rc = create( region, target, false ).cursor();

		while ( cc.hasNext() && rc.hasNext() )
		{
			testSum( cc.next().cursor(), rc.next().cursor() );
		}

	}

	@Test
	public void cursorConsistencyTest()
	{
		final Cursor< IterableInterval< FloatType > > cc = create( code, target, true ).cursor();
		final Cursor< IterableInterval< FloatType > > rc = create( region, target, true ).cursor();

		while ( cc.hasNext() && rc.hasNext() )
		{
			testSum( cc.next().cursor(), rc.next().cursor() );
		}
	}

	/*
	 * HELPERS
	 */

	private void testSum( final Cursor< FloatType > ccc, final Cursor< FloatType > rcc )
	{
		// Iteration orders doesn't have to be the same, however sum
		// should..
		float sumCode = 0, sumRegion = 0;
		while ( ccc.hasNext() && rcc.hasNext() )
		{
			sumCode += ccc.next().get();
			sumRegion += rcc.next().get();
		}
		assert ( sumCode == sumRegion );
	}

	private Img< FloatType > creatRandomImg( final int width, final int height )
	{
		final ArrayImg< FloatType, FloatArray > floats = ArrayImgs.floats( width, height );
		final Random r = new Random( SEED );

		for ( final FloatType f : floats )
		{
			f.set( r.nextFloat() );
		}

		return floats;
	}

	private // < P extends Localizable & Positionable & IterableInterval< Void > >
	IntervalView< IterableInterval< FloatType > > create(
			final PositionableIterableInterval< Void > region,
			final RandomAccessibleInterval< FloatType > source,
			final boolean isSafe )
	{
		final PositionableIterableInterval< FloatType > sample = Regions.sample( region, source, !isSafe );
		final TemplateRandomAccessible< FloatType > ra = new TemplateRandomAccessible<>( sample );
		return Views.interval( ra, source );
	}
}
