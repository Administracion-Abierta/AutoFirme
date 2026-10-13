package es.gob.afirma.core.signers;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.Base64;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/TriphaseDataSigner.class */
public final class TriphaseDataSigner {
    private static final String PROPERTY_NAME_PRESIGN = "PRE";
    private static final String PROPERTY_NAME_PKCS1_SIGN = "PK1";
    private static final String PROPERTY_NAME_NEED_PRE = "NEED_PRE";

    private TriphaseDataSigner() {
    }

    public static TriphaseData doSign(AOPkcs1Signer aOPkcs1Signer, String str, PrivateKey privateKey, Certificate[] certificateArr, TriphaseData triphaseData, Properties properties) throws AOException {
        if (triphaseData.getSignsCount() < 1) {
            throw new AOException("No se han recibido prefirmas que firmar");
        }
        for (int i = 0; i < triphaseData.getSignsCount(); i++) {
            TriphaseData.TriSign sign = triphaseData.getSign(i);
            String property = sign.getProperty(PROPERTY_NAME_PRESIGN);
            if (property == null) {
                throw new AOException("El servidor no ha devuelto la prefirma numero " + i);
            }
            try {
                sign.addProperty(PROPERTY_NAME_PKCS1_SIGN, Base64.encode(aOPkcs1Signer.sign(Base64.decode(property), AOSignConstants.composeSignatureAlgorithmName(str, privateKey.getAlgorithm()), privateKey, certificateArr, properties)));
                if (sign.getProperty(PROPERTY_NAME_NEED_PRE) == null || !Boolean.parseBoolean(sign.getProperty(PROPERTY_NAME_NEED_PRE))) {
                    sign.deleteProperty(PROPERTY_NAME_PRESIGN);
                }
            } catch (IOException e) {
                throw new AOException("Error decodificando la prefirma: " + e, e);
            }
        }
        return triphaseData;
    }
}
