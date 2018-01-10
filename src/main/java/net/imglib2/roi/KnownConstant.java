package net.imglib2.roi;

import java.util.function.Predicate;

public enum KnownConstant
{
	ALL_TRUE, ALL_FALSE, UNKNOWN;

	KnownConstant and( KnownConstant that )
	{
		if ( this == ALL_TRUE && that == ALL_TRUE )
			return ALL_TRUE;
		if ( this == ALL_FALSE || that == ALL_FALSE )
			return ALL_FALSE;
		return UNKNOWN;
	}

	KnownConstant or( KnownConstant that )
	{
		if ( this == ALL_TRUE || that == ALL_TRUE )
			return ALL_TRUE;
		if ( this == ALL_FALSE && that == ALL_FALSE )
			return ALL_FALSE;
		return UNKNOWN;
	}

	KnownConstant negate()
	{
		if ( this == ALL_FALSE )
			return ALL_TRUE;
		if ( this == ALL_TRUE )
			return ALL_FALSE;
		return UNKNOWN;
	}

	KnownConstant minus( KnownConstant that )
	{
		if ( this == ALL_FALSE || that == ALL_TRUE )
			return ALL_FALSE;
		if ( that == ALL_FALSE )
			return this;
		return UNKNOWN;
	}

	KnownConstant xor( KnownConstant that )
	{
		if ( this == UNKNOWN || that == UNKNOWN )
			return UNKNOWN;
		return this == that ? ALL_FALSE : ALL_TRUE;
	}

	public static KnownConstant of( final Predicate< ? > predicate )
	{
		if ( predicate instanceof MaskPredicate )
			return ( (net.imglib2.roi.MaskPredicate< ? > ) predicate ).knownConstant();
		return UNKNOWN;
	}
}
