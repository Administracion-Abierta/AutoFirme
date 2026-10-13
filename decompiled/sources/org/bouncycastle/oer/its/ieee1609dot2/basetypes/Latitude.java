package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/Latitude.class */
public class Latitude extends NinetyDegreeInt {
    public Latitude(long j) {
        super(j);
    }

    public Latitude(BigInteger bigInteger) {
        super(bigInteger);
    }

    private Latitude(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public static Latitude getInstance(Object obj) {
        if (obj instanceof Latitude) {
            return (Latitude) obj;
        }
        if (obj != null) {
            return new Latitude(ASN1Integer.getInstance(obj));
        }
        return null;
    }
}
