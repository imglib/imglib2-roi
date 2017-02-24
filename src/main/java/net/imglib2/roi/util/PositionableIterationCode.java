package net.imglib2.roi.util;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.Sampler;
import net.imglib2.roi.util.iterationcode.IterationCode;
import net.imglib2.roi.util.iterationcode.IterationCodeIterator;

public class PositionableIterationCode extends AbstractPositionableInterval implements IterableInterval< Void >
{
	private final IterationCode source;

	public PositionableIterationCode( final IterationCode source )
	{
		// TODO is a boundingbox is guaranteed to be minimal?
		super( new FinalInterval( source.getBoundingBoxMin(), source.getBoundingBoxMax() ) );
		this.source = source;
	}

	private PositionableIterationCode( long[] currentOffset, long[] currentMin, long[] currentMax, IterationCode source )
	{
		super( currentMin.clone(), currentMax.clone(), currentOffset.clone() );
		this.source = source;
	}

	public PositionableIterationCode copy()
	{
		return new PositionableIterationCode( currentOffset, currentMin, currentMax, source );
	}

	@Override
	public long size()
	{
		// TODO is size correct?
		return source.getSize();
	}

	@Override
	public Void firstElement()
	{
		return null;
	}

	@Override
	public Object iterationOrder()
	{
		return source;
	}

	@Override
	public Iterator< Void > iterator()
	{
		return cursor();
	}

	@Override
	public Cursor< Void > cursor()
	{
		return new PositionableIterableIntervalCursor( source );
	}

	@Override
	public Cursor< Void > localizingCursor()
	{
		return cursor();
	}

	class PositionableIterableIntervalCursor extends Point implements Cursor< Void >
	{
		private final IterationCodeIterator< Point > iterator;

		private long[] pos;

		private PositionableIterableIntervalCursor( final IterationCodeIterator< Point > code, final Localizable pos )
		{
			super( pos );
			this.iterator = new IterationCodeIterator< Point >( code, this );
		}

		public PositionableIterableIntervalCursor( final IterationCode code )
		{
			super( PositionableIterationCode.super.numDimensions() );
			this.iterator = new IterationCodeIterator< Point >( code, currentOffset, this );
		}

		@Override
		public Void get()
		{
			return null;
		}

		@Override
		public Sampler< Void > copy()
		{
			return copyCursor();
		}

		@Override
		public void jumpFwd( long steps )
		{
			iterator.jumpFwd( steps );
		}

		@Override
		public void fwd()
		{
			iterator.fwd();
		}

		@Override
		public void reset()
		{
			iterator.reset();
		}

		@Override
		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		@Override
		public Void next()
		{
			iterator.fwd();
			return null;
		}

		@Override
		public Cursor< Void > copyCursor()
		{
			return new PositionableIterableIntervalCursor( iterator, this );
		}

		@Override
		public void localize( float[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				position[ i ] = pos[ i ] + currentOffset[ i ];
			}
		}

		@Override
		public void localize( double[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				position[ i ] = pos[ i ] + currentOffset[ i ];
			}
		}

		@Override
		public float getFloatPosition( int d )
		{
			return pos[ d ] + currentOffset[ d ];
		}

		@Override
		public double getDoublePosition( int d )
		{
			return pos[ d ] + currentOffset[ d ];
		}

		@Override
		public int numDimensions()
		{
			return pos.length;
		}

		@Override
		public void localize( int[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				position[ i ] = ( int ) ( pos[ i ] + currentOffset[ i ] );
			}
		}

		@Override
		public void localize( long[] position )
		{
			for ( int i = 0; i < position.length; i++ )
			{
				position[ i ] = pos[ i ] + currentOffset[ i ];
			}
		}

		@Override
		public int getIntPosition( int d )
		{
			return ( int ) ( pos[ d ] + currentOffset[ d ] );
		}

		@Override
		public long getLongPosition( int d )
		{
			return pos[ d ] + currentOffset[ d ];
		}
	}
}
