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

	public BoundaryType and( BoundaryType that )
	{
		return this == that ? this : UNSPECIFIED;
	}

	public BoundaryType or( BoundaryType that )
	{
		return this == that ? this : UNSPECIFIED;
	}

	public BoundaryType negate()
	{
		return this == OPEN ? CLOSED : this == CLOSED ? OPEN : UNSPECIFIED;
	}

	public BoundaryType minus( BoundaryType that )
	{
		// TODO
		throw new UnsupportedOperationException( "TODO, not yet implemented" );
	}

	public BoundaryType xor( BoundaryType that )
	{
		// TODO
		throw new UnsupportedOperationException( "TODO, not yet implemented" );
	}

	public static BoundaryType of( Predicate< ? > predicate )
	{
		if ( predicate instanceof MaskPredicate )
			return ( ( MaskPredicate< ? > ) predicate ).boundaryType();
		else
			return UNSPECIFIED;
	}
}
