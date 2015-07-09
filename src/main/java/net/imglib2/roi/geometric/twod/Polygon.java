
package net.imglib2.roi.geometric.twod;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.geometric.Polytope;
import net.imglib2.roi.geometric.PolytopRandomAccess;
import net.imglib2.util.Intervals;

/**
 * A {@link Polygon} is bounded by a finite closed polygonal chain and is a
 * 2-dimensional example of the more general polytope.
 *
 * @author Daniel Seebacher, University of Konstanz.
 */
public class Polygon extends PolygonalChain implements Polytope

{

	public Polygon(final List<RealLocalizable> points) {
		super(points);
	}

	public Polygon() {
		this(new ArrayList<RealLocalizable>());
	}

	/**
	 * Return true if the given point is contained inside the boundary. See:
	 * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
	 */
	@Override
	public boolean contains(final RealLocalizable localizable) {
		if (localizable.numDimensions() == 2 && Intervals.contains(this, localizable)) {
			int i;
			int j;
			boolean result = false;
			for (i = 0, j = this.vertices().size() - 1; i < this.vertices().size(); j = i++) {
				if ((this.vertices().get(i).getDoublePosition(1) > localizable.getDoublePosition(1)) != (this.vertices()
						.get(j).getDoublePosition(1) > localizable.getDoublePosition(1))
						&& (localizable.getDoublePosition(0) < (this.vertices().get(j).getDoublePosition(0)
								- this.vertices().get(i).getDoublePosition(0))
								* (localizable.getDoublePosition(1) - this.vertices().get(i).getDoublePosition(1))
								/ (this.vertices().get(j).getDoublePosition(1)
										- this.vertices().get(i).getDoublePosition(1))
								+ this.vertices().get(i).getDoublePosition(0))) {
					result = !result;
				}
			}

			return result;
		}
		return false;
	}

	@Override
	public PolytopRandomAccess realRandomAccess() {
		return new PolytopRandomAccess(this, this);
	}

	@Override
	public PolytopRandomAccess realRandomAccess(final RealInterval interval) {
		return new PolytopRandomAccess(this, interval);
	}

}
