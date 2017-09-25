package net.imglib2.troi.util;

import java.util.function.Predicate;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.Mask;
import net.imglib2.troi.RealMask;

public class DefaultRealMask extends AbstractEuclideanSpace implements RealMask
{
	private final BoundaryType boundaryType;

	private final Predicate< ? super RealLocalizable > predicate;

	/**
	 * @param n
	 * 		number of dimensions.
	 */
	public DefaultRealMask(
			final int n,
			final BoundaryType boundaryType,
			final Predicate< ? super RealLocalizable > predicate )
	{
		super( n );
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
