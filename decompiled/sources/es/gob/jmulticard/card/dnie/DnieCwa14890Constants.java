package es.gob.jmulticard.card.dnie;

import es.gob.jmulticard.card.cwa14890.Cwa14890PrivateConstants;
import es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/DnieCwa14890Constants.class */
final class DnieCwa14890Constants implements Cwa14890PublicConstants, Cwa14890PrivateConstants {
    private static final byte[] REF_C_CV_CA_PUBLIC_KEY = {2, 15};
    private static final byte[] C_CV_CA = {127, 33, -127, -50, 95, 55, -127, -128, 60, -70, -36, 54, -124, -66, -13, 32, 65, -83, 21, 80, -119, 37, -115, -3, 32, -58, -111, 21, -41, 47, -100, 56, -86, -103, -83, 108, 26, -19, -6, -78, -65, -84, -112, -110, -4, 112, -52, -64, 12, -81, 72, 42, 75, -29, 26, -3, -67, 60, -68, -116, -125, -126, -49, 6, -68, 7, 25, -70, -85, -75, 107, 110, -56, 7, 96, -92, -87, 63, -94, -41, -61, 71, -13, 68, 39, -7, -1, 92, -115, -26, -42, 93, -84, -107, -14, -15, -99, -84, 0, 83, -33, 17, -91, 7, -5, 98, 94, -21, -115, -92, -64, 41, -98, 74, 33, 18, -85, 112, 71, 88, -117, -115, 109, -89, 89, 34, 20, -14, -37, -95, 64, -57, -47, 34, 87, -101, 95, 56, 61, 34, 83, -56, -71, -53, 91, -61, 84, 58, 85, 102, 11, -38, -128, -108, 106, -5, 5, 37, -24, -27, 88, 107, 78, 99, -24, -110, 65, 73, 120, 54, -40, -45, -85, 8, -116, -44, 76, 33, 77, 106, -56, 86, -30, -96, 7, -12, 79, -125, 116, 51, 55, 55, 26, -35, -114, 3, 0, 1, 0, 1, 66, 8, 101, 115, 82, 68, 73, 96, 0, 6};
    private static final byte[] CHR_C_CV_CA = {101, 115, 83, 68, 73, 96, 0, 6};
    private static final byte[] REF_ICC_PRIVATE_KEY = {2, 31};
    private static final byte[] C_CV_IFD = {127, 33, -127, -51, 95, 55, -127, -128, -126, 91, 105, -58, 69, 30, 95, 81, 112, 116, 56, 95, 47, 23, -42, 77, -2, 46, 104, 86, 117, 103, 9, 75, 87, -13, -59, 120, -24, 48, -28, 37, 87, 45, -24, 40, -6, -12, -34, 27, 1, -61, -108, -29, 69, -62, -5, 6, 41, -93, -109, 73, 47, -108, -11, 112, -80, 11, 29, 103, 119, 41, -9, 85, -47, 7, 2, 43, -80, -95, 22, -31, -41, -41, 101, -99, -75, -60, -84, 13, -34, -85, 7, -1, 4, 95, 55, -75, -38, -15, 115, 43, 84, -22, -78, 56, -94, -50, 23, -55, 121, 65, -121, 117, -100, -22, -97, -110, -95, 120, 5, -94, 124, 16, 21, -20, 86, -52, 126, 71, 26, 72, -114, 111, 27, -111, -9, -86, 95, 56, 60, -83, -4, 18, -24, 86, -78, 2, 52, 106, -8, 34, 107, 26, -120, 33, 55, -36, 60, 90, 87, -16, -46, -127, 92, 31, -51, 75, -76, 111, -87, 21, 127, -33, -3, 121, -20, 58, 16, -88, 36, -52, -63, -21, 60, -32, -74, -76, 57, 106, -30, 54, 89, 0, 22, -70, 105, 0, 1, 0, 1, 66, 8, 101, 115, 83, 68, 73, 96, 0, 6};
    private static final byte[] CHR_C_CV_IFD = {32, 0, 0, 0, 0, 0, 0, 1};
    private static final RSAPrivateKey IFD_PRIVATE_KEY = new RSAPrivateKey() { // from class: es.gob.jmulticard.card.dnie.DnieCwa14890Constants.1
        private static final long serialVersionUID = 6991556885804507378L;
        private final BigInteger ifdModulus = new BigInteger(1, new byte[]{-37, 44, -76, 30, 17, 43, -84, -6, 43, -41, -61, -45, -41, -106, 126, -124, -5, -108, 52, -4, 38, 31, -99, 9, 10, -119, -125, -108, 125, -81, -124, -120, -45, -33, -113, -67, -52, 31, -110, 73, 53, -123, -31, 52, -95, -76, 45, -27, 25, -12, 99, 36, 77, 126, -45, -124, -30, 109, 81, 108, -57, -92, -1, 120, -107, -79, -103, 33, 64, 4, 58, -84, -83, -4, 18, -24, 86, -78, 2, 52, 106, -8, 34, 107, 26, -120, 33, 55, -36, 60, 90, 87, -16, -46, -127, 92, 31, -51, 75, -76, 111, -87, 21, 127, -33, -3, 121, -20, 58, 16, -88, 36, -52, -63, -21, 60, -32, -74, -76, 57, 106, -30, 54, 89, 0, 22, -70, 105});
        private final BigInteger ifdPrivateExponent = new BigInteger(1, new byte[]{24, -76, 74, 61, 21, 92, 97, -21, -12, -29, 38, 28, -117, -79, 87, -29, 111, 99, -2, 48, -23, -81, 40, -119, 43, 89, -30, -83, -21, 24, -52, -116, -117, -83, 40, 75, -111, 101, -127, -100, -92, -34, -55, 74, -96, 107, 105, -68, -24, 23, 6, -47, -63, -74, 104, -21, 18, -122, -107, -27, -9, -2, -34, 24, -87, 8, -93, 1, 26, 100, 106, 72, 29, 62, -89, 29, -118, 56, 125, 71, 70, 9, -67, 87, -88, -126, -79, -126, -32, 71, -34, -128, -32, 75, 66, 33, 65, 107, -45, -99, -6, 31, -84, 3, 0, 100, 25, 98, -83, -79, 9, -30, -116, -81, 80, 6, 27, 104, -55, -54, -67, -101, 0, 49, 60, 15, 70, -19});
        private final byte[] encoded = {48, -126, 1, 54, 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 4, -126, 1, 32, 48, -126, 1, 28, 2, 1, 0, 2, -127, -127, 0, -37, 44, -76, 30, 17, 43, -84, -6, 43, -41, -61, -45, -41, -106, 126, -124, -5, -108, 52, -4, 38, 31, -99, 9, 10, -119, -125, -108, 125, -81, -124, -120, -45, -33, -113, -67, -52, 31, -110, 73, 53, -123, -31, 52, -95, -76, 45, -27, 25, -12, 99, 36, 77, 126, -45, -124, -30, 109, 81, 108, -57, -92, -1, 120, -107, -79, -103, 33, 64, 4, 58, -84, -83, -4, 18, -24, 86, -78, 2, 52, 106, -8, 34, 107, 26, -120, 33, 55, -36, 60, 90, 87, -16, -46, -127, 92, 31, -51, 75, -76, 111, -87, 21, 127, -33, -3, 121, -20, 58, 16, -88, 36, -52, -63, -21, 60, -32, -74, -76, 57, 106, -30, 54, 89, 0, 22, -70, 105, 2, 1, 0, 2, -127, -128, 24, -76, 74, 61, 21, 92, 97, -21, -12, -29, 38, 28, -117, -79, 87, -29, 111, 99, -2, 48, -23, -81, 40, -119, 43, 89, -30, -83, -21, 24, -52, -116, -117, -83, 40, 75, -111, 101, -127, -100, -92, -34, -55, 74, -96, 107, 105, -68, -24, 23, 6, -47, -63, -74, 104, -21, 18, -122, -107, -27, -9, -2, -34, 24, -87, 8, -93, 1, 26, 100, 106, 72, 29, 62, -89, 29, -118, 56, 125, 71, 70, 9, -67, 87, -88, -126, -79, -126, -32, 71, -34, -128, -32, 75, 66, 33, 65, 107, -45, -99, -6, 31, -84, 3, 0, 100, 25, 98, -83, -79, 9, -30, -116, -81, 80, 6, 27, 104, -55, -54, -67, -101, 0, 49, 60, 15, 70, -19, 2, 1, 0, 2, 1, 0, 2, 1, 0, 2, 1, 0, 2, 1, 0};

        public String toString() {
            return "Clave privada RSA del certificado de Terminal para DNIe 2.0";
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
            byte[] out = new byte[this.encoded.length];
            System.arraycopy(this.encoded, 0, out, 0, this.encoded.length);
            return out;
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
    private static final PublicKey CA_COMPONENT_PUBLIC_KEY = new PublicKey() { // from class: es.gob.jmulticard.card.dnie.DnieCwa14890Constants.2
        private static final long serialVersionUID = -143874096089393139L;
        private final byte[] encoded = {48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127, -115, 0, 48, -127, -119, 2, -127, -127, 0, -22, -34, -38, 69, 83, 50, -108, 80, 57, -38, -92, 4, -56, -21, -60, -45, -73, -11, -36, -122, -110, -125, -51, -22, 47, 16, 30, 42, -75, 79, -80, -48, -80, 61, -113, 3, 13, -81, 36, 88, 2, -126, -120, -11, 76, -27, 82, -8, -6, 87, -85, 47, -79, 3, -79, 18, 66, 126, 17, 19, 29, 29, 39, -31, 10, 91, 80, 14, -86, -27, -39, 64, 48, 30, 48, -21, 38, -61, -23, 6, 107, 37, 113, 86, -19, 99, -99, 112, -52, -64, -112, -72, 99, -81, -69, 59, -2, -40, -63, 123, -25, 103, 48, 52, -71, -126, 62, -105, 126, -42, 87, 37, 41, 39, -7, 87, 91, -97, -1, 102, -111, -37, 100, -8, 11, 94, -110, -51, 2, 3, 1, 0, 1};

        @Override // java.security.Key
        public String getFormat() {
            return XMLX509Certificate.JCA_CERT_ID;
        }

        @Override // java.security.Key
        public byte[] getEncoded() {
            byte[] out = new byte[this.encoded.length];
            System.arraycopy(this.encoded, 0, out, 0, this.encoded.length);
            return out;
        }

        @Override // java.security.Key
        public String getAlgorithm() {
            return "RSA";
        }
    };

    DnieCwa14890Constants() {
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public byte[] getRefCCvCaPublicKey() {
        return REF_C_CV_CA_PUBLIC_KEY;
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public byte[] getCCvCa() {
        return C_CV_CA;
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public byte[] getChrCCvCa() {
        return CHR_C_CV_CA;
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public byte[] getRefIccPrivateKey() {
        return REF_ICC_PRIVATE_KEY;
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
    public PublicKey getCaComponentPublicKey() {
        return CA_COMPONENT_PUBLIC_KEY;
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public int getIfdKeyLength() {
        return 128;
    }
}
