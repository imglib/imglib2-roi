package net.imglib2.roi.util.iterationcode;

import gnu.trove.list.array.TIntArrayList;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Iterator;
import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * TODO
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class IterationCodeIterator< P extends Positionable & Localizable > extends AbstractEuclideanSpace implements Iterator
{
	private final TIntArrayList itcode;

	private final long[] offset;

	private final P position;

	private int itcodeIndex;

	private int itcodeOffsetX;

	private long maxX;

	private boolean hasNextRaster;

	public IterationCodeIterator( final IterationCode iterationCode, final long[] offset, final P position )
	{
		super( position.numDimensions() );
		this.position = position;
		this.itcode = iterationCode.getItcode();
		this.offset = offset;
		reset();
	}

	public IterationCodeIterator( final IterationCodeIterator< ? > copyFrom, final P position )
	{
		super( position.numDimensions() );
		this.itcode = copyFrom.itcode;
		this.offset = copyFrom.offset;
		this.position = position;
		this.position.setPosition( copyFrom.position );
		this.itcodeIndex = copyFrom.itcodeIndex;
		this.itcodeOffsetX = copyFrom.itcodeOffsetX;
		this.maxX = copyFrom.maxX;
		this.hasNextRaster = copyFrom.hasNextRaster;
	}

	private void nextRasterStretch()
	{
		int minItcodeX = itcode.get( itcodeIndex++ );
		if ( minItcodeX < 0 )
		{
			for ( int d = 1; d <= -minItcodeX; ++d )
				position.setPosition( itcode.get( itcodeIndex++ ) + offset[ d ], d );
			minItcodeX = itcode.get( itcodeIndex++ );
		}
		position.setPosition( minItcodeX + itcodeOffsetX + offset[ 0 ], 0 );
		maxX = itcode.get( itcodeIndex++ ) + itcodeOffsetX + offset[ 0 ];
		hasNextRaster = itcodeIndex < itcode.size();
	}

	@Override
	public void jumpFwd( final long steps )
	{
		for ( long j = 0; j < steps; ++j )
			fwd();
	}

	@Override
	public void fwd()
	{
		position.fwd( 0 );
		if ( position.getLongPosition( 0 ) > maxX )
			nextRasterStretch();
	}

	@Override
	public void reset()
	{
		itcodeIndex = 0;
		if ( !itcode.isEmpty() )
		{
			itcodeOffsetX = itcode.get( itcodeIndex++ );
			for ( int d = 1; d < n; ++d )
				position.setPosition( itcode.get( itcodeIndex++ ) + offset[ d ], d );
			nextRasterStretch();
			position.bck( 0 );
		}
		else
		{
			hasNextRaster = false;
			position.setPosition( 0, 0 );
			maxX = 0;
		}
	}

	@Override
	public boolean hasNext()
	{
		return hasNextRaster || ( position.getLongPosition( 0 ) < maxX );
	}
}
