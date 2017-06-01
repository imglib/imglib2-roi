package net.imglib2.roi.mask.real;

import net.imglib2.RealLocalizable;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.MaskUnaryOperation;

/**
 * A real space {@link Mask} transformed by an {@link AffineTransform}.
 *
 * @author Alison Walter
 */
public interface MaskAffineTransform extends MaskUnaryOperation< RealLocalizable >
{

	AffineGet transform();
}
