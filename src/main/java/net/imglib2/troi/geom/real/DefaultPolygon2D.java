/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.troi.geom.real;

import java.util.List;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.troi.geom.GeomMaths;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * A {@link DefaultPolygon2D} defined by the given vertices. The resulting
 * polygon may contain some edge points, all edge points, or no edge points
 * depending on the specified shape.
 *
 * <p>
 * If consistent edge point inclusion/exclusion is needed see
 * {@link OpenPolygon2D} or {@link ClosedPolygon2D}. These implementations will
 * be less efficient but have consistent edge behavior.
 * </p>
 *
 * @author Tobias Pietzsch
 * @author Daniel Seebacher, University of Konstanz
 * @author Christian Dietz, University of Konstanz
 */
public class DefaultPolygon2D implements Polygon2D< RealPoint >
{
	protected final TDoubleArrayList x;

	protected final TDoubleArrayList y;

	/**
	 * Creates a 2D polygon with the provided vertices.
	 *
	 * @param vertices
	 *            List of vertices which will be copied. Each vertex should have
	 *            a position in 2D space, positions beyond 2D will be ignored.
	 */
	public DefaultPolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		x = new TDoubleArrayList( vertices.size() );
		y = new TDoubleArrayList( vertices.size() );
		populateXY( vertices );
	}

	/**
	 * Creates a 2D polygon with vertices at the provided x, y coordinates. If
	 * the x and y arrays have unequal lengths, the longer array will be
	 * truncated.
	 *
	 * @param x
	 *            X coordinates of the vertices which will be copied
	 * @param y
	 *            Y coordinates of the vertices which will be copied
	 */
	public DefaultPolygon2D( final double[] x, final double[] y )
	{
		if ( x.length == y.length )
		{
			this.x = new TDoubleArrayList( x );
			this.y = new TDoubleArrayList( y );
		}
		else
		{
			final int l = x.length < y.length ? x.length : y.length;
			this.x = new TDoubleArrayList( l );
			this.x.add( x, 0, l );
			this.y = new TDoubleArrayList( l );
			this.y.add( y, 0, l );
		}
	}

	/**
	 * Return true if the given point is contained inside the boundary. See:
	 * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
	 */
	@Override
	public boolean test( final RealLocalizable localizable )
	{
		return GeomMaths.pnpoly( x, y, localizable );
	}

	/** Return a copy of the vertex */
	@Override
	public RealPoint vertex( final int pos )
	{
		return new Polygon2DVertex( pos );
	}

	@Override
	public int numVertices()
	{
		return x.size();
	}

	/**
	 * If the given vertex has more than 2 dimensions, the higher dimensions
	 * will be ignored.
	 */
	@Override
	public void addVertex( final int index, final double[] vertex )
	{
		x.insert( index, vertex[ 0 ] );
		y.insert( index, vertex[ 1 ] );
	}

	@Override
	public void removeVertex( final int index )
	{
		x.removeAt( index );
		y.removeAt( index );
	}

	// -- Helper methods --

	/**
	 * Populates the x and y arrays, and sets min/max values.
	 *
	 * @param vertices
	 *            Contains the vertices, dimensions beyond two will be ignored.
	 */
	private void populateXY( final List< ? extends RealLocalizable > vertices )
	{
		for ( int i = 0; i < vertices.size(); i++ )
		{
			final double xi = vertices.get( i ).getDoublePosition( 0 );
			final double yi = vertices.get( i ).getDoublePosition( 1 );
			x.add( xi );
			y.add( yi );
		}
	}

	// -- Helper classes --

	private class Polygon2DVertex extends RealPoint
	{

		private final int pos;

		protected Polygon2DVertex( final int pos )
		{
			super( new double[] { x.get( pos ), y.get( pos ) } );
			this.pos = pos;
		}

		@Override
		public void fwd( final int d )
		{
			super.fwd( d );
			updateXY();
		}

		@Override
		public void bck( final int d )
		{
			super.bck( d );
			updateXY();
		}

		@Override
		public void move( final int distance, final int d )
		{
			super.move( distance, d );
			updateXY();
		}

		@Override
		public void move( final long distance, final int d )
		{
			super.move( distance, d );
			updateXY();
		}

		@Override
		public void move( final Localizable localizable )
		{
			super.move( localizable );
			updateXY();
		}

		@Override
		public void move( final int[] distance )
		{
			super.move( distance );
			updateXY();
		}

		@Override
		public void move( final long[] distance )
		{
			super.move( distance );
			updateXY();
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			super.setPosition( localizable );
			updateXY();
		}

		@Override
		public void setPosition( final int[] position )
		{
			super.setPosition( position );
			updateXY();
		}

		@Override
		public void setPosition( final long[] position )
		{
			super.setPosition( position );
			updateXY();
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			super.setPosition( position, d );
			updateXY();
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			super.setPosition( position, d );
			updateXY();
		}

		@Override
		public void move( final float distance, final int d )
		{
			super.move( distance, d );
			updateXY();
		}

		@Override
		public void move( final double distance, final int d )
		{
			super.move( distance, d );
			updateXY();
		}

		@Override
		public void move( final RealLocalizable distance )
		{
			super.move( distance );
			updateXY();
		}

		@Override
		public void move( final float[] distance )
		{
			super.move( distance );
			updateXY();
		}

		@Override
		public void move( final double[] distance )
		{
			super.move( distance );
			updateXY();
		}

		@Override
		public void setPosition( final RealLocalizable position )
		{
			super.setPosition( position );
			updateXY();
		}

		@Override
		public void setPosition( final float[] position )
		{
			super.setPosition( position );
			updateXY();
		}

		@Override
		public void setPosition( final double[] position )
		{
			super.setPosition( position );
			updateXY();
		}

		@Override
		public void setPosition( final float position, final int d )
		{
			super.setPosition( position, d );
			updateXY();
		}

		@Override
		public void setPosition( final double position, final int d )
		{
			super.setPosition( position, d );
			updateXY();
		}

		// -- Helper methods --

		private void updateXY()
		{
			x.set( pos, position[ 0 ] );
			y.set( pos, position[ 1 ] );
		}
	}
}
