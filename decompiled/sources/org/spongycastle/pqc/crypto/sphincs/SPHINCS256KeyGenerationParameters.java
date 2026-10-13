package org.spongycastle.pqc.crypto.sphincs;

import com.sun.jna.platform.win32.WinError;
import java.security.SecureRandom;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/sphincs/SPHINCS256KeyGenerationParameters.class */
public class SPHINCS256KeyGenerationParameters extends KeyGenerationParameters {
    private final Digest treeDigest;

    public SPHINCS256KeyGenerationParameters(SecureRandom random, Digest treeDigest) {
        super(random, WinError.ERROR_DS_DRA_REF_ALREADY_EXISTS);
        this.treeDigest = treeDigest;
    }

    public Digest getTreeDigest() {
        return this.treeDigest;
    }
}
