package bsep.api;

import bsep.certificates.CRL;
import bsep.certificates.CSR;
import bsep.certificates.CertificateService;

import io.quarkus.security.Authenticated;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.cert.*;
import java.util.*;
import java.util.stream.Collectors;

@Path("/certs")
public class Certificates extends Resource {

    @GET
    @Path("/")
    @Authenticated
    public Response getAll(
        @QueryParam("page") @NotNull @Min(1) int page,
        @QueryParam("storeType") @NotBlank @Pattern(regexp = "^(keystore|truststore)$") String storeType,
        @QueryParam("alias") @Size(max = 128) String alias
    ) {
        alias = alias != null && !alias.trim().isEmpty() ? alias : "";

        var store= storeType.equals("truststore") ? CertificateService.trustedStore : CertificateService.keyStore;
        var certificates = store.getCertificates(page - 1, 4, alias, userId(), isAdmin());

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

    @GET
    @Authenticated
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

    @POST
    @Path("/check-validity")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkValidity(@NotBlank @Size(max = 32000) String pemChain) {
        var crl = CRL.getLatest();
        if (crl == null) return badRequest("Could not find the latest CRL. Please try again later.");

        var certificates = new ArrayList<X509Certificate>();
        try {
            X509CertificateHolder certHolder;
            var pemParser = new PEMParser(new StringReader(pemChain));

            while ((certHolder = (X509CertificateHolder) pemParser.readObject()) != null) {
                var certConverter = new JcaX509CertificateConverter();
                certificates.add(certConverter.getCertificate(certHolder));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return badRequest("Could not parse the PEM chain.");
        }
        try {
            var certificateFactory = CertificateFactory.getInstance("X.509");
            var chain = certificateFactory.generateCertPath(certificates);

            var params = new PKIXParameters(CertificateService.trustedStore.getStore());
            params.setRevocationEnabled(false);
            var validator = CertPathValidator.getInstance("PKIX", "BC");
            validator.validate(chain, params);
        }
        catch (Exception e) {
            e.printStackTrace();
            return badRequest("The certificate chain is broken.");
        }

        boolean chainBroken = false;
        var validity = new HashMap<String, Boolean>();
        Collections.reverse(certificates);

        for (var certificate : certificates) {
            var hex = certificate.getSerialNumber().toString(16);
            var serialNumber = hex.length() % 2 == 0 ? hex : "0" + hex;

            if (chainBroken) {
                validity.put(serialNumber, false);
                continue;
            }

            var isRevoked = crl.isRevoked(certificate.getSerialNumber());
            if (isRevoked) chainBroken = true;

            validity.put(serialNumber, !isRevoked);
        }

        return ok(validity);
    }

    /**
     * For available revocation reasons check the {@link CRLReason} class.
     */
    @POST
    @Path("/{serial-number}/revoke")
    @Produces(MediaType.TEXT_PLAIN)
    public Response revoke(
        @PathParam("serial-number") @NotBlank @Size(max = 128) @Pattern(regexp = "^[0-9a-zA-Z]+$") String serialNumber,
        @QueryParam("reason") @NotNull @Min(0) @Max(10) int reason
    ) {
        boolean success;
        synchronized (CRL.class) { success = CRL.revokeCertificate(new BigInteger(serialNumber, 16), reason); }
        return success ? ok() : badRequest("Could not revoke the certificate. Please try again later.");
    }

}
