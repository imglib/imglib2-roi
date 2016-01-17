package net.imglib2.roi.geometric;

import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.util.Contains;
import net.imglib2.type.logic.BoolType;
import net.imglib2.RealLocalizable;

public interface  RealRandomAccessibleRealIntervalContains extends RealRandomAccessibleRealInterval<BoolType>, Contains<RealLocalizable>
{

}
