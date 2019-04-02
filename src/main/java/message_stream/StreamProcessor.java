package message_stream;

import fab_data_connector.FabConnectEvent;
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
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.RecordContext;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import org.apache.kafka.streams.state.*;
import org.apache.kafka.streams.state.internals.InMemoryKeyValueStore;
import raw_data_connector.RawConnectEvent;
import raw_data_connector.RawDataStreamer;
import utils.CustomExceptionHandler;
import utils.FabEventSerde;
import utils.JsonPOJODeserializer;
import utils.JsonPOJOSerializer;

import java.security.KeyPair;
import java.util.*;
import java.util.regex.Pattern;

public class StreamProcessor {
    public final static String BOOTSTRAP_SERVERS = "localhost:9092";
    public static String EQUIP_TRANSLATION_STATE = "equip_analytics";
    public static String RECIPE_TRANSLATION_STATE = "recipe_analytics";
    public static String STEP_TRANSLATION_STATE = "step_analytics";

    public static String FAILED_TRANSLATION = "failed_translations";


    public static String OUTPUT_TOPIC_PREFIX = "translated_";


    private int id;
    private KafkaStreams streamProcessor;
    private Set<String> outputTopics;
    private String[] translation_topics;

    public StreamProcessor(int id) {
        this.id = id;
        this.translation_topics = RawDataStreamer.MAPPINGS;
        this.outputTopics = new HashSet<>();

        // Configure the stream.
        Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "streamer-processor");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, FabEventSerde.class);
        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/home/luigi/sa18/");
//        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10 * 1024 * 1024L);
//        streamsConfiguration.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, CustomExceptionHandler.class);

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
        final KStream<String, FabEvent> fabDataEntries = builder.stream(inputTopicPattern, Consumed.with(Serdes.String(), fabRowSerde));

        // Insert all the input stream into the output specific topic by using a topic name extractor.
        // If the topic is missing it will be automatically created.
//        fabDataEntries.to("topic_test14", Produced.with(Serdes.String(), fabRowSerde));


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

        // Create a table over the raw_data translation topics and materialize it (i.e. create a table in RAM or in DISK).
        // One for the equipOID translation into names.
        // This for a table in the disk.
        // KeyValueBytesStoreSupplier equipStoreSupplier = Stores.persistentKeyValueStore(EQUIP_TRANSLATION_STATE);
        // This for a table in RAM.
        KeyValueBytesStoreSupplier equipStoreSupplier = Stores.persistentKeyValueStore(EQUIP_TRANSLATION_STATE);

        KTable<String, RawEvent> equipTable = builder.table(
                translation_topics[0],
                Materialized.<String, RawEvent>as(equipStoreSupplier).withKeySerde(Serdes.String()).withValueSerde(rawEventSerde));

//        builder.addStateStore(Stores.keyValueStoreBuilder(equipStoreSupplier, Serdes.String(), rawEventSerde));

        // One for the recipeOID translation into names.
        KeyValueBytesStoreSupplier recipeStoreSupplier = Stores.persistentKeyValueStore(RECIPE_TRANSLATION_STATE);

        KTable<String, RawEvent> recipeTable = builder.table(
                translation_topics[1],
                Materialized.<String, RawEvent>as(recipeStoreSupplier).withKeySerde(Serdes.String()).withValueSerde(rawEventSerde));

//        builder.addStateStore(Stores.keyValueStoreBuilder(recipeStoreSupplier, Serdes.String(), rawEventSerde));

        // One for the stepOID translation into names.
        KeyValueBytesStoreSupplier stepStoreSupplier = Stores.persistentKeyValueStore(STEP_TRANSLATION_STATE);

        KTable<String, RawEvent> stepTable = builder.table(
                translation_topics[2],
                Materialized.<String, RawEvent>as(stepStoreSupplier).withKeySerde(Serdes.String()).withValueSerde(rawEventSerde));


        // fab translated event parts.
        // =============================================================================================================
        final Serializer<FabTranslatedEvent> fabTranslatedEventSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", FabTranslatedEvent.class);
        fabTranslatedEventSerializer.configure(serdeProps, false);

        final Deserializer<FabTranslatedEvent> fabTranslatedEventDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", FabTranslatedEvent.class);
        fabTranslatedEventDeserializer.configure(serdeProps, false);

        // Create the SerDe (SerializationDeserialization) object that Kafka Stream need.
        final Serde<FabTranslatedEvent> fabTranslatedEventSerde = Serdes.serdeFrom(fabTranslatedEventSerializer, fabTranslatedEventDeserializer);


        KeyValueBytesStoreSupplier failedStoreSupplier = Stores.inMemoryKeyValueStore(FAILED_TRANSLATION);
        builder.addStateStore(Stores.keyValueStoreBuilder(failedStoreSupplier, Serdes.String(), fabTranslatedEventSerde));

//        builder.addStateStore(Stores.keyValueStoreBuilder(stepStoreSupplier, Serdes.String(), rawEventSerde));

        // Obtain one instance of the FabDataTransformer.
        TransformerSupplier<String, FabEvent, KeyValue<String, FabTranslatedEvent>> transformerSupplier = new TransformerSupplier<String, FabEvent, KeyValue<String, FabTranslatedEvent>>() {
            @Override
            public Transformer<String, FabEvent, KeyValue<String, FabTranslatedEvent>> get() {
                return new FabDataTransformer();
            }
        };

        // Extract the topic from the message, because a message is published in the category type topic prefixed with specific text.
        TopicNameExtractor<String, FabTranslatedEvent> topicNameExtractor = new TopicNameExtractor<String, FabTranslatedEvent>() {
            @Override
            public String extract(String s, FabTranslatedEvent fabEvent, RecordContext recordContext) {
                String topic = OUTPUT_TOPIC_PREFIX + fabEvent.getHoldType();
                outputTopics.add(topic);
//                System.out.println("Using topic: " + topic);
                return topic;
            }
        };

        // Transform each fab_data entry with the data coming from the raw_data topics and forward the result in the
        // specific topic obtained with a topic extractor based on each row contents.
        fabDataEntries
//                .transformValues(valueTransformerSupplier, EQUIP_TRANSLATION_STATE, RECIPE_TRANSLATION_STATE, STEP_TRANSLATION_STATE)
                .transform(transformerSupplier, EQUIP_TRANSLATION_STATE, RECIPE_TRANSLATION_STATE, STEP_TRANSLATION_STATE, FAILED_TRANSLATION)
                .through("translated_categories", Produced.with(Serdes.String(), fabTranslatedEventSerde))
                .to(topicNameExtractor, Produced.with(Serdes.String(), fabTranslatedEventSerde));
        this.streamProcessor = new KafkaStreams(builder.build(), streamsConfiguration);
    }


    public void start() {
        this.streamProcessor.start();
    }
}
