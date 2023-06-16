package bsep.api;

import bsep.api.dto.devices.AddDeviceRequest;
import bsep.buildings.Building;
import bsep.devices.Device;

import io.quarkus.security.Authenticated;

import org.bson.types.ObjectId;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Devices extends Resource {

    @GET
    @Authenticated
    @Path("/types")
    public Response getDeviceTypes()
    {
        return ok(Device.devices);
    }

    @POST
    @Path("/")
    @Authenticated
    public Response addDevice(@Valid AddDeviceRequest request)
    {
        var deviceType = Device.getTypeById(request.typeId);
        if (deviceType == null) return badRequest("This device type does not exist.");

        Building building = Building.findById(new ObjectId(request.buildingId));
        if (building == null) return badRequest("This building does not exist.");

        if (!isAdmin() && !building.landlordId.equals(userId())) return forbidden();

        var device = new Device(request.name, request.brand, userId(), request.buildingId, deviceType);
        device.persist();

        return ok(device);
    }

    @GET
    @Authenticated
    @Path("/{id}")
    public Response getDevice(@PathParam("id") String deviceId)
    {
        Device device = Device.findById(new ObjectId(deviceId));
        if (device == null) return badRequest("This device does not exist.");

        Building building = Building.findById(new ObjectId(device.buildingId));
        if (building == null) return badRequest("This building does not exist.");

        if (!isAdmin() && !building.visibleTo(userId())) return forbidden();

        return ok(device);
    }

    @DELETE
    @Authenticated
    @Path("/{id}")
    public Response removeDevice(@PathParam("id") String deviceId)
    {
        Device device = Device.findById(new ObjectId(deviceId));
        if (device == null) return badRequest("This device does not exist.");

        if (!isAdmin() && !device.ownerId.equals(userId())) return forbidden();

        device.delete();

        return ok();
    }

    @POST
    @Authenticated
    @Path("/{id}/config")
    public Response updateConfig(
            @PathParam("id") String deviceId,
            @QueryParam("regex") String regex,
            @QueryParam("interval") int interval
    )
    {
        Device device = Device.findById(new ObjectId(deviceId));
        if (device == null) return badRequest("This device does not exist.");

        if (!isAdmin() && !device.ownerId.equals(userId())) return forbidden();

        device.setConfig(regex, interval);

        return ok();
    }

    @GET
    @Path("/")
    public Response getDevices(@QueryParam("key") String key, @QueryParam("buildingId") String buildingId)
    {
        return key.equals("X9Nf2NqBKw3xtDNh")
        ? ok(Device.list("buildingId", buildingId))
        : forbidden();
    }

}
