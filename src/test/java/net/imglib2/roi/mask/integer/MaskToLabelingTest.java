/*-
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
package net.imglib2.roi.mask.integer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Localizable;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.MaskInterval;
import net.imglib2.roi.Masks;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.numeric.integer.IntType;

/**
 * Tests for converting {@link MaskInterval}s to {@link ImgLabeling}s.
 *
 * @author Alison Walter
 */
public class MaskToLabelingTest
{

	@Test
	public void testOneMask()
	{
		final MaskInterval m = createBox( new long[] { 10, 12 }, new long[] { 22, 20 } );
		final LabeledMaskInterval< String > labeledMask = new LabeledMaskInterval<>( m, "kittyCat" );
		final Img< IntType > indexImg = ArrayImgs.ints( 25, 25 );
		final ImgLabeling< String, IntType > imgLabeling = new ImgLabeling<>( indexImg );
		Masks.addMasksToLabeling( Collections.singletonList( labeledMask ), imgLabeling, String.class );

		final Cursor< LabelingType< String > > c = imgLabeling.cursor();
		while ( c.hasNext() )
		{
			final LabelingType< String > labels = c.next();
			if ( labeledMask.test( c ) )
			{
				assertEquals( 1, labels.size() );
				assertTrue( labels.contains( labeledMask.getLabel() ) );
			}
			else
				assertTrue( labels.isEmpty() );
		}
	}

	@Test
	public void testOneMask3D()
	{
		final MaskInterval m = createSphere( new long[] { 9, 19, 14 }, 5 );
		final LabeledMaskInterval< String > labeledMask = new LabeledMaskInterval<>( m, "prettyKitty" );
		final Img< IntType > indexImg = ArrayImgs.ints( 15, 25, 20 );
		final ImgLabeling< String, IntType > imgLabeling = new ImgLabeling<>( indexImg );
		Masks.addMasksToLabeling( Collections.singletonList( labeledMask ), imgLabeling, String.class );

		final Cursor< LabelingType< String > > c = imgLabeling.cursor();
		while ( c.hasNext() )
		{
			final LabelingType< String > labels = c.next();
			if ( labeledMask.test( c ) )
			{
				assertEquals( 1, labels.size() );
				assertTrue( labels.contains( labeledMask.getLabel() ) );
			}
			else
			{
				assertTrue( labels.isEmpty() );
			}
		}
	}

	@Test
	public void testManyMasks()
	{
		final MaskInterval diagonal = new DefaultMaskInterval( new FinalInterval( new long[] { 0, 0 }, new long[] { 10, 10 } ), //
				BoundaryType.UNSPECIFIED, p -> p.getLongPosition( 0 ) == p.getLongPosition( 1 ), KnownConstant.UNKNOWN );
		final MaskInterval horizontal = new DefaultMaskInterval( new FinalInterval( new long[] { 2, 3 }, new long[] { 15, 5 } ), //
				BoundaryType.UNSPECIFIED, p -> p.getLongPosition( 1 ) == 4, KnownConstant.UNKNOWN );
		final MaskInterval vertical = new DefaultMaskInterval( new FinalInterval( new long[] { 3, 2 }, new long[] { 5, 8 } ), //
				BoundaryType.UNSPECIFIED, p -> p.getLongPosition( 0 ) == 4, KnownConstant.UNKNOWN );
		final MaskInterval box = createBox( new long[] { 4, 4 }, new long[] { 10, 10 } );
		final LabeledMaskInterval< Byte > labelDiagonal = new LabeledMaskInterval<>( diagonal, ( byte ) 2 );
		final LabeledMaskInterval< Integer > labelHorizontal = new LabeledMaskInterval<>( horizontal, 33000 );
		final LabeledMaskInterval< Long > labelVertical = new LabeledMaskInterval<>( vertical, 0xFEEDBEEFl );
		final LabeledMaskInterval< Integer > labelBox = new LabeledMaskInterval<>( box, -400024900 );
		final Img< IntType > indexImg = ArrayImgs.ints( 16, 16 );
		final ImgLabeling< Number, IntType > imgLabeling = new ImgLabeling<>( indexImg );
		Masks.addMasksToLabeling( Arrays.asList( labelDiagonal, labelHorizontal, labelVertical, labelBox ), imgLabeling, Number.class );

		final Cursor< LabelingType< Number > > c = imgLabeling.cursor();
		while ( c.hasNext() )
		{
			final LabelingType< Number > labels = c.next();
			int numLabels = 0;
			if ( labelDiagonal.test( c ) )
			{
				assertTrue( labels.contains( labelDiagonal.getLabel() ) );
				numLabels++;
			}
			if ( labelHorizontal.test( c ) )
			{
				assertTrue( labels.contains( labelHorizontal.getLabel() ) );
				numLabels++;
			}
			if ( labelVertical.test( c ) )
			{
				assertTrue( labels.contains( labelVertical.getLabel() ) );
				numLabels++;
			}
			if ( labelBox.test( c ) )
			{
				assertTrue( labels.contains( labelBox.getLabel() ) );
				numLabels++;
			}
			assertEquals( numLabels, labels.size() );
		}
	}

