package org.bouncycastle.oer.its.etsi102941.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncrypted;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSignedExternalPayload;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/basetypes/EcSignature.class */
public class EcSignature extends ASN1Object implements ASN1Choice {
    public static final int encryptedEcSignature = 0;
    public static final int ecSignature = 1;
    private final int choice;
    private final ASN1Encodable _ecSignature;

    public EcSignature(int i, ASN1Encodable aSN1Encodable) {
        this.choice = i;
        this._ecSignature = aSN1Encodable;
    }

    private EcSignature(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0:
                this._ecSignature = EtsiTs103097DataEncrypted.getInstance((Object) aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 1:
                this._ecSignature = EtsiTs103097DataSignedExternalPayload.getInstance((Object) aSN1TaggedObject.getExplicitBaseObject());
                return;
            default:
                throw new IllegalArgumentException("invalid choice value " + this.choice);
        }
    }

    public static EcSignature getInstance(Object obj) {
        if (obj instanceof EcSignature) {
            return (EcSignature) obj;
        }
        if (obj != null) {
            return new EcSignature(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getEcSignature() {
        return this._ecSignature;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this._ecSignature);
    }
}
