package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/UINT64.class */
public class UINT64 extends UintBase {
    private static final BigInteger MAX = new BigInteger("18446744073709551615");

    public UINT64(BigInteger bigInteger) {
        super(bigInteger);
    }

    public UINT64(int i) {
        super(i);
    }

    public UINT64(long j) {
        super(j);
    }

    protected UINT64(ASN1Integer aSN1Integer) {
        super(aSN1Integer);
    }

    public static UINT64 getInstance(Object obj) {
        if (obj instanceof UINT64) {
            return (UINT64) obj;
        }
        if (obj != null) {
            return new UINT64(ASN1Integer.getInstance(obj));
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
