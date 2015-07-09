
package net.imglib2.roi.geometric.twod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.roi.IterableRegion;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

/**
 * Rasterizes a {@link PolygonalChain} using the Bresenham Algorithm.
 *
 * @author Daniel Seebacher, University of Konstanz.
 * @see "https://de.wikipedia.org/wiki/Bresenham-Algorithmus#Kompakte_Variante"
 */
public class RasterizedPolygonalChain implements IterableRegion<BoolType> {

	private final PolygonalChain pc;
	private final List<HashablePoint> outline;

	public RasterizedPolygonalChain(final PolygonalChain pc) {
		this.pc = pc;
		this.outline = getRasterizedOutline(pc.vertices());
	}

	@Override
	public Cursor<BoolType> cursor() {
		return new RasterizedPolygonalChainCursor(this.outline);
	}

	@Override
	public Cursor<BoolType> localizingCursor() {
		return cursor();
	}

	@Override
	public long size() {
		return this.outline.size();
	}

	@Override
	public BoolType firstElement() {
		return cursor().next();
	}

	@Override
	public Object iterationOrder() {
		return this;
	}

	@Override
	public double realMin(final int d) {
		return this.pc.realMin(d);
	}

	@Override
	public void realMin(final double[] min) {
		this.pc.realMin(min);
	}

	@Override
	public void realMin(final RealPositionable min) {
		this.pc.realMin(min);
	}

	@Override
	public double realMax(final int d) {
		return this.pc.realMax(d);
	}

	@Override
	public void realMax(final double[] max) {
		this.pc.realMax(max);
	}

	@Override
	public void realMax(final RealPositionable max) {
		this.pc.realMax(max);
	}

	@Override
	public int numDimensions() {
		return this.pc.numDimensions();
	}

	@Override
	public Iterator<BoolType> iterator() {
		return cursor();
	}

	@Override
	public long min(final int d) {
		return this.pc.min(d);
	}

	@Override
	public void min(final long[] min) {
		this.pc.min(min);
	}

	@Override
	public void min(final Positionable min) {
		this.pc.min(min);
	}

	@Override
	public long max(final int d) {
		return this.pc.max(d);
	}

	@Override
	public void max(final long[] max) {
		this.pc.max(max);
	}

	@Override
	public void max(final Positionable max) {
		this.pc.max(max);
	}

	@Override
	public void dimensions(final long[] dimensions) {
		this.pc.dimensions(dimensions);
	}

	@Override
	public long dimension(final int d) {
		return this.pc.dimension(d);
	}

	@Override
	public RandomAccess<BoolType> randomAccess() {
		return new RasterizedPolygonalChainRandomAcces(new HashSet<HashablePoint>(this.outline), this);
	}

	@Override
	public RandomAccess<BoolType> randomAccess(final Interval interval) {
		return new RasterizedPolygonalChainRandomAcces(new HashSet<HashablePoint>(this.outline), interval);
	}

	private List<HashablePoint> getRasterizedOutline(final List<RealLocalizable> vertices) {

		final ArrayList<HashablePoint> tmp = new ArrayList<HashablePoint>();
		for (int i = 0; i < vertices.size(); i++) {

			long x0 = Math.round(vertices.get(i).getDoublePosition(0));
			long y0 = Math.round(vertices.get(i).getDoublePosition(1));
			final long x1 = Math.round(vertices.get((i + 1) % vertices.size()).getDoublePosition(0));
			final long y1 = Math.round(vertices.get((i + 1) % vertices.size()).getDoublePosition(1));

			final long dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
			final long dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;

			long err = dx + dy, e2; /* error value e_xy */

			while (true) {
				tmp.add(new HashablePoint(x0, y0));
				if (x0 == x1 && y0 == y1)
					break;
				e2 = 2 * err;
				if (e2 > dy) {
					err += dy;
					x0 += sx;
				} /* e_xy+e_x > 0 */
				if (e2 < dx) {
					err += dx;
					y0 += sy;
				} /* e_xy+e_y < 0 */
			}

			// remove last point, because the last point is the identical to the
			// first point of the next edge
			tmp.remove(tmp.size() - 1);
		}

		return tmp;
	}

	private static final class RasterizedPolygonalChainCursor extends AbstractCursor<BoolType> {

		private static final BoolType TRUE = new BoolType(true);
		private final List<HashablePoint> outline;
		private int currentIndex = -1;

		public RasterizedPolygonalChainCursor(final List<HashablePoint> c) {
			super(c.get(0).numDimensions());
			this.outline = c;
		}

		@Override
		public BoolType get() {
			return TRUE;
		}

		@Override
		public void fwd() {
			++this.currentIndex;
		}

		@Override
		public void reset() {
			this.currentIndex = -1;
		}

		@Override
		public boolean hasNext() {
			return this.currentIndex < this.outline.size() - 1;
		}

		@Override
		public void localize(final long[] position) {
			this.outline.get(this.currentIndex).localize(position);
		}

		@Override
		public long getLongPosition(final int d) {
			return this.outline.get(this.currentIndex).getLongPosition(d);
		}

		@Override
		public AbstractCursor<BoolType> copy() {
			return new RasterizedPolygonalChainCursor(this.outline);
		}

		@Override
		public AbstractCursor<BoolType> copyCursor() {
			return copy();
		}

	}

	/**
	 * Simple {@link RandomAccess} which returns true if the
	 * {@link RandomAccess} is positioned on a vertex or on a line between two
	 * vertices. The lines are calculated using the Bresenham Algorithm.
	 *
	 * @author Daniel Seebacher, University of Konstanz.
	 *
	 */
	private static final class RasterizedPolygonalChainRandomAcces extends Point implements RandomAccess<BoolType> {

		private static final BoolType TRUE = new BoolType(true);
		private static final BoolType FALSE = new BoolType(false);

		private final Interval bounds;
		private final Set<HashablePoint> outline;

		public RasterizedPolygonalChainRandomAcces(final Set<HashablePoint> outline, final Interval interval) {
			super(interval.numDimensions());
			this.outline = outline;
			this.bounds = interval;
		}

		@Override
		public BoolType get() {
			final long[] pos = new long[this.numDimensions()];
			this.localize(pos);
			final HashablePoint point = new HashablePoint(pos);

			if (!Intervals.contains(this.bounds, point)) {
				return FALSE;
			}

			return this.outline.contains(point) ? TRUE : FALSE;
		}

		@Override
		public RasterizedPolygonalChainRandomAcces copy() {
			return new RasterizedPolygonalChainRandomAcces(this.outline, this.bounds);
		}

		@Override
		public RasterizedPolygonalChainRandomAcces copyRandomAccess() {
			return copy();
		}
	}

	/**
	 * Simple extension of the {@link Point} class to which uses its coordinates
	 * to generate a hashcode. Used for fast {@link Set#contains(Object)} check
	 * in {@link RasterizedPolygonalChainRandomAcces}.
	 *
	 * @author Daniel Seebacher, University of Konstanz.
	 *
	 */
	private static class HashablePoint extends Point {

		public HashablePoint(final long... position) {
			super(position, true);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(this.position);
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final HashablePoint other = (HashablePoint) obj;
			if (!Arrays.equals(this.position, other.position))
				return false;
			return true;
		}
	}
}
