
package net.imglib2.roi.geometric;

import net.imglib2.RandomAccess;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

/**
 * A general {@link RandomAccess} for a {@link Polytope}. Checks whether the a
 * {@link RealLocalizable} is inside the {@link Polytope} or not.
 *
 * @author Daniel Seebacher, University of Konstanz.
 */
public final class PolytopRandomAccess extends RealPoint implements RealRandomAccess<BoolType> {

	private static final BoolType TRUE = new BoolType(true);
	private static final BoolType FALSE = new BoolType(false);

	private final RealInterval bounds;
	private final Polytope p;

	public PolytopRandomAccess(final Polytope p, final RealInterval bounds) {
		super(bounds.numDimensions());

		this.p = p;
		this.bounds = bounds;
	}

	public PolytopRandomAccess(final Polytope p) {
		this(p, null);
	}

	@Override
	public BoolType get() {

		final RealPoint currentPosition = new RealPoint(this.position);

		if (this.bounds != null) {
			if (!Intervals.contains(this.bounds, currentPosition)) {
				return FALSE;
			}
		}

		if (currentPosition.numDimensions() == this.numDimensions()) {
			return (this.p.contains(currentPosition) ? TRUE : FALSE);

		}

		return FALSE;
	}

	@Override
	public PolytopRandomAccess copy() {
		return new PolytopRandomAccess(this.p, this.bounds);
	}

	@Override
	public PolytopRandomAccess copyRealRandomAccess() {
		return copy();
	}
}
