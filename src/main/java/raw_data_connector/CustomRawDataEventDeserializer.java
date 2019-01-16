package raw_data_connector;

import org.apache.kafka.common.serialization.Deserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class CustomRawDataEventDeserializer implements Deserializer<RawEvent> {


    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public RawEvent deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        RawEvent rawEvent = null;
        try {
            rawEvent = mapper.readValue(bytes, RawEvent.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rawEvent;
    }

    @Override
    public void close() {

    }
}
