package net.imglib2.troi;

import java.util.function.Predicate;

/**
 * Defines the edge behavior of the Mask.
 * <ul>
 * <li>CLOSED: contains all points on the boundary</li>
 * <li>OPEN: contains no points on the boundary</li>
 * <li>UNSPECIFIED: boundary behavior is unclear</li>
 * </ul>
 */
public enum BoundaryType
{
	CLOSED, OPEN, UNSPECIFIED;

	public BoundaryType and( final BoundaryType that )
	{
		return this == that ? this : UNSPECIFIED;
	}

	public BoundaryType or( final BoundaryType that )
	{
		return this == that ? this : UNSPECIFIED;
	}

	public BoundaryType negate()
	{
		return this == OPEN ? CLOSED : this == CLOSED ? OPEN : UNSPECIFIED;
	}

	public BoundaryType minus( final BoundaryType that )
	{
		return this != that && that != UNSPECIFIED ? this : UNSPECIFIED;
	}

	public BoundaryType xor( final BoundaryType that )
	{
		return UNSPECIFIED;
	}

	public static BoundaryType of( final Predicate< ? > predicate )
	{
		if ( predicate instanceof MaskPredicate )
			return ( ( MaskPredicate< ? > ) predicate ).boundaryType();
		else
			return UNSPECIFIED;
	}
}
