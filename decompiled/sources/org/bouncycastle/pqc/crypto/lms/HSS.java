package org.bouncycastle.pqc.crypto.lms;

import java.util.Arrays;
import java.util.List;
import org.bouncycastle.pqc.crypto.ExhaustedPrivateKeyException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/HSS.class */
class HSS {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/HSS$PlaceholderLMSPrivateKey.class */
    static class PlaceholderLMSPrivateKey extends LMSPrivateKeyParameters {
        public PlaceholderLMSPrivateKey(LMSigParameters lMSigParameters, LMOtsParameters lMOtsParameters, int i, byte[] bArr, int i2, byte[] bArr2) {
            super(lMSigParameters, lMOtsParameters, i, bArr, i2, bArr2);
        }

        @Override // org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters
        LMOtsPrivateKey getNextOtsPrivateKey() {
            throw new RuntimeException("placeholder only");
        }

        @Override // org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters
        public LMSPublicKeyParameters getPublicKey() {
            throw new RuntimeException("placeholder only");
        }
    }

    HSS() {
    }

    public static HSSPrivateKeyParameters generateHSSKeyPair(HSSKeyGenerationParameters hSSKeyGenerationParameters) {
        LMSPrivateKeyParameters[] lMSPrivateKeyParametersArr = new LMSPrivateKeyParameters[hSSKeyGenerationParameters.getDepth()];
        LMSSignature[] lMSSignatureArr = new LMSSignature[hSSKeyGenerationParameters.getDepth() - 1];
        byte[] bArr = new byte[hSSKeyGenerationParameters.getLmsParameters()[0].getLMSigParam().getM()];
        hSSKeyGenerationParameters.getRandom().nextBytes(bArr);
        byte[] bArr2 = new byte[16];
        hSSKeyGenerationParameters.getRandom().nextBytes(bArr2);
        byte[] bArr3 = new byte[0];
        long h = 1;
        for (int i = 0; i < lMSPrivateKeyParametersArr.length; i++) {
            if (i == 0) {
                lMSPrivateKeyParametersArr[i] = new LMSPrivateKeyParameters(hSSKeyGenerationParameters.getLmsParameters()[i].getLMSigParam(), hSSKeyGenerationParameters.getLmsParameters()[i].getLMOTSParam(), 0, bArr2, 1 << hSSKeyGenerationParameters.getLmsParameters()[i].getLMSigParam().getH(), bArr);
            } else {
                lMSPrivateKeyParametersArr[i] = new PlaceholderLMSPrivateKey(hSSKeyGenerationParameters.getLmsParameters()[i].getLMSigParam(), hSSKeyGenerationParameters.getLmsParameters()[i].getLMOTSParam(), -1, bArr3, 1 << hSSKeyGenerationParameters.getLmsParameters()[i].getLMSigParam().getH(), bArr3);
            }
            h *= (long) (1 << hSSKeyGenerationParameters.getLmsParameters()[i].getLMSigParam().getH());
        }
        if (h == 0) {
            h = Long.MAX_VALUE;
        }
        return new HSSPrivateKeyParameters(hSSKeyGenerationParameters.getDepth(), Arrays.asList(lMSPrivateKeyParametersArr), Arrays.asList(lMSSignatureArr), 0L, h);
    }

    public static void incrementIndex(HSSPrivateKeyParameters hSSPrivateKeyParameters) {
        synchronized (hSSPrivateKeyParameters) {
            rangeTestKeys(hSSPrivateKeyParameters);
            hSSPrivateKeyParameters.incIndex();
            hSSPrivateKeyParameters.getKeys().get(hSSPrivateKeyParameters.getL() - 1).incIndex();
        }
    }

