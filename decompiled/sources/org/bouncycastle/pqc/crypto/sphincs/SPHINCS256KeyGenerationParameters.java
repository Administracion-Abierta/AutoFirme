package org.bouncycastle.pqc.crypto.sphincs;

import com.sun.jna.platform.win32.WinError;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincs/SPHINCS256KeyGenerationParameters.class */
public class SPHINCS256KeyGenerationParameters extends KeyGenerationParameters {
    private final Digest treeDigest;

    public SPHINCS256KeyGenerationParameters(SecureRandom secureRandom, Digest digest) {
        super(secureRandom, WinError.ERROR_DS_DRA_REF_ALREADY_EXISTS);
        this.treeDigest = digest;
    }

    public Digest getTreeDigest() {
        return this.treeDigest;
    }
}
