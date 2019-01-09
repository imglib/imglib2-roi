package net.imglib2.roi.sparse;

import java.util.Arrays;

import net.imglib2.roi.sparse.util.BitUtils;
import net.imglib2.util.Intervals;

/**
 * Bit-mask are stored in the leafs of the tree. Each bit-mask is a
 * {@code leafDims}-sized boolean image backed by a {@code byte[]} array. It
 * also keeps track of how many bits are currently set.
 */
class BitMask
{

	private final Specification specification;

	/**
	 * Stores data of this mask
	 */
	private final byte[] bytes;

	/**
	 * Current number of true bits in this mask
	 */
	private int numSet;

	/**
	 * Create a new BitMask that is initially completely filled with the given
	 * value.
	 *
	 * @param initialValue
	 */
	BitMask( final Specification specification, final boolean initialValue )
	{
		this.specification = specification;
		bytes = new byte[ specification.size >> 3 ];
		if ( initialValue )
		{
			Arrays.fill( bytes, ( byte ) 255 );
			numSet = specification.size;
		}
		else
		{
			numSet = 0;
		}
	}

	/**
	 * @param globalpos
	 *            global position of the bit to set
	 * @param value
	 *            value to set the bit to
	 * @return {@code true} iff the mask was completely filled or emptied by
	 *         this operation
	 */
	boolean set( final long[] globalpos, final boolean value )
	{
		final int i = byteIndex( globalpos );
		final int mask = 1 << ( globalpos[ 0 ] & specification.mask[ 0 ] & 0x07 );

		final byte b = bytes[ i ];
		if ( value )
		{
			final byte bm = ( byte ) ( b | mask );
			if ( bm != b ) // changing bit from 0 to 1
			{
				bytes[ i ] = bm;
				if ( ++numSet == specification.size )
					return true;
			}
		}
		else
		{
			final byte bm = ( byte ) ( b & ~mask );
			if ( bm != b ) // changing bit from 1 to 0
			{
				bytes[ i ] = bm;
				if ( --numSet == 0 )
					return true;
			}
		}

		return false;
	}

	boolean get( final long[] globalpos )
	{
		final byte b = bytes[ byteIndex( globalpos ) ];
		final int mask = 1 << ( globalpos[ 0 ] & specification.mask[ 0 ] & 0x07 );
		return ( b & mask ) != 0;
	}

	private int byteIndex( final long[] globalpos )
	{
		int i = 0;
		for ( int d = specification.n - 1; d > 0; --d )
			i = ( i + ( int ) ( globalpos[ d ] & specification.mask[ d ] ) ) * specification.byteDims[ d - 1 ];
		return i + ( ( ( int ) ( globalpos[ 0 ] & specification.mask[ 0 ] ) ) >> 3 );
	}

	int numSet()
	{
		return numSet;
	}

	/**
	 * Recompute the bounding box of true mask pixels. The result is stored in
	 * {@code bbmin}, {@code bbmax}
	 *
	 * @param bbmin
	 *            bounding box min is stored here
	 * @param bbmax
	 *            bounding box min is stored here
	 * @param tmp
	 *            temporary variable for storing positions while scanning the
	 *            mask.
	 */
	void computeBoundingBox( final int[] bbmin, final int[] bbmax, final int[] tmp )
	{
		Arrays.fill( bbmin, Integer.MAX_VALUE );
		Arrays.fill( bbmax, Integer.MIN_VALUE );
		if ( numSet == 0 )
			return;

		Arrays.fill( tmp, 0 );
		for ( int i = 0; i < bytes.length; ++i )
		{
			if ( bytes[ i ] != 0 )
			{
				for ( int d = 0; d < specification.n; ++d )
				{
					bbmin[ d ] = Math.min( bbmin[ d ], tmp[ d ] );
					bbmax[ d ] = Math.max( bbmax[ d ], tmp[ d ] );
				}
			}
			for ( int d = 0; d < specification.n; ++d )
			{
				if ( ++tmp[ d ] == specification.byteDims[ d ] )
					tmp[ d ] = 0;
				else
					break;
			}
		}

		final int step = specification.byteDims[ 0 ];

		byte minproj = 0;
		for ( int i = bbmin[ 0 ]; i < bytes.length; i += step )
			minproj |= bytes[ i ];

		byte maxproj = 0;
		if ( bbmin[ 0 ] == bbmax[ 0 ] )
			maxproj = minproj;
		else
			for ( int i = bbmax[ 0 ]; i < bytes.length; i += step )
				maxproj |= bytes[ i ];

		bbmin[ 0 ] = ( bbmin[ 0 ] << 3 ) + BitUtils.lowestOneBit( minproj );
		bbmax[ 0 ] = ( bbmax[ 0 ] << 3 ) + BitUtils.highestOneBit( maxproj );
	}

	public static class Specification
	{

		public final int n;

		/**
		 * Mask out tile coordinate part from a position. Because leafDims are
		 * power of 2, {@code leafMask[ d ] = leafDims[ d ] - 1}
		 */
		private final int[] mask;

		/**
		 * Number of bits in a leaf bit-mask.
		 */
		private final int size;

		/**
		 * Dimensions of a leaf bit-mask in bytes. This is the same as leafDims
		 * (the dimensions of the bitmask), except that leafDims[0] is divided
		 * by 8.
		 */
		private final int[] byteDims;

		public Specification( final int[] leafDims )
		{
			this.n = leafDims.length;

			this.mask = new int[ n ];
			Arrays.setAll( mask, d -> leafDims[ d ] - 1 );
			final long lsize = Intervals.numElements( leafDims );
			this.size = toInt( lsize );
			this.byteDims = leafDims.clone();
			byteDims[ 0 ] = byteDims[ 0 ] >> 3;
		}

		private int toInt( final long lsize )
		{
			if ( lsize > Integer.MAX_VALUE )
				throw new IllegalArgumentException();
			return ( int ) lsize;
		}
	}
}
