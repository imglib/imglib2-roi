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
import java.util.HashSet;
import java.util.List;

import net.imglib2.KDTree;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPointSampleList;
import net.imglib2.roi.MaskPredicate;
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
import net.imglib2.roi.geom.real.NNSRealPointCollection;
import net.imglib2.roi.geom.real.OpenWritableBox;
import net.imglib2.roi.geom.real.OpenWritableEllipsoid;
import net.imglib2.roi.geom.real.OpenWritablePolygon2D;
import net.imglib2.roi.geom.real.OpenWritableSphere;
import net.imglib2.roi.geom.real.OpenWritableSuperEllipsoid;
import net.imglib2.roi.geom.real.PointMask;
import net.imglib2.roi.geom.real.RealPointCollection;
import net.imglib2.roi.geom.real.RealPointSampleListWritableRealPointCollection;
import net.imglib2.roi.geom.real.WritableBox;
import net.imglib2.roi.geom.real.WritableEllipsoid;
import net.imglib2.roi.geom.real.WritableLine;
import net.imglib2.roi.geom.real.WritablePolygon2D;
import net.imglib2.roi.geom.real.WritablePolyline;
import net.imglib2.roi.geom.real.WritableSphere;
import net.imglib2.roi.geom.real.WritableSuperEllipsoid;

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

	/** Creates a {@link ClosedWritableBox}. */
	public static WritableBox closedWritableBox( final double[] min, final double[] max )
	{
		return new ClosedWritableBox( min, max );
	}

	/** Creates an {@link OpenWritableBox}. */
	public static WritableBox openWritableBox( final double[] min, final double[] max )
	{
		return new OpenWritableBox( min, max );
	}

	// -- Ellipsoid --

	/** Creates a {@link ClosedWritableEllipsoid}. */
	public static WritableEllipsoid closedWritableEllipsoid( final double[] center, final double[] semiAxisLengths )
	{
		return new ClosedWritableEllipsoid( center, semiAxisLengths );
	}

	/** Creates an {@link OpenWritableEllipsoid}. */
	public static WritableEllipsoid openWritableEllipsoid( final double[] center, final double[] semiAxisLengths )
	{
		return new OpenWritableEllipsoid( center, semiAxisLengths );
	}

	// -- Line --

	/** Creates a {@link DefaultWritableLine}. */
	public static WritableLine writableLine( final RealLocalizable pointOne, final RealLocalizable pointTwo )
	{
		return new DefaultWritableLine( pointOne, pointTwo );
	}

	/** Creates a {@link DefaultWritableLine}. */
	public static WritableLine writableLine( final double[] pointOne, final double[] pointTwo, final boolean copy )
	{
		return new DefaultWritableLine( pointOne, pointTwo, copy );
	}

	// -- Point --

	/** Creates a {@link DefaultWritablePointMask}. */
	public static PointMask writablePointMask( final double[] point )
	{
		return new DefaultWritablePointMask( point );
	}

	/** Creates a {@link DefaultWritablePointMask}. */
	public static PointMask writablePointMask( final RealLocalizable point )
	{
		return new DefaultWritablePointMask( point );
	}

	// -- Polygon2D --

	/** Creates a {@link DefaultWritablePolygon2D}. */
	public static WritablePolygon2D writablePolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		return new DefaultWritablePolygon2D( vertices );
	}

	/** Creates a {@link DefaultWritablePolygon2D}. */
	public static WritablePolygon2D writablePolygon2D( final double[] x, final double[] y )
	{
		return new DefaultWritablePolygon2D( x, y );
	}

	/** Creates a {@link ClosedWritablePolygon2D}. */
	public static WritablePolygon2D closedWritablePolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		return new ClosedWritablePolygon2D( vertices );
	}

	/** Creates a {@link ClosedWritablePolygon2D}. */
	public static WritablePolygon2D closedWritablePolygon2D( final double[] x, final double[] y )
	{
		return new ClosedWritablePolygon2D( x, y );
	}

	/** Creates an {@link OpenWritablePolygon2D}. */
	public static WritablePolygon2D openWritablePolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		return new OpenWritablePolygon2D( vertices );
	}

	/** Creates an {@link OpenWritablePolygon2D}. */
	public static WritablePolygon2D openWritablePolygon2D( final double[] x, final double[] y )
	{
		return new OpenWritablePolygon2D( x, y );
	}

	// -- Polyline --

	/** Creates a {@link DefaultWritablePolyline}. */
	public static WritablePolyline writablePolyline( final List< ? extends RealLocalizable > vertices )
	{
		return new DefaultWritablePolyline( vertices );
	}

	// -- RealPointCollection --

	/** Creates a {@link DefaultWritableRealPointCollection}. */
	public static < L extends RealLocalizable > RealPointCollection< L > writableRealPointCollection( final HashSet< L > points )
	{
		return new DefaultWritableRealPointCollection<>( points );
	}

	/** Creates a {@link DefaultWritableRealPointCollection}. */
	public static < L extends RealLocalizable > RealPointCollection< L > writableRealPointCollection( final Collection< L > points )
	{
		return new DefaultWritableRealPointCollection<>( points );
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

	/** Creates a {@link RealPointSampleListWritableRealPointCollection}. */
	public static < L extends RealLocalizable > NNSRealPointCollection< L > realPointSampleListWritableRealPointCollection( final RealPointSampleList< L > points )
	{
		return new RealPointSampleListWritableRealPointCollection<>( points );
	}

	// -- Sphere --

	/** Creates a {@link ClosedWritableSphere}. */
	public static WritableSphere closedWritableSphere( final double[] center, final double radius )
	{
		return new ClosedWritableSphere( center, radius );
	}

	/** Creates an {@link OpenWritableSphere}. */
	public static WritableSphere openWritableSphere( final double[] center, final double radius )
	{
		return new OpenWritableSphere( center, radius );
	}

	// -- SuperEllipsoid --

	/** Creates a {@link ClosedWritableSuperEllipsoid}. */
	public static WritableSuperEllipsoid closedWritableSuperEllipsoid( final double[] center, final double[] semiAxisLengths, final double exponent )
	{
		if ( exponent == 2 )
			return new ClosedWritableEllipsoid( center, semiAxisLengths );
		return new ClosedWritableSuperEllipsoid( center, semiAxisLengths, exponent );
	}

	/** Creates an {@link OpenWritableSuperEllipsoid}. */
	public static WritableSuperEllipsoid openWritableSuperEllipsoid( final double[] center, final double[] semiAxisLengths, final double exponent )
	{
		if ( exponent == 2 )
			return new OpenWritableEllipsoid( center, semiAxisLengths );
		return new OpenWritableSuperEllipsoid( center, semiAxisLengths, exponent );
	}
}
