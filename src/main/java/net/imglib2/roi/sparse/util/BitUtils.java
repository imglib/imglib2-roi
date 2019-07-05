package net.imglib2.roi.sparse.util;

/**
 * @author Tobias Pietzsch
 */
public class BitUtils
{
	/**
	 * Find the highest 1-bit in byte {@code b}.
	 *
	 * @return the index of the highest 1-bit {@code b}, or {@code -1} if no bit is set.
	 */
	public static int highestOneBit( byte b )
	{
		if ( b == 0 )
			return -1;

		int highest = 0;
		if ( ( b & 0xf0 ) != 0 )
		{
			highest += 4;
			b >>>= 4;
		}
		if ( ( b & 0x0c ) != 0 )
		{
			highest += 2;
			b >>>= 2;
		}
		if ( ( b & 0x2 ) != 0 )
		{
			highest += 1;
		}

		return highest;
	}

	/**
	 * Find the lowest 1-bit in byte {@code b}.
	 *
	 * @return the index of the lowest 1-bit {@code b}, or {@code -1} if no bit is set.
	 */
	public static int lowestOneBit( byte b )
	{
		if ( b == 0 )
			return -1;

		int lowest = 0;
		if ( ( b & 0x0f ) == 0 )
		{
			lowest += 4;
			b >>>= 4;
		}
		if ( ( b & 0x03 ) == 0 )
		{
			lowest += 2;
			b >>>= 2;
		}
		if ( ( b & 0x01 ) == 0 )
		{
			lowest += 1;
		}

		return lowest;
	}

}
