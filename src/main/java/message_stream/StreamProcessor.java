package message_stream;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.*;
import org.apache.kafka.streams.state.internals.InMemoryKeyValueStore;
import raw_data_connector.RawConnectEvent;
import raw_data_connector.RawDataStreamer;
import utils.JsonPOJODeserializer;
import utils.JsonPOJOSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class StreamProcessor {
    public final static String BOOTSTRAP_SERVERS = "localhost:9092";
    public static String EQUIP_TRANSLATION_STATE = "equip_analytics";
    public static String RECIPE_TRANSLATION_STATE = "recipe_analytics";
    public static String STEP_TRANSLATION_STATE = "step_analytics";

    private int id;
    private KafkaStreams streamProcessor;
    private String[] translation_topics;

    public StreamProcessor(int id) {
        this.id = id;
        this.translation_topics = RawDataStreamer.MAPPINGS;

        // Configure the stream.
        Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "ls");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10 * 1024 * 1024L);

        StreamsBuilder builder = new StreamsBuilder();

        // fab_data part.
        // =============================================================================================================
        // Configure the serialization and deserialization.
        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<FabEvent> fabRowSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", FabEvent.class);
        fabRowSerializer.configure(serdeProps, false);

        final Deserializer<FabEvent> fabRowDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", FabEvent.class);
        fabRowDeserializer.configure(serdeProps, false);

        // Create the SerDe (SerializationDeserialization) object that Kafka Stream need.
        final Serde<FabEvent> fabRowSerde = Serdes.serdeFrom(fabRowSerializer, fabRowDeserializer);

        // We don't know how many categories the simulator will create, so we subscribe to topics based on pattern's
        // matching. Every topics like category1, category40 is valid.
        Pattern inputTopicPattern = Pattern.compile("category[0-9]+");

        // Create a stream over the input_topic
        KStream<String, FabEvent> fabDataEntries = builder.stream(inputTopicPattern, Consumed.with(Serdes.String(), fabRowSerde));

        // Insert all the input stream into the output specific topic by using a topic name extractor.
        // If the topic is missing it will be automatically created.
        fabDataEntries.to("topic_test", Produced.with(Serdes.String(), fabRowSerde));


        // raw_data part.
        // ===========================================================================================================
        final Serializer<RawEvent> rawEventSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", RawEvent.class);
        rawEventSerializer.configure(serdeProps, false);

        final Deserializer<RawEvent> rawEventDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", RawEvent.class);
        rawEventDeserializer.configure(serdeProps, false);

        // Create the SerDe (SerializationDeserialization) object that Kafka Stream need.
        final Serde<RawEvent> rawEventSerde = Serdes.serdeFrom(rawEventSerializer, rawEventDeserializer);

        // Create a table over the raw_data translation topics.
        // One for the equipOID translation into names.


        KeyValueBytesStoreSupplier equipStoreSupplier = Stores.inMemoryKeyValueStore(EQUIP_TRANSLATION_STATE);

        KTable<String, RawEvent> equipTable = builder.table(
                translation_topics[0],
                Materialized.<String, RawEvent>as(equipStoreSupplier));

//        builder.addStateStore(Stores.keyValueStoreBuilder(equipStoreSupplier, Serdes.String(), rawEventSerde));

        // One for the recipeOID translation into names.
        KeyValueBytesStoreSupplier recipeStoreSupplier = Stores.inMemoryKeyValueStore(RECIPE_TRANSLATION_STATE);

        KTable<String, RawEvent> recipeTable = builder.table(
                translation_topics[1],
                Materialized.<String, RawEvent>as(recipeStoreSupplier));

//        builder.addStateStore(Stores.keyValueStoreBuilder(recipeStoreSupplier, Serdes.String(), rawEventSerde));

        // One for the stepOID translation into names.
        KeyValueBytesStoreSupplier stepStoreSupplier = Stores.inMemoryKeyValueStore(STEP_TRANSLATION_STATE);

        KTable<String, RawEvent> stepTable = builder.table(
                translation_topics[2],
                Materialized.<String, RawEvent>as(stepStoreSupplier));

//        builder.addStateStore(Stores.keyValueStoreBuilder(stepStoreSupplier, Serdes.String(), rawEventSerde));


        ValueTransformerSupplier valueTransformerSupplier = new ValueTransformerSupplier() {
            @Override
            public ValueTransformer get() {
                return new FabDataTransformer();
            }
        };

        fabDataEntries.transformValues(valueTransformerSupplier, EQUIP_TRANSLATION_STATE, RECIPE_TRANSLATION_STATE, STEP_TRANSLATION_STATE);
//        .to();
        this.streamProcessor = new KafkaStreams(builder.build(), streamsConfiguration);
    }


    public void start() {
        this.streamProcessor.start();
    }
}
