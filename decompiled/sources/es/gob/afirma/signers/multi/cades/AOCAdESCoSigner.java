package es.gob.afirma.signers.multi.cades;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.SigningLTSException;
import es.gob.afirma.core.signers.AOCoSigner;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.cades.CAdESExtraParams;
import es.gob.afirma.signers.cades.CAdESParameters;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Properties;
import java.util.logging.Logger;
import org.spongycastle.cms.CMSSignedData;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/multi/cades/AOCAdESCoSigner.class */
public final class AOCAdESCoSigner implements AOCoSigner {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        return cosign(null, bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        Properties extraParams = getExtraParams(properties);
        String property = extraParams.getProperty("allowSignLTSignature");
        if (property == null || !Boolean.parseBoolean(property)) {
            try {
                CAdESMultiUtil.checkUnsupportedAttributes(bArr2);
            } catch (SigningLTSException e) {
                if (property != null) {
                    throw new AOException(e.getMessage());
                }
                throw e;
            }
        }
        noticeIncompatibleConfig(str, extraParams);
        try {
            try {
                return CAdESCoSigner.coSigner(bArr2, str, privateKey, certificateArr, CAdESParameters.load(bArr, new CMSSignedData(bArr2), str, extraParams));
            } catch (AOException e2) {
                throw e2;
            } catch (Exception e3) {
                throw new AOException("Error generando la cofirma CAdES: " + e3, e3);
            }
        } catch (Exception e4) {
            throw new AOException("La firma proporcionada no es CMS/CAdES", e4);
        }
    }

    private static Properties getExtraParams(Properties properties) {
        return properties != null ? (Properties) properties.clone() : new Properties();
    }

    private static void noticeIncompatibleConfig(String str, Properties properties) {
        if (properties.containsKey("precalculatedHashAlgorithm")) {
            LOGGER.warning("Se ignorara el parametro 'mode' por haberse proporcionado tambien el parametro 'precalculatedHashAlgorithm'. La firma sera explicita.");
            properties.remove(CAdESExtraParams.MODE);
        }
        if (str != null && AOSignConstants.isSHA2SignatureAlgorithm(str) && properties.containsKey(CAdESExtraParams.SIGNING_CERTIFICATE_V2)) {
            LOGGER.warning("Se ignorara la propiedad 'signingCertificateV2' porque las firmas SHA2 siempre usan SigningCertificateV2");
            properties.remove(CAdESExtraParams.SIGNING_CERTIFICATE_V2);
        }
    }
}