	@Test
	public void testCompositeMasks()
	{
		final MaskInterval boxOne = createBox( new long[] { 2, 2 }, new long[] { 10, 10 } );
		final MaskInterval boxTwo = createBox( new long[] { 3, 4 }, new long[] { 8, 10 } );
		final MaskInterval boxThree = createBox( new long[] { 5, 5 }, new long[] { 20, 25 } );
		final MaskInterval sphereOne = createSphere( new long[] { 30, 30 }, 5 );
		final MaskInterval sphereTwo = createSphere( new long[] { 30, 30 }, 10 );
		final MaskInterval sphereXor = sphereOne.xor( sphereTwo );
		final LabeledMaskInterval< Character > labelBoxOne = new LabeledMaskInterval<>( boxOne, 'a' );
		final LabeledMaskInterval< Character > labelBoxTwo = new LabeledMaskInterval<>( boxTwo, 'b' );
		final LabeledMaskInterval< Character > labelBoxThree = new LabeledMaskInterval<>( boxThree, 'c' );
		final LabeledMaskInterval< Character > labelXor = new LabeledMaskInterval<>( sphereXor, 'd' );
		final MaskInterval boxComposite = labelBoxThree.and( labelBoxTwo.or( labelBoxOne ) );
		final Img< IntType > indexImg = ArrayImgs.ints( 41, 41 );
		final ImgLabeling< Character, IntType > imgLabeling = new ImgLabeling<>( indexImg );
		Masks.addMasksToLabeling( Arrays.asList( boxComposite, labelXor ), imgLabeling, Character.class );

		final MaskInterval XorBoxComposite = labelBoxThree.xor( labelBoxTwo.or( labelBoxOne ) );
		final Cursor< LabelingType< Character > > c = imgLabeling.cursor();
		while ( c.hasNext() )
		{
			int numLabels = 0;
			final LabelingType< Character > labels = c.next();
			// Only labels of the children with the parent ROI should be written
			// to the ImgLabeling
			// So none of the labels in the xor should be in the ImgLabeling
			if ( XorBoxComposite.test( c ) )
			{
				assertTrue( labels.isEmpty() );
			}
			if ( boxComposite.test( c ) )
			{
				assertTrue( labels.contains( labelBoxThree.getLabel() ) );
				numLabels++;
				if ( boxOne.test( c ) )
				{
					assertTrue( labels.contains( labelBoxOne.getLabel() ) );
					numLabels++;
				}
				if ( boxTwo.test( c ) )
				{
					assertTrue( labels.contains( labelBoxTwo.getLabel() ) );
					numLabels++;
				}
			}
			// Label was set on the Xor so none of the inner circles points
			// should have labels
			if ( sphereXor.test( c ) )
			{
				assertTrue( labels.contains( labelXor.getLabel() ) );
				numLabels++;
			}
			if ( sphereOne.test( c ) )
				assertTrue( labels.isEmpty() );
			assertEquals( numLabels, labels.size() );
		}
	}

