package net.imglib2.roi.sparse;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.type.logic.NativeBoolType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

/**
 * Using {@code SparseBitmask} as a (unbounded) {@code RandomAccessible<NativeBoolType>}.
 *
 * @author Tobias Pietzsch
 */
public class SparseBitmaskExample
{
	public static void main( String[] args )
	{
		/*
		 * Create a 2D SparseBitmask.
		 * SparseBitmask is a RandomAccessible<NativeBoolType>.
		 */
		final SparseBitmask bitmask = new SparseBitmask( 2 );

		/*
		 * Paint some stuff.
		 * bitmask grows as needed.
		 */
		Views.interval( bitmask, Intervals.createMinSize( 70, 140, 80, 40 ) ).forEach( t -> t.set( true ) );
		fillHypersphere( bitmask, 100, 100, 20 );
		fillHypersphere( bitmask, 50, 10, 200 );
//		fillHypersphere( bitmask, 500, -10000, -200 );
//		fillHypersphere( bitmask, 5000, 1000, 20000 );

		/*
		 * Display in BDV.
		 * bitmask is an unbounded RandomAccessible, so we need to provide an Interval.
		 */
		BdvFunctions.show( bitmask, Intervals.createMinSize( 0, 0, 1000, 1000 ), "bitmask", Bdv.options().is2D() );

		/*
		 * Alternatively, we could use SparseBitmask.region(), which provides a (read-only) view as an IterableRegion<NativeBoolType>.
		 * The interval of bitmask.region() is the current bounding box of the set pixels in the bitmask.
		 */
//		BdvFunctions.show( bitmask.region(), "bitmask", Bdv.options().is2D() );
	}

	private static void fillHypersphere( RandomAccessible< NativeBoolType > img, long radius, long... position )
	{
		final RandomAccess< Neighborhood< NativeBoolType > > a = new HyperSphereShape( radius ).neighborhoodsRandomAccessible( img ).randomAccess();
		a.setPosition( position );
		a.get().forEach( t -> t.set( true ) );
	}
}
