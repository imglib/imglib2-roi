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
import net.imglib2.roi.geom.real.ClosedSphere;
import net.imglib2.roi.geom.real.OpenSphere;
import net.imglib2.roi.geom.real.Sphere;

import org.junit.Test;

/**
 * Tests for {@link Sphere}.
 *
 * @author Alison Walter
 */
public class SphereTest
{
	@Test
	public void testOpenCircle()
	{
		final Sphere s = new OpenSphere( new double[] { 10, 10 }, 8 );

		// vertices
		assertFalse( s.contains( new RealPoint( new double[] { 2, 10 } ) ) );
		assertFalse( s.contains( new RealPoint( new double[] { 18, 10 } ) ) );
		assertFalse( s.contains( new RealPoint( new double[] { 10, 2 } ) ) );
		assertFalse( s.contains( new RealPoint( new double[] { 10, 18 } ) ) );

		// inside
		assertTrue( s.contains( new RealPoint( new double[] { 12, 9 } ) ) );

		// outside
		assertFalse( s.contains( new RealPoint( new double[] { 20, 1 } ) ) );

		// sphere characteristics
		assertEquals( s.exponent(), 2, 0 );
		assertEquals( s.center()[ 0 ], 10, 0 );
		assertEquals( s.center()[ 1 ], 10, 0 );
		assertEquals( s.semiAxisLength( 0 ), 8, 0 );
		assertEquals( s.semiAxisLength( 1 ), 8, 0 );
		assertEquals( s.radius(), 8, 0 );
	}

	@Test
	public void testClosedCircle()
	{
		final Sphere s = new ClosedSphere( new double[] { 10, 10 }, 8 );

		// vertices
		assertTrue( s.contains( new RealPoint( new double[] { 2, 10 } ) ) );
		assertTrue( s.contains( new RealPoint( new double[] { 18, 10 } ) ) );
		assertTrue( s.contains( new RealPoint( new double[] { 10, 2 } ) ) );
		assertTrue( s.contains( new RealPoint( new double[] { 10, 18 } ) ) );

		// inside
		assertTrue( s.contains( new RealPoint( new double[] { 12, 9 } ) ) );

		// outside
		assertFalse( s.contains( new RealPoint( new double[] { 20, 1 } ) ) );

		// sphere characteristics
		assertEquals( s.exponent(), 2, 0 );
		assertEquals( s.center()[ 0 ], 10, 0 );
		assertEquals( s.center()[ 1 ], 10, 0 );
		assertEquals( s.semiAxisLength( 0 ), 8, 0 );
		assertEquals( s.semiAxisLength( 1 ), 8, 0 );
		assertEquals( s.radius(), 8, 0 );
	}
}
