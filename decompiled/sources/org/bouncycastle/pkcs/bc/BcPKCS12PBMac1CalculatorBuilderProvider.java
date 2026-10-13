package org.bouncycastle.pkcs.bc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilderProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/bc/BcPKCS12PBMac1CalculatorBuilderProvider.class */
public class BcPKCS12PBMac1CalculatorBuilderProvider implements PKCS12MacCalculatorBuilderProvider {
    @Override // org.bouncycastle.pkcs.PKCS12MacCalculatorBuilderProvider
    public PKCS12MacCalculatorBuilder get(final AlgorithmIdentifier algorithmIdentifier) {
        return new PKCS12MacCalculatorBuilder() { // from class: org.bouncycastle.pkcs.bc.BcPKCS12PBMac1CalculatorBuilderProvider.1
            @Override // org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder
            public MacCalculator build(char[] cArr) throws OperatorCreationException {
                if (!PKCSObjectIdentifiers.id_PBMAC1.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm())) {
                    throw new OperatorCreationException("protection algorithm not PB mac based");
                }
                try {
                    return new BcPKCS12PBMac1CalculatorBuilder(PBMAC1Params.getInstance(algorithmIdentifier.getParameters())).build(cArr);
                } catch (IOException e) {
                    throw new OperatorCreationException("invalid parameters in protection algorithm: " + e.getMessage());
                }
            }

            @Override // org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder
            public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
                return new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), algorithmIdentifier.getParameters());
            }
        };
    }
}
