package net.imglib2.roi.sparse;

import java.util.function.Predicate;

import net.imglib2.Interval;
import net.imglib2.roi.sparse.util.DefaultInterval;

/**
 * A unbounded {@link SparseBitmaskNTree}, based on a {@link Tree} that grows
 * when out-of-bounds pixels are set.
 * <p>
 * This class is thread-safe!
 *
 * @author Tobias Pietzsch
 */
public class GrowableTree implements SparseBitmaskNTree
{
	private TreeAndOffset treeAndOffset;

	/**
	 * @param leafDims
	 *            Dimensions of a leaf bit-mask. <em>Every element must be a
	 *            power of 2!</em>
	 */
	public GrowableTree( final int[] leafDims )
	{
		treeAndOffset = new TreeAndOffset( new Tree( leafDims, 0 ), new long[ leafDims.length ] );
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
	public synchronized void set( final long[] position, final long[] tmp, final boolean value )
	{
		final TreeAndOffset o = treeAndOffset;
		final int n = o.tree.numDimensions();
		int childindex = 0;
		boolean outOfBounds = false;
		long[] newOffset = null;
		for ( int d = 0; d < n; ++d )
		{
			final long p = position[ d ] - o.offset[ d ];
			tmp[ d ] = p;
			outOfBounds = outOfBounds || p < 0 || p > o.tree.bounds().max( d );
			if ( p < 0 )
			{
				if(newOffset == null)
					newOffset = new long[n];
				childindex |= 1 << d;
				newOffset[ d ] -= o.tree.bounds().dimension( d );
			}
		}
		if ( outOfBounds )
		{
			if ( value )
			{
				if(newOffset == null)
					newOffset = new long[n];
				treeAndOffset = new TreeAndOffset( Tree.newParentTree( o.tree, childindex ), newOffset );
				set( position, tmp, value );
			}
		}
		else
			o.tree.set( tmp, value );
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
		TreeAndOffset o = treeAndOffset;
		final int n = o.tree.numDimensions();
		for ( int d = 0; d < n; ++d )
		{
			final long p = position[ d ] - o.offset[ d ];
			if ( p < 0 || p > o.tree.bounds().max( d ) )
				return false;
			tmp[ d ] = p;
		}
		return o.tree.get( tmp );
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
		return treeAndOffset.tree.height();
	}

	@Override
	public int numDimensions()
	{
		return treeAndOffset.tree.numDimensions();
	}

	@Override
	public void forEach( final Predicate< Node > op )
	{
		TreeAndOffset o = treeAndOffset;
		final TranslatedNode w = new TranslatedNode( o.offset );
		o.tree.forEach( ( final Node nd ) -> op.test( w.wrap( nd ) ) );
	}

	@Override
	public NodeIterator iterator()
	{
		return new TranslatedNodeIterator( treeAndOffset );
	}

	private static class TreeAndOffset
	{
		private final Tree tree;
		private final long[] offset;

		private TreeAndOffset( Tree tree, long[] offset )
		{
			this.tree = tree;
			this.offset = offset;
		}
	}

	private static class TranslatedNodeIterator implements NodeIterator
	{
		private final TranslatedNode w;

		private final NodeIterator wi;

		private TranslatedNodeIterator( TreeAndOffset tree )
		{
			w = new TranslatedNode( tree.offset );
			wi = tree.tree.iterator();
		}

		private TranslatedNodeIterator( final TranslatedNodeIterator other )
		{
			w = new TranslatedNode( other.w );
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
			return new TranslatedNodeIterator( this );
		}
	}

	private static class TranslatedNode implements Node
	{
		private Node source;

		private final long[] offset;

		private TranslatedNode( final long[] offset )
		{
			this.offset = offset;
		}

		private TranslatedNode( final TranslatedNode other )
		{
			this.source = other.source;
			this.offset = other.offset;
		}

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
