package net.imglib2.roi.geometric;

import Jama.Matrix;
import net.imglib2.AbstractRealInterval;
import net.imglib2.Localizable;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.RealRandomAccess;
import net.imglib2.roi.util.ContainsRealRandomAccess;
import net.imglib2.type.logic.BoolType;

/**
 * This class represents a rotatable and positionable binary geometric region of interest in continuous space having an extension >= 0. Easiest example: a cuboid in 3d space.
 * 
 * @author Robert Haase, Scientific Computing Facility, MPI-CBG, rhaase@mpi-cbg.de
 * @version 1.0.0 Jan 13, 2016
 */
public abstract class AbstractGeometricShape extends AbstractRealInterval implements RealRandomAccessibleRealIntervalContains,
				RealPositionable, RealLocalizable {
	protected double[] semiAxisLengths;
	protected double[][] rotationMatrix;
	protected double[][] inverseRotationMatrix;

	protected double[] position;

	public AbstractGeometricShape(int n) {
		super(n);

		semiAxisLengths = new double[n];

		initializeIdentityTransformationMatrix();

		// //
		position = new double[n];
	}

	/**
	 * Initialize the object with center and extension. Rotation will be zero.
	 * 
	 * @param center
	 *            position of the object's center in space.
	 * @param semiAxisLengths
	 *            arrays with values of half of width/height/depth/...
	 */
	public AbstractGeometricShape(final RealLocalizable center, final double[] semiAxisLengths) {
		super(center.numDimensions());
		assert (n == semiAxisLengths.length);

		this.semiAxisLengths = new double[semiAxisLengths.length];
		System.arraycopy(semiAxisLengths, 0, this.semiAxisLengths, 0, semiAxisLengths.length);

		initializeIdentityTransformationMatrix();

		// //
		position = new double[n];
		center.localize(position);

		for (int d = 0; d < n; d++) {
			min[d] = position[d] - semiAxisLengths[d];
			max[d] = position[d] + semiAxisLengths[d];
		}
	}

	/**
	 * Initialize the object with center, extension and rotation
	 * 
	 * @param center
	 *            position of the object's center in space.
	 * @param semiAxisLengths
	 *            arrays with values of half of width/height/depth/...
	 * @param rotationMatrix
	 *            rotation matrix with n times n entries.
	 */
	public AbstractGeometricShape(final RealLocalizable center, final double[] semiAxisLengths, double[][] rotationMatrix) {
		super(center.numDimensions());

		assert (n == semiAxisLengths.length);
		assert (n == rotationMatrix.length);
		for (int d = 0; d < n; d++) {
			assert (n == rotationMatrix[d].length);
		}

		this.semiAxisLengths = new double[semiAxisLengths.length];
		System.arraycopy(semiAxisLengths, 0, this.semiAxisLengths, 0, semiAxisLengths.length);

		this.rotationMatrix = new double[n][n];
		copyMatrix(rotationMatrix, this.rotationMatrix);

		this.inverseRotationMatrix = new double[n][n];
		invertMatrix(this.rotationMatrix, this.inverseRotationMatrix);

		// //
		position = new double[n];
		center.localize(position);

		updateBoundingBox();
	}
	
	public double getSemiAxisLength(int d)
	{
		return semiAxisLengths[d];
	}
	
	public void setSemiAxisLength(double value, int d)
	{
		semiAxisLengths[d] = value;
		updateBoundingBox();
	}
	
	public void setSemiAxisLength(double[] value)
	{
		System.arraycopy(value, 0, this.semiAxisLengths, 0, semiAxisLengths.length);
		updateBoundingBox();
	}
	
	public double[][] getRotationMatrix()
	{
		double[][] matrix = new double[n][n];
		copyMatrix(rotationMatrix, matrix);
		return matrix;
	}
	
	public void setRotationMatrix(double[][] matrix)
	{
		copyMatrix(matrix, rotationMatrix);
		invertMatrix(this.rotationMatrix, this.inverseRotationMatrix);
		updateBoundingBox();
	}
	
	private void copyMatrix(double[][] source, double[][] target)
	{
		for (int d = 0; d < n; d++) {
			System.arraycopy(source[d], 0, target[d], 0, n);
		}
	}
	

	/**
	 * internal handler to initialize a rotation corresponding to a null transform
	 */
	private void initializeIdentityTransformationMatrix() {
		rotationMatrix = new double[n][n];
		inverseRotationMatrix = new double[n][n];
		for (int d = 0; d < n; d++) {
			rotationMatrix[d][d] = 1;
			inverseRotationMatrix[d][d] = 1;
		}
	}

	/**
	 * invert a matrix
	 * 
	 * @param source
	 *            matrix to invert
	 * @param target
	 *            matrix to write the result to
	 */
	private void invertMatrix(double[][] source, double[][] target) {
		Matrix m = new Matrix(source);

		double[][] temp = m.inverse().getArray();
		for (int i = 0; i < n; i++) {
			System.arraycopy(temp, 0, target, 0, n);
		}
	}

	@Override
	public RealRandomAccess<BoolType> realRandomAccess() {
		return new ContainsRealRandomAccess(this);
	}

	@Override
	public RealRandomAccess<BoolType> realRandomAccess(final RealInterval interval) {
		return realRandomAccess();
	}

	/**
	 * Abstract internal handler to update the bounding box. The bounding box obviously depends on the kind of the geometric shape.
	 */
	protected abstract void updateBoundingBox();

	// -----------------------
	// Contains implementation

	/**
	 * Abstract handler to decide if a pixel belongs to the volume or not.
	 */
	@Override
	public abstract boolean contains(RealLocalizable l);

	/**
	 * Translate a position in space. Target and source may point to the same object.
	 * 
	 * @param source
	 *            position to move
	 * @param target
	 *            position to write the result to.
	 * @param translationVector
	 *            vector the position should be moved by.
	 * @param prefactor
	 *            scalar the translation will be multiplied with. By setting prefactor = -1, the translation direction can easily be inverted.
	 */
	final protected void translate(final double[] source, final double[] target, double[] translationVector, double prefactor) {
		for (int td = 0; td < n; td++) {
			target[td] = source[td] + prefactor * translationVector[td];
		}
	}

	/**
	 * rotate a vector using a rotationMatrix. Target and source should not point to the same object.
	 * 
	 * @param source
	 *            vector to rotate
	 * @param target
	 *            vector to write the result to
	 * @param rotationMatrix
	 *            n times n matrix describing the rotation
	 */
	final protected void rotate(final double[] source, final double[] target, double[][] rotationMatrix) {
		// assert source.length >= 3 && target.length >= 3: "3d affine transformations can be applied to 3d coordinates only.";

		/* source and target may be the same vector, so do not write into target before done with source */
		for (int td = 0; td < n; td++) {
			target[td] = 0;
			for (int sd = 0; sd < n; sd++) {
				target[td] += source[sd] * rotationMatrix[td][sd];
			}
		}
	}

	@Override
	public AbstractGeometricShape copyContains() {
		return this;
	}

	// -------------------------------
	// RealPositionable implementation
	@Override
	public void fwd(int d) {
		position[d]++;
		updateBoundingBox();
	}

	@Override
	public void bck(int d) {
		position[d]--;
		updateBoundingBox();
	}

	@Override
	public void move(int distance, int d) {
		position[d] += distance;
		updateBoundingBox();
	}

	@Override
	public void move(long distance, int d) {
		position[d] += distance;
		updateBoundingBox();
	}

	@Override
	public void move(Localizable localizable) {
		double[] distance = new double[n];
		localizable.localize(distance);
		move(distance);
	}

	@Override
	public void move(int[] distance) {
		for (int d = 0; d < n; d++) {
			position[d] += distance[d];
		}
		updateBoundingBox();
	}

	@Override
	public void move(long[] distance) {
		for (int d = 0; d < n; d++) {
			position[d] += distance[d];
		}
		updateBoundingBox();
	}

	@Override
	public void setPosition(Localizable localizable) {
		double[] distance = new double[n];
		localizable.localize(distance);
		setPosition(distance);
	}

	@Override
	public void setPosition(int[] position) {
		for (int d = 0; d < n; d++) {
			this.position[d] = position[d];
		}
		updateBoundingBox();
	}

	@Override
	public void setPosition(long[] position) {
		for (int d = 0; d < n; d++) {
			this.position[d] = position[d];
		}
		updateBoundingBox();
	}

	@Override
	public void setPosition(int position, int d) {
		this.position[d] = position;
		updateBoundingBox();
	}

	@Override
	public void setPosition(long position, int d) {
		this.position[d] = position;
		updateBoundingBox();
	}

	@Override
	public void move(float distance, int d) {
		this.position[d] += distance;
		updateBoundingBox();
	}

	@Override
	public void move(double distance, int d) {
		this.position[d] += distance;
		updateBoundingBox();
	}

	@Override
	public void move(RealLocalizable localizable) {
		double[] distance = new double[n];
		localizable.localize(distance);
		move(distance);
	}

	@Override
	public void move(float[] distance) {
		for (int d = 0; d < n; d++) {
			this.position[d] += position[d];
		}
		updateBoundingBox();
	}

	@Override
	public void move(double[] distance) {
		for (int d = 0; d < n; d++) {
			this.position[d] += position[d];
		}
		updateBoundingBox();
	}

	@Override
	public void setPosition(RealLocalizable localizable) {
		double[] distance = new double[n];
		localizable.localize(distance);
		setPosition(distance);
	}

	@Override
	public void setPosition(float[] position) {
		for (int d = 0; d < n; d++) {
			this.position[d] = position[d];
		}
		updateBoundingBox();
	}

	@Override
	public void setPosition(double[] position) {
		for (int d = 0; d < n; d++) {
			this.position[d] = position[d];
		}
		updateBoundingBox();
	}

	@Override
	public void setPosition(float position, int d) {
		this.position[d] = position;
		updateBoundingBox();
	}

	@Override
	public void setPosition(double position, int d) {
		this.position[d] = position;
		updateBoundingBox();
	}
	

	@Override
	public void localize(float[] position) {
		for (int d = 0; d < n; d++)
		{
			position[d] = (float)this.position[d];
		}
	}

	@Override
	public void localize(double[] position) {
		System.arraycopy(this.position, 0, position, 0, this.position.length);

	}

	@Override
	public float getFloatPosition(int d) {
		return (float)position[d];
	}

	@Override
	public double getDoublePosition(int d) {
		return position[d];
	}

}
