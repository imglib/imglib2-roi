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
package net.imglib2.roi;

import net.imglib2.FinalInterval;
import net.imglib2.roi.mask.integer.DefaultMaskInterval;
import net.imglib2.roi.mask.real.DefaultRealMaskRealInterval;
import org.junit.Test;

public class AdaptingIntervalPerformanceTest
{
	@Test(timeout=5000)
	public void testRecursiveIntersectionRealIntervalPerformance()
	{
		RealMaskRealInterval maskInterval = new DefaultRealMaskRealInterval( new FinalInterval( 1, 1, 1 ), BoundaryType.UNSPECIFIED, t -> false, KnownConstant.ALL_FALSE );
		RealMaskRealInterval intersection = maskInterval;
		for ( int i = 0; i < 100; i++ )
			intersection = intersection.and( maskInterval );
		intersection.isEmpty();
	}

	@Test(timeout=5000)
	public void testRecursiveIntersectionIntervalPerformance()
	{
		MaskInterval maskInterval = new DefaultMaskInterval( new FinalInterval( 1, 1, 1 ), BoundaryType.UNSPECIFIED, t -> false, KnownConstant.ALL_FALSE );
		MaskInterval intersection = maskInterval;
		for ( int i = 0; i < 100; i++ )
			intersection = intersection.and( maskInterval );
		intersection.isEmpty();
	}

	@Test(timeout=5000)
	public void testRecursiveUnionRealIntervalPerformance()
	{
		RealMaskRealInterval maskInterval = new DefaultRealMaskRealInterval( new FinalInterval( 1, 1, 1 ), BoundaryType.UNSPECIFIED, t -> false, KnownConstant.ALL_FALSE );
		RealMaskRealInterval union = maskInterval;
		for ( int i = 0; i < 100; i++ )
			union = union.or( maskInterval );
		union.isEmpty();
	}

	@Test(timeout=5000)
	public void testRecursiveUnionIntervalPerformance()
	{
		MaskInterval maskInterval = new DefaultMaskInterval( new FinalInterval( 1, 1, 1 ), BoundaryType.UNSPECIFIED, t -> false, KnownConstant.ALL_FALSE );
		MaskInterval union = maskInterval;
		for ( int i = 0; i < 100; i++ )
			union = union.or( maskInterval );
		union.isEmpty();
	}
}
