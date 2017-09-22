package net.imglib2.roi.util;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;

/**
 * Implemented by {@code PositionableIterableInterval} with optimized sampling cursors.
 */
public interface ProvidesSamplingCursor
{
	public < T > Cursor< T > samplingCursor( final RandomAccess< T > target );

	public < T > Cursor< T > samplingLocalizingCursor( final RandomAccess< T > target );
}
