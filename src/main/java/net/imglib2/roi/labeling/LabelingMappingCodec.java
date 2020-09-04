package net.imglib2.roi.labeling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongFunction;
import java.util.function.ToLongFunction;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * A codec (encoder/decoder) for the LabelingMapping class to and from the BSON (binary JSON) data type.
 * The resulting data structure consists of the number of sets, a mapping from complex type to integer
 * as well as the actual label sets. The Codec class is used in the {@link LabelingIO} class and handles
 * the basic structure. For non-primitive label types, an additional codec must be written.
 * V1 Data structure:
 * // @formatter:off
 * {
 *     version: int32
 *     numSets: int32
 *     indexImg: String
 *     mapping: { //may be empty
 *         "1": {//encoded type}
 *         ...
 *     }
 *     labelSets:{
 *         labelSet_n: [...]
 *         ...
 *     }
 * }
 * // @formatter:on
 * @author Tom Burke
 */
public class LabelingMappingCodec< T > implements Codec< LabelingMapping< T > >
{
	private final static int VERSION = 1;

	private Class clazz;

	private CodecRegistry codecRegistry;

	private String indexImg;

	private LongFunction< T > idToLabel;

	private ToLongFunction< T > labelToId;

	private LabelingMappingCodec( final Builder<T> builder )
	{
		this.clazz = builder.clazz;
		this.codecRegistry = builder.codecRegistry;
		this.indexImg = builder.indexImg;
		this.idToLabel = builder.idToLabel;
		this.labelToId = builder.labelToId;

	}

	@Override
	public LabelingMapping< T > decode( BsonReader reader, DecoderContext decoderContext )
	{
		LabelingMapping< T > labelingMapping = new LabelingMapping< T >( new IntType() );
		reader.readStartDocument();
		int version = reader.readInt32( "version" );
		int numSets = reader.readInt32( "numSets" );
		this.indexImg = reader.readString( "indexImg" );

		Map< Integer, T > mapping = readMapping( reader, decoderContext, clazz );
		List< Set< T > > labelSets;
		if ( mapping.isEmpty() )
		{
			labelSets = readLabelSets( reader, decoderContext, numSets );
		}
		else
		{
			labelSets = readLabelSets( reader, decoderContext, numSets, mapping );
		}

		labelingMapping.setLabelSets( labelSets );
		reader.readEndDocument();
		return labelingMapping;
	}

	private List< Set< T > > readLabelSets( BsonReader reader, DecoderContext decoderContext, int numSets, Map< Integer, T > mapping )
	{
		List< Set <T> > labelSets = new ArrayList<>();
		reader.readStartDocument();
		for ( int i = 0; i < numSets; i++ )
		{
			Set< T > labelSet = new HashSet<>();
			reader.readStartArray();
			while ( reader.readBsonType() != BsonType.END_OF_DOCUMENT )
			{
				labelSet.add( mapping.get( reader.readInt32() ) );
			}
			reader.readEndArray();
			labelSets.add( labelSet );

		}
		reader.readEndDocument();
		return labelSets;
	}

	@Override
	public void encode( BsonWriter writer, LabelingMapping< T > value, EncoderContext encoderContext )
	{
		writer.writeStartDocument();
		writer.writeInt32( "version", VERSION );
		writer.writeInt32( "numSets", value.numSets() );
		writer.writeString( "indexImg", indexImg );
		Optional<T> first = value.getLabels().stream().findFirst();
		if ( first.isPresent() && !isWrapperType( first.get().getClass() ) )
		{
			if ( clazz == null )
			{
				writer.writeStartDocument( "labelMapping" );
				writer.writeEndDocument();
				writer.writeStartDocument( "labelSets" );
				for ( int i = 0; i < value.numSets(); i++ )
				{
					Set< T > labelSet = value.labelsAtIndex( i );
					writer.writeStartArray( "labelSet_" + i );
					labelSet.forEach( v -> writeValue( labelToId.applyAsLong( v ), writer, encoderContext ) );
					writer.writeEndArray();
				}
				writer.writeEndDocument();
			}
			else
			{
				AtomicInteger count = new AtomicInteger();
				HashMap< T, Integer > map = new HashMap<>();
				writer.writeStartDocument( "labelMapping" );
				value.getLabels().forEach( v -> {
					map.put( v, count.get() );
					writer.writeName( String.valueOf( count.getAndIncrement() ) );
					Codec<T> codec = ( Codec< T > ) codecRegistry.get( v.getClass() );
					encoderContext.encodeWithChildContext( codec, writer, v );
				} );
				writer.writeEndDocument();
				writer.writeStartDocument( "labelSets" );
				for ( int i = 0; i < value.numSets(); i++ )
				{
					Set<T> labelSet = value.labelsAtIndex( i );
					writer.writeStartArray( "labelSet_" + i );
					labelSet.forEach( v -> writeValue( map.get( v ), writer, encoderContext ) );
					writer.writeEndArray();
				}
				writer.writeEndDocument();
			}
		}
		else
		{
			writer.writeStartDocument( "labelMapping" );
			writer.writeEndDocument();
			writer.writeStartDocument( "labelSets" );
			for ( int i = 0; i < value.numSets(); i++ )
			{
				Set<T> labelSet = value.labelsAtIndex( i );
				writer.writeStartArray( "labelSet_" + i );
				labelSet.forEach( v -> writeValue( v, writer, encoderContext ) );
				writer.writeEndArray();
			}
			writer.writeEndDocument();
		}

		writer.writeEndDocument();
	}

	private Map< Integer, T > readMapping( BsonReader reader, DecoderContext decoderContext, Class clazz )
	{
		Map< Integer, T > mapping = new HashMap<>();
		reader.readStartDocument();
		while ( reader.readBsonType() != BsonType.END_OF_DOCUMENT )
		{
			int key = Integer.parseInt( reader.readName() );
			Codec<T> c = codecRegistry.get( clazz );
			T value = c.decode( reader, decoderContext );
			mapping.put( key, value );
		}

		reader.readEndDocument();
		return mapping;
	}

	private List< Set< T > > readLabelSets( BsonReader reader, DecoderContext decoderContext, int numSets )
	{
		List< Set< T > > labelSets = new ArrayList<>();
		reader.readStartDocument();
		for ( int i = 0; i < numSets; i++ )
		{

			Set< T > labelSet = new HashSet<>();
			reader.readStartArray();

			while ( reader.readBsonType() != BsonType.END_OF_DOCUMENT )
			{
				if ( idToLabel != null )
				{
					labelSet.add( idToLabel.apply( reader.readInt64() ) );
				}
				else
				{
					switch ( reader.getCurrentBsonType() )
					{
					case INT32:
						Integer intValue = reader.readInt32();
						labelSet.add( ( T ) intValue );
						break;
					case INT64:
						Long longValue = reader.readInt64();
						labelSet.add( ( T ) longValue );
						break;
					case BOOLEAN:
						Boolean booleanValue = reader.readBoolean();
						labelSet.add( ( T ) booleanValue );
						break;
					case STRING:
						String stringValue = reader.readString();
						labelSet.add( ( T ) stringValue );
						break;
					case DOCUMENT:
						labelSet.add( ( T ) codecRegistry.get( clazz ).decode( reader, decoderContext ) );
						break;
					default:
						System.out.println( "Type currently not supported. " + reader.getCurrentBsonType() );
					}
				}
			}

			reader.readEndArray();
			labelSets.add( labelSet );
		}
		reader.readEndDocument();
		return labelSets;
	}

	private void writeValue( Object v, BsonWriter writer, EncoderContext encoderContext )
	{
		if ( v instanceof IntType )
		{
			writer.writeInt32( ( ( IntType ) v ).get() );
		}
		else if ( v instanceof LongType )
		{
			writer.writeInt64( ( ( LongType ) v ).get() );
		}
		else if ( v instanceof BoolType )
		{
			writer.writeBoolean( ( ( BoolType ) v ).get() );
		}
		else if ( v instanceof Integer )
		{
			writer.writeInt32( ( Integer ) v );
		}
		else if ( v instanceof Long )
		{
			writer.writeInt64( ( Long ) v );
		}
		else if ( v instanceof Float )
		{
			writer.writeDouble( ( Float ) v );
		}
		else if ( v instanceof Double )
		{
			writer.writeDouble( ( Double ) v );
		}
		else if ( v instanceof Character )
		{
			writer.writeString( String.valueOf( v ) );
		}
		else if ( v instanceof Byte )
		{
			writer.writeInt32( ( ( Byte ) v ).intValue() );
		}
		else if ( v instanceof Short )
		{
			writer.writeInt32( ( ( Short ) v ).intValue() );
		}
		else if ( v instanceof Boolean )
		{
			writer.writeBoolean( ( Boolean ) v );
		}
		else if ( v instanceof String )
		{
			writer.writeString( ( ( String ) v ).intern() );
		}
		else
		{
			System.out.println( "Type not supported. Type: " + v.getClass().getSimpleName() );
		}

	}

	@Override
	public Class< LabelingMapping< T > > getEncoderClass()
	{
		return ( Class< LabelingMapping< T > > ) new LabelingMapping< T >( new IntType() ).getClass();
	}

	public void setCodecRegistry( CodecRegistry codecRegistry )
	{
		this.codecRegistry = codecRegistry;
	}

	public Class getClazz()
	{
		return clazz;
	}

	public CodecRegistry getCodecRegistry()
	{
		return codecRegistry;
	}

	public String getIndexImg()
	{
		return indexImg;
	}

	public void setIndexImg( final String indexImg )
	{
		this.indexImg = indexImg;
	}

	public LongFunction< T > getIdToLabel()
	{
		return idToLabel;
	}

	public void setIdToLabel( final LongFunction< T > idToLabel )
	{
		this.idToLabel = idToLabel;
	}

	public ToLongFunction< T > getLabelToId()
	{
		return labelToId;
	}

	public void setLabelToId( final ToLongFunction< T > labelToId )
	{
		this.labelToId = labelToId;
	}

	private static final Set< Class > WRAPPER_TYPES = new HashSet( Arrays.asList( IntType.class, LongType.class, BoolType.class,
			Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class, String.class ) );

	public static boolean isWrapperType( Class clazz )
	{
		return WRAPPER_TYPES.contains( clazz );
	}

	public static final class Builder< T >
	{
		private Class< T > clazz = null;

		private CodecRegistry codecRegistry = null;

		private String indexImg = null;

		private LongFunction< T > idToLabel = null;

		private ToLongFunction< T > labelToId = null;

		/**
		 * Set either a class and include a codec for that class
		 * or provide functions for encoding {@link #setLabelToId}
		 * and decoding {@link #setIdToLabel}.
		 *
		 * @param clazz
		 *
		 * @return
		 */
		public Builder< T > setClazz( final Class clazz )
		{
			this.clazz = clazz;
			return this;
		}

		public Builder< T > setCodecRegistry( final CodecRegistry codecRegistry )
		{
			this.codecRegistry = codecRegistry;
			return this;
		}

		public Builder< T > setIndexImg( final String indexImg )
		{
			this.indexImg = indexImg;
			return this;
		}

		/**
		 * Set either this function for decoding or provide a class through {@link #setClazz}
		 * and codec through the registry {@link #setCodecRegistry}
		 *
		 * @param idToLabel
		 *
		 * @return
		 */
		public Builder< T > setIdToLabel( final LongFunction< T > idToLabel )
		{
			this.idToLabel = idToLabel;
			return this;
		}

		public Builder< T > setLabelToId( final ToLongFunction< T > labelToId )
		{
			this.labelToId = labelToId;
			return this;
		}

		public LabelingMappingCodec< T > build()
		{
			return new LabelingMappingCodec< T >( this );
		}

	}

}
