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
