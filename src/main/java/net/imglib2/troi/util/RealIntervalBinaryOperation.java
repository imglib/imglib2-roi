package net.imglib2.troi.util;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealInterval;
import net.imglib2.RealPositionable;

public abstract class RealIntervalBinaryOperation extends AbstractEuclideanSpace implements RealInterval
{
	protected RealInterval i1;

	protected RealInterval i2;

	public RealIntervalBinaryOperation( RealInterval i1, RealInterval i2 )
	{
		super( i1.numDimensions() );
		this.i1 = i1;
		this.i2 = i2;

		assert ( i1.numDimensions() == i2.numDimensions() );
	}

	@Override
	public void realMin( final double[] realMin )
	{
		for ( int d = 0; d < n; ++d )
			realMin[ d ] = realMin( d );
	}

	@Override
	public void realMin( final RealPositionable realMin )
	{
		for ( int d = 0; d < n; ++d )
			realMin.setPosition( realMin( d ), d );
	}

	@Override
	public void realMax( final double[] realMax )
	{
		for ( int d = 0; d < n; ++d )
			realMax[ d ] = realMax( d );
	}

	@Override
	public void realMax( final RealPositionable realMax )
	{
		for ( int d = 0; d < n; ++d )
			realMax.setPosition( realMax( d ), d );
	}
}
