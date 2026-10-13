package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/PolicyMappings.class */
public class PolicyMappings extends ASN1Object {
    ASN1Sequence seq;

    public static PolicyMappings getInstance(Object obj) {
        if (obj instanceof PolicyMappings) {
            return (PolicyMappings) obj;
        }
        if (obj != null) {
            return new PolicyMappings(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private PolicyMappings(ASN1Sequence aSN1Sequence) {
        this.seq = null;
        this.seq = aSN1Sequence;
    }

    public PolicyMappings(Hashtable hashtable) {
        this.seq = null;
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(hashtable.size());
        Enumeration enumerationKeys = hashtable.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            aSN1EncodableVector.add(new DERSequence(new ASN1ObjectIdentifier(str), new ASN1ObjectIdentifier((String) hashtable.get(str))));
        }
        this.seq = new DERSequence(aSN1EncodableVector);
    }

    public PolicyMappings(CertPolicyId certPolicyId, CertPolicyId certPolicyId2) {
        this.seq = null;
        this.seq = new DERSequence(new DERSequence(certPolicyId, certPolicyId2));
    }

    public PolicyMappings(CertPolicyId[] certPolicyIdArr, CertPolicyId[] certPolicyIdArr2) {
        this.seq = null;
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(certPolicyIdArr.length);
        for (int i = 0; i != certPolicyIdArr.length; i++) {
            aSN1EncodableVector.add(new DERSequence(certPolicyIdArr[i], certPolicyIdArr2[i]));
        }
        this.seq = new DERSequence(aSN1EncodableVector);
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}
