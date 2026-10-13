package org.bouncycastle.crypto.agreement.ecjpake;

import java.math.BigInteger;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/ecjpake/ECJPAKECurve.class */
public class ECJPAKECurve {
    private final ECCurve.Fp curve;
    private final ECPoint g;

    public ECJPAKECurve(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4, BigInteger bigInteger5, BigInteger bigInteger6, BigInteger bigInteger7) {
        ECJPAKEUtil.validateNotNull(bigInteger2, PDPageLabelRange.STYLE_LETTERS_LOWER);
        ECJPAKEUtil.validateNotNull(bigInteger3, OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE);
        ECJPAKEUtil.validateNotNull(bigInteger, OperatorName.SAVE);
        ECJPAKEUtil.validateNotNull(bigInteger4, OperatorName.ENDPATH);
        ECJPAKEUtil.validateNotNull(bigInteger5, OperatorName.CLOSE_PATH);
        ECJPAKEUtil.validateNotNull(bigInteger6, "g_x");
        ECJPAKEUtil.validateNotNull(bigInteger7, "g_y");
        if (!bigInteger.isProbablePrime(20)) {
            throw new IllegalArgumentException("Field size q must be prime");
        }
        if (bigInteger2.compareTo(BigInteger.ZERO) < 0 || bigInteger2.compareTo(bigInteger) >= 0) {
            throw new IllegalArgumentException("The parameter 'a' is not in the field [0, q-1]");
        }
        if (bigInteger3.compareTo(BigInteger.ZERO) < 0 || bigInteger3.compareTo(bigInteger) >= 0) {
            throw new IllegalArgumentException("The parameter 'b' is not in the field [0, q-1]");
        }
        if (calculateDeterminant(bigInteger, bigInteger2, bigInteger3).equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("The curve is singular, i.e the discriminant is equal to 0 mod q.");
        }
        if (!bigInteger4.isProbablePrime(20)) {
            throw new IllegalArgumentException("The order n must be prime");
        }
        ECCurve.Fp fp = new ECCurve.Fp(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5);
        ECPoint eCPointCreatePoint = fp.createPoint(bigInteger6, bigInteger7);
        if (!eCPointCreatePoint.isValid()) {
            throw new IllegalArgumentException("The base point G does not lie on the curve.");
        }
        this.curve = fp;
        this.g = eCPointCreatePoint;
    }

    ECJPAKECurve(ECCurve.Fp fp, ECPoint eCPoint) {
        ECJPAKEUtil.validateNotNull(fp, "curve");
        ECJPAKEUtil.validateNotNull(eCPoint, OperatorName.NON_STROKING_GRAY);
        ECJPAKEUtil.validateNotNull(fp.getOrder(), OperatorName.ENDPATH);
        ECJPAKEUtil.validateNotNull(fp.getCofactor(), OperatorName.CLOSE_PATH);
        this.curve = fp;
        this.g = eCPoint;
    }

    public ECCurve.Fp getCurve() {
        return this.curve;
    }

    public ECPoint getG() {
        return this.g;
    }

    public BigInteger getA() {
        return this.curve.getA().toBigInteger();
    }

    public BigInteger getB() {
        return this.curve.getB().toBigInteger();
    }

    public BigInteger getN() {
        return this.curve.getOrder();
    }

    public BigInteger getH() {
        return this.curve.getCofactor();
    }

    public BigInteger getQ() {
        return this.curve.getQ();
    }

    private static BigInteger calculateDeterminant(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3) {
        return bigInteger2.multiply(bigInteger2).mod(bigInteger).multiply(bigInteger2).mod(bigInteger).shiftLeft(2).add(bigInteger3.multiply(bigInteger3).mod(bigInteger).multiply(BigInteger.valueOf(27L))).mod(bigInteger);
    }
}
