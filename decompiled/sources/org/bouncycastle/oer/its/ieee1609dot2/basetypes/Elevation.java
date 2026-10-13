package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/Elevation.class */
public class Elevation extends UINT16 {
    public Elevation(UINT16 uint16) {
        super(uint16.getValue());
    }

    public Elevation(BigInteger bigInteger) {
        super(bigInteger);
    }

    public Elevation(int i) {
        super(i);
    }

    public Elevation(long j) {
        super(j);
    }

    protected Elevation(ASN1Integer aSN1Integer) {
        super(aSN1Integer);
    }

    public static Elevation getInstance(Object obj) {
        if (obj instanceof Elevation) {
            return (Elevation) obj;
        }
        if (obj != null) {
            return new Elevation(UINT16.getInstance(obj));
        }
        return null;
    }
}
