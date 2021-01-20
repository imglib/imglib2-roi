/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.real.DoubleType;

public class OverlappingLabelsTest
{

	@Test
	public void testOverlappingLabels()
	{
		ImgLabeling< Integer, UnsignedByteType > labeling = createTestLabeling();
		OverlappingLabels< Integer > overlap = new OverlappingLabels<>( labeling );

		List< Integer > labels = overlap.getIndexedLabels();
		assertArrayEquals( new Integer[] { 1, 2, 3 }, labels.toArray() );

		RandomAccessibleInterval< UnsignedIntType > matrix = overlap.getMatrix();
		assertEquals( 3, matrix.dimension( 0 ) );
		assertEquals( 3, matrix.dimension( 1 ) );
		RandomAccess< UnsignedIntType > ra = matrix.randomAccess();
		ra.setPosition( new int[] { 0, 0 } );
		assertEquals( 4, ra.get().get() );
		ra.setPosition( new int[] { 1, 0 } );
		assertEquals( 1, ra.get().get() );
		ra.setPosition( new int[] { 2, 0 } );
		assertEquals( 2, ra.get().get() );
		ra.setPosition( new int[] { 0, 1 } );
		assertEquals( 1, ra.get().get() );
		ra.setPosition( new int[] { 1, 1 } );
		assertEquals( 2, ra.get().get() );
		ra.setPosition( new int[] { 2, 1 } );
		assertEquals( 1, ra.get().get() );
		ra.setPosition( new int[] { 0, 2 } );
		assertEquals( 2, ra.get().get() );
		ra.setPosition( new int[] { 1, 2 } );
		assertEquals( 1, ra.get().get() );
		ra.setPosition( new int[] { 2, 2 } );
		assertEquals( 4, ra.get().get() );

		RandomAccessibleInterval< DoubleType > normalizedMatrix = overlap.getNormalizedMatrix();
		RandomAccess< DoubleType > nra = normalizedMatrix.randomAccess();
		nra.setPosition( new int[] { 0, 0 } );
		assertEquals( 1.0, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 1, 0 } );
		assertEquals( 0.5, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 2, 0 } );
		assertEquals( 0.5, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 0, 1 } );
		assertEquals( 0.25, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 1, 1 } );
		assertEquals( 1.0, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 2, 1 } );
		assertEquals( 0.25, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 0, 2 } );
		assertEquals( 0.5, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 1, 2 } );
		assertEquals( 0.5, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 2, 2 } );
		assertEquals( 1.0, nra.get().get(), 0.0 );

		assertEquals( 2, overlap.getPixelOverlap( 1, 3 ) );
		assertEquals( 2, overlap.getPixelOverlapForIndex( 0, 2 ) );

		assertEquals( 0.50, overlap.getPartialOverlap( 2, 3 ), 0.0 );
		assertEquals( 0.25, overlap.getPartialOverlap( 3, 2 ), 0.0 );
		assertEquals( 0.50, overlap.getPartialOverlapForIndex( 1, 2 ), 0.0 );
		assertEquals( 0.25, overlap.getPartialOverlapForIndex( 2, 1 ), 0.0 );
	}

	private ImgLabeling< Integer, UnsignedByteType > createTestLabeling()
	{
		Img< UnsignedByteType > indexImg = ArrayImgs.unsignedBytes( new byte[] { 0, 1, 1, 2, 5, 4, 0, 3, 3 }, 3, 3 );
		List< Set< Integer > > mapping = Arrays.asList( asSet(), asSet( 1 ), asSet( 2 ), asSet( 3 ), asSet( 1, 3 ), asSet( 1, 2, 3 ) );
		return ImgLabeling.fromImageAndLabelSets( indexImg, mapping );
	}

	private Set< Integer > asSet( Integer... values )
	{
		return new TreeSet<>( Arrays.asList( values ) );
	}
}
