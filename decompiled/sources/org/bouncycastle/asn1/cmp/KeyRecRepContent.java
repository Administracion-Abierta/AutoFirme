package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmp/KeyRecRepContent.class */
public class KeyRecRepContent extends ASN1Object {
    private final PKIStatusInfo status;
    private CMPCertificate newSigCert;
    private ASN1Sequence caCerts;
    private ASN1Sequence keyPairHist;

    private KeyRecRepContent(ASN1Sequence aSN1Sequence) {
        Enumeration objects = aSN1Sequence.getObjects();
        this.status = PKIStatusInfo.getInstance(objects.nextElement());
        while (objects.hasMoreElements()) {
            ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(objects.nextElement(), 128);
            switch (aSN1TaggedObject.getTagNo()) {
                case 0:
                    this.newSigCert = CMPCertificate.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                    break;
                case 1:
                    this.caCerts = ASN1Sequence.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                    break;
                case 2:
                    this.keyPairHist = ASN1Sequence.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                    break;
                default:
                    throw new IllegalArgumentException("unknown tag number: " + aSN1TaggedObject.getTagNo());
            }
        }
    }

    public static KeyRecRepContent getInstance(Object obj) {
        if (obj instanceof KeyRecRepContent) {
            return (KeyRecRepContent) obj;
        }
        if (obj != null) {
            return new KeyRecRepContent(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public PKIStatusInfo getStatus() {
        return this.status;
    }

    public CMPCertificate getNewSigCert() {
        return this.newSigCert;
    }

    public CMPCertificate[] getCaCerts() {
        if (this.caCerts == null) {
            return null;
        }
        CMPCertificate[] cMPCertificateArr = new CMPCertificate[this.caCerts.size()];
        for (int i = 0; i != cMPCertificateArr.length; i++) {
            cMPCertificateArr[i] = CMPCertificate.getInstance(this.caCerts.getObjectAt(i));
        }
        return cMPCertificateArr;
    }

    public CertifiedKeyPair[] getKeyPairHist() {
        if (this.keyPairHist == null) {
            return null;
        }
        CertifiedKeyPair[] certifiedKeyPairArr = new CertifiedKeyPair[this.keyPairHist.size()];
        for (int i = 0; i != certifiedKeyPairArr.length; i++) {
            certifiedKeyPairArr[i] = CertifiedKeyPair.getInstance(this.keyPairHist.getObjectAt(i));
        }
        return certifiedKeyPairArr;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(4);
        aSN1EncodableVector.add(this.status);
        addOptional(aSN1EncodableVector, 0, this.newSigCert);
        addOptional(aSN1EncodableVector, 1, this.caCerts);
        addOptional(aSN1EncodableVector, 2, this.keyPairHist);
        return new DERSequence(aSN1EncodableVector);
    }

    private void addOptional(ASN1EncodableVector aSN1EncodableVector, int i, ASN1Encodable aSN1Encodable) {
        if (aSN1Encodable != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, i, aSN1Encodable));
        }
    }
}
