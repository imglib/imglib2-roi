package net.imglib2.roi.labeling;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.roi.util.iterationcode.IterationCode;
import net.imglib2.roi.util.iterationcode.IterationCodeIterator;
import net.imglib2.type.logic.BoolType;

public class FragmentCursor extends AbstractLocalizable implements Cursor< BoolType >
{
	private final BoolType type = new BoolType( true );

	private final IterationCodeIterator< Point > iter;

	public FragmentCursor( final IterationCode iterationCode, final long[] offset )
	{
		super( offset.length );
		iter = new IterationCodeIterator< Point >( iterationCode, offset, Point.wrap( position ) );
		reset();
	}

	protected FragmentCursor( final FragmentCursor c )
	{
		super( c.n );
		iter = new IterationCodeIterator< Point >( c.iter, Point.wrap( position ) );
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
	public FragmentCursor copy()
	{
		return new FragmentCursor( this );
	}

	@Override
	public FragmentCursor copyCursor()
	{
		return copy();
	}
}