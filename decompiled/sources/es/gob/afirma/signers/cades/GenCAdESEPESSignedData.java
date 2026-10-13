package es.gob.afirma.signers.cades;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.signers.AOPkcs1Signer;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Date;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/GenCAdESEPESSignedData.class */
public final class GenCAdESEPESSignedData {
    private GenCAdESEPESSignedData() {
    }

    public static byte[] generateSignedData(String str, PrivateKey privateKey, Certificate[] certificateArr, CAdESParameters cAdESParameters) throws AOException {
        if (cAdESParameters == null) {
            throw new IllegalArgumentException("No se ha introducido configuracion para la construccion de la firma");
        }
        Date date = new Date();
        Certificate[] certificateArr2 = cAdESParameters.isIncludedOnlySigningCertificate() ? new Certificate[]{certificateArr[0]} : certificateArr;
        byte[] bArrPreSign = CAdESTriPhaseSigner.preSign(certificateArr2, date, cAdESParameters);
        return CAdESTriPhaseSigner.postSign(str, cAdESParameters.getContentData(), certificateArr2, new AOPkcs1Signer().sign(bArrPreSign, str, privateKey, certificateArr2, cAdESParameters.getExtraParams()), bArrPreSign);
    }
}
