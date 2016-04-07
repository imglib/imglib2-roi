package net.imglib2.roi.boundary;

import java.util.Arrays;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.AbstractConvertedRandomAccess;
import net.imglib2.iterator.IntervalIterator;
import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BoolType;

/**
 * A {@link BoolType} {@link RandomAccess} on a {@link BooleanType} source
 * {@link RandomAccessibleInterval}. It is {@code true} for pixels that are
 * {@code true} in the source and have at least one {@code false} pixel in their
 * 8-neighborhood (or n-dimensional equivalent).
 *
 * @param <T>
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public final class BoundaryRandomAccess8< T extends BooleanType< T > > extends AbstractConvertedRandomAccess< T, BoolType >
{
	private final int n;

	private final long[] min;

	private final long[] max;

	private final long[][] offsets;

	private final long[][] resets;

	private final BoolType type;

	public BoundaryRandomAccess8( final RandomAccessibleInterval< T > sourceInterval )
	{
		super( sourceInterval.randomAccess() );
		n = sourceInterval.numDimensions();
		min = new long[ n ];
		max = new long[ n ];
		sourceInterval.min( min );
		sourceInterval.max( max );

		offsets = new long[ ( int ) Math.pow( 3, n ) - 1 ][];
		resets = new long[ offsets.length ][];
		final long[] omin = new long[ n ];
		Arrays.fill( omin, -1 );
		final long[] omax = new long[ n ];
		Arrays.fill( omax, 1 );
		final IntervalIterator idx = new IntervalIterator( new FinalInterval( omin, omax ) );
		final int center = ( offsets.length - 1 ) / 2;
		final long[] pos = new long[ n ];
		for ( int i = 0; i < offsets.length; ++i )
		{
			offsets[ i ] = new long[ n ];
			resets[ i ] = new long[ n ];
			if ( i == center )
				idx.fwd();
			idx.fwd();
			idx.localize( offsets[ i ] );
			for ( int d = 0; d < n; ++d )
			{
				offsets[ i ][ d ] -= pos[ d ];
				pos[ d ] += offsets[ i ][ d ];
				resets[ i ][ d ] = -pos[ d ];
			}
		}

		type = new BoolType();
	}

	private BoundaryRandomAccess8( final BoundaryRandomAccess8< T > ba )
	{
		super( ba.source.copyRandomAccess() );
		this.n = ba.n;
		this.min = ba.min;
		this.max = ba.max;
		this.offsets = ba.offsets;
		this.resets = ba.resets;
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
			for ( int i = 0; i < offsets.length; ++i )
			{
				source.move( offsets[ i ] );
				if ( !source.get().get() )
				{
					source.move( resets[ i ] );
					type.set( true );
					return type;
				}
			}
			source.move( resets[ resets.length - 1 ] );
		}
		type.set( false );
		return type;
	}

	@Override
	public BoundaryRandomAccess8< T > copy()
	{
		return new BoundaryRandomAccess8< T >( this );
	}
}
