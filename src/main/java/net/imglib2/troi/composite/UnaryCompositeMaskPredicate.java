package net.imglib2.troi.composite;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import net.imglib2.troi.Operators;

public interface UnaryCompositeMaskPredicate< T > extends CompositeMaskPredicate< T >
{
	@Override
	Operators.UnaryMaskOperator operator();

	Predicate< ? super T > arg0();

	@Override
	default Predicate< ? super T > operand( int index )
	{
		if ( index == 0 )
			return arg0();
		else
			throw new IllegalArgumentException();
	}

	default List< Predicate< ? > > operands()
	{
		return Arrays.asList( arg0() );
	}
}
