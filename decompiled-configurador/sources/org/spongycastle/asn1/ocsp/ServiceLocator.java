package org.spongycastle.asn1.ocsp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AuthorityInformationAccess;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/ocsp/ServiceLocator.class */
public class ServiceLocator extends ASN1Object {
    private final X500Name issuer;
    private final AuthorityInformationAccess locator;

    private ServiceLocator(ASN1Sequence sequence) {
        this.issuer = X500Name.getInstance(sequence.getObjectAt(0));
        if (sequence.size() == 2) {
            this.locator = AuthorityInformationAccess.getInstance(sequence.getObjectAt(1));
        } else {
            this.locator = null;
        }
    }

    public static ServiceLocator getInstance(Object obj) {
        if (obj instanceof ServiceLocator) {
            return (ServiceLocator) obj;
        }
        if (obj != null) {
            return new ServiceLocator(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public AuthorityInformationAccess getLocator() {
        return this.locator;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.issuer);
        if (this.locator != null) {
            v.add(this.locator);
        }
        return new DERSequence(v);
    }
}
