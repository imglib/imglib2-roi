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
package net.imglib2.roi;

import static net.imglib2.roi.Operators.AND;
import static net.imglib2.roi.Operators.MINUS;
import static net.imglib2.roi.Operators.NEGATE;
import static net.imglib2.roi.Operators.OR;
import static net.imglib2.roi.Operators.XOR;

import java.util.function.Predicate;

import net.imglib2.RealLocalizable;
import net.imglib2.realtransform.RealTransform;

/**
 * A {@link MaskPredicate} for {@link RealLocalizable}. Results of operations
 * ({@code and, or, negate}, etc) are also {@code RealMask}s.
 *
 * @author Tobias Pietzsch
 */
public interface RealMask extends MaskPredicate< RealLocalizable >
{
	@Override
	default RealMask and( final Predicate< ? super RealLocalizable > other )
	{
		return AND.applyReal( this, other );
	}

	@Override
	default RealMask or( final Predicate< ? super RealLocalizable > other )
	{
		return OR.applyReal( this, other );
	}

	@Override
	default RealMask negate()
	{
		return NEGATE.applyReal( this );
	}

	@Override
	default RealMask minus( final Predicate< ? super RealLocalizable > other )
	{
		return MINUS.applyReal( this, other );
	}

	@Override
	default RealMask xor( final Predicate< ? super RealLocalizable > other )
	{

		return XOR.applyReal( this, other );
	}

	default RealMask transform( final RealTransform transformToSource )
	{
		return new Operators.RealTransformMaskOperator( transformToSource ).applyReal( this );
	}
}
