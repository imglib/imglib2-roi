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
package net.imglib2.roi.boundary;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.logic.BitType;

public class BoundaryRandomAccessTest
{
	@Test
	public void testBoundaryRandomAccess8()
	{
		final int[] region = new int[]
		{
			0, 0, 1, 1, 1, 1,
			0, 0, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1,
			0, 1, 1, 1, 1, 0,
			0, 0, 1, 1, 0, 0
		};
		final int[] b8 = new int[]
		{
			0, 0, 1, 1, 1, 1,
			0, 0, 1, 0, 0, 1,
			1, 1, 1, 0, 0, 1,
			1, 1, 0, 0, 1, 1,
			0, 1, 1, 1, 1, 0,
			0, 0, 1, 1, 0, 0
		};

		final Img< BitType > regionImg = ArrayImgs.bits( 6, 6 );
		final Cursor< BitType > c = regionImg.localizingCursor();
		int i = 0;
		while ( c.hasNext() )
			c.next().set( region[ i++ ] != 0 );

		final BoundaryRandomAccess8< BitType > ba = new BoundaryRandomAccess8< BitType >( regionImg );
		c.reset();
		i = 0;
		while ( c.hasNext() )
		{
			c.fwd();
			ba.setPosition( c );
			assertTrue( ba.get().get() == ( b8[ i++ ] != 0 ) );
		}
	}

	@Test
	public void testBoundaryRandomAccess4()
	{
		final int[] region = new int[]
		{
			0, 0, 1, 1, 1, 1,
			0, 0, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1,
			0, 1, 1, 1, 1, 0,
			0, 0, 1, 1, 0, 0
		};
		final int[] b4 = new int[]
		{
			0, 0, 1, 1, 1, 1,
			0, 0, 1, 0, 0, 1,
			1, 1, 0, 0, 0, 1,
			1, 0, 0, 0, 0, 1,
			0, 1, 0, 0, 1, 0,
			0, 0, 1, 1, 0, 0
		};

		final Img< BitType > regionImg = ArrayImgs.bits( 6, 6 );
		final Cursor< BitType > c = regionImg.localizingCursor();
		int i = 0;
		while ( c.hasNext() )
			c.next().set( region[ i++ ] != 0 );

		final BoundaryRandomAccess4< BitType > ba = new BoundaryRandomAccess4< BitType >( regionImg );
		c.reset();
		i = 0;
		while ( c.hasNext() )
		{
			c.fwd();
			ba.setPosition( c );
			assertTrue( ba.get().get() == ( b4[ i++ ] != 0 ) );
		}
	}
}
