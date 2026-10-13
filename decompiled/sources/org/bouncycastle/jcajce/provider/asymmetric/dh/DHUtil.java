package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dh/DHUtil.class */
class DHUtil {
    DHUtil() {
    }

    static String privateKeyToString(String str, BigInteger bigInteger, DHParameters dHParameters) {
        StringBuffer stringBuffer = new StringBuffer();
        String strLineSeparator = Strings.lineSeparator();
        BigInteger bigIntegerModPow = dHParameters.getG().modPow(bigInteger, dHParameters.getP());
        stringBuffer.append(str);
        stringBuffer.append(" Private Key [").append(generateKeyFingerprint(bigIntegerModPow, dHParameters)).append("]").append(strLineSeparator);
        stringBuffer.append("              Y: ").append(bigIntegerModPow.toString(16)).append(strLineSeparator);
        return stringBuffer.toString();
    }

    static String publicKeyToString(String str, BigInteger bigInteger, DHParameters dHParameters) {
        StringBuffer stringBuffer = new StringBuffer();
        String strLineSeparator = Strings.lineSeparator();
        stringBuffer.append(str);
        stringBuffer.append(" Public Key [").append(generateKeyFingerprint(bigInteger, dHParameters)).append("]").append(strLineSeparator);
        stringBuffer.append("             Y: ").append(bigInteger.toString(16)).append(strLineSeparator);
        return stringBuffer.toString();
    }

    private static String generateKeyFingerprint(BigInteger bigInteger, DHParameters dHParameters) {
        return new Fingerprint(Arrays.concatenate(bigInteger.toByteArray(), dHParameters.getP().toByteArray(), dHParameters.getG().toByteArray())).toString();
    }
}
