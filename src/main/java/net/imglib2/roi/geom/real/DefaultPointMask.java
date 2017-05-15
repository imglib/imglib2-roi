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
import net.imglib2.roi.mask.Mask;

/**
 * A {@link PointMask} specified by the given location.
 *
 * @author Alison Walter
 */
public class DefaultPointMask extends AbstractEuclideanSpace implements PointMask
{

	private final double[] location;

	/**
	 * Creates a {@link PointMask} with the given point, such that only that
	 * point is contained in the {@link Mask}. The dimensionality of the space
	 * is determined by the number of dimensions of {@code pt}.
	 *
	 * @param pt
	 *            The point which the mask should contain.
	 */
	public DefaultPointMask( final RealLocalizable pt )
	{
		super( pt.numDimensions() );
		location = new double[ n ];
		pt.localize( location );
	}

	/**
	 * Creates a {@link PointMask} with given array, such that only that
	 * location is contained in the {@link Mask}.
	 *
	 * @param pt
	 *            Array containing the location of the point in n-d space, where
	 *            n is the array length. A copy of this array is stored.
	 */
	public DefaultPointMask( final double[] pt )
	{
		super( pt.length );
		location = pt.clone();
	}

	@Override
	public boolean contains( final RealLocalizable l )
	{
		for ( int i = 0; i < n; i++ )
		{
			if ( l.getDoublePosition( i ) != location[ i ] )
				return false;
		}
		return true;
	}

	/** Returns a copy of the location. */
	@Override
	public double[] location()
	{
		return location.clone();
	}

	@Override
	public void setLocation( final double[] location )
	{
		if ( location.length < n )
			throw new IllegalArgumentException( "location must have a lenght of at least " + n );

		System.arraycopy( location, 0, this.location, 0, n );
	}

}
