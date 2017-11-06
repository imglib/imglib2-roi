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
package net.imglib2.roi.labeling;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.util.iterationcode.IterationCodeBuilder;
import net.imglib2.view.Views;

/**
 * Provides {@link LabelRegion}s for all labels of a labeling.
 *
 * <p>
 * A labeling can be for example an {@link ImgLabeling} or a view onto an
 * {@link ImgLabeling}.
 *
 * <p>
 * Changes to the underlying labeling are correctly reflected.
 * {@link LabelRegions} is thread-safe, however, concurrently modifying the
 * labeling and accessing the {@link LabelRegions} has undefined results.
 *
 * @param <T>
 *            the label type
 *
 * @author Tobias Pietzsch
 */
public class LabelRegions< T > extends AbstractEuclideanSpace implements Iterable< LabelRegion< T > >
{
	final RandomAccessibleInterval< LabelingType< T > > labeling;

	private final LabelingType< T > type;

	private final ArrayList< FragmentProperties > indexToFragmentProperties;

	/**
	 * Maps labels to {@link LabelRegionProperties} for all currently non-empty labels in the labeling.
	 */
	private final HashMap< T, LabelRegionProperties > labelToLabelRegionProperties;

	/**
	 * Maps labels to {@link LabelRegionProperties} for all labels that were
	 * ever seen. This is maintained to be able to "resurrect"
	 * {@link LabelRegionProperties}: If a label becomes empty, its
	 * {@link LabelRegionProperties} will be removed from the live map
	 * {@link #labelToLabelRegionProperties}. If the label later becomes
	 * non-empty again we can add the old {@link LabelRegionProperties} back
	 * into the live map, meaning that {@link LabelRegion}s that reference it
	 * will be updated correctly.
	 */
	private final HashMap< T, LabelRegionProperties > allLabelToLabelRegionProperties;

	/**
	 * maintains "canonical" {@link LabelRegion}s that were created by
	 * {@link #getLabelRegion(Object)} or by {@link #iterator()} such that these
	 * will not be re-created on subsequent calls.
	 *
	 * <p>
	 * TODO: I'm not sure whether this is the best behavior. Maybe it is better
	 * to always create new {@link LabelRegion}s, which are not re-positioned or
	 * have had their origin changed.
	 */
	private final HashMap< T, LabelRegion< T > > labelToLabelRegion;

	private int expectedGeneration;

	public LabelRegions( final RandomAccessibleInterval< LabelingType< T > > labeling )
	{
		super( labeling.numDimensions() );
		this.labeling = labeling;
		type = Views.iterable( labeling ).firstElement();
		indexToFragmentProperties = new ArrayList< FragmentProperties >();
		labelToLabelRegionProperties = new HashMap< T, LabelRegionProperties >();
		allLabelToLabelRegionProperties = new HashMap< T, LabelRegionProperties >();
		labelToLabelRegion = new HashMap< T, LabelRegion< T > >();
		expectedGeneration = type.getGeneration() - 1;
	}

	public LabelRegion< T > getLabelRegion( final T label )
	{
		update();
		LabelRegion< T > labelRegion = labelToLabelRegion.get( label );
		if ( labelRegion == null )
		{
			labelRegion = new LabelRegion< T >( this, labelToLabelRegionProperties.get( label ), label );
			labelToLabelRegion.put( label, labelRegion );
		}
		return labelRegion;
	}

	/**
	 * Get the set of labels which actually occur at some pixel in the labeling.
	 * (This is a subset of all labels defined in the {@link LabelingMapping}.)
	 */
	public Set< T > getExistingLabels()
	{
		update();
		return labelToLabelRegionProperties.keySet();
	}

