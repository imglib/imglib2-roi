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
 * Provides {@link LabelRegion}s for all labels of a {@link Labeling}.
 *
 * @param <T>
 *            the label type
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class LabelRegions< T > extends AbstractEuclideanSpace implements Iterable< LabelRegion< T > >
{
	final RandomAccessibleInterval< LabelingType< T > > labeling;

	private final LabelingType< T > type;

	private final ArrayList< FragmentProperties > indexToFragmentProperties;

	private final HashMap< T, LabelRegionProperties > labelToLabelRegionProperties;

	private final HashMap< T, LabelRegion< T > > labelToLabelRegion;

	private int expectedGeneration;

	public LabelRegions( final RandomAccessibleInterval< LabelingType< T > > labeling )
	{
		super( labeling.numDimensions() );
		this.labeling = labeling;
		type = Views.iterable( labeling ).firstElement();
		indexToFragmentProperties = new ArrayList< FragmentProperties >();
		labelToLabelRegionProperties = new HashMap< T, LabelRegionProperties >();
		labelToLabelRegion = new HashMap< T, LabelRegion< T > >();
		expectedGeneration = Views.iterable( labeling ).firstElement().getGeneration() - 1;
	}

	public LabelRegion< T > getLabelRegion( final T label )
	{
		computeStatistics();
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
		computeStatistics();
		return labelToLabelRegionProperties.keySet();
	}

	@Override
	public Iterator< LabelRegion< T > > iterator()
	{
		computeStatistics();
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
		 * @return true iff there was an update
		 */
		boolean updateIfNecessary()
		{
			return labelRegions.computeStatistics();
		}

		void reset()
		{
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
			final long[] fragBBMax = frag.getBoundingBoxMin();
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
	 * Compute all statistics on the labels if cache is dirty.
	 *
	 * @return true iff statistics were recomputed.
	 */
	private synchronized boolean computeStatistics()
	{
		if ( type.getGeneration() != expectedGeneration )
		{
			expectedGeneration = type.getGeneration();

			final HashMap< T, LabelRegionProperties > oldLabelToLabelRegionProperties = new HashMap< T, LabelRegionProperties >( labelToLabelRegionProperties );
			for ( final LabelRegionProperties props : oldLabelToLabelRegionProperties.values() )
				props.reset();
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
				indexToFragmentProperties.get( index ).add( c );
			}
	//		generation = type.getGeneration();
			for ( final FragmentProperties frag : indexToFragmentProperties )
				frag.finish();

			// now build LabelProperties
			for ( final FragmentProperties frag : indexToFragmentProperties )
			{
				final Set< T > fragLabels = mapping.labelsAtIndex( frag.getIndex() );
				for ( final T label : fragLabels )
				{
					LabelRegionProperties props = labelToLabelRegionProperties.get( label );
					if ( props == null )
					{
						props = oldLabelToLabelRegionProperties.get( label );
						if ( props == null )
							props = new LabelRegionProperties( this );
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
				final LabelRegion< T > labelRegion = oldLabelToLabelRegion.get( label );
				if ( labelRegion != null )
					labelToLabelRegion.put( label, labelRegion );
			}

			oldLabelToLabelRegionProperties.clear();
			oldLabelToLabelRegion.clear();
			computeStatistics(); // call recursively in case there were more updates in the meantime
			return true;
		}
		return false;
	}

	// TODO should not be public
	public IterableFragment getFragment( final int index )
	{
		computeStatistics();
		return new IterableFragment( indexToFragmentProperties.get( index ) );
	}
}
