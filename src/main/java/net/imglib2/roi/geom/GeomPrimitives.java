package net.imglib2.roi.geom;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealPoint;

/**
 * Collection of static utilities related to iterating over geometry primitives.
 * 
 * @author Jean-Yves Tinevez
 *
 */
public class GeomPrimitives
{

	/**
	 * Returns an iterable that will iterate exactly once over all the integer
	 * locations on a line between in proper order from the specified start to
	 * the specified end points, included.
	 * <p>
	 * This implementation uses floating-point logic instead of the pure integer
	 * logic of Bresenham line (<a href=
	 * "https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm">Wikipedia</a>)
	 * but the results are quasi identical and the performance penalty small.
	 * 
	 * @param start
	 *            the start position.
	 * @param end
	 *            the end position.
	 * @return a new iterable.
	 */
	public static final Iterable< Localizable > line( final Localizable start, final Localizable end )
	{
		if ( start.numDimensions() != end.numDimensions() )
			throw new IllegalArgumentException( "Start and end points do not have the same number of dimensions." );
		if ( start.numDimensions() < 1 )
			throw new IllegalArgumentException( "Start and end points have 0 dimensions." );

		return new Iterable< Localizable >()
		{

			@Override
			public Iterator< Localizable > iterator()
			{
				return new LineIterator( start, end );
			}
		};
	}

	/**
	 * Returns a cursor that will iterate over the specified
	 * {@link RandomAccessible} at the locations given by the iterable, in the
	 * order they are iterated.
	 * 
	 * @param randomAccessible
	 *            the {@link RandomAccessible} to iterate over.
	 * @param iterable
	 *            an iterable that returns locations to iterate.
	 * @return a new cursor.
	 */
	public static final < T > Cursor< T > cursor( final RandomAccessible< T > randomAccessible, final Iterable< Localizable > iterable )
	{
		return new MyCursor< T >( randomAccessible, iterable );
	}

	private static final class LineIterator implements Iterator< Localizable >
	{

		private final RealPoint increment;

		private final RealPoint next;

		private final Point current;

		private final long nPoints;

		private final int n;

		private long index;

		public LineIterator( final Localizable start, final Localizable end )
		{
			this.n = start.numDimensions();

			final Point diff = new Point( n );
			long maxDiff = -1;
			for ( int d = 0; d < n; d++ )
			{
				final long dx = end.getLongPosition( d ) - start.getLongPosition( d );
				diff.setPosition( dx, d );
				if ( Math.abs( dx ) > maxDiff )
					maxDiff = Math.abs( dx );
			}
			this.nPoints = maxDiff;

			this.increment = new RealPoint( n );
			for ( int d = 0; d < n; d++ )
				increment.setPosition( diff.getDoublePosition( d ) / maxDiff, d );

			this.index = -1;
			this.current = new Point( start.numDimensions() );
			this.next = new RealPoint( start.numDimensions() );
			next.setPosition( start );
		}

		@Override
		public boolean hasNext()
		{
			return index < nPoints;
		}

		@Override
		public Localizable next()
		{
			index++;
			if ( index < 0 || index > nPoints )
				throw new NoSuchElementException();

			for ( int d = 0; d < n; d++ )
				current.setPosition( Math.round( next.getDoublePosition( d ) ), d );

			for ( int d = 0; d < next.numDimensions(); d++ )
				next.move( increment.getDoublePosition( d ), d );

			return current;
		}
	}

	private static final class MyCursor< T > extends AbstractCursor< T > implements Cursor< T >
	{

		private final RandomAccess< T > ra;

		private Iterator< Localizable > it;

		private final Iterable< Localizable > iterable;

		private final RandomAccessible< T > randomAccessible;

		private long index;

		public MyCursor( final RandomAccessible< T > randomAccessible, final Iterable< Localizable > iterable )
		{
			super( randomAccessible.numDimensions() );
			this.randomAccessible = randomAccessible;
			this.iterable = iterable;
			final int n = randomAccessible.numDimensions();
			/*
			 * Try to be clever (is this a good idea?) and determine the box in
			 * which we will iterate before creating the randomAccess.
			 */
			final long[] min = new long[ n ];
			Arrays.fill( min, Long.MAX_VALUE );
			final long[] max = new long[ n ];
			Arrays.fill( max, Long.MIN_VALUE );
			for ( final Localizable p : iterable )
			{
				for ( int d = 0; d < n; d++ )
				{
					final long x = p.getLongPosition( d );
					if ( x < min[ d ] )
						min[ d ] = x;
					if ( x > max[ d ] )
						max[ d ] = x;
				}
			}
			this.ra = randomAccessible.randomAccess( new FinalInterval( min, max ) );
			reset();
		}

		@Override
		public T get()
		{
			return ra.get();
		}

		@Override
		public void fwd()
		{
			ra.setPosition( it.next() );
			index++;
		}

		@Override
		public void jumpFwd( long steps )
		{
			if ( steps < 1 )
				return;
			while ( steps > 1 )
			{
				steps--;
				it.next();
				index++;
			}
			fwd();
		}

		@Override
		public void reset()
		{
			it = iterable.iterator();
			index = 0;
		}

		@Override
		public boolean hasNext()
		{
			return it.hasNext();
		}

		@Override
		public void localize( final long[] position )
		{
			ra.localize( position );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return ra.getLongPosition( d );
		}

		@Override
		public AbstractCursor< T > copy()
		{
			final AbstractCursor< T > copy = copyCursor();
			copy.jumpFwd( index );
			return copy;
		}

		@Override
		public AbstractCursor< T > copyCursor()
		{
			return new MyCursor< T >( randomAccessible, iterable );
		}

	}

	private GeomPrimitives()
	{}
}
