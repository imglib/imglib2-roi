package net.imglib2.roi.labeling;

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

import java.io.IOException;
import java.util.*;

public class LabelingIOTest {


    @Test
    public void saveLabelingPrimitiveTest(){
        Integer[] values1 = new Integer[] { 42, 13 };
        Integer[] values2 = new Integer[] { 1 };
        Integer[] values3 = new Integer[] { 1,13,42 };
        String[] expected1 = new String[] { "prefix42", "prefix13" };
        String[] expected2 = new String[] { "prefix1" };
        // setup
        Img<UnsignedByteType> indexImg = ArrayImgs.unsignedBytes( new byte[] { 1, 0, 2 }, 3 );
        List<Set< Integer >> labelSets = Arrays.asList( asSet(), asSet( values1 ), asSet( values2 ), asSet(values3) );
        ImgLabeling< Integer, UnsignedByteType > labeling = ImgLabeling.fromImageAndLabelSets( indexImg, labelSets );


        new LabelingIO().saveLabeling(labeling.getMapping(), "C:/Users/TomB/Desktop/labeling/labelSaveTest");
    }

    @Test
    public void loadLabelingPrimitiveTest() throws IOException {
        LabelingMapping mapping = new LabelingIO().loadLabeling( "C:/Users/TomB/Desktop/labeling/labelSaveTest", Example.class);
        Assert.assertTrue(mapping.getLabels().size()>0);
    }

    @Test
    public void saveLabelingComplexTest(){
        Example[] values1 = new Example[] { new Example("a", 1.0, 1), new Example("b", 2.24121, 2) };
        Example[] values2 = new Example[] { new Example("a", 1.0, 1) };
        Example[] values3 = new Example[] { new Example("b", 2.24121, 2),new Example("a", 1.0, 1),new Example("a", 1.0, 3) };
        String[] expected1 = new String[] { "prefix42", "prefix13" };
        String[] expected2 = new String[] { "prefix1" };
        // setup
        Img<UnsignedByteType> indexImg = ArrayImgs.unsignedBytes( new byte[] { 1, 0, 2 }, 3 );
        List<Set< Example >> labelSets = Arrays.asList( asSet(), asSet( values1 ), asSet( values2 ), asSet(values3) );
        ImgLabeling< Example, UnsignedByteType > labeling = ImgLabeling.fromImageAndLabelSets( indexImg, labelSets );

        LabelingIO io = new LabelingIO();
        io.addCodecs(new ExampleCodec());
        io.saveLabeling(labeling.getMapping(), "C:/Users/TomB/Desktop/labeling/labelSaveTestComplex");
    }

    @Test
    public void loadLabelingComplexTest() throws IOException {
        LabelingIO io = new LabelingIO();
        io.addCodecs(new ExampleCodec());

        LabelingMapping mapping = io.loadLabeling( "C:/Users/TomB/Desktop/labeling/labelSaveTestComplex", Example.class);
        Assert.assertTrue(mapping.getLabels().size()>0);
    }

    private class ExampleCodec implements Codec<Example>{

        @Override
        public Example decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            String a = reader.readString("a");
            double b = reader.readDouble("b");
            int c = reader.readInt32("c");
            reader.readEndDocument();
            return new Example(a,b,c);
        }

        @Override
        public void encode(BsonWriter writer, Example value, EncoderContext encoderContext) {
            writer.writeStartDocument();
            writer.writeString("a", value.a);
            writer.writeDouble("b", value.b);
            writer.writeInt32("c", value.c);
            writer.writeEndDocument();
        }

        @Override
        public Class<Example> getEncoderClass() {
            return Example.class;
        }
    }

    private class Example implements Comparable<Example>{

       private String a;
       private double b;
       private int c;

        public Example(String a, double b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Example example = (Example) o;
            return Double.compare(example.b, b) == 0 &&
                    c == example.c &&
                    Objects.equals(a, example.a);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b, c);
        }

        @Override
        public int compareTo(Example o) {
            return this.equals(o) ? 0: 1;
        }
    }

    @SuppressWarnings( "unchecked" )
    private < T > Set< T > asSet( T... values )
    {
        return new TreeSet<>( Arrays.asList( values ) );
    }


}
