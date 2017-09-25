package net.imglib2.troi;

import java.util.function.Predicate;

import net.imglib2.Interval;
import net.imglib2.RealInterval;
import net.imglib2.troi.util.TODO_Intervals;
import net.imglib2.util.Intervals;

/**
 * Operations on mask bounds. Bounds can be UNBOUNDED, EMPTY, or a
 * (Real)Interval.
 *
 * Specialized for RealInterval and Interval in nested subclasses
 * {@link IntBounds} and {@link RealBounds}.
 *
 * @param <I>
 *            interval type ({@link Interval} or {@link RealInterval})
 * @param <B>
 *            recursive type of this {@code Bounds}
 */
public abstract class Bounds< I, B extends Bounds< I, B > >
{
	public interface BinaryBoundsOperator
	{
		public < I, B extends Bounds< I, B > > B apply( B left, B right );
	}

	public interface UnaryBoundsOperator
	{
		public < I, B extends Bounds< I, B > > B apply( B arg );
	}

	public static final BinaryBoundsOperator and = new BinaryBoundsOperator()
	{
		@Override
		public < I, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.and( right );
		}
	};

	public static final BinaryBoundsOperator or = new BinaryBoundsOperator()
	{
		@Override
		public < I, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.or( right );
		}
	};

	public static final UnaryBoundsOperator negate = new UnaryBoundsOperator()
	{
		@Override
		public < I, B extends Bounds< I, B > > B apply( final B arg )
		{
			return arg.negate();
		}
	};

	public static final BinaryBoundsOperator xor = new BinaryBoundsOperator()
	{
		@Override
		public < I, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.xor( right );
		}
	};

	public static final BinaryBoundsOperator minus = new BinaryBoundsOperator()
	{
		@Override
		public < I, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.minus( right );
		}
	};

	public boolean isUnbounded()
	{
		return interval == null && !empty;
	}

	public boolean isEmpty()
	{
		return empty;
	}

	public I interval()
	{
		return empty ? null : interval;
	}

	protected abstract B bounds( I i );

	protected abstract I intersect( I i1, I i2 );

	protected abstract I union( I i1, I i2 );

	protected abstract B UNBOUNDED();

	protected abstract B EMPTY();

	@SuppressWarnings( "unchecked" )
	public B and( final B that )
	{
		if ( this == EMPTY() || that == EMPTY() )
			return EMPTY();
		if ( this == UNBOUNDED() )
			return that;
		if ( that == UNBOUNDED() )
			return ( B ) this;
		return bounds( intersect( this.interval(), that.interval() ) );
	}

	@SuppressWarnings( "unchecked" )
	public B or( final B that )
	{
		if ( this == UNBOUNDED() || that == UNBOUNDED() )
			return UNBOUNDED();
		if ( this == EMPTY() )
			return that;
		if ( that == EMPTY() )
			return ( B ) this;
		return bounds( union( this.interval(), that.interval() ) );
	}

	public B negate()
	{
		return UNBOUNDED();
	}

	public B xor( final B that )
	{
		return this.or( that );
	}

	public B minus( final B that )
	{
		return ( B ) this;
	}

	private final I interval;

	private final boolean empty;

	protected Bounds( final I interval, final boolean empty )
	{
		this.interval = interval;
		this.empty = empty;
	}

	public static class IntBounds extends Bounds< Interval, IntBounds >
	{
		public static final IntBounds UNBOUNDED = new IntBounds( null, false );

		public static final IntBounds EMPTY = new IntBounds( null, true );

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
			return i == null ? UNBOUNDED : Intervals.isEmpty( i ) ? EMPTY : new IntBounds( i, false );
		}

		@Override
		protected IntBounds bounds( final Interval i )
		{
			return of( i );
		}

		protected IntBounds( final Interval interval, final boolean empty )
		{
			super( interval, empty );
		}

		@Override
		protected Interval intersect( final Interval i1, final Interval i2 )
		{
			return Intervals.intersect( i1, i2 );
		}

		@Override
		protected Interval union( final Interval i1, final Interval i2 )
		{
			return Intervals.union( i1, i2 );
		}

		@Override
		protected IntBounds UNBOUNDED()
		{
			return UNBOUNDED;
		}

		@Override
		protected IntBounds EMPTY()
		{
			return EMPTY;
		}
	}

	public static class RealBounds extends Bounds< RealInterval, RealBounds >
	{
		public static final RealBounds UNBOUNDED = new RealBounds( null, false );

		public static final RealBounds EMPTY = new RealBounds( null, true );

		public static RealBounds of( final Predicate< ? > predicate )
		{
			if ( predicate instanceof RealInterval )
				return RealBounds.of( ( RealInterval ) predicate );
			else
				return RealBounds.UNBOUNDED;
		}

		public static RealBounds of( final RealInterval i )
		{
			return i == null ? UNBOUNDED : TODO_Intervals.isEmpty( i ) ? EMPTY : new RealBounds( i, false );
		}

		@Override
		protected RealBounds bounds( final RealInterval i )
		{
			return of( i );
		}

		protected RealBounds( final RealInterval interval, final boolean empty )
		{
			super( interval, empty );
		}

		@Override
		protected RealInterval intersect( final RealInterval i1, final RealInterval i2 )
		{
			return TODO_Intervals.intersect( i1, i2 );
		}

		@Override
		protected RealInterval union( final RealInterval i1, final RealInterval i2 )
		{
			return TODO_Intervals.union( i1, i2 );
		}

		@Override
		protected RealBounds UNBOUNDED()
		{
			return UNBOUNDED;
		}

		@Override
		protected RealBounds EMPTY()
		{
			return EMPTY;
		}
	}
}
