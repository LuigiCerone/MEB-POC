import main.KafkaRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream_processor.JacksonConfig;
import stream_processor.RESTService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;


@ApplicationPath("/")
public class Main extends Application {

    @Override
    public Set<Object> getSingletons() {
        Set<Object> set = new HashSet<>();
        KafkaRunner kafkaRunner = new KafkaRunner();
        set.add(kafkaRunner);
        set.add(new RESTService());
        set.add(new JacksonConfig());
        return set;
    }
}
