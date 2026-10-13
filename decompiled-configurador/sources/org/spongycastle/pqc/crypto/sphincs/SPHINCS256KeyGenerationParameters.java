package org.spongycastle.pqc.crypto.sphincs;

import java.security.SecureRandom;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/sphincs/SPHINCS256KeyGenerationParameters.class */
public class SPHINCS256KeyGenerationParameters extends KeyGenerationParameters {
    private final Digest treeDigest;

    public SPHINCS256KeyGenerationParameters(SecureRandom random, Digest treeDigest) {
        super(random, 8448);
        this.treeDigest = treeDigest;
    }

    public Digest getTreeDigest() {
        return this.treeDigest;
    }
}
