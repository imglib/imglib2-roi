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

package net.imglib2.roi.mask.integer;

import java.util.List;
import java.util.function.Predicate;

import net.imglib2.EuclideanSpace;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;
import net.imglib2.roi.mask.DefaultMaskOperationResult;
import net.imglib2.roi.mask.MaskOperationResult;

/**
 * A discrete space {@link MaskOperationResult} which is a {@link MaskInterval}.
 *
 * @author Alison Walter
 */
public class MaskIntervalOperationResult< T extends EuclideanSpace > extends DefaultMaskOperationResult< Localizable, T > implements MaskInterval
{
	private final Interval interval;

	/**
	 * Creates a {@link MaskInterval} which resulted from an operation.
	 *
	 * @param predicate
	 *            defines which points are included/excluded from this Mask
	 * @param bt
	 *            boundary behavior of this Mask
	 * @param operands
	 *            a list of Masks which were used to create this Mask
	 * @param operation
	 *            the specific operation performed on the operands
	 * @param interval
	 *            the bounds of this Mask
	 */
	public MaskIntervalOperationResult( final Predicate< Localizable > predicate, final BoundaryType bt, final List< T > operands, final Operation operation, final Interval interval )
	{
		super( predicate, bt, operands, operation );
		this.interval = interval;
	}

	@Override
	public long min( final int d )
	{
		return interval.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < numDimensions(); d++ )
			min[ d ] = min( d );
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < numDimensions(); d++ )
			min.setPosition( min( d ), d );
	}

	@Override
	public long max( final int d )
	{
		return interval.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < numDimensions(); d++ )
			max[ d ] = max( d );
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < numDimensions(); d++ )
			max.setPosition( max( d ), d );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < numDimensions(); d++ )
			dimensions[ d ] = dimension( d );
	}

	@Override
	public long dimension( final int d )
	{
		return max( d ) - min( d ) + 1;
	}

	@Override
	public double realMin( final int d )
	{
		return min( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < numDimensions(); d++ )
			min[ d ] = realMin( d );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < numDimensions(); d++ )
			min.setPosition( realMin( d ), d );
	}

	@Override
	public double realMax( final int d )
	{
		return max( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < numDimensions(); d++ )
			max[ d ] = realMax( d );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < numDimensions(); d++ )
			max.setPosition( realMax( d ), d );
	}
}
