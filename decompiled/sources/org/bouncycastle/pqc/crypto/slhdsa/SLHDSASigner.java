package org.bouncycastle.pqc.crypto.slhdsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/slhdsa/SLHDSASigner.class */
public class SLHDSASigner implements MessageSigner {
    private static final byte[] DEFAULT_PREFIX = {0, 0};
    private byte[] msgPrefix;
    private SLHDSAPublicKeyParameters pubKey;
    private SLHDSAPrivateKeyParameters privKey;
    private SecureRandom random;

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public void init(boolean z, CipherParameters cipherParameters) {
        SLHDSAParameters parameters;
        if (cipherParameters instanceof ParametersWithContext) {
            ParametersWithContext parametersWithContext = (ParametersWithContext) cipherParameters;
            cipherParameters = parametersWithContext.getParameters();
            int contextLength = parametersWithContext.getContextLength();
            if (contextLength > 255) {
                throw new IllegalArgumentException("context too long");
            }
            this.msgPrefix = new byte[2 + contextLength];
            this.msgPrefix[0] = 0;
            this.msgPrefix[1] = (byte) contextLength;
            parametersWithContext.copyContextTo(this.msgPrefix, 2, contextLength);
        } else {
            this.msgPrefix = DEFAULT_PREFIX;
        }
        if (z) {
            this.pubKey = null;
            if (cipherParameters instanceof ParametersWithRandom) {
                ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
                this.privKey = (SLHDSAPrivateKeyParameters) parametersWithRandom.getParameters();
                this.random = parametersWithRandom.getRandom();
            } else {
                this.privKey = (SLHDSAPrivateKeyParameters) cipherParameters;
                this.random = null;
            }
            parameters = this.privKey.getParameters();
        } else {
            this.pubKey = (SLHDSAPublicKeyParameters) cipherParameters;
            this.privKey = null;
            this.random = null;
            parameters = this.pubKey.getParameters();
        }
        if (parameters.isPreHash()) {
            throw new IllegalArgumentException("\"pure\" slh-dsa must use non pre-hash parameters");
        }
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public byte[] generateSignature(byte[] bArr) {
        SLHDSAEngine engine = this.privKey.getParameters().getEngine();
        engine.init(this.privKey.pk.seed);
        byte[] bArr2 = new byte[engine.N];
        if (this.random != null) {
            this.random.nextBytes(bArr2);
        } else {
            System.arraycopy(this.privKey.pk.seed, 0, bArr2, 0, bArr2.length);
        }
        return internalGenerateSignature(this.privKey, this.msgPrefix, bArr, bArr2);
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] bArr, byte[] bArr2) {
        return internalVerifySignature(this.pubKey, this.msgPrefix, bArr, bArr2);
    }

    protected boolean internalVerifySignature(byte[] bArr, byte[] bArr2) {
        return internalVerifySignature(this.pubKey, null, bArr, bArr2);
    }

    private static boolean internalVerifySignature(SLHDSAPublicKeyParameters sLHDSAPublicKeyParameters, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        SLHDSAEngine engine = sLHDSAPublicKeyParameters.getParameters().getEngine();
        engine.init(sLHDSAPublicKeyParameters.getSeed());
        ADRS adrs = new ADRS();
        if ((1 + (engine.K * (1 + engine.A)) + engine.H + (engine.D * engine.WOTS_LEN)) * engine.N != bArr3.length) {
            return false;
        }
        SIG sig = new SIG(engine.N, engine.K, engine.A, engine.D, engine.H_PRIME, engine.WOTS_LEN, bArr3);
        byte[] r = sig.getR();
        SIG_FORS[] sig_fors = sig.getSIG_FORS();
        SIG_XMSS[] sig_ht = sig.getSIG_HT();
        IndexedDigest indexedDigestH_msg = engine.H_msg(r, sLHDSAPublicKeyParameters.getSeed(), sLHDSAPublicKeyParameters.getRoot(), bArr, bArr2);
        byte[] bArr4 = indexedDigestH_msg.digest;
        long j = indexedDigestH_msg.idx_tree;
        int i = indexedDigestH_msg.idx_leaf;
        adrs.setTypeAndClear(3);
        adrs.setLayerAddress(0);
        adrs.setTreeAddress(j);
        adrs.setKeyPairAddress(i);
        byte[] bArrPkFromSig = new Fors(engine).pkFromSig(sig_fors, bArr4, sLHDSAPublicKeyParameters.getSeed(), adrs);
        adrs.setTypeAndClear(2);
        adrs.setLayerAddress(0);
        adrs.setTreeAddress(j);
        adrs.setKeyPairAddress(i);
        return new HT(engine, null, sLHDSAPublicKeyParameters.getSeed()).verify(bArrPkFromSig, sig_ht, sLHDSAPublicKeyParameters.getSeed(), j, i, sLHDSAPublicKeyParameters.getRoot());
    }

    protected byte[] internalGenerateSignature(byte[] bArr, byte[] bArr2) {
        return internalGenerateSignature(this.privKey, null, bArr, bArr2);
    }

    private static byte[] internalGenerateSignature(SLHDSAPrivateKeyParameters sLHDSAPrivateKeyParameters, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        SLHDSAEngine engine = sLHDSAPrivateKeyParameters.getParameters().getEngine();
        engine.init(sLHDSAPrivateKeyParameters.pk.seed);
        Fors fors = new Fors(engine);
        byte[] bArrPRF_msg = engine.PRF_msg(sLHDSAPrivateKeyParameters.sk.prf, bArr3, bArr, bArr2);
        IndexedDigest indexedDigestH_msg = engine.H_msg(bArrPRF_msg, sLHDSAPrivateKeyParameters.pk.seed, sLHDSAPrivateKeyParameters.pk.root, bArr, bArr2);
        byte[] bArr4 = indexedDigestH_msg.digest;
        long j = indexedDigestH_msg.idx_tree;
        int i = indexedDigestH_msg.idx_leaf;
        ADRS adrs = new ADRS();
        adrs.setTypeAndClear(3);
        adrs.setTreeAddress(j);
        adrs.setKeyPairAddress(i);
        SIG_FORS[] sig_forsArrSign = fors.sign(bArr4, sLHDSAPrivateKeyParameters.sk.seed, sLHDSAPrivateKeyParameters.pk.seed, adrs);
        ADRS adrs2 = new ADRS();
        adrs2.setTypeAndClear(3);
        adrs2.setTreeAddress(j);
        adrs2.setKeyPairAddress(i);
        byte[] bArrPkFromSig = fors.pkFromSig(sig_forsArrSign, bArr4, sLHDSAPrivateKeyParameters.pk.seed, adrs2);
        new ADRS().setTypeAndClear(2);
        byte[] bArrSign = new HT(engine, sLHDSAPrivateKeyParameters.getSeed(), sLHDSAPrivateKeyParameters.getPublicSeed()).sign(bArrPkFromSig, j, i);
        byte[][] bArr5 = new byte[sig_forsArrSign.length + 2][];
        bArr5[0] = bArrPRF_msg;
        for (int i2 = 0; i2 != sig_forsArrSign.length; i2++) {
            bArr5[1 + i2] = Arrays.concatenate(sig_forsArrSign[i2].sk, Arrays.concatenate(sig_forsArrSign[i2].authPath));
        }
        bArr5[bArr5.length - 1] = bArrSign;
        return Arrays.concatenate(bArr5);
    }
}
