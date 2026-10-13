package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.ExhaustedPrivateKeyException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMSPrivateKeyParameters.class */
public class LMSPrivateKeyParameters extends LMSKeyParameters implements LMSContextBasedSigner {
    private static CacheKey T1 = new CacheKey(1);
    private static CacheKey[] internedKeys = new CacheKey[129];
    private final byte[] I;
    private final LMSigParameters parameters;
    private final LMOtsParameters otsParameters;
    private final int maxQ;
    private final byte[] masterSecret;
    private final Map<CacheKey, byte[]> tCache;
    private final int maxCacheR;
    private final Digest tDigest;
    private int q;
    private LMSPublicKeyParameters publicKey;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMSPrivateKeyParameters$CacheKey.class */
    private static class CacheKey {
        private final int index;

        CacheKey(int i) {
            this.index = i;
        }

        public int hashCode() {
            return this.index;
        }

        public boolean equals(Object obj) {
            return (obj instanceof CacheKey) && ((CacheKey) obj).index == this.index;
        }
    }

    public LMSPrivateKeyParameters(LMSigParameters lMSigParameters, LMOtsParameters lMOtsParameters, int i, byte[] bArr, int i2, byte[] bArr2) {
        super(true);
        this.parameters = lMSigParameters;
        this.otsParameters = lMOtsParameters;
        this.q = i;
        this.I = Arrays.clone(bArr);
        this.maxQ = i2;
        this.masterSecret = Arrays.clone(bArr2);
        this.maxCacheR = 1 << (this.parameters.getH() + 1);
        this.tCache = new WeakHashMap();
        this.tDigest = DigestUtil.getDigest(lMSigParameters);
    }

    private LMSPrivateKeyParameters(LMSPrivateKeyParameters lMSPrivateKeyParameters, int i, int i2) {
        super(true);
        this.parameters = lMSPrivateKeyParameters.parameters;
        this.otsParameters = lMSPrivateKeyParameters.otsParameters;
        this.q = i;
        this.I = lMSPrivateKeyParameters.I;
        this.maxQ = i2;
        this.masterSecret = lMSPrivateKeyParameters.masterSecret;
        this.maxCacheR = 1 << this.parameters.getH();
        this.tCache = lMSPrivateKeyParameters.tCache;
        this.tDigest = DigestUtil.getDigest(this.parameters);
        this.publicKey = lMSPrivateKeyParameters.publicKey;
    }

    public static LMSPrivateKeyParameters getInstance(byte[] bArr, byte[] bArr2) throws IOException {
        LMSPrivateKeyParameters lMSPrivateKeyParameters = getInstance(bArr);
        lMSPrivateKeyParameters.publicKey = LMSPublicKeyParameters.getInstance(bArr2);
        return lMSPrivateKeyParameters;
    }

    public static LMSPrivateKeyParameters getInstance(Object obj) throws IOException {
        if (obj instanceof LMSPrivateKeyParameters) {
            return (LMSPrivateKeyParameters) obj;
        }
        if (!(obj instanceof DataInputStream)) {
            if (!(obj instanceof byte[])) {
                if (obj instanceof InputStream) {
                    return getInstance(Streams.readAll((InputStream) obj));
                }
                throw new IllegalArgumentException("cannot parse " + obj);
            }
            DataInputStream dataInputStream = null;
            try {
                DataInputStream dataInputStream2 = new DataInputStream(new ByteArrayInputStream((byte[]) obj));
                LMSPrivateKeyParameters lMSPrivateKeyParameters = getInstance(dataInputStream2);
                if (dataInputStream2 != null) {
                }
                return lMSPrivateKeyParameters;
            } finally {
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
            }
        }
        DataInputStream dataInputStream3 = (DataInputStream) obj;
        if (dataInputStream3.readInt() != 0) {
            throw new IllegalStateException("expected version 0 lms private key");
        }
        LMSigParameters parametersForType = LMSigParameters.getParametersForType(dataInputStream3.readInt());
        LMOtsParameters parametersForType2 = LMOtsParameters.getParametersForType(dataInputStream3.readInt());
        byte[] bArr = new byte[16];
        dataInputStream3.readFully(bArr);
        int i = dataInputStream3.readInt();
        int i2 = dataInputStream3.readInt();
        int i3 = dataInputStream3.readInt();
        if (i3 < 0) {
            throw new IllegalStateException("secret length less than zero");
        }
        if (i3 > dataInputStream3.available()) {
            throw new IOException("secret length exceeded " + dataInputStream3.available());
        }
        byte[] bArr2 = new byte[i3];
        dataInputStream3.readFully(bArr2);
        return new LMSPrivateKeyParameters(parametersForType, parametersForType2, i, bArr, i2, bArr2);
    }

