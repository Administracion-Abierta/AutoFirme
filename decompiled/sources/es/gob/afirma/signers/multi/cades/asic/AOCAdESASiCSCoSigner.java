package es.gob.afirma.signers.multi.cades.asic;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.signers.AOCoSigner;
import es.gob.afirma.core.signers.asic.ASiCUtil;
import es.gob.afirma.signers.multi.cades.AOCAdESCoSigner;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/multi/cades/asic/AOCAdESASiCSCoSigner.class */
public final class AOCAdESASiCSCoSigner implements AOCoSigner {
    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        return cosign(bArr2, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        byte[] aSiCSData = ASiCUtil.getASiCSData(bArr);
        return ASiCUtil.createSContainer(new AOCAdESCoSigner().cosign(aSiCSData, ASiCUtil.getASiCSBinarySignature(bArr), str, privateKey, certificateArr, properties), aSiCSData, ASiCUtil.getASiCSDataFilename(bArr), ASiCUtil.ENTRY_NAME_BINARY_SIGNATURE);
    }
}
