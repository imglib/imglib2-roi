package net.imglib2.troi.geom.real;

import java.util.Collection;
import java.util.HashMap;

import net.imglib2.AbstractRealInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.Regions;
import net.imglib2.util.Intervals;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * {@link RealPointCollection} backed by a {@code HashMap}.
 *
 * @author Alison Walter
 */
public class DefaultRealPointCollection< L extends RealLocalizable > extends AbstractRealInterval implements RealPointCollection< L >
{
	private final HashMap< TDoubleArrayList, L > points;

	/**
	 * Creates a point collection which includes points in the given
	 * {@code HashMap}.
	 *
	 * @param points
	 *            points to include in the collection, the first point
	 *            determines the dimensionality of the collection. The keys in
	 *            the map should be {@code TDoubleArrayList}s which correspond
	 *            to the position of the points.
	 */
	public DefaultRealPointCollection( final HashMap< TDoubleArrayList, L > points )
	{
		super( Regions.getBoundsReal( points.values() ) );
		this.points = points;
	}

	/**
	 * Creates a point collection which includes points in the given
	 * {@code Collection}.
	 *
	 * @param points
	 *            points to include in the collection, the first point
	 *            determines the dimensionality of the collection
	 */
	public DefaultRealPointCollection( final Collection< L > points )
	{
		this( createHashMap( points ) );
	}

	@Override
	public boolean test( final RealLocalizable l )
	{
		if ( Intervals.contains( this, l ) )
		{
			double bestDistance = Double.POSITIVE_INFINITY;
			for ( final L pt : points.values() )
			{
				final double distance = squareDistance( pt, l );
				if ( distance < bestDistance )
					bestDistance = distance;
			}

			return bestDistance <= 0;
		}
		return false;
	}

	@Override
	public Iterable< L > points()
	{
		return points.values();
	}

	@Override
	public void addPoint( final L point )
	{
		if ( point.numDimensions() != n )
			throw new IllegalArgumentException( "Point must have " + n + " dimensions" );

		final double[] l = new double[ point.numDimensions() ];
		point.localize( l );
		points.put( new TDoubleArrayList( l ), point );

		updateMinMax();
	}

	/**
	 * Removes the given point from the set, if the point is found in the set.
	 *
	 * @param point
	 *            point to be removed, it must have the same hash as a point in
	 *            the set in order to be removed
	 */
	@Override
	public void removePoint( final L point )
	{
		final double[] l = new double[ point.numDimensions() ];
		point.localize( l );
		points.remove( new TDoubleArrayList( l ) );

		updateMinMax();
	}

	// -- Helper methods --

	private double squareDistance( final L ptOne, final RealLocalizable ptTwo )
	{
		double distance = 0;
		for ( int i = 0; i < n; i++ )
			distance += ( ptOne.getDoublePosition( i ) - ptTwo.getDoublePosition( i ) ) * ( ptOne.getDoublePosition( i ) - ptTwo.getDoublePosition( i ) );
		return distance;
	}

	private static < L extends RealLocalizable > HashMap< TDoubleArrayList, L > createHashMap( final Collection< L > points )
	{
		final HashMap< TDoubleArrayList, L > map = new HashMap<>();

		for ( final L p : points )
		{
			final double[] l = new double[ p.numDimensions() ];
			p.localize( l );
			map.put( new TDoubleArrayList( l ), p );
		}
		return map;
	}

	private void updateMinMax()
	{
		RealInterval interval = Regions.getBoundsReal( points.values() );
		interval.realMin( min );
		interval.realMax( max );
	}
}
