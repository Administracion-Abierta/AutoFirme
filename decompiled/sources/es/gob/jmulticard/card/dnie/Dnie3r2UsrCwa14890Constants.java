package es.gob.jmulticard.card.dnie;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/Dnie3r2UsrCwa14890Constants.class */
final class Dnie3r2UsrCwa14890Constants extends Dnie3r2Cwa14890Constants {
    private static final byte[] C_CV_IFD = {127, 33, -127, -51, 95, 55, -127, -128, 93, -87, 75, 107, 78, -72, 97, -20, -90, 54, -46, 103, 57, 116, 113, 31, 85, 99, 15, 91, -119, 3, -116, 87, -48, -69, -69, -63, -46, -58, -116, -61, -21, 86, -43, 48, 56, 0, -11, -87, -11, -30, -106, 127, -33, 40, -111, 123, -81, -56, -121, 99, -72, -20, 44, 14, -66, 122, -53, 11, -92, -81, -65, -26, 109, -78, -95, -19, -95, 62, 69, 100, -9, -114, 101, 88, 110, 81, 1, 118, -15, 28, 76, -103, 54, 74, -81, 24, -105, -47, 27, -7, -114, -99, 29, 10, 18, -48, 106, -85, 117, 118, 74, -88, -36, -123, -115, -16, -16, 3, -21, -117, 75, 59, 86, -11, -7, 95, -90, 55, 83, 117, 25, -28, -58, 85, 16, -9, 95, 56, 60, 96, 45, -44, -70, 4, -37, -55, -123, -120, 69, -26, -87, -60, 5, 91, -59, -65, -96, -19, -37, -122, 103, -119, -16, -20, 106, -128, -4, -27, 60, 102, 8, -33, -36, -101, -97, -30, -19, 86, 117, 44, -58, 5, 81, 59, -93, -15, 117, -100, -35, -107, 34, 117, 63, 24, -41, 0, 1, 0, 1, 66, 8, 101, 115, 83, 68, 73, 98, 0, 24};
    private static final byte[] CHR_C_CV_IFD = {-48, 2, -32, 0, 0, 0, 0, 2};
    private static final RSAPrivateKey IFD_PRIVATE_KEY = new RSAPrivateKey() { // from class: es.gob.jmulticard.card.dnie.Dnie3r2UsrCwa14890Constants.1
        private static final long serialVersionUID = 6991556885804507378L;
        private final BigInteger ifdModulus = new BigInteger(1, new byte[]{-67, -17, -37, -124, -20, -26, -104, -72, 40, 127, 127, -26, 41, 109, -128, 114, -104, 58, 27, 61, 59, -97, 87, -83, -104, 79, -70, 120, 88, 31, -1, 82, -23, 61, -119, 107, -11, 98, 37, -23, -8, 46, -106, -107, 20, 0, 105, -104, 46, 91, 91, -50, 55, -83, 115, 22, 69, 2, -40, -84, -67, 96, 95, 105, 18, 74, 60, -11, -81, -28, -80, 24, 96, 45, -44, -70, 4, -37, -55, -123, -120, 69, -26, -87, -60, 5, 91, -59, -65, -96, -19, -37, -122, 103, -119, -16, -20, 106, -128, -4, -27, 60, 102, 8, -33, -36, -101, -97, -30, -19, 86, 117, 44, -58, 5, 81, 59, -93, -15, 117, -100, -35, -107, 34, 117, 63, 24, -41});
        private final BigInteger ifdPrivateExponent = new BigInteger(1, new byte[]{-96, 81, 85, -109, -44, 54, 43, -113, -67, -73, 40, -88, -120, 45, 66, 46, -9, -88, -116, 23, 90, 60, -5, -49, -83, -15, 21, -18, -64, 75, 121, -62, 108, -42, -95, 40, -69, -67, 53, 77, 80, 75, 90, -108, -56, -122, 52, -102, -37, -2, 6, -10, 127, -18, 106, 102, -48, -89, 63, 102, 70, -114, -110, -40, 115, -74, -114, -30, -53, 71, -79, -95, 90, 42, -89, -40, -58, -50, -113, 63, 20, -109, 13, 86, -74, 50, 127, 86, -53, 33, 84, 105, -91, 122, 30, -32, 24, -113, -42, -46, 109, -125, -93, -128, -90, -85, -45, -88, -97, 27, 99, -60, -103, -127, -112, 70, 83, 105, 53, -83, -78, -37, 60, 23, -52, -67, -86, 81});

        public String toString() {
            return "Clave privada RSA del certificado de Terminal para DNIe 3.0 R2 (canal de usuario)";
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

    Dnie3r2UsrCwa14890Constants() {
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
