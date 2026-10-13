package es.gob.jmulticard.card.dnie;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/Dnie3r2PinCwa14890Constants.class */
final class Dnie3r2PinCwa14890Constants extends Dnie3r2Cwa14890Constants {
    private static final byte[] C_CV_IFD = {127, 33, -127, -51, 95, 55, -127, -128, 10, 61, -76, -47, 87, -104, -14, 52, -10, 49, -3, -108, -55, 29, 42, 99, 99, -48, -31, -114, 27, 86, -38, -67, -26, 34, -68, 32, 31, -41, -57, -1, 89, -1, 102, -38, 110, 67, 79, -30, -9, -12, 110, 66, -28, -90, 6, -22, -126, 57, -84, 26, -61, 12, 125, -83, -30, 41, 101, -33, 96, 109, 17, 94, 4, -56, -17, -4, 119, 43, -113, 93, 72, 119, 62, 52, -107, 95, 51, -12, 100, -19, -123, -52, 14, -79, -68, 87, 42, -6, -70, 71, 37, -5, -11, -67, -49, 29, -116, 56, -55, -2, -100, -40, 83, 111, 52, 11, -50, 20, 29, -11, 24, 127, -94, -30, 55, 45, 115, -68, 127, -119, 72, 53, 12, -70, -34, -14, 95, 56, 60, 13, -52, -120, -115, 71, -106, 84, 63, 3, 37, 79, 78, 44, -33, -104, -79, -31, 38, 17, -29, -104, 31, 83, 51, -33, -104, -56, -122, 1, -109, 117, -124, 15, -84, 97, -37, -113, 27, -93, -75, 67, -36, -22, 61, 5, -98, 106, 65, 79, 109, -46, -97, -57, -55, -99, -117, 0, 1, 0, 1, 66, 8, 101, 115, 83, 68, 73, 98, 0, 24};
    private static final byte[] CHR_C_CV_IFD = {-48, 2, -32, 0, 0, 0, 0, 4};
    private static final RSAPrivateKey IFD_PRIVATE_KEY = new RSAPrivateKey() { // from class: es.gob.jmulticard.card.dnie.Dnie3r2PinCwa14890Constants.1
        private static final long serialVersionUID = 6991556885804507378L;
        private final BigInteger ifdModulus = new BigInteger(1, new byte[]{-33, 3, -109, 13, 79, 29, -105, 21, -21, -80, 15, -67, -82, 72, -81, -100, -99, -65, -42, -103, -54, -80, -67, -66, 92, -37, 1, 52, 0, 14, 70, 46, 113, 58, -23, 122, 47, 126, 32, -81, -65, -124, -45, -50, 115, 79, -30, 21, 117, 122, -81, -95, -24, -98, 100, 87, -22, -30, -24, 8, 17, 3, 115, -30, 86, 86, 52, -108, -5, 93, 16, 79, 13, -52, -120, -115, 71, -106, 84, 63, 3, 37, 79, 78, 44, -33, -104, -79, -31, 38, 17, -29, -104, 31, 83, 51, -33, -104, -56, -122, 1, -109, 117, -124, 15, -84, 97, -37, -113, 27, -93, -75, 67, -36, -22, 61, 5, -98, 106, 65, 79, 109, -46, -97, -57, -55, -99, -117});
        private final BigInteger ifdPrivateExponent = new BigInteger(1, new byte[]{-122, 111, 15, 44, 12, -81, 23, -82, 125, 30, -22, -66, 58, -37, 82, 17, 36, -2, -55, -116, 119, -92, -57, 28, -125, -72, -7, 38, -79, -119, -23, 64, -127, -67, 51, -107, 22, 31, -1, -16, 49, -111, 14, 100, -5, 26, 2, 125, 81, 14, 29, -27, -119, -26, 65, 50, -58, 66, -10, 0, 54, -47, 79, -2, -43, -48, -50, 31, 69, -25, 17, 111, 19, -60, -26, 56, -114, 37, -35, 67, -125, 87, 120, 5, -123, 115, -36, 41, -83, 106, 55, 50, 113, 109, 8, 17, 36, -73, 82, 81, 64, -79, -35, -85, -30, 81, -92, -104, 12, -59, -64, 58, -122, -88, 45, 23, 79, -73, -88, 29, 36, -115, 124, -86, 43, 61, 97, -47});

        public String toString() {
            return "Clave privada RSA del certificado de Terminal para DNIe 3.0 R2 (canal de PIN)";
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

    Dnie3r2PinCwa14890Constants() {
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
