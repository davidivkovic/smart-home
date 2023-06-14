package bsep.api;

import bsep.certificates.CRL;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("crls")
public class RevocationLists extends Resource {

    @GET
    @Path("/latest.crl")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getNewest() {

        var crl = CRL.getLatest();
        if (crl == null || crl.asX509() == null) {
            return badRequest("Could not find the newest CRL. Please try again later.");
        }

        var pem = crl.asPEM();
        if (pem == null) {
            return badRequest("Could not convert the newest CRL to PEM format. Please try again later.");
        }

        var filename = String.format("smart-home-%s.crl", crl.id.toString());
        return attachment(pem, filename, MediaType.TEXT_PLAIN);
    }

    @GET
    @Path("/{id}.crl")
    @Produces(MediaType.TEXT_PLAIN)
    public Response get(@PathParam("id") @NotNull Long id) {
        CRL crl = CRL.findById(id);
        if (crl == null || crl.asX509() == null) return notFound();

        var pem = crl.asPEM();
        if (pem == null) {
            return badRequest("Could not convert the newest CRL to PEM format. Please try again later.");
        }

        var filename = String.format("smart-home-%s.crl", crl.id.toString());
        return attachment(pem, filename, MediaType.TEXT_PLAIN);
    }

}
