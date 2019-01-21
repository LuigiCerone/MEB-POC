package stream_processor;

import org.apache.kafka.streams.state.HostInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("rest")
public class RESTService {

    private HostInfo hostInfo;

    public RESTService(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }

    public RESTService() {
    }

    @GET
    @Path("/category/{number}")
    @Produces(MediaType.TEXT_PLAIN)
    public String genreCharts(@PathParam("number") final String genre) {
        return "Test";
    }

}
