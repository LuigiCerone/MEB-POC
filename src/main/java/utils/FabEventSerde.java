package utils;

import message_stream.FabEvent;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class FabEventSerde implements Serde<FabEvent> {

    private JsonPOJOSerializer<FabEvent> serializer = new JsonPOJOSerializer<FabEvent>();
    private JsonPOJODeserializer<FabEvent> deserializer = new JsonPOJODeserializer<FabEvent>();

    @Override
    public void configure(Map<String, ?> map, boolean isKey) {
        serializer.configure(map, isKey);
        deserializer.configure(map, isKey);
    }

    @Override
    public void close() {
        serializer.close();
        deserializer.close();
    }

    @Override
    public Serializer<FabEvent> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<FabEvent> deserializer() {
        return deserializer;
    }
}