	@Test
	public void testAddingToExisting()
	{
		final Img< IntType > indexImg = ArrayImgs.ints( 10, 10 );
		final ImgLabeling< Byte, IntType > imgLabeling = new ImgLabeling<>( indexImg );
		final Cursor< LabelingType< Byte > > c = imgLabeling.cursor();
		while ( c.hasNext() )
		{
			final LabelingType< Byte > labels = c.next();
			if ( ( c.getLongPosition( 0 ) + c.getLongPosition( 1 ) ) % 2 == 0 )
				labels.add( ( byte ) 120 );
		}
		final LabeledMaskInterval< Byte > labeledMask = new LabeledMaskInterval<>( createSphere( new long[] { 5, 4 }, 4 ), ( byte ) -80 );
		Masks.addMasksToLabeling( Collections.singletonList( labeledMask ), imgLabeling, Byte.class );

		final Cursor< LabelingType< Byte > > c2 = imgLabeling.cursor();
		while ( c2.hasNext() )
		{
			final LabelingType< Byte > labels = c2.next();
			int numLabels = 0;
			if ( ( c2.getLongPosition( 0 ) + c2.getLongPosition( 1 ) ) % 2 == 0 )
			{
				assertTrue( labels.contains( ( byte ) 120 ) );
				numLabels++;
			}
			if ( labeledMask.test( c2 ) )
			{
				assertTrue( labels.contains( labeledMask.getLabel() ) );
				numLabels++;
			}
			assertEquals( numLabels, labels.size() );
		}
	}

	@Test
	public void testIncompatibleLabelType()
	{
		final LabeledMaskInterval< String > sphereOne = new LabeledMaskInterval<>( createSphere( new long[] { 30, 34 }, 9 ), "abc" );
		final LabeledMaskInterval< Integer > sphereTwo = new LabeledMaskInterval<>( createSphere( new long[] { 29, 39 }, 5 ), 80 );
		final LabeledMaskInterval< Integer > sphereThree = new LabeledMaskInterval<>( createSphere( new long[] { 20, 35 }, 11 ), 981 );
		final LabeledMaskInterval< Integer > sphereFour = new LabeledMaskInterval<>( createSphere( new long[] { 30, 53 }, 4 ), -7932 );
		final LabeledMaskInterval< Integer > sphereOr = new LabeledMaskInterval< Integer >( sphereOne.or( sphereTwo ), -189 );
		final Img< IntType > indexImg = ArrayImgs.ints( 100, 100 );
		final ImgLabeling< Integer, IntType > imgLabeling = new ImgLabeling<>( indexImg );

		// Ensure exception thrown
		Exception e = null;
		try
		{
			Masks.addMasksToLabeling( Arrays.asList( sphereThree, sphereFour, sphereOr ), imgLabeling, Integer.class );
		}
		catch ( final Exception ex )
		{
			e = ex;
		}

		assertNotNull( "Expected an exception but none was thrown!", e );
		assertEquals( IllegalArgumentException.class, e.getClass() );

		// Ensure ImgLabeling was not modified
		final Cursor< LabelingType< Integer > > c = imgLabeling.cursor();
		while ( c.hasNext() )
			assertTrue( c.next().isEmpty() );
	}

	@Test
	public void testMaskOutsideInterval()
	{
		final LabeledMaskInterval< String > sphere = new LabeledMaskInterval<>( createSphere( new long[] { 20, 25 }, 10 ), "cat" );
		final Img< IntType > indexImg = ArrayImgs.ints( 30, 30 );
		final ImgLabeling< String, IntType > imgLabeling = new ImgLabeling<>( indexImg );

		// Ensure exception thrown
		Exception e = null;
		try
		{
			Masks.addMasksToLabeling( Collections.singletonList( sphere ), imgLabeling, String.class );
		}
		catch ( final Exception ex )
		{
			e = ex;
		}
		assertNotNull( "Expected an exception but none was thrown!", e );
		assertEquals( IllegalArgumentException.class, e.getClass() );

		// Ensure ImgLabeling was not modified
		final Cursor< LabelingType< String > > c = imgLabeling.cursor();
		while ( c.hasNext() )
			assertTrue( c.next().isEmpty() );
	}

