package net.imglib2.roi.util;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;

/**
 * A {@link Localizable} that is the location of another {@link Localizable}
 * plus an offset.
 *
 * @author Tobias Pietzsch
 */
//TODO: move to core? (probably not)
public class OffsetLocalizable< L extends Localizable > extends AbstractEuclideanSpace implements Localizable
{
	protected L source;

	protected long[] offset;

	public OffsetLocalizable( final L source, final long[] offset )
	{
		super( source.numDimensions() );
		this.source = source;
		this.offset = offset;
	}

	@Override
	public void localize( final float[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = getFloatPosition( d );
	}

	@Override
	public void localize( final double[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = getDoublePosition( d );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return source.getFloatPosition( d ) + offset[ d ];
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return source.getDoublePosition( d ) + offset[ d ];
	}

	@Override
	public void localize( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = getIntPosition( d );
	}

	@Override
	public void localize( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = getLongPosition( d );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return ( int ) ( source.getIntPosition( d ) + offset[ d ] );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return source.getLongPosition( d ) + offset[ d ];
	}
}
