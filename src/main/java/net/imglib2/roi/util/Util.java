package net.imglib2.roi.util;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.labeling.LabelingMapping;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.BooleanType;
import net.imglib2.view.Views;

public class Util
{
	public static < T extends BooleanType< T > > long countTrue( final RandomAccessibleInterval< T > interval )
	{
		long sum = 0;
		for ( final T t : Views.iterable( interval ) )
			if ( t.get() )
				++sum;
		return sum;
	}

	public static < T > LabelingMapping< T > getLabelingMapping( final RandomAccessibleInterval< LabelingType< T > > labeling )
	{
		return Views.iterable( labeling ).firstElement().getMapping();
	}
}
