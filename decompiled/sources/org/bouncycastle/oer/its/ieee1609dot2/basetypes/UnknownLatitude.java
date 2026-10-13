package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Integer;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/UnknownLatitude.class */
public class UnknownLatitude extends Latitude {
    public static UnknownLatitude INSTANCE = new UnknownLatitude();

    private UnknownLatitude() {
        super(900000001L);
    }

    public static UnknownLatitude getInstance(Object obj) {
        if (obj instanceof UnknownLatitude) {
            return (UnknownLatitude) obj;
        }
        if (obj == null) {
            return null;
        }
        ASN1Integer aSN1Integer = ASN1Integer.getInstance(obj);
        if (aSN1Integer.getValue().intValue() != 900000001) {
            throw new IllegalArgumentException("value " + aSN1Integer.getValue() + " is not unknown value of 900000001");
        }
        return INSTANCE;
    }
}
