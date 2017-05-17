package net.imglib2.roi.geometric;

import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.util.Contains;
import net.imglib2.type.logic.BoolType;
import net.imglib2.RealLocalizable;

/**
 * This interface makes rectangles, elipsoids, unions, intersections, ... combinable in n-dimensional continuous space.
 *
 * @author Robert Haase, Scientific Computing Facility, MPI-CBG, rhaase@mpi-cbg.de
 * @version 1.0.0 Jan 14, 2016
 *
 */
public interface  RealRandomAccessibleRealIntervalContains extends RealRandomAccessibleRealInterval<BoolType>, Contains<RealLocalizable>
{

}
