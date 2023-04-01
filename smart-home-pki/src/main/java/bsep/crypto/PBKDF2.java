package bsep.crypto;

import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

public class PBKDF2 {

    public static String hash(String password, String salt) {
        var gen = new PKCS5S2ParametersGenerator();
        gen.init(password.getBytes(), salt.getBytes(), 1000);
        var key = (KeyParameter) gen.generateDerivedParameters(256);
        return Hex.toHexString(key.getKey());
    }

    public static boolean verify(String password, String salt, String hash) {
        return hash(password, salt).equals(hash);
    }

}
