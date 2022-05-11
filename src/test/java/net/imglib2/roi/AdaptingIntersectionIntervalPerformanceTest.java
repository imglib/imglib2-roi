package net.imglib2.roi;

import net.imglib2.FinalInterval;
import net.imglib2.roi.mask.integer.DefaultMaskInterval;
import net.imglib2.roi.mask.real.DefaultRealMaskRealInterval;
import org.junit.Test;

public class AdaptingIntersectionIntervalPerformanceTest
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
}
