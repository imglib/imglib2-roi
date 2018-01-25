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

import net.imglib2.RealLocalizable;

/**
 * Abstract base class for {@link WritableEllipsoid} implementations.
 *
 * @author Alison Walter
 */
public abstract class AbstractWritableEllipsoid extends AbstractWritableSuperEllipsoid implements WritableEllipsoid
{

	/**
	 * Creates an n-d ellipsoid, where n is determined by the length of the
	 * smaller array.
	 *
	 * @param center
	 *            Array containing the positions in each dimension at which the
	 *            ellipsoid is centered. A copy of this array is stored.
	 * @param semiAxisLengths
	 *            Array containing the lengths of the semi-axes in each
	 *            dimension. A copy of this array is stored.
	 */
	public AbstractWritableEllipsoid( final double[] center, final double[] semiAxisLengths )
	{
		super( center, semiAxisLengths, 2 );
	}

	/**
	 * Ellipsoids have exponents of 2.
	 *
	 * @throws UnsupportedOperationException
	 *             Ellipsoids, by definition, have an exponent of 2
	 */
	@Override
	public void setExponent( final double exponent )
	{
		throw new UnsupportedOperationException( "setExponent: can only have an exponent of 2" );
	}

	// -- Helper methods --

	/**
	 * Computes the unit distance squared between a given location and the
	 * center of the ellipsoid.
	 *
	 * @param l
	 *            location to check
	 * @return squared unit distance
	 */
	@Override
	protected double distancePowered( final RealLocalizable l )
	{
		assert ( l.numDimensions() >= n ): "l must have no less than " + n + " dimensions";

		double distancePowered = 0;
		for ( int d = 0; d < n; d++ )
			distancePowered += ( ( l.getDoublePosition( d ) - center[ d ] ) / semiAxisLengths[ d ] ) * ( ( l.getDoublePosition( d ) - center[ d ] ) / semiAxisLengths[ d ] );

		return distancePowered;
	}
}
