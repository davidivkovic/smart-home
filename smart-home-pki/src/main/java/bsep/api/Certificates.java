package bsep.api;

import bsep.certificates.CRL;
import bsep.certificates.CSR;
import bsep.certificates.CertificateService;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.CRLReason;

import javax.validation.constraints.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/certs")
public class Certificates extends Resource {

    @GET
    @Path("/")
    public Response getAll(
        @QueryParam("page") @NotNull @Min(1) int page,
        @QueryParam("storeType") @NotBlank @Pattern(regexp = "^(keystore|trusted)$") String storeType,
        @QueryParam("alias") @Size(max = 128) String alias
    ) {
        alias = alias != null && alias.trim().isEmpty() ? alias : "";

        var store= storeType.equals("trusted") ? CertificateService.trustedStore : CertificateService.keyStore;
        var certificates = store
                .getCertificates(page - 1, 4, alias)
                .stream()
                .map(CertificateService::toPem)
                .filter(Objects::nonNull)
                .toList();

        return ok(certificates);
    }

    @GET
    @Path("/ca")
    public Response getCA() {

        var aliasedCertificates = new HashMap<String, String>();
        for (var alias : CertificateService.keyStore.aliases("ca-")) {

            var certificate = CertificateService.keyStore.getCertificate(alias);
            if (certificate == null) continue;

            var isCA = certificate.getBasicConstraints() != -1;
            if (!isCA) continue;

            var pem = CertificateService.toPem(certificate);
            if (pem == null) continue;

            aliasedCertificates.put(alias, pem);
        }

        return ok(aliasedCertificates);
    }

    // TODO: Check if the user is authorized to see the certificate
    @GET
    @Path("/{alias}")
    public Response get(@PathParam("alias") @Size(max = 128) String alias) {

        var certificate = CertificateService.keyStore.getCertificate(alias);
        if (certificate == null) return notFound();

        var subjectId = CSR.getRDN(new X500Name(certificate.getSubjectX500Principal().getName()), BCStyle.UID);
        if (!isAdmin() && !subjectId.equals(userId())) return notFound();

        var chain = CertificateService.keyStore.getCertificateChain(alias);
        if (chain.length == 0) return badRequest("Could not find the certificate chain for the subject alias.");

        var pem = CertificateService.toPem(chain);
        if (pem == null) return badRequest("Could not convert the certificate chain to the PEM format.");

        return ok(pem, MediaType.TEXT_PLAIN);
    }

    @GET
    @Path("/check-validity")
    public Response checkValidity(
        @QueryParam("aliases") @NotNull @Size(min = 1, max = 4)
        Set<@NotBlank @Pattern(regexp = "^[0-9a-z.\\-]*$") @Size(max = 128) String> aliases
    ) {
        var crl = CRL.getLatest();
        if (crl == null) return badRequest("Could not find the latest CRL. Please try again later.");

        var certificates = aliases.stream()
            .map(CertificateService.keyStore::getCertificate)
            .filter(Objects::nonNull)
            .toList();

        var unauthorized = certificates.stream().anyMatch(c -> {
            var subject = new X500Name(c.getSubjectX500Principal().getName());
            var subjectId = CSR.getRDN(subject, BCStyle.UID);
            return !isAdmin() && !subjectId.equals(userId());
        });

        if (unauthorized || certificates.size() != aliases.size()) return badRequest();

        var validity = aliases.stream().collect(Collectors.toMap(
            alias -> alias,
            alias -> {
                var certificate = CertificateService.keyStore.getCertificate(alias);
                return CertificateService.isValid(certificate) && !crl.isRevoked(certificate.getSerialNumber());
            }
        ));

        return ok(validity);
    }

    /**
     * For available revocation reasons check the {@link CRLReason} class.
     */
    @POST
    @Path("/{serial-number}/revoke")
    @Produces(MediaType.TEXT_PLAIN)
    public Response revoke(
        @PathParam("serial-number") @NotBlank @Size(max = 128) @Pattern(regexp = "^[0-9]+$") String serialNumber,
        @QueryParam("reason") @NotNull @Min(0) @Max(10) int reason
    ) {
        boolean success;
        synchronized (CRL.class) { success = CRL.revokeCertificate(new BigInteger(serialNumber), reason); }
        return success ? ok() : badRequest("Could not revoke the certificate. Please try again later.");
    }

}
