package bsep.devices;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.inject.Singleton;

import java.util.List;

@Singleton
@Path("/devices")
@RegisterRestClient(baseUri = "http://localhost:8081")
public interface DevicesService {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    List<Object> getAll(@QueryParam("buildingId") String buildingId, @QueryParam("key") String key);

}
