package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/ServiceSpecificPermissions.class */
public class ServiceSpecificPermissions extends ASN1Object implements ASN1Choice {
    public static final int opaque = 0;
    public static final int bitmapSsp = 1;
    private final int choice;
    private final ASN1Encodable serviceSpecificPermissions;

    public ServiceSpecificPermissions(int i, ASN1Encodable aSN1Encodable) {
        this.choice = i;
        this.serviceSpecificPermissions = aSN1Encodable;
    }

    private ServiceSpecificPermissions(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0:
                this.serviceSpecificPermissions = Opaque.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 1:
                this.serviceSpecificPermissions = BitmapSsp.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            default:
                throw new IllegalArgumentException("invalid choice value " + this.choice);
        }
    }

    public static ServiceSpecificPermissions getInstance(Object obj) {
        if (obj instanceof ServiceSpecificPermissions) {
            return (ServiceSpecificPermissions) obj;
        }
        if (obj != null) {
            return new ServiceSpecificPermissions(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    public static ServiceSpecificPermissions opaque(ASN1OctetString aSN1OctetString) {
        return new ServiceSpecificPermissions(0, aSN1OctetString);
    }

    public static ServiceSpecificPermissions opaque(byte[] bArr) {
        return new ServiceSpecificPermissions(0, new DEROctetString(bArr));
    }

    public static ServiceSpecificPermissions bitmapSsp(BitmapSsp bitmapSsp2) {
        return new ServiceSpecificPermissions(1, bitmapSsp2);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getServiceSpecificPermissions() {
        return this.serviceSpecificPermissions;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.serviceSpecificPermissions);
    }
}
