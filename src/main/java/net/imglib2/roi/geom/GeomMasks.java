/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2017 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.roi.geom;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.imglib2.KDTree;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPointSampleList;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.MaskPredicate;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.ClosedWritableBox;
import net.imglib2.roi.geom.real.ClosedWritableEllipsoid;
import net.imglib2.roi.geom.real.ClosedWritablePolygon2D;
import net.imglib2.roi.geom.real.ClosedWritableSphere;
import net.imglib2.roi.geom.real.ClosedWritableSuperEllipsoid;
import net.imglib2.roi.geom.real.DefaultWritableLine;
import net.imglib2.roi.geom.real.DefaultWritablePointMask;
import net.imglib2.roi.geom.real.DefaultWritablePolygon2D;
import net.imglib2.roi.geom.real.DefaultWritablePolyline;
import net.imglib2.roi.geom.real.DefaultWritableRealPointCollection;
import net.imglib2.roi.geom.real.KDTreeRealPointCollection;
import net.imglib2.roi.geom.real.OpenWritableBox;
import net.imglib2.roi.geom.real.OpenWritableEllipsoid;
import net.imglib2.roi.geom.real.OpenWritablePolygon2D;
import net.imglib2.roi.geom.real.OpenWritableSphere;
import net.imglib2.roi.geom.real.OpenWritableSuperEllipsoid;
import net.imglib2.roi.geom.real.RealPointCollection;
import net.imglib2.roi.geom.real.RealPointSampleListWritableRealPointCollection;
import net.imglib2.roi.geom.real.WritableBox;
import net.imglib2.roi.geom.real.WritableEllipsoid;
import net.imglib2.roi.geom.real.WritableLine;
import net.imglib2.roi.geom.real.WritablePointMask;
import net.imglib2.roi.geom.real.WritablePolygon2D;
import net.imglib2.roi.geom.real.WritablePolyline;
import net.imglib2.roi.geom.real.WritableRealPointCollection;
import net.imglib2.roi.geom.real.WritableSphere;
import net.imglib2.roi.geom.real.WritableSuperEllipsoid;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * Utility class for creating {@link MaskPredicate}s.
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

	/**
	 * Creates a {@link WritableBox} with {@link BoundaryType#CLOSED closed}
	 * {@link MaskPredicate#boundaryType() boundaries}.
	 */
	public static WritableBox closedBox( final double[] min, final double[] max )
	{
		return new ClosedWritableBox( min, max );
	}

	/**
	 * Creates a {@link WritableBox} with {@link BoundaryType#OPEN open}
	 * {@link MaskPredicate#boundaryType() boundaries}.
	 */
	public static WritableBox openBox( final double[] min, final double[] max )
	{
		return new OpenWritableBox( min, max );
	}

	// -- Ellipsoid --

	/**
	 * Creates a {@link WritableEllipsoid} with {@link BoundaryType#CLOSED
	 * closed} {@link MaskPredicate#boundaryType() boundaries}.
	 */
	public static WritableEllipsoid closedEllipsoid( final double[] center, final double[] semiAxisLengths )
	{
		return new ClosedWritableEllipsoid( center, semiAxisLengths );
	}

	/**
	 * Creates a {@link WritableEllipsoid} with {@link BoundaryType#OPEN
	 * open} {@link MaskPredicate#boundaryType() boundaries}.
	 */
	public static WritableEllipsoid openEllipsoid( final double[] center, final double[] semiAxisLengths )
	{
		return new OpenWritableEllipsoid( center, semiAxisLengths );
	}

	// -- Line --

	/** Creates a {@link WritableLine} from two {@link RealLocalizable} points. */
	public static WritableLine line( final RealLocalizable pointOne, final RealLocalizable pointTwo )
	{
		return new DefaultWritableLine( pointOne, pointTwo );
	}

	/** Creates a {@link WritableLine} from two {@code double[]} points. */
	public static WritableLine line( final double[] pointOne, final double[] pointTwo, final boolean copy )
	{
		return new DefaultWritableLine( pointOne, pointTwo, copy );
	}

	// -- Point --

	/**
	 * Creates a {@link WritablePointMask} from {@code double[]} coordinates.
	 */
	public static WritablePointMask pointMask( final double[] point )
	{
		return new DefaultWritablePointMask( point );
	}

	/**
	 * Creates a {@link WritablePointMask} from a {@link RealLocalizable} point.
	 */
	public static WritablePointMask pointMask( final RealLocalizable point )
	{
		return new DefaultWritablePointMask( point );
	}

	// -- Polygon2D --

	/**
	 * Creates a {@link WritablePolygon2D} from a list of vertices, with
	 * {@link BoundaryType#UNSPECIFIED unspecified}
	 * {@link MaskPredicate#boundaryType() boundaries}.
	 */
	public static WritablePolygon2D polygon2D( final List< ? extends RealLocalizable > vertices )
	{
		return new DefaultWritablePolygon2D( vertices );
	}

	/**
	 * Creates a {@link WritablePolygon2D} from a list of (x, y) coordinates,
	 * with {@link BoundaryType#UNSPECIFIED unspecified}
	 * {@link MaskPredicate#boundaryType() boundaries}.
	 */
	public static WritablePolygon2D polygon2D( final double[] x, final double[] y )
	{
		return new DefaultWritablePolygon2D( x, y );
	}

	/**
	 * Creates a {@link WritablePolygon2D} from a list of vertices, with
	 * {@link BoundaryType#CLOSED closed} {@link MaskPredicate#boundaryType()
	 * boundaries}.
	 */
	public static WritablePolygon2D closedPolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		return new ClosedWritablePolygon2D( vertices );
	}

	/**
	 * Creates a {@link WritablePolygon2D} from a list of (x, y) coordinates,
	 * with {@link BoundaryType#CLOSED closed}
	 * {@link MaskPredicate#boundaryType() boundaries}.
	 */
	public static WritablePolygon2D closedPolygon2D( final double[] x, final double[] y )
	{
		return new ClosedWritablePolygon2D( x, y );
	}

	/**
	 * Creates a {@link WritablePolygon2D} from a list of vertices, with
	 * {@link BoundaryType#OPEN open} {@link MaskPredicate#boundaryType()
	 * boundaries}.
	 */
	public static WritablePolygon2D openPolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		return new OpenWritablePolygon2D( vertices );
	}

	/**
	 * Creates a {@link WritablePolygon2D} from a list of (x, y) coordinates,
	 * with {@link BoundaryType#OPEN open} {@link MaskPredicate#boundaryType()
	 * boundaries}.
	 */
	public static WritablePolygon2D openPolygon2D( final double[] x, final double[] y )
	{
		return new OpenWritablePolygon2D( x, y );
	}

	// -- Polyline --

	/** Creates a {@link WritablePolyline} from a list of vertices. */
	public static WritablePolyline polyline( final List< ? extends RealLocalizable > vertices )
	{
		return new DefaultWritablePolyline( vertices );
	}

	// -- RealPointCollection --

	/** Creates a {@link WritableRealPointCollection} from a map of points. */
	public static < L extends RealLocalizable > WritableRealPointCollection< L > realPointCollection( final HashMap< TDoubleArrayList, L > points )
	{
		return new DefaultWritableRealPointCollection<>( points );
	}

	/** Creates a {@link WritableRealPointCollection} from a collection of points. */
	public static < L extends RealLocalizable > WritableRealPointCollection< L > realPointCollection( final Collection< L > points )
	{
		return new DefaultWritableRealPointCollection<>( points );
	}

	/** Creates a {@link RealPointCollection} from a {@link KDTree}. */
	public static < L extends RealLocalizable > RealPointCollection< L > kDTreeRealPointCollection( final KDTree< L > tree )
	{
		return new KDTreeRealPointCollection<>( tree );
	}

	/**
	 * Creates a {@link KDTree}-based {@link RealPointCollection} from a
	 * collection of points.
	 */
	public static < L extends RealLocalizable > KDTreeRealPointCollection< L > kDTreeRealPointCollection( final Collection< L > points )
	{
		return new KDTreeRealPointCollection<>( points );
	}

	/**
	 * Creates a {@link WritableRealPointCollection} from a
	 * {@Link RealPointSampleList}.
	 */
	public static < L extends RealLocalizable > WritableRealPointCollection< L > realPointSampleListRealPointCollection( final RealPointSampleList< L > points )
	{
		return new RealPointSampleListWritableRealPointCollection<>( points );
	}

	/**
	 * Creates a {@link RealPointSampleList}-based
	 * {@link WritableRealPointCollection} from a collection of points.
	 */
	public static < L extends RealLocalizable > RealPointSampleListWritableRealPointCollection< L > realPointSampleListRealPointCollection( final Collection< L > points )
	{
		return new RealPointSampleListWritableRealPointCollection<>( points );
	}

	// -- Sphere --

	/**
	 * Creates a {@link WritableSphere} with {@link BoundaryType#CLOSED closed}
	 * {@link MaskPredicate#boundaryType() boundaries}.
	 */
	public static WritableSphere closedSphere( final double[] center, final double radius )
	{
		return new ClosedWritableSphere( center, radius );
	}

	/**
	 * Creates a {@link WritableSphere} with {@link BoundaryType#OPEN open}
	 * {@link MaskPredicate#boundaryType() boundaries}.
	 */
	public static WritableSphere openSphere( final double[] center, final double radius )
	{
		return new OpenWritableSphere( center, radius );
	}

	// -- SuperEllipsoid --

	/**
	 * Creates a {@link WritableSuperEllipsoid} with {@link BoundaryType#CLOSED
	 * closed} {@link MaskPredicate#boundaryType() boundaries}.
	 */
	public static WritableSuperEllipsoid closedSuperEllipsoid( final double[] center, final double[] semiAxisLengths, final double exponent )
	{
		if ( exponent == 2 )
			return new ClosedWritableEllipsoid( center, semiAxisLengths );
		return new ClosedWritableSuperEllipsoid( center, semiAxisLengths, exponent );
	}

	/**
	 * Creates a {@link WritableSuperEllipsoid} with {@link BoundaryType#OPEN
	 * open} {@link MaskPredicate#boundaryType() boundaries}.
	 */
	public static WritableSuperEllipsoid openSuperEllipsoid( final double[] center, final double[] semiAxisLengths, final double exponent )
	{
		if ( exponent == 2 )
			return new OpenWritableEllipsoid( center, semiAxisLengths );
		return new OpenWritableSuperEllipsoid( center, semiAxisLengths, exponent );
	}
}
