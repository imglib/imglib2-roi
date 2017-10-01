package net.imglib2.troi.composite;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import net.imglib2.troi.Masks;

public interface BinaryCompositeMaskPredicate< T > extends CompositeMaskPredicate< T >
{
	@Override
	Masks.BinaryMaskOperator operator();

	Predicate< ? super T > arg0();

	Predicate< ? super T > arg1();

	@Override
	default Predicate< ? super T > operand( int index )
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

	default List< Predicate< ? > > operands()
	{
		return Arrays.asList( arg0(), arg1() );
	}
}
