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

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.Regions;
import net.imglib2.roi.util.PositionableIntervalRandomAccessible.PositionableIntervalFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import org.junit.Before;
import org.junit.Test;

public class PositionableIterableIntervalTest
{
	private static final long SEED = 42l;

	private PositionableIntervalFactory< PositionableIterationCode > code;

	private PositionableIntervalFactory< PositionableIterableIntervalImp< Void, IterableRegion< BitType > > > iterable;

	private IntervalView< FloatType > target;

	private PositionableIntervalFactory< PositionableIterableIntervalImp< Void, IterableRegion< BitType > > > region;

	@Before
	public void testPositionables()
	{
		final ArrayImg< BitType, ? > bits = ArrayImgs.bits( new long[] { 3, 3 } );

		final Random r = new Random( SEED );
		for ( BitType bit : bits )
		{
			bit.set( r.nextBoolean() );
		}

		iterable = new PositionableIntervalFactory< PositionableIterableIntervalImp< Void, IterableRegion< BitType > > >()
		{

			@Override
			public PositionableIterableIntervalImp< Void, IterableRegion< BitType > > create()
			{
				return new PositionableIterableIntervalImp<>( Regions.iterable( bits ) );
			}

			@Override
			public PositionableIterableIntervalImp< Void, IterableRegion< BitType > > copy( PositionableIterableIntervalImp< Void, IterableRegion< BitType > > source )
			{
				return source.copy();
			}
		};

		region = new PositionableIntervalFactory< PositionableIterableIntervalImp< Void, IterableRegion< BitType > > >()
		{

			@Override
			public PositionableIterableIntervalImp< Void, IterableRegion< BitType > > create()
			{
				return new PositionableIterableRegionImp<>( Regions.iterable( bits ) );
			}

			@Override
			public PositionableIterableIntervalImp< Void, IterableRegion< BitType > > copy( PositionableIterableIntervalImp< Void, IterableRegion< BitType > > source )
			{
				return source.copy();
			}
		};
		code = new PositionableIntervalFactory< PositionableIterationCode >()
		{

			@Override
			public PositionableIterationCode create()
			{
				return new PositionableIterationCode( ROIUtils.iterationCode( bits ) );
			}

			@Override
			public PositionableIterationCode copy( PositionableIterationCode source )
			{
				return source.copy();
			}
		};

		final Img< FloatType > rnd = creatRandomImg( 10, 10 );
		target = Views.interval( Views.extendBorder( rnd ), rnd );
	}

	@Test
	public void testSafety()
	{
		RandomAccess< IterableInterval< FloatType > > ra = create( code, target, true ).randomAccess();
		ra.setPosition( new long[] { 5, 5 } );
		Cursor< FloatType > first = ra.get().cursor();
		first.fwd();
		float curr = first.next().get();

		ra.setPosition( new long[] { 42, 42 } );
		Cursor< FloatType > second = ra.get().cursor();
		second.jumpFwd( 3 );

		assert ( curr == first.get().get() );
	}

	@Test
	public void raConsistencyTests()
	{

		final RandomAccess< IterableInterval< FloatType > > codeRA = create( code, target, true ).randomAccess();
		final RandomAccess< IterableInterval< FloatType > > regionRA = create( iterable, target, true ).randomAccess();

		codeRA.setPosition( new long[] { 52, 52 } );
		regionRA.setPosition( codeRA );

		testSum( codeRA.get().cursor(), regionRA.get().cursor() );

	}

	@Test
	public void safeUnsafeConsistencyTest()
	{
		Cursor< IterableInterval< FloatType > > cc = create( code, target, true ).cursor();
		Cursor< IterableInterval< FloatType > > rc = create( code, target, false ).cursor();

		while ( cc.hasNext() && rc.hasNext() )
		{
			testSum( cc.next().cursor(), rc.next().cursor() );
		}

	}

	@Test
	public void regionNonRegionConsistencyTest()
	{
		Cursor< IterableInterval< FloatType > > cc = create( code, target, true ).cursor();
		Cursor< IterableInterval< FloatType > > rc = create( region, target, false ).cursor();

		while ( cc.hasNext() && rc.hasNext() )
		{
			testSum( cc.next().cursor(), rc.next().cursor() );
		}

	}

	@Test
	public void cursorConsistencyTest()
	{
		Cursor< IterableInterval< FloatType > > cc = create( code, target, true ).cursor();
		Cursor< IterableInterval< FloatType > > rc = create( iterable, target, true ).cursor();

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

	private Img< FloatType > creatRandomImg( int width, int height )
	{
		final ArrayImg< FloatType, FloatArray > floats = ArrayImgs.floats( width, height );
		final Random r = new Random( SEED );

		for ( final FloatType f : floats )
		{
			f.set( r.nextFloat() );
		}

		return floats;
	}

	private < P extends Localizable & Positionable & IterableInterval< Void > > IntervalView< IterableInterval< FloatType > > create( final PositionableIntervalFactory< P > fac, final RandomAccessibleInterval< FloatType > source, boolean isSafe )
	{
		return Views.interval( new PositionableIntervalRandomAccessible<>( fac, source, isSafe ), source );
	}

}
