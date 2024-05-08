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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.MaskInterval;
import net.imglib2.roi.Masks;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelRegion;
import net.imglib2.roi.labeling.LabelRegions;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.IntType;

/**
 * Tests for converting {@link LabelRegion}s and {@link ImgLabeling} to
 * {@link MaskInterval}.
 *
 * @author Alison Walter
 */
public class LabelingToMaskTest
{

	@Test
	public void testLabelRegionToMask()
	{
		final Img< IntType > indexImg = ArrayImgs.ints( 10, 10 );
		final ImgLabeling< String, IntType > imgLabeling = new ImgLabeling<>( indexImg );
		final long[][] labeledPoints = addLabelToLabeling( imgLabeling, "testing", 4923, 10 );

		final LabelRegion< String > region = new LabelRegions<>( imgLabeling ).getLabelRegion( "testing" );
		final LabeledMaskInterval< String > mask = Masks.toMaskInterval( region );

		intervalsEqual( region, mask );
		assertEquals( region.getLabel(), mask.getLabel() );
		locationsContained( labeledPoints, mask );
	}

	@Test
	public void testImgLabelingToMasks()
	{
		final Img< IntType > indexImg = ArrayImgs.ints( 100, 100 );
		final ImgLabeling< Long, IntType > imgLabeling = new ImgLabeling<>( indexImg );
		final long[][] labelOnePts = addLabelToLabeling( imgLabeling, 100l, 203, 100 );
		final long[][] labelTwoPts = addLabelToLabeling( imgLabeling, 200l, 984312, 100 );
		final long[][] labelThreePts = addLabelToLabeling( imgLabeling, 300l, -8932149, 100 );
		final long[][] labelFourPts = addLabelToLabeling( imgLabeling, 400l, -7159, 100 );

		final List< LabeledMaskInterval< Long > > masks = Masks.extractMaskIntervals( imgLabeling );
		final HashMap< Long, LabeledMaskInterval< Long > > map = new HashMap<>( masks.size() );
		masks.forEach( l -> map.put( l.getLabel(), l ) );

		assertEquals( 4, masks.size() );
		intervalsEqual( createInterval( labelOnePts ), map.get( 100l ) );
		intervalsEqual( createInterval( labelTwoPts ), map.get( 200l ) );
		intervalsEqual( createInterval( labelThreePts ), map.get( 300l ) );
		intervalsEqual( createInterval( labelFourPts ), map.get( 400l ) );
		locationsContained( labelOnePts, map.get( 100l ) );
		locationsContained( labelTwoPts, map.get( 200l ) );
		locationsContained( labelThreePts, map.get( 300l ) );
		locationsContained( labelFourPts, map.get( 400l ) );
	}

	@Test
	public void testImgLabelingToMasks4D()
	{
		final Img< IntType > indexImg = ArrayImgs.ints( 10, 5, 20, 4 );
		final ImgLabeling< Character, IntType > imgLabeling = new ImgLabeling<>( indexImg );
		final long[][] labelOnePts = addLabelToLabeling( imgLabeling, 'q', 932, 500 );
		final long[][] labelTwoPts = addLabelToLabeling( imgLabeling, 'w', 43, 500 );

		final List< LabeledMaskInterval< Character > > masks = Masks.extractMaskIntervals( imgLabeling );
		final HashMap< Character, LabeledMaskInterval< Character > > map = new HashMap<>( masks.size() );
		masks.forEach( l -> map.put( l.getLabel(), l ) );

		assertEquals( 2, masks.size() );
		intervalsEqual( createInterval( labelOnePts ), map.get( 'q' ) );
		intervalsEqual( createInterval( labelTwoPts ), map.get( 'w' ) );
		locationsContained( labelOnePts, map.get( 'q' ) );
		locationsContained( labelTwoPts, map.get( 'w' ) );
	}

	// -- Helper methods --

	private static < T, I extends IntegerType< I > > long[][] addLabelToLabeling( final ImgLabeling< T, I > labeling, final T label, final long seed, final long labelLimit )
	{
		final RandomAccess< LabelingType< T > > ra = labeling.randomAccess();
		final Random rand = new Random( seed );
		final int numLabels = ( int ) Math.round( rand.nextDouble() * labelLimit );
		final long[][] points = new long[ numLabels ][ labeling.numDimensions() ];

		for ( int i = 0; i < numLabels; i++ )
		{
			for ( int d = 0; d < labeling.numDimensions(); d++ )
			{
				ra.setPosition( rand.nextInt( ( int ) labeling.max( d ) ), d );
			}
			ra.get().add( label );
			ra.localize( points[ i ] );
		}
		return points;
	}

	private static void intervalsEqual( final Interval expected, final Interval actual )
	{
		assertEquals( expected.numDimensions(), actual.numDimensions() );
		for ( int d = 0; d < expected.numDimensions(); d++ )
		{
			assertEquals( expected.min( d ), actual.min( d ) );
			assertEquals( expected.max( d ), actual.max( d ) );
		}
	}

	private static < T > void locationsContained( final long[][] labeledPoints, final LabeledMaskInterval< T > mask )
	{
		final Point p = new Point( mask.numDimensions() );
		for ( int i = 0; i < labeledPoints.length; i++ )
		{
			p.setPosition( labeledPoints[ i ] );
			assertTrue( "Mask should contain location " + locationToString( p ) + " but doesn't", mask.test( p ) );
		}
	}

	private static String locationToString( final Localizable pos )
	{
		String separator = "";
		final StringBuilder b = new StringBuilder();
		b.append( "(" );
		for ( int d = 0; d < pos.numDimensions(); d++ )
		{
			b.append( separator );
			b.append( pos.getLongPosition( d ) );
			separator = ", ";
		}
		b.append( ")" );
		return b.toString();
	}

	private static Interval createInterval( final long[][] points )
	{
		final long[] min = new long[ points[ 0 ].length ];
		final long[] max = new long[ points[ 0 ].length ];
		Arrays.fill( min, Long.MAX_VALUE );
		Arrays.fill( max, Long.MIN_VALUE );
		for ( int i = 0; i < points.length; i++ )
		{
			for ( int d = 0; d < min.length; d++ )
			{
				if ( points[ i ][ d ] > max[ d ] )
					max[ d ] = points[ i ][ d ];
				if ( points[ i ][ d ] < min[ d ] )
					min[ d ] = points[ i ][ d ];
			}
		}
		return new FinalInterval( min, max );
	}

}
