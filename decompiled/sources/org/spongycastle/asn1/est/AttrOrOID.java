package org.spongycastle.asn1.est;

import java.io.IOException;
import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.pkcs.Attribute;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/est/AttrOrOID.class */
public class AttrOrOID extends ASN1Object implements ASN1Choice {
    private final ASN1ObjectIdentifier oid;
    private final Attribute attribute;

    public AttrOrOID(ASN1ObjectIdentifier oid) {
        this.oid = oid;
        this.attribute = null;
    }

    public AttrOrOID(Attribute attribute) {
        this.oid = null;
        this.attribute = attribute;
    }

    public static AttrOrOID getInstance(Object obj) {
        if (obj instanceof AttrOrOID) {
            return (AttrOrOID) obj;
        }
        if (obj != null) {
            if (obj instanceof ASN1Encodable) {
                ASN1Encodable asn1Prim = ((ASN1Encodable) obj).toASN1Primitive();
                if (asn1Prim instanceof ASN1ObjectIdentifier) {
                    return new AttrOrOID(ASN1ObjectIdentifier.getInstance(asn1Prim));
                }
                if (asn1Prim instanceof ASN1Sequence) {
                    return new AttrOrOID(Attribute.getInstance(asn1Prim));
                }
            }
            if (obj instanceof byte[]) {
                try {
                    return getInstance(ASN1Primitive.fromByteArray((byte[]) obj));
                } catch (IOException e) {
                    throw new IllegalArgumentException("unknown encoding in getInstance()");
                }
            }
            throw new IllegalArgumentException("unknown object in getInstance(): " + obj.getClass().getName());
        }
        return null;
    }

    public boolean isOid() {
        return this.oid != null;
    }

    public ASN1ObjectIdentifier getOid() {
        return this.oid;
    }

    public Attribute getAttribute() {
        return this.attribute;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.oid != null) {
            return this.oid;
        }
        return this.attribute.toASN1Primitive();
    }
}
