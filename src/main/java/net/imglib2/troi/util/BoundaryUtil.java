package net.imglib2.troi.util;

import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import net.imglib2.troi.MaskPredicate;
import net.imglib2.troi.MaskPredicate.BoundaryType;

import static net.imglib2.troi.MaskPredicate.BoundaryType.CLOSED;
import static net.imglib2.troi.MaskPredicate.BoundaryType.OPEN;
import static net.imglib2.troi.MaskPredicate.BoundaryType.UNSPECIFIED;

public class BoundaryUtil
{
	private static BoundaryType boundaryType( Predicate< ? > predicate )
	{
		return MaskPredicate.class.isInstance( predicate )
				? ( ( MaskPredicate< ? > ) predicate ).boundaryType()
				: UNSPECIFIED;
	}

	public static BoundaryType and( Predicate< ? > left, Predicate< ? > right )
	{
		return op( left, right, AND );
	}

	public static BoundaryType op( Predicate< ? > left, Predicate< ? > right, BinaryOperator< BoundaryType > op )
	{
		return op.apply( boundaryType( left ), boundaryType( right ) );
	}

	public static BoundaryType op( Predicate< ? > arg, UnaryOperator< BoundaryType > op )
	{
		return op.apply( boundaryType( arg ) );
	}

	public static final BinaryOperator< BoundaryType > AND = ( left, right ) -> left == right ? left : UNSPECIFIED;

	public static final BinaryOperator< BoundaryType > OR = AND;

	public static final UnaryOperator< BoundaryType > NEGATE = ( type ) -> type == OPEN ? CLOSED : type == CLOSED ? OPEN : UNSPECIFIED;
}
