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

package net.imglib2.roi.mask;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;

/**
 * Default implementation for {@link MaskOperationResult}.
 *
 * @author Alison Walter
 *
 * @param <L>
 *            location in N-space; typically a {@link RealLocalizable} or
 *            subtype (e.g., {@link Localizable}).
 * @param <T>
 *            the type of {@link Mask} used to compose this, they may or may not
 *            have intervals
 */
public class DefaultMaskOperationResult< L, T > implements MaskOperationResult< L, T >
{
	private final Predicate< L > predicate;

	private final BoundaryType bt;

	private final List< T > operands;

	private final Operation operation;

	private int numDimensions;

	/**
	 * Creates a DefaultMaskOperationResult. When using this constructor the
	 * first element in the {@code List} <strong>must</strong> be a
	 * {@link EuclideanSpace}, if not an error will be thrown. This
	 * EuclideanSpace will be used to compute the dimensionality of this
	 * {@link Mask}.
	 *
	 * @param predicate
	 *            defines the behavior which specifies if a point is part of the
	 *            Mask
	 * @param bt
	 *            specifies if points on the boundary are or are not part of the
	 *            Mask, or some other undefined behavior
	 * @param operands
	 *            the Masks which were used to compute this result
	 * @param operation
	 *            the specific operation which was performed on the operands
	 */
	public DefaultMaskOperationResult( final Predicate< L > predicate, final BoundaryType bt, final List< T > operands, final Operation operation )
	{
		this( predicate, bt, inferDimensions( operands ), operands, operation );
	}

	/**
	 * Creates a DefaultMaskOperationResult.
	 *
	 * @param predicate
	 *            defines the behavior which specifies if a point is part of the
	 *            Mask
	 * @param bt
	 *            specifies if points on the boundary are or are not part of the
	 *            Mask, or some other undefined behavior
	 * @param numDimensions
	 *            specifies the number of dimensions this resulting Mask
	 *            occupies, this should be the same as the dimensions of each of
	 *            the operands
	 * @param operands
	 *            the Masks which were used to compute this result
	 * @param operation
	 *            the specific operation which was performed on the operands
	 */
	public DefaultMaskOperationResult( final Predicate< L > predicate, final BoundaryType bt, final int numDimensions, final List< T > operands, final Operation operation )
	{
		this.predicate = predicate;
		this.bt = bt;
		this.numDimensions = numDimensions;
		this.operands = operands;
		this.operation = operation;
	}

	@Override
	public boolean test( final L t )
	{
		return predicate.test( t );
	}

	@Override
	public List< T > operands()
	{
		return Collections.unmodifiableList( operands );
	}

	@Override
	public BoundaryType boundaryType()
	{
		return bt;
	}

	@Override
	public Operation operation()
	{
		return operation;
	}

	@Override
	public int numDimensions()
	{
		return numDimensions;
	}

	// -- Helper methods --

	private static int inferDimensions( final List< ? > operands )
	{
		if ( operands == null || operands.size() < 1 )
			throw new IllegalArgumentException( "Need at least one operand" );
		final Object firstOperand = operands.get( 0 );
		if ( !( firstOperand instanceof EuclideanSpace ) )
			throw new IllegalArgumentException( "First argument must extend EuclideanSpace" );
		final EuclideanSpace space = ( EuclideanSpace ) firstOperand;
		return space.numDimensions();
	}
}
