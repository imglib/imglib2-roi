package net.imglib2.roi.sparse;

import java.util.ArrayList;
import java.util.function.Predicate;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.roi.sparse.util.DefaultInterval;

/*
 * The level of a nodeData corresponds to its height in a fully populated tree.
 * I.e, the level of a nodeData is height of the fully populated tree minus the depth of the nodeData.
 */

/**
 * A {@link SparseBitmaskNTree} with fixed dimensions. (Dimensions are specified
 * by height of the tree and dimensions of the leaf bitmasks.)
 * <p>
 * This class is not thread-safe!
 *
 * @author Tobias Pietzsch
 */
public class Tree implements SparseBitmaskNTree
{
	/**
	 * Number of dimensions.
	 */
	private final int n;

	/**
	 * How many children a inner node has ({@code 2^n}).
	 */
	private final int numChildren;

	/**
	 * Dimensions of a leaf bit-mask. <em>Every element must be a power of
	 * 2!</em>
	 */
	private final int[] leafDims;

	private final LeafBitmask.Specification bitmaskSpecification;

	/**
	 * The height of the tree.
	 */
	private final int height;

	/**
	 * The root node.
	 */
	private final NodeData root;

	private final Interval bounds;

	/**
	 * @param leafDims Dimensions of a leaf bit-mask. <em>Every element must be a
	 *                 power of 2!</em>
	 * @param height   Initial height of the tree.
	 */
	public Tree(
			final int[] leafDims,
			final int height )
	{
		this( checkLeafDims( leafDims.clone() ), height, new NodeData( null, false ) );
	}

	private Tree( final int[] leafDims, final int height, final NodeData root ) {
		this.leafDims = leafDims;
		this.n = leafDims.length;
		this.bitmaskSpecification = new LeafBitmask.Specification( leafDims );
		this.height = height;
		this.numChildren = 1 << n;
		this.root = root;
		this.bounds = initBounds( leafDims, height );
	}

	/**
	 * Creates a new parent tree.
	 * The child trees root will become a child of parent trees root at index {@code childindex}.
	 * All other children of the new root are leaf nodes with value
	 * {@code false}.
	 */
	public static Tree newParentTree( Tree childTree, final int childIndex )
	{
		Tree parentTree = new Tree( childTree.leafDims, childTree.height + 1, childTree.root.newParent( childIndex, childTree.numChildren ) );
		if ( !childTree.root.hasChildren() && childTree.root.bitmask() == null )
			childTree.mergeUpwards( childTree.root, childTree.root.value() );
		return parentTree;
	}

	/**
	 * Check dimensions of leaf bit-mask (for constructor).
	 * Verify that
	 * <ul>
	 *     <li>{@code leafDims} has at least one element,</li>
	 *     <li>every element is a (non-zero) power of two, and</li>
	 *     <li>{@code leafDims[0]} is at least 8.</li>
	 * </ul>
	 */
	private static int[] checkLeafDims( final int[] leafDims )
	{
		if ( leafDims == null || leafDims.length == 0 )
			throw new IllegalArgumentException( "leafDims must not be empty");

		for ( int i = 0; i < leafDims.length; i++ )
		{
			final int leafDim = leafDims[ i ];
			if ( leafDim < 1 )
				throw new IllegalArgumentException( "leafDim[ " + i + "] must be >= 1");
			if ( i == 0 && leafDim < 8 )
				throw new IllegalArgumentException( "leafDim[0] must be >= 8");
			if ( Integer.highestOneBit( leafDim ) != leafDim )
				throw new IllegalArgumentException( "leafDim[ " + i + "] must be a power of 2");
		}

		return leafDims;
	}

