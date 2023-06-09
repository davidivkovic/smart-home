package bsep.api;

import bsep.users.User;
import bsep.certificates.CSR;
import bsep.certificates.CertificateService;
import bsep.api.dto.pki.ApproveSigningRequest;

import io.quarkus.panache.common.Sort;
import io.quarkus.security.Authenticated;

import org.apache.commons.lang3.time.DateUtils;
import org.bson.types.ObjectId;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.security.cert.X509Certificate;
import java.util.Date;

@Path("csrs")
public class SigningRequests extends Resource {

    @Inject
    Validator validator;

    @POST
    @Path("/")
    @Authenticated
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submit(@NotBlank @Size(max = 32000) String pemCsr) {

        User user = User.findById(new ObjectId(userId()));
        if (user == null) return badRequest("Could not find the user associated with this request.");

        var csr = CSR.fromPem(pemCsr, user);
        if (csr == null) return badRequest("Could not parse the PEM formatted certificate signing request.");

        var validation = validator.validate(csr);
        if (validation.size() > 0) return validationProblem(validation);

        csr.persist();

        return ok(csr);
    }

    @GET
    @Path("/")
    @Authenticated
    public Response getAll(@QueryParam("page") @NotNull @Min(1) int page) {
        var CSRs = isAdmin()
            ? CSR.findAll(Sort.by("requestedAt").descending()).page(page - 1, 4).list()
            : CSR.find("userId", Sort.by("requestedAt").descending(), userId()).page(page - 1, 4).list();
        return ok(CSRs);
    }

    @GET
    @Path("/{id}")
    @Authenticated
    public Response get(@PathParam("id") @NotBlank @Size(max = 100) String id) {

        var csr = CSR.findById(new ObjectId(id));
        if (csr == null) return notFound();

        return ok(csr);
    }

    @POST
    @Path("/{id}/approve")
    @RolesAllowed({ User.Roles.ADMIN })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    public Response approve(
        @PathParam("id") @NotBlank @Size(max = 100) String id,
        @Valid @NotNull ApproveSigningRequest request
    ) {
        CSR csr = CSR.findById(new ObjectId(id));

        if (csr == null) return notFound();
        if (csr.isProcessed()) return badRequest("The certificate signing request has already been processed.");

        var csrPKCS = csr.asPKCS();
        if (csrPKCS == null) return badRequest("Could not parse the certificate signing request.");

        User requester = User.findById(new ObjectId(csr.userId));
        if (requester == null) {
            return badRequest("Could not find the user associated with the certificate signing request.");
        }

        if (!requester.isAdmin() && request.subjectAlias.startsWith("ca-")) {
            return badRequest("Only administrators can request CA certificates.");
        };

        var issuer = (X509Certificate) CertificateService.keyStore.getCertificate(request.issuerAlias);
        if (issuer == null) return badRequest("Could not find the issuer certificate.");

        var certificate = CertificateService.createCertificate(
                requester,
                csrPKCS,
                request.extractKeyUsage(),
                request.extractExtendedKeyUsage(),
                request.subjectAlias,
                new Date(),
                DateUtils.addHours(new Date(), request.hoursValid),
                request.issuerAlias,
                issuer
        );

        if (certificate == null) return badRequest("Could not process the certificate signing request.");

        var chain = CertificateService.keyStore.getCertificateChain(request.subjectAlias);
        if (chain.length == 0) return badRequest("Could not build the certificate chain for the subject alias.");

        var pem = CertificateService.toPem(chain);
        if (pem == null) return badRequest("Could not convert the certificate chain to the PEM format.");

        csr.pemCertificate = pem;
        csr.certificateAlias = request.subjectAlias;

        csr.approve();
        csr.persistOrUpdate();

        return ok(pem, MediaType.TEXT_PLAIN);
    }

    @POST
    @Path("/{id}/reject")
    @RolesAllowed({ User.Roles.ADMIN })
    @Produces(MediaType.TEXT_PLAIN)
    public Response reject(
        @PathParam("id") @NotBlank @Size(max = 100) String id,
        @QueryParam("reason") @Size(max = 1024) String reason
    ) {
        CSR csr = CSR.findById(new ObjectId(id));

        if (csr == null) return notFound();
        if (csr.isProcessed()) return badRequest("The certificate signing request has already been processed.");

        csr.reject(reason);
        csr.persistOrUpdate();

        return ok();
    }

}
