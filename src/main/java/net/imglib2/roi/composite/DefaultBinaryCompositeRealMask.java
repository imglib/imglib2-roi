/*-
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
package net.imglib2.roi.composite;

import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.Operators.BinaryMaskOperator;
import net.imglib2.roi.RealMask;

/**
 * A {@link RealMask} which is the result of an operation on two
 * {@link Predicate}s.
 *
 * @author Tobias Pietzsch
 */
public class DefaultBinaryCompositeRealMask
		extends AbstractEuclideanSpace
		implements BinaryCompositeMaskPredicate< RealLocalizable >, RealMask
{
	private final BinaryMaskOperator operator;

	private final Predicate< ? super RealLocalizable > arg0;

	private final Predicate< ? super RealLocalizable > arg1;

	private final BoundaryType boundaryType;

	private final Predicate< ? super RealLocalizable > predicate;

	private final BinaryOperator< KnownConstant > knownConstantOp;

	public DefaultBinaryCompositeRealMask(
			final BinaryMaskOperator operator,
			final Predicate< ? super RealLocalizable > arg0,
			final Predicate< ? super RealLocalizable > arg1,
			final int numDimensions,
			final BoundaryType boundaryType,
			final BinaryOperator< KnownConstant > knownConstantOp )
	{
		super( numDimensions );
		this.operator = operator;
		this.arg0 = arg0;
		this.arg1 = arg1;
		this.boundaryType = boundaryType;
		this.predicate = operator.predicate( arg0, arg1 );
		this.knownConstantOp = knownConstantOp;
	}

	@Override
	public BoundaryType boundaryType()
	{
		return boundaryType;
	}

	@Override
	public KnownConstant knownConstant()
	{
		return knownConstantOp.apply( KnownConstant.of( arg0 ), KnownConstant.of( arg1 ) );
	}

	@Override
	public boolean test( final RealLocalizable localizable )
	{
		return predicate.test( localizable );
	}

	@Override
	public BinaryMaskOperator operator()
	{
		return operator;
	}

	@Override
	public Predicate< ? super RealLocalizable > arg0()
	{
		return arg0;
	}

	@Override
	public Predicate< ? super RealLocalizable > arg1()
	{
		return arg1;
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof BinaryCompositeMaskPredicate ) || !( obj instanceof RealMask ) )
			return false;

		final BinaryCompositeMaskPredicate< ? > b = ( BinaryCompositeMaskPredicate< ? > ) obj;
		return b.operator() == operator && arg0.equals( b.arg0() ) && arg1.equals( b.arg1() );
	}

	@Override
	public int hashCode()
	{
		return ( arg0.hashCode() + arg1.hashCode() * arg1.hashCode() + operator.hashCode() ) * 87;
	}
}
