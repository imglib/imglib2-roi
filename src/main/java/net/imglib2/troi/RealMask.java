package net.imglib2.troi;

import java.util.function.Predicate;
import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.realtransform.RealTransform;

public interface RealMask extends MaskPredicate< RealLocalizable >, EuclideanSpace
{
	@Override
	public default RealMask and( Predicate< ? super RealLocalizable > other )
	{
		throw new UnsupportedOperationException( "TODO" );
	}

	@Override
	public default RealMask or( Predicate< ? super RealLocalizable > other )
	{
		throw new UnsupportedOperationException( "TODO" );
	}

	@Override
	public default RealMask negate()
	{
		throw new UnsupportedOperationException( "TODO" );
	}

	@Override
	public default RealMask substract( Predicate< ? super RealLocalizable > other )
	{
		throw new UnsupportedOperationException( "TODO" );
	}

	@Override
	public default RealMask xor( Predicate< ? super RealLocalizable > other )
	{
		throw new UnsupportedOperationException( "TODO" );
	}

	public default RealMask transform( RealTransform transformToSource )
	{
		throw new UnsupportedOperationException( "TODO" );
	}
}
