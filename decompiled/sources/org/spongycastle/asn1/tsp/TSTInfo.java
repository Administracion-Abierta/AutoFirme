package org.spongycastle.asn1.tsp;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Boolean;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.GeneralName;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/tsp/TSTInfo.class */
public class TSTInfo extends ASN1Object {
    private ASN1Integer version;
    private ASN1ObjectIdentifier tsaPolicyId;
    private MessageImprint messageImprint;
    private ASN1Integer serialNumber;
    private ASN1GeneralizedTime genTime;
    private Accuracy accuracy;
    private ASN1Boolean ordering;
    private ASN1Integer nonce;
    private GeneralName tsa;
    private Extensions extensions;

    public static TSTInfo getInstance(Object o) {
        if (o instanceof TSTInfo) {
            return (TSTInfo) o;
        }
        if (o != null) {
            return new TSTInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    private TSTInfo(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.version = ASN1Integer.getInstance(e.nextElement());
        this.tsaPolicyId = ASN1ObjectIdentifier.getInstance(e.nextElement());
        this.messageImprint = MessageImprint.getInstance(e.nextElement());
        this.serialNumber = ASN1Integer.getInstance(e.nextElement());
        this.genTime = ASN1GeneralizedTime.getInstance(e.nextElement());
        this.ordering = ASN1Boolean.getInstance(false);
        while (e.hasMoreElements()) {
            ASN1Object o = (ASN1Object) e.nextElement();
            if (o instanceof ASN1TaggedObject) {
                ASN1TaggedObject tagged = (ASN1TaggedObject) o;
                switch (tagged.getTagNo()) {
                    case 0:
                        this.tsa = GeneralName.getInstance(tagged, true);
                        break;
                    case 1:
                        this.extensions = Extensions.getInstance(tagged, false);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown tag value " + tagged.getTagNo());
                }
            } else if ((o instanceof ASN1Sequence) || (o instanceof Accuracy)) {
                this.accuracy = Accuracy.getInstance(o);
            } else if (o instanceof ASN1Boolean) {
                this.ordering = ASN1Boolean.getInstance(o);
            } else if (o instanceof ASN1Integer) {
                this.nonce = ASN1Integer.getInstance(o);
            }
        }
    }

    public TSTInfo(ASN1ObjectIdentifier tsaPolicyId, MessageImprint messageImprint, ASN1Integer serialNumber, ASN1GeneralizedTime genTime, Accuracy accuracy, ASN1Boolean ordering, ASN1Integer nonce, GeneralName tsa, Extensions extensions) {
        this.version = new ASN1Integer(1L);
        this.tsaPolicyId = tsaPolicyId;
        this.messageImprint = messageImprint;
        this.serialNumber = serialNumber;
        this.genTime = genTime;
        this.accuracy = accuracy;
        this.ordering = ordering;
        this.nonce = nonce;
        this.tsa = tsa;
        this.extensions = extensions;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public MessageImprint getMessageImprint() {
        return this.messageImprint;
    }

    public ASN1ObjectIdentifier getPolicy() {
        return this.tsaPolicyId;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public Accuracy getAccuracy() {
        return this.accuracy;
    }

    public ASN1GeneralizedTime getGenTime() {
        return this.genTime;
    }

    public ASN1Boolean getOrdering() {
        return this.ordering;
    }

    public ASN1Integer getNonce() {
        return this.nonce;
    }

    public GeneralName getTsa() {
        return this.tsa;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector seq = new ASN1EncodableVector();
        seq.add(this.version);
        seq.add(this.tsaPolicyId);
        seq.add(this.messageImprint);
        seq.add(this.serialNumber);
        seq.add(this.genTime);
        if (this.accuracy != null) {
            seq.add(this.accuracy);
        }
        if (this.ordering != null && this.ordering.isTrue()) {
            seq.add(this.ordering);
        }
        if (this.nonce != null) {
            seq.add(this.nonce);
        }
        if (this.tsa != null) {
            seq.add(new DERTaggedObject(true, 0, this.tsa));
        }
        if (this.extensions != null) {
            seq.add(new DERTaggedObject(false, 1, this.extensions));
        }
        return new DERSequence(seq);
    }
}
