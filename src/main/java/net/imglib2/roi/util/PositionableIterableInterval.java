package net.imglib2.roi.util;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;

public class PositionableIterableInterval< T, I extends IterableInterval< T > >
		extends AbstractPositionableInterval
		implements IterableInterval< T >
{
	protected final I source;

	public PositionableIterableInterval( final I source )
	{
		super( source );
		this.source = source;
	}

	protected PositionableIterableInterval( final PositionableIterableInterval< T, I > other )
	{
		super( other );
		this.source = other.source;
	}

	@Override
	public long size()
	{
		return source.size();
	}

	@Override
	public T firstElement()
	{
		return source.firstElement();
	}

	@Override
	public Object iterationOrder()
	{
		return this;
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public Cursor< T > cursor()
	{
		return new PositionableIterableIntervalCursor( source.cursor() );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return new PositionableIterableIntervalCursor( source.localizingCursor() );
	}

	class PositionableIterableIntervalCursor extends OffsetLocalizable< Cursor< T > > implements Cursor< T >
	{
		public PositionableIterableIntervalCursor( final Cursor< T > cursor )
		{
			super( cursor, currentOffset );
		}

		@Override
		public T get()
		{
			return null;
		}

		@Override
		public void jumpFwd( final long steps )
		{
			source.jumpFwd( steps );
		}

		@Override
		public void fwd()
		{
			source.fwd();
		}

		@Override
		public void reset()
		{
			source.reset();
		}

		@Override
		public boolean hasNext()
		{
			return source.hasNext();
		}

		@Override
		public T next()
		{
			return source.next();
		}

		@Override
		public PositionableIterableIntervalCursor copy()
		{
			return new PositionableIterableIntervalCursor( source.copyCursor() );
		}

		@Override
		public PositionableIterableIntervalCursor copyCursor()
		{
			return copy();
		}
	}

	public PositionableIterableInterval< T, I > copy()
	{
		return new PositionableIterableInterval<>( this );
	}
}
