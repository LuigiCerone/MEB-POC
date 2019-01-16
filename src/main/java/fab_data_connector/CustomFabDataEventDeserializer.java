package fab_data_connector;

import org.apache.kafka.common.serialization.Deserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class CustomFabDataEventDeserializer implements Deserializer<FabEvent> {


    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public FabEvent deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        FabEvent fabEvent = null;
        try {
            fabEvent = mapper.readValue(bytes, FabEvent.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fabEvent;
    }

    @Override
    public void close() {

    }
}
