package stream_processor;

import fab_data_connector.FabConnectEvent;
import message_stream.FabTranslatedEvent;
import message_stream.StreamProcessor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.RecordContext;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.Stores;
import utils.CustomExceptionHandler;
import utils.JsonPOJODeserializer;
import utils.JsonPOJOSerializer;

import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
public class PersistentTopicStreamer {


    public final static String BOOTSTRAP_SERVERS = "localhost:9092";
    public final static String PERSISTENT_TABLE_NAME_PREFIX = "ktable_";

    private int id;
    private Set<String> outputTopics;
    private KafkaStreams kafkaStreams;

    KTable<String, FabTranslatedEvent>[] tables;
    KeyValueBytesStoreSupplier[] storeSuppliers;

    private boolean started = false;

    public PersistentTopicStreamer(int id) {
        this.id = id;
        this.outputTopics = new HashSet<>();

        // Configure the stream.
        Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "persistent-streamer");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        streamsConfiguration.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, CustomExceptionHandler.class);
        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/home/luigi-cer1/sa18/");


        // Configure the serialization and deserialization.
        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<FabTranslatedEvent> fabEventSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", FabTranslatedEvent.class);
        fabEventSerializer.configure(serdeProps, false);

        final Deserializer<FabTranslatedEvent> fabEventDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", FabTranslatedEvent.class);
        fabEventDeserializer.configure(serdeProps, false);

        // Create the SerDe (SerializationDeserialization) object that Kafka Stream need.
        final Serde<FabTranslatedEvent> fabEventSerde = Serdes.serdeFrom(fabEventSerializer, fabEventDeserializer);

        StreamsBuilder builder = new StreamsBuilder();

        // We don't know how many categories the simulator will create, so we subscribe to topics based on pattern's
        // matching. Every topics like category1, category40 is valid.
        Pattern inputTopicPattern = Pattern.compile(StreamProcessor.OUTPUT_TOPIC_PREFIX + "category[0-9]+");
//        Pattern inputTopicPattern = Pattern.compile("category[0-9]+");

        // Create a stream over the input_topic
        KStream<String, FabTranslatedEvent> fabDataEntries =
                builder.stream(inputTopicPattern, Consumed.with(Serdes.String(), fabEventSerde));

        KStream<String, FabTranslatedEvent>[] branches = fabDataEntries.branch(
                (key, fab) -> fab.getHoldType().contains("0"),
                (key, fab) -> fab.getHoldType().contains("1"),
                (key, fab) -> fab.getHoldType().contains("2"),
                (key, fab) -> fab.getHoldType().contains("3"),
                (key, fab) -> fab.getHoldType().contains("4"),
                (key, fab) -> fab.getHoldType().contains("5"),
                (key, fab) -> fab.getHoldType().contains("6"),
                (key, fab) -> fab.getHoldType().contains("7"),
                (key, fab) -> fab.getHoldType().contains("8"),
                (key, fab) -> fab.getHoldType().contains("9"));


        storeSuppliers = new KeyValueBytesStoreSupplier[branches.length];
        tables = new KTable[branches.length];

        for (int i = 0; i < branches.length; i++) {
//            storeSuppliers[i] = Stores.inMemoryKeyValueStore(PERSISTENT_TABLE_NAME_PREFIX + i);
            storeSuppliers[i] = Stores.persistentKeyValueStore(PERSISTENT_TABLE_NAME_PREFIX + i);
            tables[i] = branches[i]
                    .groupByKey()
                    .reduce((aggValue, newValue) -> newValue, Materialized.<String, FabTranslatedEvent>as(storeSuppliers[i])
                            .withKeySerde(Serdes.String())
                            .withValueSerde(fabEventSerde));
//            builder.addStateStore(Stores.keyValueStoreBuilder(storeSuppliers[i], Serdes.String(), fabEventSerde));
        }

        // Insert all the input stream into the output specific topic by using a topic name extractor.
        // If the topic is missing it will be automatically created.


        // Another stream into a general topic for debug purpose only.
//        KStream<String, FabConnectEvent> fabDataEntriesDebug = builder.stream(inputTopic, Consumed.with(Serdes.String(), fabEventSerde));
//        fabDataEntriesDebug.to("debug_categories", Produced.with(Serdes.String(), fabEventSerde));

        this.kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
    }

    public void start() {
        this.started = true;
        this.kafkaStreams.start();
    }

    public String getTest() {
        return "From Persistent";
    }

    public List<FabTranslatedEvent> getTableAsListFromCategory(int category) throws InterruptedException {
        if (category >= 10) return null;
        LinkedList<FabTranslatedEvent> list = new LinkedList<>();
        KTable<String, FabTranslatedEvent> table = this.tables[category];

        try {
            KeyValueIterator<String, FabTranslatedEvent> iterator =
                    this.kafkaStreams.store(table.queryableStoreName(), QueryableStoreTypes.<String, FabTranslatedEvent>keyValueStore()).all();
            while (iterator.hasNext()) {
                KeyValue<String, FabTranslatedEvent> entry = iterator.next();
                list.add(entry.value);
            }
        } catch (InvalidStateStoreException ex) {
            Thread.sleep(300);
        }
        return list;

    }
}