	@Override
	public int height()
	{
		return height;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	/**
	 * Get the lowest-level node containing position. Note that position is not
	 * necessarily the only pixel inside the node.
	 * <p>
	 * The level of a node corresponds to its height in a fully populated tree.
	 * I.e., the level of a node is the depth of the fully populated tree minus
	 * the depth of the node. For example, leaf nodes in the fully populated
	 * tree have level {@code 0}.
	 *
	 * @param pos
	 *            a position inside the image.
	 * @param maxDepth
	 *            maximum depth of the requested node. {@code maxDepth > 0}
	 *            means that the search is possibly terminated early.
	 *
	 * @return the lowest-level node containing position.
	 */
	private NodeData getNode( final long[] pos, final int maxDepth )
	{
		NodeData current = root;
		for ( int level = height - 1; level >= maxDepth; --level )
		{
			final NodeData child = current.child( getChildIndex( pos, level ) );
			if( child == null )
				return current;
			current = child;
		}
		return current;
	}

	private int getChildIndex( long[] pos, int level )
	{
		int childindex = 0;
		for ( int d = 0; d < n; ++d )
		{
			final long bitmask = ( ( long ) leafDims[ d ] ) << level;
			if ( ( pos[ d ] & bitmask ) != 0 )
				childindex |= 1 << d;
		}
		return childindex;
	}

	@Override
	public boolean get( final long[] pos )
	{
		final NodeData node = getNode( pos, 0 );

		final LeafBitmask bitmask = node.bitmask();
		if ( bitmask == null )
			return node.value();

		return bitmask.get( pos );
	}

	/**
	 * For debugging.
	 *
	 * returns 0 if false
	 * returns 1 if true
	 * returns 2 if mixed
	 */
	public int getForDebugging( final long[] pos, final int level )
	{
		final NodeData node = getNode( pos, level );

		if ( node.hasChildren() )
			return 2;

		if ( node.bitmask() == null )
			return node.value() ? 1 : 0;

		return node.bitmask().get( pos ) ? 1 : 0;
	}

	@Override
	public void set( final long[] pos, final boolean value )
	{
		NodeData current = root;
		for ( int l = height - 1; l >= 0; --l )
		{
			if ( !current.hasChildren() )
			{
				if ( current.value() == value )
					return;

				current.createChildren( numChildren );
			}

			current = current.child( getChildIndex( pos, l ) );
		}

		if ( current.bitmask() == null && current.value() != value )
			current.createBitmask( bitmaskSpecification );

		if ( current.bitmask() != null && current.bitmask().set( pos, value ) )
		{
			current.mergeLeafToValue( value );
			mergeUpwards( current, value );
		}
	}

	/**
	 * If all the children of our parent have the same value remove them all.
	 * Call recursively for parent.
	 *
	 * @param node
	 *            the starting node (whose parents should be tested
	 *            recursively).
	 * @return nodeData that the starting nodeData was ultimately merged into.
	 */
	private void mergeUpwards( final NodeData node, final boolean value )
	{
		final NodeData parent = node.parent();
		if ( parent == null )
			return;
		if ( ! parent.merge( value ) )
			return;
		mergeUpwards( parent, value );
	}

	@Override
	public void forEach( final Predicate< Node > op )
	{
		final NodeIteratorImp iter = new NodeIteratorImp();
		while ( iter.hasNext() )
			if ( !op.test( iter.next() ) )
				iter.truncate();
	}

	@Override
	public NodeIterator iterator()
	{
		return new NodeIteratorImp();
	}

	/**
	 * A stack of these is used to implement NodeIterator.
	 */
	private static class NodeImp implements Node
	{
		private final int n;

		final int level;

		NodeData nodeData;

		final long[] min;

		final long[] max;

		int nextChildindex;

		// TODO: the should be and Intervals.wrap( min, max ) method that does this
		private final Interval interval = new DefaultInterval()
		{
			@Override
			public long min( final int d )
			{
				return min[ d ];
			}

			@Override
			public long max( final int d )
			{
				return max[ d ];
			}

			@Override
			public long dimension( final int d )
			{
				return max[ d ] - min[ d ] + 1;
			}

			@Override
			public int numDimensions()
			{
				return n;
			}
		};

		NodeImp( final int level, final int numDimensions )
		{
			this.level = level;
			n = numDimensions;
			min = new long[ n ];
			max = new long[ n ];
		}

		@Override
		public boolean hasChildren()
		{
			return nodeData.hasChildren();
		}

		@Override
		public boolean value()
		{
			return nodeData.value();
		}

		@Override
		public LeafBitmask bitmask()
		{
			return nodeData.bitmask();
		}

		@Override
		public Interval interval()
		{
			return interval;
		}

		@Override
		public int level()
		{
			return level;
		}
	}

	private class NodeIteratorImp implements NodeIterator
	{
		private final ArrayList< NodeImp > nds;

		private NodeImp next;

		private NodeImp current;

		NodeIteratorImp()
		{
			nds = new ArrayList<>( height + 1 );
			for ( int h = 0; h <= height; ++h )
				nds.add( new NodeImp( h, n ) );
			reset();
		}

		private NodeIteratorImp( final NodeIteratorImp other )
		{
			nds = new ArrayList<>( height + 1 );
			for ( int h = 0; h <= height; ++h )
			{
				final NodeImp nd = new NodeImp( h, n );
				final NodeImp ndo = other.nds.get( h );
				nd.nodeData = ndo.nodeData;
				System.arraycopy( ndo.min, 0, nd.min, 0, n );
				System.arraycopy( ndo.max, 0, nd.max, 0, n );
				nd.nextChildindex = ndo.nextChildindex;

				nds.add( nd );

				if ( other.current == ndo )
					current = nd;
				if ( other.next == ndo )
					next = nd;
			}
		}

		@Override
		public void reset()
		{
			final NodeImp rootData = nds.get( height );
			for ( int d = 0; d < n; ++d )
			{
				rootData.min[ d ] = 0;
				final long s = ( ( long ) leafDims[ d ] ) << height;
				rootData.max[ d ] = rootData.min[ d ] + s - 1;
			}
			rootData.nodeData = root;
			rootData.nextChildindex = root.hasChildren() ? 0 : numChildren;
			next = rootData;
			current = null;
		}

		@Override
		public NodeIterator copy()
		{
			return new NodeIteratorImp( this );
		}

		@Override
		public boolean hasNext()
		{
			return next != null;
		}

		@Override
		public Node next()
		{
			current = next;
			final int l = current.level;
			if ( l < height )
			{
				final NodeImp parentData = nds.get( l + 1 );
				final int i = parentData.nextChildindex++;
				current.nodeData = parentData.nodeData.child( i );
				current.nextChildindex = current.hasChildren() ? 0 : numChildren;
				for ( int d = 0; d < n; ++d )
				{
					final long s = ( ( long ) leafDims[ d ] ) << l;
					final long min = parentData.min[ d ] + (
							( i & ( 1 << d ) ) == 0
									? 0
									: s );
					current.min[ d ] = min;
					current.max[ d ] = min + s - 1;
				}
			}
			next = getNext( current );
			return current;
		}

		@Override
		public Node current()
		{
			return current;
		}

		void truncate()
		{
			current.nextChildindex = numChildren;
			next = getNext( current );
		}

		private NodeImp getNext( NodeImp nodeData )
		{
			while ( nodeData.nextChildindex >= numChildren )
			{
				final int l = nodeData.level;
				if ( l == height )
				{
					return null;
				}
				nodeData = nds.get( l + 1 );
			}
			return nds.get( nodeData.level - 1 );
		}
	}

	private static FinalInterval initBounds( int[] leafDims, int height )
	{
		long[] dimensions = new long[leafDims.length];
		for ( int d = 0; d < dimensions.length; ++d )
			dimensions[ d ] = ( leafDims[ d ] << height ) - 1;
		return new FinalInterval( dimensions );
	}

	Interval bounds()
	{
		return bounds;
	}
}
