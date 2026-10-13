package org.bouncycastle.cert.crmf;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.MacCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/PKMACValueGenerator.class */
abstract class PKMACValueGenerator {
    private PKMACValueGenerator() {
    }

    public static PKMACValue generate(PKMACBuilder pKMACBuilder, char[] cArr, SubjectPublicKeyInfo subjectPublicKeyInfo) throws CRMFException {
        MacCalculator macCalculatorBuild = pKMACBuilder.build(cArr);
        OutputStream outputStream = macCalculatorBuild.getOutputStream();
        try {
            outputStream.write(subjectPublicKeyInfo.getEncoded("DER"));
            outputStream.close();
            return new PKMACValue(macCalculatorBuild.getAlgorithmIdentifier(), new DERBitString(macCalculatorBuild.getMac()));
        } catch (IOException e) {
            throw new CRMFException("exception encoding mac input: " + e.getMessage(), e);
        }
    }
}
