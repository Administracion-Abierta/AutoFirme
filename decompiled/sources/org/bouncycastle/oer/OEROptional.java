package org.bouncycastle.oer;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.bouncycastle.asn1.ASN1Absent;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OEROptional.class */
public class OEROptional extends ASN1Object {
    public static final OEROptional ABSENT = new OEROptional(false, null);
    private final boolean defined;
    private final ASN1Encodable value;

    private OEROptional(boolean z, ASN1Encodable aSN1Encodable) {
        this.defined = z;
        this.value = aSN1Encodable;
    }

    public static OEROptional getInstance(Object obj) {
        if (obj instanceof OEROptional) {
            return (OEROptional) obj;
        }
        return obj instanceof ASN1Encodable ? new OEROptional(true, (ASN1Encodable) obj) : ABSENT;
    }

    public static <T> T getValue(Class<T> cls, Object obj) {
        OEROptional oEROptional = getInstance(obj);
        if (oEROptional.defined) {
            return (T) oEROptional.getObject(cls);
        }
        return null;
    }

    public <T> T getObject(final Class<T> cls) {
        if (this.defined) {
            return this.value.getClass().isInstance(cls) ? cls.cast(this.value) : (T) AccessController.doPrivileged(new PrivilegedAction<T>() { // from class: org.bouncycastle.oer.OEROptional.1
                @Override // java.security.PrivilegedAction
                public T run() {
                    try {
                        return (T) cls.cast(cls.getMethod("getInstance", Object.class).invoke(null, OEROptional.this.value));
                    } catch (Exception e) {
                        throw new IllegalStateException("could not invoke getInstance on type " + e.getMessage(), e);
                    }
                }
            });
        }
        return null;
    }

    public ASN1Encodable get() {
        return !this.defined ? ABSENT : this.value;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return !this.defined ? ASN1Absent.INSTANCE : get().toASN1Primitive();
    }

    public boolean isDefined() {
        return this.defined;
    }

    public String toString() {
        return this.defined ? "OPTIONAL(" + this.value + ")" : "ABSENT";
    }

    @Override // org.bouncycastle.asn1.ASN1Object
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        OEROptional oEROptional = (OEROptional) obj;
        if (this.defined != oEROptional.defined) {
            return false;
        }
        if (this.value != null) {
            return this.value.equals(oEROptional.value);
        }
        return oEROptional.value == null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return (31 * ((31 * super.hashCode()) + (this.defined ? 1 : 0))) + (this.value != null ? this.value.hashCode() : 0);
    }
}
