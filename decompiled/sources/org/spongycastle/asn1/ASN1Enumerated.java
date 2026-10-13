package org.spongycastle.asn1;

import java.io.IOException;
import java.math.BigInteger;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/ASN1Enumerated.class */
public class ASN1Enumerated extends ASN1Primitive {
    private final byte[] bytes;
    private static ASN1Enumerated[] cache = new ASN1Enumerated[12];

    public static ASN1Enumerated getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1Enumerated)) {
            return (ASN1Enumerated) obj;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1Enumerated) fromByteArray((byte[]) obj);
            } catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Enumerated getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        if (explicit || (o instanceof ASN1Enumerated)) {
            return getInstance(o);
        }
        return fromOctetString(((ASN1OctetString) o).getOctets());
    }

    public ASN1Enumerated(int value) {
        this.bytes = BigInteger.valueOf(value).toByteArray();
    }

    public ASN1Enumerated(BigInteger value) {
        this.bytes = value.toByteArray();
    }

    public ASN1Enumerated(byte[] bytes) {
        if (!Properties.isOverrideSet("org.spongycastle.asn1.allow_unsafe_integer") && ASN1Integer.isMalformed(bytes)) {
            throw new IllegalArgumentException("malformed enumerated");
        }
        this.bytes = Arrays.clone(bytes);
    }

    public BigInteger getValue() {
        return new BigInteger(this.bytes);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    boolean isConstructed() {
        return false;
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    int encodedLength() {
        return 1 + StreamUtil.calculateBodyLength(this.bytes.length) + this.bytes.length;
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream out) throws IOException {
        out.writeEncoded(10, this.bytes);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1Enumerated)) {
            return false;
        }
        ASN1Enumerated other = (ASN1Enumerated) o;
        return Arrays.areEqual(this.bytes, other.bytes);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive, org.spongycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    static ASN1Enumerated fromOctetString(byte[] enc) {
        if (enc.length > 1) {
            return new ASN1Enumerated(enc);
        }
        if (enc.length == 0) {
            throw new IllegalArgumentException("ENUMERATED has zero length");
        }
        int value = enc[0] & 255;
        if (value >= cache.length) {
            return new ASN1Enumerated(Arrays.clone(enc));
        }
        ASN1Enumerated possibleMatch = cache[value];
        if (possibleMatch == null) {
            ASN1Enumerated[] aSN1EnumeratedArr = cache;
            ASN1Enumerated aSN1Enumerated = new ASN1Enumerated(Arrays.clone(enc));
            aSN1EnumeratedArr[value] = aSN1Enumerated;
            possibleMatch = aSN1Enumerated;
        }
        return possibleMatch;
    }
}
