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

import java.util.Arrays;

import net.imglib2.AbstractRealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.geom.GeomMaths;
import net.imglib2.roi.util.AbstractRealMaskPoint;
import net.imglib2.roi.util.RealLocalizableRealPositionable;
import net.imglib2.util.Intervals;

/**
 * Represents a line segment as defined by two end points, which can be embedded
 * in n-dimensional space. Lines can only be closed.
 *
 * @author Alison Walter
 */
public class DefaultWritableLine extends AbstractRealInterval implements WritableLine
{
	private double[] pointOne;

	private double[] pointTwo;

	/**
	 * Creates a line with endpoints at the given positions.
	 *
	 * @param pointOne
	 *            The position of the first point. This position will be copied
	 *            and stored as a {@code double[]}. This is the position which
	 *            will be returned by {@link #endpointOne()}.
	 * @param pointTwo
	 *            The position of the second point. This position will be copied
	 *            and stored as a {@code double[]}. This is the position which
	 *            will be returned by {@link #endpointTwo()}.
	 */
	public DefaultWritableLine( final RealLocalizable pointOne, final RealLocalizable pointTwo )
	{
		this( createArray( pointOne ), createArray( pointTwo ), false );
	}

	/**
	 * Creates a line with endpoints at the given positions. If {@code pointOne}
	 * and {@code pointTwo} do not have the same length, the dimensionality of
	 * the space is the smaller of the two. If the arrays have uneven lengths
	 * the arrays will be copied and truncated, regardless of the value of
	 * {@code copy}.
	 *
	 * @param pointOne
	 *            The position of the first point. This is the position which
	 *            will be returned by {@link #endpointOne()}.
	 * @param pointTwo
	 *            The position of the second point.This is the position which
	 *            will be returned by {@link #endpointTwo()}.
	 * @param copy
	 *            If true, pointOne and pointTwo arrays are copied and stored.
	 *            If false, copies are not made.
	 */
	public DefaultWritableLine( final double[] pointOne, final double[] pointTwo, final boolean copy )
	{
		super( Math.min( pointOne.length, pointTwo.length ) );
		if ( copy || pointOne.length != pointTwo.length )
		{
			this.pointOne = Arrays.copyOf( pointOne, n );
			this.pointTwo = Arrays.copyOf( pointTwo, n );
		}
		else
		{
			this.pointOne = pointOne;
			this.pointTwo = pointTwo;
		}

		for ( int d = 0; d < n; d++ )
		{
			min[ d ] = Math.min( this.pointOne[ d ], this.pointTwo[ d ] );
			max[ d ] = Math.max( this.pointOne[ d ], this.pointTwo[ d ] );
		}
	}

	@Override
	public boolean test( final RealLocalizable l )
	{
		if ( Intervals.contains( this, l ) ) { return GeomMaths.lineContains( pointOne, pointTwo, l, n ); }
		return false;
	}

	/**
	 * Returns a {@link RealLocalizableRealPositionable} positioned at the same
	 * location as the first location passed to the constructor.
	 */
	@Override
	public RealLocalizableRealPositionable endpointOne()
	{
		return new LineEndPoint( pointOne );
	}

	/**
	 * Returns a {@link RealLocalizableRealPositionable} positioned at the same
	 * location as the second location passed to the constructor.
	 */
	@Override
	public RealLocalizableRealPositionable endpointTwo()
	{
		return new LineEndPoint( pointTwo );
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof Line ) )
			return false;

		final Line l = ( Line ) obj;
		if ( l.numDimensions() != n || boundaryType() != l.boundaryType() )
			return false;

		for ( int d = 0; d < n; d++ )
		{
			if ( pointOne[ d ] != l.endpointOne().getDoublePosition( d ) || pointTwo[ d ] != l.endpointTwo().getDoublePosition( d ) )
				return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int result = 119;
		for ( int i = 0; i < n; i++ )
			result += 53 * pointOne[ i ] + 91 * pointTwo[ i ];
		return result + 5;
	}

	// -- Helper methods --

	/**
	 * Creates a {@code double[]} from a {@link RealLocalizable}.
	 *
	 * @param l
	 *            {@link RealLocalizable} to extract position from
	 * @return {@code double[]} containing the position of l.
	 */
	private static double[] createArray( final RealLocalizable l )
	{
		final double[] pt = new double[ l.numDimensions() ];
		l.localize( pt );
		return pt;
	}

	// -- Helper classes --

	private class LineEndPoint extends AbstractRealMaskPoint
	{
		public LineEndPoint( final double[] pos )
		{
			super( pos );
		}

		@Override
		public void updateBounds()
		{
			for ( int d = 0; d < n; d++ )
			{
				min[ d ] = Math.min( pointOne[ d ], pointTwo[ d ] );
				max[ d ] = Math.max( pointOne[ d ], pointTwo[ d ] );
			}
		}
	}
}
