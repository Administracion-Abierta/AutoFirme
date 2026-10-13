package es.gob.jmulticard.card.dnie;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/Dnie3UsrCwa14890Constants.class */
final class Dnie3UsrCwa14890Constants extends Dnie3r1Cwa14890Constants {
    private static final byte[] C_CV_IFD = {127, 33, -127, -51, 95, 55, -127, -128, -126, 91, 105, -58, 69, 30, 95, 81, 112, 116, 56, 95, 47, 23, -42, 77, -2, 46, 104, 86, 117, 103, 9, 75, 87, -13, -59, 120, -24, 48, -28, 37, 87, 45, -24, 40, -6, -12, -34, 27, 1, -61, -108, -29, 69, -62, -5, 6, 41, -93, -109, 73, 47, -108, -11, 112, -80, 11, 29, 103, 119, 41, -9, 85, -47, 7, 2, 43, -80, -95, 22, -31, -41, -41, 101, -99, -75, -60, -84, 13, -34, -85, 7, -1, 4, 95, 55, -75, -38, -15, 115, 43, 84, -22, -78, 56, -94, -50, 23, -55, 121, 65, -121, 117, -100, -22, -97, -110, -95, 120, 5, -94, 124, 16, 21, -20, 86, -52, 126, 71, 26, 72, -114, 111, 27, -111, -9, -86, 95, 56, 60, -83, -4, 18, -24, 86, -78, 2, 52, 106, -8, 34, 107, 26, -120, 33, 55, -36, 60, 90, 87, -16, -46, -127, 92, 31, -51, 75, -76, 111, -87, 21, 127, -33, -3, 121, -20, 58, 16, -88, 36, -52, -63, -21, 60, -32, -74, -76, 57, 106, -30, 54, 89, 0, 22, -70, 105, 0, 1, 0, 1, 66, 8, 101, 115, 83, 68, 73, 96, 0, 6};
    private static final byte[] CHR_C_CV_IFD = {32, 0, 0, 0, 0, 0, 0, 1};
    private static final RSAPrivateKey IFD_PRIVATE_KEY = new RSAPrivateKey() { // from class: es.gob.jmulticard.card.dnie.Dnie3UsrCwa14890Constants.1
        private static final long serialVersionUID = 6991556885804507378L;
        private final BigInteger ifdModulus = new BigInteger(1, new byte[]{-37, 44, -76, 30, 17, 43, -84, -6, 43, -41, -61, -45, -41, -106, 126, -124, -5, -108, 52, -4, 38, 31, -99, 9, 10, -119, -125, -108, 125, -81, -124, -120, -45, -33, -113, -67, -52, 31, -110, 73, 53, -123, -31, 52, -95, -76, 45, -27, 25, -12, 99, 36, 77, 126, -45, -124, -30, 109, 81, 108, -57, -92, -1, 120, -107, -79, -103, 33, 64, 4, 58, -84, -83, -4, 18, -24, 86, -78, 2, 52, 106, -8, 34, 107, 26, -120, 33, 55, -36, 60, 90, 87, -16, -46, -127, 92, 31, -51, 75, -76, 111, -87, 21, 127, -33, -3, 121, -20, 58, 16, -88, 36, -52, -63, -21, 60, -32, -74, -76, 57, 106, -30, 54, 89, 0, 22, -70, 105});
        private final BigInteger ifdPrivateExponent = new BigInteger(1, new byte[]{24, -76, 74, 61, 21, 92, 97, -21, -12, -29, 38, 28, -117, -79, 87, -29, 111, 99, -2, 48, -23, -81, 40, -119, 43, 89, -30, -83, -21, 24, -52, -116, -117, -83, 40, 75, -111, 101, -127, -100, -92, -34, -55, 74, -96, 107, 105, -68, -24, 23, 6, -47, -63, -74, 104, -21, 18, -122, -107, -27, -9, -2, -34, 24, -87, 8, -93, 1, 26, 100, 106, 72, 29, 62, -89, 29, -118, 56, 125, 71, 70, 9, -67, 87, -88, -126, -79, -126, -32, 71, -34, -128, -32, 75, 66, 33, 65, 107, -45, -99, -6, 31, -84, 3, 0, 100, 25, 98, -83, -79, 9, -30, -116, -81, 80, 6, 27, 104, -55, -54, -67, -101, 0, 49, 60, 15, 70, -19});

        public String toString() {
            return "Clave privada RSA del certificado de Terminal para DNIe 3.0 (canal de usuario)";
        }

        @Override // java.security.interfaces.RSAKey
        public BigInteger getModulus() {
            return this.ifdModulus;
        }

        @Override // java.security.Key
        public String getFormat() {
            return "PKCS#8";
        }

        @Override // java.security.Key
        public byte[] getEncoded() {
            throw new UnsupportedOperationException();
        }

        @Override // java.security.Key
        public String getAlgorithm() {
            return "RSA";
        }

        @Override // java.security.interfaces.RSAPrivateKey
        public BigInteger getPrivateExponent() {
            return this.ifdPrivateExponent;
        }
    };

    Dnie3UsrCwa14890Constants() {
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public byte[] getCCvIfd() {
        return C_CV_IFD;
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public byte[] getChrCCvIfd() {
        return CHR_C_CV_IFD;
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PrivateConstants
    public RSAPrivateKey getIfdPrivateKey() {
        return IFD_PRIVATE_KEY;
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public int getIfdKeyLength() {
        return 128;
    }
}
