package net.imglib2.roi.labeling;

import java.util.Set;

import net.imglib2.type.Type;

public interface GenericLabelingType< G extends GenericLabelingType< G, T >, T > extends Type< G >, Set< T >
{

}
