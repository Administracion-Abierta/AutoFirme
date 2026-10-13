package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x9/X9IntegerConverter.class */
public class X9IntegerConverter {
    public int getByteLength(ECCurve eCCurve) {
        return eCCurve.getFieldElementEncodingLength();
    }

    public int getByteLength(ECFieldElement eCFieldElement) {
        return eCFieldElement.getEncodedLength();
    }

    public byte[] integerToBytes(BigInteger bigInteger, int i) {
        byte[] byteArray = bigInteger.toByteArray();
        if (i < byteArray.length) {
            byte[] bArr = new byte[i];
            System.arraycopy(byteArray, byteArray.length - bArr.length, bArr, 0, bArr.length);
            return bArr;
        }
        if (i <= byteArray.length) {
            return byteArray;
        }
        byte[] bArr2 = new byte[i];
        System.arraycopy(byteArray, 0, bArr2, bArr2.length - byteArray.length, byteArray.length);
        return bArr2;
    }
}
