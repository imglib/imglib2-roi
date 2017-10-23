package net.imglib2.troi.composite;

import java.util.List;
import java.util.function.Predicate;

import net.imglib2.troi.MaskPredicate;
import net.imglib2.troi.Operators.MaskOperator;

/**
 * A composite {@link MaskPredicate} that knows the operator and operands that
 * are used to create it.
 *
 * @param <T>
 *            location in N-space; typically a {@code RealLocalizable} or
 *            {@code Localizable}).
 *
 * @author Tobias Pietzsch
 */
public interface CompositeMaskPredicate< T > extends MaskPredicate< T >
{
	/** Returns the operation which lead to this mask. */
	MaskOperator operator();

	Predicate< ? super T > operand( final int index );

	/**
	 * Returns the list of operands, which were used to compute this Mask.
	 */
	List< Predicate< ? > > operands();
}
