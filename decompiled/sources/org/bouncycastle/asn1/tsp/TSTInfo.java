package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/tsp/TSTInfo.class */
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

    public static TSTInfo getInstance(Object obj) {
        if (obj instanceof TSTInfo) {
            return (TSTInfo) obj;
        }
        if (obj != null) {
            return new TSTInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private TSTInfo(ASN1Sequence aSN1Sequence) {
        Enumeration objects = aSN1Sequence.getObjects();
        this.version = ASN1Integer.getInstance(objects.nextElement());
        this.tsaPolicyId = ASN1ObjectIdentifier.getInstance(objects.nextElement());
        this.messageImprint = MessageImprint.getInstance(objects.nextElement());
        this.serialNumber = ASN1Integer.getInstance(objects.nextElement());
        this.genTime = ASN1GeneralizedTime.getInstance(objects.nextElement());
        this.ordering = ASN1Boolean.getInstance(false);
        while (objects.hasMoreElements()) {
            ASN1Object aSN1Object = (ASN1Object) objects.nextElement();
            if (aSN1Object instanceof ASN1TaggedObject) {
                ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) aSN1Object;
                switch (aSN1TaggedObject.getTagNo()) {
                    case 0:
                        this.tsa = GeneralName.getInstance(aSN1TaggedObject, true);
                        break;
                    case 1:
                        this.extensions = Extensions.getInstance(aSN1TaggedObject, false);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown tag value " + aSN1TaggedObject.getTagNo());
                }
            } else if ((aSN1Object instanceof ASN1Sequence) || (aSN1Object instanceof Accuracy)) {
                this.accuracy = Accuracy.getInstance(aSN1Object);
            } else if (aSN1Object instanceof ASN1Boolean) {
                this.ordering = ASN1Boolean.getInstance(aSN1Object);
            } else if (aSN1Object instanceof ASN1Integer) {
                this.nonce = ASN1Integer.getInstance(aSN1Object);
            }
        }
    }

    public TSTInfo(ASN1ObjectIdentifier aSN1ObjectIdentifier, MessageImprint messageImprint, ASN1Integer aSN1Integer, ASN1GeneralizedTime aSN1GeneralizedTime, Accuracy accuracy, ASN1Boolean aSN1Boolean, ASN1Integer aSN1Integer2, GeneralName generalName, Extensions extensions) {
        this.version = new ASN1Integer(1L);
        this.tsaPolicyId = aSN1ObjectIdentifier;
        this.messageImprint = messageImprint;
        this.serialNumber = aSN1Integer;
        this.genTime = aSN1GeneralizedTime;
        this.accuracy = accuracy;
        this.ordering = aSN1Boolean;
        this.nonce = aSN1Integer2;
        this.tsa = generalName;
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

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(10);
        aSN1EncodableVector.add(this.version);
        aSN1EncodableVector.add(this.tsaPolicyId);
        aSN1EncodableVector.add(this.messageImprint);
        aSN1EncodableVector.add(this.serialNumber);
        aSN1EncodableVector.add(this.genTime);
        if (this.accuracy != null) {
            aSN1EncodableVector.add(this.accuracy);
        }
        if (this.ordering != null && this.ordering.isTrue()) {
            aSN1EncodableVector.add(this.ordering);
        }
        if (this.nonce != null) {
            aSN1EncodableVector.add(this.nonce);
        }
        if (this.tsa != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, (ASN1Encodable) this.tsa));
        }
        if (this.extensions != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable) this.extensions));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
