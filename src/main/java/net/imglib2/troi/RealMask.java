package net.imglib2.troi;

import java.util.function.Predicate;
import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;

public interface RealMask extends MaskPredicate< RealLocalizable >, EuclideanSpace
{
	@Override
	public RealMask and( Predicate< ? super RealLocalizable > other );

	@Override
	public RealMask or( Predicate< ? super RealLocalizable > other );

	@Override
	public RealMask negate();

}
