package raw_data_connector;

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
import utils.JsonPOJODeserializer;
import utils.JsonPOJOSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class RawDataStreamer {

    public final static String INPUT_TOPIC = "sa18.raw_data.analytics";
    //    public final static String TOPIC = "consumer-tutorial";
    public final static String BOOTSTRAP_SERVERS = "localhost:9092";

    private int id;
    private String inputTopic;
    private List<String> outputTopics;
    private KafkaStreams kafkaStreams;

    public RawDataStreamer(int id, String inputTopic) {
        this.id = id;
        this.inputTopic = inputTopic;

        Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

//        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.serdeFrom(CustomRawDataEventDeserializer.class));

        // TODO: the following can be removed with a serialization factory
        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<RawEvent> rawEventSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", RawEvent.class);
        rawEventSerializer.configure(serdeProps, false);

        final Deserializer<RawEvent> rawEventDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", RawEvent.class);
        rawEventDeserializer.configure(serdeProps, false);

        final Serde<RawEvent> rawEventSerde = Serdes.serdeFrom(rawEventSerializer, rawEventDeserializer);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, RawEvent> rawDataEntries = builder.stream(inputTopic, Consumed.with(Serdes.String(), rawEventSerde));

        rawDataEntries.to("test", Produced.with(Serdes.String(), rawEventSerde));

        this.kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
    }

    public void start() {
        this.kafkaStreams.start();
    }
}
