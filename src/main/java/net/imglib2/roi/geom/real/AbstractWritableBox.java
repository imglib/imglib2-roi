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

import net.imglib2.AbstractRealInterval;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.util.AbstractRealMaskPoint;
import net.imglib2.roi.util.RealLocalizableRealPositionable;

/**
 * Abstract base class for implementations of {@link WritableBox}.
 *
 * @author Alison Walter
 */
public abstract class AbstractWritableBox extends AbstractRealInterval implements WritableBox
{
	/**
	 * Creates an n-d rectangular {@link RealMask}. The dimensionality is
	 * dictated by the length of the min array.
	 *
	 * @param min
	 *            An array containing the minimum position in each dimension. A
	 *            copy of this array is stored.
	 * @param max
	 *            An array containing maximum position in each dimension. A copy
	 *            of this array is stored.
	 */
	public AbstractWritableBox( final double[] min, final double[] max )
	{
		super( min, max );
		if ( max.length < min.length )
			throw new IllegalArgumentException( "Max array cannot be smaller than the min array" );
	}

	@Override
	public double sideLength( final int d )
	{
		return Math.abs( max[ d ] - min[ d ] );
	}

	@Override
	public RealLocalizableRealPositionable center()
	{
		final double[] center = new double[ n ];
		for ( int d = 0; d < n; d++ )
		{
			center[ d ] = ( max[ d ] + min[ d ] ) / 2.0;
		}
		return new BoxCenter( center );
	}

	@Override
	public void setSideLength( final int d, final double length )
	{
		if ( length < 0 )
			throw new IllegalArgumentException( "Cannot have negative edge lengths " );
		final double center = ( max[ d ] + min[ d ] ) / 2.0;
		max[ d ] = center + length / 2.0;
		min[ d ] = center - length / 2.0;
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof Box ) )
			return false;

		final Box b = ( Box ) obj;
		if ( b.numDimensions() != n || boundaryType() != b.boundaryType() )
			return false;

		for ( int i = 0; i < n; i++ )
		{
			if ( min[ i ] != b.realMin( i ) || max[ i ] != b.realMax( i ) )
				return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int result = 17;
		for ( int i = 0; i < n; i++ )
			result += 31 * min[ i ] + 31 * max[ i ];
		if ( BoundaryType.CLOSED == boundaryType() )
			result += 5;
		else if ( BoundaryType.OPEN == boundaryType() )
			result += 8;
		else
			result += 0;
		return result;
	}

	// -- Helper classes --

	private class BoxCenter extends AbstractRealMaskPoint
	{
		protected BoxCenter( final double[] center )
		{
			super( center );
		}

		@Override
		public void updateBounds()
		{
			for ( int d = 0; d < n; d++ )
			{
				final double halfSideLength = sideLength( d ) / 2.0;
				max[ d ] = position[ d ] + halfSideLength;
				min[ d ] = position[ d ] - halfSideLength;
			}
		}
	}
}
