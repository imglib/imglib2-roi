
package net.imglib2.roi.geometric.twod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;
import net.imglib2.roi.IterableRegion;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Intervals;

/**
 * Polygon Rasterizer. Uses the scanline algorithm from
 * http://alienryderflex.com/polygon_fill/.
 *
 * @author Daniel Seebacher, University of Konstanz.
 */
public class RasterizedPolygon implements IterableRegion<BoolType> {

	private final Polygon p;

	private final Map<Integer, List<Integer>> scanline;
	private final long size;

	public RasterizedPolygon(final Polygon p) {
		this.p = p;
		this.scanline = createScanLine(p);
		this.size = calculateSize(this.scanline);
	}

	/**
	 * Calculates the size of the polygon.
	 *
	 * @param scl
	 *            the scanline map
	 * @return the size of the polygon
	 */
	private long calculateSize(final Map<Integer, List<Integer>> scl) {

		long sz = 0;

		for (final Entry<Integer, List<Integer>> singleScanline : scl.entrySet()) {
			final List<Integer> xvalues = singleScanline.getValue();
			for (int i = 0; i < xvalues.size(); i += 2) {
				final int x0 = xvalues.get(i);
				final int x1 = xvalues.get(i + 1);
				sz += Math.abs(x1 - x0);
			}
		}

		return sz;
	}

	/**
	 * Creates the scanline for the given polygon using the scanline algorithm
	 * from http://alienryderflex.com/polygon_fill/.
	 *
	 * @param poly
	 *            a polygon
	 * @return a map with the x values of the scanline for each y coordinate.
	 */
	private Map<Integer, List<Integer>> createScanLine(final Polygon poly) {

		final Map<Integer, List<Integer>> scl = new HashMap<Integer, List<Integer>>();

		for (int y = (int) Math.floor(poly.min(1)); y <= Math.ceil(poly.max(1)); y++) {
			// Build a list of nodes.

			final List<Integer> xValues = new ArrayList<Integer>();
			int j = poly.vertices().size() - 1;
			for (int i = 0; i < poly.vertices().size(); i++) {

				final double polyY_i = poly.vertices().get(i).getDoublePosition(1);
				final double polyX_i = poly.vertices().get(i).getDoublePosition(0);
				final double polyY_j = poly.vertices().get(j).getDoublePosition(1);
				final double polyX_j = poly.vertices().get(j).getDoublePosition(0);

				if (polyY_i < y && polyY_j >= y || polyY_j < y && polyY_i >= y) {
					xValues.add((int) (polyX_i + (y - polyY_i) / (polyY_j - polyY_i) * (polyX_j - polyX_i)));
				}
				j = i;
			}

			// Sort the nodes, via a simple “Bubble” sort.
			Collections.sort(xValues);

			if (!xValues.isEmpty()) {
				scl.put(y, xValues);
			}
		}

		return scl;
	}

	@Override
	public Cursor<BoolType> cursor() {
		return new RasterizedPolygonCursor(this.p, this.scanline);
	}

	@Override
	public Cursor<BoolType> localizingCursor() {
		return cursor();
	}

	@Override
	public long size() {
		return this.size;
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
		return this.p.realMin(d);
	}

	@Override
	public void realMin(final double[] min) {
		this.p.realMin(min);
	}

	@Override
	public void realMin(final RealPositionable min) {
		this.p.realMin(min);
	}

	@Override
	public double realMax(final int d) {
		return this.p.realMax(d);
	}

	@Override
	public void realMax(final double[] max) {
		this.p.realMax(max);
	}

	@Override
	public void realMax(final RealPositionable max) {
		this.p.realMax(max);
	}

	@Override
	public int numDimensions() {
		return this.p.numDimensions();
	}

	@Override
	public Iterator<BoolType> iterator() {
		return cursor();
	}

	@Override
	public long min(final int d) {
		return this.p.min(d);
	}

	@Override
	public void min(final long[] min) {
		this.p.min(min);
	}

	@Override
	public void min(final Positionable min) {
		this.p.min(min);
	}

	@Override
	public long max(final int d) {
		return this.p.max(d);
	}

	@Override
	public void max(final long[] max) {
		this.p.max(max);
	}

	@Override
	public void max(final Positionable max) {
		this.p.max(max);
	}

	@Override
	public void dimensions(final long[] dimensions) {
		this.p.dimensions(dimensions);
	}

	@Override
	public long dimension(final int d) {
		return this.p.dimension(d);
	}

