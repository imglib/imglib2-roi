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

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.type.logic.BoolType;

public class LabelRegionRandomAccess< T >  extends AbstractEuclideanSpace implements RandomAccess< BoolType >
{
	private final T label;

	private final BoolType type;

	private final RandomAccess< LabelingType< T > > source;

	private final long[] offset;

	private final long[] tmp;

	public LabelRegionRandomAccess( final LabelRegion< T > region, final long[] offset )
	{
		super( region.numDimensions() );
		source = region.regions.labeling.randomAccess( region );
		label = region.getLabel();
		type = new BoolType();
		this.offset = offset;
		tmp = new long[ n ];
	}

	public LabelRegionRandomAccess( final LabelRegion< T > region, final Interval interval, final long[] offset )
	{
		super( region.numDimensions() );
		source = region.regions.labeling.randomAccess( interval );
		label = region.getLabel();
		type = new BoolType();
		this.offset = offset;
		tmp = new long[ n ];
	}

	protected LabelRegionRandomAccess( final LabelRegionRandomAccess< T > a )
	{
		super( a.numDimensions() );
		source = a.source.copyRandomAccess();
		type = a.type.copy();
		label = a.label;
		offset = a.offset;
		tmp = new long[ n ];
	}

	@Override
	public BoolType get()
	{
		type.set( source.get().contains( label ) );
		return type;
	}

	@Override
	public LabelRegionRandomAccess< T > copy()
	{
		return new LabelRegionRandomAccess<>( this );
	}

	@Override
	public LabelRegionRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}

	@Override
	public void localize( final int[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = source.getIntPosition( d ) + ( int ) offset[ d ];
	}

	@Override
	public void localize( final long[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = source.getLongPosition( d ) + ( int ) offset[ d ];
	}

	@Override
	public int getIntPosition( final int d )
	{
		assert d <= n;
		return source.getIntPosition( d ) + ( int ) offset[ d ];
	}

	@Override
	public long getLongPosition( final int d )
	{
		assert d <= n;
		return source.getLongPosition( d ) + ( int ) offset[ d ];
	}

	@Override
	public void localize( final float[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = source.getFloatPosition( d ) + offset[ d ];
	}

	@Override
	public void localize( final double[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = source.getDoublePosition( d ) + offset[ d ];
	}

	@Override
	public float getFloatPosition( final int d )
	{
		assert d <= n;
		return source.getFloatPosition( d ) + offset[ d ];
	}

	@Override
	public double getDoublePosition( final int d )
	{
		assert d <= n;
		return source.getDoublePosition( d ) + offset[ d ];
	}

	@Override
	public void fwd( final int d )
	{
		source.fwd( d );
	}

	@Override
	public void bck( final int d )
	{
		source.bck( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		source.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		source.move( distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		source.move( localizable );
	}

	@Override
	public void move( final int[] distance )
	{
		source.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		source.move( distance );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		assert localizable.numDimensions() == n;
		localizable.localize( tmp );
		for ( int d = 0; d < n; ++d )
			tmp[ d ] -= offset[ d ];
		source.setPosition( tmp );
	}

	@Override
	public void setPosition( final int[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			tmp[ d ] = position[ d ] - offset[ d ];
		source.setPosition( tmp );
	}

	@Override
	public void setPosition( final long[] position )
	{
		assert position.length >= n;
		for ( int d = 0; d < n; ++d )
			tmp[ d ] = position[ d ] - offset[ d ];
		source.setPosition( tmp );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		assert d <= n;
		source.setPosition( position - offset[ d ], d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		assert d <= n;
		source.setPosition( position - offset[ d ], d );
	}
}
