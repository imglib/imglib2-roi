
package net.imglib2.roi.geometric.twod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.roi.geometric.AbstractUpdatableRealInterval;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

/**
 * A {@link PolygonalChain} is a connected series of line segments.More
 * formally, a polygonal chain P is a curve specified by a sequence of points
 * (A1, A2, ... , An) called its vertices. We assume that every
 * {@link PolygonalChain} is closed, i.e. An = A1.
 *
 * @author Daniel Seebacher, University of Konstanz.
 */
public class PolygonalChain extends AbstractUpdatableRealInterval implements Iterable<RealLocalizable> {

	private static final int NUM_DIMENSIONS = 2;
	private final List<RealLocalizable> vertices;

	/**
	 * Creates an <em>2</em>-dimensional {@link PolygonalChain} with min and max
	 * = 0 <sup>2</sup>.
	 */
	public PolygonalChain() {
		this(new ArrayList<RealLocalizable>());
	}

	/**
	 * Creates an <em>2</em>-dimensional {@link PolygonalChain}.
	 */
	public PolygonalChain(final List<RealLocalizable> points) {
		super(NUM_DIMENSIONS);

		this.vertices = new ArrayList<RealLocalizable>();
		for (final RealLocalizable realLocalizable : points) {
			this.add(realLocalizable);
		}
	}

	@Override
	public Iterator<RealLocalizable> iterator() {
		return this.vertices.iterator();
	}

	@Override
	public RealRandomAccess<BoolType> realRandomAccess() {
		return new PolygonalChainRandomAccess(this, this);
	}

	@Override
	public RealRandomAccess<BoolType> realRandomAccess(final RealInterval interval) {
		return new PolygonalChainRandomAccess(this, interval);
	}

	public List<RealLocalizable> vertices() {
		return this.vertices;
	}

	/**
	 * Add this point
	 *
	 * @param p
	 */
	public void add(final RealLocalizable p) {
		if (p.numDimensions() != numDimensions()) {
			throw new IllegalArgumentException(this.getClass().getSimpleName() + "supports only two dimensions");
		}

		this.vertices().add(p);
		update();
	}

	/**
	 * In our case we can only add points, so check if we have to expand the
	 * borders after a point was added.
	 */
	@Override
	protected void update() {
		for (int d = 0; d < numDimensions(); d++) {
			final double p = this.vertices.get(this.vertices.size() - 1).getDoublePosition(d);
			if (p < this.min[d])
				this.min[d] = p;
			if (p > this.max[d])
				this.max[d] = p;
		}
	}

	private static class PolygonalChainRandomAccess extends RealPoint implements RealRandomAccess<BoolType> {

		private static final double EPSILON = 1e-10;
		private static final BoolType TRUE = new BoolType(true);
		private static final BoolType FALSE = new BoolType(false);

		private final PolygonalChain pc;
		private final RealInterval rl;

		public PolygonalChainRandomAccess(final PolygonalChain pc) {
			this(pc, pc);
		}

		public PolygonalChainRandomAccess(final PolygonalChain pc, final RealInterval rl) {
			super(pc.numDimensions());
			this.pc = pc;
			this.rl = rl;
		}

		@Override
		public BoolType get() {
			final double[] pos = new double[this.numDimensions()];
			this.localize(pos);
			final RealLocalizable point = new RealPoint(pos);

			if (!Intervals.contains(this.rl, point)) {
				return FALSE;
			}

			for (int i = 0; i < this.pc.vertices().size(); i++) {

				final double x0 = this.getDoublePosition(0);
				final double y0 = this.getDoublePosition(1);

				final double x1 = this.pc.vertices().get(i).getDoublePosition(0);
				final double y1 = this.pc.vertices().get(i).getDoublePosition(1);
				final double x2 = this.pc.vertices().get((i + 1) % this.pc.vertices().size()).getDoublePosition(0);
				final double y2 = this.pc.vertices().get((i + 1) % this.pc.vertices().size()).getDoublePosition(1);

				// check if the point is on the line by calculating the distance
				// between the point and the line using the equation from
				// https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line#Line_defined_by_two_points
				final double pointLineDistance = Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1)
						/ Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
				if (pointLineDistance < EPSILON) {
					return TRUE;
				}
			}

			return FALSE;
		}

		@Override
		public PolygonalChainRandomAccess copy() {
			return new PolygonalChainRandomAccess(this.pc);
		}

		@Override
		public PolygonalChainRandomAccess copyRealRandomAccess() {
			return copy();
		}
	}
}
