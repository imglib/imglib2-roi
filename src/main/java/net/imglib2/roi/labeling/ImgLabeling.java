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

package net.imglib2.roi.labeling;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.AbstractConvertedCursor;
import net.imglib2.converter.AbstractConvertedRandomAccess;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.view.Views;
import net.imglib2.view.iteration.SubIntervalIterable;
import net.imglib2.roi.labeling.LabelingType.ModCount;

/**
 * A labeling backed by a {@link RandomAccessibleInterval image} of integer
 * indices.
 *
 * @param <T>
 *            The type of labels assigned to pixels
 * @param <I>
 *            The pixel type of the backing image. The {@link LabelingMapping}
 *            maps sets of labels to index values which can be more compactly
 *            stored.
 *
 * @see LabelingMapping
 *
 * @author Lee Kamentsky, Christian Dietz, Martin Horn
 * @author Tobias Pietzsch
 */
public class ImgLabeling< T, I extends IntegerType< I > >
		extends AbstractWrappedInterval< RandomAccessibleInterval< I > >
		implements RandomAccessibleInterval< LabelingType< T > >,
				IterableInterval< LabelingType< T > >,
				SubIntervalIterable< LabelingType< T > >
{
	private final RandomAccessibleInterval< I > indexAccessible;

	private final IterableInterval< I > indexIterable;

	private final boolean subIterable;

	private final ModCount generation;

	private final LabelingMapping< T > mapping;

	public ImgLabeling( final RandomAccessibleInterval< I > img )
	{
		super( img );
		indexAccessible = img;
		indexIterable = Views.iterable( img );
		subIterable = indexIterable instanceof SubIntervalIterable;
		generation = new ModCount();
		mapping = new LabelingMapping<>( indexIterable.firstElement() );
	}

	/**
	 * Creates a non empty ImgLabeling, from an index image and a list of label
	 * sets.
	 * <p>
	 * Each pixel can have multiple labels. If there's only one label per pixel,
	 * see {@link #fromImageAndLabels}
	 * <p>
	 * The list of label sets gives the initial mapping between pixel values and
	 * labels. The pixel values of the index image must be non negative, and
	 * smaller than the size of the list of label sets. The first element of the
	 * list of label sets must always be the empty set. Every element in the
	 * list must be unique.
	 * <p>
	 * A pixel value of i represents a pixel, which has the labels, that are at
	 * index i in the list of label sets. A pixel value of 0 always represents a
	 * pixel with no labels, because the first entry in the list is always the
	 * empty set.
	 */
	public static < T, I extends IntegerType< I > > ImgLabeling< T, I > fromImageAndLabelSets(
			final RandomAccessibleInterval< I > img,
			final List< Set< T > > labelSets )
	{
		final ImgLabeling< T, I > result = new ImgLabeling<>( img );
		result.getMapping().setLabelSets( labelSets );
		return result;
	}

	/**
	 * Creates a non empty ImgLabeling, from an index image and a list of
	 * labels.
	 * <p>
	 * This method does not support intersection labels, for intersecting labels
	 * set see {@link #fromImageAndLabelSets}.
	 * <p>
	 * The pixel values of the index image must be between 0 and the length of
	 * the list of labels. A pixel value of zero represents a pixel with no
	 * label. A pixel value of N represents the label, which is given by the Nth
	 * entry in the list of labels.
	 */
	public static < T, I extends IntegerType< I > > ImgLabeling< T, I > fromImageAndLabels(
			final RandomAccessibleInterval< I > img,
			final List< T > labels )
	{
		final List< Set< T > > labelSets = Stream.concat( Stream.< Set< T > >of( Collections.emptySet() ),
				labels.stream().map( Collections::singleton ) ).collect( Collectors.toList() );
		return fromImageAndLabelSets( img, labelSets );
	}

	public LabelingMapping< T > getMapping()
	{
		return mapping;
	}

	class LabelingConvertedRandomAccess extends AbstractConvertedRandomAccess< I, LabelingType< T > >
	{
		private final LabelingType< T > type;

		public LabelingConvertedRandomAccess( final RandomAccess< I > source )
		{
			super( source );
			this.type = new LabelingType<>( null, mapping, generation );
		}

		@Override
		public LabelingType< T > get()
		{
			type.setType( source.get() );
			return type;
		}

		@Override
		public LabelingConvertedRandomAccess copy()
		{
			return new LabelingConvertedRandomAccess( source.copyRandomAccess() );
		}
	}

	class LabelingConvertedCursor extends AbstractConvertedCursor< I, LabelingType< T > >
	{
		private final LabelingType< T > type;

		public LabelingConvertedCursor( final Cursor< I > source )
		{
			super( source );
			this.type = new LabelingType<>( null, mapping, generation );
		}

		@Override
		public LabelingType< T > get()
		{
			type.setType( source.get() );
			return type;
		}

		@Override
		public LabelingConvertedCursor copy()
		{
			return new LabelingConvertedCursor( source.copyCursor() );
		}
	}

	@Override
	public RandomAccess< LabelingType< T > > randomAccess()
	{
		return new LabelingConvertedRandomAccess( indexAccessible.randomAccess() );
	}

	@Override
	public RandomAccess< LabelingType< T > > randomAccess( final Interval interval )
	{
		return new LabelingConvertedRandomAccess( indexAccessible.randomAccess( interval ) );
	}

	@Override
	public Cursor< LabelingType< T > > cursor()
	{
		return new LabelingConvertedCursor( indexIterable.cursor() );
	}

	@Override
	public Cursor< LabelingType< T > > localizingCursor()
	{
		return new LabelingConvertedCursor( indexIterable.localizingCursor() );
	}

	@Override
	public LabelingType< T > firstElement()
	{
		return cursor().next();
	}

	@Override
	public Iterator< LabelingType< T > > iterator()
	{
		return cursor();
	}

	@Override
	public long size()
	{
		return indexIterable.size();
	}

	@Override
	public Object iterationOrder()
	{
		return indexIterable.iterationOrder();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean supportsOptimizedCursor( final Interval interval )
	{
		if ( subIterable )
			return ( ( SubIntervalIterable< I > ) indexIterable ).supportsOptimizedCursor( interval );
		else
			return false;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Object subIntervalIterationOrder( final Interval interval )
	{
		if ( subIterable )
			return ( ( SubIntervalIterable< I > ) indexIterable ).subIntervalIterationOrder( interval );
		else
			return new FlatIterationOrder( interval );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Cursor< LabelingType< T > > cursor( final Interval interval )
	{
		final Cursor< I > c;
		if ( subIterable )
			c = ( ( SubIntervalIterable< I > ) indexIterable ).cursor( interval );
		else
			c = Views.interval( indexAccessible, interval ).cursor();
		return new LabelingConvertedCursor( c );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Cursor< LabelingType< T > > localizingCursor( final Interval interval )
	{
		final Cursor< I > c;
		if ( subIterable )
			c = ( ( SubIntervalIterable< I > ) indexIterable ).localizingCursor( interval );
		else
			c = Views.interval( indexAccessible, interval ).localizingCursor();
		return new LabelingConvertedCursor( c );
	}

	/**
	 * @return RandomAccessibleInterval containing the indices
	 */
	public RandomAccessibleInterval< I > getIndexImg()
	{
		return indexAccessible;
	}
}
