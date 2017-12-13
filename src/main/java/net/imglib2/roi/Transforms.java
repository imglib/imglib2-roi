package net.imglib2.roi;

import static net.imglib2.roi.BoundaryType.UNSPECIFIED;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.imglib2.EuclideanSpace;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.InvertibleRealTransform;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.roi.Operators.MaskOperator;
import net.imglib2.roi.composite.RealTransformUnaryCompositeRealMask;
import net.imglib2.roi.composite.RealTransformUnaryCompositeRealMaskRealInterval;
import net.imglib2.util.Intervals;

public class Transforms
{
	public static final boolean isContinuous( RealTransform transform )
	{
		return transform instanceof AffineGet; // TODO
	}

	public static final boolean willPreserveBounds( RealTransform transform )
	{
		return transform instanceof AffineGet; // TODO
	}


	public static class RealTransformMaskOperator implements MaskOperator
	{
		private final RealTransform transformToSource;

		/**
		 * Number of dimensions of the target mask (that this operator creates).
		 */
		private final int n;

		/**
		 * Number of dimensions of the source mask (to which this operator is applied).
		 */
		private final int m;

		private final ThreadLocal< RealPoint > pt;

		private final UnaryOperator< BoundaryType > boundaryTypeOp;

		private final UnaryOperator< KnownConstant > knownConstantOp;

		public RealTransformMaskOperator( final RealTransform transformToSource )
		{
			this.transformToSource = transformToSource;
			n = transformToSource.numSourceDimensions();
			m = transformToSource.numTargetDimensions();
			pt = ThreadLocal.withInitial( () -> new RealPoint( m ) );
			boundaryTypeOp = ( willPreserveBounds( transformToSource ) && isContinuous( transformToSource ) )
					? UnaryOperator.identity()
					: t -> UNSPECIFIED;
			knownConstantOp = UnaryOperator.identity();
		}

		public RealTransform getTransformToSource()
		{
			return transformToSource;
		}

		public Predicate< RealLocalizable > predicate( final Predicate< ? super RealLocalizable > arg )
		{
			return pos -> {
				final RealPoint sourcePos = pt.get();
				transformToSource.apply( pos, sourcePos );
				return arg.test( sourcePos );
			};
		}

		public RealMask applyReal( final Predicate< ? super RealLocalizable > arg )
		{
			checkDimensions( arg );
			final BoundaryType boundaryType = boundaryTypeOp.apply( BoundaryType.of( arg ) );
			if( arg instanceof RealInterval && willPreserveBounds( transformToSource ) )
				return new RealTransformUnaryCompositeRealMaskRealInterval( this, arg,
						new RealTransformRealInterval( ( RealInterval ) arg, ( InvertibleRealTransform ) transformToSource ),
						boundaryType, knownConstantOp );
			return new RealTransformUnaryCompositeRealMask( this, arg, n, boundaryType, knownConstantOp );
		}

		public RealMaskRealInterval applyRealInterval( final Predicate< ? super RealLocalizable > arg )
		{
			final RealMask mask = applyReal( arg );
			if ( mask instanceof RealMaskRealInterval )
				return ( RealMaskRealInterval ) mask;
			throw new IllegalArgumentException( "result is not an interval" );
		}

		@Override
		public boolean equals( final Object obj )
		{
			if( obj instanceof RealTransformMaskOperator )
			{
				return transformToSource.equals( ( ( RealTransformMaskOperator ) obj ).getTransformToSource() );
			}
			return false;
		}

		@Override
		public int hashCode()
		{
			return transformToSource.hashCode() * 23;
		}

		private void checkDimensions( Object source )
		{
			if ( source instanceof EuclideanSpace )
			{
				if ( ( ( EuclideanSpace ) source ).numDimensions() != m )
					throw new IllegalArgumentException( "incompatible dimensionalities" );
			}
			else
				throw new IllegalArgumentException( "couldn't find dimensionality" );
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
			// NB: transformToSource so final dimensions of resulting Mask are source dimensions
			super( transformToSource.numSourceDimensions() );
			this.source = source;
			this.transformToSource = transformToSource;

			cachedSourceMin = new double[ this.transformToSource.numTargetDimensions() ];
			cachedSourceMax = new double[ this.transformToSource.numTargetDimensions() ];
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
			for ( int d = 0; d < transformToSource.numTargetDimensions(); d++ )
			{
				if ( cachedSourceMin[ d ] != source.realMin( d ) || cachedSourceMax[ d ] != source.realMax( d ) )
					return true;
			}
			return false;
		}

		private void updateMinMax()
		{
			if( Intervals.isEmpty( source ) )
			{
				Arrays.fill( max, Double.NEGATIVE_INFINITY );
				Arrays.fill( min, Double.POSITIVE_INFINITY );
			}
			final double[] sMx = new double[ transformToSource.numTargetDimensions() ];
			final double[] sMn = new double[ sMx.length ];

			while ( !Arrays.equals( sMx, cachedSourceMax ) ||
					!Arrays.equals( sMn, cachedSourceMin ) )
			{
				source.realMax( sMx );
				source.realMin( sMn );

				final double[][] transformedCorners = createCorners();
				final int numTransformedCorners = transformedCorners.length;

				for ( int d = 0; d < n; d++ )
				{
					double mx = transformedCorners[ 0 ][ d ];
					double mn = transformedCorners[ 0 ][ d ];
					for ( int i = 1; i < numTransformedCorners; i++ )
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
		}

		private double[][] createCorners()
		{
			final int numCorners = ( int ) Math.pow( 2, transformToSource.numTargetDimensions() );
			final int numSourceDims = transformToSource.numTargetDimensions();
			final double[][] cornersTransformed = new double[ numCorners ][ numSourceDims ];
			int s = numCorners / 2;
			boolean mn = false;
			for ( int d = 0; d < numSourceDims; d++ )
			{
				for ( int i = 0; i < numCorners; i++ )
				{
					if ( i % s == 0 )
					{
						mn = !mn;
					}
					if ( mn )
						cornersTransformed[ i ][ d ] = source.realMin( d );
					else
						cornersTransformed[ i ][ d ] = source.realMax( d );
				}
				s = s / 2;
			}

			final double[][] points = new double[ numCorners ][ n ];

				for ( int i = 0; i < points.length; i++ )
					transformToSource.inverse().apply( cornersTransformed[ i ], points[ i ] );

			return points;
		}
	}

}
