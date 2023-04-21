/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.roi.util;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;

/**
 * A {@link Localizable} that is the location of another {@link Localizable}
 * plus an offset.
 *
 * @author Tobias Pietzsch
 */
public class OffsetLocalizable< L extends Localizable >
		extends AbstractEuclideanSpace
		implements Localizable
{
	protected L source;

	protected long[] offset;

	public OffsetLocalizable( final L source, final long[] offset )
	{
		super( source.numDimensions() );
		this.source = source;
		this.offset = offset;
	}

	@Override
	public void localize( final float[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = getFloatPosition( d );
	}

	@Override
	public void localize( final double[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = getDoublePosition( d );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return source.getFloatPosition( d ) + offset[ d ];
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return source.getDoublePosition( d ) + offset[ d ];
	}

	@Override
	public void localize( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = getIntPosition( d );
	}

	@Override
	public void localize( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = getLongPosition( d );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return ( int ) ( source.getIntPosition( d ) + offset[ d ] );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return source.getLongPosition( d ) + offset[ d ];
	}
}
