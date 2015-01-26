package net.imglib2.roi.labeling;

import net.imglib2.Interval;
import net.imglib2.converter.AbstractConvertedRandomAccess;
import net.imglib2.type.logic.BoolType;

public class LabelRegionRandomAccess< T > extends AbstractConvertedRandomAccess< LabelingType< T >, BoolType >
{
	private final T label;

	private final BoolType type;

	public LabelRegionRandomAccess( final LabelRegion< T > region )
	{
		super( region.regions.labeling.randomAccess( region ) );
		label = region.getLabel();
		type = new BoolType();
	}

	public LabelRegionRandomAccess( final LabelRegion< T > region, final Interval interval )
	{
		super( region.regions.labeling.randomAccess( interval ) );
		label = region.getLabel();
		type = new BoolType();
	}

	protected LabelRegionRandomAccess( final LabelRegionRandomAccess< T > a )
	{
		super( a.source.copyRandomAccess() );
		type = a.type.copy();
		label = a.label;
	}

	@Override
	public BoolType get()
	{
		type.set( source.get().contains( label ) );
		return type;
	}

	@Override
	public LabelRegionRandomAccess< T > copy()
	{
		return new LabelRegionRandomAccess< T >( this );
	}

	@Override
	public LabelRegionRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}