package net.imglib2.roi.util;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

public interface UpdateBoundsRealPoint extends RealPositionable, RealLocalizable
{
	void updateBounds();
}
