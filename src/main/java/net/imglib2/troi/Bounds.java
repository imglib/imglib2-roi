package net.imglib2.troi;

import java.util.function.Predicate;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.AbstractWrappedInterval;
import net.imglib2.AbstractWrappedRealInterval;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RealInterval;
import net.imglib2.RealPositionable;
import net.imglib2.troi.util.TODO_Intervals;
import net.imglib2.util.Intervals;

/**
 * Operations on mask bounds. Bounds can be UNBOUNDED, or a (possibly empty)
 * (Real)Interval. Bounds can be composites of other Bounds and change if the
 * underlying Bounds are modified. A Bounds can never go from UNBOUNDED to being
 * an interval or vice versa, though.
 * <p>
 * Specialized for RealInterval and Interval in nested subclasses
 * {@link IntBounds} and {@link RealBounds}.
 *
 * @param <I>
 *            interval type ({@code Interval} or {@code RealInterval})
 * @param <B>
 *            recursive type of this {@code Bounds}
 *
 * @author Tobias Pietzsch
 */
public abstract class Bounds< I extends Bounds.Empty, B extends Bounds< I, B > >
{
	public interface BinaryBoundsOperator
	{
		public < I extends Bounds.Empty, B extends Bounds< I, B > > B apply( B left, B right );
	}

	public interface UnaryBoundsOperator
	{
		public < I extends Bounds.Empty, B extends Bounds< I, B > > B apply( B arg );
	}

