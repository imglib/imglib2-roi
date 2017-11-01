package net.imglib2.troi.util;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

public interface UpdateBoundsRealPoint extends RealPositionable, RealLocalizable
{
	void updateBounds();
}
