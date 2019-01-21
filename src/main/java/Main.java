import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import fab_data_connector.FabDataStreamer;
import message_stream.StreamProcessor;
import org.apache.kafka.streams.state.HostInfo;
import raw_data_connector.RawDataStreamer;
import stream_processor.RESTService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;


@ApplicationPath("/")
public class Main extends Application {
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


//            RESTService restService = new RESTService(new HostInfo("http://localhost", 8080));

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(RESTService.class);
        resources.add(JacksonJsonProvider.class);
//        resources.add(CustomJacksonJsonProvider.class);
        return resources;
    }
}
