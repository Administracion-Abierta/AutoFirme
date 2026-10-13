package org.spongycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/ASN1TaggedObject.class */
public abstract class ASN1TaggedObject extends ASN1Primitive implements ASN1TaggedObjectParser {
    int tagNo;
    boolean empty = false;
    boolean explicit;
    ASN1Encodable obj;

    @Override // org.spongycastle.asn1.ASN1Primitive
    abstract void encode(ASN1OutputStream aSN1OutputStream) throws IOException;

    public static ASN1TaggedObject getInstance(ASN1TaggedObject obj, boolean explicit) {
        if (explicit) {
            return (ASN1TaggedObject) obj.getObject();
        }
        throw new IllegalArgumentException("implicitly tagged tagged object");
    }

    public static ASN1TaggedObject getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1TaggedObject)) {
            return (ASN1TaggedObject) obj;
        }
        if (obj instanceof byte[]) {
            try {
                return getInstance(fromByteArray((byte[]) obj));
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to construct tagged object from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
    }

    public ASN1TaggedObject(boolean explicit, int tagNo, ASN1Encodable obj) {
        this.explicit = true;
        this.obj = null;
        if (obj instanceof ASN1Choice) {
            this.explicit = true;
        } else {
            this.explicit = explicit;
        }
        this.tagNo = tagNo;
        if (this.explicit) {
            this.obj = obj;
            return;
        }
        ASN1Primitive prim = obj.toASN1Primitive();
        if (prim instanceof ASN1Set) {
        }
        this.obj = obj;
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1TaggedObject)) {
            return false;
        }
        ASN1TaggedObject other = (ASN1TaggedObject) o;
        if (this.tagNo != other.tagNo || this.empty != other.empty || this.explicit != other.explicit) {
            return false;
        }
        if (this.obj == null) {
            if (other.obj != null) {
                return false;
            }
            return true;
        }
        if (!this.obj.toASN1Primitive().equals(other.obj.toASN1Primitive())) {
            return false;
        }
        return true;
    }

    @Override // org.spongycastle.asn1.ASN1Primitive, org.spongycastle.asn1.ASN1Object
    public int hashCode() {
        int code = this.tagNo;
        if (this.obj != null) {
            code ^= this.obj.hashCode();
        }
        return code;
    }

    @Override // org.spongycastle.asn1.ASN1TaggedObjectParser
    public int getTagNo() {
        return this.tagNo;
    }

    public boolean isExplicit() {
        return this.explicit;
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public ASN1Primitive getObject() {
        if (this.obj != null) {
            return this.obj.toASN1Primitive();
        }
        return null;
    }

    @Override // org.spongycastle.asn1.ASN1TaggedObjectParser
    public ASN1Encodable getObjectParser(int tag, boolean isExplicit) throws IOException {
        switch (tag) {
            case 4:
                return ASN1OctetString.getInstance(this, isExplicit).parser();
            case 16:
                return ASN1Sequence.getInstance(this, isExplicit).parser();
            case 17:
                return ASN1Set.getInstance(this, isExplicit).parser();
            default:
                if (isExplicit) {
                    return getObject();
                }
                throw new ASN1Exception("implicit tagging not implemented for tag: " + tag);
        }
    }

    @Override // org.spongycastle.asn1.InMemoryRepresentable
    public ASN1Primitive getLoadedObject() {
        return toASN1Primitive();
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    ASN1Primitive toDERObject() {
        return new DERTaggedObject(this.explicit, this.tagNo, this.obj);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    ASN1Primitive toDLObject() {
        return new DLTaggedObject(this.explicit, this.tagNo, this.obj);
    }

    public String toString() {
        return "[" + this.tagNo + "]" + this.obj;
    }
}
