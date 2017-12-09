package net.imglib2.roi;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import org.junit.Before;
import org.junit.Test;

/**
 * Test consistency of unary and binary operations on enums
 * {@link KnownConstant} and {@link BoundaryType}.
 *
 * @param <T>
 *            enum type
 *
 * @author Tobias Pietzsch
 */
public abstract class EnumConsistencyTest< T >
{
	BinaryOperator< T > and;
	BinaryOperator< T > or;
	UnaryOperator< T > negate;
	BinaryOperator< T > minus;
	BinaryOperator< T > xor;
	ArrayList< T > values;

	/**
	 * for all l, r: l ^ r == (l && !r) || (!l && r)
	 */
	@Test
	public void xorDef()
	{
		for ( final T l : values )
			for ( final T r : values )
				assertEquals(
						xor.apply( l, r ),
						or.apply(
								and.apply( l, negate.apply( r ) ),
								and.apply( negate.apply( l ), r )
						)
				);
	}

	/**
	 * for all l, r: !(l && r) == !l || !r
	 * for all l, r: !(l || r) == !l && !r
	 */
	@Test
	public void deMorgan()
	{
		for ( final T l : values )
			for ( final T r : values )
			{
				assertEquals(
						negate.apply( and.apply( l, r ) ),
						or.apply( negate.apply( l ), negate.apply( r ) )
				);
				assertEquals(
						negate.apply( or.apply( l, r ) ),
						and.apply( negate.apply( l ), negate.apply( r ) )
				);
			}
	}

	/**
	 * for all l, r: l - r == (l && !r)
	 */
	@Test
	public void minusDef()
	{
		for ( final T l : values )
			for ( final T r : values )
				assertEquals(
						minus.apply( l, r ),
						and.apply( l, negate.apply( r )	)
				);
	}

	public static class BoundaryTypeTest extends EnumConsistencyTest< BoundaryType >
	{
		@Before
		public void setup()
		{
			and = BoundaryType::and;
			or = BoundaryType::or;
			negate = BoundaryType::negate;
			minus = BoundaryType::minus;
			xor = BoundaryType::xor;
			values = new ArrayList<>( Arrays.asList( BoundaryType.values() ) );
		}
	}

	public static class KnownConstantTest extends EnumConsistencyTest< KnownConstant >
	{
		@Before
		public void setup()
		{
			and = KnownConstant::and;
			or = KnownConstant::or;
			negate = KnownConstant::negate;
			minus = KnownConstant::minus;
			xor = KnownConstant::xor;
			values = new ArrayList<>( Arrays.asList( KnownConstant.values() ) );
		}
	}
}
