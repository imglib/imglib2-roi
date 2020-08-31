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

public class LabelingIO {

    CodecRegistry registry = CodecRegistries.fromProviders(new BsonValueCodecProvider(), new DocumentCodecProvider()
            , new ValueCodecProvider());


    void saveLabeling(LabelingMapping labelingMapping, String fileName, Class clazz) {
        LabelingMappingCodec labelingMappingCodec = new LabelingMappingCodec(registry, clazz);
        BasicOutputBuffer outputBuffer = new BasicOutputBuffer();
        BsonBinaryWriter writer = new BsonBinaryWriter(outputBuffer);
        labelingMappingCodec.encode(writer, labelingMapping, EncoderContext.builder().isEncodingCollectibleDocument(false).build());
        byte[] byteArr = outputBuffer.toByteArray();
        writeToFile(byteArr, new File(fileName + ".bson"));
    }

    LabelingMapping loadLabeling(String fileName, Class clazz) throws IOException {

        LabelingMappingCodec labelingMappingCodec = new LabelingMappingCodec(registry, clazz);

        RandomAccessFile aFile = new RandomAccessFile(fileName + ".bson", "r");
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

    public CodecRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(CodecRegistry registry) {
        this.registry = registry;
    }

    public void addCodecRegistries(CodecRegistry... registries) {
        this.registry = CodecRegistries.fromRegistries(getRegistry(),CodecRegistries.fromRegistries(registries));
    }

    public void addCodecProviders(CodecProvider... providers) {
        this.registry = CodecRegistries.fromRegistries(getRegistry(),CodecRegistries.fromProviders(providers));
    }

    public void addCodecs(Codec... codecs) {
        this.registry = CodecRegistries.fromRegistries(getRegistry(),CodecRegistries.fromCodecs(codecs));
    }
}

