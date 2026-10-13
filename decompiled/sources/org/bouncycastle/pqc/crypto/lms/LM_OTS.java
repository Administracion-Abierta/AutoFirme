package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LM_OTS.class */
class LM_OTS {
    private static final short D_PBLC = -32640;
    private static final int ITER_K = 20;
    private static final int ITER_PREV = 23;
    private static final int ITER_J = 22;
    static final int SEED_RANDOMISER_INDEX = -3;
    static final int MAX_HASH = 32;
    static final short D_MESG = -32383;

    LM_OTS() {
    }

    public static int coef(byte[] bArr, int i, int i2) {
        return (bArr[(i * i2) / 8] >>> (i2 * ((i ^ (-1)) & ((8 / i2) - 1)))) & ((1 << i2) - 1);
    }

    public static int cksm(byte[] bArr, int i, LMOtsParameters lMOtsParameters) {
        int iCoef = 0;
        int w = (1 << lMOtsParameters.getW()) - 1;
        for (int i2 = 0; i2 < (i * 8) / lMOtsParameters.getW(); i2++) {
            iCoef = (iCoef + w) - coef(bArr, i2, lMOtsParameters.getW());
        }
        return iCoef << lMOtsParameters.getLs();
    }

    public static LMOtsPublicKey lms_ots_generatePublicKey(LMOtsPrivateKey lMOtsPrivateKey) {
        return new LMOtsPublicKey(lMOtsPrivateKey.getParameter(), lMOtsPrivateKey.getI(), lMOtsPrivateKey.getQ(), lms_ots_generatePublicKey(lMOtsPrivateKey.getParameter(), lMOtsPrivateKey.getI(), lMOtsPrivateKey.getQ(), lMOtsPrivateKey.getMasterSecret()));
    }

    static byte[] lms_ots_generatePublicKey(LMOtsParameters lMOtsParameters, byte[] bArr, int i, byte[] bArr2) {
        Digest digest = DigestUtil.getDigest(lMOtsParameters);
        byte[] bArrBuild = Composer.compose().bytes(bArr).u32str(i).u16str(D_PBLC).padUntil(0, 22).build();
        digest.update(bArrBuild, 0, bArrBuild.length);
        Digest digest2 = DigestUtil.getDigest(lMOtsParameters);
        byte[] bArrBuild2 = Composer.compose().bytes(bArr).u32str(i).padUntil(0, 23 + digest2.getDigestSize()).build();
        SeedDerive seedDerive = new SeedDerive(bArr, bArr2, DigestUtil.getDigest(lMOtsParameters));
        seedDerive.setQ(i);
        seedDerive.setJ(0);
        int p = lMOtsParameters.getP();
        int n = lMOtsParameters.getN();
        int w = (1 << lMOtsParameters.getW()) - 1;
        int i2 = 0;
        while (i2 < p) {
            seedDerive.deriveSeed(bArrBuild2, i2 < p - 1, 23);
            Pack.shortToBigEndian((short) i2, bArrBuild2, 20);
            for (int i3 = 0; i3 < w; i3++) {
                bArrBuild2[22] = (byte) i3;
                digest2.update(bArrBuild2, 0, bArrBuild2.length);
                digest2.doFinal(bArrBuild2, 23);
            }
            digest.update(bArrBuild2, 23, n);
            i2++;
        }
        byte[] bArr3 = new byte[digest.getDigestSize()];
        digest.doFinal(bArr3, 0);
        return bArr3;
    }

    public static LMOtsSignature lm_ots_generate_signature(LMSigParameters lMSigParameters, LMOtsPrivateKey lMOtsPrivateKey, byte[][] bArr, byte[] bArr2, boolean z) {
        byte[] c;
        byte[] q = new byte[34];
        if (z) {
            int n = lMOtsPrivateKey.getParameter().getN();
            c = new byte[n];
            System.arraycopy(bArr2, 0, q, 0, n);
        } else {
            LMSContext signatureContext = lMOtsPrivateKey.getSignatureContext(lMSigParameters, bArr);
            LmsUtils.byteArray(bArr2, 0, bArr2.length, signatureContext);
            c = signatureContext.getC();
            q = signatureContext.getQ();
        }
        return lm_ots_generate_signature(lMOtsPrivateKey, q, c);
    }

