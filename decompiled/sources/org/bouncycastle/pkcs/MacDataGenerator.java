package org.bouncycastle.pkcs;

import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/MacDataGenerator.class */
class MacDataGenerator {
    private PKCS12MacCalculatorBuilder builder;

    MacDataGenerator(PKCS12MacCalculatorBuilder pKCS12MacCalculatorBuilder) {
        this.builder = pKCS12MacCalculatorBuilder;
    }

    public MacData build(char[] cArr, byte[] bArr) throws PKCSException {
        byte[] iv;
        int iIntValue;
        try {
            MacCalculator macCalculatorBuild = this.builder.build(cArr);
            OutputStream outputStream = macCalculatorBuild.getOutputStream();
            outputStream.write(bArr);
            outputStream.close();
            AlgorithmIdentifier algorithmIdentifier = macCalculatorBuild.getAlgorithmIdentifier();
            DigestInfo digestInfo = new DigestInfo(this.builder.getDigestAlgorithmIdentifier(), macCalculatorBuild.getMac());
            if (PKCSObjectIdentifiers.id_PBMAC1.equals((ASN1Primitive) digestInfo.getAlgorithmId().getAlgorithm())) {
                iv = Strings.toUTF8ByteArray("NOT USED".toCharArray());
                iIntValue = 1;
            } else {
                PKCS12PBEParams pKCS12PBEParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
                iv = pKCS12PBEParams.getIV();
                iIntValue = pKCS12PBEParams.getIterations().intValue();
            }
            return new MacData(digestInfo, iv, iIntValue);
        } catch (Exception e) {
            throw new PKCSException("unable to process data: " + e.getMessage(), e);
        }
    }
}
