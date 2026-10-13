package es.gob.jmulticard.card.dnie;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/Dnie3PinCwa14890Constants.class */
final class Dnie3PinCwa14890Constants extends Dnie3r1Cwa14890Constants {
    private static final byte[] C_CV_IFD = {127, 33, -127, -51, 95, 55, -127, -128, 105, -60, -28, -108, -16, 8, -30, 66, 20, -79, -63, 49, -74, 31, -50, -100, 21, -6, 60, -80, 97, -35, 111, 2, -40, -94, -51, 48, -41, 47, -74, -33, -119, -102, -15, 91, 113, 120, 33, -65, -79, -81, 125, 117, -123, 1, 109, -116, 54, -81, 74, -62, -96, -80, -59, 42, -42, 91, 105, 37, 103, 49, -61, 77, 89, 2, 14, -121, -85, 115, -94, 48, -6, 105, -18, -126, -77, 58, 49, -33, 4, 12, -23, 15, 10, -4, 58, 17, 29, 53, -38, -107, 102, -88, -51, -85, -22, 14, 63, 117, -108, -60, 64, -45, 116, 80, 122, -108, 53, 87, 89, -77, -98, -59, -27, -4, -72, 3, -115, 121, 61, 95, -101, -88, -75, -79, 11, 112, 95, 56, 60, 76, -122, -111, -57, -66, 47, -40, -63, 35, 102, 14, -104, 101, -31, 79, 25, -33, -5, -73, -1, 56, 8, -55, -14, 4, -25, -105, -48, 109, -40, 51, 58, -59, -125, -122, -18, 78, -74, 30, 32, -20, -89, -17, 56, -43, -80, 94, -79, 21, -106, 106, 90, -119, -83, 88, -91, 0, 1, 0, 1, 66, 8, 101, 115, 83, 68, 73, 96, 0, 6};
    private static final byte[] CHR_C_CV_IFD = {64, 0, 0, 0, 0, 0, 0, 1};
    private static final RSAPrivateKey IFD_PRIVATE_KEY = new RSAPrivateKey() { // from class: es.gob.jmulticard.card.dnie.Dnie3PinCwa14890Constants.1
        private static final long serialVersionUID = 6991556885804507378L;
        private final BigInteger ifdModulus = new BigInteger(1, new byte[]{-12, 39, -105, -115, -95, 89, -70, 2, 121, 48, -118, 108, 106, -119, 80, 90, -38, 90, 103, -61, -38, 38, 121, -22, -12, -95, -80, 17, -98, -35, 77, -12, 110, 120, 4, 36, 113, -87, -47, 48, 29, 63, -78, -113, 56, -59, 125, 8, -119, -9, 49, -37, -114, -35, -68, 19, 103, -63, 52, -31, -23, 71, 120, 107, -114, -56, -28, -71, -54, 106, -89, -62, 76, -122, -111, -57, -66, 47, -40, -63, 35, 102, 14, -104, 101, -31, 79, 25, -33, -5, -73, -1, 56, 8, -55, -14, 4, -25, -105, -48, 109, -40, 51, 58, -59, -125, -122, -18, 78, -74, 30, 32, -20, -89, -17, 56, -43, -80, 94, -79, 21, -106, 106, 90, -119, -83, 88, -91});
        private final BigInteger ifdPrivateExponent = new BigInteger(1, new byte[]{-46, 122, 3, 35, 124, 114, 46, 113, -115, 105, -12, 26, -20, 104, -67, -107, -28, -32, -60, -51, 73, 21, -100, 74, -103, 99, 125, -74, 98, -2, -93, 2, 81, -19, 50, -100, -4, 67, -119, -21, 113, 123, -123, 2, 4, -51, -13, 48, -42, 70, -4, 123, 43, 25, 41, -42, -116, -66, 57, 73, 123, 98, 58, -126, -57, 100, 26, -61, 72, 121, 87, 61, -22, 13, -85, -57, -54, 48, -102, -28, -77, -19, -38, -6, -18, 85, -43, 66, -9, -128, 35, 3, 81, -25, 94, 127, 50, -36, 101, 46, -15, -19, 71, -91, 28, 24, -39, -33, -97, -12, -115, -121, -115, -74, 34, -22, 110, -109, 112, -23, -58, 59, 53, -117, 124, 17, 90, -95});

        public String toString() {
            return "Clave privada RSA del certificado de Terminal para DNIe 3.0 (canal de PIN)";
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

    Dnie3PinCwa14890Constants() {
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
