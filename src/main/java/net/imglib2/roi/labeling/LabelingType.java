/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2017 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import net.imglib2.labeling.LabelingROIStrategy;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.IntegerType;

/**
 * The {@link LabelingType} represents a labeling of a pixel with zero or more
 * labels of type T. Each label names a distinct object in the image space.
 *
 * @param <T>
 *            the desired type of the pixel labels, for instance {@link Integer}
 *            to number objects or {@link String} for user-assigned label names.
 *
 * @author Lee Kamentsky
 * @author Tobias Pietzsch
 */
public class LabelingType< T > implements Type< LabelingType< T > >, Set< T >
{
	protected static class ModCount
	{
		private int modCount = 0;
	}

	protected final ModCount generation;

	protected final LabelingMapping< T > mapping;

	protected final IntegerType< ? > type;

	/**
	 * Constructor for mirroring state with another labeling
	 *
	 * @param type
	 *            Wrapped type
	 * @param mapping
	 *            Mapping from wrapped type to LabelingList
	 * @param modCount
	 *            Generation of the type
	 */
	protected LabelingType( final IntegerType< ? > type, final LabelingMapping< T > mapping, final ModCount modCount )
	{
		this.type = type;
		this.mapping = mapping;
		this.generation = modCount;
	}

	@Override
	public void set( final LabelingType< T > c )
	{
		if ( c.mapping == mapping )
			type.setInteger( c.type.getInteger() );
		else
			type.setInteger( mapping.intern( c ).index );
		generation.modCount++;
	}

	/**
	 * Note: This creates an <em>"independent"</em> {@link LabelingType}
	 * instance that has its own {@link LabelingMapping}.
	 */
	@Override
	public LabelingType< T > createVariable()
	{
		final IntegerType< ? > newtype = type.createVariable();
		final LabelingMapping< T > newmapping = mapping.newInstance();
		final LabelingType< T > t = new LabelingType< T >( newtype, newmapping, new ModCount() );
		return t;
	}

	/**
	 * Note: The copy shares the mapping of this {@link LabelingType}. The
	 * rationale is that clients will use this to remember the value at a given
	 * position, and {@code this.set( this.copy() )} will be fast if the copy
	 * has the same mapping. If this causes problems, we may change it later.
	 */
	@Override
	public LabelingType< T > copy()
	{
		final IntegerType< ? > newtype = type.copy();
		final LabelingType< T > t = new LabelingType< T >( newtype, mapping, new ModCount() );
		return t;
	}

	@Override
	public String toString()
	{
		return mapping.setAtIndex( type.getInteger() ).set.toString();
	}

	/**
	 * The underlying storage has an associated generation which is incremented
	 * every time the storage is modified. For cacheing, it's often convenient
	 * or necessary to know whether the storage has changed to know when the
	 * cache is invalid. The strategy is to save the generation number at the
	 * time of cacheing and invalidate the cache if the number doesn't match.
	 *
	 * @return the generation of the underlying storage
	 */
	public int getGeneration()
	{
		return generation.modCount;
	}

	public LabelingMapping< T > getMapping()
	{
		return mapping;
	}

	/**
	 * @return {@link IntegerType} holding the current index at the position of
	 *         the LabelingType.
	 *
	 *         NB: The returned {@link IntegerType} should be used read-only.
	 *         Don't write to this type. The value of the {@link IntegerType}
	 *         refers to a key in the {@link LabelingMapping}. Writing to this
	 *         type may invalidate the caching of the
	 *         {@link LabelingROIStrategy}.
	 */
	public IntegerType< ? > getIndex()
	{
		return type;
	}

	@Override
	public boolean add( final T label )
	{
		final int index = type.getInteger();
		final int newindex = mapping.addLabelToSetAtIndex( label, index ).index;
		if ( newindex == index )
			return false;
		type.setInteger( newindex );
		generation.modCount++;
		return true;
	}

	@Override
	public boolean addAll( final Collection< ? extends T > c )
	{
		final int index = type.getInteger();
		int newindex = index;
		for ( final T label : c )
			newindex = mapping.addLabelToSetAtIndex( label, newindex ).index;
		if ( newindex == index )
			return false;
		type.setInteger( newindex );
		generation.modCount++;
		return true;
	}

	@Override
	public void clear()
	{
		final int index = type.getInteger();
		final int newindex = mapping.emptySet().index;
		if ( newindex != index )
		{
			type.setInteger( newindex );
			generation.modCount++;
		}
	}

	@Override
	public boolean contains( final Object label )
	{
		return mapping.setAtIndex( type.getInteger() ).set.contains( label );
	}

	@Override
	public boolean containsAll( final Collection< ? > labels )
	{
		return mapping.setAtIndex( type.getInteger() ).set.containsAll( labels );
	}

	@Override
	public boolean isEmpty()
	{
		return mapping.setAtIndex( type.getInteger() ).set.isEmpty();
	}

	/**
	 * Note: the returned iterator reflects the label set at the time this
	 * method was called. Subsequent changes to the position of the
	 * {@link LabelingType} or the label set are not reflected!
	 */
	@Override
	public Iterator< T > iterator()
	{
		final Iterator< T > iter = mapping.setAtIndex( type.getInteger() ).set.iterator();
		return new Iterator< T >()
		{
			@Override
			public boolean hasNext()
			{
				return iter.hasNext();
			}

			@Override
			public T next()
			{
				return iter.next();
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean remove( final Object label )
	{
		final int index = type.getInteger();
		final int newindex = mapping.removeLabelFromSetAtIndex( ( T ) label, index ).index;
		if ( newindex == index )
			return false;
		type.setInteger( newindex );
		generation.modCount++;
		return true;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean removeAll( final Collection< ? > c )
	{
		final int index = type.getInteger();
		int newindex = index;
		for ( final T label : ( Collection< ? extends T > ) c )
			newindex = mapping.removeLabelFromSetAtIndex( label, newindex ).index;
		if ( newindex == index )
			return false;
		type.setInteger( newindex );
		generation.modCount++;
		return true;
	}

	@Override
	public boolean retainAll( final Collection< ? > c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int size()
	{
		return mapping.setAtIndex( type.getInteger() ).set.size();
	}

	@Override
	public Object[] toArray()
	{
		return mapping.setAtIndex( type.getInteger() ).set.toArray();
	}

	@Override
	public < T1 > T1[] toArray( final T1[] a )
	{
		return mapping.setAtIndex( type.getInteger() ).set.toArray( a );
	}

	@Override
	public int hashCode()
	{
		return mapping.setAtIndex( type.getInteger() ).hashCode;
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( obj instanceof LabelingType )
		{
			@SuppressWarnings( "unchecked" )
			final LabelingType< T > c = ( LabelingType< T > ) obj;
			if ( c.mapping == mapping )
				return c.type.getInteger() == type.getInteger();
		}
		return mapping.setAtIndex( type.getInteger() ).set.equals( obj );
	}

	/**
	 * Creates a new {@link LabelingType} based on the underlying
	 * {@link IntegerType} of the existing {@link LabelingType} with a different
	 * label type L
	 * 
	 * @param newType
	 *            the type of the labels of the created {@link LabelingType}
	 * 
	 * @return new {@link LabelingType} 
	 */
	public < L > LabelingType< L > createVariable( Class< ? extends L > newType )
	{
		return new LabelingType< L >( this.type.createVariable(),
				new LabelingMapping< L >( this.type ), new ModCount() );
	}

	@Override
	public boolean valueEquals( final LabelingType< T > t )
	{
		return equals( t );
	}
}
