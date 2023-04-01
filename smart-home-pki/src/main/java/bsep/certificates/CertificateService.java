package bsep.certificates;

import static bsep.util.Utils.Environment;
import static bsep.util.Utils.RootDir;

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

    public static final CertificateService keyStore;
    public static final CertificateService trustedStore;

    static {
        var password = Environment.getOrDefault("unified-password", "Eoo7kXdxtOxU85YI4/w=");

        keyStore = new CertificateService("keystore.p12", password);
        trustedStore = new CertificateService("truststore.p12", password);
    }

    public static void init() { /* This method is called to initialize the static fields */ }

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

    public X509Certificate getCertificate(String alias) {
        try { return (X509Certificate) this.store.getCertificate(alias); }
        catch (Exception e) { return null; }
    }

    public List<X509Certificate> getCertificates(long page, int pageSize, String alias) {
        var filterByAlias = alias.length() > 0;
        try {
            return Collections
                    .list(this.store.aliases())
                    .stream()
                    .filter(a -> !filterByAlias || a.contains(alias))
                    .map(this::getCertificate)
                    .filter(Objects::nonNull)
                    .skip(page * pageSize)
                    .limit(pageSize)
                    .toList();
        }
        catch (Exception e) { return Collections.emptyList(); }
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

            var subject = csr.getSubject();
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
            certificateBuilder.addExtension(Extension.issuingDistributionPoint, false, CRL.distributionPoint);

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

    public static boolean isValid(X509Certificate certificate) {
        if (certificate == null) return false;
        try {
            certificate.checkValidity();
            return true;
        }
        catch (Exception e) { return false; }
    }

    public static String toPem(Object BCObject) {
        var sw = new StringWriter();

        try (var pw = new JcaPEMWriter(sw)) { pw.writeObject(BCObject); }
        catch (IOException e) { return null; }

        return sw.toString();
    }

    public static String toPem(Certificate[] certificates) {
        var sw = new StringWriter();

        try (var pw = new JcaPEMWriter(sw)) {
            for (var certificate : certificates) { pw.writeObject(certificate); }
        }
        catch (IOException e) { return null; }

        return sw.toString();
    }

}
