package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/tsp/TimeStampReq.class */
public class TimeStampReq extends ASN1Object {
    ASN1Integer version;
    MessageImprint messageImprint;
    ASN1ObjectIdentifier tsaPolicy;
    ASN1Integer nonce;
    ASN1Boolean certReq;
    Extensions extensions;

    public static TimeStampReq getInstance(Object obj) {
        if (obj instanceof TimeStampReq) {
            return (TimeStampReq) obj;
        }
        if (obj != null) {
            return new TimeStampReq(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private TimeStampReq(ASN1Sequence aSN1Sequence) {
        int size = aSN1Sequence.size();
        this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
        int i = 0 + 1;
        this.messageImprint = MessageImprint.getInstance(aSN1Sequence.getObjectAt(i));
        for (int i2 = i + 1; i2 < size; i2++) {
            if (aSN1Sequence.getObjectAt(i2) instanceof ASN1ObjectIdentifier) {
                checkOption(this.tsaPolicy, i2, 2);
                this.tsaPolicy = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(i2));
            } else if (aSN1Sequence.getObjectAt(i2) instanceof ASN1Integer) {
                checkOption(this.nonce, i2, 3);
                this.nonce = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(i2));
            } else if (aSN1Sequence.getObjectAt(i2) instanceof ASN1Boolean) {
                checkOption(this.certReq, i2, 4);
                this.certReq = ASN1Boolean.getInstance(aSN1Sequence.getObjectAt(i2));
            } else {
                if (!(aSN1Sequence.getObjectAt(i2) instanceof ASN1TaggedObject)) {
                    throw new IllegalArgumentException("unidentified structure in sequence");
                }
                checkOption(this.extensions, i2, 5);
                ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) aSN1Sequence.getObjectAt(i2);
                if (aSN1TaggedObject.getTagNo() == 0) {
                    this.extensions = Extensions.getInstance(aSN1TaggedObject, false);
                }
            }
        }
    }

    private void checkOption(Object obj, int i, int i2) {
        if (obj != null || i > i2) {
            throw new IllegalArgumentException("badly placed optional in sequence");
        }
    }

    public TimeStampReq(MessageImprint messageImprint, ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Integer aSN1Integer, ASN1Boolean aSN1Boolean, Extensions extensions) {
        this.version = new ASN1Integer(1L);
        this.messageImprint = messageImprint;
        this.tsaPolicy = aSN1ObjectIdentifier;
        this.nonce = aSN1Integer;
        this.certReq = aSN1Boolean;
        this.extensions = extensions;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public MessageImprint getMessageImprint() {
        return this.messageImprint;
    }

    public ASN1ObjectIdentifier getReqPolicy() {
        return this.tsaPolicy;
    }

    public ASN1Integer getNonce() {
        return this.nonce;
    }

    public ASN1Boolean getCertReq() {
        return this.certReq == null ? ASN1Boolean.FALSE : this.certReq;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(6);
        aSN1EncodableVector.add(this.version);
        aSN1EncodableVector.add(this.messageImprint);
        if (this.tsaPolicy != null) {
            aSN1EncodableVector.add(this.tsaPolicy);
        }
        if (this.nonce != null) {
            aSN1EncodableVector.add(this.nonce);
        }
        if (this.certReq != null && this.certReq.isTrue()) {
            aSN1EncodableVector.add(this.certReq);
        }
        if (this.extensions != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable) this.extensions));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
