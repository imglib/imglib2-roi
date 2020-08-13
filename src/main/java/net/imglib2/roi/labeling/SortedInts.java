package net.imglib2.roi.labeling;

import java.util.Arrays;

/**
 * A immutable sorted list of integers, with unique values.
 * <p>
 * This class doesn't implement any list interfaces.
 *
 * @author Matthias Arzt
 */
class SortedInts
{

	private static final SortedInts EMPTY_LIST = wrapSortedValues();

	private final int[] values;

	private final int hashCode;

	private SortedInts( int sortedValues[] )
	{
		this.values = sortedValues;
		this.hashCode = Arrays.hashCode( sortedValues );
	}

	public static SortedInts emptyList()
	{
		return EMPTY_LIST;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( !( obj instanceof SortedInts ) )
			return false;
		SortedInts other = ( SortedInts ) obj;
		if ( hashCode != other.hashCode )
			return false;
		return Arrays.equals( values, other.values );
	}

	public boolean contains( int value )
	{
		return Arrays.binarySearch( values, value ) >= 0;
	}

	/**
	 * @return A new SortedInts object, that contains all the values of this object
	 * plus the specified value. Simply returns this if the specified value is
	 * already in the list.
	 */
	public SortedInts copyAndAdd( int value )
	{
		int index = Arrays.binarySearch( values, value );
		if ( index >= 0 )
			return this;
		int insertionPoint = -1 - index;
		int[] newValues = new int[ values.length + 1 ];
		System.arraycopy( values, 0, newValues, 0, insertionPoint );
		newValues[ insertionPoint ] = value;
		System.arraycopy( values, insertionPoint, newValues, insertionPoint + 1, values.length - insertionPoint );
		return SortedInts.wrapSortedValues( newValues );
	}

	/**
	 * @return A new SortedInts object, that contains all the values of this object
	 * except the specified value. Simply returns {@code this} if the specified value
	 * is not in the list.
	 */
	public SortedInts copyAndRemove( int value )
	{
		int index = Arrays.binarySearch( values, value );
		if ( index < 0 )
			return this;
		int[] newValues = new int[ values.length - 1 ];
		System.arraycopy( values, 0, newValues, 0, index );
		System.arraycopy( values, index + 1, newValues, index, values.length - index - 1 );
		return SortedInts.wrapSortedValues( newValues );
	}

	public static SortedInts create( int... values )
	{
		int[] newValues = Arrays.copyOf( values, values.length );
		Arrays.sort( newValues );
		return new SortedInts( newValues );
	}

	public static SortedInts wrapSortedValues( int... sortedValues )
	{
		return new SortedInts( sortedValues );
	}

	public int[] toArray()
	{
		return Arrays.copyOf( values, values.length );
	}

	@Override
	public String toString()
	{
		return Arrays.toString( values );
	}

	public int size()
	{
		return values.length;
	}

	public boolean isEmpty()
	{
		return values.length == 0;
	}

	public int get( int i )
	{
		return values[ i ];
	}

	@Override
	public int hashCode()
	{
		return hashCode;
	}
}
