
package net.imglib2.roi.geometric;

import net.imglib2.RealLocalizable;

/**
 * A polytope is a geometric object with flat sides, and may exist in any
 * general number of dimensions n as an n-dimensional polytope.
 *
 * @author Daniel Seebacher, University of Konstanz.
 */
public interface Polytope {

	/**
	 * @param rl
	 *            a {@link RealLocalizable}
	 * @return true if the {@link Polytope} contains the given
	 *         {@link RealLocalizable}
	 */
	public boolean contains(RealLocalizable rl);

}
