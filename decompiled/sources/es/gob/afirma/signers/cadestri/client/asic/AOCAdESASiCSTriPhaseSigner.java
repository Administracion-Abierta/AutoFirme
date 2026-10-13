package es.gob.afirma.signers.cadestri.client.asic;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.signers.cadestri.client.AOCAdESTriPhaseSigner;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cadestri/client/asic/AOCAdESASiCSTriPhaseSigner.class */
public final class AOCAdESASiCSTriPhaseSigner extends AOCAdESTriPhaseSigner {
    private static final String CRYPTO_OPERATION_SIGN = "sign";

    @Override // es.gob.afirma.signers.cadestri.client.AOCAdESTriPhaseSigner, es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return triPhaseOperation(AOSignConstants.SIGN_FORMAT_CADES_ASIC_S, "sign", bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.signers.cadestri.client.AOCAdESTriPhaseSigner, es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soporta cofirma trifasica CAdES-ASiC-S");
    }

    @Override // es.gob.afirma.signers.cadestri.client.AOCAdESTriPhaseSigner, es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soporta cofirma trifasica CAdES-ASiC-S");
    }

    @Override // es.gob.afirma.signers.cadestri.client.AOCAdESTriPhaseSigner, es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soporta contrafirma trifasica CAdES-ASiC-S");
    }

    @Override // es.gob.afirma.signers.cadestri.client.AOCAdESTriPhaseSigner, es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        return str + (str2 != null ? str2 : "") + ".asics";
    }
}
