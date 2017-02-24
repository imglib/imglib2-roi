package net.imglib2.roi.util;

import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * A {@link Localizable} {@link Positionable} that is the position of another
 * {@link Localizable} {@link Positionable} with an offset.
 *
 * @author Tobias Pietzsch
 */
public class OffsetPositionableLocalizable< P extends Positionable & Localizable > extends OffsetLocalizable< P > implements Positionable
{
	public OffsetPositionableLocalizable( final P source, final long[] offset )
	{
		super( source, offset );
	}

	@Override
	public void fwd( final int d )
	{
		source.fwd( d );
	}

	@Override
	public void bck( final int d )
	{
		source.bck( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		source.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		source.move( distance, d );
	}

	@Override
	public void move( final Localizable distance )
	{
		source.move( distance );
	}

	@Override
	public void move( final int[] distance )
	{
		source.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		source.move( distance );
	}

	@Override
	public void setPosition( final Localizable position )
	{
		for ( int d = 0; d < n; ++d )
			source.setPosition( position.getLongPosition( d ) - offset[ d ], d );
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			source.setPosition( position[ d ] - offset[ d ], d );
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			source.setPosition( position[ d ] - offset[ d ], d );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		source.setPosition( position - offset[ d ], d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		source.setPosition( position - offset[ d ], d );
	}
}
