package net.imglib2.roi.labeling;

import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LabelingMappingCodec implements Codec<LabelingMapping> {


    private Class clazz;

    private CodecRegistry codecRegistry;

    public LabelingMappingCodec(CodecRegistry codecRegistry, Class clazz){
        this.codecRegistry = codecRegistry;
        this.clazz = clazz;
    }



    @Override
    public LabelingMapping decode(BsonReader reader, DecoderContext decoderContext) {
        LabelingMapping labelingMapping = new LabelingMapping(new IntType());
        reader.readStartDocument();
        int numSets = reader.readInt32("numSets");

        //TODO: add mapping of complex types
        Map<Integer,Object> mapping = readMapping(reader, decoderContext, clazz);
        List<Set> labelSets = Collections.EMPTY_LIST;
        if(mapping.isEmpty()){
            labelSets = readLabelSets(reader, decoderContext, numSets);
        }else{
            labelSets = readLabelSets(reader, decoderContext, numSets, mapping);
        }



        labelingMapping.setLabelSets(labelSets);
        reader.readEndDocument();
        return labelingMapping;
    }

    private List<Set> readLabelSets(BsonReader reader, DecoderContext decoderContext, int numSets, Map<Integer, Object> mapping) {
        List<Set> labelSets = new ArrayList<>();
        reader.readStartDocument();
        for (int i = 0; i < numSets; i++) {
            Set<Object> labelSet = new HashSet<>();
            reader.readStartArray();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                labelSet.add(mapping.get(reader.readInt32()));
            }
            reader.readEndArray();
            labelSets.add(labelSet);

        }
        reader.readEndDocument();
        return labelSets;
    }


    @Override
    public void encode(BsonWriter writer, LabelingMapping value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeInt32("numSets", value.numSets());


        Optional first = value.getLabels().stream().findFirst();
        if(first.isPresent() && !isWrapperType(first.get().getClass())){
            System.out.println("Complex Type");
            AtomicInteger count = new AtomicInteger();
            HashMap<Object, Integer> map = new HashMap();
            writer.writeStartDocument("labelMapping");
            value.getLabels().stream().forEach(v->{
                map.put(v, count.get());
                writer.writeName(String.valueOf(count.getAndIncrement()));
                Codec codec = codecRegistry.get(v.getClass());
                encoderContext.encodeWithChildContext(codec, writer,v);
                //writer.writeBinaryData(i.getAndIncrement(), new BsonBinary());

            });
            writer.writeEndDocument();
            writer.writeStartDocument("labelSets");
            for (int i = 0; i < value.numSets(); i++) {
                Set labelSet = value.labelsAtIndex(i);
                writer.writeStartArray("labelSet_" + i);
                labelSet.stream().forEach(v -> writeValue(map.get(v), writer, encoderContext));
                writer.writeEndArray();
            }
            writer.writeEndDocument();

        }else{
            System.out.println("Primitive Type");
            writer.writeStartDocument("labelMapping");
            writer.writeEndDocument();
            writer.writeStartDocument("labelSets");
            for (int i = 0; i < value.numSets(); i++) {
                Set labelSet = value.labelsAtIndex(i);
                writer.writeStartArray("labelSet_" + i);
                labelSet.stream().forEach(v -> writeValue(v, writer, encoderContext));
                writer.writeEndArray();
            }
            writer.writeEndDocument();
        }




        writer.writeEndDocument();
    }

    private Map<Integer,Object> readMapping(BsonReader reader, DecoderContext decoderContext, Class clazz) {
        Map<Integer, Object> mapping = new HashMap<>();
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            int key = Integer.parseInt(reader.readName());
            Codec c = codecRegistry.get(clazz);
            Object value = c.decode(reader, decoderContext);
            mapping.put(key,value);
        }

        reader.readEndDocument();
        return mapping;
    }

    private List<Set> readLabelSets(BsonReader reader, DecoderContext decoderContext, int numSets) {
        List<Set> labelSets = new ArrayList<>();
        reader.readStartDocument();
        for (int i = 0; i < numSets; i++) {

            Set<Object> labelSet = new HashSet<>();
            reader.readStartArray();

            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                switch (reader.getCurrentBsonType()) {
                    case INT32:
                        int intValue = reader.readInt32();
                        labelSet.add(intValue);
                        break;
                    case INT64:
                        long longValue = reader.readInt64();
                        labelSet.add(longValue);
                        break;
                    case BOOLEAN:
                        boolean booleanValue = reader.readBoolean();
                        labelSet.add(booleanValue);
                        break;
                    case STRING:
                        String stringValue = reader.readString();
                        labelSet.add(stringValue);
                        break;
                    case DOCUMENT:
                        labelSet.add(codecRegistry.get(null).decode(reader, decoderContext));
                        break;
                    default:
                        System.out.println("Type currently not supported. " + reader.getCurrentBsonType());
                }
            }

            reader.readEndArray();
            labelSets.add(labelSet);
        }
        reader.readEndDocument();
        return labelSets;
    }


    //TODO: remove non primitives as soon as mapping code is established
    private void writeValue(Object v, BsonWriter writer, EncoderContext encoderContext) {
        if (v instanceof IntType) {
            writer.writeInt32(((IntType) v).get());
        } else if (v instanceof LongType) {
            writer.writeInt64(((LongType) v).get());
        } else if (v instanceof BoolType) {
            writer.writeBoolean(((BoolType) v).get());
        } else if (v instanceof Integer) {
            writer.writeInt32(((Integer) v).intValue());
        } else if (v instanceof Long) {
            writer.writeInt64(((Long) v).longValue());
        } else if (v instanceof Boolean) {
            writer.writeBoolean(((Boolean) v).booleanValue());
        } else if (v instanceof String) {
            writer.writeString(((String) v).intern());
        } else {
            Codec codec = codecRegistry.get(v.getClass());
            if(codec != null){
                encoderContext.encodeWithChildContext(codec, writer, v);
            }else{
                System.out.println("Type not supported. Type: " + v.getClass().getSimpleName());
            }
        }

    }

    @Override
    public Class<LabelingMapping> getEncoderClass() {
        return LabelingMapping.class;
    }

    public void setCodecRegistry(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    private static final Set<Class> WRAPPER_TYPES = new HashSet(Arrays.asList(
            Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class));
    public static boolean isWrapperType(Class clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }
}

/* data structure
{
    numSets
    labelSets{...}
}
 */