	@Override
	public RandomAccess<BoolType> randomAccess() {
		return new RasterizedPolygonRandomAccess(this.p, this.scanline);
	}

	@Override
	public RandomAccess<BoolType> randomAccess(final Interval interval) {
		return new RasterizedPolygonRandomAccess(interval, this.scanline);
	}

	private static final class RasterizedPolygonCursor extends AbstractCursor<BoolType> {

		private static final BoolType TRUE = new BoolType(true);
		private static final BoolType FALSE = new BoolType(false);

		private final Interval p;
		private final Map<Integer, List<Integer>> scl;

		private final long[] maxAsLongArray;
		private final long[] minAsLongArray;

		private Point currentPosition;
		private BoolType currentValue;

		public RasterizedPolygonCursor(final Interval p, final Map<Integer, List<Integer>> scanline) {
			super(p.numDimensions());

			this.p = p;

			this.minAsLongArray = Intervals.minAsLongArray(p);
			this.maxAsLongArray = Intervals.maxAsLongArray(p);
			this.maxAsLongArray[1]++; // intervals go from y0 to y1 inclusive

			this.scl = scanline;

			this.currentPosition = null;
			this.currentValue = null;
		}

		@Override
		public BoolType get() {
			return this.currentValue;
		}

		@Override
		public void fwd() {

			if (this.currentValue == null) {
				this.currentPosition = new Point(this.minAsLongArray);
			} else {
				this.currentPosition.fwd(0);
				for (int d = 0; d < numDimensions() - 1; d++) {
					if (this.currentPosition.getLongPosition(d) >= this.maxAsLongArray[d]) {
						this.currentPosition.setPosition(this.minAsLongArray[d], d);
						this.currentPosition.move(1, d + 1);
					}
				}
			}

			final List<Integer> list = this.scl.get(this.currentPosition.getIntPosition(1));
			if (list == null) {
				this.currentValue = FALSE;
				return;
			}

			final int currentx = this.currentPosition.getIntPosition(0);
			for (int i = 0; i < list.size(); i += 2) {
				final int x0 = list.get(i);
				final int x1 = list.get(i + 1);

				if (currentx >= x0 && currentx <= x1) {
					this.currentValue = TRUE;
					return;
				}
			}

			this.currentValue = FALSE;
		}

		@Override
		public void reset() {
			this.currentPosition = null;
			this.currentValue = null;
		}

		@Override
		public boolean hasNext() {

			if (this.currentPosition == null) {
				return true;
			}

			final long[] currentPos = new long[numDimensions()];
			localize(currentPos);
			for (int i = 0; i < numDimensions(); i++) {
				currentPos[i] += 1;
			}

			if (!Arrays.equals(currentPos, this.maxAsLongArray)) {
				return true;
			}

			return false;
		}

		@Override
		public void localize(final long[] position) {
			this.currentPosition.localize(position);
		}

		@Override
		public long getLongPosition(final int d) {
			return this.currentPosition.getLongPosition(d);
		}

		@Override
		public AbstractCursor<BoolType> copy() {
			return new RasterizedPolygonCursor(this.p, this.scl);
		}

		@Override
		public AbstractCursor<BoolType> copyCursor() {
			return copy();
		}
	}

	private static final class RasterizedPolygonRandomAccess extends Point implements RandomAccess<BoolType> {

		private static final BoolType TRUE = new BoolType(true);
		private static final BoolType FALSE = new BoolType(false);

		private final Interval interval;
		private final Map<Integer, List<Integer>> scl;

		public RasterizedPolygonRandomAccess(final Interval p, final Map<Integer, List<Integer>> scanline) {
			super(2);

			this.interval = p;
			this.scl = scanline;
		}

		@Override
		public BoolType get() {

			final long[] pos = new long[numDimensions()];
			this.localize(pos);

			final int currenty = this.getIntPosition(1);

			final List<Integer> list = this.scl.get(currenty);
			if (list == null) {
				return FALSE;
			}

			final int currentx = this.getIntPosition(0);
			for (int i = 0; i < list.size(); i += 2) {
				final int x0 = list.get(i);
				final int x1 = list.get(i + 1);

				if (currentx >= x0 && currentx <= x1) {
					return TRUE;
				}
			}

			return FALSE;
		}

		@Override
		public RasterizedPolygonRandomAccess copy() {
			return new RasterizedPolygonRandomAccess(this.interval, this.scl);
		}

		@Override
		public RasterizedPolygonRandomAccess copyRandomAccess() {
			return copy();
		}

	}
}
