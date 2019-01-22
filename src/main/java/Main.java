import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import fab_data_connector.FabDataStreamer;
import message_stream.StreamProcessor;
import org.apache.kafka.streams.state.HostInfo;
import raw_data_connector.RawDataStreamer;
import stream_processor.JacksonConfig;
import stream_processor.PersistentTopicStreamer;
import stream_processor.RESTService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;


@ApplicationPath("/")
public class Main extends Application {
    static PersistentTopicStreamer persistentTopicStreamer;

    static {
        persistentTopicStreamer = new PersistentTopicStreamer(123455);
        persistentTopicStreamer.start();
    }

    public static void main(String[] agrs) throws Exception {
        System.out.println("Started");

        try {
            RawDataStreamer rawDataStreamer = new RawDataStreamer(123457, RawDataStreamer.INPUT_TOPIC);
            rawDataStreamer.start();

            FabDataStreamer fabDataStreamer = new FabDataStreamer(123458, FabDataStreamer.INPUT_TOPIC);
            fabDataStreamer.start();

//            Testy testy = new Testy(12367776);
//            testy.start();

            StreamProcessor streamProcessor = new StreamProcessor(123459);
            streamProcessor.start();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> set = new HashSet<>();
        set.add(new RESTService(persistentTopicStreamer));
        set.add(new JacksonConfig());
        return set;
    }
}
