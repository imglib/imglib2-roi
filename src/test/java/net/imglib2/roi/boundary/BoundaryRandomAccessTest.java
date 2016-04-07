package net.imglib2.roi.boundary;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.logic.BitType;

public class BoundaryRandomAccessTest
{
	@Test
	public void testBoundaryRandomAccess8()
	{
		final int[] region = new int[]
		{
			0, 0, 1, 1, 1, 1,
			0, 0, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1,
			0, 1, 1, 1, 1, 0,
			0, 0, 1, 1, 0, 0
		};
		final int[] b8 = new int[]
		{
			0, 0, 1, 1, 1, 1,
			0, 0, 1, 0, 0, 1,
			1, 1, 1, 0, 0, 1,
			1, 1, 0, 0, 1, 1,
			0, 1, 1, 1, 1, 0,
			0, 0, 1, 1, 0, 0
		};

		final Img< BitType > regionImg = ArrayImgs.bits( 6, 6 );
		final Cursor< BitType > c = regionImg.localizingCursor();
		int i = 0;
		while ( c.hasNext() )
			c.next().set( region[ i++ ] != 0 );

		final BoundaryRandomAccess8< BitType > ba = new BoundaryRandomAccess8< BitType >( regionImg );
		c.reset();
		i = 0;
		while ( c.hasNext() )
		{
			c.fwd();
			ba.setPosition( c );
			assertTrue( ba.get().get() == ( b8[ i++ ] != 0 ) );
		}
	}

	@Test
	public void testBoundaryRandomAccess4()
	{
		final int[] region = new int[]
		{
			0, 0, 1, 1, 1, 1,
			0, 0, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1,
			0, 1, 1, 1, 1, 0,
			0, 0, 1, 1, 0, 0
		};
		final int[] b4 = new int[]
		{
			0, 0, 1, 1, 1, 1,
			0, 0, 1, 0, 0, 1,
			1, 1, 0, 0, 0, 1,
			1, 0, 0, 0, 0, 1,
			0, 1, 0, 0, 1, 0,
			0, 0, 1, 1, 0, 0
		};

		final Img< BitType > regionImg = ArrayImgs.bits( 6, 6 );
		final Cursor< BitType > c = regionImg.localizingCursor();
		int i = 0;
		while ( c.hasNext() )
			c.next().set( region[ i++ ] != 0 );

		final BoundaryRandomAccess4< BitType > ba = new BoundaryRandomAccess4< BitType >( regionImg );
		c.reset();
		i = 0;
		while ( c.hasNext() )
		{
			c.fwd();
			ba.setPosition( c );
			assertTrue( ba.get().get() == ( b4[ i++ ] != 0 ) );
		}
	}
}
