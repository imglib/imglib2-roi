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

package net.imglib2.roi.util;

import java.util.NoSuchElementException;
import java.util.Random;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.Regions;
import net.imglib2.type.logic.BitType;
import net.imglib2.util.ConstantUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link IterableRandomAccessibleRegion}.
 *
 * @author Alison Walter
 *
 */
public class IterableRegionOnBooleanRAITest
{

	private static IterableRegion< BitType > empty;

	private static IterableRegion< BitType > ir;

	private static Img< BitType > img;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public static void setUp()
	{
		final int maxOne = 10;
		final int maxTwo = 20;
		final RandomAccessibleInterval< BitType > emptyRAI = ConstantUtils.constantRandomAccessibleInterval( new BitType( false ), 2, new FinalInterval( new long[] { 0, 0 }, new long[] { maxOne, maxTwo } ) );

		img = ArrayImgs.bits( maxOne, maxTwo );
		final Random rand = new Random( 12 );
		final Cursor< BitType > c = img.cursor();
		while ( c.hasNext() )
			c.next().set( rand.nextBoolean() );

		empty = Regions.iterable( emptyRAI );
		ir = Regions.iterable( img );
	}

	@Test
	public void testCursorFwd()
	{
		final long count = Regions.countTrue( img );
		long countIR = 0;
		final Cursor< Void > c = ir.cursor();
		while ( c.hasNext() )
		{
			c.fwd();
			countIR++;
		}

		assertEquals( count, countIR );
	}

	@Test
	public void testCursorNext()
	{
		final Cursor< BitType > imgC = img.cursor();
		final Cursor< Void > irC = ir.cursor();

		while ( imgC.hasNext() )
		{
			final boolean value = imgC.next().get();
			if ( value )
			{
				irC.next();
				assertEquals( imgC.getLongPosition( 0 ), irC.getLongPosition( 0 ) );
				assertEquals( imgC.getLongPosition( 1 ), irC.getLongPosition( 1 ) );
			}
		}
	}

	@Test
	public void testFirstElement()
	{
		// Ensure no error is thrown
		assertTrue( ir.firstElement() == null );
	}

	@Test
	public void testCursorFwdEmptyRegion()
	{
		int count = 0;
		final Cursor< Void > c = empty.cursor();
		while ( c.hasNext() )
		{
			c.fwd();
			count++;
		}

		assertEquals( 0, count );
	}

	@Test
	public void testCursorNextEmptyRegion()
	{
		final long[] originalLocation = new long[ 2 ];
		final long[] newLocation = new long[ 2 ];

		final Cursor< Void > c = empty.cursor();
		c.fwd();
		c.localize( originalLocation );
		c.next();
		c.localize( newLocation );

		assertEquals( originalLocation[ 0 ], newLocation[ 0 ] );
		assertEquals( originalLocation[ 1 ], newLocation[ 1 ] );
	}

	@Test
	public void testCursorFirstElementEmptyRegion()
	{
		exception.expect( NoSuchElementException.class );
		empty.firstElement();
	}
}
