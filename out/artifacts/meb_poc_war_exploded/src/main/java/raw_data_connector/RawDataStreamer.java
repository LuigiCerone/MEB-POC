package raw_data_connector;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.RecordContext;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import utils.JsonPOJODeserializer;
import utils.JsonPOJOSerializer;

import java.util.*;

public class RawDataStreamer {

    public final static String INPUT_TOPIC = "sa18.raw_data.analytics";
    public final static String BOOTSTRAP_SERVERS = "localhost:9092";

    private int id;
    private String inputTopic;
    private Set<String> outputTopics;
    private KafkaStreams kafkaStreams;

    public static String[] MAPPINGS = {"equip_analytics", "recipe_analytics", "step_analytics"};


    public RawDataStreamer(int id, String inputTopic) {
        this.id = id;
        this.inputTopic = inputTopic;
        this.outputTopics = new HashSet<>();

        // Configure the stream.
        Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "test1");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // Configure the serialization and deserialization.
        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<RawConnectEvent> rawEventSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", RawConnectEvent.class);
        rawEventSerializer.configure(serdeProps, false);

        final Deserializer<RawConnectEvent> rawEventDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", RawConnectEvent.class);
        rawEventDeserializer.configure(serdeProps, false);

        // Create the SerDe (SerializationDeserialization) object that Kafka Stream need.
        final Serde<RawConnectEvent> rawEventSerde = Serdes.serdeFrom(rawEventSerializer, rawEventDeserializer);

        StreamsBuilder builder = new StreamsBuilder();

        // Create a stream over the input_topic
        KStream<String, RawConnectEvent> rawDataEntries = builder.stream(inputTopic, Consumed.with(Serdes.String(), rawEventSerde));

        // This is another version in which the rawDataEntries are smaller in size.
//        KStream<Long, String> mappedStream = rawDataEntries.map(new KeyValueMapper<String, RawConnectEvent, KeyValue<? extends Long, ? extends String>>() {
//            @Override
//            public KeyValue<? extends Long, ? extends String> apply(String s, RawConnectEvent rawConnectEvent) {
//                return KeyValue.pair(rawConnectEvent.getOid(), rawConnectEvent.getNameTranslation());
//            }
//        });

        // Extract the topic from the message, because a message is published in the category type topic.
        TopicNameExtractor<String, RawConnectEvent> topicNameExtractor = new TopicNameExtractor<String, RawConnectEvent>() {
            @Override
            public String extract(String s, RawConnectEvent rawEvent, RecordContext recordContext) {
                System.out.println("RawDataStreamer");
                outputTopics.add(MAPPINGS[rawEvent.getType()]);
                return MAPPINGS[rawEvent.getType()];
            }
        };

        // Insert all the input stream into the output specific topic by using a topic name extractor.
        // If the topic is missing it will be automatically created.
        rawDataEntries.to(topicNameExtractor, Produced.with(Serdes.String(), rawEventSerde));
//        mappedStream.to("equip_analytics", Produced.with(Serdes.Long(), Serdes.String()));

        this.kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
    }

    public void start() {
        this.kafkaStreams.start();
    }

}
