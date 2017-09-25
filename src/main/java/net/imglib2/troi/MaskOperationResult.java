package net.imglib2.troi;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;

/**
 * A {@link Mask} which has resulted from an {@link Operation} on one or more
 * {@link Mask}s.
 *
 * @author Alison Walter
 *
 * @param <L>
 *            location in N-space; typically a {@link RealLocalizable} or
 *            subtype (e.g., {@link Localizable}).
 * @param <T>
 *            the type of operands used to compose this, typically (but not
 *            necessarily) masks
 */
public interface MaskOperationResult{}
//< L, T > extends Mask< L >
//{
//	/**
//	 * Returns the list of operands, which were used to compute this Mask. One
//	 * of the operands could be a {@code MaskOperationResult}, which only counts
//	 * as one operand.
//	 */
//	List< T > operands();
//
//	/** Returns the operation which lead to this mask. */
//	Operation operation();
//}
