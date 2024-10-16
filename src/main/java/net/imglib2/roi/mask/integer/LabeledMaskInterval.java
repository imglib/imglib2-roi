/*-
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
package net.imglib2.roi.mask.integer;

import java.util.Objects;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Localizable;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.MaskInterval;

/**
 * A {@link MaskInterval} with an associated label.
 *
 * @author Alison Walter
 * @param <T>
 *            The type of labels assigned to the points
 */
public class LabeledMaskInterval< T > extends AbstractWrappedInterval< MaskInterval > implements MaskInterval
{

	private final T label;

	public LabeledMaskInterval( final MaskInterval source, final T label )
	{
		super( source );
		this.label = label;
	}

	public T getLabel()
	{
		return label;
	}

	@Override
	public boolean test( final Localizable t )
	{
		return getSource().test( t );
	}

	@Override
	public BoundaryType boundaryType()
	{
		return getSource().boundaryType();
	}

	@Override
	public KnownConstant knownConstant()
	{
		return getSource().knownConstant();
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( obj == null )
			return false;
		if ( !( obj instanceof LabeledMaskInterval ) )
			return false;
		final LabeledMaskInterval< ? > other = ( LabeledMaskInterval< ? > ) obj;
		return getSource().equals( other.getSource() ) && label.equals( other.getLabel() );
	}

	@Override
	public int hashCode()
	{
		return Objects.hash( getSource(), label );
	}
}
