package net.imglib2.roi;

import net.imglib2.IterableInterval;
import net.imglib2.type.BooleanType;

/**
 * Iteration of only the true pixels of a region (instead of all pixels in bounding box).
 *
 * @param <T>
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface IterableRegion< T extends BooleanType< T > > extends IterableInterval< T >
{}
