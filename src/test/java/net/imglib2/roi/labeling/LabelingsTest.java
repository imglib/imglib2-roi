/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.roi.labeling;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class LabelingsTest
{

	@Test
	public void testRemapLabels()
	{
		Integer[] values1 = new Integer[] { 42, 13 };
		Integer[] values2 = new Integer[] { 1 };
		String[] expected1 = new String[] { "prefix42", "prefix13" };
		String[] expected2 = new String[] { "prefix1" };
		// setup
		Img< UnsignedByteType > indexImg = ArrayImgs.unsignedBytes( new byte[] { 1, 0, 2 }, 3 );
		List< Set< Integer > > labelSets = Arrays.asList( asSet(), asSet( values1 ), asSet( values2 ) );
		ImgLabeling< Integer, UnsignedByteType > labeling = ImgLabeling.fromImageAndLabelSets( indexImg, labelSets );
		// process
		ImgLabeling< String, UnsignedByteType > remapped = Labelings.remapLabels( labeling, i -> "prefix" + i );
		// test
		RandomAccess< LabelingType< String > > ra = remapped.randomAccess();
		ra.setPosition( new long[] { 0 } );
		assertEquals( asSet( expected1 ), ra.get() );
		ra.setPosition( new long[] { 1 } );
		assertEquals( asSet(), ra.get() );
		ra.setPosition( new long[] { 2 } );
		assertEquals( asSet( expected2 ), ra.get() );
	}

	@Test( expected = IllegalArgumentException.class )
	public void testRemapLabelsNonInjective()
	{
		Integer[] values1 = new Integer[] { 1 };
		Integer[] values2 = new Integer[] { 2 };
		Integer[] values12 = new Integer[] { 1, 2 };
		// setup
		Img< UnsignedByteType > indexImg = ArrayImgs.unsignedBytes( new byte[] { 1, 0, 3, 2 }, 2, 2 );
		List< Set< Integer > > labelSets = Arrays.asList( asSet(), asSet( values1 ), asSet( values2 ), asSet( values12 ) );
		ImgLabeling< Integer, UnsignedByteType > labeling = ImgLabeling.fromImageAndLabelSets( indexImg, labelSets );
		// process
		Labelings.remapLabels( labeling, i -> "foo" );
	}

	@SuppressWarnings( "unchecked" )
	private < T > Set< T > asSet( T... values )
	{
		return new TreeSet<>( Arrays.asList( values ) );
	}

}
