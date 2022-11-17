/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.roi;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import org.junit.Before;
import org.junit.Test;

/**
 * Test consistency of unary and binary operations on enums
 * {@link KnownConstant} and {@link BoundaryType}.
 *
 * @param <T>
 *            enum type
 *
 * @author Tobias Pietzsch
 */
public abstract class EnumConsistencyTest< T >
{
	BinaryOperator< T > and;
	BinaryOperator< T > or;
	UnaryOperator< T > negate;
	BinaryOperator< T > minus;
	BinaryOperator< T > xor;
	ArrayList< T > values;

	/**
	 * for all l, r: l ^ r == (l && !r) || (!l && r)
	 */
	@Test
	public void xorDef()
	{
		for ( final T l : values )
			for ( final T r : values )
				assertEquals(
						xor.apply( l, r ),
						or.apply(
								and.apply( l, negate.apply( r ) ),
								and.apply( negate.apply( l ), r )
						)
				);
	}

	/**
	 * for all l, r: !(l && r) == !l || !r
	 * for all l, r: !(l || r) == !l && !r
	 */
	@Test
	public void deMorgan()
	{
		for ( final T l : values )
			for ( final T r : values )
			{
				assertEquals(
						negate.apply( and.apply( l, r ) ),
						or.apply( negate.apply( l ), negate.apply( r ) )
				);
				assertEquals(
						negate.apply( or.apply( l, r ) ),
						and.apply( negate.apply( l ), negate.apply( r ) )
				);
			}
	}

	/**
	 * for all l, r: l - r == (l && !r)
	 */
	@Test
	public void minusDef()
	{
		for ( final T l : values )
			for ( final T r : values )
				assertEquals(
						minus.apply( l, r ),
						and.apply( l, negate.apply( r )	)
				);
	}

	public static class BoundaryTypeTest extends EnumConsistencyTest< BoundaryType >
	{
		@Before
		public void setup()
		{
			and = BoundaryType::and;
			or = BoundaryType::or;
			negate = BoundaryType::negate;
			minus = BoundaryType::minus;
			xor = BoundaryType::xor;
			values = new ArrayList<>( Arrays.asList( BoundaryType.values() ) );
		}
	}

	public static class KnownConstantTest extends EnumConsistencyTest< KnownConstant >
	{
		@Before
		public void setup()
		{
			and = KnownConstant::and;
			or = KnownConstant::or;
			negate = KnownConstant::negate;
			minus = KnownConstant::minus;
			xor = KnownConstant::xor;
			values = new ArrayList<>( Arrays.asList( KnownConstant.values() ) );
		}
	}
}
