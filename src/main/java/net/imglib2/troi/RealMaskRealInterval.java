package net.imglib2.troi;

import java.util.function.Predicate;
import net.imglib2.Interval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;

public interface RealMaskRealInterval extends RealMask, RealInterval
{
	@Override
	public RealMaskRealInterval and( Predicate< ? super RealLocalizable > other );
}
