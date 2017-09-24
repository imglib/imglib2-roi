package net.imglib2.troi.util;

import java.util.function.Predicate;
import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.troi.Mask;
import net.imglib2.troi.MaskInterval;
import net.imglib2.troi.MaskPredicate;

public abstract class AbstractMaskInterval extends AbstractInterval implements MaskInterval
{
	private final BoundaryType boundaryType;

	public AbstractMaskInterval( final Interval interval, final BoundaryType boundaryType )
	{
		super( interval );
		this.boundaryType = boundaryType;
	}

	public BoundaryType boundaryType()
	{
		return boundaryType;
	}
}
