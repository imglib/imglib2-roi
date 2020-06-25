package net.imglib2.roi.labeling;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class LabelingsTest
{

	@Test
	public void testRemapLabels()
	{
		Integer[] values1 = new Integer[] { 42, 13 };
		Integer[] values2 = new Integer[] { 1 };
		String[] expected1 = new String[] { "prefix42", "prefix13" };
		String[] expected2 = new String[] { "prefix1" };
		// setup
		Img< UnsignedByteType > indexImg = ArrayImgs.unsignedBytes( new byte[] { 1, 0, 2 }, 3 );
		List< Set< Integer > > labelSets = Arrays.asList( asSet(), asSet( values1 ), asSet( values2 ) );
		ImgLabeling< Integer, UnsignedByteType > labeling = ImgLabeling.fromImageAndLabelSets( indexImg, labelSets );
		// process
		ImgLabeling< String, UnsignedByteType > remapped = Labelings.remapLabels( labeling, i -> "prefix" + i );
		// test
		RandomAccess< LabelingType< String > > ra = remapped.randomAccess();
		ra.setPosition( new long[] { 0 } );
		assertEquals( asSet( expected1 ), ra.get() );
		ra.setPosition( new long[] { 1 } );
		assertEquals( asSet(), ra.get() );
		ra.setPosition( new long[] { 2 } );
		assertEquals( asSet( expected2 ), ra.get() );
	}

	@Test( expected = IllegalArgumentException.class )
	public void testRemapLabelsNonInjective()
	{
		Integer[] values1 = new Integer[] { 1 };
		Integer[] values2 = new Integer[] { 2 };
		Integer[] values12 = new Integer[] { 1, 2 };
		// setup
		Img< UnsignedByteType > indexImg = ArrayImgs.unsignedBytes( new byte[] { 1, 0, 3, 2 }, 2, 2 );
		List< Set< Integer > > labelSets = Arrays.asList( asSet(), asSet( values1 ), asSet( values2 ), asSet( values12 ) );
		ImgLabeling< Integer, UnsignedByteType > labeling = ImgLabeling.fromImageAndLabelSets( indexImg, labelSets );
		// process
		Labelings.remapLabels( labeling, i -> "foo" );
	}

	@SuppressWarnings( "unchecked" )
	private < T > Set< T > asSet( T... values )
	{
		return new TreeSet<>( Arrays.asList( values ) );
	}

}
