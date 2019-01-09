package net.imglib2.roi.sparse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;

import net.imglib2.EuclideanSpace;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.roi.sparse.SparseBitmaskNTree.Node;
import net.imglib2.util.Intervals;

/**
 * A bitmask stored as a quadtree (or n-dimensional equivalent). The tree is
 * truncated such that it does not contain boolean value-nodes all the way to
 * the leafs. Instead, leafs store bitmasks of a specified size.
 *
 * @author Tobias Pietzsch
 */
public interface SparseBitmaskNTree extends EuclideanSpace, Iterable< Node >
{
	/**
	 * Get the height of the tree.
	 * <p>
	 * The height is the (assumed) length of the path from the root to a leaf
	 * containing a bit mask. E.g., a tree comprising only a root node has
	 * {@code height = 0}.
	 *
	 * @return the current height of the tree.
	 */
	int height();

	/**
	 * Get the value at the specified position.
	 * <p>
	 * {@code position} must be within bounds of this tree, i.e., within the
	 * interval covered by the root node.
	 *
	 * @param position
	 *            coordinates within bounds of this tree.
	 * @return the value at {@code position}.
	 */
	boolean get( final long[] position );

	/**
	 * Set the value at the specified position. If necessary, new nodes will be
	 * created. If possible, nodes will be merged.
	 * <p>
	 * {@code position} must be within bounds of this tree, i.e., within the
	 * interval covered by the root node.
	 *
	 * @param position
	 *            coordinates within bounds of this tree.
	 * @param value
	 *            value to store at {@code position}.
	 */
	void set( final long[] position, final boolean value );

	/**
	 * Evaluates {@code op} for each node, traversing the tree depth-first. If
	 * {@code op} returns {@code false} for a node, the branch below the node is
	 * truncated.
	 *
	 * @param op
	 *            evaluated for each node
	 */
	void forEach( final Predicate< Node > op );

	/**
	 * Returns a {@link NodeIterator} that iterates the nodes of this tree using
	 * depth first traversal.
	 *
	 * @return a node iterator.
	 */
	@Override
	NodeIterator iterator();

	interface Node
	{
		/**
		 * Returns {@code true}, iff this node has children.
		 * <p>
		 * Nodes without children are either completely {@code true} or
		 * {@code false} (see {@link #value()}) or have values specified by a
		 * {@link #bitmask()}.
		 *
		 * @return whether this node has children.
		 */
		boolean hasChildren();

		/**
		 * Returns {@code true}, iff this node has a bitmask. (This implies
		 * {@code hasCildren() == false}).
		 *
		 * @return whether this node has a bitmask.
		 */
		default boolean hasBitMask()
		{
			return bitmask() != null;
		}

		/**
		 * Get the value of this node, i.e. ,the value of all voxels covered by
		 * this node. The returned value is meaningless if this node has
		 * children or an associated bitmask.
		 *
		 * @return the value of this node.
		 */
		boolean value();

		/**
		 * internal
		 */
		BitMask bitmask();

		/**
		 * Get the coordinate interval covered by this node.
		 *
		 * @return interval covered by this node.
		 */
		Interval interval();

		/**
		 * Get the <em>level</em> of this node.
		 * <p>
		 * </p>
		 * The level of a node corresponds to its height in a fully populated
		 * tree. I.e., the level of a node is the height of the tree (with at
		 * least one bit mask) minus the depth of the node. For example, leaf
		 * nodes in the fully populated tree have level {@code 0}. The root node
		 * has level {@link #height()}.
		 *
		 * @return the level of this node
		 */
		int level();
	}

	/**
	 * Iterates the nodes of a {@link SparseBitmaskNTree} using depth first
	 * traversal.
	 */
	interface NodeIterator extends Iterator< Node >
	{
		void reset();

		/**
		 * Get an independent copy that is pointing to the same node as this
		 * iterator.
		 *
		 * @return copy of this iterator.
		 */
		NodeIterator copy();

		/**
		 * Get the node returned by the most recent {@link #next()}.
		 *
		 * @return the node returned by the most recent {@link #next()}.
		 */
		Node current();
	}

	/*
	 *
	 * Simple algorithms on SparseBitmaskNTree
	 *
	 */

	/**
	 * Compute the number of {@code true} pixels in a {@link SparseBitmaskNTree}.
	 *
	 * @param tree a {@code SparseBitmaskNTree}
	 * @return the number of {@code true} pixels in a {@code tree}.
	 */
	static long size( final SparseBitmaskNTree tree )
	{
		long size = 0;
		final NodeIterator iter = tree.iterator();
		while ( iter.hasNext() )
		{
			final Node nd = iter.next();
			if ( !nd.hasChildren() )
			{
				if ( nd.hasBitMask() )
					size += nd.bitmask().numSet();
				else if ( nd.value() )
					size += Intervals.numElements( nd.interval() );
			}
		}
		return size;
	}

