package net.imglib2.roi.operators;

import net.imglib2.AbstractRealInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.RealRandomAccess;
import net.imglib2.roi.geometric.RealRandomAccessibleRealIntervalContains;
import net.imglib2.roi.util.Contains;
import net.imglib2.roi.util.ContainsRealRandomAccess;
import net.imglib2.type.logic.BoolType;

/**
 * This is an abstract class as base for all binary operators which allow combining continuous regions of interest like in the theory of sets. Example intersection of two ROIs.
 *
 * @author Robert Haase, Scientific Computing Facility, MPI-CBG, rhaase@mpi-cbg.de
 * @version 1.0.0 Jan 14, 2016
 *
 */
public abstract class AbstractRealBinaryOperator extends AbstractRealInterval implements RealRandomAccessibleRealIntervalContains {
	
	protected RealRandomAccessibleRealIntervalContains leftOperand;
	protected RealRandomAccessibleRealIntervalContains rightOperand;
	
	public AbstractRealBinaryOperator(RealRandomAccessibleRealIntervalContains leftOperand, RealRandomAccessibleRealIntervalContains rightOperand)
	{
		super(leftOperand.numDimensions());
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	@Override
	public RealRandomAccess<BoolType> realRandomAccess() {
		return new ContainsRealRandomAccess(this);
	}

	@Override
	public RealRandomAccess<BoolType> realRandomAccess(RealInterval interval) {
		
		return realRandomAccess();
	}

	@Override
	public abstract boolean contains(RealLocalizable l);

	@Override
	public Contains<RealLocalizable> copyContains() {
		return this;
	}


	@Override
	public double realMin( final int d )
	{
		return Math.min(leftOperand.realMin(d), rightOperand.realMin(d));
	}

	@Override
	public void realMin( final double[] realMin )
	{
		for ( int d = 0; d < n; ++d )
			realMin[ d ] = realMin(d);
	}

	@Override
	public void realMin( final RealPositionable realMin )
	{
		for ( int d = 0; d < n; ++d )
			realMin.setPosition( realMin(d), d );
	}

	@Override
	public double realMax( final int d )
	{
		return Math.max(leftOperand.realMax(d), rightOperand.realMax(d));
	}

	@Override
	public void realMax( final double[] realMax )
	{
		for ( int d = 0; d < n; ++d )
			realMax[ d ] = realMax(d) ;
	}

	@Override
	public void realMax( final RealPositionable realMax )
	{
		for ( int d = 0; d < n; ++d )
			realMax.setPosition( realMax(d), d );
	}
}
