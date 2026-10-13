package es.gob.afirma.signers.cades;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/AOSigPolicyQualifierInfo.class */
final class AOSigPolicyQualifierInfo implements ASN1Encodable {
    private final ASN1ObjectIdentifier sigPolicyQualifierId = PKCSObjectIdentifiers.id_spq_ets_uri;
    private final ASN1Encodable sigQualifier;

    AOSigPolicyQualifierInfo(String str) {
        this.sigQualifier = new DERIA5String(str);
    }

    ASN1ObjectIdentifier getSigPolicyQualifierId() {
        return this.sigPolicyQualifierId;
    }

    ASN1Encodable getSigQualifier() {
        return this.sigQualifier;
    }

    @Override // org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(this.sigPolicyQualifierId);
        aSN1EncodableVector.add(this.sigQualifier);
        return new DERSequence(aSN1EncodableVector);
    }
}
