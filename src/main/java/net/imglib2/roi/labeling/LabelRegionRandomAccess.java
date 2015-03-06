/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

import net.imglib2.Interval;
import net.imglib2.converter.AbstractConvertedRandomAccess;
import net.imglib2.type.logic.BoolType;

public class LabelRegionRandomAccess< T > extends AbstractConvertedRandomAccess< LabelingType< T >, BoolType >
{
	private final T label;

	private final BoolType type;

	public LabelRegionRandomAccess( final LabelRegion< T > region )
	{
		super( region.regions.labeling.randomAccess( region ) );
		label = region.getLabel();
		type = new BoolType();
	}

	public LabelRegionRandomAccess( final LabelRegion< T > region, final Interval interval )
	{
		super( region.regions.labeling.randomAccess( interval ) );
		label = region.getLabel();
		type = new BoolType();
	}

	protected LabelRegionRandomAccess( final LabelRegionRandomAccess< T > a )
	{
		super( a.source.copyRandomAccess() );
		type = a.type.copy();
		label = a.label;
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
		return new LabelRegionRandomAccess< T >( this );
	}

	@Override
	public LabelRegionRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}