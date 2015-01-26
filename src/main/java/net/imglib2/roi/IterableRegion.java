package net.imglib2.roi;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.BooleanType;

/**
 * Iteration of only the true pixels of a region (instead of all pixels in
 * bounding box).
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
public interface IterableRegion< T extends BooleanType< T > > extends IterableInterval< T >, RandomAccessibleInterval< T >
{}
