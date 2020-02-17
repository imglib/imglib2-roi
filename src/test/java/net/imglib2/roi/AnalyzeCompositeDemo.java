/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.function.Predicate;

import net.imglib2.roi.Operators.MaskOperator;
import net.imglib2.roi.composite.CompositeMaskPredicate;
import net.imglib2.roi.geom.real.ClosedWritableSphere;
import net.imglib2.roi.geom.real.Sphere;

public class AnalyzeCompositeDemo
{
	public static void main( final String[] args )
	{
		final Sphere s1 = new ClosedWritableSphere( new double[] { 0, 0, 0 }, 3.5 );
		final Sphere s2 = new ClosedWritableSphere( new double[] { 1, 2, 0 }, 1.5 );
		final Sphere s3 = new ClosedWritableSphere( new double[] { 2, 2, 0 }, 1.5 );
		final RealMaskRealInterval composite = s1.and( s2.minus( s3 ) ).and( s3 ).or( s1.minus( s3.negate() ) );

		printComposite( s1, "", "" );
		printComposite( composite, "", "" );
	}

	public static void printComposite( final Predicate< ? > p, final String indent, final String indentChild )
	{
		if ( p instanceof CompositeMaskPredicate )
			printComposite( ( CompositeMaskPredicate< ? > ) p, indent, indentChild );
		else
			System.out.println( indent + "leaf  (" + p + ")" );
	}

	public static void printComposite( final CompositeMaskPredicate< ? > p, final String indent, final String indentChild )
	{
		System.out.println( indent + operatorName( p.operator() ) + "  (" + p + ")" );
		int i = p.operands().size();
		for ( final Predicate< ? > q : p.operands() )
			printComposite( q, indentChild + " +--", indentChild + ( --i == 0 ? "    " : " |  " ) );
	}

	public static String operatorName( final MaskOperator operator )
	{
		if ( operator == Operators.AND )
			return "AND";
		else if ( operator == Operators.OR )
			return "OR";
		else if ( operator == Operators.XOR )
			return "XOR";
		else if ( operator == Operators.MINUS )
			return "MINUS";
		else if ( operator == Operators.NEGATE )
			return "NEGATE";
		else
			return operator.toString();
	}
}
