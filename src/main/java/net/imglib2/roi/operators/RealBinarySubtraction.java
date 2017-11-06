package net.imglib2.roi.operators;

import net.imglib2.RealLocalizable;
import net.imglib2.roi.geometric.RealRandomAccessibleRealIntervalContains;

/**
 * This class represents the difference of two regions of interest.
 *
 * @author Robert Haase, Scientific Computing Facility, MPI-CBG, rhaase@mpi-cbg.de
 * @version 1.0.0 Jan 14, 2016
 *
 */
public class RealBinarySubtraction extends AbstractRealBinaryOperator {

	public RealBinarySubtraction(RealRandomAccessibleRealIntervalContains leftOperand,
								 RealRandomAccessibleRealIntervalContains rightOperand) {
		super(leftOperand, rightOperand);
		
	}

	@Override
	public boolean contains(RealLocalizable l) {
		return leftOperand.contains(l) && !rightOperand.contains(l);
	}
}
