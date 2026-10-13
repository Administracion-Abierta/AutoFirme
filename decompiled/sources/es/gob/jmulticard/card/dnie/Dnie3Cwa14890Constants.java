package es.gob.jmulticard.card.dnie;

import es.gob.jmulticard.card.cwa14890.Cwa14890PrivateConstants;
import es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants;
import java.security.PublicKey;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/Dnie3Cwa14890Constants.class */
public abstract class Dnie3Cwa14890Constants implements Cwa14890PublicConstants, Cwa14890PrivateConstants {
    private static final byte[] REF_C_CV_CA_PUBLIC_KEY = {2, 15};
    private static final byte[] REF_ICC_PRIVATE_KEY = {2, 31};
    private static final PublicKey CA_COMPONENT_PUBLIC_KEY = new PublicKey() { // from class: es.gob.jmulticard.card.dnie.Dnie3Cwa14890Constants.1
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

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public byte[] getRefCCvCaPublicKey() {
        return REF_C_CV_CA_PUBLIC_KEY;
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public byte[] getRefIccPrivateKey() {
        return REF_ICC_PRIVATE_KEY;
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public PublicKey getCaComponentPublicKey() {
        return CA_COMPONENT_PUBLIC_KEY;
    }
}