	@Test
	public void testDifferentDimensions()
	{
		final LabeledMaskInterval< String > box = new LabeledMaskInterval<>( createBox( new long[] { 10, 14, 9 }, new long[] { 21, 35, 20 } ), "quadruped" );
		final Img< IntType > indexImg = ArrayImgs.ints( 50, 50 );
		final ImgLabeling< String, IntType > imgLabeling = new ImgLabeling<>( indexImg );

		// Ensure exception thrown
		Exception e = null;
		try
		{
			Masks.addMasksToLabeling( Collections.singletonList( box ), imgLabeling, String.class );
		}
		catch ( final Exception ex )
		{
			e = ex;
		}
		assertNotNull( "Expected an exception but none was thrown!", e );
		assertEquals( IllegalArgumentException.class, e.getClass() );

		// Ensure ImgLabeling was not modified
		final Cursor< LabelingType< String > > c = imgLabeling.cursor();
		while ( c.hasNext() )
			assertTrue( c.next().isEmpty() );
	}

	@Test
	public void testDefaultLabelEntireMask()
	{
		final MaskInterval m = createBox( new long[] { 10, 10 }, new long[] { 20, 20 } );
		final Img< IntType > indexImg = ArrayImgs.ints( 21, 21 );
		final ImgLabeling< String, IntType > imgLabeling = new ImgLabeling<>( indexImg );
		final String defaultLabel = "default";
		Masks.addMasksToLabeling( Collections.singletonList( m ), imgLabeling, defaultLabel );

		final Cursor< LabelingType< String > > c = imgLabeling.cursor();
		while ( c.hasNext() )
		{
			final LabelingType< String > labels = c.next();
			if ( m.test( c ) )
			{
				assertTrue( labels.contains( defaultLabel ) );
				assertEquals( 1, labels.size() );
			}
			else
				assertTrue( labels.isEmpty() );
		}
	}

	@Test
	public void testDefaultLabelPartialMask()
	{
		final LabeledMaskInterval< String > labeledSphere = new LabeledMaskInterval<>( createSphere( new long[] { 10, 10 }, 5 ), "label" );
		final MaskInterval sphere = createSphere( new long[] { 15, 13 }, 4 );
		final MaskInterval or = labeledSphere.or( sphere );
		final MaskInterval minus = sphere.minus( labeledSphere );
		final String defaultLabel = "default";
		final Img< IntType > indexImg = ArrayImgs.ints( 20, 20 );
		final ImgLabeling< String, IntType > imgLabeling = new ImgLabeling<>( indexImg );
		Masks.addMasksToLabeling( Collections.singletonList( or ), imgLabeling, defaultLabel );

		final Cursor< LabelingType< String > > c = imgLabeling.cursor();
		while ( c.hasNext() )
		{
			final LabelingType< String > labels = c.next();
			if ( labeledSphere.test( c ) )
			{
				assertTrue( labels.contains( labeledSphere.getLabel() ) );
				assertEquals( 1, labels.size() );
			}
			else if ( minus.test( c ) )
			{
				assertTrue( labels.contains( defaultLabel ) );
				assertEquals( 1, labels.size() );
			}
			else
				assertTrue( labels.isEmpty() );
		}
	}

	// -- Helper methods --

	private static MaskInterval createBox( final long[] min, final long[] max )
	{
		final Predicate< Localizable > p = l -> {
			boolean inside = true;
			for ( int d = 0; d < l.numDimensions(); d++ )
				inside &= ( l.getLongPosition( d ) < max[ d ] && l.getLongPosition( d ) > min[ d ] );
			return inside;
		};
		return new DefaultMaskInterval( new FinalInterval( min, max ), BoundaryType.OPEN, p, KnownConstant.UNKNOWN );
	}

	private static MaskInterval createSphere( final long[] center, final long radius )
	{
		final Predicate< Localizable > p = l -> {
			long distancePowered = 0;
			for ( int d = 0; d < l.numDimensions(); d++ )
				distancePowered += ( l.getLongPosition( d ) - center[ d ] ) * ( l.getLongPosition( d ) - center[ d ] );
			return distancePowered < ( radius * radius );
		};
		final long[] min = new long[ center.length ];
		final long[] max = new long[ center.length ];
		for ( int d = 0; d < center.length; d++ )
		{
			min[ d ] = center[ d ] - radius;
			max[ d ] = center[ d ] + radius;
		}
		return new DefaultMaskInterval( new FinalInterval( min, max ), BoundaryType.OPEN, p, KnownConstant.UNKNOWN );
	}
}
