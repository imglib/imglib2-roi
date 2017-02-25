package net.imglib2.roi;

import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;

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
public interface PositionableIterableInterval< T > extends IterableInterval< T >, Localizable, Positionable
{}
