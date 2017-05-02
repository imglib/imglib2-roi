/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

/**
 * Abstract base class for {@link SuperEllipsoid} implementations.
 *
 * @author Alison Walter
 */
public abstract class AbstractSuperEllipsoid extends AbstractEuclideanSpace implements SuperEllipsoid
{
	protected double exponent;

	protected double[] center;

	protected final double[] semiAxisLengths;

	/**
	 * Creates an n-d superellipsoid, where n is determined by the length of
	 * the smaller array.
	 *
	 * @param center
	 *            position of the superellipsoid in space, given in pixel
	 *            coordinates
	 * @param semiAxisLengths
	 *            array containing n elements representing half values of
	 *            width/height/depth/...
	 * @param exponent
	 */
	public AbstractSuperEllipsoid( final double[] center, final double[] semiAxisLengths, final double exponent )
	{
		super( Math.min( center.length, semiAxisLengths.length ) );

		if( exponent <= 0 )
			throw new IllegalArgumentException("exponent must be positve and non-zero");

		this.exponent = exponent;
		this.semiAxisLengths = new double[ n ];
		this.center = new double[ n ];

		for ( int i = 0; i < n; i++ )
		{
			final double val = semiAxisLengths[ i ];
			if ( val <= 0 )
				throw new IllegalArgumentException( "Semi-axis lengths must be positive and non-zero" );
			this.semiAxisLengths[ i ] = val;
			this.center[ i ] = center[ i ];
		}
	}

	@Override
	public abstract boolean contains( final RealLocalizable l );

	@Override
	public double exponent()
	{
		return exponent;
	}

	@Override
	public double semiAxisLength( final int d )
	{
		return semiAxisLengths[ d ];
	}

	/** Return copy of center */
	@Override
	public double[] center()
	{
		return center.clone();
	}

	@Override
	public void setExponent( final double exponent )
	{
		if( exponent <= 0 )
			throw new IllegalArgumentException("exponent must be positve and non-zero");
		this.exponent = exponent;
	}

	@Override
	public void setSemiAxisLength( final int d, final double length )
	{
		if ( length <= 0 )
			throw new IllegalArgumentException( "Semi-axis length must be positive and non-zero" );
		semiAxisLengths[ d ] = length;
	}

	/**
	 * Sets the center.
	 *
	 * @param center
	 *            if this array is longer than {@code n} it will be truncated,
	 *            if shorter an {@link IllegalArgumentException} is thrown.
	 */
	@Override
	public void setCenter( final double[] center )
	{
		if ( center.length < n )
			throw new IllegalArgumentException( "Center must have a length of at least " + n );
		System.arraycopy( center, 0, this.center, 0, n );
	}
}
