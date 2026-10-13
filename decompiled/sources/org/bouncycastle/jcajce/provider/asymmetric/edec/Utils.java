package org.bouncycastle.jcajce.provider.asymmetric.edec;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/Utils.class */
class Utils {
    Utils() {
    }

    static boolean isValidPrefix(byte[] bArr, byte[] bArr2) {
        if (bArr2.length < bArr.length) {
            return !isValidPrefix(bArr, bArr);
        }
        int i = 0;
        for (int i2 = 0; i2 != bArr.length; i2++) {
            i |= bArr[i2] ^ bArr2[i2];
        }
        return i == 0;
    }

    static String keyToString(String str, String str2, AsymmetricKeyParameter asymmetricKeyParameter) {
        byte[] encoded;
        StringBuffer stringBuffer = new StringBuffer();
        String strLineSeparator = Strings.lineSeparator();
        if (asymmetricKeyParameter instanceof X448PublicKeyParameters) {
            encoded = ((X448PublicKeyParameters) asymmetricKeyParameter).getEncoded();
        } else if (asymmetricKeyParameter instanceof Ed448PublicKeyParameters) {
            encoded = ((Ed448PublicKeyParameters) asymmetricKeyParameter).getEncoded();
        } else {
            encoded = asymmetricKeyParameter instanceof X25519PublicKeyParameters ? ((X25519PublicKeyParameters) asymmetricKeyParameter).getEncoded() : ((Ed25519PublicKeyParameters) asymmetricKeyParameter).getEncoded();
        }
        stringBuffer.append(str2).append(" ").append(str).append(" [").append(generateKeyFingerprint(encoded)).append("]").append(strLineSeparator).append("    public data: ").append(Hex.toHexString(encoded)).append(strLineSeparator);
        return stringBuffer.toString();
    }

    private static String generateKeyFingerprint(byte[] bArr) {
        return new Fingerprint(bArr).toString();
    }
}
