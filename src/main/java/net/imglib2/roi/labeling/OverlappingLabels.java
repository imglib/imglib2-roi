/*-
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
package net.imglib2.roi.labeling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Util;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class OverlappingLabels< T extends Comparable< T > >
{

	private Img< UnsignedIntType > overlapMatrix;

	private List< T > labelList;

	private Img< LongType > labelSizeImg;

	private Img< DoubleType > relativeOverlapMatrix;

	public OverlappingLabels( RandomAccessibleInterval< LabelingType< T > > labeling )
	{

		FragmentLabelRegions labelRegions = new FragmentLabelRegions( labeling );

		// List of existing labels
		// NB: this triggers labelRegions computation
		labelList = new ArrayList<>( labelRegions.getExistingLabels() );
		Collections.sort( labelList );

		// Create 2D matrix image
		FinalInterval matrixDims = new FinalInterval( labelList.size(), labelList.size() );
		overlapMatrix = Util.getSuitableImgFactory( matrixDims, new UnsignedIntType() ).create( matrixDims );

		// Sum up sizes of single fragments
		RandomAccess< UnsignedIntType > randomAccess = overlapMatrix.randomAccess();
		UnsignedIntType sizeType = new UnsignedIntType();
		LabelingMapping< T > labelingMapping = Labelings.getLabelingMapping( labeling );
		for ( int i = 0; i < labelingMapping.numSets(); i++ )
		{
			Set< T > currentLabels = labelingMapping.labelsAtIndex( i );
			long size = labelRegions.getFragmentSizeForIndex( i );
			if ( size > 0 )
			{
				sizeType.set( size );
				for ( T a : currentLabels )
				{
					randomAccess.setPosition( labelList.indexOf( a ), 0 );
					for ( T b : currentLabels )
					{
						randomAccess.setPosition( labelList.indexOf( b ), 1 );
						randomAccess.get().add( sizeType );
					}
				}
			}
		}

		labelSizeImg = ArrayImgs.longs( labelList.size() );
		Cursor< LongType > cursor = labelSizeImg.localizingCursor();
		while ( cursor.hasNext() )
		{
			cursor.next().set( labelRegions.getLabelRegion( labelList.get( cursor.getIntPosition( 0 ) ) ).size() );
		}

		relativeOverlapMatrix = Util.getSuitableImgFactory( matrixDims, new DoubleType() ).create( matrixDims );
		IntervalView< LongType > divisor = Views.addDimension( labelSizeImg, 0, labelList.size() - 1 );

		LoopBuilder.setImages( overlapMatrix, divisor, relativeOverlapMatrix ).forEachPixel( ( a, b, c ) -> c.set( ( double ) a.get() / b.get() ) );
	}

	/**
	 * Get the confusion/co-occurrence matrix.
	 * 
	 * Values indicate the number of pixels that are in common between the label
	 * at position x and the label at position y. For the mapping of dimension
	 * indices to labels, see {@link OverlappingLabels#getIndexedLabels()}.
	 * 
	 * @return the co-occurrence matrix
	 */
	public RandomAccessibleInterval< UnsignedIntType > getMatrix()
	{
		return overlapMatrix;
	}

	/**
	 * Get the normalized directional confusion matrix.
	 * 
	 * Values indicate the proportion of the label at position x that is
	 * co-occurring with the label at position y. For the mapping of dimension
	 * indices to labels, see {@link OverlappingLabels#getIndexedLabels()}.
	 * 
	 * @return the normalized co-occurrence matrix
	 */
	public RandomAccessibleInterval< DoubleType > getNormalizedMatrix()
	{
		return relativeOverlapMatrix;
	}

	/**
	 * Get the list of labels for this confusion matrix.
	 * 
	 * @return the list of labels in order of the matrix indices
	 */
	public List< T > getIndexedLabels()
	{
		return labelList;
	}

	/**
	 * The number of pixels overlapping between two labels.
	 * 
	 * @param label1
	 *            The first label
	 * @param label2
	 *            The second label
	 * @return the size of the overlapping region (in pixels)
	 */
	public long getPixelOverlap( T label1, T label2 )
	{
		return getPixelOverlapForIndex( labelList.indexOf( label1 ), labelList.indexOf( label2 ) );
	}

	/**
	 * The number of pixels overlapping between two labels at the specified
	 * indices.
	 * 
	 * @param index1
	 *            The index of the first label
	 * @param index2
	 *            The index of the second label
	 * @return the size of the overlapping region (in pixels)
	 */
	public long getPixelOverlapForIndex( int index1, int index2 )
	{
		return overlapMatrix.getAt( index1, index2 ).get();
	}

	/**
	 * The fraction of {@code ofLabel} that overlaps with
	 * {@code overlappingLabel}.
	 * 
	 * If {@code ofLabel} has size 2 and {@code overlappingLabel} has size 100,
	 * and they overlap by 1 pixel, the resulting fraction is 0.5
	 * 
	 * @param ofLabel
	 * @param overlappingLabel
	 * @return the overlapping fraction
	 */
	public double getPartialOverlap( T ofLabel, T overlappingLabel )
	{
		return getPartialOverlapForIndex( labelList.indexOf( ofLabel ), labelList.indexOf( overlappingLabel ) );
	}

	/**
	 * The fraction of the label at {@code ofLabelIndex} that overlaps with the
	 * label at {@code overlappingLabelIndex}.
	 * 
	 * See {@link OverlappingLabels#getPartialOverlap(Comparable, Comparable)}.
	 * 
	 * @param ofLabelIndex
	 * @param overlappingLabelIndex
	 * @return the overlapping fraction
	 */
	public double getPartialOverlapForIndex( int ofLabelIndex, int overlappingLabelIndex )
	{
		return relativeOverlapMatrix.getAt( ofLabelIndex, overlappingLabelIndex ).get();
	}

	private class FragmentLabelRegions extends LabelRegions< T >
	{

		public FragmentLabelRegions( RandomAccessibleInterval< LabelingType< T > > labeling )
		{
			super( labeling );
		}

		public long getFragmentSizeForIndex( int index )
		{
			return indexToFragmentProperties.get( index ).getSize();
		}
	}
}
