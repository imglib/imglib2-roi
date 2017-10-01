package net.imglib2.troi.composite;

import java.util.List;
import java.util.function.Predicate;
import net.imglib2.troi.MaskPredicate;
import net.imglib2.troi.Operators;

public interface CompositeMaskPredicate< T > extends MaskPredicate< T >
{
	/** Returns the operation which lead to this mask. */
	Operators.MaskOperator operator();

	Predicate< ? super T > operand( int index );

	/**
	 * Returns the list of operands, which were used to compute this Mask.
	 */
	List< Predicate< ? > > operands();
}
