package raw_data_connector;

import fab_data_connector.FabEvent;
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
import utils.JsonPOJODeserializer;
import utils.JsonPOJOSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class RawDataStreamer {

    public final static String INPUT_TOPIC = "sa18.raw_data.analytics";
    public final static String BOOTSTRAP_SERVERS = "localhost:9092";

    private int id;
    private String inputTopic;
    private List<String> outputTopics;
    private KafkaStreams kafkaStreams;

    private String[] mappings = {"equip_analytics", "recipe_analytics", "step_analytics"};


    public RawDataStreamer(int id, String inputTopic) {
        this.id = id;
        this.inputTopic = inputTopic;

        // Configure the stream.
        Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // Configure the serialization and deserialization.
        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<RawEvent> rawEventSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", RawEvent.class);
        rawEventSerializer.configure(serdeProps, false);

        final Deserializer<RawEvent> rawEventDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", RawEvent.class);
        rawEventDeserializer.configure(serdeProps, false);

        // Create the SerDe (SerializationDeserialization) object that Kafka Stream need.
        final Serde<RawEvent> rawEventSerde = Serdes.serdeFrom(rawEventSerializer, rawEventDeserializer);

        StreamsBuilder builder = new StreamsBuilder();

        // Create a stream over the input_topic
        KStream<String, RawEvent> rawDataEntries = builder.stream(inputTopic, Consumed.with(Serdes.String(), rawEventSerde));

        // Extract the topic from the message, because a message is published in the category type topic.
        TopicNameExtractor<String, RawEvent> topicNameExtractor = new TopicNameExtractor<String, RawEvent>() {
            @Override
            public String extract(String s, RawEvent rawEvent, RecordContext recordContext) {

                outputTopics.add(mappings[rawEvent.getType()]);
                return mappings[rawEvent.getType()];
            }
        };

        // Insert all the input stream into the output specific topic by using a topic name extractor.
        // If the topic is missing it will be automatically created.
        rawDataEntries.to(topicNameExtractor, Produced.with(Serdes.String(), rawEventSerde));

        this.kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
    }

    public void start() {
        this.kafkaStreams.start();
    }

}
