package net.imglib2.roi.labeling;

import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonReader;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.io.BasicOutputBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * A utility class to easily access a load/save functionality for BSON-based labeling data files.
 * Basic support for primitive types and BSON standard types is already included. For non-primitive types,
 * a codec must be set and the class must be given as an argument to the methods.
 * Examples for Codecs can be found at {@Link LabelingMappingCodec} and {@Link LabelingIOTest}.
 *
 * @author Tom Burke
 */
public class LabelingIO {

    CodecRegistry registry = CodecRegistries.fromProviders(new BsonValueCodecProvider(), new DocumentCodecProvider()
            , new ValueCodecProvider());


    /**
     * Saves the {@Link LabelingMapping} of an {@Link ImgLabeling} at the specified path.
     * For the save to work correctly with non-primitive types, a codec must be added to the registry through
     * one of the available methods in this class.
     *
     * @param labelingMapping the mapping of labels contained in {@Link ImgLabeling}
     * @param fileName        the path to the file
     */
    void saveLabeling(LabelingMapping labelingMapping, String fileName) {
        LabelingMappingCodec labelingMappingCodec = new LabelingMappingCodec(registry, null);
        BasicOutputBuffer outputBuffer = new BasicOutputBuffer();
        BsonBinaryWriter writer = new BsonBinaryWriter(outputBuffer);
        labelingMappingCodec.encode(writer, labelingMapping, EncoderContext.builder().isEncodingCollectibleDocument(false).build());
        byte[] byteArr = outputBuffer.toByteArray();
        writeToFile(byteArr, new File(getBsonFilePath(fileName)));
    }

    /**
     * Loads the {@Link LabelingMapping} of an {@Link ImgLabeling} from the specified path.
     * The Class-argument is only necessary if the labeling type is non-primitive.
     * For the load to work correctly with non-primitive types, a codec must be added to the registry through
     * one of the available methods in this class.
     *
     * @param fileName the path to the file
     * @param clazz    can be null if labeling is a primitive type
     * @return the Labeling in the file
     * @throws IOException
     */
    LabelingMapping loadLabeling(String fileName, Class clazz) throws IOException {

        LabelingMappingCodec labelingMappingCodec = new LabelingMappingCodec(registry, clazz);

        RandomAccessFile aFile = new RandomAccessFile(getBsonFilePath(fileName), "r");
        FileChannel inChannel = aFile.getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        BsonReader bsonReader = new BsonBinaryReader(buffer);

        LabelingMapping labelingMapping = labelingMappingCodec.decode(bsonReader, DecoderContext.builder().build());

        return labelingMapping;
    }

    private void writeToFile(byte[] byteArr, File file) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(byteArr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getBsonFilePath(final String filename) {
        if (filename.endsWith(".bson")) {
            return filename;
        }
        final int index = filename.lastIndexOf(".");
        return filename.substring(0, index == -1 ? filename.length() : index).concat(".bson");
    }

    public CodecRegistry getRegistry() {
        return registry;
    }

    /**
     * Overwrites the complete CodecRegistry.
     *
     * @param registry
     */
    public void setRegistry(CodecRegistry registry) {
        this.registry = registry;
    }

    /**
     * Adds the codecs contained in one or more {@Link CodecRegistry} to the current registry.
     *
     * @param registries
     */
    public void addCodecRegistries(CodecRegistry... registries) {
        this.registry = CodecRegistries.fromRegistries(getRegistry(), CodecRegistries.fromRegistries(registries));
    }

    /**
     * Adds the codecs contained in one or more {@Link CodecProvider} to the current registry.
     *
     * @param providers
     */
    public void addCodecProviders(CodecProvider... providers) {
        this.registry = CodecRegistries.fromRegistries(getRegistry(), CodecRegistries.fromProviders(providers));
    }

    /**
     * Adds one or more {@Link Codec} to the current registry.
     *
     * @param codecs
     */
    public void addCodecs(Codec... codecs) {
        this.registry = CodecRegistries.fromRegistries(getRegistry(), CodecRegistries.fromCodecs(codecs));
    }
}

