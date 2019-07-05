
package net.imglib2.roi.sparse.labkit;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import net.imglib2.AbstractCursor;
import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.roi.IterableRegion;
import net.imglib2.type.logic.BitType;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Matthias Arzt
 */
public class SparseIterableRegion extends AbstractWrappedInterval<Interval>
	implements IterableRegion<BitType>
{

	final private TLongSet codes;

	final private IntervalIndexer2 indexer;

	public SparseIterableRegion(Interval interval) {
		this(interval, new TLongHashSet());
	}

	public SparseIterableRegion(Interval interval, TLongSet positions) {
		super(interval);
		this.codes = positions;
		this.indexer = new IntervalIndexer2(interval);
	}

	public void add(Localizable position) {
		codes.add(indexer.positionToIndex(position));
	}

	public void remove(Localizable position) {
		codes.remove(indexer.positionToIndex(position));
	}

	private boolean contains(Localizable position) {
		return codes.contains(indexer.positionToIndex(position));
	}

	@Override
	public Cursor<Void > cursor() {
		return new SparseRoiCursor();
	}

	@Override
	public Cursor<Void > localizingCursor() {
		return cursor();
	}

	@Override
	public long size() {
		return codes.size();
	}

	@Override
	public Void firstElement() {
		return null;
	}

	@Override
	public Object iterationOrder() {
		return null;
	}

	@Override
	public Iterator<Void> iterator() {
		return cursor();
	}

	@Override
	public RandomAccess<BitType> randomAccess() {
		return new SparseRoiRandomAccess();
	}

	@Override
	public RandomAccess<BitType> randomAccess(Interval interval) {
		return randomAccess();
	}

	private class SparseRoiCursor extends AbstractCursor<Void > implements
		Cursor<Void >
	{

		private final long[] sortedCodes;
		private final int lastIndex;
		private final Point point;
		private int i;

		private SparseRoiCursor() {
			super(SparseIterableRegion.this.numDimensions());
			point = new Point(SparseIterableRegion.this.numDimensions());
			sortedCodes = codes.toArray();
			Arrays.sort(sortedCodes);
			lastIndex = sortedCodes.length - 1;
			reset();
		}

		@Override
		public Void get() {
			return null;
		}

		@Override
		public AbstractCursor<Void > copy() {
			throw new UnsupportedOperationException();
		}

		@Override
		public AbstractCursor<Void > copyCursor() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void fwd() {
			i++;
			indexer.indexToPosition(sortedCodes[i], point);
		}

		@Override
		public void reset() {
			i = -1;
		}

		@Override
		public boolean hasNext() {
			return i < lastIndex;
		}

		@Override
		public void localize(long[] position) {
			point.localize(position);
		}

		@Override
		public long getLongPosition(int d) {
			return point.getLongPosition(d);
		}
	}

	private class SparseRoiRandomAccess extends Point implements
		RandomAccess<BitType>
	{

		private BitType value = new BitType(new LongArray(1)) {

			@Override
			public void set(boolean value) {
				if (value) SparseIterableRegion.this.add(SparseRoiRandomAccess.this);
				else remove(SparseRoiRandomAccess.this);
			}

			@Override
			public boolean get() {
				return contains(SparseRoiRandomAccess.this);
			}
		};

		private SparseRoiRandomAccess() {
			super(SparseIterableRegion.this.numDimensions());
		}

		private SparseRoiRandomAccess(Localizable localizable) {
			super(localizable);
		}

		@Override
		public RandomAccess<BitType> copyRandomAccess() {
			return new SparseRoiRandomAccess(this);
		}

		@Override
		public BitType get() {
			return value;
		}

		@Override
		public Sampler<BitType> copy() {
			throw new UnsupportedOperationException();
		}
	}
}