    static void rangeTestKeys(HSSPrivateKeyParameters hSSPrivateKeyParameters) {
        synchronized (hSSPrivateKeyParameters) {
            if (hSSPrivateKeyParameters.getIndex() >= hSSPrivateKeyParameters.getIndexLimit()) {
                throw new ExhaustedPrivateKeyException("hss private key" + (hSSPrivateKeyParameters.isShard() ? " shard" : "") + " is exhausted");
            }
            int l = hSSPrivateKeyParameters.getL();
            int i = l;
            List<LMSPrivateKeyParameters> keys = hSSPrivateKeyParameters.getKeys();
            while (keys.get(i - 1).getIndex() == (1 << keys.get(i - 1).getSigParameters().getH())) {
                i--;
                if (i == 0) {
                    throw new ExhaustedPrivateKeyException("hss private key" + (hSSPrivateKeyParameters.isShard() ? " shard" : "") + " is exhausted the maximum limit for this HSS private key");
                }
            }
            while (i < l) {
                hSSPrivateKeyParameters.replaceConsumedKey(i);
                i++;
            }
        }
    }

    public static HSSSignature generateSignature(HSSPrivateKeyParameters hSSPrivateKeyParameters, byte[] bArr) {
        LMSPrivateKeyParameters lMSPrivateKeyParameters;
        LMSSignedPubKey[] lMSSignedPubKeyArr;
        int l = hSSPrivateKeyParameters.getL();
        synchronized (hSSPrivateKeyParameters) {
            rangeTestKeys(hSSPrivateKeyParameters);
            List<LMSPrivateKeyParameters> keys = hSSPrivateKeyParameters.getKeys();
            List<LMSSignature> sig = hSSPrivateKeyParameters.getSig();
            lMSPrivateKeyParameters = hSSPrivateKeyParameters.getKeys().get(l - 1);
            lMSSignedPubKeyArr = new LMSSignedPubKey[l - 1];
            for (int i = 0; i < l - 1; i++) {
                lMSSignedPubKeyArr[i] = new LMSSignedPubKey(sig.get(i), keys.get(i + 1).getPublicKey());
            }
            hSSPrivateKeyParameters.incIndex();
        }
        LMSContext lMSContextWithSignedPublicKeys = lMSPrivateKeyParameters.generateLMSContext().withSignedPublicKeys(lMSSignedPubKeyArr);
        lMSContextWithSignedPublicKeys.update(bArr, 0, bArr.length);
        return generateSignature(l, lMSContextWithSignedPublicKeys);
    }

    public static HSSSignature generateSignature(int i, LMSContext lMSContext) {
        return new HSSSignature(i - 1, lMSContext.getSignedPubKeys(), LMS.generateSign(lMSContext));
    }

    public static boolean verifySignature(HSSPublicKeyParameters hSSPublicKeyParameters, HSSSignature hSSSignature, byte[] bArr) {
        int i = hSSSignature.getlMinus1();
        if (i + 1 != hSSPublicKeyParameters.getL()) {
            return false;
        }
        LMSSignature[] lMSSignatureArr = new LMSSignature[i + 1];
        LMSPublicKeyParameters[] lMSPublicKeyParametersArr = new LMSPublicKeyParameters[i];
        for (int i2 = 0; i2 < i; i2++) {
            lMSSignatureArr[i2] = hSSSignature.getSignedPubKey()[i2].getSignature();
            lMSPublicKeyParametersArr[i2] = hSSSignature.getSignedPubKey()[i2].getPublicKey();
        }
        lMSSignatureArr[i] = hSSSignature.getSignature();
        LMSPublicKeyParameters lMSPublicKey = hSSPublicKeyParameters.getLMSPublicKey();
        for (int i3 = 0; i3 < i; i3++) {
            if (!LMS.verifySignature(lMSPublicKey, lMSSignatureArr[i3], lMSPublicKeyParametersArr[i3].toByteArray())) {
                return false;
            }
            try {
                lMSPublicKey = lMSPublicKeyParametersArr[i3];
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        return LMS.verifySignature(lMSPublicKey, lMSSignatureArr[i], bArr);
    }
}
