package net.imglib2.roi.labeling;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.junit.Assert;
import org.junit.Test;

public class LabelingIOTest
{

	@Test
	public void saveLabelingPrimitiveTest() throws IOException
	{
		ImgLabeling< Integer, UnsignedByteType > labeling = getSimpleImgLabeling();

		new LabelingIO().saveLabeling( labeling, File.createTempFile( "labelSaveTestSimple", ".tif" ).getAbsolutePath() );

	}

	@Test
	public void loadLabelingPrimitiveTest() throws IOException
	{
		LabelingMapping< Integer > mapping = new LabelingIO().loadLabeling( "src/test/resources/labeling/labelSaveTestSimple.bson", Integer.class );

		Assert.assertEquals( getSimpleImgLabeling().getMapping().getLabels(), mapping.getLabels() );
	}

	@Test
	public void saveLabelingComplexWithCodecTest() throws IOException
	{
		ImgLabeling< Example, UnsignedByteType > labeling = getComplexImgLabeling();

		LabelingIO io = new LabelingIO();
		io.addCodecs( new ExampleCodec() );
		io.saveLabeling( labeling, File.createTempFile( "labelSaveTestComplex", ".tif" ).getAbsolutePath(), Example.class );
	}

	@Test
	public void loadLabelingComplexWithCodecTest() throws IOException
	{
		LabelingIO io = new LabelingIO();
		io.addCodecs( new ExampleCodec() );

		LabelingMapping< Example > mapping = io.loadLabeling( "src/test/resources/labeling/labelSaveTestComplex.bson", Example.class );

		Assert.assertEquals( getComplexImgLabeling().getMapping().getLabels(), mapping.getLabels() );
	}

	@Test
	public void saveLabelingComplexWithFunctionTest() throws IOException
	{
		ImgLabeling< Example, UnsignedByteType > labeling = getComplexImgLabeling();

		LabelingIO io = new LabelingIO();
		Map< Example, Long > mapping = new HashMap<>();
		AtomicLong atomicLong = new AtomicLong( 0 );
		labeling.getMapping().getLabels().forEach( label -> mapping.put( label, atomicLong.getAndIncrement() ) );
		io.saveLabeling( labeling, File.createTempFile( "labelSaveTestComplexFunction", ".tif" ).getAbsolutePath(), mapping::get );
	}

	@Test
	public void loadLabelingComplexWithFunctionTest() throws IOException
	{
		Set< Example > labels = getComplexImgLabeling().getMapping().getLabels();
		LabelingIO io = new LabelingIO();
		Map< Long, Example > map = new HashMap<>();
		AtomicLong atomicLong = new AtomicLong( 0 );
		labels.forEach( label -> map.put( atomicLong.getAndIncrement(), label ) );

		ImgLabeling< Example, UnsignedByteType > imgLabeling = io.loadLabeling( "src/test/resources/labeling/labelSaveTestComplexFunction.bson", map::get );

		Assert.assertEquals( labels, imgLabeling.getMapping().getLabels() );
	}

	@Test
	public void encoderClassTest()
	{
		LabelingIO io = new LabelingIO();
		io.addCodecs( new ExampleCodec() );
		LabelingMappingCodec< Integer > labelingMappingCodec = new LabelingMappingCodec.Builder< Integer >().build();
		Class c = labelingMappingCodec.getEncoderClass();
		Assert.assertEquals( LabelingMapping.class, c );
	}

	private ImgLabeling< Integer, UnsignedByteType > getSimpleImgLabeling()
	{
		Integer[] values1 = new Integer[] { 42, 13 };
		Integer[] values2 = new Integer[] { 1 };
		Integer[] values3 = new Integer[] { 1, 13, 42 };
		// setup
		Img< UnsignedByteType > indexImg = ArrayImgs.unsignedBytes( new byte[] { 1, 0, 2 }, 3 );
		List< Set< Integer > > labelSets = Arrays.asList( asSet(), asSet( values1 ), asSet( values2 ), asSet( values3 ) );
		return ImgLabeling.fromImageAndLabelSets( indexImg, labelSets );
	}

	private ImgLabeling< Example, UnsignedByteType > getComplexImgLabeling()
	{
		Example[] values1 = new Example[] { new Example( "a", 1.0, 1 ), new Example( "b", 2.24121, 2 ) };
		Example[] values2 = new Example[] { new Example( "a", 1.0, 1 ) };
		Example[] values3 = new Example[] { new Example( "b", 2.24121, 2 ), new Example( "a", 1.0, 1 ), new Example( "a", 1.0, 3 ) };
		// setup
		Img< UnsignedByteType > indexImg = ArrayImgs.unsignedBytes( new byte[] { 1, 0, 2 }, 3 );
		List< Set< Example > > labelSets = Arrays.asList( asSet(), asSet( values1 ), asSet( values2 ), asSet( values3 ) );
		return ImgLabeling.fromImageAndLabelSets( indexImg, labelSets );
	}

	private static class ExampleCodec implements Codec< Example >
	{

		@Override
		public Example decode( BsonReader reader, DecoderContext decoderContext )
		{
			reader.readStartDocument();
			String a = reader.readString( "a" );
			double b = reader.readDouble( "b" );
			int c = reader.readInt32( "c" );
			reader.readEndDocument();
			return new Example( a, b, c );
		}

		@Override
		public void encode( BsonWriter writer, Example value, EncoderContext encoderContext )
		{
			writer.writeStartDocument();
			writer.writeString( "a", value.a );
			writer.writeDouble( "b", value.b );
			writer.writeInt32( "c", value.c );
			writer.writeEndDocument();
		}

		@Override
		public Class< Example > getEncoderClass()
		{
			return Example.class;
		}
	}

	private static class Example implements Comparable< Example >
	{

		private String a;

		private double b;

		private int c;

		public Example( String a, double b, int c )
		{
			this.a = a;
			this.b = b;
			this.c = c;
		}

		@Override
		public boolean equals( Object o )
		{
			if ( this == o )
				return true;
			if ( o == null || getClass() != o.getClass() )
				return false;
			Example example = ( Example ) o;
			return Double.compare( example.b, b ) == 0 &&
					c == example.c &&
					Objects.equals( a, example.a );
		}

		@Override
		public int hashCode()
		{
			return Objects.hash( a, b, c );
		}

		@Override
		public int compareTo( Example o )
		{
			return this.equals( o ) ? 0 : 1;
		}
	}

	@SuppressWarnings( "unchecked" )
	private < T > Set< T > asSet( T... values )
	{
		return new TreeSet<>( Arrays.asList( values ) );
	}

}
