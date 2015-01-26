package net.imglib2.roi.util.iterationcode;

import gnu.trove.list.array.TIntArrayList;

import java.util.Arrays;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;

/**
 * TODO
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class IterationCodeBuilder extends AbstractEuclideanSpace implements IterationCode
{
	private final TIntArrayList itcode;

	private final long itcodeOffsetX;

	private long size;

	protected long[] prev;

	protected long[] curr;

	private final long[] bbmin;

	private final long[] bbmax;

	private boolean startedRasterization;

	private long rasterBegin;

	public IterationCodeBuilder( final int numDimensions, final long minX )
	{
		super( numDimensions );
		itcode = new TIntArrayList();
		itcodeOffsetX = minX;
		size = 0;
		prev = new long[ n ];
		curr = new long[ n ];
		bbmin = new long[ n ];
		bbmax = new long[ n ];
		Arrays.fill( bbmin, Long.MAX_VALUE );
		Arrays.fill( bbmax, Long.MIN_VALUE );
		startedRasterization = false;
		rasterBegin = 0;
	}

	/**
	 * accumulate the given coordinates.
	 */
	public void add( final Localizable pos )
	{
		++size;
		pos.localize( curr );
		if ( startedRasterization )
		{
			for ( int d = n - 1; d >= 0; --d )
			{
				if ( d == 0 )
				{
					if ( curr[ 0 ] != prev[ 0 ] + 1 )
					{
						endRaster( 0 );
						break;
					}
				}
				else if ( curr[ d ] != prev[ d ] )
				{
					endRaster( d );
					break;
				}
			}
			for ( int d = 0; d < n; d++ )
			{
				if ( curr[ d ] < bbmin[ d ] )
					bbmin[ d ] = curr[ d ];
				else if ( curr[ d ] > bbmax[ d ] )
					bbmax[ d ] = curr[ d ];
			}
		}
		else
		{
			itcode.add( ( int ) itcodeOffsetX );
			for ( int d = 1; d < n; ++d )
				itcode.add( ( int ) curr[ d ] );
			rasterBegin = curr[ 0 ];
			System.arraycopy( curr, 0, bbmin, 0, n );
			System.arraycopy( curr, 0, bbmax, 0, n );
			startedRasterization = true;
		}
		// swap prev and curr pos arrays
		final long[] tmp = prev;
		prev = curr;
		curr = tmp;
	}

	private void endRaster( final int badDimension )
	{
		itcode.add( ( int ) ( rasterBegin - itcodeOffsetX ) );
		itcode.add( ( int ) ( prev[ 0 ] - itcodeOffsetX ) );
		if ( badDimension > 0 )
		{
			itcode.add( -badDimension );
			for ( int d = 1; d <= badDimension; ++d )
				itcode.add( ( int )curr[ d ] );
		}
		rasterBegin = curr[ 0 ];
	}

	public void finish()
	{
		if ( startedRasterization )
		{
			itcode.add( ( int ) ( rasterBegin - itcodeOffsetX ) );
			itcode.add( ( int ) ( prev[ 0 ] - itcodeOffsetX ) );
		}
		itcode.trimToSize();
		prev = null;
		curr = null;
	}

	@Override
	public TIntArrayList getItcode()
	{
		return itcode;
	}

	@Override
	public long getSize()
	{
		return size;
	}

	@Override
	public long[] getBoundingBoxMin()
	{
		return bbmin;
	}

	@Override
	public long[] getBoundingBoxMax()
	{
		return bbmax;
	}
}