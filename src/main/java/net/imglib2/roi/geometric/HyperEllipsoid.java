package net.imglib2.roi.geometric;

import java.util.Arrays;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;

/**
 * This class implements a positionable, rotatable elipsoid in space with possible different extensions in different dimensions.
 * 
 * TODO: An ellipsoid with an extension of zero in any dimension does not contain any voxels. However, the in the @link{HyperRectangle} implementation this is
 * contrary.
 * 
 * @author Robert Haase, Scientific Computing Facility, MPI-CBG, rhaase@mpi-cbg.de
 * @version 1.0.0 Jan 14, 2016
 */
public class HyperEllipsoid extends AbstractGeometricShape {

	double exponent = 2;

	/**
	 * Initialize the hypercube with position 0 in n dimensions. The extent in all dimensions is zero as well. Furthermore, the object is not rotated in space
	 * by default.
	 * 
	 * @param n
	 */
	public HyperEllipsoid(int n) {
		super(n);
	}

	/**
	 * Initialize the ellipse with a given center position and extent.
	 * 
	 * @param center
	 *            position of the cube in space, given in pixel coordinates
	 * @param semiAxisLengths
	 *            array containing n elements representing half values of width/height/depth/...
	 */
	public HyperEllipsoid(final RealLocalizable center, final double[] semiAxisLengths) {
		super(center, semiAxisLengths);
	}

	/**
	 * Initialize the ellipse with
	 * 
	 * @param center
	 * @param semiAxisLengths
	 * @param rotationMatrix
	 */
	public HyperEllipsoid(final RealLocalizable center, final double[] semiAxisLengths, double[][] rotationMatrix) {
		super(center, semiAxisLengths, rotationMatrix);
	}

	/**
	 * Initialize the ellipse with
	 * 
	 * @param center
	 * @param semiAxisLengths
	 * @param rotationMatrix
	 */
	public HyperEllipsoid(final RealLocalizable center, final double[] semiAxisLengths, double[][] rotationMatrix, double exponent) {
		super(center, semiAxisLengths, rotationMatrix);
		this.exponent = exponent;
	}

	/**
	 * This function determines if a position is inside the cuboid or not.
	 */
	public boolean contains(RealLocalizable l) {
		double[] questionablePosition = new double[n];
		l.localize(questionablePosition);

		double[] temp = new double[n];
		translate(questionablePosition, temp, position, -1);
		rotate(temp, questionablePosition, inverseRotationMatrix);

		double distancePowered = 0;
		for (int d = 0; d < n; d++) {
			distancePowered += Math.pow(Math.abs((questionablePosition[d]) / semiAxisLengths[d]), exponent);
		}

		return (distancePowered <= 1.0);
	}

	/**
	 * Internal handler to update the bounding box. The bounding box is determined by translating/rotating all corner points of the cuboid in space (as
	 * configured) and afterwards determining minimum and maximum coordinates in all dimensions.
	 * 
	 * TODO: The implementation presented here is taken from a hyperrectangle. It should be reprogrammed to match the hyperellipsoid, because the bounding box
	 * of it may be smaller in fact.
	 */
	protected void updateBoundingBox() {
		// there are n*8 points defining minimum and maximum positions. each of them has n scalars:
		double[][] boundingBoxPositions = new double[(int) Math.pow(2, n)][n];

		// initialize position
		for (int d = 0; d < n; d++) {
			min[d] = 0;
			max[d] = 0;
		}

		// collect all corner positions of the unrotated object
		int counter = 0;
		for (int i = 0; i < boundingBoxPositions.length; i++) {
			for (int d = 0; d < n; d++) {
				if ((i & (1 << d)) > 0)
				{
					boundingBoxPositions[i][d] = semiAxisLengths[d];
				}
				else
				{
					boundingBoxPositions[i][d] = -semiAxisLengths[d];
				}
			}
		}
		

		// rotate them as configured
		for (int i = 0; i < boundingBoxPositions.length; i++) {
			double[] temp = new double[n];
			this.rotate(boundingBoxPositions[i], temp, inverseRotationMatrix);
			for (int d = 0; d < n; d++) {
				if (min[d] > temp[d]) {
					min[d] = temp[d];
				}
				if (max[d] < temp[d]) {
					max[d] = temp[d];
				}
			}
		}

		// determine minimum and maximum coordinates in all dimensions
		for (int d = 0; d < n; d++) {
			min[d] += position[d];
			max[d] += position[d];
		}
	}

	public double getExponent() {
		return exponent;
	}

	public void setExponent(double exponent) {
		this.exponent = exponent;
		
		updateBoundingBox();
	}
	
	public HyperEllipsoid copy()
	{	
		return new HyperEllipsoid(new RealPoint(position), semiAxisLengths, rotationMatrix, exponent);
	}

}
