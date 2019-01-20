package message_stream;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import raw_data_connector.RawDataStreamer;
import utils.CustomExceptionHandler;
import utils.JsonPOJODeserializer;
import utils.JsonPOJOSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class Testy {
    public final static String BOOTSTRAP_SERVERS = "localhost:9092";
    public static String EQUIP_TRANSLATION_STATE = "equip_analytics";
    public static String RECIPE_TRANSLATION_STATE = "recipe_analytics";
    public static String STEP_TRANSLATION_STATE = "step_analytics";

    private int id;
    private KafkaStreams streamProcessor;
    private String[] translation_topics;

    public Testy(int id) {
        this.id = id;
        this.translation_topics = RawDataStreamer.MAPPINGS;

        // Configure the stream.
        Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "test1");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10 * 1024 * 1024L);
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

//        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, fabRowSerde.getClass().getName());


        // We don't know how many categories the simulator will create, so we subscribe to topics based on pattern's
        // matching. Every topics like category1, category40 is valid.
        Pattern inputTopicPattern = Pattern.compile("category[0-9]+");

        // Create a stream over the input_topic
        KStream<String, FabEvent> fabDataEntries = builder.stream(inputTopicPattern, Consumed.with(Serdes.String(), fabRowSerde));

        // Insert all the input stream into the output specific topic by using a topic name extractor.
        // If the topic is missing it will be automatically created.
        fabDataEntries.to("topic_test13", Produced.with(Serdes.String(), fabRowSerde));

        this.streamProcessor = new KafkaStreams(builder.build(), streamsConfiguration);
    }


    public void start() {
        this.streamProcessor.start();
    }
}
