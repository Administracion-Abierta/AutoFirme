package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/UINT3.class */
public class UINT3 extends UintBase {
    private static final BigInteger MAX = BigInteger.valueOf(7);

    public UINT3(BigInteger bigInteger) {
        super(bigInteger);
    }

    public UINT3(int i) {
        super(i);
    }

    public UINT3(long j) {
        super(j);
    }

    protected UINT3(ASN1Integer aSN1Integer) {
        super(aSN1Integer);
    }

    public static UINT3 getInstance(Object obj) {
        if (obj instanceof UINT3) {
            return (UINT3) obj;
        }
        if (obj != null) {
            return new UINT3(ASN1Integer.getInstance(obj));
        }
        return null;
    }

    @Override // org.bouncycastle.oer.its.ieee1609dot2.basetypes.UintBase
    protected void assertLimit() {
        if (this.value.signum() < 0) {
            throw new IllegalArgumentException("value must not be negative");
        }
        if (this.value.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("value must not exceed " + MAX.toString(16));
        }
    }
}
