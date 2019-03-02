import main.KafkaRunner;
import org.glassfish.jersey.jackson.JacksonFeature;
import stream_processor.RESTService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;


@ApplicationPath("/")
public class Main extends Application {

//    @Override
//    public Set<Class<?>> getClasses() {
//        Set<Class<?>> resources = new java.util.HashSet<>();
//        resources.add(JacksonFeature.class);
//        return resources;
//    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> set = new HashSet<>();

        set.add(new KafkaRunner());
        set.add(new RESTService());
        set.add(new JacksonFeature());
//        set.add(new MarshallingFeature());
//        set.add(new JacksonJsonProvider());
        return set;
    }

}
