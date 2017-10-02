package net.imglib2.troi.composite;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import net.imglib2.troi.Operators.UnaryMaskOperator;

/**
 * A {@link CompositeMaskPredicate} with a unary operator and one operand.
 *
 * @param <T>
 *            location in N-space; typically a {@code RealLocalizable} or
 *            {@code Localizable}).
 *
 * @author Tobias Pietzsch
 */
public interface UnaryCompositeMaskPredicate< T > extends CompositeMaskPredicate< T >
{
	@Override
	public UnaryMaskOperator operator();

	public Predicate< ? super T > arg0();

	@Override
	public default Predicate< ? super T > operand( final int index )
	{
		if ( index == 0 )
			return arg0();
		else
			throw new IllegalArgumentException();
	}

	@Override
	public default List< Predicate< ? > > operands()
	{
		return Arrays.asList( arg0() );
	}
}
