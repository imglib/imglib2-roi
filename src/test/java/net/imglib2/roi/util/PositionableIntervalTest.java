/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.FinalInterval;
import net.imglib2.util.Intervals;

import org.junit.Assert;
import org.junit.Test;

public class PositionableIntervalTest
{
	@Test
	public void testConstruction()
	{
		final long[] min = new long[] { 1, 2, 3 };
		final long[] max = new long[] { 4, 5, 6 };

		final PositionableInterval interval = new PositionableInterval( new FinalInterval( min, max ) );

		Assert.assertEquals( interval.numDimensions(), min.length );
		for ( int d = 0; d < interval.numDimensions(); ++d )
		{
			Assert.assertEquals( min[ d ], interval.min( d ) );
			Assert.assertEquals( max[ d ], interval.max( d ) );
			Assert.assertEquals( 0, interval.getLongPosition( d ) );
		}
	}

	@Test
	public void testMove()
	{
		final long[] min = new long[] { 1, 2, 3 };
		final long[] max = new long[] { 4, 5, 6 };
		final long[] move = new long[] { 12, -32, 0 };

		final PositionableInterval interval = new PositionableInterval( new FinalInterval( min, max ) );
		interval.move( move );

		for ( int d = 0; d < interval.numDimensions(); ++d )
		{
			Assert.assertEquals( min[ d ] + move[ d ], interval.min( d ) );
			Assert.assertEquals( max[ d ] + move[ d ], interval.max( d ) );
			Assert.assertEquals( move[ d ], interval.getLongPosition( d ) );
		}
	}

	@Test
	public void testSetPosition()
	{
		final long[] min = new long[] { 1, 2, 3 };
		final long[] max = new long[] { 4, 5, 6 };
		final long[] pos = new long[] { 12, -32, 0 };

		final PositionableInterval interval = new PositionableInterval( new FinalInterval( min, max ) );
		interval.setPosition( pos );

		for ( int d = 0; d < interval.numDimensions(); ++d )
		{
			Assert.assertEquals( min[ d ] + pos[ d ], interval.min( d ) );
			Assert.assertEquals( max[ d ] + pos[ d ], interval.max( d ) );
			Assert.assertEquals( pos[ d ], interval.getLongPosition( d ) );
		}
	}

	@Test
	public void testOrigin()
	{
		final long[] min = new long[] { 1, 2, 3 };
		final long[] max = new long[] { 4, 5, 6 };

		final PositionableInterval interval = new PositionableInterval( new FinalInterval( min, max ) );

		for ( int d = 0; d < interval.numDimensions(); ++d )
		{
			Assert.assertEquals( -min[ d ], interval.origin().getLongPosition( d ) );
		}
	}

	@Test
	public void testSetOrigin()
	{
		final PositionableInterval interval = new PositionableInterval( Intervals.createMinSize( 0, 0, 9, 9 ) );
		interval.origin().setPosition( new int[] { 4, 4 } );
		for ( int d = 0; d < 2; ++d )
		{
			Assert.assertEquals( -4, interval.min( d ) );
			Assert.assertEquals( 4, interval.max( d ) );
			Assert.assertEquals( 0, interval.getLongPosition( d ) );
			Assert.assertEquals( 4, interval.origin().getLongPosition( d ) );
		}
	}

	@Test
	public void testMoveOrigin()
	{
		final PositionableInterval interval = new PositionableInterval( Intervals.createMinSize( 0, 0, 9, 9 ) );
		interval.origin().move( new int[] { 4, 4 } );
		for ( int d = 0; d < 2; ++d )
		{
			Assert.assertEquals( -4, interval.min( d ) );
			Assert.assertEquals( 4, interval.max( d ) );
			Assert.assertEquals( 0, interval.getLongPosition( d ) );
			Assert.assertEquals( 4, interval.origin().getLongPosition( d ) );
		}
	}
}
