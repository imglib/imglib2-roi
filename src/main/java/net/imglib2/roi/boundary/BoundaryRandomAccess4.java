package net.imglib2.roi.boundary;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.AbstractConvertedRandomAccess;
import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BoolType;

/**
 * A {@link BoolType} {@link RandomAccess} on a {@link BooleanType} source
 * {@link RandomAccessibleInterval}. It is {@code true} for pixels that are
 * {@code true} in the source and have at least one {@code false} pixel in their
 * 4-neighborhood (or n-dimensional equivalent).
 *
 * @param <T>
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public final class BoundaryRandomAccess4< T extends BooleanType< T > > extends AbstractConvertedRandomAccess< T, BoolType >
{
	private final int n;

	private final long[] min;

	private final long[] max;

	private final BoolType type;

	public BoundaryRandomAccess4( final RandomAccessibleInterval< T > sourceInterval )
	{
		super( sourceInterval.randomAccess() );
		n = sourceInterval.numDimensions();
		min = new long[ n ];
		max = new long[ n ];
		sourceInterval.min( min );
		sourceInterval.max( max );
		type = new BoolType();
	}

	private BoundaryRandomAccess4( final BoundaryRandomAccess4< T > ba )
	{
		super( ba.source.copyRandomAccess() );
		this.n = ba.n;
		this.min = ba.min;
		this.max = ba.max;
		this.type = ba.type.copy();
	}

	@Override
	public BoolType get()
	{
		if ( source.get().get() )
		{
			for ( int d = 0; d < n; ++d )
			{
				final long pos = getLongPosition( d );
				if ( pos <= min[ d ] || pos >= max[ d ] )
				{
					type.set( true );
					return type;
				}
			}
			for ( int d = 0; d < n; ++d )
			{
				bck( d );
				if ( !source.get().get() )
				{
					fwd( d );
					type.set( true );
					return type;
				}
				fwd( d );
				fwd( d );
				if ( !source.get().get() )
				{
					bck( d );
					type.set( true );
					return type;
				}
				bck( d );
			}
		}
		type.set( false );
		return type;
	}

	@Override
	public BoundaryRandomAccess4< T > copy()
	{
		return new BoundaryRandomAccess4< T >( this );
	}
}
