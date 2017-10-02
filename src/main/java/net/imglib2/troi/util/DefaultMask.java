package net.imglib2.troi.util;

import java.util.function.Predicate;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.Mask;

/**
 * @author Tobias Pietzsch
 */
public class DefaultMask extends AbstractEuclideanSpace implements Mask
{
	private final BoundaryType boundaryType;

	private final Predicate< ? super Localizable > predicate;

	/**
	 * @param n
	 * 		number of dimensions.
	 */
	public DefaultMask(
			final int n,
			final BoundaryType boundaryType,
			final Predicate< ? super Localizable > predicate )
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
	public boolean test( final Localizable localizable )
	{
		return predicate.test( localizable );
	}
}
