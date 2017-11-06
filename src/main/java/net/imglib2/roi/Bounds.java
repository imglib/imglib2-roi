/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2017 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.roi;

import java.util.function.Predicate;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RealInterval;
import net.imglib2.RealPositionable;
import net.imglib2.realtransform.InvertibleRealTransform;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.transform.Transform;
import net.imglib2.util.Intervals;

/**
 * Operations on mask bounds. Bounds can be UNBOUNDED, or a (possibly empty)
 * (Real)Interval. Bounds can be composites of other Bounds and change if the
 * underlying Bounds are modified. A Bounds can never go from UNBOUNDED to being
 * an interval or vice versa, though.
 * <p>
 * Specialized for RealInterval and Interval in nested subclasses
 * {@link IntBounds} and {@link RealBounds}.
 * </p>
 * <p>
 * TODO: explain how empty intervals are determined by min/max and how emptiness
 * property is propagated.
 * </p>
 *
 *
 * @param <I>
 *            interval type ({@code Interval} or {@code RealInterval})
 * @param <B>
 *            recursive type of this {@code Bounds}
 *
 * @author Tobias Pietzsch
 */
public abstract class Bounds< I extends RealInterval, B extends Bounds< I, B > >
{
	public interface BinaryBoundsOperator
	{
		public < I extends RealInterval, B extends Bounds< I, B > > B apply( B left, B right );
	}

	public interface UnaryBoundsOperator
	{
		public < I extends RealInterval, B extends Bounds< I, B > > B apply( B arg );
	}

