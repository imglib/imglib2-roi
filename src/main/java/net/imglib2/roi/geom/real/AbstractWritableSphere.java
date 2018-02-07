/*
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
package net.imglib2.roi.geom.real;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.util.AbstractRealMaskPoint;
import net.imglib2.roi.util.RealLocalizableRealPositionable;

/**
 * Abstract base class for {@link WritableSphere} implementations.
 *
 * @author Alison Walter
 */
public abstract class AbstractWritableSphere extends AbstractEuclideanSpace implements WritableSphere
{
	protected final double[] center;

	protected double radius;

	/**
	 * Creates an n-d sphere.
	 *
	 * @param center
	 *            Point where the sphere is centered
	 * @param radius
	 *            Radius of the sphere
	 */
	public AbstractWritableSphere( final double[] center, final double radius )
	{
		super( center.length );

		if ( radius <= 0 )
			throw new IllegalArgumentException( "Radius must be positive and non-zero." );

		this.center = center.clone();
		this.radius = radius;
	}

	@Override
	public double realMin( final int d )
	{
		return center[ d ] - radius;
	}

	@Override
	public double realMax( final int d )
	{
		return center[ d ] + radius;
	}

	@Override
	public double exponent()
	{
		return 2;
	}

	@Override
	public double semiAxisLength( final int d )
	{
		return radius;
	}

	/**
	 * Returns the center. Changes to the returned center will mutate the
	 * sphere.
	 */
	@Override
	public RealLocalizableRealPositionable center()
	{
		return new SphereCenter( center );
	}

	@Override
	public double radius()
	{
		return radius;
	}

	/**
	 * Sets the radius of this sphere. The dimension, d, is ignored since
	 * spheres have the same extension in all dimensions.
	 */
	@Override
	public void setSemiAxisLength( final int d, final double length )
	{
		setRadius( length );
	}

	@Override
	public void setRadius( final double radius )
	{
		if ( radius <= 0 )
			throw new IllegalArgumentException( "Radius must be positive and non-zero." );
		this.radius = radius;
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof SuperEllipsoid ) )
			return false;

		final SuperEllipsoid se = ( SuperEllipsoid ) obj;
		if ( se.numDimensions() != n || 2 != se.exponent() || boundaryType() != se.boundaryType() )
			return false;

		for ( int i = 0; i < n; i++ )
		{
			if ( center[ i ] != se.center().getDoublePosition( i ) || radius != se.semiAxisLength( i ) )
				return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int result = 22;
		for ( int i = 0; i < n; i++ )
			result += 13 * center[ i ] + 13 * radius;

		result += 2; // for exponent

		if ( BoundaryType.CLOSED == boundaryType() )
			result += 5;
		else if ( BoundaryType.OPEN == boundaryType() )
			result += 8;
		else
			result += 0;

		return result;
	}

	// -- Helper methods --

	protected double distancePowered( final RealLocalizable l )
	{
		assert ( l.numDimensions() >= n ): "l must have no less than " + n + " dimensions";

		double distancePowered = 0;
		for ( int d = 0; d < n; d++ )
			distancePowered += ( l.getDoublePosition( d ) - center[ d ] ) * ( l.getDoublePosition( d ) - center[ d ] );

		return distancePowered;
	}

	// -- Helper classes --

	private class SphereCenter extends AbstractRealMaskPoint
	{

		public SphereCenter( final double[] pos )
		{
			super( pos );
		}

		@Override
		public void updateBounds()
		{
			// No implementation needed, min/max easy to compute
		}

	}
}
