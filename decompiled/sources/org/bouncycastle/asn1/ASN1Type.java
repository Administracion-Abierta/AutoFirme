package org.bouncycastle.asn1;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1Type.class */
abstract class ASN1Type {
    final Class javaClass;

    ASN1Type(Class cls) {
        this.javaClass = cls;
    }

    final Class getJavaClass() {
        return this.javaClass;
    }

    public final boolean equals(Object obj) {
        return this == obj;
    }

    public final int hashCode() {
        return super.hashCode();
    }
}