    public static LMOtsSignature lm_ots_generate_signature(LMOtsPrivateKey lMOtsPrivateKey, byte[] bArr, byte[] bArr2) {
        LMOtsParameters parameter = lMOtsPrivateKey.getParameter();
        int n = parameter.getN();
        int p = parameter.getP();
        int w = parameter.getW();
        byte[] bArr3 = new byte[p * n];
        Digest digest = DigestUtil.getDigest(parameter);
        SeedDerive derivationFunction = lMOtsPrivateKey.getDerivationFunction();
        int iCksm = cksm(bArr, n, parameter);
        bArr[n] = (byte) ((iCksm >>> 8) & 255);
        bArr[n + 1] = (byte) iCksm;
        byte[] bArrBuild = Composer.compose().bytes(lMOtsPrivateKey.getI()).u32str(lMOtsPrivateKey.getQ()).padUntil(0, 23 + n).build();
        derivationFunction.setJ(0);
        int i = 0;
        while (i < p) {
            Pack.shortToBigEndian((short) i, bArrBuild, 20);
            derivationFunction.deriveSeed(bArrBuild, i < p - 1, 23);
            int iCoef = coef(bArr, i, w);
            for (int i2 = 0; i2 < iCoef; i2++) {
                bArrBuild[22] = (byte) i2;
                digest.update(bArrBuild, 0, 23 + n);
                digest.doFinal(bArrBuild, 23);
            }
            System.arraycopy(bArrBuild, 23, bArr3, n * i, n);
            i++;
        }
        return new LMOtsSignature(parameter, bArr2, bArr3);
    }

    public static boolean lm_ots_validate_signature(LMOtsPublicKey lMOtsPublicKey, LMOtsSignature lMOtsSignature, byte[] bArr, boolean z) throws LMSException {
        if (lMOtsSignature.getType().equals(lMOtsPublicKey.getParameter())) {
            return Arrays.areEqual(lm_ots_validate_signature_calculate(lMOtsPublicKey, lMOtsSignature, bArr), lMOtsPublicKey.getK());
        }
        throw new LMSException("public key and signature ots types do not match");
    }

    public static byte[] lm_ots_validate_signature_calculate(LMOtsPublicKey lMOtsPublicKey, LMOtsSignature lMOtsSignature, byte[] bArr) {
        LMSContext lMSContextCreateOtsContext = lMOtsPublicKey.createOtsContext(lMOtsSignature);
        LmsUtils.byteArray(bArr, lMSContextCreateOtsContext);
        return lm_ots_validate_signature_calculate(lMSContextCreateOtsContext);
    }

    public static byte[] lm_ots_validate_signature_calculate(LMSContext lMSContext) {
        LMOtsPublicKey publicKey = lMSContext.getPublicKey();
        LMOtsParameters parameter = publicKey.getParameter();
        Object signature = lMSContext.getSignature();
        LMOtsSignature otsSignature = signature instanceof LMSSignature ? ((LMSSignature) signature).getOtsSignature() : (LMOtsSignature) signature;
        int n = parameter.getN();
        int w = parameter.getW();
        int p = parameter.getP();
        byte[] q = lMSContext.getQ();
        int iCksm = cksm(q, n, parameter);
        q[n] = (byte) ((iCksm >>> 8) & 255);
        q[n + 1] = (byte) iCksm;
        byte[] i = publicKey.getI();
        int q2 = publicKey.getQ();
        Digest digest = DigestUtil.getDigest(parameter);
        LmsUtils.byteArray(i, digest);
        LmsUtils.u32str(q2, digest);
        LmsUtils.u16str((short) -32640, digest);
        byte[] bArrBuild = Composer.compose().bytes(i).u32str(q2).padUntil(0, 23 + n).build();
        int i2 = (1 << w) - 1;
        byte[] y = otsSignature.getY();
        Digest digest2 = DigestUtil.getDigest(parameter);
        for (int i3 = 0; i3 < p; i3++) {
            Pack.shortToBigEndian((short) i3, bArrBuild, 20);
            System.arraycopy(y, i3 * n, bArrBuild, 23, n);
            for (int iCoef = coef(q, i3, w); iCoef < i2; iCoef++) {
                bArrBuild[22] = (byte) iCoef;
                digest2.update(bArrBuild, 0, 23 + n);
                digest2.doFinal(bArrBuild, 23);
            }
            digest.update(bArrBuild, 23, n);
        }
        byte[] bArr = new byte[n];
        digest.doFinal(bArr, 0);
        return bArr;
    }
}
