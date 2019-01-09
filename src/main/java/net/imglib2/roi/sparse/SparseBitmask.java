package net.imglib2.roi.sparse;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.img.basictypeaccess.BooleanAccess;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.sparse.SparseBitmaskNTree.Node;
import net.imglib2.roi.sparse.SparseBitmaskNTree.NodeIterator;
import net.imglib2.roi.sparse.util.DefaultInterval;
import net.imglib2.type.logic.NativeBoolType;
import net.imglib2.util.Intervals;

/**
 * A (unbounded) {@code RandomAccessible<NativeBoolType>}.
 * <p>
 * Use {@link #region()} to obtain a (read-only) view as an
 * {@link IterableRegion}.
 *
 * @author Tobias Pietzsch
 */
public class SparseBitmask implements RandomAccessible< NativeBoolType >
{
	private final GrowableTree tree;

	private final ReentrantReadWriteLock lock;

	private int modCount;

	public SparseBitmask(
			final int numDimensions )
	{
		final int[] leafDims = new int[ numDimensions ];
		Arrays.fill( leafDims, 8 );
		tree = new GrowableTree( leafDims );
		lock = new ReentrantReadWriteLock();
	}

	public SparseBitmaskNTree tree()
	{
		return tree;
	}

	/**
	 * Get a view of this {@code SparseBitmask} as an {@code IterableRegion}.
	 * The view is read-only, and is only valid while the bitmask is not modified.
	 * After writing to the bitmask, using the {@code IterableRegion} results in {@link ConcurrentModificationException}.
	 *
	 * @return a read-only view of this {@code SparseBitmask}.
	 */
	public IterableRegion< NativeBoolType > region()
	{
		return new Region();
	}

	/*
	 *
	 * RandomAccessible< NativeBoolType >
	 *
	 */

	@Override
	public int numDimensions()
	{
		return tree.numDimensions();
	}

	@Override
	public RandomAccess< NativeBoolType > randomAccess()
	{
		return new RA();
	}

	@Override
	public RandomAccess< NativeBoolType > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	private class Access implements BooleanAccess
	{
		private final long[] tmp;

		private final long[] position;

		Access( final long[] position )
		{
			this.position = position;
			tmp = new long[ position.length ];
		}

		@Override
		public boolean getValue( final int index )
		{
			try
			{
				lock.readLock().lock();
				return tree.get( position, tmp );
			}
			finally
			{
				lock.readLock().unlock();
			}
		}

		@Override
		public void setValue( final int index, final boolean value )
		{
			try
			{
				lock.writeLock().lock();
				tree.set( position, tmp, value );
				++modCount;
			}
			finally
			{
				lock.writeLock().unlock();
			}
		}
	}

	private class RA extends Point implements RandomAccess< NativeBoolType >
	{
		private final NativeBoolType type;

		RA()
		{
			super( tree.numDimensions() );
			type = new NativeBoolType( new Access( position ) );
		}

		@Override
		public NativeBoolType get()
		{
			return type;
		}

		@Override
		public RA copyRandomAccess()
		{
			return copy();
		}

		@Override
		public RA copy()
		{
			final RA copy = new RA();
			localize( copy.position );
			return copy;
		}
	}

	/*
	 *
	 * IterableRegion< NativeBoolType >
	 *
	 */

	private class ReadOnlyAccess implements BooleanAccess
	{
		private final long[] tmp;

		private final long[] position;

		ReadOnlyAccess( final long[] position )
		{
			this.position = position;
			tmp = new long[ position.length ];
		}

		@Override
		public boolean getValue( final int index )
		{
			return tree.get( position, tmp );
		}

		@Override
		public void setValue( final int index, final boolean value )
		{
			throw new UnsupportedOperationException();
		}
	}

	private class Region implements IterableRegion< NativeBoolType >, DefaultInterval
	{
		private Interval bbox;

		private long size = -1;

		private int expectedModCount = modCount;

		private void checkForComodification()
		{
			if ( modCount != expectedModCount )
				throw new ConcurrentModificationException();
		}

		private void checkModifications()
		{
			bbox = SparseBitmaskNTree.bbox( tree );
			size = SparseBitmaskNTree.size( tree );
			expectedModCount = modCount;
		}

		@Override
		public int numDimensions()
		{
			return tree.numDimensions();
		}

		@Override
		public Cursor< Void > cursor()
		{
			checkForComodification();
			return new TrueCursor();
		}

		@Override
		public Cursor< Void > localizingCursor()
		{
			return cursor();
		}

