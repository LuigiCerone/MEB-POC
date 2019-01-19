package message_stream;

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
import utils.JsonPOJODeserializer;
import utils.JsonPOJOSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class StreamProcessor {
    public final static String BOOTSTRAP_SERVERS = "localhost:9092";


    private int id;
    private KafkaStreams streamProcessor;

    public StreamProcessor(int id) {
        this.id = id;

        // Configure the stream.
        Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "ls");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10 * 1024 * 1024L);


        // Configure the serialization and deserialization.
        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<FabRow> fabRowSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", FabRow.class);
        fabRowSerializer.configure(serdeProps, false);

        final Deserializer<FabRow> fabRowDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", FabRow.class);
        fabRowDeserializer.configure(serdeProps, false);

        // Create the SerDe (SerializationDeserialization) object that Kafka Stream need.
        final Serde<FabRow> fabRowSerde = Serdes.serdeFrom(fabRowSerializer, fabRowDeserializer);

        StreamsBuilder builder = new StreamsBuilder();

        // We don't know how many categories the simulator will create, so we subscribe to topics based on pattern's
        // matching. Every topics like category1, category40 is valid.
        Pattern inputTopicPattern = Pattern.compile("category[0-9]+");

        // Create a stream over the input_topic
        KStream<String, FabRow> fabDataEntries = builder.stream(inputTopicPattern, Consumed.with(Serdes.String(), fabRowSerde));


        // Insert all the input stream into the output specific topic by using a topic name extractor.
        // If the topic is missing it will be automatically created.
        fabDataEntries.to("topic_test", Produced.with(Serdes.String(), fabRowSerde));


        // test.
        // Create a stream over the input_topic
        KStream<String, String> test = builder.stream("topic_test", Consumed.with(Serdes.String(), Serdes.String()));
        test.to("topic_test2", Produced.with(Serdes.String(), Serdes.String()));

        this.streamProcessor = new KafkaStreams(builder.build(), streamsConfiguration);
    }

    public void start() {
        this.streamProcessor.start();
    }
}
