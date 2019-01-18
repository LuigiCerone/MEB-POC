package utils;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class TopicUtils {

    public final static String BOOTSTRAP_SERVERS = "localhost:9092";

    public static boolean createTopic(String topicName, int partitions, int replicationFactory) {
        Properties properties = new Properties();

        properties.setProperty("bootstrap.servers", BOOTSTRAP_SERVERS);
        properties.setProperty("client.id", "consumerAdmin");
        properties.setProperty("metadata.max.age.ms", "3000");
        properties.setProperty("group.id", "test");
        properties.setProperty("enable.auto.commit", "true");
        properties.setProperty("auto.commit.interval.ms", "1000");
        properties.setProperty("session.timeout.ms", "30000");
        properties.setProperty("key.deserializer", StringDeserializer.class.getName());
        properties.setProperty("value.deserializer", StringDeserializer.class.getName());

        AdminClient adminClient = KafkaAdminClient.create(properties);

        //new NewTopic(topicName, numPartitions, replicationFactor)
        NewTopic newTopic = new NewTopic(topicName, partitions, (short) replicationFactory);

        List<NewTopic> newTopics = new ArrayList<NewTopic>();
        newTopics.add(newTopic);

        adminClient.createTopics(newTopics);
        adminClient.close();
        return true;
    }
}
