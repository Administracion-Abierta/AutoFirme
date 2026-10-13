package org.spongycastle.asn1.x509;

import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/CertPolicyId.class */
public class CertPolicyId extends ASN1Object {
    private ASN1ObjectIdentifier id;

    private CertPolicyId(ASN1ObjectIdentifier id) {
        this.id = id;
    }

    public static CertPolicyId getInstance(Object o) {
        if (o instanceof CertPolicyId) {
            return (CertPolicyId) o;
        }
        if (o != null) {
            return new CertPolicyId(ASN1ObjectIdentifier.getInstance(o));
        }
        return null;
    }

    public String getId() {
        return this.id.getId();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.id;
    }
}
