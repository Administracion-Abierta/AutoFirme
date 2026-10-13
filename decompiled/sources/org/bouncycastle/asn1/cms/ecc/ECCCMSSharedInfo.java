package org.bouncycastle.asn1.cms.ecc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cms/ecc/ECCCMSSharedInfo.class */
public class ECCCMSSharedInfo extends ASN1Object {
    private final AlgorithmIdentifier keyInfo;
    private final byte[] entityUInfo;
    private final byte[] suppPubInfo;

    public ECCCMSSharedInfo(AlgorithmIdentifier algorithmIdentifier, byte[] bArr, byte[] bArr2) {
        this.keyInfo = algorithmIdentifier;
        this.entityUInfo = Arrays.clone(bArr);
        this.suppPubInfo = Arrays.clone(bArr2);
    }

    public ECCCMSSharedInfo(AlgorithmIdentifier algorithmIdentifier, byte[] bArr) {
        this.keyInfo = algorithmIdentifier;
        this.entityUInfo = null;
        this.suppPubInfo = Arrays.clone(bArr);
    }

    private ECCCMSSharedInfo(ASN1Sequence aSN1Sequence) {
        this.keyInfo = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        if (aSN1Sequence.size() == 2) {
            this.entityUInfo = null;
            this.suppPubInfo = ASN1OctetString.getInstance((ASN1TaggedObject) aSN1Sequence.getObjectAt(1), true).getOctets();
        } else {
            this.entityUInfo = ASN1OctetString.getInstance((ASN1TaggedObject) aSN1Sequence.getObjectAt(1), true).getOctets();
            this.suppPubInfo = ASN1OctetString.getInstance((ASN1TaggedObject) aSN1Sequence.getObjectAt(2), true).getOctets();
        }
    }

    public static ECCCMSSharedInfo getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static ECCCMSSharedInfo getInstance(Object obj) {
        if (obj instanceof ECCCMSSharedInfo) {
            return (ECCCMSSharedInfo) obj;
        }
        if (obj != null) {
            return new ECCCMSSharedInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(this.keyInfo);
        if (this.entityUInfo != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, (ASN1Encodable) new DEROctetString(this.entityUInfo)));
        }
        aSN1EncodableVector.add(new DERTaggedObject(true, 2, (ASN1Encodable) new DEROctetString(this.suppPubInfo)));
        return new DERSequence(aSN1EncodableVector);
    }
}
