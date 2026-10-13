package org.spongycastle.asn1.x9;

import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/x9/X9ECPoint.class */
public class X9ECPoint extends ASN1Object {
    private final ASN1OctetString encoding;
    private ECCurve c;
    private ECPoint p;

    public X9ECPoint(ECPoint p) {
        this(p, false);
    }

    public X9ECPoint(ECPoint p, boolean compressed) {
        this.p = p.normalize();
        this.encoding = new DEROctetString(p.getEncoded(compressed));
    }

    public X9ECPoint(ECCurve c, byte[] encoding) {
        this.c = c;
        this.encoding = new DEROctetString(Arrays.clone(encoding));
    }

    public X9ECPoint(ECCurve c, ASN1OctetString s) {
        this(c, s.getOctets());
    }

    public byte[] getPointEncoding() {
        return Arrays.clone(this.encoding.getOctets());
    }

    public synchronized ECPoint getPoint() {
        if (this.p == null) {
            this.p = this.c.decodePoint(this.encoding.getOctets()).normalize();
        }
        return this.p;
    }

    public boolean isPointCompressed() {
        byte[] octets = this.encoding.getOctets();
        return octets != null && octets.length > 0 && (octets[0] == 2 || octets[0] == 3);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.encoding;
    }
}
