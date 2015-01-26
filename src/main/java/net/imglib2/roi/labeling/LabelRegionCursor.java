package net.imglib2.roi.labeling;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.roi.util.iterationcode.IterationCodeListIterator;
import net.imglib2.type.logic.BoolType;

public class LabelRegionCursor extends AbstractLocalizable implements Cursor< BoolType >
{
	private final BoolType type = new BoolType( true );

	private final IterationCodeListIterator< Point > iter;

	public LabelRegionCursor( final ArrayList< TIntArrayList > itcodesList, final long[] offset )
	{
		super( offset.length );
		iter = new IterationCodeListIterator< Point >( itcodesList, offset, Point.wrap( position ) );
	}

	protected LabelRegionCursor( final LabelRegionCursor c )
	{
		super( c.n );
		iter = new IterationCodeListIterator< Point >( c.iter, Point.wrap( position ) );
	}

	@Override
	public BoolType get()
	{
		return type;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		iter.jumpFwd( steps );
	}

	@Override
	public void fwd()
	{
		iter.fwd();
	}

	@Override
	public void reset()
	{
		iter.reset();
	}

	@Override
	public boolean hasNext()
	{
		return iter.hasNext();
	}

	@Override
	public BoolType next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{
		// NB: no action.
	}

	@Override
	public LabelRegionCursor copy()
	{
		return new LabelRegionCursor( this );
	}

	@Override
	public LabelRegionCursor copyCursor()
	{
		return copy();
	}
}
