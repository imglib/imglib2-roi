package net.imglib2.roi;

import net.imglib2.roi.geom.GeomMasks;
import org.junit.Test;

public class GeomMaskMultipleORTest
{
	@Test
	public void multipleOr()
	{
		final RealMaskRealInterval mask = GeomMasks.closedBox( new double[]{ 0, 0, 0 }, new double[]{ 1, 1, 1 } );
		RealMaskRealInterval test = mask;
		long start = System.currentTimeMillis();
		for ( int i = 0; i < 20; i++ )
		{
			test = test.or( mask );
			System.out.println( "RealMin: " + test.realMin( 0 ));
			System.out.println( "NumOr: " + i );
			System.out.println( "Duration [ms]:" + ( System.currentTimeMillis() - start ) );
			start = System.currentTimeMillis();
		}
	}
}
