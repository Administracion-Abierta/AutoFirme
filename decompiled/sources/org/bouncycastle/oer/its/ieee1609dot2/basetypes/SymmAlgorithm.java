package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/SymmAlgorithm.class */
public class SymmAlgorithm extends ASN1Enumerated {
    public static final SymmAlgorithm aes128Ccm = new SymmAlgorithm(BigInteger.ZERO);

    public SymmAlgorithm(BigInteger bigInteger) {
        super(bigInteger);
        assertValues();
    }

    private SymmAlgorithm(ASN1Enumerated aSN1Enumerated) {
        super(aSN1Enumerated.getValue());
        assertValues();
    }

    protected void assertValues() {
        switch (BigIntegers.intValueExact(getValue())) {
            case 0:
                return;
            default:
                throw new IllegalArgumentException("invalid enumeration value " + getValue());
        }
    }

    public static SymmAlgorithm getInstance(Object obj) {
        if (obj instanceof SymmAlgorithm) {
            return (SymmAlgorithm) obj;
        }
        if (obj != null) {
            return new SymmAlgorithm(ASN1Enumerated.getInstance(obj));
        }
        return null;
    }
}
