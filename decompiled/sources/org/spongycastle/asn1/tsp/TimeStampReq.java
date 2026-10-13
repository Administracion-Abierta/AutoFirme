package org.spongycastle.asn1.tsp;

import org.spongycastle.asn1.ASN1Boolean;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.Extensions;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/tsp/TimeStampReq.class */
public class TimeStampReq extends ASN1Object {
    ASN1Integer version;
    MessageImprint messageImprint;
    ASN1ObjectIdentifier tsaPolicy;
    ASN1Integer nonce;
    ASN1Boolean certReq;
    Extensions extensions;

    public static TimeStampReq getInstance(Object o) {
        if (o instanceof TimeStampReq) {
            return (TimeStampReq) o;
        }
        if (o != null) {
            return new TimeStampReq(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    private TimeStampReq(ASN1Sequence seq) {
        int nbObjects = seq.size();
        this.version = ASN1Integer.getInstance(seq.getObjectAt(0));
        int seqStart = 0 + 1;
        this.messageImprint = MessageImprint.getInstance(seq.getObjectAt(seqStart));
        for (int opt = seqStart + 1; opt < nbObjects; opt++) {
            if (seq.getObjectAt(opt) instanceof ASN1ObjectIdentifier) {
                this.tsaPolicy = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(opt));
            } else if (seq.getObjectAt(opt) instanceof ASN1Integer) {
                this.nonce = ASN1Integer.getInstance(seq.getObjectAt(opt));
            } else if (seq.getObjectAt(opt) instanceof ASN1Boolean) {
                this.certReq = ASN1Boolean.getInstance(seq.getObjectAt(opt));
            } else if (seq.getObjectAt(opt) instanceof ASN1TaggedObject) {
                ASN1TaggedObject tagged = (ASN1TaggedObject) seq.getObjectAt(opt);
                if (tagged.getTagNo() == 0) {
                    this.extensions = Extensions.getInstance(tagged, false);
                }
            }
        }
    }

    public TimeStampReq(MessageImprint messageImprint, ASN1ObjectIdentifier tsaPolicy, ASN1Integer nonce, ASN1Boolean certReq, Extensions extensions) {
        this.version = new ASN1Integer(1L);
        this.messageImprint = messageImprint;
        this.tsaPolicy = tsaPolicy;
        this.nonce = nonce;
        this.certReq = certReq;
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
        return this.certReq;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        v.add(this.messageImprint);
        if (this.tsaPolicy != null) {
            v.add(this.tsaPolicy);
        }
        if (this.nonce != null) {
            v.add(this.nonce);
        }
        if (this.certReq != null && this.certReq.isTrue()) {
            v.add(this.certReq);
        }
        if (this.extensions != null) {
            v.add(new DERTaggedObject(false, 0, this.extensions));
        }
        return new DERSequence(v);
    }
}
