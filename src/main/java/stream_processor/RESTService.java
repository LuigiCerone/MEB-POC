package stream_processor;

import org.apache.kafka.streams.state.HostInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;


@Path("rest")
public class RESTService {

    private HostInfo hostInfo;
    private ArrayList<String> endpoints;

    public RESTService(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }

    public RESTService() {
        endpoints = new ArrayList<>();
        endpoints.add("/category/{number}");
        endpoints.add("/categories/{number}");
    }

    @GET
    @Path("/category/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCategoryLive(@PathParam("number") final String genre) {
        return "Test";
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String availableEndPoint() {
        return endpoints.toString();
    }
}
