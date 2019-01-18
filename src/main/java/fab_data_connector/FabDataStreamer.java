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
import raw_data_connector.CustomRawDataEventDeserializer;
import raw_data_connector.RawEvent;
import utils.JsonPOJODeserializer;
import utils.JsonPOJOSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class FabDataStreamer {

    public final static String INPUT_TOPIC = "sa18.fab_data.event";
    //    public final static String TOPIC = "consumer-tutorial";
    public final static String BOOTSTRAP_SERVERS = "localhost:9092";

    private int id;
    private String inputTopic;
    private List<String> outputTopics;
    private KafkaStreams kafkaStreams;

    public FabDataStreamer(int id, String inputTopic) {
        this.id = id;
        this.inputTopic = inputTopic;

        Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

//        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.serdeFrom(CustomRawDataEventDeserializer.class));

        // TODO: the following can be removed with a serialization factory
        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<FabEvent> fabEventSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", FabEvent.class);
        fabEventSerializer.configure(serdeProps, false);

        final Deserializer<FabEvent> fabEventDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", FabEvent.class);
        fabEventDeserializer.configure(serdeProps, false);

        final Serde<FabEvent> fabEventSerde = Serdes.serdeFrom(fabEventSerializer, fabEventDeserializer);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, FabEvent> rawDataEntries = builder.stream(inputTopic, Consumed.with(Serdes.String(), fabEventSerde));

        rawDataEntries.to("test", Produced.with(Serdes.String(), fabEventSerde));

        this.kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
    }

    public void start() {
        this.kafkaStreams.start();
    }
}
