package net.imglib2.roi.labeling;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.view.Views;

public class ConvertedLabelingExample
{
	public static void main( final String[] args )
	{
		final int w = 2;
		final int h = 2;

		final Random rand = new Random( 112341 );
		final Img< UnsignedIntType > indexImg = ArrayImgs.unsignedInts( w, h );
		for ( final UnsignedIntType t : indexImg )
			t.set( rand.nextInt( 1000 ) );

		final RandomAccessibleInterval< ? extends GenericLabelingType< ?, UnsignedIntType > > labeling = Converters.convert(
				( RandomAccessibleInterval< UnsignedIntType > ) indexImg,
				new Converter< UnsignedIntType, SingleLabelConvertedLabelingType< UnsignedIntType > >()
				{
					@Override
					public void convert( final UnsignedIntType input, final SingleLabelConvertedLabelingType< UnsignedIntType > output )
					{
						output.setValue( input );
					}
				},
				new SingleLabelConvertedLabelingType< UnsignedIntType >()
		);

		final RandomAccessibleInterval< ? extends GenericLabelingType< ?, String > > stringLabeling = Converters.convert(
				( RandomAccessibleInterval< UnsignedIntType > ) indexImg,
				new Converter< UnsignedIntType, SingleLabelConvertedLabelingType< String > >()
				{
					@Override
					public void convert( final UnsignedIntType input, final SingleLabelConvertedLabelingType< String > output )
					{
						output.setValue( "\"" + Integer.toString( input.getInteger() ) + "\"" );
					}
				},
				new SingleLabelConvertedLabelingType< String >()
		);


		System.out.println( "image values" );
		for ( final UnsignedIntType t : indexImg )
			System.out.println( t );
		System.out.println();

		System.out.println( "wrapped as UnsignedIntType labeling" );
		for ( final GenericLabelingType< ?, UnsignedIntType > t : Views.iterable( labeling ) )
			System.out.println( formatSet( t ) );
		System.out.println();

		System.out.println( "wrapped as String labeling" );
		for ( final GenericLabelingType< ?, String > t : Views.iterable( stringLabeling ) )
			System.out.println( formatSet( t ) );
		System.out.println();
	}

	static < T > String formatSet( final Set< T > set )
	{
		final StringBuffer sb = new StringBuffer( "{" );
		final Iterator< T > iter = set.iterator();
		if ( iter.hasNext() )
			sb.append( iter.next() );
		while ( iter.hasNext() )
		{
			sb.append( ", " );
			sb.append( iter.next() );
		}
		sb.append( "}" );
		return sb.toString();
	}

	static class SingleLabelConvertedLabelingType< T > implements GenericLabelingType< SingleLabelConvertedLabelingType< T >, T >
	{
		private T value = null;

		@Override
		public SingleLabelConvertedLabelingType< T > createVariable()
		{
			return new SingleLabelConvertedLabelingType< T >();
		}

		@Override
		public SingleLabelConvertedLabelingType< T > copy()
		{
			final SingleLabelConvertedLabelingType< T > t = new SingleLabelConvertedLabelingType< T >();
			t.setValue( value );
			return t;
		}

		public void setValue( final T value )
		{
			this.value = value;
		}

		@Override
		public boolean contains( final Object obj )
		{
			if ( obj == null )
				return false;

			return obj.equals( value );
		}

		@Override
		public boolean containsAll( final Collection< ? > objs )
		{
			if ( objs == null )
				return false;

			for ( final Object obj : objs )
				if ( !contains( obj ) )
					return false;
			return true;
		}

		@Override
		public boolean isEmpty()
		{
			return value == null;
		}

		@Override
		public Iterator< T > iterator()
		{
			return new Iterator< T >()
			{
				private boolean hasNext = value != null;

				@Override
				public boolean hasNext()
				{
					return hasNext;
				}

				@Override
				public T next()
				{
					hasNext = false;
					return value;
				}

				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}

		@Override
		public int size()
		{
			return isEmpty() ? 0 : 1;
		}

		@Override
		public Object[] toArray()
		{
			return new Object[] { value };
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public < T > T[] toArray( final T[] type )
		{
			return ( T[] ) toArray();
		}


		@Override
		public void set( final SingleLabelConvertedLabelingType< T > c )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add( final T arg0 )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll( final Collection< ? extends T > arg0 )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove( final Object arg0 )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll( final Collection< ? > arg0 )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll( final Collection< ? > arg0 )
		{
			throw new UnsupportedOperationException();
		}
	}
}

