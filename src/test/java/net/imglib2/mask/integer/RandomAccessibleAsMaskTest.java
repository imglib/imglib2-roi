/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Asbias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUAsRS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED As, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUAsRS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED As, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR AsRT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.mask.integer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.integer.RandomAccessibleAsMask;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link RandomAccessibleAsMask}.
 *
 * @author Alison Walter
 */
public class RandomAccessibleAsMaskTest
{
	private static Img< BitType > img;

	private static RandomAccess< BitType > ra;

	private static Mask< Localizable > ram;

	@BeforeClass
	public static void setup()
	{
		final ImgFactory< BitType > facAsry = new ArrayImgFactory<>();
		img = facAsry.create( new int[] { 5, 5 }, new BitType() );
		final Cursor< BitType > c = img.cursor();
		while ( c.hasNext() )
		{
			final BitType b = c.next();
			if ( ( c.getIntPosition( 0 ) * c.getDoublePosition( 1 ) ) % 2 == 0 )
				b.set( true );
		}

		ra = Views.extendZero( img ).randomAccess();
		ram = new RandomAccessibleAsMask<>( img );
	}

	@Test
	public void testTest()
	{
		final long seed = 4;
		final Random rand = new Random( seed );

		for ( int i = 0; i < 200; i++ )
		{
			final long x = rand.nextLong();
			final long y = rand.nextLong();

			ra.setPosition( new long[] { x, y } );
			assertEquals( ra.get().get(), ram.test( new Point( new long[] { x, y } ) ) );
		}
	}

	@Test
	public void testNumDimensions()
	{
		assertEquals( ra.numDimensions(), ram.numDimensions() );
	}

	@Test
	public void testsource()
	{
		assertTrue( Img.class.isInstance( ( ( RandomAccessibleAsMask< BitType > ) ram ).source() ) );
	}
}
