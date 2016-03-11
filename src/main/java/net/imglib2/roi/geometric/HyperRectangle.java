package net.imglib2.roi.geometric;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;

/**
 * This class represents a rectangle/cuboid/hyperrectangle region of interest in n-d space.
 *
 * @author Robert Haase, Scientific Computing Facility, MPI-CBG, rhaase@mpi-cbg.de
 * @version 1.0.0 Jan 14, 2016
 *
 */
public class HyperRectangle extends AbstractGeometricShape {

	/**
	 * Initialize the rectangle with position 0 in n dimensions. The extent in all dimensions is zero as well. Furthermore, the object is not rotated in space by default.
	 * @param n
	 */
	public HyperRectangle(int n) {
		super(n);
	}

	/**
	 * Initialize the rectangle with a given center position and extent. 
	 * @param center position of the cube in space, given in pixel coordinates
	 * @param semiAxisLengths array containing n elements representing half values of width/height/depth/...
	 */
	public HyperRectangle(final RealLocalizable center, final double[] semiAxisLengths) {
		super(center, semiAxisLengths);
	}

	/**
	 * Initialize the rectangle with 
	 * @param center
	 * @param semiAxisLengths
	 * @param rotationMatrix
	 */
	public HyperRectangle(final RealLocalizable center, final double[] semiAxisLengths, double[][] rotationMatrix) {
		super(center, semiAxisLengths, rotationMatrix);
	}

	/**
	 * This function determines if a position is inside the rectangle or not.
	 */
	public boolean contains(RealLocalizable l) {
		double[] questionablePosition = new double[n];
		l.localize(questionablePosition);

		double[] temp = new double[n];
		translate(questionablePosition, temp, position, -1);
		rotate(temp, questionablePosition, inverseRotationMatrix);

		boolean isInside = true;
		for (int d = 0; d < n; d++) {
			isInside = isInside && questionablePosition[d] >= -semiAxisLengths[d] && questionablePosition[d] <= semiAxisLengths[d];
			if (!isInside) {
				break;
			}
		}

		return isInside;
	}

	/**
	 * Internal handler to update the bounding box. The bounding box is determined by translating/rotating all corner points of the cuboid in space (as
	 * configured) and afterwards determining minimum and maximum coordinates in all dimensions.
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

	public HyperRectangle copy()
	{
		return new HyperRectangle(new RealPoint(position), semiAxisLengths, rotationMatrix);
	}
}
