package org.spongycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/ASN1Null.class */
public abstract class ASN1Null extends ASN1Primitive {
    @Override // org.spongycastle.asn1.ASN1Primitive
    abstract void encode(ASN1OutputStream aSN1OutputStream) throws IOException;

    public static ASN1Null getInstance(Object o) {
        if (o instanceof ASN1Null) {
            return (ASN1Null) o;
        }
        if (o != null) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[]) o));
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to construct NULL from byte[]: " + e.getMessage());
            } catch (ClassCastException e2) {
                throw new IllegalArgumentException("unknown object in getInstance(): " + o.getClass().getName());
            }
        }
        return null;
    }

    @Override // org.spongycastle.asn1.ASN1Primitive, org.spongycastle.asn1.ASN1Object
    public int hashCode() {
        return -1;
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1Null)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "NULL";
    }
}
