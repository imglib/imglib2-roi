package net.imglib2.roi.composite;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import net.imglib2.roi.Operators.BinaryMaskOperator;

/**
 * A {@link CompositeMaskPredicate} with a binary operator and two operands.
 *
 * @param <T>
 *            location in N-space; typically a {@code RealLocalizable} or
 *            {@code Localizable}).
 *
 * @author Tobias Pietzsch
 */
public interface BinaryCompositeMaskPredicate< T > extends CompositeMaskPredicate< T >
{
	@Override
	BinaryMaskOperator operator();

	Predicate< ? super T > arg0();

	Predicate< ? super T > arg1();

	@Override
	default Predicate< ? super T > operand( final int index )
	{
		switch ( index )
		{
		case 0:
			return arg0();
		case 1:
			return arg1();
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	default List< Predicate< ? > > operands()
	{
		return Arrays.asList( arg0(), arg1() );
	}
}