	@Override
	public Iterator< LabelRegion< T > > iterator()
	{
		update();
		final Iterator< T > labelIterator = labelToLabelRegionProperties.keySet().iterator();
		return new Iterator< LabelRegion<T> >()
		{
			@Override
			public boolean hasNext()
			{
				return labelIterator.hasNext();
			}

			@Override
			public LabelRegion< T > next()
			{
				final T label = labelIterator.next();
				LabelRegion< T > labelRegion = labelToLabelRegion.get( label );
				if ( labelRegion == null )
				{
					labelRegion = new LabelRegion< T >( LabelRegions.this, labelToLabelRegionProperties.get( label ), label );
					labelToLabelRegion.put( label, labelRegion );
				}
				return labelRegion;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	static final class FragmentProperties extends IterationCodeBuilder
	{
		private final int index;

		private final long[] sumPositions;

		public < T > FragmentProperties( final int index, final RandomAccessibleInterval< LabelingType< T > > labeling )
		{
			super( labeling.numDimensions(), labeling.min( 0 ) );
			this.index = index;
			this.sumPositions = new long[ n ];
		}

		/**
		 * Get the index value (see {@link LabelingMapping}) representing the set of
		 * labels of this fragment. The fragment is made up of all pixels having
		 * exactly this set of labels.
		 *
		 * @return index value representing the label set of this fragment.
		 */
		public int getIndex()
		{
			return index;
		}

		public long[] getSumPositions()
		{
			return sumPositions;
		}

		@Override
		public void add( final Localizable pos )
		{
			super.add( pos );
			for ( int d = 0; d < n; ++d )
				sumPositions[ d ] += pos.getLongPosition( d );
		}
	}

	static final class LabelRegionProperties extends AbstractEuclideanSpace
	{
		private long size;

		private final long[] sumPositions;

		private final double[] centerOfMass;

		private final long[] bbmin;

		private final long[] bbmax;

		private final ArrayList< TIntArrayList > itcodes;

		private final LabelRegions< ? > labelRegions;

		LabelRegionProperties( final LabelRegions< ? > labelRegions )
		{
			super( labelRegions.numDimensions() );
			this.labelRegions = labelRegions;
			sumPositions = new long[ n ];
			centerOfMass = new double[ n ];
			bbmin = new long[ n ];
			bbmax = new long[ n ];
			itcodes = new ArrayList< TIntArrayList >();
			reset();
		}

		/**
		 * Compute all statistics on the labels if cache is dirty. Returns the
		 * generation (modification count of the labeling) for which the update
		 * was computed. Getting the same generation from two consecutive
		 * invocations of {@link #update()} means that there was no update
		 * necessary in the second invocation.
		 *
		 * @return the current generation.
		 */
		int update()
		{
			return labelRegions.update();
		}

		void reset()
		{
			size = 0;
			Arrays.fill( sumPositions, 0 );
			Arrays.fill( centerOfMass, 0 );
			Arrays.fill( bbmin, Long.MAX_VALUE );
			Arrays.fill( bbmax, Long.MIN_VALUE );
			itcodes.clear();
		}

		void add( final FragmentProperties frag )
		{
			size += frag.getSize();

			final long[] fragSumPositions = frag.getSumPositions();
			for ( int d = 0; d < n; ++d )
				sumPositions[ d ] += fragSumPositions[ d ];

			final long[] fragBBMin = frag.getBoundingBoxMin();
			final long[] fragBBMax = frag.getBoundingBoxMax();
			for ( int d = 0; d < n; d++ )
			{
				if ( fragBBMin[ d ] < bbmin[ d ] )
					bbmin[ d ] = fragBBMin[ d ];
				if ( fragBBMax[ d ] > bbmax[ d ] )
					bbmax[ d ] = fragBBMax[ d ];
			}

			itcodes.add( frag.getItcode() );
		}

		void finish()
		{
			if ( size != 0 )
				for ( int d = 0; d < n; d++ )
					centerOfMass[ d ] = ( double ) sumPositions[ d ] / ( double ) size;
		}

		long getSize()
		{
			return size;
		}

		long[] getSumPositions()
		{
			return sumPositions;
		}

		double[] getCenterOfMass()
		{
			return centerOfMass;
		}

		long[] getBoundingBoxMin()
		{
			return bbmin;
		}

		long[] getBoundingBoxMax()
		{
			return bbmax;
		}

		ArrayList< TIntArrayList > getItcodes()
		{
			return itcodes;
		}
	}

	/**
	 * Compute all statistics on the labels if cache is dirty. Returns the
	 * generation (modification count of the labeling) for which the update was
	 * computed. Getting the same generation from two consecutive invocations of
	 * {@link #update()} means that there was no update necessary in the second
	 * invocation.
	 *
	 * @return the current generation.
	 */
	private int update()
	{
		if ( type.getGeneration() != expectedGeneration )
		{
			synchronized ( this )
			{
				final int generation = type.getGeneration();
				if ( generation != expectedGeneration )
				{
					for ( final LabelRegionProperties props : allLabelToLabelRegionProperties.values() )
						props.reset();

					// remember existing LabelRegions created on previous getLabelRegion() or iterator()
					final HashMap< T, LabelRegion< T > > oldLabelToLabelRegion = new HashMap< T, LabelRegion< T > >( labelToLabelRegion );

					indexToFragmentProperties.clear();
					labelToLabelRegionProperties.clear();
					labelToLabelRegion.clear();

					final LabelingMapping< T > mapping = type.getMapping();
					final int numFragments = mapping.numSets();
					for ( int i = 0; i < numFragments; ++i )
						indexToFragmentProperties.add( new FragmentProperties( i, labeling ) );

					final Cursor< ? extends LabelingType< ? > > c = Views.flatIterable( labeling ).localizingCursor();
					while ( c.hasNext() )
					{
						final int index = c.next().getIndex().getInteger();
						// TODO: Do a benchmark: For sparsely labeled images it
						// might be faster to use a non-localizing Cursor, because
						// we don't collect background coordinates. What is the
						// trade-off?
						if ( index > 0 )
							indexToFragmentProperties.get( index ).add( c );
					}
					// generation = type.getGeneration();
					for ( final FragmentProperties frag : indexToFragmentProperties )
						frag.finish();

					// now build LabelProperties
					for ( final FragmentProperties frag : indexToFragmentProperties )
					{
						if ( frag.getSize() <= 0 )
							continue;

						final Set< T > fragLabels = mapping.labelsAtIndex( frag.getIndex() );
						for ( final T label : fragLabels )
						{
							LabelRegionProperties props = labelToLabelRegionProperties.get( label );
							if ( props == null )
							{
								props = allLabelToLabelRegionProperties.get( label );
								if ( props == null )
								{
									props = new LabelRegionProperties( this );
									allLabelToLabelRegionProperties.put( label, props );
								}
								labelToLabelRegionProperties.put( label, props );
							}
							props.add( frag );
						}
					}

					for ( final Entry< T, LabelRegionProperties > entry : labelToLabelRegionProperties.entrySet() )
					{
						final T label = entry.getKey();
						final LabelRegionProperties props = entry.getValue();
						props.finish();

						// remember existing LabelRegions created on previous getLabelRegion() or iterator()
						final LabelRegion< T > labelRegion = oldLabelToLabelRegion.get( label );
						if ( labelRegion != null )
							labelToLabelRegion.put( label, labelRegion );
					}

					oldLabelToLabelRegion.clear();

					// call recursively in case there were more updates in the meantime
					expectedGeneration = generation;
					update();
				}
			}
		}
		return expectedGeneration;
	}
}