    LMOtsPrivateKey getCurrentOTSKey() {
        LMOtsPrivateKey lMOtsPrivateKey;
        synchronized (this) {
            if (this.q >= this.maxQ) {
                throw new ExhaustedPrivateKeyException("ots private keys expired");
            }
            lMOtsPrivateKey = new LMOtsPrivateKey(this.otsParameters, this.I, this.q, this.masterSecret);
        }
        return lMOtsPrivateKey;
    }

    public synchronized int getIndex() {
        return this.q;
    }

    synchronized void incIndex() {
        this.q++;
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSContextBasedSigner
    public LMSContext generateLMSContext() {
        int h = getSigParameters().getH();
        int index = getIndex();
        LMOtsPrivateKey nextOtsPrivateKey = getNextOtsPrivateKey();
        int i = (1 << h) + index;
        byte[][] bArr = new byte[h][];
        for (int i2 = 0; i2 < h; i2++) {
            bArr[i2] = findT((i / (1 << i2)) ^ 1);
        }
        return nextOtsPrivateKey.getSignatureContext(getSigParameters(), bArr);
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSContextBasedSigner
    public byte[] generateSignature(LMSContext lMSContext) {
        try {
            return LMS.generateSign(lMSContext).getEncoded();
        } catch (IOException e) {
            throw new IllegalStateException("unable to encode signature: " + e.getMessage(), e);
        }
    }

    LMOtsPrivateKey getNextOtsPrivateKey() {
        LMOtsPrivateKey lMOtsPrivateKey;
        synchronized (this) {
            if (this.q >= this.maxQ) {
                throw new ExhaustedPrivateKeyException("ots private key exhausted");
            }
            lMOtsPrivateKey = new LMOtsPrivateKey(this.otsParameters, this.I, this.q, this.masterSecret);
            incIndex();
        }
        return lMOtsPrivateKey;
    }

    public LMSPrivateKeyParameters extractKeyShard(int i) {
        LMSPrivateKeyParameters lMSPrivateKeyParameters;
        synchronized (this) {
            try {
                if (i < 0) {
                    throw new IllegalArgumentException("usageCount cannot be negative");
                }
                if (i > this.maxQ - this.q) {
                    throw new IllegalArgumentException("usageCount exceeds usages remaining");
                }
                int i2 = this.q;
                int i3 = this.q + i;
                this.q = i3;
                lMSPrivateKeyParameters = new LMSPrivateKeyParameters(this, i2, i3);
            } catch (Throwable th) {
                throw th;
            }
        }
        return lMSPrivateKeyParameters;
    }

    public LMSigParameters getSigParameters() {
        return this.parameters;
    }

    public LMOtsParameters getOtsParameters() {
        return this.otsParameters;
    }

    public byte[] getI() {
        return Arrays.clone(this.I);
    }

    public byte[] getMasterSecret() {
        return Arrays.clone(this.masterSecret);
    }

    public int getIndexLimit() {
        return this.maxQ;
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSContextBasedSigner
    public long getUsagesRemaining() {
        return getIndexLimit() - getIndex();
    }

    public LMSPublicKeyParameters getPublicKey() {
        LMSPublicKeyParameters lMSPublicKeyParameters;
        synchronized (this) {
            if (this.publicKey == null) {
                this.publicKey = new LMSPublicKeyParameters(this.parameters, this.otsParameters, findT(T1), this.I);
            }
            lMSPublicKeyParameters = this.publicKey;
        }
        return lMSPublicKeyParameters;
    }

    byte[] findT(int i) {
        if (i < this.maxCacheR) {
            return findT(i < internedKeys.length ? internedKeys[i] : new CacheKey(i));
        }
        return calcT(i);
    }

    private byte[] findT(CacheKey cacheKey) {
        synchronized (this.tCache) {
            byte[] bArr = this.tCache.get(cacheKey);
            if (bArr != null) {
                return bArr;
            }
            byte[] bArrCalcT = calcT(cacheKey.index);
            this.tCache.put(cacheKey, bArrCalcT);
            return bArrCalcT;
        }
    }

    private byte[] calcT(int i) {
        int h = 1 << getSigParameters().getH();
        if (i >= h) {
            LmsUtils.byteArray(getI(), this.tDigest);
            LmsUtils.u32str(i, this.tDigest);
            LmsUtils.u16str((short) -32126, this.tDigest);
            LmsUtils.byteArray(LM_OTS.lms_ots_generatePublicKey(getOtsParameters(), getI(), i - h, getMasterSecret()), this.tDigest);
            byte[] bArr = new byte[this.tDigest.getDigestSize()];
            this.tDigest.doFinal(bArr, 0);
            return bArr;
        }
        byte[] bArrFindT = findT(2 * i);
        byte[] bArrFindT2 = findT((2 * i) + 1);
        LmsUtils.byteArray(getI(), this.tDigest);
        LmsUtils.u32str(i, this.tDigest);
        LmsUtils.u16str((short) -31869, this.tDigest);
        LmsUtils.byteArray(bArrFindT, this.tDigest);
        LmsUtils.byteArray(bArrFindT2, this.tDigest);
        byte[] bArr2 = new byte[this.tDigest.getDigestSize()];
        this.tDigest.doFinal(bArr2, 0);
        return bArr2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LMSPrivateKeyParameters lMSPrivateKeyParameters = (LMSPrivateKeyParameters) obj;
        if (this.q != lMSPrivateKeyParameters.q || this.maxQ != lMSPrivateKeyParameters.maxQ || !Arrays.areEqual(this.I, lMSPrivateKeyParameters.I)) {
            return false;
        }
        if (this.parameters != null) {
            if (!this.parameters.equals(lMSPrivateKeyParameters.parameters)) {
                return false;
            }
        } else if (lMSPrivateKeyParameters.parameters != null) {
            return false;
        }
        if (this.otsParameters != null) {
            if (!this.otsParameters.equals(lMSPrivateKeyParameters.otsParameters)) {
                return false;
            }
        } else if (lMSPrivateKeyParameters.otsParameters != null) {
            return false;
        }
        return Arrays.areEqual(this.masterSecret, lMSPrivateKeyParameters.masterSecret);
    }

    public int hashCode() {
        return (31 * ((31 * ((31 * ((31 * ((31 * this.q) + Arrays.hashCode(this.I))) + (this.parameters != null ? this.parameters.hashCode() : 0))) + (this.otsParameters != null ? this.otsParameters.hashCode() : 0))) + this.maxQ)) + Arrays.hashCode(this.masterSecret);
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSKeyParameters, org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return Composer.compose().u32str(0).u32str(this.parameters.getType()).u32str(this.otsParameters.getType()).bytes(this.I).u32str(this.q).u32str(this.maxQ).u32str(this.masterSecret.length).bytes(this.masterSecret).build();
    }

    static {
        internedKeys[1] = T1;
        for (int i = 2; i < internedKeys.length; i++) {
            internedKeys[i] = new CacheKey(i);
        }
    }
}
