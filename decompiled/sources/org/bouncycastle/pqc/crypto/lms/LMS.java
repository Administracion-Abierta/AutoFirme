package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMS.class */
class LMS {
    static final short D_LEAF = -32126;
    static final short D_INTR = -31869;

    LMS() {
    }

    public static LMSPrivateKeyParameters generateKeys(LMSigParameters lMSigParameters, LMOtsParameters lMOtsParameters, int i, byte[] bArr, byte[] bArr2) throws IllegalArgumentException {
        if (bArr2 == null || bArr2.length < lMSigParameters.getM()) {
            throw new IllegalArgumentException("root seed is less than " + lMSigParameters.getM());
        }
        return new LMSPrivateKeyParameters(lMSigParameters, lMOtsParameters, i, bArr, 1 << lMSigParameters.getH(), bArr2);
    }

    public static LMSSignature generateSign(LMSPrivateKeyParameters lMSPrivateKeyParameters, byte[] bArr) {
        LMSContext lMSContextGenerateLMSContext = lMSPrivateKeyParameters.generateLMSContext();
        lMSContextGenerateLMSContext.update(bArr, 0, bArr.length);
        return generateSign(lMSContextGenerateLMSContext);
    }

    public static LMSSignature generateSign(LMSContext lMSContext) {
        return new LMSSignature(lMSContext.getPrivateKey().getQ(), LM_OTS.lm_ots_generate_signature(lMSContext.getPrivateKey(), lMSContext.getQ(), lMSContext.getC()), lMSContext.getSigParams(), lMSContext.getPath());
    }

    public static boolean verifySignature(LMSPublicKeyParameters lMSPublicKeyParameters, LMSSignature lMSSignature, byte[] bArr) {
        LMSContext lMSContextGenerateOtsContext = lMSPublicKeyParameters.generateOtsContext(lMSSignature);
        LmsUtils.byteArray(bArr, lMSContextGenerateOtsContext);
        return verifySignature(lMSPublicKeyParameters, lMSContextGenerateOtsContext);
    }

    public static boolean verifySignature(LMSPublicKeyParameters lMSPublicKeyParameters, byte[] bArr, byte[] bArr2) {
        LMSContext lMSContextGenerateLMSContext = lMSPublicKeyParameters.generateLMSContext(bArr);
        LmsUtils.byteArray(bArr2, lMSContextGenerateLMSContext);
        return verifySignature(lMSPublicKeyParameters, lMSContextGenerateLMSContext);
    }

    public static boolean verifySignature(LMSPublicKeyParameters lMSPublicKeyParameters, LMSContext lMSContext) {
        LMSSignature lMSSignature = (LMSSignature) lMSContext.getSignature();
        LMSigParameters parameter = lMSSignature.getParameter();
        int h = parameter.getH();
        byte[][] y = lMSSignature.getY();
        byte[] bArrLm_ots_validate_signature_calculate = LM_OTS.lm_ots_validate_signature_calculate(lMSContext);
        int q = (1 << h) + lMSSignature.getQ();
        byte[] i = lMSPublicKeyParameters.getI();
        Digest digest = DigestUtil.getDigest(parameter);
        byte[] bArr = new byte[digest.getDigestSize()];
        digest.update(i, 0, i.length);
        LmsUtils.u32str(q, digest);
        LmsUtils.u16str((short) -32126, digest);
        digest.update(bArrLm_ots_validate_signature_calculate, 0, bArrLm_ots_validate_signature_calculate.length);
        digest.doFinal(bArr, 0);
        int i2 = 0;
        while (q > 1) {
            if ((q & 1) == 1) {
                digest.update(i, 0, i.length);
                LmsUtils.u32str(q / 2, digest);
                LmsUtils.u16str((short) -31869, digest);
                digest.update(y[i2], 0, y[i2].length);
                digest.update(bArr, 0, bArr.length);
                digest.doFinal(bArr, 0);
            } else {
                digest.update(i, 0, i.length);
                LmsUtils.u32str(q / 2, digest);
                LmsUtils.u16str((short) -31869, digest);
                digest.update(bArr, 0, bArr.length);
                digest.update(y[i2], 0, y[i2].length);
                digest.doFinal(bArr, 0);
            }
            q /= 2;
            i2++;
            if (i2 == y.length && q > 1) {
                return false;
            }
        }
        return lMSPublicKeyParameters.matchesT1(bArr);
    }
}
