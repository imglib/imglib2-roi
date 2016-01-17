package net.imglib2.roi.operators;

import net.imglib2.RealLocalizable;
import net.imglib2.roi.geometric.RealRandomAccessibleRealIntervalContains;

public class RealBinaryDifference  extends AbstractRealBinaryOperator {

	public RealBinaryDifference(RealRandomAccessibleRealIntervalContains leftOperand,
			RealRandomAccessibleRealIntervalContains rightOperand) {
		super(leftOperand, rightOperand);
		
	}

	@Override
	public boolean contains(RealLocalizable l) {
		return leftOperand.contains(l) && !rightOperand.contains(l);
	}
}
