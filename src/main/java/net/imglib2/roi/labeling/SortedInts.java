/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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

	private SortedInts( final int[] sortedValues )
	{
		this.values = sortedValues;
		this.hashCode = Arrays.hashCode( sortedValues );
	}

	public static SortedInts emptyList()
	{
		return EMPTY_LIST;
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( this == obj )
			return true;
		if ( !( obj instanceof SortedInts ) )
			return false;
		final SortedInts other = ( SortedInts ) obj;
		if ( hashCode != other.hashCode )
			return false;
		return Arrays.equals( values, other.values );
	}

	public boolean contains( final int value )
	{
		return Arrays.binarySearch( values, value ) >= 0;
	}

	/**
	 * @return A new SortedInts object, that contains all the values of this object
	 * plus the specified value. Simply returns this if the specified value is
	 * already in the list.
	 */
	public SortedInts copyAndAdd( final int value )
	{
		final int index = Arrays.binarySearch( values, value );
		if ( index >= 0 )
			return this;
		final int insertionPoint = -1 - index;
		final int[] newValues = new int[ values.length + 1 ];
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
	public SortedInts copyAndRemove( final int value )
	{
		final int index = Arrays.binarySearch( values, value );
		if ( index < 0 )
			return this;
		final int[] newValues = new int[ values.length - 1 ];
		System.arraycopy( values, 0, newValues, 0, index );
		System.arraycopy( values, index + 1, newValues, index, values.length - index - 1 );
		return SortedInts.wrapSortedValues( newValues );
	}

	public static SortedInts create( final int... values )
	{
		final int[] newValues = Arrays.copyOf( values, values.length );
		Arrays.sort( newValues );
		return new SortedInts( newValues );
	}

	public static SortedInts wrapSortedValues( final int... sortedValues )
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

	public int get( final int i )
	{
		return values[ i ];
	}

	@Override
	public int hashCode()
	{
		return hashCode;
	}
}
