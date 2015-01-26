package net.imglib2.roi.labeling;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;
import net.imglib2.roi.PositionableIterableRegion;
import net.imglib2.roi.labeling.LabelRegions.FragmentProperties;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Util;

public class IterableFragment extends AbstractLocalizable implements PositionableIterableRegion< BoolType >
{
	// TODO: remove
	private final FragmentProperties frag;

	protected final long[] currentOffset;

	protected final long[] currentMin;

	protected final long[] currentMax;

	public IterableFragment( final FragmentProperties frag )
	{
		super( frag.numDimensions() );
		currentOffset = new long[ n ];
		currentMin = frag.getBoundingBoxMin().clone();
		currentMax = frag.getBoundingBoxMax().clone();
		this.frag = frag;
	}

	public void printOrigin()
	{
		final long[] origin = new long[ n ];
		for ( int d = 0; d < n; ++d )
			origin[ d ] = position[ d ] - currentOffset[ d ];
		System.out.println( Util.printCoordinates( origin ) );
	}

	public void setOrigin( final long[] origin )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = position[ d ] - currentOffset[ d ] - origin[ d ];
			currentOffset[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}
	}

	@Override
	public FragmentCursor cursor()
	{
		return new FragmentCursor( frag, currentOffset );
	}

	@Override
	public FragmentCursor localizingCursor()
	{
		return cursor();
	}

	@Override
	public long size()
	{
		return frag.getSize();
	}

	@Override
	public BoolType firstElement()
	{
		return cursor().next();
	}

	@Override
	public Object iterationOrder()
	{
		return this;
	}

	@Override
	public FragmentCursor iterator()
	{
		return cursor();
	}

	@Override
	public void fwd( final int d )
	{
		++position[ d ];
		++currentOffset[ d ];
		++currentMin[ d ];
		++currentMax[ d ];
	}

	@Override
	public void bck( final int d )
	{
		--position[ d ];
		--currentOffset[ d ];
		--currentMin[ d ];
		--currentMax[ d ];
	}

	@Override
	public void move( final int distance, final int d )
	{
		position[ d ] += distance;
		currentOffset[ d ] += distance;
		currentMin[ d ] += distance;
		currentMax[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		position[ d ] += distance;
		currentOffset[ d ] += distance;
		currentMin[ d ] += distance;
		currentMax[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = localizable.getLongPosition( d );
			position[ d ] += distance;
			currentOffset[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			currentOffset[ d ] += distance[ d ];
			currentMin[ d ] += distance[ d ];
			currentMax[ d ] += distance[ d ];
		}
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			currentOffset[ d ] += distance[ d ];
			currentMin[ d ] += distance[ d ];
			currentMax[ d ] += distance[ d ];
		}
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = localizable.getLongPosition( d ) - position[ d ];
			position[ d ] += distance;
			currentOffset[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}
	}

	@Override
	public void setPosition( final int[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = pos[ d ] - position[ d ];
			position[ d ] += distance;
			currentOffset[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}
	}

	@Override
	public void setPosition( final long[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = pos[ d ] - position[ d ];
			position[ d ] += distance;
			currentOffset[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}
	}

	@Override
	public void setPosition( final int pos, final int d )
	{
		final long distance = pos - position[ d ];
		position[ d ] += distance;
		currentOffset[ d ] += distance;
		currentMin[ d ] += distance;
		currentMax[ d ] += distance;
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		final long distance = pos - position[ d ];
		position[ d ] += distance;
		currentOffset[ d ] += distance;
		currentMin[ d ] += distance;
		currentMax[ d ] += distance;
	}

	@Override
	public double realMin( final int d )
	{
		return currentMin[ d ];
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = currentMin[ d ];
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( currentMin[ d ], d );
	}

	@Override
	public double realMax( final int d )
	{
		return currentMax[ d ];
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = currentMax[ d ];
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( currentMax[ d ], d );
	}

	@Override
	public long min( final int d )
	{
		return currentMin[ d ];
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = currentMin[ d ];
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( currentMin[ d ], d );
	}

	@Override
	public long max( final int d )
	{
		return currentMax[ d ];
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = currentMax[ d ];
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( currentMax[ d ], d );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = dimension( d );
	}

	@Override
	public long dimension( final int d )
	{
		return currentMax[ d ] - currentMin[ d ] + 1;
	}
}