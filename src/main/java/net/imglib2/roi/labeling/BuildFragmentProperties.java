package net.imglib2.roi.labeling;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import net.imglib2.AbstractInterval;
import net.imglib2.AbstractLocalizableInt;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.roi.IterableRegion;
import net.imglib2.type.logic.BoolType;
import net.imglib2.view.Views;

public class BuildFragmentProperties
{
	private final Labeling< ? > labeling;

	private final ArrayList< FragmentProperties > indexToFragmentProperties;

	static class FragmentCursor extends AbstractLocalizableInt implements Cursor< BoolType >
	{
		private final BoolType type = new BoolType( true );

		private final TIntArrayList itcode;

		private int itcodeIndex;

		private int maxX;

		private boolean hasNextRaster;

		public FragmentCursor( final int numDimensions, final TIntArrayList itcode )
		{
			super( numDimensions );
			this.itcode = itcode;
			reset();
		}

		protected FragmentCursor( final FragmentCursor c )
		{
			super( c.n );
			itcode = c.itcode;
			itcodeIndex = c.itcodeIndex;
			maxX = c.maxX;
			hasNextRaster = c.hasNextRaster;
			System.arraycopy( position, 0, c.position, 0, n );
		}

		private void nextRasterStretch()
		{
			int minX = itcode.get( itcodeIndex++ );
			if ( minX < 0 )
			{
				for ( int d = 1; d <= -minX; ++d )
					position[ d ] = itcode.get( itcodeIndex++ );
				minX = itcode.get( itcodeIndex++ );
			}
			maxX = itcode.get( itcodeIndex++ );
			position[ 0 ] = minX;
			hasNextRaster = itcodeIndex < itcode.size();
		}

		@Override
		public BoolType get()
		{
			return type;
		}

		@Override
		public void jumpFwd( final long steps )
		{
			for ( long j = 0; j < steps; ++j )
				fwd();
		}

		@Override
		public void fwd()
		{
			if ( ++position[ 0 ] > maxX )
				nextRasterStretch();
		}

		@Override
		public void reset()
		{
			itcodeIndex = 0;
			hasNextRaster = itcodeIndex < itcode.size();
			if ( hasNextRaster )
			{
				for ( int d = 1; d < n; ++d )
					position[ d ] = itcode.get( itcodeIndex++ );
				position[ 0 ] = itcode.get( itcodeIndex++ ) - 1;
				maxX = itcode.get( itcodeIndex++ );
			}
			else
			{
				position[ 0 ] = 0;
				maxX = 0;
			}
		}

		@Override
		public boolean hasNext()
		{
			return hasNextRaster || ( position[ 0 ] < maxX );
		}

		@Override
		public BoolType next()
		{
			fwd();
			return get();
		}

		@Override
		public void remove()
		{
			// NB: no action.
		}

		@Override
		public FragmentCursor copy()
		{
			return new FragmentCursor( this );
		}

		@Override
		public FragmentCursor copyCursor()
		{
			return copy();
		}
	}

	static class IterableFragment extends AbstractInterval implements IterableRegion< BoolType >
	{
		private final FragmentProperties frag;

		public IterableFragment( final FragmentProperties frag )
		{
			super( frag.bbmin, frag.bbmax );
			this.frag = frag;
		}

		@Override
		public FragmentCursor cursor()
		{
			return new FragmentCursor( n, frag.itcode );
		}

		@Override
		public FragmentCursor localizingCursor()
		{
			return cursor();
		}

		@Override
		public long size()
		{
			return frag.size;
		}

		@Override
		public BoolType firstElement()
		{
			return cursor().next();
		}

		@Override
		public Object iterationOrder()
		{
			return this;
		}

		@Override
		public FragmentCursor iterator()
		{
			return cursor();
		}

	}

	static class FragmentProperties
	{
		private final int n;

		private final int index;

		private int size;

		private final TIntArrayList itcode;

		private int[] prev;

		private int[] curr;

		private final long[] bbmin;

		private final long[] bbmax;

		public FragmentProperties( final int index, final int numDimensions )
		{
			this.index = index;
			n = numDimensions;
			size = 0;
			itcode = new TIntArrayList();
			prev = new int[ n ];
			curr = new int[ n ];
			bbmin = new long[ n ];
			bbmax = new long[ n ];
			Arrays.fill( bbmin, Long.MAX_VALUE );
			Arrays.fill( bbmax, Long.MIN_VALUE );
		}

		private boolean startedRasterization = false;

		private int rasterBegin = 0;

		private void endRaster( final int badDimension )
		{
			itcode.add( rasterBegin );
			itcode.add( prev[ 0 ] );
			if ( badDimension > 0 )
			{
				itcode.add( -badDimension );
				for ( int d = 1; d <= badDimension; ++d )
					itcode.add( curr[ d ] );
			}
			rasterBegin = curr[ 0 ];
		}

		private void add( final Localizable l )
		{
			++size;
			l.localize( curr );
			for ( int d = 0; d < n; d++ )
			{
				if ( curr[ d ] < bbmin[ d ] )
					bbmin[ d ] = curr[ d ];
				if ( curr[ d ] > bbmax[ d ] )
					bbmax[ d ] = curr[ d ];
			}
			if ( startedRasterization )
			{
				for ( int d = n - 1; d >= 0; --d )
				{
					if ( d == 0 )
					{
						if ( curr[ 0 ] != prev[ 0 ] + 1 )
						{
							endRaster( 0 );
							break;
						}
					}
					else if ( curr[ d ] != prev[ d ] )
					{
						endRaster( d );
						break;
					}
				}
			}
			else
			{
				for ( int d = 1; d < n; ++d )
					itcode.add( curr[ d ] );
				rasterBegin = curr[ 0 ];
				startedRasterization = true;
			}
			// swap prev and curr pos arrays
			final int[] tmp = prev;
			prev = curr;
			curr = tmp;
		}

		private void done()
		{
			if ( startedRasterization )
			{
				itcode.add( rasterBegin );
				itcode.add( prev[ 0 ] );
			}
		}
	}

	public BuildFragmentProperties( final Labeling< ? > labeling )
	{
		this.labeling = labeling;

		final LabelingMapping< ? > mapping = labeling.getMapping();
		final int numFragments = mapping.numSets();
		indexToFragmentProperties = new ArrayList< FragmentProperties >( numFragments );
		for ( int i = 0; i < numFragments; ++i )
			indexToFragmentProperties.add( new FragmentProperties( i, labeling.numDimensions() ) );

		final Cursor< ? extends LabelingType< ? > > c = Views.flatIterable( labeling ).localizingCursor();
		while ( c.hasNext() )
		{
			final int index = c.next().getIndex().getInteger();
			if ( index == 1 )
			{
				final FragmentProperties fp = indexToFragmentProperties.get( index );
//				if ( c.getIntPosition( 0 ) == 99 )
//					System.out.println();
				fp.add( c );
			}
			else
			{
				indexToFragmentProperties.get( index ).add( c );
			}
		}
//		generation = type.getGeneration();
		for ( final FragmentProperties frag : indexToFragmentProperties )
			frag.done();
	}

	public IterableRegion< BoolType > getFragment( final int index )
	{
		return new IterableFragment( indexToFragmentProperties.get( index ) );
	}
}
