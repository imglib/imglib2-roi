package net.imglib2.roi.labeling;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.real.DoubleType;

public class OverlappingLabelsTest
{

	@Test
	public void testOverlappingLabels()
	{
		ImgLabeling< Integer, UnsignedByteType > labeling = createTestLabeling();
		OverlappingLabels< Integer > overlap = new OverlappingLabels<>( labeling );

		List< Integer > labels = overlap.getIndexedLabels();
		assertArrayEquals( new Integer[] { 1, 2, 3 }, labels.toArray() );

		RandomAccessibleInterval< UnsignedIntType > matrix = overlap.getMatrix();
		assertEquals( 3, matrix.dimension( 0 ) );
		assertEquals( 3, matrix.dimension( 1 ) );
		RandomAccess< UnsignedIntType > ra = matrix.randomAccess();
		ra.setPosition( new int[] { 0, 0 } );
		assertEquals( 4, ra.get().get() );
		ra.setPosition( new int[] { 1, 0 } );
		assertEquals( 1, ra.get().get() );
		ra.setPosition( new int[] { 2, 0 } );
		assertEquals( 2, ra.get().get() );
		ra.setPosition( new int[] { 0, 1 } );
		assertEquals( 1, ra.get().get() );
		ra.setPosition( new int[] { 1, 1 } );
		assertEquals( 2, ra.get().get() );
		ra.setPosition( new int[] { 2, 1 } );
		assertEquals( 1, ra.get().get() );
		ra.setPosition( new int[] { 0, 2 } );
		assertEquals( 2, ra.get().get() );
		ra.setPosition( new int[] { 1, 2 } );
		assertEquals( 1, ra.get().get() );
		ra.setPosition( new int[] { 2, 2 } );
		assertEquals( 4, ra.get().get() );

		RandomAccessibleInterval< DoubleType > normalizedMatrix = overlap.getNormalizedMatrix();
		RandomAccess< DoubleType > nra = normalizedMatrix.randomAccess();
		nra.setPosition( new int[] { 0, 0 } );
		assertEquals( 1.0, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 1, 0 } );
		assertEquals( 0.5, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 2, 0 } );
		assertEquals( 0.5, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 0, 1 } );
		assertEquals( 0.25, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 1, 1 } );
		assertEquals( 1.0, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 2, 1 } );
		assertEquals( 0.25, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 0, 2 } );
		assertEquals( 0.5, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 1, 2 } );
		assertEquals( 0.5, nra.get().get(), 0.0 );
		nra.setPosition( new int[] { 2, 2 } );
		assertEquals( 1.0, nra.get().get(), 0.0 );

		assertEquals( 2, overlap.getPixelOverlap( 1, 3 ) );
		assertEquals( 2, overlap.getPixelOverlapForIndex( 0, 2 ) );

		assertEquals( 0.50, overlap.getPartialOverlap( 2, 3 ), 0.0 );
		assertEquals( 0.25, overlap.getPartialOverlap( 3, 2 ), 0.0 );
		assertEquals( 0.50, overlap.getPartialOverlapForIndex( 1, 2 ), 0.0 );
		assertEquals( 0.25, overlap.getPartialOverlapForIndex( 2, 1 ), 0.0 );
	}

	private ImgLabeling< Integer, UnsignedByteType > createTestLabeling()
	{
		Img< UnsignedByteType > indexImg = ArrayImgs.unsignedBytes( new byte[] { 0, 1, 1, 2, 5, 4, 0, 3, 3 }, 3, 3 );
		List< Set< Integer > > mapping = Arrays.asList( asSet(), asSet( 1 ), asSet( 2 ), asSet( 3 ), asSet( 1, 3 ), asSet( 1, 2, 3 ) );
		return ImgLabeling.fromImageAndLabelSets( indexImg, mapping );
	}

	private Set< Integer > asSet( Integer... values )
	{
		return new TreeSet<>( Arrays.asList( values ) );
	}
}
