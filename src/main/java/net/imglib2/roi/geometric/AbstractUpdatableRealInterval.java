
package net.imglib2.roi.geometric;

import net.imglib2.AbstractRealInterval;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.type.logic.BoolType;

/**
 * Abstract 2D Polytop.
 *
 * @author Daniel Seebacher, University of Konstanz.
 */
public abstract class AbstractUpdatableRealInterval extends AbstractRealInterval
		implements RealRandomAccessibleRealInterval<BoolType>, Interval {

	protected AbstractUpdatableRealInterval(final int numDimensions) {
		super(numDimensions);
	}

	/**
	 * Abstract update method to shrink/expand the interval borders. Must be
	 * implemented by subclasses.
	 */
	protected abstract void update();

	@Override
	public long min(final int d) {
		return (long) this.min[d];
	}

	@Override
	public void min(final long[] minArray) {
		for (int d = 0; d < numDimensions(); d++) {
			minArray[d] = min(d);
		}
	}

	@Override
	public void min(final Positionable minPositionable) {
		for (int d = 0; d < numDimensions(); d++) {
			minPositionable.setPosition(min(d), d);
		}
	}

	@Override
	public long max(final int d) {
		return (long) this.max[d];
	}

	@Override
	public void max(final long[] maxArray) {
		for (int d = 0; d < numDimensions(); d++) {
			maxArray[d] = max(d);
		}
	}

	@Override
	public void max(final Positionable maxPositionable) {
		for (int d = 0; d < numDimensions(); d++) {
			maxPositionable.setPosition(max(d), d);
		}
	}

	@Override
	public void dimensions(final long[] dimensions) {
		for (int d = 0; d < numDimensions(); d++) {
			dimensions[d] = dimension(d);
		}
	}

	@Override
	public long dimension(final int d) {
		return max(d) - min(d);
	}

}
