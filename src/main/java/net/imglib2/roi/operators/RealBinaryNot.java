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
 * 
 * This class represents a negation of a continuous region of interest.
 * 
 * TODO: Discuss if the negation of an ROI isn't endless in space and should therefore not be an Interval.
 *
 * @author Robert Haase, Scientific Computing Facility, MPI-CBG, rhaase@mpi-cbg.de
 * @version 1.0.0 Jan 14, 2016
 *
 */
public class RealBinaryNot extends AbstractRealInterval implements RealRandomAccessibleRealIntervalContains{

	protected RealRandomAccessibleRealIntervalContains operand;
	
	public RealBinaryNot(RealRandomAccessibleRealIntervalContains operand)
	{
		super(operand.numDimensions());
		this.operand = operand;
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
	public boolean contains(RealLocalizable l)
	{
		return !operand.contains(l);
	}

	@Override
	public Contains<RealLocalizable> copyContains() {
		return this;
	}


	@Override
	public double realMin( final int d )
	{
		return operand.realMin(d);
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
		return operand.realMax(d);
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
