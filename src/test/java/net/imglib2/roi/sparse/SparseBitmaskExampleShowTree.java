package net.imglib2.roi.sparse;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.util.BdvOverlay;
import bdv.util.BdvSource;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.roi.sparse.SparseBitmaskNTree.Node;
import net.imglib2.type.logic.NativeBoolType;
import net.imglib2.type.numeric.ARGBType;

/**
 * Visualize the quadtree underlying a (2D) SparseBitmask.
 *
 * @author Tobias Pietzsch
 */
public class SparseBitmaskExampleShowTree
{
	public static void main( String[] args )
	{
		/*
		 * Create, paint, and display 2D bitmask.
		 */
		final SparseBitmask bitmask = new SparseBitmask( 2 );
		fillHypersphere( bitmask, 100, 100, 20 );
		fillHypersphere( bitmask, 50, 10, 200 );
		fillHypersphere( bitmask, 500, -10000, -200 );
		fillHypersphere( bitmask, 5000, 1000, 20000 );
		Bdv bdv = BdvFunctions.show( bitmask.region(), "bitmask", Bdv.options().is2D() );

		/*
		 * Add BDV overlay to display a bunch of Intervals.
		 */
		final IntervalsOverlay nodes = new IntervalsOverlay();
		final BdvSource nodesSource = BdvFunctions.showOverlay( nodes, "nodes", Bdv.options().addTo( bdv ) );
		nodesSource.setColor( new ARGBType( 0x00ff0000 ) );

		/*
		 * Iterate all nodes of the quadtree backing the bitmask,
		 * adding the node intervals to the overlay.
		 */
		bitmask.tree().forEach( ( Node node ) -> nodes.add( node.interval() ) );
	}

	private static void fillHypersphere( RandomAccessible< NativeBoolType > img, long radius, long... position )
	{
		final RandomAccess< Neighborhood< NativeBoolType > > a = new HyperSphereShape( radius ).neighborhoodsRandomAccessible( img ).randomAccess();
		a.setPosition( position );
		a.get().forEach( t -> t.set( true ) );
	}

	/**
	 * Helper to paint some Intervals as a BDV overlay
	 */
	static class IntervalsOverlay extends BdvOverlay
	{
		private final List< Consumer< Graphics2D > > intervalPainters = new ArrayList<>();

		public synchronized  void add( final Interval interval )
		{
			final RealPoint[] points = new RealPoint[] {
					new RealPoint( interval.min( 0 ) - 0.5, interval.min( 1 ) - 0.5 ),
					new RealPoint( interval.min( 0 ) - 0.5, interval.max( 1 ) + 0.5 ),
					new RealPoint( interval.max( 0 ) + 0.5, interval.max( 1 ) + 0.5 ),
					new RealPoint( interval.max( 0 ) + 0.5, interval.min( 1 ) - 0.5 ),
			};
			intervalPainters.add( graphics -> {
				final Color col = new Color( info.getColor().get() );
				final AffineTransform3D transform = new AffineTransform3D();
				getCurrentTransform3D( transform );
				final double[] lPos = new double[ 3 ];
				final double[] gPos = new double[ 3 ];
				for ( int i = 0; i < points.length; i++ )
				{
					final int j = ( i + 1 ) % points.length;
					points[ i ].localize( lPos );
					transform.apply( lPos, gPos );
					final int x1 = ( int ) gPos[ 0 ];
					final int y1 = ( int ) gPos[ 1 ];
					points[ j ].localize( lPos );
					transform.apply( lPos, gPos );
					final int x2 = ( int ) gPos[ 0 ];
					final int y2 = ( int ) gPos[ 1 ];
					graphics.setColor( col );
					graphics.drawLine( x1, y1, x2, y2 );
				}
			} );
		}

		@Override
		protected synchronized void draw( final Graphics2D graphics )
		{
			intervalPainters.forEach( c -> c.accept( graphics ) );
		}
	}
}
