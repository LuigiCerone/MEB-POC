package fab_data_connector;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import utils.TopicUtils;

import java.util.*;

public class FabConsumer implements Runnable {

    public final static String TOPIC = "sa18.fab_data.event";
    //    public final static String TOPIC = "consumer-tutorial";
    public final static String BOOTSTRAP_SERVERS = "localhost:9092";

    private int id;
    private List<String> topics;
    private KafkaConsumer consumer;

    public FabConsumer(int id,
                       String groupId,
                       List<String> topics) {
        this.id = id;
        this.topics = topics;
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", CustomFabDataEventDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(topics);

            while (true) {
                ConsumerRecords<String, FabEvent> records = consumer.poll(Long.MAX_VALUE);
                for (ConsumerRecord<String, FabEvent> record : records) {

                    Map<String, Object> data = new HashMap<>();
                    data.put("partition", record.partition());
                    data.put("offset", record.offset());
                    data.put("value", record.value());
                    System.out.println(this.id + ": " + data);


                    // We need to republish the rows in the categories' topics.
                    String topicName = record.value().getHoldType();

                    TopicUtils.createTopic(topicName);
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }
}
