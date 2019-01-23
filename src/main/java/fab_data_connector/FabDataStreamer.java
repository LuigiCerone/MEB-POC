package fab_data_connector;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.RecordContext;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import utils.CustomExceptionHandler;
import utils.JsonPOJODeserializer;
import utils.JsonPOJOSerializer;

import java.util.*;

public class FabDataStreamer {

    public final static String INPUT_TOPIC = "sa18.fab_data.event";
    //    public final static String TOPIC = "consumer-tutorial";
    public final static String BOOTSTRAP_SERVERS = "localhost:9092";

    private int id;
    private String inputTopic;
    private Set<String> outputTopics;
    private KafkaStreams kafkaStreams;

    public FabDataStreamer(int id, String inputTopic) {
        this.id = id;
        this.inputTopic = inputTopic;
        this.outputTopics = new HashSet<>();

        // Configure the stream.
        Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "fab-data-streamer");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        streamsConfiguration.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, CustomExceptionHandler.class);


        // Configure the serialization and deserialization.
        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<FabConnectEvent> fabEventSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", FabConnectEvent.class);
        fabEventSerializer.configure(serdeProps, false);

        final Deserializer<FabConnectEvent> fabEventDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", FabConnectEvent.class);
        fabEventDeserializer.configure(serdeProps, false);

        // Create the SerDe (SerializationDeserialization) object that Kafka Stream need.
        final Serde<FabConnectEvent> fabEventSerde = Serdes.serdeFrom(fabEventSerializer, fabEventDeserializer);

        StreamsBuilder builder = new StreamsBuilder();

        // Create a stream over the input_topic
        KStream<String, FabConnectEvent> fabDataEntries = builder.stream(inputTopic, Consumed.with(Serdes.String(), fabEventSerde));

        // Extract the topic from the message, because a message is published in the category type topic.
        TopicNameExtractor<String, FabConnectEvent> topicNameExtractor = new TopicNameExtractor<String, FabConnectEvent>() {
            @Override
            public String extract(String s, FabConnectEvent fabEvent, RecordContext recordContext) {
                outputTopics.add(fabEvent.getHoldType());
                System.out.println("Using topic: " + fabEvent.getHoldType());
                return fabEvent.getHoldType();
            }
        };

        // Insert all the input stream into the output specific topic by using a topic name extractor.
        // If the topic is missing it will be automatically created.
        fabDataEntries.to(topicNameExtractor, Produced.with(Serdes.String(), fabEventSerde));

        // Another stream into a general topic for debug purpose only.
//        KStream<String, FabConnectEvent> fabDataEntriesDebug = builder.stream(inputTopic, Consumed.with(Serdes.String(), fabEventSerde));
//        fabDataEntriesDebug.to("debug_categories", Produced.with(Serdes.String(), fabEventSerde));

        this.kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
    }

    public void start() {
        this.kafkaStreams.start();
    }
}
