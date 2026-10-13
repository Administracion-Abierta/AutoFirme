package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/EccP256CurvePoint.class */
public class EccP256CurvePoint extends EccCurvePoint implements ASN1Choice {
    public static final int xonly = 0;
    public static final int fill = 1;
    public static final int compressedY0 = 2;
    public static final int compressedY1 = 3;
    public static final int uncompressedP256 = 4;
    private final int choice;
    private final ASN1Encodable eccp256CurvePoint;

    public EccP256CurvePoint(int i, ASN1Encodable aSN1Encodable) {
        this.choice = i;
        this.eccp256CurvePoint = aSN1Encodable;
    }

    private EccP256CurvePoint(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (aSN1TaggedObject.getTagNo()) {
            case 0:
            case 2:
            case 3:
                this.eccp256CurvePoint = ASN1OctetString.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 1:
                this.eccp256CurvePoint = ASN1Null.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 4:
                this.eccp256CurvePoint = Point256.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            default:
                throw new IllegalArgumentException("invalid choice value " + aSN1TaggedObject.getTagNo());
        }
    }

    public static EccP256CurvePoint xOnly(ASN1OctetString aSN1OctetString) {
        return new EccP256CurvePoint(0, aSN1OctetString);
    }

    public static EccP256CurvePoint xOnly(byte[] bArr) {
        return new EccP256CurvePoint(0, new DEROctetString(Arrays.clone(bArr)));
    }

    public static EccP256CurvePoint fill() {
        return new EccP256CurvePoint(1, DERNull.INSTANCE);
    }

    public static EccP256CurvePoint compressedY0(ASN1OctetString aSN1OctetString) {
        return new EccP256CurvePoint(2, aSN1OctetString);
    }

    public static EccP256CurvePoint compressedY1(ASN1OctetString aSN1OctetString) {
        return new EccP256CurvePoint(3, aSN1OctetString);
    }

    public static EccP256CurvePoint compressedY0(byte[] bArr) {
        return new EccP256CurvePoint(2, new DEROctetString(Arrays.clone(bArr)));
    }

    public static EccP256CurvePoint compressedY1(byte[] bArr) {
        return new EccP256CurvePoint(3, new DEROctetString(Arrays.clone(bArr)));
    }

    public static EccP256CurvePoint uncompressedP256(Point256 point256) {
        return new EccP256CurvePoint(4, point256);
    }

    public static EccP256CurvePoint uncompressedP256(BigInteger bigInteger, BigInteger bigInteger2) {
        return new EccP256CurvePoint(4, Point256.builder().setX(bigInteger).setY(bigInteger2).createPoint256());
    }

    public static EccP256CurvePoint createEncodedPoint(byte[] bArr) {
        if (bArr[0] == 2) {
            byte[] bArr2 = new byte[bArr.length - 1];
            System.arraycopy(bArr, 1, bArr2, 0, bArr2.length);
            return new EccP256CurvePoint(2, new DEROctetString(bArr2));
        }
        if (bArr[0] == 3) {
            byte[] bArr3 = new byte[bArr.length - 1];
            System.arraycopy(bArr, 1, bArr3, 0, bArr3.length);
            return new EccP256CurvePoint(3, new DEROctetString(bArr3));
        }
        if (bArr[0] == 4) {
            return new EccP256CurvePoint(4, new Point256(new DEROctetString(Arrays.copyOfRange(bArr, 1, 34)), new DEROctetString(Arrays.copyOfRange(bArr, 34, 66))));
        }
        throw new IllegalArgumentException("unrecognised encoding " + ((int) bArr[0]));
    }

    public EccP256CurvePoint createCompressed(ECPoint eCPoint) {
        int i = 0;
        byte[] encoded = eCPoint.getEncoded(true);
        if (encoded[0] == 2) {
            i = 2;
        } else if (encoded[0] == 3) {
            i = 3;
        }
        byte[] bArr = new byte[encoded.length - 1];
        System.arraycopy(encoded, 1, bArr, 0, bArr.length);
        return new EccP256CurvePoint(i, new DEROctetString(bArr));
    }

    public static EccP256CurvePoint getInstance(Object obj) {
        if (obj instanceof EccP256CurvePoint) {
            return (EccP256CurvePoint) obj;
        }
        if (obj != null) {
            return new EccP256CurvePoint(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    public ASN1Encodable getEccp256CurvePoint() {
        return this.eccp256CurvePoint;
    }

    public int getChoice() {
        return this.choice;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.eccp256CurvePoint);
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
                byte[] octets = DEROctetString.getInstance(this.eccp256CurvePoint).getOctets();
                bArrConcatenate = new byte[octets.length + 1];
                bArrConcatenate[0] = 2;
                System.arraycopy(octets, 0, bArrConcatenate, 1, octets.length);
                break;
            case 3:
                byte[] octets2 = DEROctetString.getInstance(this.eccp256CurvePoint).getOctets();
                bArrConcatenate = new byte[octets2.length + 1];
                bArrConcatenate[0] = 3;
                System.arraycopy(octets2, 0, bArrConcatenate, 1, octets2.length);
                break;
            case 4:
                Point256 point256 = Point256.getInstance(this.eccp256CurvePoint);
                bArrConcatenate = Arrays.concatenate(new byte[]{4}, point256.getX().getOctets(), point256.getY().getOctets());
                break;
        }
        return bArrConcatenate;
    }
}
