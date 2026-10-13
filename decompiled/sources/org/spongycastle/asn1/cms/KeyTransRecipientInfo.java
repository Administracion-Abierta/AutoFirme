package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cms/KeyTransRecipientInfo.class */
public class KeyTransRecipientInfo extends ASN1Object {
    private ASN1Integer version;
    private RecipientIdentifier rid;
    private AlgorithmIdentifier keyEncryptionAlgorithm;
    private ASN1OctetString encryptedKey;

    public KeyTransRecipientInfo(RecipientIdentifier rid, AlgorithmIdentifier keyEncryptionAlgorithm, ASN1OctetString encryptedKey) {
        if (rid.toASN1Primitive() instanceof ASN1TaggedObject) {
            this.version = new ASN1Integer(2L);
        } else {
            this.version = new ASN1Integer(0L);
        }
        this.rid = rid;
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.encryptedKey = encryptedKey;
    }

    public KeyTransRecipientInfo(ASN1Sequence seq) {
        this.version = (ASN1Integer) seq.getObjectAt(0);
        this.rid = RecipientIdentifier.getInstance(seq.getObjectAt(1));
        this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(2));
        this.encryptedKey = (ASN1OctetString) seq.getObjectAt(3);
    }

    public static KeyTransRecipientInfo getInstance(Object obj) {
        if (obj instanceof KeyTransRecipientInfo) {
            return (KeyTransRecipientInfo) obj;
        }
        if (obj != null) {
            return new KeyTransRecipientInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public RecipientIdentifier getRecipientIdentifier() {
        return this.rid;
    }

    public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
        return this.keyEncryptionAlgorithm;
    }

    public ASN1OctetString getEncryptedKey() {
        return this.encryptedKey;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        v.add(this.rid);
        v.add(this.keyEncryptionAlgorithm);
        v.add(this.encryptedKey);
        return new DERSequence(v);
    }
}
