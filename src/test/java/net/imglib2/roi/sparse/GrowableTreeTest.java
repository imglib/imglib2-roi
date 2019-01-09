package net.imglib2.roi.sparse;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GrowableTreeTest
{
	@Test
	public void testCreate() {
		GrowableTree tree = new GrowableTree( new int[] { 8, 8, 8 } );
		assertEquals(0,  tree.height());
	}
}
