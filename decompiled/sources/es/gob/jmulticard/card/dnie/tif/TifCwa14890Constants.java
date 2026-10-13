package es.gob.jmulticard.card.dnie.tif;

import es.gob.jmulticard.card.cwa14890.Cwa14890PrivateConstants;
import es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/tif/TifCwa14890Constants.class */
final class TifCwa14890Constants implements Cwa14890PublicConstants, Cwa14890PrivateConstants {
    private static final byte[] REF_C_CV_CA_PUBLIC_KEY = {2, 15};
    private static final byte[] C_CV_CA = {127, 33, -127, -50, 95, 55, -127, -128, -106, -53, 86, -40, 44, -66, -39, -116, 53, 112, 115, -50, -19, -82, -64, -121, 43, -104, -127, 51, 58, 99, -39, -1, -119, -89, 69, -102, -24, 76, -123, -2, 104, 63, 80, 68, -18, 48, 63, 92, -14, 31, 66, -118, -90, 104, 14, -70, -13, -111, -22, -128, 115, 50, -35, 13, -1, 127, 17, -96, -89, 10, 94, -85, -116, -50, 42, 60, 75, 58, 32, 49, 70, -52, 60, -3, -3, 117, 110, -70, -35, 33, 122, 32, -58, 42, 53, 8, -58, 57, -104, -108, -95, -32, 91, 33, -117, 71, 37, 24, 10, -51, -102, -26, -67, -106, -121, 10, -77, -108, 68, 61, 115, 42, -104, 70, 36, 111, -61, 111, -6, -7, 12, -65, -12, -82, 76, 87, 95, 56, 61, -13, -18, -87, 70, 93, -78, 8, 17, -41, -88, 91, -15, -38, 85, 76, 33, 100, 105, 7, -108, 56, -49, 12, 80, 61, -43, -65, -102, -35, -122, 87, -79, -120, -117, -56, 78, -77, -126, -112, -102, -121, 94, 117, 27, -109, 71, -67, -82, -35, -51, 86, -114, 20, 86, 14, 108, -121, 0, 1, 0, 1, 66, 8, 101, 115, 82, 67, 80, 96, 0, 7};
    private static final byte[] CHR_C_CV_CA = {101, 115, 83, 67, 80, 96, 0, 7};
    private static final byte[] REF_ICC_PRIVATE_KEY = {2, 31};
    private static final byte[] C_CV_IFD = {127, 33, -127, -51, 95, 55, -127, -128, 4, -70, 16, -95, -19, -33, 97, -68, -49, 6, -103, -81, -18, -59, 65, 52, 73, 101, -105, 51, 10, 45, 42, -116, 46, -77, 80, 60, 65, 53, 81, -114, -119, -62, -94, 84, -98, 13, 11, -82, 73, -68, -65, 62, -21, 17, 0, 14, 88, -5, 57, 83, 14, -114, 84, -96, -107, 105, -24, -56, -126, 68, 8, -45, -5, 95, -12, 24, 89, 69, 74, -91, 32, 0, -118, -10, 46, -30, -34, 84, 11, 45, 20, 12, 72, -32, -82, -19, 60, 109, 118, 119, 47, 69, -48, -127, 102, 77, -67, 99, -9, -81, 117, -36, -88, -74, 0, 34, 127, 72, -19, -85, 81, -77, 82, 125, -77, -89, 8, 30, 94, 49, -43, 66, 53, -25, -122, 51, 95, 56, 60, -121, -96, -1, -118, 124, 113, 32, -28, -15, 13, -18, 108, -122, -30, -123, -74, 113, 122, 18, -19, -64, -78, -9, -98, 20, 120, 61, 30, -62, -21, 31, 89, 69, -113, 116, 73, 26, -117, 0, -109, -14, -82, -58, -90, 53, -55, 40, 0, 73, -111, 51, 85, -28, 109, -44, -89, 0, 1, 0, 1, 66, 8, 101, 115, 83, 67, 80, 96, 0, 7};
    private static final byte[] CHR_C_CV_IFD = {32, 0, 0, 0, 0, 0, 0, 1};
    private static final RSAPrivateKey IFD_PRIVATE_KEY = new RSAPrivateKey() { // from class: es.gob.jmulticard.card.dnie.tif.TifCwa14890Constants.1
        private static final long serialVersionUID = 7871208407563977221L;
        private final BigInteger ifdModulus = new BigInteger(1, new byte[]{-42, 49, -45, -35, 97, -105, 81, 22, 11, -121, -70, 4, -90, -46, -48, -29, 21, 46, -66, 64, 109, 87, -104, 47, 6, 125, -79, -43, 73, -13, 118, -67, -120, -13, 50, 23, -115, -114, 72, 74, -123, 73, 88, 26, 42, -89, 97, 4, -71, 106, 119, -38, 40, 37, -31, -111, 97, -84, 95, 46, 119, -127, 86, -100, -28, 73, 117, 32, -4, 40, 79, -107, -121, -96, -1, -118, 124, 113, 32, -28, -15, 13, -18, 108, -122, -30, -123, -74, 113, 122, 18, -19, -64, -78, -9, -98, 20, 120, 61, 30, -62, -21, 31, 89, 69, -113, 116, 73, 26, -117, 0, -109, -14, -82, -58, -90, 53, -55, 40, 0, 73, -111, 51, 85, -28, 109, -44, -89});
        private final BigInteger ifdPrivateExponent = new BigInteger(1, new byte[]{-92, 112, 34, 111, -67, -63, 86, 77, -17, 82, 38, -69, -11, 122, -71, 68, -20, 121, 51, 51, -16, -3, -93, 102, -50, -69, -26, -81, -111, 19, 105, 32, 63, 40, 61, 87, -103, -92, 22, -39, -89, 19, -44, -96, 90, 8, -97, -47, -23, -86, -44, 18, 86, 106, 38, -114, -33, 6, 95, 24, 36, 68, -42, 65, 57, -104, -6, -32, -62, 49, 93, -17, 52, 70, 125, 16, -94, 112, 60, -89, 75, -28, 47, 126, -3, 65, -4, 21, 114, -33, 122, -13, 84, 66, -97, -59, 100, 4, 84, -42, -121, 27, -58, 124, 18, -69, 120, 33, -91, -41, -68, -109, 62, 11, -41, -101, 40, 78, -78, -24, 53, -33, -2, -21, -45, 72, -64, -47});
        private final byte[] encoded = {48, -126, 1, 53, 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 4, -126, 1, 31, 48, -126, 1, 27, 2, 1, 0, 2, -127, -128, -42, 49, -45, -35, 97, -105, 81, 22, 11, -121, -70, 4, -90, -46, -48, -29, 21, 46, -66, 64, 109, 87, -104, 47, 6, 125, -79, -43, 73, -13, 118, -67, -120, -13, 50, 23, -115, -114, 72, 74, -123, 73, 88, 26, 42, -89, 97, 4, -71, 106, 119, -38, 40, 37, -31, -111, 97, -84, 95, 46, 119, -127, 86, -100, -28, 73, 117, 32, -4, 40, 79, -107, -121, -96, -1, -118, 124, 113, 32, -28, -15, 13, -18, 108, -122, -30, -123, -74, 113, 122, 18, -19, -64, -78, -9, -98, 20, 120, 61, 30, -62, -21, 31, 89, 69, -113, 116, 73, 26, -117, 0, -109, -14, -82, -58, -90, 53, -55, 40, 0, 73, -111, 51, 85, -28, 109, -44, -89, 2, 1, 0, 2, -127, -128, -92, 112, 34, 111, -67, -63, 86, 77, -17, 82, 38, -69, -11, 122, -71, 68, -20, 121, 51, 51, -16, -3, -93, 102, -50, -69, -26, -81, -111, 19, 105, 32, 63, 40, 61, 87, -103, -92, 22, -39, -89, 19, -44, -96, 90, 8, -97, -47, -23, -86, -44, 18, 86, 106, 38, -114, -33, 6, 95, 24, 36, 68, -42, 65, 57, -104, -6, -32, -62, 49, 93, -17, 52, 70, 125, 16, -94, 112, 60, -89, 75, -28, 47, 126, -3, 65, -4, 21, 114, -33, 122, -13, 84, 66, -97, -59, 100, 4, 84, -42, -121, 27, -58, 124, 18, -69, 120, 33, -91, -41, -68, -109, 62, 11, -41, -101, 40, 78, -78, -24, 53, -33, -2, -21, -45, 72, -64, -47, 2, 1, 0, 2, 1, 0, 2, 1, 0, 2, 1, 0, 2, 1, 0};

        public String toString() {
            return "Clave privada RSA del certificado de Terminal para tarjetas FNMT TIF";
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
    private static final PublicKey CA_COMPONENT_PUBLIC_KEY = new PublicKey() { // from class: es.gob.jmulticard.card.dnie.tif.TifCwa14890Constants.2
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

    TifCwa14890Constants() {
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
