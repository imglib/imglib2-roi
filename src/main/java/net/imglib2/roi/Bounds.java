/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.AbstractWrappedInterval;
import net.imglib2.AbstractWrappedRealInterval;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RealInterval;
import net.imglib2.RealPositionable;
import net.imglib2.realtransform.InvertibleRealTransform;
import net.imglib2.util.Intervals;

import java.util.Arrays;
import java.util.function.Predicate;

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

	public static final BinaryBoundsOperator AND = new BinaryBoundsOperator()
	{
		@Override
		public < I extends RealInterval, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.and( right );
		}
	};

	public static final BinaryBoundsOperator OR = new BinaryBoundsOperator()
	{
		@Override
		public < I extends RealInterval, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.or( right );
		}
	};

	public static final UnaryBoundsOperator NEGATE = new UnaryBoundsOperator()
	{
		@Override
		public < I extends RealInterval, B extends Bounds< I, B > > B apply( final B arg )
		{
			return arg.negate();
		}
	};

	public static final BinaryBoundsOperator XOR = new BinaryBoundsOperator()
	{
		@Override
		public < I extends RealInterval, B extends Bounds< I, B > > B apply( final B left, final B right )
		{
			return left.xor( right );
		}
	};

	public static final BinaryBoundsOperator MINUS = new BinaryBoundsOperator()
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

	protected abstract B unbounded();

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
			return unbounded();
		return unionBounded( ( B ) this, that );
	}

	public B negate()
	{
		return unbounded();
	}

	public B xor( final B that )
	{
		return this.or( that );
	}

	@SuppressWarnings( { "unchecked" } )
	public B minus( final B that )
	{
		return ( B ) this;
	}

	/**
	 * Abstract base class which adapts to changes in source interval, 
	 * leaving {@link #minMax(long[], long[])} to be implemented by
	 * derived classes.
	 */
	public static abstract class AbstractAdaptingInterval extends AbstractAdaptingRealInterval implements Interval
	{
		public AbstractAdaptingInterval( final int n )
		{
			super( n );
		}

		@Override
		public long min( final int d )
		{
			final long[] min = new long[ n ];
			min( min );
			return min[ d ];
		}

		@Override
		public long max( final int d )
		{
			final long[] max = new long[ n ];
			max( max );
			return max[ d ];
		}

		@Override
		public void min( final long[] min )
		{
			minMax( min, null );
		}

		@Override
		public void max( final long[] max )
		{
			minMax( null, max );
		}

		@Override
		public void min( final Positionable min )
		{
			final long[] minArray = new long[ n ];
			min( minArray );
			min.setPosition( minArray );
		}

		@Override
		public void max( final Positionable max )
		{
			final long[] maxArray = new long[ n ];
			max( maxArray );
			max.setPosition( maxArray );
		}

		public abstract void minMax( long[] min, long[] max );

		@Override
		public void realMinMax( final double[] realMin, final double[] realMax )
		{
			final long[] min = realMin == null ? null : new long[ n ];
			final long[] max = realMax == null ? null : new long[ n ];
			minMax( min, max );

			if ( realMin != null )
				for ( int d = 0; d < n; d++ )
					realMin[ d ] = min[ d ];
			if ( realMax != null )
				for ( int d = 0; d < n; d++ )
					realMax[ d ] = max[ d ];
		}

		@Override
		public void dimensions( final long[] dimensions )
		{
			final long[] min = new long[ n ];
			final long[] max = new long[ n ];
			minMax( min, max );

			for ( int d = 0; d < n; ++d )
				dimensions[ d ] = max[ d ] - min[ d ] + 1;
		}

		@Override
		public long dimension( final int d )
		{
			final long[] min = new long[ n ];
			final long[] max = new long[ n ];
			minMax( min, max );

			return max[ d ] - min[ d ] + 1;
		}

		@Override
		public void dimensions( final Positionable dimensions )
		{
			final long[] min = new long[ n ];
			final long[] max = new long[ n ];
			minMax( min, max );

			for ( int d = 0; d < n; d++ )
				dimensions.setPosition( max[ d ] - min[ d ] + 1, d );
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
		public void minMax( long[] min, long[] max )
		{
			if ( min != null )
				for ( int d = 0; d < n; d++ )
					min[ d ] = ( long ) Math.floor( source.realMin( d ) );

			if ( max != null )
				for ( int d = 0; d < n; d++ )
					max[ d ] = ( long ) Math.ceil( source.realMax( d ) );
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
		public void minMax( long[] min, long[] max )
		{
			final long[] min1 = new long[ n ];
			final long[] max1 = new long[ n ];
			getMinMax( i1, min1, max1 );

			final long[] min2 = new long[ n ];
			final long[] max2 = new long[ n ];
			getMinMax( i2, min2, max2 );

			if ( min != null )
				for ( int d = 0; d < n; d++ )
					min[ d ] = Math.max( min1[ d ], min2[ d ] );

			if ( max != null )
				for ( int d = 0; d < n; d++ )
					max[ d ] = Math.min( max1[ d ], max2[ d ] );
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
		public void minMax( final long[] min, final long[] max )
		{
			final long[] min1 = new long[ n ];
			final long[] max1 = new long[ n ];
			getMinMax( i1, min1, max1 );

			final long[] min2 = new long[ n ];
			final long[] max2 = new long[ n ];
			getMinMax( i2, min2, max2 );

			if ( isEmpty( min1, max1 ) )
			{
				if ( min != null )
					System.arraycopy( min2, 0, min, 0, n );
				if ( max != null )
					System.arraycopy( max2, 0, max, 0, n );
			}
			else if ( isEmpty( min2, max2 ) )
			{
				if ( min != null )
					System.arraycopy( min1, 0, min, 0, n );
				if ( max != null )
					System.arraycopy( max1, 0, max, 0, n );
			}
			else
			{
				if ( min != null )
					for ( int d = 0; d < n; d++ )
						min[ d ] = Math.min( min1[ d ], min2[ d ] );
				if ( max != null )
					for ( int d = 0; d < n; d++ )
						max[ d ] = Math.max( max1[ d ], max2[ d ] );
			}
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
		protected IntBounds unbounded()
		{
			return UNBOUNDED;
		}
	}

	/**
	 * Abstract base class for bounds which adapt to changes in the source
	 * interval, leaving the {@link #realMinMax(double[], double[])} method
	 * to be implemented by derived classes.
	 */
	public static abstract class AbstractAdaptingRealInterval extends AbstractEuclideanSpace implements RealInterval
	{
		public AbstractAdaptingRealInterval( final int n )
		{
			super( n );
		}

		@Override
		public double realMin( final int d )
		{
			final double[] min = new double[ n ];
			realMin( min );
			return min[ d ];
		}

		@Override
		public double realMax( final int d )
		{
			final double[] max = new double[ n ];
			realMax( max );
			return max[ d ];
		}

		@Override
		public void realMin( final double[] realMin )
		{
			realMinMax( realMin, null );
		}

		@Override
		public void realMax( final double[] realMax )
		{
			realMinMax( null, realMax );
		}

		@Override
		public void realMin( final RealPositionable realMin )
		{
			final double[] pos = new double[ n ];
			realMin( pos );
			for ( int d = 0; d < n; ++d )
				realMin.setPosition( pos[ d ], d );
		}

		@Override
		public void realMax( final RealPositionable realMax )
		{
			final double[] pos = new double[ n ];
			realMax( pos );
			for ( int d = 0; d < n; ++d )
				realMax.setPosition( pos[ d ], d );
		}

		public abstract void realMinMax( final double[] realMin, final double[] realMax );

	}

	/**
	 * The intersection of two intervals.
	 * Adapts to changes of the source intervals.
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
		public void realMinMax( final double[] realMin, final double[] realMax )
		{
			final double[] min1 = new double[ n ];
			final double[] max1 = new double[ n ];
			getMinMax( i1, min1, max1 );

			final double[] min2 = new double[ n ];
			final double[] max2 = new double[ n ];
			getMinMax( i2, min2, max2 );

			if ( realMin != null )
				for ( int d = 0; d < n; d++ )
					realMin[ d ] = Math.max( min1[ d ], min2[ d ] );

			if ( realMax != null )
				for ( int d = 0; d < n; d++ )
					realMax[ d ] = Math.min( max1[ d ], max2[ d ] );
		}
	}

	private static boolean isEmpty( final double[] realMin, final double[] realMax )
	{
		for ( int d = 0; d < realMin.length; ++d )
			if ( realMin[ d ] > realMax[ d ] )
				return true;
		return false;
	}

	private static boolean isEmpty( final long[] min, final long[] max )
	{
		for ( int d = 0; d < min.length; ++d )
			if ( min[ d ] > max[ d ] )
				return true;
		return false;
	}

	private static void getMinMax( final RealInterval interval, final double[] min, final double[] max )
	{
		if ( interval instanceof AbstractWrappedRealInterval )
		{
			getMinMax( ( ( AbstractWrappedRealInterval< ? > ) interval ).getSource(), min, max );
		}
		else if ( interval instanceof AbstractAdaptingRealInterval )
		{
			( ( AbstractAdaptingRealInterval ) interval ).realMinMax( min, max );
		}
		else
		{
			interval.realMin( min );
			interval.realMax( max );
		}
	}

	private static void getMinMax( final Interval interval, final long[] min, final long[] max )
	{
		if ( interval instanceof AbstractWrappedInterval )
		{
			getMinMax( ( ( AbstractWrappedInterval< ? > ) interval ).getSource(), min, max );
		}
		else if ( interval instanceof AbstractAdaptingInterval )
		{
			( ( AbstractAdaptingInterval ) interval ).minMax( min, max );
		}
		else
		{
			interval.min( min );
			interval.max( max );
		}
	}

	/**
	 * The union of two intervals.
	 * Adapts to changes of the source intervals.
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

		public void realMinMax( final double[] realMin, final double[] realMax )
		{
			final double[] min1 = new double[ n ];
			final double[] max1 = new double[ n ];
			getMinMax( i1, min1, max1 );

			final double[] min2 = new double[ n ];
			final double[] max2 = new double[ n ];
			getMinMax( i2, min2, max2 );

			if ( isEmpty( min1, max1 ) )
			{
				if ( realMin != null )
					System.arraycopy( min2, 0, realMin, 0, n );
				if ( realMax != null )
					System.arraycopy( max2, 0, realMax, 0, n );
			}
			else if ( isEmpty( min2, max2 ) )
			{
				if ( realMin != null )
					System.arraycopy( min1, 0, realMin, 0, n );
				if ( realMax != null )
					System.arraycopy( max1, 0, realMax, 0, n );
			}
			else
			{
				if ( realMin != null )
					for ( int d = 0; d < n; d++ )
						realMin[ d ] = Math.min( min1[ d ], min2[ d ] );
				if ( realMax != null )
					for ( int d = 0; d < n; d++ )
						realMax[ d ] = Math.max( max1[ d ], max2[ d ] );
			}
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
		protected RealBounds unbounded()
		{
			return UNBOUNDED;
		}
	}

	/**
	 * The {@link Bounds} for a transformed source. These bounds are not
	 * guaranteed to represent the minimum bounding box.
	 */
	public static class RealTransformRealInterval extends Bounds.AbstractAdaptingRealInterval
	{
		private final RealInterval source;

		private final InvertibleRealTransform transformToSource;

		private final double[] cachedSourceMin;

		private final double[] cachedSourceMax;

		private final double[] currentSourceMin;

		private final double[] currentSourceMax;

		private final double[] min;

		private final double[] max;

		private final int numSourceDimensions;

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
			// NB: transformToSource so dimensions of resulting Mask are source dimensions
			super( transformToSource.numSourceDimensions() );

			this.source = source;
			this.transformToSource = transformToSource;

			assert source.numDimensions() == transformToSource.numTargetDimensions();
			numSourceDimensions = source.numDimensions();

			cachedSourceMin = new double[ numSourceDimensions ];
			cachedSourceMax = new double[ numSourceDimensions ];
			getMinMax( source, cachedSourceMin, cachedSourceMax );

			currentSourceMin = new double[ numSourceDimensions ];
			currentSourceMax = new double[ numSourceDimensions ];

			min = new double[ n ];
			max = new double[ n ];

			updateMinMax();
		}

		@Override
		public double realMin( final int d )
		{
			updateMinMaxIfNeeded();

			return min[ d ];
		}

		@Override
		public double realMax( final int d )
		{
			updateMinMaxIfNeeded();

			return max[ d ];
		}

		@Override
		public void realMinMax( double[] realMin, double[] realMax )
		{
			updateMinMaxIfNeeded();

			if ( realMin != null )
				System.arraycopy( min, 0, realMin, 0 , n );
			if ( realMax != null )
				System.arraycopy( max, 0, realMax, 0 , n );
		}

		// -- Helper methods --

		private void updateMinMaxIfNeeded()
		{
			getMinMax( source, currentSourceMin, currentSourceMax );

			for ( int d = 0; d < numSourceDimensions; d++ )
			{
				if ( cachedSourceMin[ d ] != currentSourceMin[ d ] || cachedSourceMax[ d ] != currentSourceMax[ d ] )
				{
					System.arraycopy( currentSourceMin, 0, cachedSourceMax, 0 , numSourceDimensions );
					System.arraycopy( currentSourceMax, 0, cachedSourceMin, 0 , numSourceDimensions );
					updateMinMax();
					break;
				}
			}
		}

		private void updateMinMax( )
		{
			if( Intervals.isEmpty( source ) )
			{
				Arrays.fill( max, Double.NEGATIVE_INFINITY );
				Arrays.fill( min, Double.POSITIVE_INFINITY );
			}

			final double[][] transformedCorners = createCorners();
			final int numTransformedCorners = transformedCorners.length;

			for ( int d = 0; d < n; d++ )
			{
				double maxCorner = transformedCorners[ 0 ][ d ];
				double minCorner = transformedCorners[ 0 ][ d ];
				for ( int i = 1; i < numTransformedCorners; i++ )
				{
					minCorner = Math.min( minCorner, transformedCorners[ i ][ d ] );
					maxCorner = Math.max( maxCorner, transformedCorners[ i ][ d ] );
				}
				min[ d ] = minCorner;
				max[ d ] = maxCorner;
			}
		}

		private double[][] createCorners()
		{
			final double[][] cornersTransformed = corners( cachedSourceMin, cachedSourceMax );
			final double[][] points = new double[ cornersTransformed.length ][ n ];
			for ( int i = 0; i < points.length; i++ )
				transformToSource.inverse().apply( cornersTransformed[ i ], points[ i ] );
			return points;
		}

		// TODO: This is similar to
		//       	https://github.com/bigdataviewer/bigdataviewer-core/blob/d6edacb661291ef5d6b78df1cd9a1b8045553052/src/main/java/bdv/tools/boundingbox/IntervalCorners.java
		//       Would it make sense to put it into imglib2 core?
		//       Also related to (special case of)
		//       	https://github.com/imglib/imglib2-realtransform/pull/37
		//       	https://github.com/imglib/imglib2-realtransform/issues/6
		/**
		 * Compute the corners of a RealInterval given by {@code min} and {@code max}.
		 * <p>
		 * A n-dimensional interval has {@code 2^n} corners.
		 * <p>
		 * The index of a corner is interpreted as a binary number where bit 0
		 * corresponds to X, bit 1 corresponds to Y, etc. A zero bit means min in the
		 * corresponding dimension, a one bit means max in the corresponding dimension.
		 */
		private static double[][] corners( final double[] min, final double[] max )
		{
			assert min.length == max.length;
			final int n = min.length;
			final int numCorners = 1 << n;
			final double[][] corners = new double[ numCorners ][ n ];
			for ( int index = 0; index < numCorners; index++ )
				for ( int d = 0, mask = numCorners >> 1; d < n; ++d, mask >>= 1 )
					corners[ index ][ d ] = ( index & mask ) == 0 ? min[ d ] : max[ d ];
			return corners;
		}
	}

}
