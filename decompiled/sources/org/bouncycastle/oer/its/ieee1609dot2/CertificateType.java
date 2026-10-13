package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/CertificateType.class */
public class CertificateType extends ASN1Enumerated {
    public static final CertificateType explicit = new CertificateType(BigInteger.ZERO);
    public static final CertificateType implicit = new CertificateType(BigInteger.ONE);

    public CertificateType(BigInteger bigInteger) {
        super(bigInteger);
        assertValues();
    }

    private CertificateType(ASN1Enumerated aSN1Enumerated) {
        this(aSN1Enumerated.getValue());
    }

    public static CertificateType getInstance(Object obj) {
        if (obj instanceof CertificateType) {
            return (CertificateType) obj;
        }
        if (obj != null) {
            return new CertificateType(ASN1Enumerated.getInstance(obj));
        }
        return null;
    }

    protected void assertValues() {
        if (getValue().compareTo(BigInteger.ZERO) < 0 || getValue().compareTo(BigIntegers.ONE) > 0) {
            throw new IllegalArgumentException("invalid enumeration value " + getValue());
        }
    }
}
