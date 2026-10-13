package org.spongycastle.cert.crmf;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.cmp.PBMParameter;
import org.spongycastle.asn1.crmf.PKMACValue;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.operator.MacCalculator;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/crmf/PKMACValueVerifier.class */
class PKMACValueVerifier {
    private final PKMACBuilder builder;

    public PKMACValueVerifier(PKMACBuilder builder) {
        this.builder = builder;
    }

    public boolean isValid(PKMACValue value, char[] password, SubjectPublicKeyInfo keyInfo) throws CRMFException {
        this.builder.setParameters(PBMParameter.getInstance(value.getAlgId().getParameters()));
        MacCalculator calculator = this.builder.build(password);
        OutputStream macOut = calculator.getOutputStream();
        try {
            macOut.write(keyInfo.getEncoded(ASN1Encoding.DER));
            macOut.close();
            return Arrays.areEqual(calculator.getMac(), value.getValue().getBytes());
        } catch (IOException e) {
            throw new CRMFException("exception encoding mac input: " + e.getMessage(), e);
        }
    }
}
