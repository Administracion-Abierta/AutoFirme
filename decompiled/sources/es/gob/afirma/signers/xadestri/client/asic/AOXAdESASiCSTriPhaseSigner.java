package es.gob.afirma.signers.xadestri.client.asic;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.signers.asic.ASiCUtil;
import es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xadestri/client/asic/AOXAdESASiCSTriPhaseSigner.class */
public final class AOXAdESASiCSTriPhaseSigner extends AOXAdESTriPhaseSigner {
    private static final String CRYPTO_OPERATION_SIGN = "sign";

    @Override // es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner, es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return triPhaseOperation(AOSignConstants.SIGN_FORMAT_XADES_ASIC_S, "sign", bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner, es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soportan cofirmas trifasicas XAdES-ASiC-S");
    }

    @Override // es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner, es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soportan cofirmas trifasicas XAdES-ASiC-S");
    }

    @Override // es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner, es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soportan contrafirmas trifasicas XAdES-ASiC-S");
    }

    @Override // es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner, es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) {
        try {
            try {
                return super.isSign(ASiCUtil.getASiCSXMLSignature(bArr));
            } catch (Exception e) {
                LOGGER.warning("Error extrayendo la firma del contenedor ASiC: " + e);
                return false;
            }
        } catch (Exception e2) {
            LOGGER.info("La firma proporcionada no es XAdES ASiC-S: " + e2);
            return false;
        }
    }

    @Override // es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner, es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        return str + (str2 != null ? str2 : "") + ".asics";
    }
}
