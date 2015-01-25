package net.imglib2.roi.labeling;

import java.util.HashMap;
import java.util.Map;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.view.Views;

/**
 * Provides {@link LabelRegion}s for all labels of a {@link Labeling}.
 *
 * @param <T>
 *            the label type
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class LabelRegions< T >
{
	// TODO: add back area and raster start computation (removed until needed)
	public static class LabelStatistics
	{
		// numDimensions
		private final int n;

		private final BoundingBox boundingBox;

		private long area;

		private final long[] centerOfMass;

		public LabelStatistics( final int n )
		{
			this.n = n;
			boundingBox = new BoundingBox( n );
			area = 0;
			centerOfMass = new long[ n ];
		}

		/**
		 * update with the given coordinates.
		 */
		protected void update( final Localizable position )
		{
			boundingBox.update( position );
			area += 1;
			for ( int d = 0; d < n; ++d )
				centerOfMass[ d ] += position.getLongPosition( d );
		}

		protected void allUpdatesDone()
		{
			for ( int d = 0; d < n; ++d )
				centerOfMass[ d ] /= area;
		}

		public BoundingBox getBoundingBox()
		{
			return boundingBox;
		}

		public long getArea()
		{
			return area;
		}

		public Localizable getCenterOfMass()
		{
			return Point.wrap( centerOfMass );
		}
	}

	protected final RandomAccessibleInterval< LabelingType< T > > labeling;

	protected final LabelingType< T > type;

	protected int generation;

	protected Map< T, LabelStatistics > statistics;

	public LabelRegions( final RandomAccessibleInterval< LabelingType< T > > labeling )
	{
		this.labeling = labeling;
		type = Views.iterable( labeling ).firstElement();
		generation = type.getGeneration() - 1;
	}

	/**
	 * Get {@link LabelRegion} for the specified label, backed by the labeling.
	 */
	public LabelRegion< T > getLabelRegion( final T label )
	{
		return new LabelRegion< T >( this, label );
	}

	public LabelStatistics getStatistics( final T label )
	{
		computeStatistics();
		return statistics.get( label );
	}

	// TODO: This can be made faster for NativeImgLabeling: First collect
	// statistics for all label combinations, then aggregate LabelStatistics
	// accordingly.
	/**
	 * Compute all statistics on the labels if cache is dirty.
	 */
	protected synchronized void computeStatistics()
	{
		if ( type.getGeneration() != generation )
		{
			statistics = new HashMap< T, LabelStatistics >();
			LabelStatistics last = null;
			T lastLabel = null;
			final Cursor< LabelingType< T > > c = Views.iterable( labeling ).localizingCursor();
			while ( c.hasNext() )
			{
				final LabelingType< T > type = c.next();
				for ( final T label : type )
				{
					if ( !label.equals( lastLabel ) )
					{
						lastLabel = label;
						last = statistics.get( label );
						if ( last == null )
						{
							last = new LabelStatistics( labeling.numDimensions() );
							statistics.put( label, last );
						}
					}
					last.update( c );
				}
			}
			generation = type.getGeneration();
			for ( final LabelStatistics s : statistics.values() )
				s.allUpdatesDone();
		}
	}
}
