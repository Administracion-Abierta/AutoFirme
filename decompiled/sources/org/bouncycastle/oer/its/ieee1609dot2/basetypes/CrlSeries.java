package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/CrlSeries.class */
public class CrlSeries extends UINT16 {
    public CrlSeries(int i) {
        super(i);
    }

    public CrlSeries(BigInteger bigInteger) {
        super(bigInteger);
    }

    public static CrlSeries getInstance(Object obj) {
        if (obj instanceof CrlSeries) {
            return (CrlSeries) obj;
        }
        if (obj != null) {
            return new CrlSeries(ASN1Integer.getInstance(obj).getValue());
        }
        return null;
    }
}
