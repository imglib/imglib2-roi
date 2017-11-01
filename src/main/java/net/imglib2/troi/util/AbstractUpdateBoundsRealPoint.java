package net.imglib2.troi.util;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;

public abstract class AbstractUpdateBoundsRealPoint extends RealPoint implements UpdateBoundsRealPoint
{
	public AbstractUpdateBoundsRealPoint( final int n )
	{
		super( n );
	}

	public AbstractUpdateBoundsRealPoint( final double[] pos )
	{
		super( pos, false );
	}

	public AbstractUpdateBoundsRealPoint( final RealLocalizable pos )
	{
		super( pos );
	}

	@Override
	public void move( final float distance, final int d )
	{
		super.move( distance, d );
		updateBounds();
	}

	@Override
	public void move( final double distance, final int d )
	{
		super.move( distance, d );
		updateBounds();
	}

	@Override
	public void move( final RealLocalizable distance )
	{
		super.move( distance );
		updateBounds();
	}

	@Override
	public void move( final float[] distance )
	{
		super.move( distance );
		updateBounds();
	}

	@Override
	public void move( final double[] distance )
	{
		super.move( distance );
		updateBounds();
	}

	@Override
	public void setPosition( final RealLocalizable position )
	{
		super.setPosition( position );
		updateBounds();
	}

	@Override
	public void setPosition( final float[] position )
	{
		super.setPosition( position );
		updateBounds();
	}

	@Override
	public void setPosition( final double[] position )
	{
		super.setPosition( position );
		updateBounds();
	}

	@Override
	public void setPosition( final float position, final int d )
	{
		super.setPosition( position, d );
		updateBounds();
	}

	@Override
	public void setPosition( final double position, final int d )
	{
		super.setPosition( position, d );
		updateBounds();
	}

	@Override
	public void fwd( final int d )
	{
		super.fwd( d );
		updateBounds();
	}

	@Override
	public void bck( final int d )
	{
		super.bck( d );
		updateBounds();
	}

	@Override
	public void move( final int distance, final int d )
	{
		super.move( distance, d );
		updateBounds();
	}

	@Override
	public void move( final long distance, final int d )
	{
		super.move( distance, d );
		updateBounds();
	}

	@Override
	public void move( final Localizable localizable )
	{
		super.move( localizable );
		updateBounds();
	}

	@Override
	public void move( final int[] distance )
	{
		super.move( distance );
		updateBounds();
	}

	@Override
	public void move( final long[] distance )
	{
		super.move( distance );
		updateBounds();
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		super.setPosition( localizable );
		updateBounds();
	}

	@Override
	public void setPosition( final int[] position )
	{
		super.setPosition( position );
		updateBounds();
	}

	@Override
	public void setPosition( final long[] position )
	{
		super.setPosition( position );
		updateBounds();
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		super.setPosition( position, d );
		updateBounds();
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		super.setPosition( position, d );
		updateBounds();
	}
}
