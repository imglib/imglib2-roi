package net.imglib2.troi.util;

import java.util.function.Predicate;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.troi.Mask;
import net.imglib2.troi.MaskInterval;

public abstract class AbstractMask extends AbstractEuclideanSpace implements Mask
{
	private final BoundaryType boundaryType;

	/**
	 * @param n
	 * 		number of dimensions.
	 */
	public AbstractMask( final int n, final BoundaryType boundaryType )
	{
		super( n );
		this.boundaryType = boundaryType;
	}

	public BoundaryType boundaryType()
	{
		return boundaryType;
	}
}
