package net.imglib2.troi.util;

import java.util.function.Predicate;
import net.imglib2.AbstractInterval;
import net.imglib2.AbstractRealInterval;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.MaskInterval;
import net.imglib2.troi.RealMaskRealInterval;

public class DefaultRealMaskRealInterval extends AbstractRealInterval implements RealMaskRealInterval
{
	private final BoundaryType boundaryType;

	private final Predicate< ? super RealLocalizable > predicate;

	public DefaultRealMaskRealInterval(
			final RealInterval interval,
			final BoundaryType boundaryType,
			Predicate< ? super RealLocalizable > predicate )
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
	public boolean test( final RealLocalizable localizable )
	{
		return predicate.test( localizable );
	}
}
