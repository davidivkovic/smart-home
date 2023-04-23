package bsep.api;

import bsep.users.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
public class Users extends Resource {

    @GET
    @Path("/")
    // TODO: add pagination
    public Response getAllUsers(@QueryParam("query") @Size(max = 128) String query) {
        return ok(User.search(query));
    }

}
