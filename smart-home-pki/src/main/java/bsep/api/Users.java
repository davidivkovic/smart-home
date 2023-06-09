package bsep.api;

import bsep.users.User;

import static bsep.users.User.Roles;

import org.bson.types.ObjectId;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
public class Users extends Resource {

    @GET
    @Path("/")
    @RolesAllowed({ Roles.ADMIN, Roles.LANDLORD })
    public Response getAllUsers(
            @QueryParam("query") @Size(max = 128) @Pattern(regexp = "[a-zA-Z\\s]*") String query,
            @QueryParam("page") @NotNull int page)
    {
        var users = User.search(query, page - 1, 4);
        return ok(users);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ Roles.ADMIN })
    public Response deleteUser(@PathParam("id") @NotBlank String id) {
        User user = User.findById(new ObjectId(id));
        if(user == null) return badRequest("User does not exist");

        user.delete();

        return ok(user);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ Roles.ADMIN })
    public Response updateRole(
            @PathParam("id") @NotBlank String id,
            @QueryParam("role") @NotBlank @Size(max = 100) String role)
    {
        User user = User.findById(new ObjectId(id));
        if(user == null) return badRequest("User does not exist");

        user.updateRole(role);

        return ok();
    }

    @GET
    @Path("/roles")
    @RolesAllowed({ Roles.ADMIN })
    public Response getAllRoles() {
        return ok(Roles.all);
    }

    @POST
    @Path("/{id}/unlock")
    @RolesAllowed({ Roles.ADMIN })
    public Response unlockUser(@PathParam("id") @NotBlank @Size(max = 128) String id) {
        User user = User.findById(new ObjectId(id));
        if(user == null) return badRequest("User does not exist");

        user.resetLockout();

        return ok();
    }
}
