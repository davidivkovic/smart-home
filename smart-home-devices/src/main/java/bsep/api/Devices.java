package bsep.api;

import bsep.api.dto.devices.AddDeviceRequest;
import bsep.devices.Device;
import io.quarkus.security.Authenticated;
import org.bson.types.ObjectId;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Devices extends Resource {

    @GET
    @Path("/")
    public Response getDeviceTypes()
    {
        return ok(Device.devices);
    }

    @POST
    @Path("/")
//    @Authenticated
    public Response addDevice(@Valid AddDeviceRequest request)
    {
        var deviceType = Device.getTypeById(request.deviceTypeId);
        if (deviceType == null) return badRequest("This device type does not exist.");

        var device = new Device(request.deviceName, "userid", request.buildingId, deviceType);
        device.persist();

        return ok(device);
    }

    @GET
    @Path("/{id}")
    public Response getDevice(@PathParam("id") String deviceId)
    {
        Device device = Device.findById(new ObjectId(deviceId));
        if (device == null) return badRequest("This device does not exist.");

        return ok(device);
    }

    @DELETE
    @Path("/{id}")
    public Response removeDevice(@PathParam("id") String deviceId)
    {
        Device device = Device.findById(new ObjectId(deviceId));
        if (device == null) return badRequest("This device does not exist.");

        device.delete();

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
