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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.imglib2.RealPoint;
import net.imglib2.roi.geom.real.ClosedSuperEllipsoid;
import net.imglib2.roi.geom.real.OpenSuperEllipsoid;
import net.imglib2.roi.geom.real.SuperEllipsoid;

import org.junit.Test;

/**
 * Tests for {@link SuperEllipsoid}.
 *
 * @author Alison Walter
 */
public class SuperEllipsoidTest
{
	@Test
	public void testOpen2DSuperEllipse()
	{
		final SuperEllipsoid se = new OpenSuperEllipsoid( new double[] { 10, 10 }, new double[] { 8, 8 }, 0.5 );

		// vertices
		assertFalse( se.contains( new RealPoint( new double[] { 10, 2 } ) ) );
		assertFalse( se.contains( new RealPoint( new double[] { 10, 18 } ) ) );
		assertFalse( se.contains( new RealPoint( new double[] { 2, 10 } ) ) );
		assertFalse( se.contains( new RealPoint( new double[] { 18, 10 } ) ) );

		// Inside ellipse
		assertTrue( se.contains( new RealPoint( new double[] { 10, 10 } ) ) );
		assertTrue( se.contains( new RealPoint( new double[] { 3, 10 } ) ) );

		// Outside ellipse
		assertFalse( se.contains( new RealPoint( new double[] { 6, 10.7 } ) ) );
		assertFalse( se.contains( new RealPoint( new double[] { 20, 11 } ) ) );

		// superellipsoid characteristics
		assertEquals( se.exponent(), 0.5, 0 );
		assertEquals( se.center()[ 0 ], 10, 0 );
		assertEquals( se.center()[ 1 ], 10, 0 );
		assertEquals( se.semiAxisLength( 0 ), 8, 0 );
		assertEquals( se.semiAxisLength( 1 ), 8, 0 );
	}

	@Test
	public void testClosed2DSuperEllipse()
	{
		final SuperEllipsoid se = new ClosedSuperEllipsoid( new double[] { 10, 10 }, new double[] { 8, 8 }, 0.5 );

		// vertices
		assertTrue( se.contains( new RealPoint( new double[] { 10, 2 } ) ) );
		assertTrue( se.contains( new RealPoint( new double[] { 10, 18 } ) ) );
		assertTrue( se.contains( new RealPoint( new double[] { 2, 10 } ) ) );
		assertTrue( se.contains( new RealPoint( new double[] { 18, 10 } ) ) );

		// Inside ellipse
		assertTrue( se.contains( new RealPoint( new double[] { 10, 10 } ) ) );
		assertTrue( se.contains( new RealPoint( new double[] { 6, 10.6 } ) ) );

		// Outside ellipse
		assertFalse( se.contains( new RealPoint( new double[] { 20, 11 } ) ) );
		assertFalse( se.contains( new RealPoint( new double[] { 11, 15 } ) ) );

		// superellipsoid characteristics
		assertEquals( se.exponent(), 0.5, 0 );
		assertEquals( se.center()[ 0 ], 10, 0 );
		assertEquals( se.center()[ 1 ], 10, 0 );
		assertEquals( se.semiAxisLength( 0 ), 8, 0 );
		assertEquals( se.semiAxisLength( 1 ), 8, 0 );
	}

	@Test
	public void testOpen3DSuperEllipsoid()
	{
		final SuperEllipsoid se = new OpenSuperEllipsoid( new double[] { 4, 7, 2 }, new double[] { 3, 10, 1.5 }, 6 );

		// vertices
		assertFalse( se.contains( new RealPoint( new double[] { 1, 7, 2 } ) ) );
		assertFalse( se.contains( new RealPoint( new double[] { 7, 7, 2 } ) ) );
		assertFalse( se.contains( new RealPoint( new double[] { 4, -3, 2 } ) ) );
		assertFalse( se.contains( new RealPoint( new double[] { 4, 17, 2 } ) ) );
		assertFalse( se.contains( new RealPoint( new double[] { 4, 7, 0.5 } ) ) );
		assertFalse( se.contains( new RealPoint( new double[] { 4, 7, 3.5 } ) ) );

		// Inside ellipse
		assertTrue( se.contains( new RealPoint( new double[] { 4, 7, 2 } ) ) );

		// Outside ellipse
		assertFalse( se.contains( new RealPoint( new double[] { 12, 0, 3 } ) ) );
		assertFalse( se.contains( new RealPoint( new double[] { 3, 13, 3.488 } ) ) );

		// superellipsoid characteristics
		assertEquals( se.exponent(), 6, 0 );
		assertEquals( se.center()[ 0 ], 4, 0 );
		assertEquals( se.center()[ 1 ], 7, 0 );
		assertEquals( se.center()[ 2 ], 2, 0 );
		assertEquals( se.semiAxisLength( 0 ), 3, 0 );
		assertEquals( se.semiAxisLength( 1 ), 10, 0 );
		assertEquals( se.semiAxisLength( 2 ), 1.5, 0 );
	}

	@Test
	public void testClosed3DSuperEllipsoid()
	{
		final SuperEllipsoid se = new ClosedSuperEllipsoid( new double[] { 4, 7, 2 }, new double[] { 3, 10, 1.5 }, 6 );

		// vertices
		assertTrue( se.contains( new RealPoint( new double[] { 1, 7, 2 } ) ) );
		assertTrue( se.contains( new RealPoint( new double[] { 7, 7, 2 } ) ) );
		assertTrue( se.contains( new RealPoint( new double[] { 4, -3, 2 } ) ) );
		assertTrue( se.contains( new RealPoint( new double[] { 4, 17, 2 } ) ) );
		assertTrue( se.contains( new RealPoint( new double[] { 4, 7, 0.5 } ) ) );
		assertTrue( se.contains( new RealPoint( new double[] { 4, 7, 3.5 } ) ) );

		// Inside ellipse
		assertTrue( se.contains( new RealPoint( new double[] { 4, 7, 2 } ) ) );
		assertTrue( se.contains( new RealPoint( new double[] { 3, 13, 3.487 } ) ) );

		// Outside ellipse
		assertFalse( se.contains( new RealPoint( new double[] { 12, 0, 3 } ) ) );

		// superellipsoid characteristics
		assertEquals( se.exponent(), 6, 0 );
		assertEquals( se.center()[ 0 ], 4, 0 );
		assertEquals( se.center()[ 1 ], 7, 0 );
		assertEquals( se.center()[ 2 ], 2, 0 );
		assertEquals( se.semiAxisLength( 0 ), 3, 0 );
		assertEquals( se.semiAxisLength( 1 ), 10, 0 );
		assertEquals( se.semiAxisLength( 2 ), 1.5, 0 );
	}
}