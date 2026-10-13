package org.spongycastle.pqc.crypto.sphincs;

import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/sphincs/SPHINCS256KeyPairGenerator.class */
public class SPHINCS256KeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private SecureRandom random;
    private Digest treeDigest;

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters param) {
        this.random = param.getRandom();
        this.treeDigest = ((SPHINCS256KeyGenerationParameters) param).getTreeDigest();
    }

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        Tree.leafaddr a = new Tree.leafaddr();
        byte[] sk = new byte[1088];
        this.random.nextBytes(sk);
        byte[] pk = new byte[1056];
        System.arraycopy(sk, 32, pk, 0, 1024);
        a.level = 11;
        a.subtree = 0L;
        a.subleaf = 0L;
        HashFunctions hs = new HashFunctions(this.treeDigest);
        Tree.treehash(hs, pk, 1024, 5, sk, a, pk, 0);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new SPHINCSPublicKeyParameters(pk), (AsymmetricKeyParameter) new SPHINCSPrivateKeyParameters(sk));
    }
}
