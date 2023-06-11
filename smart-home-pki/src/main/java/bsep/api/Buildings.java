package bsep.api;

import bsep.api.dto.buildings.CreateBuildingRequest;
import bsep.api.dto.users.UserDTO;
import bsep.buildings.Building;
import bsep.users.User;

import io.quarkus.security.Authenticated;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static bsep.util.Utils.mapper;

@Path("/buildings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Buildings extends Resource {

    @GET
    @Path("/types")
    @Authenticated
    public Response getBuildingTypes()
    {
        return ok(Building.types);
    }

    @GET
    @Path("/")
    @Authenticated
    public Response getBuildings(@QueryParam("page") int page)
    {
        // TODO: access by role
        return ok(Building.findAll().page(page, 4).list());
    }

    @POST
    @Path("/")
    @Authenticated
    public Response createBuilding(CreateBuildingRequest request)
    {
        User user = User.findById(new ObjectId(userId()));

        if (user == null) return badRequest("User not found");
        if (user.role.equals(User.Roles.TENANT)) return badRequest("Tenants cannot create buildings");

        Building building = new Building(request.name, request.address, request.type, userId());
        building.persist();

        return ok(building);
    }

    @GET
    @Path("/{id}")
    @Authenticated
    public Response getBuilding(@PathParam("id") String id)
    {
        Building building = Building.findById(new ObjectId(id));
        if (building == null) return badRequest("Building with this id does not exist");

        if (!isAdmin() && !building.landlordId.equals(userId())) return forbidden();

        return ok(building);
    }

    @DELETE
    @Path("/{id}")
    @Authenticated
    public Response deleteBuilding(@PathParam("id") String id)
    {
        Building building = Building.findById(new ObjectId(id));
        if (building == null) return badRequest("Building with this id does not exist");

        if (!isAdmin() && !building.landlordId.equals(userId())) return forbidden();

        building.delete();

        return ok();
    }

    @POST
    @Authenticated
    @Path("/{id}/tenants")
    @Operation(summary = "Sets and overrides the tenants of a building. This method is idempotent. Can be used to remove tenants from a building.")
    public Response setTenants(@PathParam("id") String id, @QueryParam("tenantIds") List<String> tenantIds)
    {
        Building building = Building.findById(new ObjectId(id));
        if (building == null) return badRequest("Building with this id does not exist");

        if (!isAdmin() && !building.landlordId.equals(userId())) return forbidden();

        building.tenants = User.list("_id in ?1", tenantIds.stream().map(ObjectId::new).toList())
                .stream()
                .map(user -> mapper.map(user, UserDTO.class))
                .toList();
        building.update();

        return ok();
    }

}
