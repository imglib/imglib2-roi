package net.imglib2.roi.geom.real;

import java.util.Collection;
import java.util.HashSet;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class DefaultRealPointCollection< L extends RealLocalizable > extends AbstractEuclideanSpace implements RealPointCollection< L >
{
	private final HashSet< L > points;

	public DefaultRealPointCollection( final HashSet< L > points )
	{
		super( points.iterator().next().numDimensions() );
		this.points = points;
	}

	public DefaultRealPointCollection( final Collection< L > points )
	{
		this( new HashSet<>( points ) );
	}

	@Override
	public boolean contains( final RealLocalizable l )
	{
		double bestDistance = Double.POSITIVE_INFINITY;
		for ( final L pt : points )
		{
			final double distance = squareDistance( pt, l );
			if ( distance < bestDistance )
				bestDistance = distance;
		}

		return bestDistance <= 0;
	}

	@Override
	public Iterable< L > points()
	{
		return points;
	}

	// -- Helper methods --

	private double squareDistance( final L ptOne, final RealLocalizable ptTwo )
	{
		double distance = 0;
		for ( int i = 0; i < n; i++ )
			distance += ( ptOne.getDoublePosition( i ) - ptTwo.getDoublePosition( i ) ) * ( ptOne.getDoublePosition( i ) - ptTwo.getDoublePosition( i ) );
		return distance;
	}
}