		@Override
		public long size()
		{
			checkForComodification();
			if ( size < 0 )
				size = SparseBitmaskNTree.size( tree );
			return size;
		}

		@Override
		public Void firstElement()
		{
			return null;
		}

		@Override
		public Object iterationOrder()
		{
			return this;
		}

		@Override
		public Iterator< Void > iterator()
		{
			return cursor();
		}

		@Override
		public long min( final int d )
		{
			checkForComodification();
			if ( bbox == null )
				bbox = SparseBitmaskNTree.bbox( tree );
			return bbox.min( d );
		}

		@Override
		public long max( final int d )
		{
			checkForComodification();
			if ( bbox == null )
				bbox = SparseBitmaskNTree.bbox( tree );
			return bbox.max( d );
		}

		@Override
		public long dimension( final int d )
		{
			checkForComodification();
			if ( bbox == null )
				bbox = SparseBitmaskNTree.bbox( tree );
			return bbox.dimension( d );
		}

		@Override
		public RandomAccess< NativeBoolType > randomAccess()
		{
			return new RA();
		}

		@Override
		public RandomAccess< NativeBoolType > randomAccess( final Interval interval )
		{
			return randomAccess();
		}

		private class RA extends Point implements RandomAccess< NativeBoolType >
		{
			private final NativeBoolType type;

			RA()
			{
				super( tree.numDimensions() );
				type = new NativeBoolType( new ReadOnlyAccess( position ) );
			}

			@Override
			public NativeBoolType get()
			{
				checkForComodification();
				return type;
			}

			@Override
			public RA copyRandomAccess()
			{
				return copy();
			}

			@Override
			public RA copy()
			{
				final RA copy = new RA();
				localize( copy.position );
				return copy;
			}

		}

		private class TrueCursor extends AbstractLocalizable implements Cursor< Void >
		{
			private final NodeIterator iter;

			private final long[] nodeMin;

			private final long[] nodeMax;

			private Node nextNode;

			private int currentSize;

			private int currentIndex;

			private BitMask currentMask;

			TrueCursor()
			{
				super( tree.numDimensions() );
				iter = tree.iterator();
				nodeMin = new long[ n ];
				nodeMax = new long[ n ];
				nextNode = nextNonEmptyNode();
				currentSize = 0;
				currentIndex = 0;
			}

			private TrueCursor( final TrueCursor other )
			{
				super( other.numDimensions() );
				other.localize( position );
				iter = other.iter.copy();
				nodeMin = other.nodeMin.clone();
				nodeMax = other.nodeMax.clone();
				nextNode = iter.current();
				currentSize = other.currentSize;
				currentIndex = other.currentIndex;
				currentMask = other.currentMask;
			}

			private Node nextNonEmptyNode()
			{
				while ( iter.hasNext() )
				{
					final Node nd = iter.next();
					if ( ( !nd.hasChildren() && nd.value() ) || nd.hasBitMask() )
						return nd;
				}
				return null;
			}

			@Override
			public void reset()
			{
				iter.reset();
				nextNode = nextNonEmptyNode();
				currentIndex = 0;
				currentSize = 0;
			}

			@Override
			public boolean hasNext()
			{
				return currentIndex < currentSize || nextNode != null;
			}

			@Override
			public void fwd()
			{
				checkForComodification();
				if ( ++currentIndex < currentSize )
				{
					if ( currentMask == null )
						advance();
					else
						do
							advance();
						while ( !currentMask.get( position ) );
				}
				else
				{
					if ( nextNode != null )
					{
						nextNode.interval().min( nodeMin );
						nextNode.interval().max( nodeMax );
						System.arraycopy( nodeMin, 0, position, 0, n );
						if ( nextNode.hasBitMask() )
						{
							currentMask = nextNode.bitmask();
							currentSize = currentMask.numSet();
							while ( !currentMask.get( position ) )
								advance();
						}
						else
						{
							currentMask = null;
							currentSize = ( int ) Intervals.numElements( nextNode.interval() );
						}
						currentIndex = 0;
						nextNode = nextNonEmptyNode();
					}
				}
			}

			private void advance()
			{
				for ( int d = 0; d < n; ++d )
					if ( ++position[ d ] > nodeMax[ d ] )
						position[ d ] = nodeMin[ d ];
					else
						break;
			}

			@Override
			public void jumpFwd( final long steps )
			{
				for ( long i = 0; i < steps; ++i )
					fwd();
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
			public TrueCursor copy()
			{
				return new TrueCursor( this );
			}

			@Override
			public TrueCursor copyCursor()
			{
				return copy();
			}
		}
	}
}
