package net.imglib2.roi.sparse;

import java.util.function.Predicate;

import net.imglib2.Interval;
import net.imglib2.roi.sparse.util.DefaultInterval;

/**
 * A unbounded {@link SparseBitmaskNTree}, based on a {@link Tree} that grows
 * when out-of-bounds pixels are set.
 * <p>
 * This class is not thread-safe!
 *
 * @author Tobias Pietzsch
 */
public class GrowableTree implements SparseBitmaskNTree
{
	private final Tree tree;

	/**
	 * Offset of position 0 in root to global coordinates. {@code offset} is a
	 * multiple of {@code tileDims}, so that we can skip computing differences
	 * in {@code LeafData}.
	 */
	private final long[] offset;

	/**
	 * @param leafDims
	 *            Dimensions of a leaf bit-mask. <em>Every element must be a
	 *            power of 2!</em>
	 */
	public GrowableTree( final int[] leafDims )
	{
		tree = new Tree( leafDims, 0 );
		offset = new long[ tree.numDimensions() ];
	}

	@Override
	public boolean get( final long[] position )
	{
		return get( position, new long[ numDimensions() ] );
	}

	@Override
	public void set( final long[] position, final boolean value )
	{
		set( position, new long[ numDimensions() ], value );
	}


	/**
	 * Set the value at the specified position. If necessary, new nodes will be
	 * created. If possible, nodes will be merged.
	 * <p>
	 * {@code position} must be within bounds of this tree, i.e., within the
	 * interval covered by the root node.
	 *
	 * @param position
	 *            coordinates within bounds of this tree.
	 * @param tmp
	 *            pre-allocated array to store translated coordinates.
	 * @param value
	 *            value to store at {@code position}.
	 */
	public void set( final long[] position, final long[] tmp, final boolean value )
	{
		final int n = tree.numDimensions();
		int childindex = 0;
		boolean needtogrow = false;
		for ( int d = 0; d < n; ++d )
		{
			final long p = position[ d ] - offset[ d ];
			tmp[ d ] = p;
			needtogrow = needtogrow || p < 0 || p > tree.bounds().max( d );
			if ( p < 0 )
			{
				childindex |= 1 << d;
				offset[ d ] -= tree.bounds().dimension( d );
			}
		}
		if ( needtogrow )
		{
			if ( value )
			{
				tree.grow( childindex );
				set( position, tmp, value );
			}
		}
		else
			tree.set( tmp, value );
	}

	/**
	 * Get the value at the specified position.
	 * <p>
	 * {@code position} must be within bounds of this tree, i.e., within the
	 * interval covered by the root node.
	 *
	 * @param position
	 *            coordinates within bounds of this tree.
	 * @param tmp
	 *            pre-allocated array to store translated coordinates.
	 * @return the value at {@code position}.
	 */
	public boolean get( final long[] position, final long[] tmp )
	{
		final int n = tree.numDimensions();
		for ( int d = 0; d < n; ++d )
		{
			final long p = position[ d ] - offset[ d ];
			if ( p < 0 || p > tree.bounds().max( d ) )
				return false;
			tmp[ d ] = p;
		}
		return tree.get( tmp );
	}

	/**
	 * Returns the current height of the tree.
	 * <p>
	 * The height is the length of the path from the root to a leaf (containing
	 * a bit mask). E.g., a tree comprising only a root nodeData has
	 * {@code height = 0}.
	 *
	 * @return the current height of the tree.
	 */
	@Override
	public int height()
	{
		return tree.height();
	}

	@Override
	public int numDimensions()
	{
		return tree.numDimensions();
	}

	@Override
	public void forEach( final Predicate< Node > op )
	{
		final NodeImp w = new NodeImp();
		tree.forEach( ( final Node nd ) -> op.test( w.wrap( nd ) ) );
	}

	@Override
	public NodeIterator iterator()
	{
		return new NodeIteratorImp();
	}

	private class NodeIteratorImp implements NodeIterator
	{
		private final NodeImp w = new NodeImp();

		private final NodeIterator wi;

		NodeIteratorImp()
		{
			wi = tree.iterator();
		}

		NodeIteratorImp( final NodeIteratorImp other )
		{
			wi = other.wi.copy();
		}

		@Override
		public void reset()
		{
			wi.reset();
		}

		@Override
		public boolean hasNext()
		{
			return wi.hasNext();
		}

		@Override
		public Node next()
		{
			return w.wrap( wi.next() );
		}

		@Override
		public Node current()
		{
			return w.wrap( wi.current() );
		}

		@Override
		public NodeIterator copy()
		{
			return new NodeIteratorImp( this );
		}
	}

	private class NodeImp implements Node
	{
		private Node source;

		Node wrap( final Node source )
		{
			this.source = source;
			return this;
		}

		private final Interval interval = new DefaultInterval()
		{
			@Override
			public long min( final int d )
			{
				return source.interval().min( d ) + offset[ d ];
			}

			@Override
			public long max( final int d )
			{
				return source.interval().max( d ) + offset[ d ];
			}

			@Override
			public long dimension( final int d )
			{
				return source.interval().dimension( d );
			}

			@Override
			public int numDimensions()
			{
				return source.interval().numDimensions();
			}
		};

		@Override
		public boolean hasChildren()
		{
			return source.hasChildren();
		}

		@Override
		public boolean value()
		{
			return source.value();
		}

		@Override
		public LeafBitmask bitmask()
		{
			return source.bitmask();
		}

		@Override
		public Interval interval()
		{
			return interval;
		}

		@Override
		public int level()
		{
			return source.level();
		}
	}
}
