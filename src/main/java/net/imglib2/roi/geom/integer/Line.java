package net.imglib2.roi.geom.integer;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.Positionable;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;
import net.imglib2.Sampler;

public class Line implements IterableInterval< Void >
{

	private final int n;

	private final long nPoints;

	private final Localizable start;

	private final Localizable end;

	public Line( final Localizable start, final Localizable end )
	{
		this.start = start;
		this.end = end;
		this.n = start.numDimensions();

		final Point diff = new Point( n );
		long maxDiff = -1;
		for ( int d = 0; d < n; d++ )
		{
			final long dx = end.getLongPosition( d ) - start.getLongPosition( d );
			diff.setPosition( dx, d );
			if ( Math.abs( dx ) > maxDiff )
				maxDiff = Math.abs( dx );
		}
		this.nPoints = maxDiff;
	}

	@Override
	public long size()
	{
		return nPoints;
	}

	@Override
	public Void firstElement()
	{
		return cursor().next();
	}

	@Override
	public Object iterationOrder()
	{
		return this;
	}

	@Override
	public double realMin( final int d )
	{
		return min( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < n; d++ )
			min[ d ] = realMin( d );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < n; d++ )
			min.setPosition( realMin( d ), d );
	}

	@Override
	public double realMax( final int d )
	{
		return max( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < n; d++ )
			max[ d ] = realMax( d );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < n; d++ )
			max.setPosition( realMax( d ), d );
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public Iterator< Void > iterator()
	{
		return cursor();
	}

	@Override
	public long min( final int d )
	{
		return Math.min( start.getLongPosition( d ), end.getLongPosition( d ) );
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < n; d++ )
			min[ d ] = min( d );
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < n; d++ )
			min.setPosition( min( d ), d );
	}

	@Override
	public long max( final int d )
	{
		return Math.max( start.getLongPosition( d ), end.getLongPosition( d ) );
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < n; d++ )
			max[ d ] = max( d );
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < n; d++ )
			max.setPosition( max( d ), d );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < n; d++ )
			dimensions[ d ] = max( d ) - min( d );
	}

	@Override
	public long dimension( final int d )
	{
		return max( d ) - min( d );
	}

	@Override
	public Cursor< Void > cursor()
	{
		return cursor();
	}

	@Override
	public Cursor< Void > localizingCursor()
	{
		return new LineCursor();
	}

	private final class LineCursor extends RealPoint implements Cursor< Void >
	{

		private final RealPoint increment;

		private long index;

		public LineCursor()
		{
			super( start.numDimensions() );

			final Point diff = new Point( n );
			long maxDiff = -1;
			for ( int d = 0; d < n; d++ )
			{
				final long dx = end.getLongPosition( d ) - start.getLongPosition( d );
				diff.setPosition( dx, d );
				if ( Math.abs( dx ) > maxDiff )
					maxDiff = Math.abs( dx );
			}

			this.increment = new RealPoint( n );
			for ( int d = 0; d < n; d++ )
				increment.setPosition( diff.getDoublePosition( d ) / maxDiff, d );

			reset();
		}

		public LineCursor( final LineCursor c )
		{
			this();
			index = c.index;
			setPosition( c );
		}

		@Override
		public boolean hasNext()
		{
			return index < nPoints;
		}

		@Override
		public Void get()
		{
			return null;
		}

		@Override
		public void fwd()
		{
			index++;
			for ( int d = 0; d < n; d++ )
				move( increment.getDoublePosition( d ), d );
		}
		

		@Override
		public void jumpFwd( final long steps )
		{
			if (steps < 0 || steps > nPoints)
				throw new IllegalArgumentException( "Cannot jump by " + steps + " points." );
			
			index += steps;
			for ( int d = 0; d < n; d++ )
				move( steps * increment.getDoublePosition( d ), d );
		}

		@Override
		public void reset()
		{
			index = -1;
			setPosition( start );
		}

		@Override
		public Void next()
		{
			fwd();
			return null;
		}

		@Override
		public void localize( final long[] position )
		{
			for ( int d = 0; d < n; d++ )
				position[ d ] = getLongPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return Math.round( getDoublePosition( d ) );
		}


		@Override
		public void localize( final int[] position )
		{
			for ( int d = 0; d < n; d++ )
				position[ d ] = getIntPosition( d );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return ( int ) getLongPosition( d );
		}

		@Override
		public Sampler< Void > copy()
		{
			return new LineCursor( this );
		}

		@Override
		public Cursor< Void > copyCursor()
		{
			return new LineCursor();
		}
	}

}
