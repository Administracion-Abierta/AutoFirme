package org.bouncycastle.pkcs.bc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/bc/BcPKCS12PBMac1CalculatorBuilder.class */
public class BcPKCS12PBMac1CalculatorBuilder implements PKCS12MacCalculatorBuilder {
    private final PBMAC1Params pbmac1Params;
    private PBKDF2Params pbkdf2Params;

    public BcPKCS12PBMac1CalculatorBuilder(PBMAC1Params pBMAC1Params) throws IOException {
        this.pbkdf2Params = null;
        this.pbmac1Params = pBMAC1Params;
        if (PKCSObjectIdentifiers.id_PBKDF2.equals((ASN1Primitive) pBMAC1Params.getKeyDerivationFunc().getAlgorithm())) {
            this.pbkdf2Params = PBKDF2Params.getInstance(pBMAC1Params.getKeyDerivationFunc().getParameters());
            if (this.pbkdf2Params.getKeyLength() == null) {
                throw new IOException("Key length must be present when using PBMAC1.");
            }
        }
    }

    @Override // org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder
    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBMAC1, this.pbmac1Params);
    }

    @Override // org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder
    public MacCalculator build(char[] cArr) throws OperatorCreationException {
        return PKCS12PBEUtils.createPBMac1Calculator(this.pbmac1Params, this.pbkdf2Params, cArr);
    }
}
