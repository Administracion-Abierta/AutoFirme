package es.gob.jmulticard.card.dnie.ceressc;

import es.gob.jmulticard.card.cwa14890.Cwa14890PrivateConstants;
import es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/ceressc/CeresScCwa14890Constants.class */
final class CeresScCwa14890Constants implements Cwa14890PublicConstants, Cwa14890PrivateConstants {
    private static final byte[] REF_C_CV_CA_PUBLIC_KEY = {2, 15};
    private static final byte[] C_CV_CA = {127, 33, -127, -50, 95, 55, -127, -128, -128, -102, 116, -12, -54, -85, 126, -5, -59, -123, -96, -96, -25, -92, 27, 21, -26, 41, 23, 36, 18, 79, -17, -126, -37, -101, -92, -71, -106, 50, -85, 3, 107, -12, -88, 57, -41, 61, -81, 26, -91, 2, -76, -123, -71, -78, 51, -12, -10, 94, -8, 84, 117, 17, 36, -84, -114, -7, -68, 89, 106, -15, 104, -72, -125, 54, -30, -104, 119, -70, 38, 18, -65, 41, 35, 16, 55, -117, -9, 80, 64, 119, -116, 60, 74, -25, -8, 23, 19, 61, -72, 38, 100, 122, 100, 97, 88, -34, 55, -18, 34, 55, 22, -78, 30, 91, -113, -123, -114, -8, -40, -117, 74, 37, 104, -55, 63, 109, -103, 61, 112, -119, 82, 103, -58, -84, 71, -71, 95, 56, 61, -56, -125, -83, 12, -81, 60, 106, -122, -65, 43, -13, -123, 15, -33, 67, 122, -50, -47, 122, -102, -56, -85, 75, 73, 48, -66, 125, 127, 33, 74, 55, -68, 119, 102, 82, -7, 19, 58, -108, -34, -79, -36, -79, -24, 32, 98, -93, 115, -17, -48, 108, -100, 115, -66, -6, 15, 109, 0, 1, 0, 1, 66, 8, 101, 115, 84, 67, 82, 96, 0, 17};
    private static final byte[] CHR_C_CV_CA = {101, 115, 84, 67, 83, 96, 0, 17};
    private static final byte[] REF_ICC_PRIVATE_KEY = {2, 31};
    private static final byte[] C_CV_IFD = {127, 33, -127, -51, 95, 55, -127, -128, 70, 122, 74, 21, -36, 120, -22, -43, 63, -41, -40, -119, -23, -50, -68, -67, 23, 58, -13, -120, 27, -79, -52, 124, -21, -121, 50, -123, 35, 48, 13, -105, 121, 58, 94, 19, 64, 26, 103, -6, 87, -17, -50, 92, 23, 116, -108, 41, 58, 30, 63, 112, -58, -116, 79, 70, -121, -116, -111, -27, 75, 79, -43, 54, -71, 44, -48, -77, -15, -75, -41, -44, 123, 11, 54, -82, -44, 20, 62, -6, 6, 32, 11, -109, -103, 20, 112, -123, -6, 51, -88, 21, -118, -14, 71, -92, 19, -50, -28, 112, -39, 37, -18, 116, -108, 11, -35, -4, -18, -19, -82, -34, 33, 102, -84, -107, -78, -67, -2, -28, -19, 52, -119, 104, 51, 3, 95, 82, 95, 56, 60, -24, -34, -49, 69, -79, 111, -112, 4, 50, 44, 89, 48, 55, 97, 103, -40, -41, 23, 61, 121, 37, -22, 40, 107, 48, 43, 114, 96, -49, 71, -103, -88, -120, -80, -74, 89, 87, 60, 29, -25, 7, -39, 78, 4, 6, -52, -16, -86, -6, -114, 121, -1, -95, 55, -79, 119, 0, 1, 0, 1, 66, 8, 101, 115, 84, 67, 83, 96, 0, 17};
    private static final byte[] CHR_C_CV_IFD = {0, 0, 0, 0, 0, 0, 0, 1};
    private static final RSAPrivateKey IFD_PRIVATE_KEY = new RSAPrivateKey() { // from class: es.gob.jmulticard.card.dnie.ceressc.CeresScCwa14890Constants.1
        private static final long serialVersionUID = 7871208407563977221L;
        private final BigInteger ifdModulus = new BigInteger(1, new byte[]{-70, 17, 56, 37, -12, 23, -118, 8, -58, -81, 49, -63, 98, 18, 53, 98, 20, -42, -50, 67, 110, 43, 97, 67, -34, -17, 13, 8, 86, 46, -2, -105, -99, 30, -72, -70, 109, 46, -107, 112, -53, 76, 58, -26, -109, -15, -50, 111, -81, 52, 55, 47, -31, 76, -103, 100, 22, -62, -35, -41, -28, -3, -1, -43, -2, 74, 108, 61, 56, -87, 99, 103, -24, -34, -49, 69, -79, 111, -112, 4, 50, 44, 89, 48, 55, 97, 103, -40, -41, 23, 61, 121, 37, -22, 40, 107, 48, 43, 114, 96, -49, 71, -103, -88, -120, -80, -74, 89, 87, 60, 29, -25, 7, -39, 78, 4, 6, -52, -16, -86, -6, -114, 121, -1, -95, 55, -79, 119});
        private final BigInteger ifdPrivateExponent = new BigInteger(1, new byte[]{35, 43, 12, 4, -56, 109, 6, -113, 0, 22, -82, -3, -47, 46, -123, -34, -126, -9, -46, -58, -107, -11, 39, 14, 47, -3, -40, -27, -63, 124, 98, 108, 127, -85, 5, -39, 94, -73, -106, 81, 90, 107, -113, 26, 115, -50, 25, 105, -84, -96, -34, -23, 114, -39, 10, -119, 11, 112, 75, -125, -7, -23, 52, 72, -120, -95, 99, -15, 78, -25, -62, -69, 2, 99, 64, 89, 18, -103, -81, 31, -56, 68, 32, -48, 12, -76, 66, 94, -30, -63, -118, -72, 47, 22, 4, 79, 85, 2, -19, 35, 69, 123, 77, -125, -111, 41, -124, -17, 46, 73, -96, 78, 30, -4, 64, 83, -59, -12, -127, 52, -30, 87, 125, -20, 25, -29, -40, 49});
        private final byte[] encoded = {48, -126, 1, 54, 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 4, -126, 1, 32, 48, -126, 1, 28, 2, 1, 0, 2, -127, -127, 0, -70, 17, 56, 37, -12, 23, -118, 8, -58, -81, 49, -63, 98, 18, 53, 98, 20, -42, -50, 67, 110, 43, 97, 67, -34, -17, 13, 8, 86, 46, -2, -105, -99, 30, -72, -70, 109, 46, -107, 112, -53, 76, 58, -26, -109, -15, -50, 111, -81, 52, 55, 47, -31, 76, -103, 100, 22, -62, -35, -41, -28, -3, -1, -43, -2, 74, 108, 61, 56, -87, 99, 103, -24, -34, -49, 69, -79, 111, -112, 4, 50, 44, 89, 48, 55, 97, 103, -40, -41, 23, 61, 121, 37, -22, 40, 107, 48, 43, 114, 96, -49, 71, -103, -88, -120, -80, -74, 89, 87, 60, 29, -25, 7, -39, 78, 4, 6, -52, -16, -86, -6, -114, 121, -1, -95, 55, -79, 119, 2, 1, 0, 2, -127, -128, 35, 43, 12, 4, -56, 109, 6, -113, 0, 22, -82, -3, -47, 46, -123, -34, -126, -9, -46, -58, -107, -11, 39, 14, 47, -3, -40, -27, -63, 124, 98, 108, 127, -85, 5, -39, 94, -73, -106, 81, 90, 107, -113, 26, 115, -50, 25, 105, -84, -96, -34, -23, 114, -39, 10, -119, 11, 112, 75, -125, -7, -23, 52, 72, -120, -95, 99, -15, 78, -25, -62, -69, 2, 99, 64, 89, 18, -103, -81, 31, -56, 68, 32, -48, 12, -76, 66, 94, -30, -63, -118, -72, 47, 22, 4, 79, 85, 2, -19, 35, 69, 123, 77, -125, -111, 41, -124, -17, 46, 73, -96, 78, 30, -4, 64, 83, -59, -12, -127, 52, -30, 87, 125, -20, 25, -29, -40, 49, 2, 1, 0, 2, 1, 0, 2, 1, 0, 2, 1, 0, 2, 1, 0};

        public String toString() {
            return "Clave privada RSA del certificado de Terminal para tarjetas CERES 4.30 y superiores";
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
    private static final PublicKey CA_COMPONENT_PUBLIC_KEY = new PublicKey() { // from class: es.gob.jmulticard.card.dnie.ceressc.CeresScCwa14890Constants.2
        private static final long serialVersionUID = -143874096089393139L;
        private final byte[] encoded = {48, -127, -98, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127, -116, 0, 48, -127, -120, 2, -127, -128, -22, -34, -38, 69, 83, 50, -108, 80, 57, -38, -92, 4, -56, -21, -60, -45, -73, -11, -36, -122, -110, -125, -51, -22, 47, 16, 30, 42, -75, 79, -80, -48, -80, 61, -113, 3, 13, -81, 36, 88, 2, -126, -120, -11, 76, -27, 82, -8, -6, 87, -85, 47, -79, 3, -79, 18, 66, 126, 17, 19, 29, 29, 39, -31, 10, 91, 80, 14, -86, -27, -39, 64, 48, 30, 48, -21, 38, -61, -23, 6, 107, 37, 113, 86, -19, 99, -99, 112, -52, -64, -112, -72, 99, -81, -69, 59, -2, -40, -63, 123, -25, 103, 48, 52, -71, -126, 62, -105, 126, -42, 87, 37, 41, 39, -7, 87, 91, -97, -1, 102, -111, -37, 100, -8, 11, 94, -110, -51, 2, 3, 1, 0, 1};

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

    CeresScCwa14890Constants() {
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
