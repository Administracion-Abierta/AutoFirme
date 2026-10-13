package org.bouncycastle.pqc.crypto.sphincsplus;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincsplus/SPHINCSPlusSigner.class */
public class SPHINCSPlusSigner implements MessageSigner {
    private SPHINCSPlusPrivateKeyParameters privKey;
    private SPHINCSPlusPublicKeyParameters pubKey;
    private SecureRandom random;

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!z) {
            this.pubKey = (SPHINCSPlusPublicKeyParameters) cipherParameters;
        } else if (!(cipherParameters instanceof ParametersWithRandom)) {
            this.privKey = (SPHINCSPlusPrivateKeyParameters) cipherParameters;
        } else {
            this.privKey = (SPHINCSPlusPrivateKeyParameters) ((ParametersWithRandom) cipherParameters).getParameters();
            this.random = ((ParametersWithRandom) cipherParameters).getRandom();
        }
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public byte[] generateSignature(byte[] bArr) {
        SPHINCSPlusEngine engine = this.privKey.getParameters().getEngine();
        engine.init(this.privKey.pk.seed);
        byte[] bArr2 = new byte[engine.N];
        if (this.random != null) {
            this.random.nextBytes(bArr2);
        } else {
            System.arraycopy(this.privKey.pk.seed, 0, bArr2, 0, bArr2.length);
        }
        Fors fors = new Fors(engine);
        byte[] bArrPRF_msg = engine.PRF_msg(this.privKey.sk.prf, bArr2, bArr);
        IndexedDigest indexedDigestH_msg = engine.H_msg(bArrPRF_msg, this.privKey.pk.seed, this.privKey.pk.root, bArr);
        byte[] bArr3 = indexedDigestH_msg.digest;
        long j = indexedDigestH_msg.idx_tree;
        int i = indexedDigestH_msg.idx_leaf;
        ADRS adrs = new ADRS();
        adrs.setTypeAndClear(3);
        adrs.setTreeAddress(j);
        adrs.setKeyPairAddress(i);
        SIG_FORS[] sig_forsArrSign = fors.sign(bArr3, this.privKey.sk.seed, this.privKey.pk.seed, adrs);
        ADRS adrs2 = new ADRS();
        adrs2.setTypeAndClear(3);
        adrs2.setTreeAddress(j);
        adrs2.setKeyPairAddress(i);
        byte[] bArrPkFromSig = fors.pkFromSig(sig_forsArrSign, bArr3, this.privKey.pk.seed, adrs2);
        new ADRS().setTypeAndClear(2);
        byte[] bArrSign = new HT(engine, this.privKey.getSeed(), this.privKey.getPublicSeed()).sign(bArrPkFromSig, j, i);
        byte[][] bArr4 = new byte[sig_forsArrSign.length + 2][];
        bArr4[0] = bArrPRF_msg;
        for (int i2 = 0; i2 != sig_forsArrSign.length; i2++) {
            bArr4[1 + i2] = Arrays.concatenate(sig_forsArrSign[i2].sk, Arrays.concatenate(sig_forsArrSign[i2].authPath));
        }
        bArr4[bArr4.length - 1] = bArrSign;
        return Arrays.concatenate(bArr4);
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] bArr, byte[] bArr2) {
        SPHINCSPlusEngine engine = this.pubKey.getParameters().getEngine();
        engine.init(this.pubKey.getSeed());
        ADRS adrs = new ADRS();
        SIG sig = new SIG(engine.N, engine.K, engine.A, engine.D, engine.H_PRIME, engine.WOTS_LEN, bArr2);
        byte[] r = sig.getR();
        SIG_FORS[] sig_fors = sig.getSIG_FORS();
        SIG_XMSS[] sig_ht = sig.getSIG_HT();
        IndexedDigest indexedDigestH_msg = engine.H_msg(r, this.pubKey.getSeed(), this.pubKey.getRoot(), bArr);
        byte[] bArr3 = indexedDigestH_msg.digest;
        long j = indexedDigestH_msg.idx_tree;
        int i = indexedDigestH_msg.idx_leaf;
        adrs.setTypeAndClear(3);
        adrs.setLayerAddress(0);
        adrs.setTreeAddress(j);
        adrs.setKeyPairAddress(i);
        byte[] bArrPkFromSig = new Fors(engine).pkFromSig(sig_fors, bArr3, this.pubKey.getSeed(), adrs);
        adrs.setTypeAndClear(2);
        adrs.setLayerAddress(0);
        adrs.setTreeAddress(j);
        adrs.setKeyPairAddress(i);
        return new HT(engine, null, this.pubKey.getSeed()).verify(bArrPkFromSig, sig_ht, this.pubKey.getSeed(), j, i, this.pubKey.getRoot());
    }
}
