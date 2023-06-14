package bsep.api.dto.pki;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;

import jakarta.validation.constraints.*;
import java.util.Set;

public class ApproveSigningRequest {

    /**
     * 2.5.29.15 - Key Usage
     * For available values, see {@link org.bouncycastle.asn1.x509.KeyUsage}.
     */
    @NotNull
    @Size(min = 1, max = 7)
    public Set<@Pattern(regexp = "^[012345678]$") String> keyUsage;
    /**
     * 2.5.29.15 - Extended Key Usage
     * 1.3.6.1.5.5.7.3.x - Key Purpose Id
     * For available values, see {@link org.bouncycastle.asn1.x509.KeyPurposeId}.
     */
    @NotNull
    @Size(max = 6)
    public Set<@Pattern(regexp = "^[123489]$") String> extendedKeyUsage;

    @NotBlank
    @Size(min = 1, max = 128)
    @Pattern(regexp = "^[0-9a-z.\\-]+$")
    public String subjectAlias;

    @NotBlank
    @Size(min = 1, max = 128)
    @Pattern(regexp = "^ca-[0-9a-z.\\-]+$")
    public String issuerAlias;

    @Min(1)     // 1 hour
    @Max(26280) // 3 years
    public int hoursValid;

    @JsonIgnore
    public KeyUsage extractKeyUsage() {
        return new KeyUsage(keyUsage
                .stream()
                .map(Integer::parseInt)
                .reduce(0, (acc, usage) -> acc | 1 << (8 - usage + 15) % 16)
        );
    }

    @JsonIgnore
    public ExtendedKeyUsage extractExtendedKeyUsage() {
        return new ExtendedKeyUsage(extendedKeyUsage
                .stream()
                .map(u -> "1.3.6.1.5.5.7.3." + u)
                .map(ASN1ObjectIdentifier::new)
                .map(KeyPurposeId::getInstance)
                .toArray(KeyPurposeId[]::new)
        );
    }

}