	/**
	 * Compute the bounding box of {@code true} pixels in a {@link SparseBitmaskNTree}.
	 * <p>
	 * This does an iteratively deepening search on the tree nodes, at each level
	 * refining an inner and outer bounding box. The inner bounding box is the
	 * smallest bounding box that is possible, considering data from nodes up to
	 * the current level
	 *
	 * @param tree a {@code SparseBitmaskNTree}
	 * @return the bounding box of {@code true} pixels in a {@code tree}.
	 */
	static Interval bbox( final SparseBitmaskNTree tree )
	{
		final int n = tree.numDimensions();

		// skip nodes completely outside this bbox
		// (this is the outer bbox of the previous height)
		final long[] truncMin = new long[ n ];
		final long[] truncMax = new long[ n ];
		// initially trunc bbox contains everything
		Arrays.fill( truncMin, Long.MIN_VALUE );
		Arrays.fill( truncMax, Long.MAX_VALUE );

		// largest bbox still possible
		final long[] outerMin = new long[ n ];
		final long[] outerMax = new long[ n ];

		// smallest bbox still possible
		final long[] innerMin = new long[ n ];
		final long[] innerMax = new long[ n ];
		// initially smallest bbox is empty
		Arrays.fill( innerMin, Long.MAX_VALUE );
		Arrays.fill( innerMax, Long.MIN_VALUE );

		// reused to store bboxes of leaf masks
		final int[] leafbbmin = new int[ n ];
		final int[] leafbbmax = new int[ n ];
		final int[] leafbbtmp = new int[ n ];

		for ( int i = 0; i <= tree.height(); ++i )
		{
			// initialize outer bbox to inner bbox from last iteration.
			// from there, it will grow to true and mixed nodes.
			System.arraycopy( innerMin, 0, outerMin, 0, n );
			System.arraycopy( innerMax, 0, outerMax, 0, n );

			final int l = tree.height() - i;

			// iter nodes at tree level l
			tree.forEach( node -> {
				final int level = node.level();
				final Interval nodei = node.interval();

				// if nodei is completely outside outer bb, do not recurse further
				boolean intersectsTrunc = true;
				for ( int d = 0; d < n; ++d )
				{
					final long imin = Math.max( truncMin[ d ], nodei.min( d ) );
					final long imax = Math.min( truncMax[ d ], nodei.max( d ) );
					if ( imax < imin )
					{
						intersectsTrunc = false;
						break;
					}
				}
				if ( !intersectsTrunc )
					return false;

				// if nodei is completely contained in inner bb, do not recurse further
				boolean containedInInner = true;
				for ( int d = 0; d < n; ++d )
				{
					if ( innerMin[ d ] > nodei.min( d ) || innerMax[ d ] < nodei.max( d ) )
					{
						containedInInner = false;
						break;
					}
				}
				if ( containedInInner )
					return false;

				// if node is above level l, do not modify bounding box
				// (the node has already been taken into account)
				if ( level > l )
					return true;

				if ( node.hasChildren() )
				{
					for ( int d = 0; d < n; ++d )
					{
						if ( nodei.min( d ) < outerMin[ d ] )
							outerMin[ d ] = nodei.min( d );
						if ( nodei.max( d ) > outerMax[ d ] )
							outerMax[ d ] = nodei.max( d );

						// TODO: is the +1/-1 required here?
						if ( nodei.max( d ) + 1 < innerMin[ d ] )
							innerMin[ d ] = nodei.max( d ) + 1;
						if ( nodei.min( d ) - 1 > innerMax[ d ] )
							innerMax[ d ] = nodei.min( d ) - 1;
					}
				}
				else if ( node.hasBitMask() )
				{
					node.bitmask().computeBoundingBox( leafbbmin, leafbbmax, leafbbtmp );
					for ( int d = 0; d < n; ++d )
					{
						final long dmin = nodei.min( d ) + leafbbmin[ d ];
						final long dmax = nodei.min( d ) + leafbbmax[ d ];
						if ( dmin < outerMin[ d ] )
							outerMin[ d ] = dmin;
						if ( dmax > outerMax[ d ] )
							outerMax[ d ] = dmax;
						if ( dmin < innerMin[ d ] )
							innerMin[ d ] = dmin;
						if ( dmax > innerMax[ d ] )
							innerMax[ d ] = dmax;
					}
				}
				else if ( node.value() )
				{
					for ( int d = 0; d < n; ++d )
					{
						if ( nodei.min( d ) < outerMin[ d ] )
							outerMin[ d ] = nodei.min( d );
						if ( nodei.max( d ) > outerMax[ d ] )
							outerMax[ d ] = nodei.max( d );
						if ( nodei.min( d ) < innerMin[ d ] )
							innerMin[ d ] = nodei.min( d );
						if ( nodei.max( d ) > innerMax[ d ] )
							innerMax[ d ] = nodei.max( d );
					}
				}

				// we just visited a node at level l, do not recurse further
				return false;
			} );

			// everything outside the outer bbox can be truncated in next iteration
			System.arraycopy( outerMin, 0, truncMin, 0, n );
			System.arraycopy( outerMax, 0, truncMax, 0, n );
		}

		return new FinalInterval( outerMin, outerMax );
	}
}
