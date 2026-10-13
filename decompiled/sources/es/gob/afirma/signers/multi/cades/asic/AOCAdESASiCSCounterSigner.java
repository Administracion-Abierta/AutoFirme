package es.gob.afirma.signers.multi.cades.asic;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.signers.AOCounterSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.signers.asic.ASiCUtil;
import es.gob.afirma.signers.multi.cades.AOCAdESCounterSigner;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/multi/cades/asic/AOCAdESASiCSCounterSigner.class */
public final class AOCAdESASiCSCounterSigner implements AOCounterSigner {
    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        return ASiCUtil.createSContainer(new AOCAdESCounterSigner().countersign(ASiCUtil.getASiCSBinarySignature(bArr), str, counterSignTarget, objArr, privateKey, certificateArr, properties), ASiCUtil.getASiCSData(bArr), ASiCUtil.getASiCSDataFilename(bArr), ASiCUtil.ENTRY_NAME_BINARY_SIGNATURE);
    }
}
