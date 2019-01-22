package stream_processor;

import main.KafkaRunner;
import message_stream.FabTranslatedEvent;
import org.apache.kafka.streams.state.HostInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("rest")
public class RESTService {

    private HostInfo hostInfo;
    private ArrayList<String> endpoints;
    private PersistentTopicStreamer persistentTopicStreamer;

    public RESTService() {
//        this.persistentTopicStreamer = persistentTopicStreamer;
//        this.persistentTopicStreamer = new PersistentTopicStreamer(123455);
//        persistentTopicStreamer.start();

        this.persistentTopicStreamer = KafkaRunner.getPersistentTopicStreamer();
        endpoints = new ArrayList<>();
        endpoints.add("/category/{number}");
    }


    @GET
    @Path("/category/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategoryLive(@PathParam("number") final int category) throws InterruptedException {
        List<FabTranslatedEvent> result = this.persistentTopicStreamer.getTableAsListFromCategory(category);
        return Response.ok(result).status(200).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String availableEndPoint() {
        return endpoints.toString();
    }
}
