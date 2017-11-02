package net.imglib2.troi.mask.integer;

import java.util.function.Predicate;

import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.MaskInterval;
import net.imglib2.util.Intervals;

/**
 * @author Tobias Pietzsch
 */
public class DefaultMaskInterval extends AbstractInterval implements MaskInterval
{
	private final BoundaryType boundaryType;

	private final Predicate< ? super Localizable > predicate;

	public DefaultMaskInterval(
			final Interval interval,
			final BoundaryType boundaryType,
			final Predicate< ? super Localizable > predicate )
	{
		super( interval );
		this.boundaryType = boundaryType;
		this.predicate = predicate;
	}

	@Override
	public BoundaryType boundaryType()
	{
		return boundaryType;
	}

	@Override
	public boolean test( final Localizable localizable )
	{
		if ( Intervals.contains( this, localizable ) )
			return predicate.test( localizable );
		return false;
	}
}
