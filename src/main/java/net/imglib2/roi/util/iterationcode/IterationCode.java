package net.imglib2.roi.util.iterationcode;

import net.imglib2.EuclideanSpace;
import gnu.trove.list.array.TIntArrayList;

/**
 * TODO
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface IterationCode extends EuclideanSpace
{
	public TIntArrayList getItcode();

	public long getSize();

	public long[] getBoundingBoxMin();

	public long[] getBoundingBoxMax();
}
