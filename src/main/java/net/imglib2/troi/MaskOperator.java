package net.imglib2.troi;

import java.util.function.BinaryOperator;
import net.imglib2.troi.MaskPredicate.BoundaryType;
import net.imglib2.troi.util.BooleanBinaryOperator;

/**
 * An operation which can be performed on one or more {@link Mask}s.
 */
public interface MaskOperator
{
	public int arity();

	public boolean apply( boolean... args );

	public BoundaryType apply( BoundaryType... args );

//	public interface Unary extends MaskOperator
//	{
//		default int arity() { return 1; };
//
//		default BoundaryType apply( BoundaryType... args )
//		{
//			if ( args.length == 1 )
//				return apply( args[ 0 ] );
//			else
//				throw new IllegalArgumentException();
//		}
//
//		default boolean apply( boolean... args )
//		{
//			if ( args.length == 1 )
//				return apply( args[ 0 ] );
//			else
//				throw new IllegalArgumentException();
//		}
//
//		boolean apply( boolean arg );
//
//		BoundaryType apply( BoundaryType arg );
//	}
	public static class Binary implements MaskOperator
	{
		public int arity() { return 2; };

		public BoundaryType apply( BoundaryType... args )
		{
			if ( args.length == 2 )
				return apply( args[ 0 ], args[ 1 ] );
			else
				throw new IllegalArgumentException();
		}

		public boolean apply( boolean... args )
		{
			if ( args.length == 2 )
				return apply( args[ 0 ], args[ 1 ] );
			else
				throw new IllegalArgumentException();
		}

//		public boolean isAssociative();
//		public boolean isCommutative();

		public boolean apply( boolean left, boolean right )
		{
			return boolOp.applyAsBoolean( left, right );
		}

		public BoundaryType apply( BoundaryType left, BoundaryType right )
		{
			return boundaryOp.apply( left, right );
		}

		protected Binary(
				BooleanBinaryOperator boolOp,
				BinaryOperator< BoundaryType > boundaryOp )
		{
			this.boolOp = boolOp;
			this.boundaryOp = boundaryOp;
		}

		protected final BooleanBinaryOperator boolOp;

		protected final BinaryOperator< BoundaryType > boundaryOp;
	}

	public static final Binary AND = new Binary(
			( left, right ) -> left && right,
			( l, r ) -> l == r ? l : BoundaryType.UNSPECIFIED );

//		@Override
//		public BoundaryType apply( final BoundaryType left, final BoundaryType right )
//		{
//			return right == left ? left : BoundaryType.UNSPECIFIED;
//		}
//	};
//
	public static void main( String[] args )
	{
		System.out.println( AND.apply( new BoundaryType[] {BoundaryType.UNSPECIFIED, BoundaryType.CLOSED }) );
	}

//	AND, NOT, OR, SUBTRACT, TRANSFORM, XOR;

//	/**
//	 * Determines the boundary type of the Mask resulting from the operation.
//	 *
//	 * @param left
//	 *            a Mask which is the left operand
//	 * @param right
//	 *            a Mask which is the right operand
//	 * @param op
//	 *            the operation being performed
//	 * @return the boundary type of the resulting Mask
//	 */
//	private static < L > BoundaryType boundaryType( final MaskP left, final Mask< L > right, final Operation op )
//	{
//		if ( op == Operation.AND || op == Operation.OR )
//			return right.boundaryType() == left.boundaryType() ? left.boundaryType() : BoundaryType.UNSPECIFIED;
//		else if ( op == Operation.SUBTRACT )
//		{
//			BoundaryType b = BoundaryType.UNSPECIFIED;
//			if ( left.boundaryType() != right.boundaryType() && left.boundaryType() != BoundaryType.UNSPECIFIED && right.boundaryType() != BoundaryType.UNSPECIFIED )
//				b = left.boundaryType();
//			return b;
//		}
//		else if ( op == Operation.XOR )
//			return BoundaryType.UNSPECIFIED;
//		else
//			throw new IllegalArgumentException( "No such operation " + op );
//	}
}
