package bsep.certificates;

import bsep.users.User;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.*;
import java.time.LocalDate;

@RegisterForReflection
public class CSR extends PanacheMongoEntity {

    public enum Status {
        Pending,
        Approved,
        Rejected
    }

    public String pemCSR;
    public String pemCertificate;
    public String userId;
    public LocalDate requestedAt;
    public LocalDate processedAt;
    public Status status;
    public String rejectionReason;
    public byte[] publicKey;
    public String algorithm;
    public String certificateAlias;

    @NotNull
    @Size(min = 1, max = 64)
    public String commonName;

    @NotNull
    @Size(min = 1, max = 64)
    public String givenName;

    @NotNull
    @Size(min = 1, max = 64)
    public String surname;

    @Email
    @NotNull
    @Size(min = 1, max = 255)
    public String email;

    @NotNull
    @Size(min = 1, max = 64)
    public String organization;

    @NotNull
    @Size(min = 1, max = 64)
    public String organizationUnit;

    @NotNull
    @Size(min = 1, max = 128)
    public String locality;

    @NotNull
    @Size(min = 1, max = 128)
    public String state;

    @NotNull
    @Size(min = 2, max = 2)
    public String country;

    protected CSR() {}

    public PKCS10CertificationRequest asPKCS() {
        try { return (PKCS10CertificationRequest) new PEMParser(new StringReader(this.pemCSR)).readObject(); }
        catch (Exception e) { return null; }
    }

    public static String getRDN(X500Name subject, ASN1ObjectIdentifier rdn) {
        var RDNs = subject.getRDNs(rdn);
        return RDNs != null && RDNs.length > 0 ? RDNs[0].getFirst().getValue().toString().trim() : "";
    }

    public static CSR fromPem(String pemCsr, User user) {
        var csr = new CSR();

        csr.userId = user.id.toHexString();
        csr.requestedAt = LocalDate.now();
        csr.status = Status.Pending;
        csr.pemCSR = pemCsr;

        var request = csr.asPKCS();
        if (request == null) return null;

        var publicKey = request.getSubjectPublicKeyInfo();
        try {
            csr.publicKey = publicKey.getEncoded();
            csr.algorithm = publicKey.getAlgorithm().getAlgorithm().getId();

            var verifier = new JcaContentVerifierProviderBuilder().build(publicKey);
            if (!request.isSignatureValid(verifier)) return null;
        }
        catch (Exception e) { return null; }

        var subject = request.getSubject();

        csr.commonName         = getRDN(subject, BCStyle.CN).trim();
//        csr.givenName          = getRDN(subject, BCStyle.GIVENNAME).trim();
//        csr.surname            = getRDN(subject, BCStyle.SURNAME).trim();
        csr.email              = getRDN(subject, BCStyle.E).trim();
        csr.organization       = getRDN(subject, BCStyle.O).trim();
        csr.organizationUnit   = getRDN(subject, BCStyle.OU).trim();
        csr.locality           = getRDN(subject, BCStyle.L).trim();
        csr.state              = getRDN(subject, BCStyle.ST).trim();
        csr.country            = getRDN(subject, BCStyle.C).trim();

        csr.givenName          = user.firstName;
        csr.surname            = user.lastName;

        return csr;
    }

    public boolean isProcessed() {
        return this.processedAt != null;
    }

    public void approve() {
        this.status = Status.Approved;
        this.processedAt = LocalDate.now();
    }

    public void reject(String reason) {
        this.status = Status.Rejected;
        this.processedAt = LocalDate.now();
        this.rejectionReason = reason;
    }

}