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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.roi.Operation;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.ClosedBox;
import net.imglib2.roi.geom.real.OpenBox;
import net.imglib2.roi.mask.DefaultNaryOr;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.NaryOr;

import org.junit.Test;

/**
 * Tests {@link Operation}s.
 *
 * @author Alison Walter
 */
public class NaryOperationTest
{

	@Test
	public void testOrContains()
	{
		final Box bOne = new ClosedBox( new double[] { 0, 0 }, new double[] { 10, 10 } );
		final Box bTwo = new ClosedBox( new double[] { 5, 5 }, new double[] { 15, 15 } );
		final Box bThree = new ClosedBox( new double[] { -5, -5 }, new double[] { 5, 5 } );

		final List< Mask< RealLocalizable > > boxes = new ArrayList<>( 3 );
		boxes.add( bOne );
		boxes.add( bTwo );
		boxes.add( bThree );
		final NaryOr< RealLocalizable > or = new DefaultNaryOr<>( boxes );

		final RealLocalizable lOne = new RealPoint( new double[] { 7, 1 } );
		final RealLocalizable lTwo = new RealPoint( new double[] { 12, 13 } );
		final RealLocalizable lThree = new RealPoint( new double[] { -2, -5 } );

		assertTrue( bOne.contains( lOne ) );
		assertTrue( bTwo.contains( lTwo ) );
		assertTrue( bThree.contains( lThree ) );

		assertTrue( or.contains( lOne ) );
		assertTrue( or.contains( lTwo ) );
		assertTrue( or.contains( lThree ) );
		assertTrue( or.contains( new RealPoint( new double[] { 1, 1 } ) ) );
		assertTrue( or.contains( new RealPoint( new double[] { 6, 6 } ) ) );
	}

	@Test
	public void testOrBoundaryTypeSame()
	{
		final Box bOne = new OpenBox( new double[] { 0, 0 }, new double[] { 10, 10 } );
		final Box bTwo = new OpenBox( new double[] { 5, 5 }, new double[] { 15, 15 } );
		final Box bThree = new OpenBox( new double[] { -5, -5 }, new double[] { 5, 5 } );

		final List< Mask< RealLocalizable > > boxes = new ArrayList<>( 3 );
		boxes.add( bOne );
		boxes.add( bTwo );
		boxes.add( bThree );
		final NaryOr< RealLocalizable > or = new DefaultNaryOr<>( boxes );

		assertEquals(or.boundaryType(), Mask.BoundaryType.OPEN);
	}

	@Test
	public void testOrBoundaryTypeDifferent()
	{
		final Box bOne = new OpenBox( new double[] { 0, 0 }, new double[] { 10, 10 } );
		final Box bTwo = new OpenBox( new double[] { 5, 5 }, new double[] { 15, 15 } );
		final Box bThree = new ClosedBox( new double[] { -5, -5 }, new double[] { 5, 5 } );

		final List< Mask< RealLocalizable > > boxes = new ArrayList<>( 3 );
		boxes.add( bOne );
		boxes.add( bTwo );
		boxes.add( bThree );
		final NaryOr< RealLocalizable > or = new DefaultNaryOr<>( boxes );

		assertEquals(or.boundaryType(), Mask.BoundaryType.UNSPECIFIED);
	}
}
