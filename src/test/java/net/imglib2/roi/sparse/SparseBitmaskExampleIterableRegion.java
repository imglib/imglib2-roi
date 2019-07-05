package net.imglib2.roi.sparse;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.Regions;
import net.imglib2.type.logic.NativeBoolType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

/**
 * Using {@code SparseBitmask.region()} as an {@code IterableRegion}.
 *
 * @author Tobias Pietzsch
 */
public class SparseBitmaskExampleIterableRegion
{
	public static void main( String[] args )
	{
		/*
		 * Create and paint to a 2D bitmask.
		 */
		final SparseBitmask bitmask = new SparseBitmask( 2 );
		fillHypersphere( bitmask, 100, 100, 20 );
		fillHypersphere( bitmask, 50, 10, 200 );

		/*
		 * SparseBitmask.region() provides a (read-only) view as an IterableRegion<NativeBoolType>.
		 * The interval of bitmask.region() is the bounding box of the set pixels in the bitmask.
		 * The Cursor<Void> of the region visits the set (true) pixels in the bitmask.
		 * We use it to set pixels in a ArrayImg.
		 */
		final IterableRegion< NativeBoolType > region = bitmask.region();
		final Img< ARGBType > img = ArrayImgs.argbs( Intervals.dimensionsAsLongArray( region ) );
		final RandomAccessibleInterval< ARGBType > translated = Views.translate( img, Intervals.minAsLongArray( region ) );
		Regions.sample( region, translated ).forEach( t -> t.set( 0x0000ff00 ) );

		/*
		 * or more verbosely
		 */
//		final Cursor< Void > c = region.cursor();
//		final RandomAccess< ARGBType > a = translated.randomAccess();
//		while ( c.hasNext() )
//		{
//			c.fwd();
//			a.setPosition( c );
//			a.get().set( 0x0000ff00 );
//		}

		BdvFunctions.show( img, "img", Bdv.options().is2D() );
	}

	private static void fillHypersphere( RandomAccessible< NativeBoolType > img, long radius, long... position )
	{
		final RandomAccess< Neighborhood< NativeBoolType > > a = new HyperSphereShape( radius ).neighborhoodsRandomAccessible( img ).randomAccess();
		a.setPosition( position );
		a.get().forEach( t -> t.set( true ) );
	}
}
