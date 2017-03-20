package net.imglib2.roi.geom;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.imglib2.KDTree;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPointSampleList;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.ClosedBox;
import net.imglib2.roi.geom.real.ClosedEllipsoid;
import net.imglib2.roi.geom.real.ClosedPolygon2D;
import net.imglib2.roi.geom.real.ClosedSphere;
import net.imglib2.roi.geom.real.ClosedSuperEllipsoid;
import net.imglib2.roi.geom.real.DefaultLine;
import net.imglib2.roi.geom.real.DefaultPolygon2D;
import net.imglib2.roi.geom.real.DefaultPolyline;
import net.imglib2.roi.geom.real.DefaultRealPointCollection;
import net.imglib2.roi.geom.real.Ellipsoid;
import net.imglib2.roi.geom.real.KDTreeRealPointCollection;
import net.imglib2.roi.geom.real.Line;
import net.imglib2.roi.geom.real.NNSRealPointCollection;
import net.imglib2.roi.geom.real.OpenBox;
import net.imglib2.roi.geom.real.OpenEllipsoid;
import net.imglib2.roi.geom.real.OpenPolygon2D;
import net.imglib2.roi.geom.real.OpenSphere;
import net.imglib2.roi.geom.real.OpenSuperEllipsoid;
import net.imglib2.roi.geom.real.Polygon2D;
import net.imglib2.roi.geom.real.Polyline;
import net.imglib2.roi.geom.real.RealPointCollection;
import net.imglib2.roi.geom.real.RealPointSampleListRealPointCollection;
import net.imglib2.roi.geom.real.Sphere;
import net.imglib2.roi.geom.real.SuperEllipsoid;
import net.imglib2.roi.mask.Mask;

/**
 * Utility class for creating {@link Mask}s.
 *
 * @author Alison Walter
 */
public class GeomMasks
{

	private GeomMasks()
	{
		// NB: Prevent instantiation of utility class.
	}

	// -- Box --

	/** Creates a {@link ClosedBox}. */
	public static Box closedBox( final double[] min, final double[] max )
	{
		return new ClosedBox( min, max );
	}

	/** Creates an {@link OpenBox}. */
	public static Box openBox( final double[] min, final double[] max )
	{
		return new OpenBox( min, max );
	}

	// -- Ellipsoid --

	/** Creates a {@link ClosedEllipsoid}. */
	public static Ellipsoid closedEllipsoid( final double[] center, final double[] semiAxisLengths )
	{
		return new ClosedEllipsoid( center, semiAxisLengths );
	}

	/** Creates an {@link OpenEllipsoid}. */
	public static Ellipsoid openEllipsoid( final double[] center, final double[] semiAxisLengths )
	{
		return new OpenEllipsoid( center, semiAxisLengths );
	}

	// -- Line --

	/** Creates a {@link DefaultLine}. */
	public static Line line( final RealLocalizable pointOne, final RealLocalizable pointTwo )
	{
		return new DefaultLine( pointOne, pointTwo );
	}

	/** Creates a {@link DefaultLine}. */
	public static Line line( final double[] pointOne, final double[] pointTwo, final boolean copy )
	{
		return new DefaultLine( pointOne, pointTwo, copy );
	}

	// -- Polygon2D --

	/** Creates a {@link DefaultPolygon2D}. */
	public static Polygon2D polygon2D( final List< ? extends RealLocalizable > vertices )
	{
		return new DefaultPolygon2D( vertices );
	}

	/** Creates a {@link DefaultPolygon2D}. */
	public static Polygon2D polygon2D( final double[] x, final double[] y )
	{
		return new DefaultPolygon2D( x, y );
	}

	/** Creates a {@link ClosedPolygon2D}. */
	public static Polygon2D closedPolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		return new ClosedPolygon2D( vertices );
	}

	/** Creates a {@link ClosedPolygon2D}. */
	public static Polygon2D closedPolygon2D( final double[] x, final double[] y )
	{
		return new ClosedPolygon2D( x, y );
	}

	/** Creates an {@link OpenPolygon2D}. */
	public static Polygon2D OpenPolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		return new OpenPolygon2D( vertices );
	}

	/** Creates an {@link OpenPolygon2D}. */
	public static Polygon2D OpenPolygon2D( final double[] x, final double[] y )
	{
		return new OpenPolygon2D( x, y );
	}

	// -- Polyline --

	/** Creates a {@link DefaultPolyline}. */
	public static Polyline polyline( final List< ? extends RealLocalizable > vertices )
	{
		return new DefaultPolyline( vertices );
	}

	/** Creates a {@link DefaultPolyline}. */
	public static Polyline polyline( final double[][] vertices )
	{
		return new DefaultPolyline( vertices );
	}

	// -- RealPointCollection --

	/** Creates a {@link DefaultRealPointCollection}. */
	public static < L extends RealLocalizable > RealPointCollection< L > realPointCollection( final HashSet< L > points )
	{
		return new DefaultRealPointCollection<>( points );
	}

	/** Creates a {@link DefaultRealPointCollection}. */
	public static < L extends RealLocalizable > RealPointCollection< L > realPointCollection( final Collection< L > points )
	{
		return new DefaultRealPointCollection<>( points );
	}

	/** Creates a {@link KDTreeRealPointCollection}. */
	public static < L extends RealLocalizable > NNSRealPointCollection< L > kDTreeRealPointCollection( final KDTree< L > tree )
	{
		return new KDTreeRealPointCollection<>( tree );
	}

	/** Creates a {@link KDTreeRealPointCollection}. */
	public static < L extends RealLocalizable > NNSRealPointCollection< L > kDTreeRealPointCollection( final Collection< L > points )
	{
		return new KDTreeRealPointCollection<>( points );
	}

	/** Creates a {@link RealPointSampleListRealPointCollection}. */
	public static < L extends RealLocalizable > NNSRealPointCollection< L > realPointSampleListRealPointCollection( final RealPointSampleList< L > points )
	{
		return new RealPointSampleListRealPointCollection<>( points );
	}

	// -- Sphere --

	/** Creates a {@link ClosedSphere}. */
	public static Sphere closedSphere( final double[] center, final double radius )
	{
		return new ClosedSphere( center, radius );
	}

	/** Creates an {@link OpenSphere}. */
	public static Sphere OpenSphere( final double[] center, final double radius )
	{
		return new OpenSphere( center, radius );
	}

	// -- SuperEllipsoid --

	/** Creates a {@link ClosedSuperEllipsoid}. */
	public static SuperEllipsoid closedSuperEllipsoid( final double[] center, final double[] semiAxisLengths, final double exponent )
	{
		if ( exponent == 2 )
			return new ClosedEllipsoid( center, semiAxisLengths );
		return new ClosedSuperEllipsoid( center, semiAxisLengths, exponent );
	}

	/** Creates an {@link OpenSuperEllipsoid}. */
	public static SuperEllipsoid openSuperEllipsoid( final double[] center, final double[] semiAxisLengths, final double exponent )
	{
		if ( exponent == 2 )
			return new OpenEllipsoid( center, semiAxisLengths );
		return new OpenSuperEllipsoid( center, semiAxisLengths, exponent );
	}
}
