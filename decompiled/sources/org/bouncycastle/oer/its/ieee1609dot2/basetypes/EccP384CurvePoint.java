package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/EccP384CurvePoint.class */
public class EccP384CurvePoint extends EccCurvePoint implements ASN1Choice {
    public static final int xonly = 0;
    public static final int fill = 1;
    public static final int compressedY0 = 2;
    public static final int compressedY1 = 3;
    public static final int uncompressedP384 = 4;
    private final int choice;
    private final ASN1Encodable eccP384CurvePoint;

    public EccP384CurvePoint(int i, ASN1Encodable aSN1Encodable) {
        this.choice = i;
        this.eccP384CurvePoint = aSN1Encodable;
    }

    private EccP384CurvePoint(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (aSN1TaggedObject.getTagNo()) {
            case 0:
            case 2:
            case 3:
                this.eccP384CurvePoint = ASN1OctetString.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 1:
                this.eccP384CurvePoint = ASN1Null.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 4:
                this.eccP384CurvePoint = ASN1Sequence.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            default:
                throw new IllegalArgumentException("invalid choice value " + aSN1TaggedObject.getTagNo());
        }
    }

    public static EccP384CurvePoint getInstance(Object obj) {
        if (obj instanceof EccP384CurvePoint) {
            return (EccP384CurvePoint) obj;
        }
        if (obj != null) {
            return new EccP384CurvePoint(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    public static EccP384CurvePoint xOnly(ASN1OctetString aSN1OctetString) {
        return new EccP384CurvePoint(0, aSN1OctetString);
    }

    public static EccP384CurvePoint xOnly(byte[] bArr) {
        return new EccP384CurvePoint(0, new DEROctetString(Arrays.clone(bArr)));
    }

    public static EccP384CurvePoint fill() {
        return new EccP384CurvePoint(1, DERNull.INSTANCE);
    }

    public static EccP384CurvePoint compressedY0(ASN1OctetString aSN1OctetString) {
        return new EccP384CurvePoint(2, aSN1OctetString);
    }

    public static EccP384CurvePoint compressedY1(ASN1OctetString aSN1OctetString) {
        return new EccP384CurvePoint(3, aSN1OctetString);
    }

    public static EccP384CurvePoint compressedY0(byte[] bArr) {
        return new EccP384CurvePoint(2, new DEROctetString(Arrays.clone(bArr)));
    }

    public static EccP384CurvePoint compressedY1(byte[] bArr) {
        return new EccP384CurvePoint(3, new DEROctetString(Arrays.clone(bArr)));
    }

    public static EccP384CurvePoint uncompressedP384(Point384 point384) {
        return new EccP384CurvePoint(4, point384);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getEccP384CurvePoint() {
        return this.eccP384CurvePoint;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.eccP384CurvePoint);
    }

    @Override // org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint
    public byte[] getEncodedPoint() {
        byte[] bArrConcatenate;
        switch (this.choice) {
            case 0:
                throw new IllegalStateException("x Only not implemented");
            case 1:
            default:
                throw new IllegalStateException("unknown point choice");
            case 2:
                byte[] octets = DEROctetString.getInstance(this.eccP384CurvePoint).getOctets();
                bArrConcatenate = new byte[octets.length + 1];
                bArrConcatenate[0] = 2;
                System.arraycopy(octets, 0, bArrConcatenate, 1, octets.length);
                break;
            case 3:
                byte[] octets2 = DEROctetString.getInstance(this.eccP384CurvePoint).getOctets();
                bArrConcatenate = new byte[octets2.length + 1];
                bArrConcatenate[0] = 3;
                System.arraycopy(octets2, 0, bArrConcatenate, 1, octets2.length);
                break;
            case 4:
                ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(this.eccP384CurvePoint);
                bArrConcatenate = Arrays.concatenate(new byte[]{4}, DEROctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets(), DEROctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
                break;
        }
        return bArrConcatenate;
    }
}
