package net.imglib2.roi.sparse;

class NodeData
{
	// The value, represented only counts if data and children are zero
	private boolean value;

	private LeafBitmask data;

	private NodeData parent;

	private NodeData[] children;

	NodeData( final NodeData parent, final boolean value )
	{
		this.parent = parent;
		this.value = value;
	}

	boolean hasChildren()
	{
		return children != null;
	}

	public void createChildren( final int numChildren ) {
		NodeData[] children = new NodeData[ numChildren ];
		for ( int i = 0; i < numChildren; ++i )
			children[ i ] = new NodeData( this, value );
		// NB: One atomic operation, that doesn't change the Bitmask represented.
		this.children = children;
	}

	public void createBitmask( LeafBitmask.Specification bitmaskSpecification )
	{
		// NB: One atomic operation, that doesn't change the Bitmask represented.
		data = new LeafBitmask( bitmaskSpecification, value );
	}

	public NodeData newParent( final int childindex, final int numChildren )
	{
		NodeData newRoot = new NodeData( null, false );
		newRoot.children = new NodeData[ numChildren ];
		for ( int i = 0; i < numChildren; ++i )
			newRoot.children[ i ] = ( i == childindex )
					? this : new NodeData( newRoot, false );
		this.parent = newRoot;
		return newRoot;
	}

	public boolean merge( final boolean value )
	{
		for ( NodeData child : children )
		{
			if ( child.hasChildren() || child.data != null || child.value != value )
				return false;
		}
		// NB: Two operations, that doesn't change the Bitmask represented.
		this.value = value;
		this.children = null;
		return true;
	}

	public void mergeLeafToValue( boolean value )
	{
		this.value = value;
		// NB: One atomic change, that set's all pixels represented by the lead two value
		this.data = null;
	}

	public boolean value()
	{
		return value;
	}

	public LeafBitmask data()
	{
		return data;
	}

	public NodeData parent()
	{
		return parent;
	}

	public NodeData child( int i )
	{
		return children[ i ];
	}
}