	public static final BinaryBoundsOperator and = new BinaryBoundsOperator()
	{
		@Override
		public < I extends RealInterval, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.and( right );
		}
	};

	public static final BinaryBoundsOperator or = new BinaryBoundsOperator()
	{
		@Override
		public < I extends RealInterval, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.or( right );
		}
	};

	public static final UnaryBoundsOperator negate = new UnaryBoundsOperator()
	{
		@Override
		public < I extends RealInterval, B extends Bounds< I, B > > B apply( final B arg )
		{
			return arg.negate();
		}
	};

	public static final BinaryBoundsOperator xor = new BinaryBoundsOperator()
	{
		@Override
		public < I extends RealInterval, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.xor( right );
		}
	};

	public static final BinaryBoundsOperator minus = new BinaryBoundsOperator()
	{
		@Override
		public < I extends RealInterval, B extends Bounds< I, B > > B apply( final B left, final B right )
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

	public I interval()
	{
		return interval;
	}

	/**
	 * Intersection of two <b>bounded</b> {@link Bounds}.
	 *
	 * @param arg0
	 *            must not be {@link #isUnbounded() unbounded}
	 * @param arg1
	 *            must not be {@link #isUnbounded() unbounded}
	 *
	 * @return intersection (also bounded)
	 */
	protected abstract B intersectBounded( B arg0, B arg1 );

	/**
	 * Union of two <b>bounded</b> {@link Bounds}.
	 *
	 * @param arg0
	 *            must not be {@link #isUnbounded() unbounded}
	 * @param arg1
	 *            must not be {@link #isUnbounded() unbounded}
	 *
	 * @return intersection (also bounded)
	 */
	protected abstract B unionBounded( B arg0, B arg1 );

	/**
	 * {@link Bounds} resulting from a transformation.
	 *
	 * @param arg0
	 *            must not be {@link #isUnbounded() unbounded}
	 * @param transformToSource
	 *            a {@link RealTransform} or {@link Transform}
	 * @return bounds of the transformed source {@link Bound}, if
	 *         {@code transformToSource} is not invertible returns
	 *         {@link Bounds#UNBOUNDED()}
	 */
	protected abstract B transformBounded( B arg0, Object transformToSource );

	protected abstract B UNBOUNDED();

	@SuppressWarnings( "unchecked" )
	public B and( final B that )
	{
		if ( this.isUnbounded() )
			return that;
		if ( that.isUnbounded() )
			return ( B ) this;
		return intersectBounded( ( B ) this, that );
	}

	@SuppressWarnings( "unchecked" )
	public B or( final B that )
	{
		if ( this.isUnbounded() || that.isUnbounded() )
			return UNBOUNDED();
		return unionBounded( ( B ) this, that );
	}

	public B negate()
	{
		return UNBOUNDED();
	}

	public B xor( final B that )
	{
		return this.or( that );
	}

	@SuppressWarnings( { "unchecked", "unused" } )
	public B minus( final B that )
	{
		return ( B ) this;
	}

	@SuppressWarnings( "unchecked" )
	public B transform( final Object transformToSource )
	{
		return transformBounded( ( B ) this, transformToSource );
	}

	/**
	 * Abstract base class which adapts to changes in source interval, leaving
	 * {@link #min(int)} and {@link #max(int)} methods to be implemented by
	 * derived classes.
	 */
	public static abstract class AbstractAdaptingInterval extends AbstractEuclideanSpace implements Interval
	{
		public AbstractAdaptingInterval( final int n )
		{
			super( n );
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

	/**
	 * Represents the smallest {@link Interval} completely containing a
	 * specified {@link RealInterval}. Adapts to changes of the source interval.
	 */
	public static class SmallestContainingInterval extends AbstractAdaptingInterval
	{
		private final RealInterval source;

		public SmallestContainingInterval( final RealInterval source )
		{
			super( source.numDimensions() );
			this.source = source;
		}

		@Override
		public long min( final int d )
		{
			return ( long ) Math.floor( source.realMin( d ) );
		}

		@Override
		public long max( final int d )
		{
			return ( long ) Math.ceil( source.realMax( d ) );
		}
	}

	/**
	 * The intersection of two intervals. Adapts to changes of the source
	 * intervals.
	 */
	public static class IntersectionInterval extends AbstractAdaptingInterval
	{
		private final Interval i1;

		private final Interval i2;

		public IntersectionInterval( final Interval i1, final Interval i2 )
		{
			super( i1.numDimensions() );
			this.i1 = i1;
			this.i2 = i2;
			assert ( i1.numDimensions() == i2.numDimensions() );
		}

		@Override
		public long min( final int d )
		{
			if ( Intervals.isEmpty( i1 ) || Intervals.isEmpty( i2 ) )
				return Long.MAX_VALUE;
			return Math.max( i1.min( d ), i2.min( d ) );
		}

		@Override
		public long max( final int d )
		{
			if ( Intervals.isEmpty( i1 ) || Intervals.isEmpty( i2 ) )
				return Long.MIN_VALUE;
			return Math.min( i1.max( d ), i2.max( d ) );
		}
	}

	/**
	 * The union of two intervals. Adapts to changes of the source intervals.
	 */
	public static class UnionInterval extends AbstractAdaptingInterval
	{
		private final Interval i1;

		private final Interval i2;

		public UnionInterval( final Interval i1, final Interval i2 )
		{
			super( i1.numDimensions() );
			this.i1 = i1;
			this.i2 = i2;
			assert ( i1.numDimensions() == i2.numDimensions() );
		}

		@Override
		public long min( final int d )
		{
			if ( Intervals.isEmpty( i1 ) )
			{
				if ( Intervals.isEmpty( i2 ) )
					return Long.MAX_VALUE;
				return i2.min( d );
			}
			if ( Intervals.isEmpty( i2 ) )
				return i1.min( d );
			return Math.min( i1.min( d ), i2.min( d ) );
		}

		@Override
		public long max( final int d )
		{
			if ( Intervals.isEmpty( i1 ) )
			{
				if ( Intervals.isEmpty( i2 ) )
					return Long.MIN_VALUE;
				return i2.max( d );
			}
			if ( Intervals.isEmpty( i2 ) )
				return i1.max( d );
			return Math.max( i1.max( d ), i2.max( d ) );
		}
	}

	/**
	 * Implement {@link Bounds} for integer intervals.
	 */
	public static class IntBounds extends Bounds< Interval, IntBounds >
	{
		public static final IntBounds UNBOUNDED = new IntBounds( null );

		public static IntBounds of( final Predicate< ? > predicate )
		{
			if ( predicate instanceof Interval )
				return IntBounds.of( ( Interval ) predicate );
			else if ( predicate instanceof RealInterval )
				return IntBounds.of( new SmallestContainingInterval( ( RealInterval ) predicate ) );
			else
				return IntBounds.UNBOUNDED;
		}

		public static IntBounds of( final Interval i )
		{
			if ( i == null )
				return UNBOUNDED;
			return new IntBounds( i );
		}

		protected IntBounds( final Interval interval )
		{
			super( interval );
		}

		@Override
		protected IntBounds intersectBounded( final IntBounds arg0, final IntBounds arg1 )
		{
			return new IntBounds( new IntersectionInterval( arg0.interval(), arg1.interval() ) );
		}

		@Override
		protected IntBounds unionBounded( final IntBounds arg0, final IntBounds arg1 )
		{
			return new IntBounds( new UnionInterval( arg0.interval(), arg1.interval() ) );
		}

		@Override
		protected IntBounds UNBOUNDED()
		{
			return UNBOUNDED;
		}

		@Override
		protected IntBounds transformBounded( final IntBounds arg0, final Object transformToSource )
		{
			throw new UnsupportedOperationException( "transform not supported for IntBounds" );
		}
	}

	/**
	 * Abstract base class for bounds which adapt to changes in the source
	 * interval, leaving {@link #realMin(int)} and {@link #realMax(int)} methods
	 * to be implemented by derived classes.
	 */
	public static abstract class AbstractAdaptingRealInterval extends AbstractEuclideanSpace implements RealInterval
	{
		public AbstractAdaptingRealInterval( final int n )
		{
			super( n );
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

	/**
	 * The intersection of two intervals. Adapts to changes of the source
	 * intervals.
	 */
	public static class IntersectionRealInterval extends AbstractAdaptingRealInterval
	{
		private final RealInterval i1;

		private final RealInterval i2;

		public IntersectionRealInterval( final RealInterval i1, final RealInterval i2 )
		{
			super( i1.numDimensions() );
			this.i1 = i1;
			this.i2 = i2;
			assert ( i1.numDimensions() == i2.numDimensions() );
		}

		@Override
		public double realMin( final int d )
		{
			if ( Intervals.isEmpty( i1 ) || Intervals.isEmpty( i2 ) )
				return Double.POSITIVE_INFINITY;
			return Math.max( i1.realMin( d ), i2.realMin( d ) );
		}

		@Override
		public double realMax( final int d )
		{
			if ( Intervals.isEmpty( i1 ) || Intervals.isEmpty( i2 ) )
				return Double.NEGATIVE_INFINITY;
			return Math.min( i1.realMax( d ), i2.realMax( d ) );
		}
	}

	/**
	 * The union of two intervals. Adapts to changes of the source intervals.
	 */
	public static class UnionRealInterval extends AbstractAdaptingRealInterval
	{
		private final RealInterval i1;

		private final RealInterval i2;

		public UnionRealInterval( final RealInterval i1, final RealInterval i2 )
		{
			super( i1.numDimensions() );
			this.i1 = i1;
			this.i2 = i2;
			assert ( i1.numDimensions() == i2.numDimensions() );
		}

		@Override
		public double realMin( final int d )
		{
			if ( Intervals.isEmpty( i1 ) )
			{
				if ( Intervals.isEmpty( i2 ) )
					return Double.POSITIVE_INFINITY;
				return i2.realMin( d );
			}
			if ( Intervals.isEmpty( i2 ) )
				return i1.realMin( d );
			return Math.min( i1.realMin( d ), i2.realMin( d ) );
		}

		@Override
		public double realMax( final int d )
		{
			if ( Intervals.isEmpty( i1 ) )
			{
				if ( Intervals.isEmpty( i2 ) )
					return Double.NEGATIVE_INFINITY;
				return i2.realMax( d );
			}
			if ( Intervals.isEmpty( i2 ) )
				return i1.realMax( d );
			return Math.max( i1.realMax( d ), i2.realMax( d ) );
		}
	}

	/**
	 * Implement {@link Bounds} for real intervals.
	 */
	public static class RealBounds extends Bounds< RealInterval, RealBounds >
	{
		public static final RealBounds UNBOUNDED = new RealBounds( null );

		public static RealBounds of( final Predicate< ? > predicate )
		{
			if ( predicate instanceof RealInterval )
				return RealBounds.of( ( RealInterval ) predicate );
			return RealBounds.UNBOUNDED;
		}

		public static RealBounds of( final RealInterval i )
		{
			if ( i == null )
				return UNBOUNDED;
			return new RealBounds( i );
		}

		protected RealBounds( final RealInterval interval )
		{
			super( interval );
		}

		@Override
		protected RealBounds intersectBounded( final RealBounds arg0, final RealBounds arg1 )
		{
			return new RealBounds( new IntersectionRealInterval( arg0.interval(), arg1.interval() ) );
		}

		@Override
		protected RealBounds unionBounded( final RealBounds arg0, final RealBounds arg1 )
		{
			return new RealBounds( new UnionRealInterval( arg0.interval(), arg1.interval() ) );
		}

		@Override
		protected RealBounds UNBOUNDED()
		{
			return UNBOUNDED;
		}

		@Override
		protected RealBounds transformBounded( final RealBounds arg0, final Object transformToSource )
		{
			if ( !( transformToSource instanceof RealTransform ) )
				throw new IllegalArgumentException( "transformToSource must be a RealTransform not " + transformToSource.getClass() );
			if ( !( transformToSource instanceof InvertibleRealTransform ) )
				return UNBOUNDED;
			return new RealBounds( new RealTransformRealInterval( arg0.interval(), ( InvertibleRealTransform ) transformToSource ) );
		}
	}

	// -- Transform --

	/**
	 * Applies a transformation to a given {@link Bounds}.
	 */
	public static final class TransformBoundsOperator implements UnaryBoundsOperator
	{
		private final Object transformToSource;

		/**
		 * Creates a {@link UnaryBoundsOperator} with the given transform.
		 *
		 * @param transformToSource
		 *            a {@link RealTransform} or {@link Transform}
		 */
		public TransformBoundsOperator( final Object transformToSource )
		{
			this.transformToSource = transformToSource;
		}

		@Override
		public < I extends RealInterval, B extends Bounds< I, B > > B apply( final B arg )
		{
			return arg.transform( transformToSource );
		}
	}

	/**
	 * The {@link Bounds} for a transformed source. These bounds are not
	 * guaranteed to represent the minimum bounding box.
	 */
	public static class RealTransformRealInterval extends AbstractAdaptingRealInterval
	{
		private final RealInterval source;

		private final InvertibleRealTransform transformToSource;

		private final double[] cachedSourceMin;

		private final double[] cachedSourceMax;

		private final double[] min;

		private final double[] max;

		/**
		 * Creates {@link Bounds} for a transformed source interval. These
		 * bounds update as the source interval changes.
		 *
		 * @param source
		 *            bounds to be transformed
		 * @param transformToSource
		 *            transformation for going to source
		 */
		public RealTransformRealInterval( final RealInterval source, final InvertibleRealTransform transformToSource )
		{
			super( source.numDimensions() );
			this.source = source;
			this.transformToSource = transformToSource;

			cachedSourceMin = new double[ n ];
			cachedSourceMax = new double[ n ];
			min = new double[ n ];
			max = new double[ n ];

			this.source.realMax( cachedSourceMax );
			this.source.realMin( cachedSourceMin );
			updateMinMax();
		}

		@Override
		public double realMin( final int d )
		{
			if ( updateNeeded() )
				updateMinMax();
			return min[ d ];
		}

		@Override
		public double realMax( final int d )
		{
			if ( updateNeeded() )
				updateMinMax();
			return max[ d ];
		}

		// -- Helper methods --

		private boolean updateNeeded()
		{
			for ( int d = 0; d < n; d++ )
			{
				if ( cachedSourceMin[ d ] != source.realMin( d ) || cachedSourceMax[ d ] != source.realMax( d ) )
					return true;
			}
			return false;
		}

		private void updateMinMax()
		{
			final double[][] transformedCorners = createCorners();

			for ( int d = 0; d < n; d++ )
			{
				double mx = transformedCorners[ 0 ][ d ];
				double mn = transformedCorners[ 0 ][ d ];
				for ( int i = 1; i < transformedCorners.length; i++ )
				{
					if ( transformedCorners[ i ][ d ] > mx )
						mx = transformedCorners[ i ][ d ];
					if ( transformedCorners[ i ][ d ] < mn )
						mn = transformedCorners[ i ][ d ];
				}
				min[ d ] = mn;
				max[ d ] = mx;
			}

			source.realMax( cachedSourceMax );
			source.realMin( cachedSourceMin );
		}

		private double[][] createCorners()
		{
			final double[][] corners = new double[ ( int ) Math.pow( 2, n ) ][ n ];
			int s = corners.length / 2;
			boolean mn = false;
			for ( int d = 0; d < n; d++ )
			{
				for ( int i = 0; i < corners.length; i++ )
				{
					if ( i % s == 0 )
					{
						mn = !mn;
					}
					if ( mn )
						corners[ i ][ d ] = source.realMin( d );
					else
						corners[ i ][ d ] = source.realMax( d );
				}
				s = s / 2;
			}

			for ( int i = 0; i < corners.length; i++ )
				transformToSource.inverse().apply( corners[ i ], corners[ i ] );

			return corners;
		}
	}
}
