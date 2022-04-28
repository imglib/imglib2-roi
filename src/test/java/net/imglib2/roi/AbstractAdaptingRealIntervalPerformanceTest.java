package net.imglib2.roi;

import net.imglib2.roi.geom.GeomMasks;
import org.junit.Test;

public class AbstractAdaptingRealIntervalPerformanceTest
{
	@Test
	public void testIntersectionRealIntervalPerformance()
	{
		final RealMaskRealInterval mask = GeomMasks.closedBox( new double[]{ 0, 0, 0 }, new double[]{ 1, 1, 1 } );
		RealMaskRealInterval intersectedMasks = mask;
		for ( int i = 0; i < 100; i++ )
			intersectedMasks = intersectedMasks.and( mask );
		intersectedMasks.realMin( 0 );
	}
}
