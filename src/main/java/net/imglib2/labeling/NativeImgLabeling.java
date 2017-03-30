/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.labeling;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.view.Views;
import net.imglib2.view.iteration.SubIntervalIterable;

/**
 * A labeling backed by a native image that takes a labeling type backed by an
 * int array.
 * 
 * @param <T>
 *            the type of labels assigned to pixels
 * 
 * @author Lee Kamentsky, Christian Dietz, Martin Horn
 */
@Deprecated
public class NativeImgLabeling< T extends Comparable< T >, I extends IntegerType< I > > extends AbstractNativeLabeling< T > implements SubIntervalIterable< LabelingType< T > >
{

	protected final long[] generation;

	protected final Img< I > img;

	public NativeImgLabeling( final Img< I > img )
	{
		super( dimensions( img ), new DefaultROIStrategyFactory< T >(), new LabelingMapping< T >( img.firstElement().createVariable() ) );
		this.img = img;
		this.generation = new long[ 1 ];
	}

	private static long[] dimensions( final Interval i )
	{
		final long[] dims = new long[ i.numDimensions() ];
		i.dimensions( dims );
		return dims;
	}

	/**
	 * Create a labeling backed by a native image with custom strategy and image
	 * factory
	 * 
	 * @param dim
	 *            - dimensions of the labeling
	 * @param strategyFactory
	 *            - the strategy factory that drives iteration and statistics
	 * @param imgFactory
	 *            - the image factory to generate the native image
	 */
	public NativeImgLabeling( final LabelingROIStrategyFactory< T > strategyFactory, final Img< I > img )
	{
		super( dimensions( img ), strategyFactory, new LabelingMapping< T >( img.firstElement().createVariable() ) );
		this.img = img;
		this.generation = new long[ 1 ];
	}

	@Override
	public RandomAccess< LabelingType< T > > randomAccess()
	{
		final RandomAccess< I > rndAccess = img.randomAccess();

		return new LabelingConvertedRandomAccess< I, T >( rndAccess, generation, mapping );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.imglib2.labeling.AbstractNativeLabeling#setLinkedType(net.imglib2
	 * .labeling.LabelingType)
	 */
	@Override
	public Cursor< LabelingType< T > > cursor()
	{
		final Cursor< I > c = img.cursor();
		return new LabelingConvertedCursor< I, T >( c, generation, mapping );
	}

	@Override
	public Cursor< LabelingType< T > > localizingCursor()
	{
		final Cursor< I > c = img.localizingCursor();
		return new LabelingConvertedCursor< I, T >( c, generation, mapping );
	}

	public Img< I > getStorageImg()
	{
		return img;
	}

	@Override
	public Labeling< T > copy()
	{
		final NativeImgLabeling< T, I > result = new NativeImgLabeling< T, I >( img.factory().create( img, img.firstElement().createVariable() ) );
		final Cursor< LabelingType< T > > srcCursor = cursor();
		final Cursor< LabelingType< T > > resCursor = result.cursor();

		while ( srcCursor.hasNext() )
		{
			srcCursor.fwd();
			resCursor.fwd();

			resCursor.get().set( srcCursor.get() );
		}

		return result;

	}

	@Override
	public LabelingType< T > firstElement()
	{
		return cursor().next();
	}

	@Override
	public Iterator< LabelingType< T > > iterator()
	{
		return cursor();
	}

	@Override
	public RandomAccess< LabelingType< T > > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	@Override
	public Object iterationOrder()
	{
		return img.iterationOrder();
	}

	@Override
	public < LL extends Comparable< LL > > LabelingFactory< LL > factory()
	{
		return new LabelingFactory< LL >()
		{
			@Override
			public Labeling< LL > create( final long[] dim )
			{
				return new NativeImgLabeling< LL, I >( img.factory().create( dim, img.firstElement().createVariable() ) );
			}
		};
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean supportsOptimizedCursor( final Interval interval )
	{
		if ( this.img instanceof SubIntervalIterable )
			return ( ( SubIntervalIterable< I > ) this.img ).supportsOptimizedCursor( interval );
		else
			return false;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Object subIntervalIterationOrder( final Interval interval )
	{
		if ( this.img instanceof SubIntervalIterable )
			return ( ( SubIntervalIterable< I > ) this.img ).subIntervalIterationOrder( interval );
		else
			return new FlatIterationOrder( interval );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Cursor< LabelingType< T > > cursor( final Interval interval )
	{
		Cursor< I > c = null;
		if ( this.img instanceof SubIntervalIterable )
			c = ( ( SubIntervalIterable< I > ) this.img ).cursor( interval );
		else
			c = Views.interval( this.img, interval ).cursor();

		return new LabelingConvertedCursor< I, T >( c, generation, mapping );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Cursor< LabelingType< T > > localizingCursor( final Interval interval )
	{
		Cursor< I > c = null;
		if ( this.img instanceof SubIntervalIterable )
			c = ( ( SubIntervalIterable< I > ) this.img ).localizingCursor( interval );
		else
			c = Views.interval( this.img, interval ).localizingCursor();

		return new LabelingConvertedCursor< I, T >( c, generation, mapping );
	}
}
