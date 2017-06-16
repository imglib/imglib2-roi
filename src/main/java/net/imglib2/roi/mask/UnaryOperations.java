package net.imglib2.roi.mask;

import java.util.Collections;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.mask.Mask.BoundaryType;
import net.imglib2.roi.mask.MaskOperationResult.Operation;

/**
 * Utility class for obtaining {@link Function}s which operate on one
 * {@link Mask}.
 *
 * @author Alison Walter
 */
public final class UnaryOperations
{

	private UnaryOperations()
	{
		// NB: Prevent instantiation of utility class.
	}

	// -- NOT --

	/**
	 * A function which computes the NOT of a given discrete space {@link Mask}.
	 * The result does not have interval bounds.
	 */
	public static UnaryOperator< Mask< Localizable > > not()
	{
		return ( operand ) -> new DefaultMaskOperationResult<>( operand.negate(), notBoundaryType( operand ), Collections.singletonList( operand ), Operation.NOT );
	}

	/**
	 * A function which computes the NOT of a given real space {@link Mask}. The
	 * result does not have interval bounds.
	 */
	public static UnaryOperator< Mask< RealLocalizable > > realNot()
	{
		return ( operand ) -> new DefaultMaskOperationResult<>( operand.negate(), notBoundaryType( operand ), Collections.singletonList( operand ), Operation.NOT );
	}

	// -- Helper methods --

	/**
	 * Computes the boundary behavior of a mask which resulted from a NOT.
	 *
	 * @param operand
	 *            the mask to be NOT-ed
	 * @return boundary behavior of the resulting mask
	 */
	private static < L > BoundaryType notBoundaryType( final Mask< L > operand )
	{
		if ( operand.boundaryType().equals( BoundaryType.CLOSED ) )
			return BoundaryType.OPEN;
		if ( operand.boundaryType().equals( BoundaryType.OPEN ) )
			return BoundaryType.CLOSED;
		return BoundaryType.UNSPECIFIED;
	}
}
