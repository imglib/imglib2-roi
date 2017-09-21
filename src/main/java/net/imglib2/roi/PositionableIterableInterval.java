package net.imglib2.roi;

import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.type.logic.BitType;

/**
 * An {@link IterableInterval} that can be moved around.
 *
 * <p>
 * {@link PositionableIterableInterval} is mainly intended as a return type. It
 * is discouraged to take {@link PositionableIterableInterval} as a method
 * parameter. {@code IterableInterval< T > & Localizable & Positionable} should
 * be preferred where possible.
 *
 * @param <T>
 *
 * @author Tobias Pietzsch
 */
public interface PositionableIterableInterval< T, I extends PositionableIterableInterval< T, I > >
		extends IterableInterval< T >, Localizable, Positionable
{
	/**
	 * Get the {@link Positionable}, {@link Localizable} origin of this interval.
	 *
	 * <p>
	 * The origin is basically a negative offset to the position. For example if
	 * a positionable mask is made from a {@link BitType} image with a circular
	 * pattern, then it is more natural if the mask position refers to the
	 * center of the pattern instead of the upper left corner of the
	 * {@link BitType} image. This can be achieved by positioning the origin.
	 *
	 * @return the origin to which the interval is relative.
	 */
	public Origin origin();

	/**
	 * Make a copy of this {@link PositionableIterableInterval} which can be positioned independently.
	 *
	 * @return a copy with an independent position
	 */
	public I copy();
}
