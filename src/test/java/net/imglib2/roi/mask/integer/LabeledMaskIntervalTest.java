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
package net.imglib2.roi.mask.integer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.MaskInterval;

/**
 * Tests for {@link LabeledMaskInterval}.
 *
 * @author Alison Walter
 */
public class LabeledMaskIntervalTest
{

	@Test
	public void testMaskProperties()
	{
		final MaskInterval b = create2DBox( new long[] { 10, 12 }, new long[] { 15, 20 } );
		final LabeledMaskInterval< String > lb = new LabeledMaskInterval<>( b, "test" );

		assertEquals( "test", lb.getLabel() );
		assertEquals( b.numDimensions(), lb.numDimensions() );
		assertEquals( b.min( 0 ), lb.min( 0 ) );
		assertEquals( b.max( 0 ), lb.max( 0 ) );
		assertEquals( b.min( 1 ), lb.min( 1 ) );
		assertEquals( b.max( 1 ), lb.max( 1 ) );
		assertEquals( b.boundaryType(), lb.boundaryType() );
		assertEquals( b.knownConstant(), lb.knownConstant() );

		final Point pt = new Point( new long[] { 10, 18 } );
		assertEquals( b.test( pt ), lb.test( pt ) );
		pt.setPosition( new long[] { 12, 15 } );
		assertEquals( b.test( pt ), lb.test( pt ) );
		pt.setPosition( new long[] { 15, 20 } );
		assertEquals( b.test( pt ), lb.test( pt ) );
		pt.setPosition( new long[] { 0, 4 } );
		assertEquals( b.test( pt ), lb.test( pt ) );
	}

	@Test
	public void testEquals()
	{
		final MaskInterval b1 = create2DBox( new long[] { 5, 5 }, new long[] { 10, 10 } );
		final MaskInterval b2 = create2DBox( new long[] { 8, 10 }, new long[] { 11, 13 } );
		final LabeledMaskInterval< String > labelB1 = new LabeledMaskInterval<>( b1, "test" );
		final LabeledMaskInterval< String > dupLabelB1 = new LabeledMaskInterval<>( b1, "test" );
		final LabeledMaskInterval< String > diffLabelB1 = new LabeledMaskInterval<>( b1, "not test" );
		final LabeledMaskInterval< String > labelB2 = new LabeledMaskInterval< String >( b2, "test" );

		assertTrue( "Masks with same source and label should be equal!", labelB1.equals( dupLabelB1 ) );
		assertFalse( "Masks with same source different label should not be equal!", labelB1.equals( diffLabelB1 ) );
		assertFalse( "Masks with different sources and equivalent labels should not be equal!", labelB1.equals( labelB2 ) );
	}

	@Test
	public void testHashCode()
	{
		final MaskInterval b1 = create2DBox( new long[] { 5, 5 }, new long[] { 10, 10 } );
		final MaskInterval b2 = create2DBox( new long[] { 8, 10 }, new long[] { 11, 13 } );
		final LabeledMaskInterval< String > labelB1 = new LabeledMaskInterval<>( b1, "test" );
		final LabeledMaskInterval< String > dupLabelB1 = new LabeledMaskInterval<>( b1, "test" );
		final LabeledMaskInterval< String > diffLabelB1 = new LabeledMaskInterval<>( b1, "not test" );
		final LabeledMaskInterval< String > labelB2 = new LabeledMaskInterval< String >( b2, "test" );

		assertEquals( labelB1.hashCode(), dupLabelB1.hashCode() );
		assertNotEquals( labelB1.hashCode(), diffLabelB1.hashCode() );
		assertNotEquals( labelB1.hashCode(), labelB2.hashCode() );
	}

	// -- Helper methods --

	private static MaskInterval create2DBox( final long[] min, final long[] max )
	{
		return new DefaultMaskInterval( new FinalInterval( min, max ), BoundaryType.CLOSED, //
				p -> p.getLongPosition( 0 ) >= min[ 0 ] && p.getLongPosition( 0 ) <= max[ 0 ] && //
						p.getLongPosition( 1 ) >= min[ 1 ] && p.getLongPosition( 1 ) <= max[ 1 ], //
				KnownConstant.UNKNOWN );
	}
}
