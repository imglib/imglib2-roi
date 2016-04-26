package net.imglib2.roi.boundary;

import static net.imglib2.roi.boundary.Boundary.StructuringElement.FOUR_CONNECTED;

import gnu.trove.list.array.TIntArrayList;
import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.Regions;
import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BoolType;

/**
 * An {@link IterableRegion} of the boundary pixels of a source boolean
 * {@link RandomAccessibleInterval}. A pixel is a boundary pixel if
 * <ol>
 * <li>the corresponding source pixel is {@code true} (in the source region),
 * and
 * <li>at least one of its neighbors is {@code false} (not in the source
 * region).
 * </ol>
 * Neighbors are defined by 4-neighborhood or 8-neighborhood (or n-dimensional
 * equivalent) according to the {@link StructuringElement} given in the
 * constructor. Note, that a {@link Boundary} constructed with 4-neighborhood
 * {@link StructuringElement} is 8-connected, and vice versa.
 *
 * @param <T>
 *            BooleanType of the source {@link IterableRegion}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public final class Boundary< T extends BooleanType< T > >
	extends AbstractWrappedInterval< RandomAccessibleInterval< T > > implements IterableRegion< BoolType >
{
	public static enum StructuringElement
	{
		FOUR_CONNECTED,
		EIGHT_CONNECTED
	}

	private StructuringElement structuringElement;

	private final int n;

	private final TIntArrayList coords;

	private final int size;

	public Boundary( final RandomAccessibleInterval< T > region )
	{
		this( region, FOUR_CONNECTED );
	}

	public Boundary( final RandomAccessibleInterval< T > region, final StructuringElement structuringElement )
	{
		super( region );
		this.structuringElement = structuringElement;
		n = region.numDimensions();
		coords = new TIntArrayList();
		final BoundaryConstructor< T > c = new BoundaryConstructor< T >( region, structuringElement );
		while( true )
		{
			c.fwd();
			if ( c.isValid() )
			{
				for ( int d = 0; d < n; ++d )
					coords.add( c.getIntPosition( d ) );
			}
			else
				break;
		}
		size = coords.size() / n;
	}

	@Override
	public long size()
	{
		return size;
	}

	@Override
	public Object iterationOrder()
	{
		return this;
	}

	@Override
	public BoundaryCursor cursor()
	{
		return new BoundaryCursor();
	}

	@Override
	public BoundaryCursor localizingCursor()
	{
		return cursor();
	}

	@Override
	public BoundaryCursor iterator()
	{
		return cursor();
	}

	@Override
	public Void firstElement()
	{
		return cursor().next();
	}

	@Override
	public RandomAccess< BoolType > randomAccess()
	{
		return structuringElement == FOUR_CONNECTED
				? new BoundaryRandomAccess4< T >( sourceInterval )
				: new BoundaryRandomAccess8< T >( sourceInterval );
	}

	@Override
	public RandomAccess< BoolType > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	final class BoundaryCursor extends Point implements Cursor< Void >
	{
		private int i;

		private final int size = coords.size();

		BoundaryCursor()
		{
			super( Boundary.this.numDimensions() );
			i = 0;
		}

		private BoundaryCursor( final BoundaryCursor c )
		{
			i = c.i;
			setPosition( c );
		}

		@Override
		public Void get()
		{
			return null;
		}

		@Override
		public Void next()
		{
			fwd();
			return null;
		}

		@Override
		public boolean hasNext()
		{
			return i < size;
		}

		@Override
		public void fwd()
		{
			for ( int d = 0; d < n; ++d, ++i )
				position[ d ] = coords.getQuick( i );
		}

		@Override
		public void jumpFwd( final long steps )
		{
			i += n * ( steps - 1 );
			fwd();
		}

		@Override
		public void reset()
		{
			i = 0;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public BoundaryCursor copy()
		{
			return new BoundaryCursor( this );
		}

		@Override
		public BoundaryCursor copyCursor()
		{
			return copy();
		}
	}

	static final class BoundaryConstructor< T extends BooleanType< T > > implements Localizable
	{
		private final Cursor< Void > c;

		private final RandomAccess< BoolType > a;

		private boolean valid;

		public BoundaryConstructor( final RandomAccessibleInterval< T > region, final StructuringElement structuringElement )
		{
			c = Regions.iterable( region ).localizingCursor();
			a = structuringElement == FOUR_CONNECTED
					? new BoundaryRandomAccess4< T >( region )
					: new BoundaryRandomAccess8< T >( region );
		}

		public void fwd()
		{
			while ( c.hasNext() )
			{
				c.fwd();
				a.setPosition( c );
				if ( a.get().get() )
				{
					valid = true;
					return;
				}
			}
			valid = false;
		}

		public boolean isValid()
		{
			return valid;
		}

		@Override
		public void localize( final float[] position )
		{
			c.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			c.localize( position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return c.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return c.getDoublePosition( d );
		}

		@Override
		public int numDimensions()
		{
			return c.numDimensions();
		}

		@Override
		public void localize( final int[] position )
		{
			c.localize( position );
		}

		@Override
		public void localize( final long[] position )
		{
			c.localize( position );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return c.getIntPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return c.getLongPosition( d );
		}
	}
}
