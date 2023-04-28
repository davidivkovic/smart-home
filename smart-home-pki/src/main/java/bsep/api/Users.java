package bsep.api;

import bsep.users.User;
import org.bson.types.ObjectId;

import javax.validation.Constraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Locale;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
public class Users extends Resource {

    @GET
    @Path("/")
    public Response getAllUsers(
            @QueryParam("query") @Size(max = 128) String query,
            @QueryParam("page") @NotNull int page)
    {
        var users = User.search(query, page - 1, 4);
        return ok(users);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") @NotBlank String id) {
        User user = User.findById(new ObjectId(id));
        if(user == null) return badRequest("User does not exist");
        user.delete();
        return ok(user);
    }

    @PUT
    @Path("/{id}")
    public Response updateRole(
            @PathParam("id") @NotBlank String id,
            @QueryParam("role") @NotBlank @Size(max = 100) String role)
    {
        User user = User.findById(new ObjectId(id));
        if(user == null) return badRequest("User does not exist");
        user.updateRole(role);
        return ok();
    }
}
