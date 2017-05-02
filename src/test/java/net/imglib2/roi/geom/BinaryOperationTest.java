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
package net.imglib2.roi.geom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.roi.BinaryOperation;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.ClosedBox;
import net.imglib2.roi.geom.real.OpenBox;
import net.imglib2.roi.mask.DefaultAnd;
import net.imglib2.roi.mask.DefaultOr;
import net.imglib2.roi.mask.DefaultSubtract;
import net.imglib2.roi.mask.DefaultXor;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.Mask.BoundaryType;

import org.junit.Test;

/**
 * Tests {@link BinaryOperation}s.
 *
 * @author Alison Walter
 */
public class BinaryOperationTest
{
	@Test
	public void testDefaultAnd()
	{
		final Box b1 = new ClosedBox( new double[] { 1, 3 }, new double[] { 7, 10 } );
		final Box b2 = new OpenBox( new double[] { 3, 3 }, new double[] { 12, 13 } );
		final Mask< RealLocalizable > rm = new DefaultAnd<>( b1, b2 );

		assertTrue( rm.contains( new RealPoint( new double[] { 4, 5 } ) ) );
		// b1 contains boundary points
		assertTrue( rm.contains( new RealPoint( new double[] { 7, 10 } ) ) );
		assertTrue( rm.contains( new RealPoint( new double[] { 3.1, 9.2 } ) ) );

		// b2 doesn't contain boundary points
		assertFalse( rm.contains( new RealPoint( new double[] { 3, 3 } ) ) );
		assertFalse( rm.contains( new RealPoint( new double[] { 100, 1 } ) ) );
		assertFalse( rm.contains( new RealPoint( new double[] { 5, 3 } ) ) );

		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );
	}

	@Test
	public void testDefaultOr()
	{
		final Box b = new ClosedBox( new double[] { 3, 3 }, new double[] { 7, 7 } );
		final Box b2 = new ClosedBox( new double[] { 4, 4 }, new double[] { 8, 8 } );
		final Mask< RealLocalizable > rm = new DefaultOr<>( b, b2 );

		assertTrue( rm.contains( new RealPoint( new double[] { 4, 8 } ) ) );
		assertTrue( rm.contains( new RealPoint( new double[] { 6, 5 } ) ) );
		assertTrue( rm.contains( new RealPoint( new double[] { 7.5, 4.3 } ) ) );
		assertTrue( rm.contains( new RealPoint( new double[] { 8, 7 } ) ) );

		assertFalse( rm.contains( new RealPoint( new double[] { 3, 8 } ) ) );
		assertFalse( rm.contains( new RealPoint( new double[] { 10, 10 } ) ) );

		assertTrue( rm.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testDefaultSubtract()
	{
		final Box b1 = new OpenBox( new double[] { 1, 4 }, new double[] { 10, 11 } );
		final Box b2 = new OpenBox( new double[] { 2, 3 }, new double[] { 9, 16 } );
		final Mask< RealLocalizable > rm = new DefaultSubtract<>( b1, b2 );

		assertTrue( rm.contains( new RealPoint( new double[] { 2, 5 } ) ) );
		assertTrue( rm.contains( new RealPoint( new double[] { 1.5, 10 } ) ) );
		assertTrue( rm.contains( new RealPoint( new double[] { 9.8, 8 } ) ) );
		// b2 doesn't contain boundary points
		assertTrue( rm.contains( new RealPoint( new double[] { 9, 4.1 } ) ) );

		assertFalse( rm.contains( new RealPoint( new double[] { 15, 7 } ) ) );
		assertFalse( rm.contains( new RealPoint( new double[] { 3, 4 } ) ) );
		assertFalse( rm.contains( new RealPoint( new double[] { 6, 7 } ) ) );
		assertFalse( rm.contains( new RealPoint( new double[] { 8, 15 } ) ) );

		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );
	}

	@Test
	public void testDefaultXor()
	{
		final Box b1 = new ClosedBox( new double[] { 3, 3 }, new double[] { 10, 10 } );
		final Box b2 = new ClosedBox( new double[] { 4, 4 }, new double[] { 8, 7 } );
		final Mask< RealLocalizable > rm = new DefaultXor<>( b1, b2 );

		assertTrue( rm.contains( new RealPoint( new double[] { 3, 8 } ) ) );
		assertTrue( rm.contains( new RealPoint( new double[] { 9, 4 } ) ) );
		assertTrue( rm.contains( new RealPoint( new double[] { 5, 8 } ) ) );

		assertFalse( rm.contains( new RealPoint( new double[] { 5, 5 } ) ) );
		assertFalse( rm.contains( new RealPoint( new double[] { 20, 1 } ) ) );

		assertTrue( rm.boundaryType() == BoundaryType.UNSPECIFIED );
	}
}
