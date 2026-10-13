package org.spongycastle.asn1.cmc;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmc/BodyPartID.class */
public class BodyPartID extends ASN1Object {
    public static final long bodyIdMax = 4294967295L;
    private final long id;

    public BodyPartID(long id) {
        if (id < 0 || id > 4294967295L) {
            throw new IllegalArgumentException("id out of range");
        }
        this.id = id;
    }

    private static long convert(BigInteger value) {
        if (value.bitLength() > 32) {
            throw new IllegalArgumentException("id out of range");
        }
        return value.longValue();
    }

    private BodyPartID(ASN1Integer id) {
        this(convert(id.getValue()));
    }

    public static BodyPartID getInstance(Object o) {
        if (o instanceof BodyPartID) {
            return (BodyPartID) o;
        }
        if (o != null) {
            return new BodyPartID(ASN1Integer.getInstance(o));
        }
        return null;
    }

    public long getID() {
        return this.id;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.id);
    }
}
