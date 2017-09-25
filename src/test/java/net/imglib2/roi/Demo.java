package net.imglib2.roi;

import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.ClosedWritableBox;
import net.imglib2.roi.geom.real.ClosedWritableSphere;
import net.imglib2.roi.geom.real.Sphere;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.util.Intervals;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.util.BdvSource;

public class Demo
{
	public static void testAnd()
	{
		// Create two Masks
		final Box b = new ClosedWritableBox( new double[] { 10, 10, 10 }, new double[] { 20, 20, 20 } );
		final Sphere s = new ClosedWritableSphere( new double[] { 10.5, 10.5, 10.5 }, 3.5 );
		/*
		 * NEW: Note that Box (from net.imglib2.troi.geom.real) is a (new)
		 * RealMaskRealInterval. Sphere is the old one. Because it is
		 * Predicate<?> and RealInterval, Masks operations will still do the
		 * right thing.
		 */

		// Create a new Mask which is the intersection of the two Masks
		final RealMaskRealInterval and1 = Masks.and( b, s );
		/*
		 * NEW: Note that there are no special method names for Integral/Real
		 * operations. Masks.and() has overloads for everything.
		 */

		final RealMaskRealInterval and2 = b.and( s );
		/*
		 * NEW: Masks operations are also available as interface methods that
		 * override and augment the Predicate<?>.and(), or(), negate() methods
		 * and add xor(), minus() methods.
		 */

		// Since one operands knew its bounds, the resulting Mask also knows its
		// bounds. These bounds are not guaranteed to represent the minimal
		// bounding box.
		/*
		 * NEW: Note that there is no instanceof-check necessary here. We know
		 * it's an interval from the type.
		 */

		// Wrap the intersection as a RealRandomAccessibleRealInterval
		final RealRandomAccessibleRealInterval< BoolType > rrari = Masks.toRealRandomAccessibleRealInterval( and1 );
		final Bdv bdv = BdvFunctions.show( rrari, Intervals.smallestContainingInterval( rrari ), "Box AND Sphere", Bdv.options() );
		/*
		 * NEW: Side note: Use Intervals.smallestContainingInterval( rrari )
		 * instead of new FinalInterval( new long[] { ( long ) rrari.realMin( 0
		 * ), ( long ) rrari.realMin( 1 ), ( long ) rrari.realMin( 2 ) }, new
		 * long[] { ( long ) rrari.realMax( 0 ), ( long ) rrari.realMax( 1 ), (
		 * long ) rrari.realMax( 2 ) } ) Works for all dimensionalities and is
		 * more correct than rounding.
		 */

		final BdvSource box = BdvFunctions.show( Masks.toRealRandomAccessibleRealInterval( b ), Intervals.smallestContainingInterval( b ), "Box", Bdv.options().addTo( bdv ) );
		box.setColor( new ARGBType( 0xff0000 ) );
	}

	public static final void main( final String... args )
	{
		testAnd();
	}
}
