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
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.RealMask;

/**
 * A {@link Box} which contains <b>all</b> edge points defined by the min and
 * max values in each dimension.
 *
 * @author Alison Walter
 * @author Robert Haase, Scientific Computing Facility, MPI-CBG,
 *         rhaase@mpi-cbg.de
 */
public class ClosedWritableBox extends AbstractWritableBox
{
	/**
	 * Creates an n-d rectangular {@link RealMask} in real space. The dimensionality
	 * is dictated by the length of the min array.
	 *
	 * @param min
	 *            An array containing the minimum position in each dimension. A
	 *            copy of this array is stored.
	 * @param max
	 *            An array containing maximum position in each dimension. A copy
	 *            of this array is stored.
	 */
	public ClosedWritableBox( final double[] min, final double[] max )
	{
		super( min, max );
	}

	@Override
	public boolean test( final RealLocalizable l )
	{
		boolean isInside = true;
		for ( int d = 0; d < n && isInside; d++ )
		{
			final double x = l.getDoublePosition( d );
			isInside &= x >= min[ d ] && x <= max[ d ];
		}
		return isInside;
	}

	@Override
	public BoundaryType boundaryType()
	{
		return BoundaryType.CLOSED;
	}
}
