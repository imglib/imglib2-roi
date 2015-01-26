package net.imglib2.roi;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.BooleanType;

/**
 * An {@link IterableRegion} that can be moved around.
 *
 * <p>
 * We put interfaces {@link RandomAccessibleInterval
 * RandomAccessibleInterval&lt;BooleanType&gt;}, {@link IterableRegion
 * IterableRegion&lt;BooleanType&gt;}, {@link PositionableIterableRegion
 * PositionableIterableRegion&lt;BooleanType&gt;} into this sequence such that
 * the {@link Regions} methods that "add capabilities" (being iterable,
 * positionable) can have appropriate result types.
 *
 * @param <T>
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface PositionableIterableRegion< T extends BooleanType< T > > extends IterableRegion< T >, Localizable, Positionable
{}
