package org.bouncycastle.oer.its.etsi102941.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/basetypes/Version.class */
public class Version extends ASN1Object {
    private final BigInteger version;

    public Version(BigInteger bigInteger) {
        this.version = bigInteger;
    }

    public Version(int i) {
        this(BigInteger.valueOf(i));
    }

    public Version(long j) {
        this(BigInteger.valueOf(j));
    }

    protected Version(ASN1Integer aSN1Integer) {
        this.version = aSN1Integer.getValue();
    }

    public BigInteger getVersion() {
        return this.version;
    }

    public static Version getInstance(Object obj) {
        if (obj instanceof UINT8) {
            return (Version) obj;
        }
        if (obj != null) {
            return new Version(ASN1Integer.getInstance(obj));
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.version);
    }
}
