package bsep.certificates;

import static bsep.util.Utils.coalesce;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.panache.common.Sort;

import org.apache.commons.lang3.time.DateUtils;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v2CRLBuilder;
import org.bouncycastle.jce.provider.X509CRLParser;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import org.bson.codecs.pojo.annotations.BsonId;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Set;

@MongoEntity
public class CRL extends PanacheMongoEntityBase {

    public static final CRLDistPoint distributionPoint = new CRLDistPoint(
        new DistributionPoint[] {
            new DistributionPoint(
                new DistributionPointName(
                    new GeneralNames(
                        new GeneralName(
                            GeneralName.uniformResourceIdentifier,
                            "http://localhost:8080/crls/latest.crl"
                        )
                    )
                ),
                null,
                null
            )
        }
    );

    public static final IssuingDistributionPoint issuingDistributionPoint = new IssuingDistributionPoint(
        new DistributionPointName(
            new GeneralNames(
                new GeneralName(
                    GeneralName.uniformResourceIdentifier,
                    "http://localhost:8080/crls/latest.crl"
                )
            )
        ),
        false, false, null, false, false
    );

    @BsonId
    public Long id;
    public byte[] x509Encoded;

    private X509CRL x509CRL;

    public X509CRL asX509() {
        if (this.x509CRL != null) return this.x509CRL;
        var parser = new X509CRLParser();
        try {
            parser.engineInit(new ByteArrayInputStream(this.x509Encoded));
            this.x509CRL = (X509CRL) parser.engineRead();
            return this.x509CRL;
        }
        catch (Exception e) { return null; }
    }

    public String asPEM() {
        return CertificateService.toPem(this.asX509());
    }

    public boolean isRevoked(BigInteger serialNumber) {
        if (this.asX509() == null || serialNumber == null) return false;
        return this.x509CRL.getRevokedCertificate(serialNumber) != null;
    }

    public static CRL getLatestNoCreate() {
        return findAll(Sort.descending("_id")).firstResult();
    }
    public static CRL getLatest() {
        if (!CRL.exists()) CRL.create();
        return CRL.getLatestNoCreate();
    }

    public static boolean exists() {
        return count() > 0;
    }

    public static boolean create() {
        return revokeCertificate(null, 0);
    }

    public static boolean revokeCertificate(BigInteger serialNumber, int reason) {
        var latestCRL = CRL.getLatestNoCreate();
        if (latestCRL != null && latestCRL.isRevoked(serialNumber)) return false;

        var nextCrlId = latestCRL == null ? 1 : latestCRL.id + 1;

        var rootCertificate = CertificateService.keyStore.getCertificate("ca-root");
        var rootPrivateKey = CertificateService.keyStore.getPrivateKey("ca-root", "Eoo7kXdxtOxU85YI4/w=");

        var now = new Date();
        var nextUpdate = DateUtils.addDays(now, 1);

        var crlBuilder = new JcaX509v2CRLBuilder(rootCertificate, now);
        try {
            var keyIdentifier = new JcaX509ExtensionUtils()
                .createAuthorityKeyIdentifier(rootCertificate.getPublicKey());

            crlBuilder.addExtension(Extension.cRLNumber, true, new ASN1Integer(nextCrlId));
            crlBuilder.addExtension(Extension.authorityKeyIdentifier, false, keyIdentifier);
            crlBuilder.addExtension(Extension.issuingDistributionPoint, false, CRL.issuingDistributionPoint);
            crlBuilder.setNextUpdate(nextUpdate);

            if (latestCRL != null) {
                var crlEntries =  coalesce(latestCRL.asX509().getRevokedCertificates(), Set.<X509CRLEntry>of());
                for (var entry : crlEntries) {
                    crlBuilder.addCRLEntry(
                        entry.getSerialNumber(),
                        entry.getRevocationDate(),
                        entry.getRevocationReason().ordinal()
                    );
                }
            }

            if (serialNumber != null) crlBuilder.addCRLEntry(serialNumber, new Date(), reason);

            var signer = new JcaContentSignerBuilder("SHA256withRSA").build(rootPrivateKey);
            var encodedCRL = crlBuilder.build(signer).getEncoded();

            var nextCRL = new CRL();
            nextCRL.id = nextCrlId;
            nextCRL.x509Encoded = encodedCRL;
            nextCRL.persist();

            return true;
        }
        catch (Exception e) { return false; }
    }

}