package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cms/KeyAgreeRecipientInfo.class */
public class KeyAgreeRecipientInfo extends ASN1Object {
    private ASN1Integer version;
    private OriginatorIdentifierOrKey originator;
    private ASN1OctetString ukm;
    private AlgorithmIdentifier keyEncryptionAlgorithm;
    private ASN1Sequence recipientEncryptedKeys;

    public KeyAgreeRecipientInfo(OriginatorIdentifierOrKey originator, ASN1OctetString ukm, AlgorithmIdentifier keyEncryptionAlgorithm, ASN1Sequence recipientEncryptedKeys) {
        this.version = new ASN1Integer(3L);
        this.originator = originator;
        this.ukm = ukm;
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.recipientEncryptedKeys = recipientEncryptedKeys;
    }

    public KeyAgreeRecipientInfo(ASN1Sequence seq) {
        int index = 0 + 1;
        this.version = (ASN1Integer) seq.getObjectAt(0);
        int index2 = index + 1;
        this.originator = OriginatorIdentifierOrKey.getInstance((ASN1TaggedObject) seq.getObjectAt(index), true);
        if (seq.getObjectAt(index2) instanceof ASN1TaggedObject) {
            index2++;
            this.ukm = ASN1OctetString.getInstance((ASN1TaggedObject) seq.getObjectAt(index2), true);
        }
        int i = index2;
        int index3 = index2 + 1;
        this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(i));
        int i2 = index3 + 1;
        this.recipientEncryptedKeys = (ASN1Sequence) seq.getObjectAt(index3);
    }

    public static KeyAgreeRecipientInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static KeyAgreeRecipientInfo getInstance(Object obj) {
        if (obj instanceof KeyAgreeRecipientInfo) {
            return (KeyAgreeRecipientInfo) obj;
        }
        if (obj != null) {
            return new KeyAgreeRecipientInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public OriginatorIdentifierOrKey getOriginator() {
        return this.originator;
    }

    public ASN1OctetString getUserKeyingMaterial() {
        return this.ukm;
    }

    public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
        return this.keyEncryptionAlgorithm;
    }

    public ASN1Sequence getRecipientEncryptedKeys() {
        return this.recipientEncryptedKeys;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        v.add(new DERTaggedObject(true, 0, this.originator));
        if (this.ukm != null) {
            v.add(new DERTaggedObject(true, 1, this.ukm));
        }
        v.add(this.keyEncryptionAlgorithm);
        v.add(this.recipientEncryptedKeys);
        return new DERSequence(v);
    }
}
