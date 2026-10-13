package org.spongycastle.asn1.cmp;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmp/KeyRecRepContent.class */
public class KeyRecRepContent extends ASN1Object {
    private PKIStatusInfo status;
    private CMPCertificate newSigCert;
    private ASN1Sequence caCerts;
    private ASN1Sequence keyPairHist;

    private KeyRecRepContent(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        this.status = PKIStatusInfo.getInstance(en.nextElement());
        while (en.hasMoreElements()) {
            ASN1TaggedObject tObj = ASN1TaggedObject.getInstance(en.nextElement());
            switch (tObj.getTagNo()) {
                case 0:
                    this.newSigCert = CMPCertificate.getInstance(tObj.getObject());
                    break;
                case 1:
                    this.caCerts = ASN1Sequence.getInstance(tObj.getObject());
                    break;
                case 2:
                    this.keyPairHist = ASN1Sequence.getInstance(tObj.getObject());
                    break;
                default:
                    throw new IllegalArgumentException("unknown tag number: " + tObj.getTagNo());
            }
        }
    }

    public static KeyRecRepContent getInstance(Object o) {
        if (o instanceof KeyRecRepContent) {
            return (KeyRecRepContent) o;
        }
        if (o != null) {
            return new KeyRecRepContent(ASN1Sequence.getInstance(o));
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
        CMPCertificate[] results = new CMPCertificate[this.caCerts.size()];
        for (int i = 0; i != results.length; i++) {
            results[i] = CMPCertificate.getInstance(this.caCerts.getObjectAt(i));
        }
        return results;
    }

    public CertifiedKeyPair[] getKeyPairHist() {
        if (this.keyPairHist == null) {
            return null;
        }
        CertifiedKeyPair[] results = new CertifiedKeyPair[this.keyPairHist.size()];
        for (int i = 0; i != results.length; i++) {
            results[i] = CertifiedKeyPair.getInstance(this.keyPairHist.getObjectAt(i));
        }
        return results;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.status);
        addOptional(v, 0, this.newSigCert);
        addOptional(v, 1, this.caCerts);
        addOptional(v, 2, this.keyPairHist);
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, ASN1Encodable obj) {
        if (obj != null) {
            v.add(new DERTaggedObject(true, tagNo, obj));
        }
    }
}
