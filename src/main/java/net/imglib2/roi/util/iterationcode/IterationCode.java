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
package net.imglib2.roi.util.iterationcode;

import gnu.trove.list.array.TIntArrayList;
import net.imglib2.EuclideanSpace;

/**
 * Iteration code encodes a bitmask as a set of intervals along dimension 0.
 * It is a list of numbers (see {@link #getItcode()}) structured as follows:
 *
 * <pre>
 * {@code
 * [o0]             a general X offset
 *                  (to ensure that no negative X coordinates occur.)
 * [p1, ..., pn]    the starting position in dimensions 1, ..., n
 *
 * Then follows a arbitrary long sequence of tuples
 * [p0min, p0max] where p0min >= 0
 *  or
 * [-dim, p1, ..., p(dim)] where -dim < 0.
 * Based on the sign of first element it can be decided which it is.
 * In the second case, the first element determines the length of the tuple.
 *
 * [p0min, p0max] means that the positions from [p0min + o0, p1, ..., pn] to
 * [p0max, p1, ..., pn] are contained in the bitmask, where p1, ..., pn are the
 * current starting position in dimensions 1, ..., n.
 *
 * [dim, p1, ..., p(-dim)] modifies dimensions p1, ..., p(-dim) of the current
 * starting position.
 * }
 * </pre>
 *
 * @author Tobias Pietzsch
 */
public interface IterationCode extends EuclideanSpace
{
	public TIntArrayList getItcode();

	public long getSize();

	public long[] getBoundingBoxMin();

	public long[] getBoundingBoxMax();
}
