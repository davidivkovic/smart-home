package bsep.api;

import bsep.certificates.CRL;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("crls")
public class RevocationLists extends Resource {

    @GET
    @Path("/latest.crl")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getNewest() {
        if (!CRL.exists()) {
            var success = CRL.create();
            if (!success) return badRequest("Could not create the CRL. Please try again later.");
        }

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
