package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/KnownLongitude.class */
public class KnownLongitude extends Longitude {
    public KnownLongitude(long j) {
        super(j);
    }

    public KnownLongitude(BigInteger bigInteger) {
        super(bigInteger);
    }

    private KnownLongitude(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public static KnownLongitude getInstance(Object obj) {
        if (obj instanceof KnownLongitude) {
            return (KnownLongitude) obj;
        }
        if (obj != null) {
            return new KnownLongitude(ASN1Integer.getInstance(obj));
        }
        return null;
    }
}
