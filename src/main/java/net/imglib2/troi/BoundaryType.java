package net.imglib2.troi;

import java.util.function.Predicate;

/**
 * Defines the edge behavior of the Mask.
 * <ul>
 * <li>CLOSED: contains all points on the boundary</li>
 * <li>OPEN: contains no points on the boundary</li>
 * <li>UNSPECIFIED: boundary behavior is unclear</li>
 * </ul>
 *
 * Also provides unary and binary operations on (masks having specific) edge
 * behaviours.
 *
 * @author Tobias Pietzsch
 * @author Alison Walter
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

	public BoundaryType transform()
	{
		return this;
	}

	public static BoundaryType of( final Predicate< ? > predicate )
	{
		if ( predicate instanceof MaskPredicate )
			return ( ( MaskPredicate< ? > ) predicate ).boundaryType();
		return UNSPECIFIED;
	}
}
