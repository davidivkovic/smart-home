package bsep.certificates;

import bsep.users.User;

import static bsep.util.Utils.Environment;
import static bsep.util.Utils.RootDir;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.util.*;
import java.io.*;
import java.security.*;
import java.math.BigInteger;
import java.util.stream.Stream;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;

public class CertificateService {

    private final String keystorePath;
    private final char[] keystorePassword;
    private final KeyStore store;

    public static CertificateService keyStore;
    public static CertificateService trustedStore;

    public static void init() {
        var password = Environment.getOrDefault("unified-password", "Eoo7kXdxtOxU85YI4/w=");

        keyStore = new CertificateService("keystore.p12", password);
        trustedStore = new CertificateService("truststore.p12", password);
    }

    private CertificateService(String keystoreName, String keystorePassword) {
        this.keystorePath = RootDir + "/pki/stores/" + keystoreName;
        this.keystorePassword = keystorePassword.toCharArray();

        try {
            this.store = KeyStore.getInstance("PKCS12");
            var file = new File(this.keystorePath);

            if (!file.exists()) {
                this.store.load(null, this.keystorePassword);
                this.persist();
            }

            this.store.load(new FileInputStream(file), this.keystorePassword);
        }
        catch (Exception e) { throw new RuntimeException("Error occurred while loading keystore " + keystorePath); }
    }

    public KeyStore getStore() {
        return this.store;
    }

    public X509Certificate getCertificate(String alias) {
        try { return (X509Certificate) this.store.getCertificate(alias); }
        catch (Exception e) { return null; }
    }

    public Map<String, String> getCertificates(
        long page,
        int pageSize,
        String aliasFilter,
        String userId,
        boolean isAdmin
    ) {
        var filterByAlias = aliasFilter.length() > 0;
        var aliasedCertificates = new HashMap<String, String>();
        var skip = page * pageSize;
        Enumeration<String> aliases;

        try {
            aliases = this.store.aliases();
        }
        catch (Exception e) { return aliasedCertificates; }

        while (aliases.hasMoreElements() && aliasedCertificates.size() < pageSize) {
            var alias = aliases.nextElement();
            if (filterByAlias && !alias.contains(aliasFilter)) continue;

            if (skip > 0) {
                skip--;
                continue;
            }

            var certificate = this.getCertificate(alias);
            if (certificate == null) continue;

            var subjectId = CSR.getRDN(new X500Name(certificate.getSubjectX500Principal().getName()), BCStyle.UID);
            if (!isAdmin && !subjectId.equals(userId)) continue;

            var pem = toPem(certificate);
            if (pem == null) continue;

            aliasedCertificates.put(alias, pem);
        }

        return aliasedCertificates;
    }

    public Certificate[] getCertificateChain(String alias) {
        try {
            var chain = this.store.getCertificateChain(alias);
            if (chain != null) return chain;
        }
        catch (Exception e) { /* Ignore */ }
        return new X509Certificate[] {};
    }

    public PrivateKey getPrivateKey(String alias, String password) {
        try { return (PrivateKey) this.store.getKey(alias, password.toCharArray()); }
        catch (Exception e) { return null; }
    }

    public boolean addCertificates(String alias, PrivateKey privateKey, Certificate[] chain) {
        try {
            this.store.setKeyEntry(alias, privateKey, this.keystorePassword, chain);
            if (this.persist()) return true;
            this.store.deleteEntry(alias);
            return false;
        }
        catch (Exception e) { return false; }
    }

    private boolean persist() {
        try {
            synchronized (this.store) {
                this.store.store(new FileOutputStream(this.keystorePath), this.keystorePassword);
                this.store.load(new FileInputStream(this.keystorePath), this.keystorePassword);
            }
            return true;
        }
        catch (Exception e) { return false; }
    }

    public List<String> aliases(String aliasPrefix) {
        try { return Collections.list(this.store.aliases()).stream().filter(a -> a.startsWith(aliasPrefix)).toList(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public static X509Certificate createCertificate(
            User requester,
            PKCS10CertificationRequest csr,
            KeyUsage keyUsage,
            ExtendedKeyUsage extendedKeyUsage,
            String subjectAlias,
            Date notBefore,
            Date notAfter,
            String issuerAlias,
            X509Certificate issuerCertificate
    ) {
        try {
            var issuerPrivateKey = (PrivateKey) CertificateService.keyStore.store.getKey(
                    issuerAlias, CertificateService.keyStore.keystorePassword
            );
            var issuerName = new JcaX509CertificateHolder(issuerCertificate).getSubject();

            var keygen = KeyPairGenerator.getInstance("RSA");
            var random = SecureRandom.getInstance("SHA1PRNG");
            keygen.initialize(2048, random);

            var contentSigner = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                    .setProvider("BC")
                    .build(issuerPrivateKey);

            var subjectBuilder = new X500NameBuilder(BCStyle.INSTANCE);
            for (var rdn : csr.getSubject().getRDNs())
                subjectBuilder.addRDN(rdn.getFirst());

            subjectBuilder.addRDN(BCStyle.UID, requester.id.toHexString());
            subjectBuilder.addRDN(BCStyle.GIVENNAME, requester.firstName);
            subjectBuilder.addRDN(BCStyle.SURNAME, requester.lastName);

            var subject = subjectBuilder.build();
            var publicKey = new JcaPEMKeyConverter().getPublicKey(csr.getSubjectPublicKeyInfo());
            var serialNumber = new BigInteger(158, random);

            X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(
                    issuerName,
                    serialNumber,
                    notBefore,
                    notAfter,
                    subject,
                    publicKey
            );

            certificateBuilder.addExtension(Extension.keyUsage, true, keyUsage);
            certificateBuilder.addExtension(Extension.extendedKeyUsage, false, extendedKeyUsage);
            certificateBuilder.addExtension(Extension.cRLDistributionPoints, false, CRL.distributionPoint);

            var certificateHolder = certificateBuilder.build(contentSigner);
            var certificateConverter = new JcaX509CertificateConverter().setProvider("BC");
            var certificate = certificateConverter.getCertificate(certificateHolder);

            certificate.checkValidity();
            certificate.verify(issuerCertificate.getPublicKey());

            var issuerChain = keyStore.store.getCertificateChain(issuerAlias);
            var chain = Stream
                    .of(new Certificate[] { certificate }, issuerChain)
                    .flatMap(Stream::of)
                    .toArray(Certificate[]::new);

            var success = CertificateService.keyStore.addCertificates(
                    subjectAlias,
                    issuerPrivateKey,
                    chain
            );

            return success ? certificate : null;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String toPem(Object BCObject) {
        var sw = new StringWriter();

        try (var pw = new JcaPEMWriter(sw)) { pw.writeObject(BCObject); }
        catch (IOException e) { return null; }

        return sw.toString().replaceAll("\\r", "");
    }

    public static String toPem(Certificate[] certificates) {
        var sw = new StringWriter();

        try (var pw = new JcaPEMWriter(sw)) {
            for (var certificate : certificates) { pw.writeObject(certificate); }
        }
        catch (IOException e) { return null; }

        return sw.toString().replaceAll("\\r", "");
    }

}