	public static final BinaryBoundsOperator and = new BinaryBoundsOperator()
	{
		@Override
		public < I extends Bounds.Empty, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.and( right );
		}
	};

	public static final BinaryBoundsOperator or = new BinaryBoundsOperator()
	{
		@Override
		public < I extends Bounds.Empty, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.or( right );
		}
	};

	public static final UnaryBoundsOperator negate = new UnaryBoundsOperator()
	{
		@Override
		public < I extends Bounds.Empty, B extends Bounds< I, B > > B apply( final B arg )
		{
			return arg.negate();
		}
	};

	public static final BinaryBoundsOperator xor = new BinaryBoundsOperator()
	{
		@Override
		public < I extends Bounds.Empty, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.xor( right );
		}
	};

	public static final BinaryBoundsOperator minus = new BinaryBoundsOperator()
	{
		@Override
		public < I extends Bounds.Empty, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.minus( right );
		}
	};

	private final I interval;

	protected Bounds( final I interval )
	{
		this.interval = interval;
	}

	public boolean isUnbounded()
	{
		return interval == null;
	}

	public boolean isEmpty()
	{
		return interval != null && interval.isEmpty();
	}

	public I interval()
	{
		return interval;
	}

	/**
	 * Intersection of two <b>bounded</b> {@link Bounds}.
	 *
	 * @param arg0
	 * 		must not be {@link #isUnbounded() unbounded}
	 * @param arg1
	 * 		must not be {@link #isUnbounded() unbounded}
	 *
	 * @return intersection (also bounded)
	 */
	protected abstract B intersect( B arg0, B arg1 );

	/**
	 * Union of two <b>bounded</b> {@link Bounds}.
	 *
	 * @param arg0
	 * 		must not be {@link #isUnbounded() unbounded}
	 * @param arg1
	 * 		must not be {@link #isUnbounded() unbounded}
	 *
	 * @return intersection (also bounded)
	 */
	protected abstract B union( B arg0, B arg1 );

	protected abstract B UNBOUNDED();

	@SuppressWarnings( "unchecked" )
	public B and( final B that )
	{
		if ( this.isUnbounded() )
			return that;
		if ( that.isUnbounded() )
			return ( B ) this;
		return intersect( ( B ) this, that );
	}

	@SuppressWarnings( "unchecked" )
	public B or( final B that )
	{
		if ( this.isUnbounded() || that.isUnbounded() )
			return UNBOUNDED();
		return union( ( B ) this, that );
	}

	public B negate()
	{
		return UNBOUNDED();
	}

	public B xor( final B that )
	{
		return this.or( that );
	}

	@SuppressWarnings( "unchecked" )
	public B minus( final B that )
	{
		return ( B ) this;
	}

	public interface Empty
	{
		boolean isEmpty();
	}

	public interface IntervalOrEmpty extends Interval, Empty {}

	public static class WrappedIntervalOrEmpty extends AbstractWrappedInterval< Interval > implements IntervalOrEmpty
	{
		public WrappedIntervalOrEmpty( final Interval source )
		{
			super( source );
		}

		@Override
		public boolean isEmpty()
		{
			return Intervals.isEmpty( this );
		}
	}

	public static abstract class AbstractIntervalOrEmpty extends AbstractEuclideanSpace implements IntervalOrEmpty
	{
		public AbstractIntervalOrEmpty( final int n )
		{
			super( n );
		}

		@Override
		public boolean isEmpty()
		{
			for ( int d = 0; d < n; ++d )
				if ( min( d ) > max( d ) )
					return true;
			return false;
		}

		@Override
		public double realMin( final int d )
		{
			return min( d );
		}

		@Override
		public void realMin( final double[] realMin )
		{
			for ( int d = 0; d < n; ++d )
				realMin[ d ] = realMin( d );
		}

		@Override
		public void realMin( final RealPositionable realMin )
		{
			for ( int d = 0; d < n; ++d )
				realMin.setPosition( realMin( d ), d );
		}

		@Override
		public double realMax( final int d )
		{
			return max( d );
		}

		@Override
		public void realMax( final double[] realMax )
		{
			for ( int d = 0; d < n; ++d )
				realMax[ d ] = realMax( d );
		}

		@Override
		public void realMax( final RealPositionable realMax )
		{
			for ( int d = 0; d < n; ++d )
				realMax.setPosition( realMax( d ), d );
		}

		@Override
		public void min( final long[] min )
		{
			for ( int d = 0; d < n; ++d )
				min[ d ] = min( d );
		}

		@Override
		public void min( final Positionable min )
		{
			for ( int d = 0; d < n; ++d )
				min.setPosition( min( d ), d );
		}

		@Override
		public void max( final long[] max )
		{
			for ( int d = 0; d < n; ++d )
				max[ d ] = max( d );
		}

		@Override
		public void max( final Positionable max )
		{
			for ( int d = 0; d < n; ++d )
				max.setPosition( max( d ), d );
		}

		@Override
		public void dimensions( final long[] dimensions )
		{
			for ( int d = 0; d < n; ++d )
				dimensions[ d ] = dimension( d );
		}

		@Override
		public long dimension( final int d )
		{
			return max( d ) - min( d ) + 1;
		}
	}

	public static class IntersectionIntervalOrEmpty extends AbstractIntervalOrEmpty
	{
		private final IntervalOrEmpty i1;

		private final IntervalOrEmpty i2;

		public IntersectionIntervalOrEmpty( final IntervalOrEmpty i1, final IntervalOrEmpty i2 )
		{
			super( i1.numDimensions() );
			this.i1 = i1;
			this.i2 = i2;
			assert ( i1.numDimensions() == i2.numDimensions() );
		}

		@Override
		public long min( final int d )
		{
			if ( i1.isEmpty() || i2.isEmpty() )
				return Long.MAX_VALUE;
			return Math.max( i1.min( d ), i2.min( d ) );
		}

		@Override
		public long max( final int d )
		{
			if ( i1.isEmpty() || i2.isEmpty() )
				return Long.MIN_VALUE;
			return Math.min( i1.max( d ), i2.max( d ) );
		}
	}

	public static class UnionIntervalOrEmpty extends AbstractIntervalOrEmpty
	{
		private final IntervalOrEmpty i1;

		private final IntervalOrEmpty i2;

		public UnionIntervalOrEmpty( final IntervalOrEmpty i1, final IntervalOrEmpty i2 )
		{
			super( i1.numDimensions() );
			this.i1 = i1;
			this.i2 = i2;
			assert ( i1.numDimensions() == i2.numDimensions() );
		}

		@Override
		public long min( final int d )
		{
			if ( i1.isEmpty() )
			{
				if ( i2.isEmpty() )
					return Long.MAX_VALUE;
				else
					return i2.min( d );
			}
			else
			{
				if ( i2.isEmpty() )
					return i1.min( d );
				else
					return Math.min( i1.min( d ), i2.min( d ) );
			}
		}

		@Override
		public long max( final int d )
		{
			if ( i1.isEmpty() )
			{
				if ( i2.isEmpty() )
					return Long.MIN_VALUE;
				else
					return i2.max( d );
			}
			else
			{
				if ( i2.isEmpty() )
					return i1.max( d );
				else
					return Math.max( i1.max( d ), i2.max( d ) );
			}
		}
	}

	public static class IntBounds extends Bounds<IntervalOrEmpty,IntBounds>
	{
		public static final IntBounds UNBOUNDED = new IntBounds( null );

		public static IntBounds of( final Predicate< ? > predicate )
		{
			if ( predicate instanceof Interval )
				return IntBounds.of( ( Interval ) predicate );
			else if ( predicate instanceof RealInterval )
				return IntBounds.of( Intervals.smallestContainingInterval( ( RealInterval ) predicate ) );
			else
				return IntBounds.UNBOUNDED;
		}

		public static IntBounds of( final Interval i )
		{
			if ( i == null )
				return UNBOUNDED;
			else if ( i instanceof IntervalOrEmpty )
				return new IntBounds( ( IntervalOrEmpty ) i );
			else
				return new IntBounds( new WrappedIntervalOrEmpty( i ) );
		}

		protected IntBounds( final IntervalOrEmpty interval )
		{
			super( interval );
		}

		@Override
		protected IntBounds intersect( final IntBounds arg0, final IntBounds arg1 )
		{
			return new IntBounds( new IntersectionIntervalOrEmpty( arg0.interval(), arg1.interval() ) );
		}

		@Override
		protected IntBounds union( final IntBounds arg0, final IntBounds arg1 )
		{
			return new IntBounds( new UnionIntervalOrEmpty( arg0.interval(), arg1.interval() ) );
		}

		@Override
		protected IntBounds UNBOUNDED()
		{
			return UNBOUNDED;
		}
	}

	public interface RealIntervalOrEmpty extends RealInterval, Empty {}

	public static class WrappedRealIntervalOrEmpty extends AbstractWrappedRealInterval< RealInterval > implements RealIntervalOrEmpty
	{
		public WrappedRealIntervalOrEmpty( final RealInterval source )
		{
			super( source );
		}

		@Override
		public boolean isEmpty()
		{
			return TODO_Intervals.isEmpty( this );
		}
	}

	public static abstract class AbstractRealIntervalOrEmpty extends AbstractEuclideanSpace implements RealIntervalOrEmpty
	{
		public AbstractRealIntervalOrEmpty( final int n )
		{
			super( n );
		}

		@Override
		public boolean isEmpty()
		{
			for ( int d = 0; d < n; ++d )
				if ( realMin( d ) > realMax( d ) )
					return true;
			return false;
		}

		@Override
		public void realMin( final double[] realMin )
		{
			for ( int d = 0; d < n; ++d )
				realMin[ d ] = realMin( d );
		}

		@Override
		public void realMin( final RealPositionable realMin )
		{
			for ( int d = 0; d < n; ++d )
				realMin.setPosition( realMin( d ), d );
		}

		@Override
		public void realMax( final double[] realMax )
		{
			for ( int d = 0; d < n; ++d )
				realMax[ d ] = realMax( d );
		}

		@Override
		public void realMax( final RealPositionable realMax )
		{
			for ( int d = 0; d < n; ++d )
				realMax.setPosition( realMax( d ), d );
		}
	}

	public static class IntersectionRealIntervalOrEmpty extends AbstractRealIntervalOrEmpty
	{
		private final RealIntervalOrEmpty i1;

		private final RealIntervalOrEmpty i2;

		public IntersectionRealIntervalOrEmpty( final RealIntervalOrEmpty i1, final RealIntervalOrEmpty i2 )
		{
			super( i1.numDimensions() );
			this.i1 = i1;
			this.i2 = i2;
			assert ( i1.numDimensions() == i2.numDimensions() );
		}

		@Override
		public double realMin( final int d )
		{
			if ( i1.isEmpty() || i2.isEmpty() )
				return Double.POSITIVE_INFINITY;
			return Math.max( i1.realMin( d ), i2.realMin( d ) );
		}

		@Override
		public double realMax( final int d )
		{
			if ( i1.isEmpty() || i2.isEmpty() )
				return Double.NEGATIVE_INFINITY;
			return Math.min( i1.realMax( d ), i2.realMax( d ) );
		}
	}

	public static class UnionRealIntervalOrEmpty extends AbstractRealIntervalOrEmpty
	{
		private final RealIntervalOrEmpty i1;

		private final RealIntervalOrEmpty i2;

		public UnionRealIntervalOrEmpty( final RealIntervalOrEmpty i1, final RealIntervalOrEmpty i2 )
		{
			super( i1.numDimensions() );
			this.i1 = i1;
			this.i2 = i2;
			assert ( i1.numDimensions() == i2.numDimensions() );
		}

		@Override
		public double realMin( final int d )
		{
			if ( i1.isEmpty() )
			{
				if ( i2.isEmpty() )
					return Double.POSITIVE_INFINITY;
				else
					return i2.realMin( d );
			}
			else
			{
				if ( i2.isEmpty() )
					return i1.realMin( d );
				else
					return Math.min( i1.realMin( d ), i2.realMin( d ) );
			}
		}

		@Override
		public double realMax( final int d )
		{
			if ( i1.isEmpty() )
			{
				if ( i2.isEmpty() )
					return Double.NEGATIVE_INFINITY;
				else
					return i2.realMax( d );
			}
			else
			{
				if ( i2.isEmpty() )
					return i1.realMax( d );
				else
					return Math.max( i1.realMax( d ), i2.realMax( d ) );
			}
		}
	}

	public static class RealBounds extends Bounds<RealIntervalOrEmpty,RealBounds>
	{
		public static final RealBounds UNBOUNDED = new RealBounds( null );

		public static RealBounds of( final Predicate< ? > predicate )
		{
			if ( predicate instanceof RealInterval )
				return RealBounds.of( ( RealInterval ) predicate );
			else
				return RealBounds.UNBOUNDED;
		}

		public static RealBounds of( final RealInterval i )
		{
			if ( i == null )
				return UNBOUNDED;
			else if ( i instanceof RealIntervalOrEmpty )
				return new RealBounds( ( RealIntervalOrEmpty ) i );
			else
				return new RealBounds( new WrappedRealIntervalOrEmpty( i ) );
		}

		protected RealBounds( final RealIntervalOrEmpty interval )
		{
			super( interval );
		}

		@Override
		protected RealBounds intersect( final RealBounds arg0, final RealBounds arg1 )
		{
			return new RealBounds( new IntersectionRealIntervalOrEmpty( arg0.interval(), arg1.interval() ) );
		}

		@Override
		protected RealBounds union( final RealBounds arg0, final RealBounds arg1 )
		{
			return new RealBounds( new UnionRealIntervalOrEmpty( arg0.interval(), arg1.interval() ) );
		}

		@Override
		protected RealBounds UNBOUNDED()
		{
			return UNBOUNDED;
		}
	}
}
