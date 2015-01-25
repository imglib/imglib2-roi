package net.imglib2.roi;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.type.BooleanType;

public interface PositionableIterableRegion< T extends BooleanType< T > > extends IterableRegion< T >, Localizable, Positionable
{}
