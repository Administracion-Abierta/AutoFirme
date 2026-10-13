package org.spongycastle.asn1.crmf;

import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/crmf/ProofOfPossession.class */
public class ProofOfPossession extends ASN1Object implements ASN1Choice {
    public static final int TYPE_RA_VERIFIED = 0;
    public static final int TYPE_SIGNING_KEY = 1;
    public static final int TYPE_KEY_ENCIPHERMENT = 2;
    public static final int TYPE_KEY_AGREEMENT = 3;
    private int tagNo;
    private ASN1Encodable obj;

    private ProofOfPossession(ASN1TaggedObject tagged) {
        this.tagNo = tagged.getTagNo();
        switch (this.tagNo) {
            case 0:
                this.obj = DERNull.INSTANCE;
                return;
            case 1:
                this.obj = POPOSigningKey.getInstance(tagged, false);
                return;
            case 2:
            case 3:
                this.obj = POPOPrivKey.getInstance(tagged, true);
                return;
            default:
                throw new IllegalArgumentException("unknown tag: " + this.tagNo);
        }
    }

    public static ProofOfPossession getInstance(Object o) {
        if (o == null || (o instanceof ProofOfPossession)) {
            return (ProofOfPossession) o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new ProofOfPossession((ASN1TaggedObject) o);
        }
        throw new IllegalArgumentException("Invalid object: " + o.getClass().getName());
    }

    public ProofOfPossession() {
        this.tagNo = 0;
        this.obj = DERNull.INSTANCE;
    }

    public ProofOfPossession(POPOSigningKey poposk) {
        this.tagNo = 1;
        this.obj = poposk;
    }

    public ProofOfPossession(int type, POPOPrivKey privkey) {
        this.tagNo = type;
        this.obj = privkey;
    }

    public int getType() {
        return this.tagNo;
    }

    public ASN1Encodable getObject() {
        return this.obj;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, this.obj);
    }
}
