/*
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.view.Views;

/**
 * Utility class for labeling tasks.
 *
 * @author Alison Walter
 * @author Jan Eglinger
 *
 */
public class Labelings
{
	/**
	 * Get a {@link LabelingMapping} from any {@link RandomAccessibleInterval}
	 * on {@link LabelingType}.
	 * 
	 * @param labeling
	 *            The label image
	 * @return mapping of indices to sets of labels
	 */
	public static < T > LabelingMapping< T > getLabelingMapping( final RandomAccessibleInterval< LabelingType< T > > labeling )
	{
		return Views.iterable( labeling ).firstElement().getMapping();
	}

	/**
	 * Create a new {@link ImgLabeling} from an input labeling, with the labels
	 * transformed according to the provided {@link Function}.
	 * 
	 * The mapping function needs to be an injective function (i.e. no
	 * duplicates are allowed in the result of the mapping), otherwise an
	 * {@link IllegalArgumentException} will be thrown.
	 * 
	 * @param labeling
	 *            The {@link ImgLabeling} to be transformed
	 * @param function
	 *            The {@link Function} providing the mapping from input to
	 *            output labels
	 * @return the transformed {@link ImgLabeling}
	 */
	public static < T, U, I extends IntegerType< I > > ImgLabeling< U, I > remapLabels( ImgLabeling< T, I > labeling, Function< T, U > function )
	{
		// populate map of all existing labels
		Set< T > inputLabels = labeling.getMapping().getLabels();
		Map< T, U > labelMap = new HashMap<>( inputLabels.size() );
		inputLabels.stream().forEach( t -> labelMap.put( t, function.apply( t ) ) );

		return remapLabels( labeling, labelMap );
	}

	/**
	 * Create a new {@link ImgLabeling} from an input labeling, with the labels
	 * transformed according to the provided {@link Map}.
	 * 
	 * The map needs to be injective/bijective (i.e. no duplicates are allowed
	 * in the result of the mapping), otherwise an
	 * {@link IllegalArgumentException} will be thrown.
	 * 
	 * @param labeling
	 *            The {@link ImgLabeling} to be transformed
	 * @param mapping
	 *            The {@link Map} mapping input to output labels
	 * @return the transformed {@link ImgLabeling}
	 */
	public static < T, U, I extends IntegerType< I > > ImgLabeling< U, I > remapLabels( ImgLabeling< T, I > labeling, Map< T, U > mapping )
	{
		List< Set< T > > labelSets = labeling.getMapping().getLabelSets();
		List< Set< U > > resultLabelSets = new ArrayList<>( labelSets.size() );

		labelSets.forEach( labelSet -> resultLabelSets.add( labelSet.stream().map( t -> mapping.get( t ) ).collect( Collectors.toSet() ) ) );

		ImgLabeling< U, I > result = new ImgLabeling<>( labeling.getIndexImg() );
		LabelingMapping< U > resultMapping = result.getMapping();
		resultMapping.setLabelSets( resultLabelSets );
		return result;
	}

	/**
	 * Return a {@link Set} of occurring pixel values in the {@link ImgLabeling} index image.
	 *
	 * @param img
	 * 		Image labeling from which to extract the occurring pixel values
	 * @param <T>
	 * 		The type of labels assigned to pixels
	 * @param <I>
	 * 		The pixel type of the backing image
	 *
	 * @return {@link Set} of occurring pixel values
	 */
	static < T, I extends IntegerType< I > > Set< I > getOccurringPixelSets( ImgLabeling< T, I > img )
	{
		Set< I > occurringValues = new HashSet<>();
		for ( I pixel : Views.iterable( img.getIndexImg() ) )
		{
			if ( pixel.getInteger() > 0 )
				occurringValues.add( pixel.copy() );
		}

		return occurringValues;
	}

	/**
	 * Check if the image labeling {@code img} has intersecting labels. Two labels intersect if there
	 * is at least one pixel in the image labeled with both labels.
	 *
	 * @param img
	 * 		Image labeling
	 * @param <T>
	 * 		The type of labels assigned to pixels
	 * @param <I>
	 * 		The pixel type of the backing image
	 *
	 * @return True if the image labeling has intersecting labels, false otherwise.
	 */
	static < T, I extends IntegerType< I > > boolean hasIntersectingLabels( ImgLabeling< T, I > img )
	{
		List< Set< T > > labelSets = img.getMapping().getLabelSets();
		for ( I i : getOccurringPixelSets( img ) )
		{
			if ( labelSets.get( i.getInteger() ).size() > 1 )
			{
				return true;
			}
		}
		return false;
	}
}
