package org.spongycastle.openssl.bc;

import org.spongycastle.openssl.PEMDecryptor;
import org.spongycastle.openssl.PEMDecryptorProvider;
import org.spongycastle.openssl.PEMException;
import org.spongycastle.openssl.PasswordException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/bc/BcPEMDecryptorProvider.class */
public class BcPEMDecryptorProvider implements PEMDecryptorProvider {
    private final char[] password;

    public BcPEMDecryptorProvider(char[] password) {
        this.password = password;
    }

    @Override // org.spongycastle.openssl.PEMDecryptorProvider
    public PEMDecryptor get(final String dekAlgName) {
        return new PEMDecryptor() { // from class: org.spongycastle.openssl.bc.BcPEMDecryptorProvider.1
            @Override // org.spongycastle.openssl.PEMDecryptor
            public byte[] decrypt(byte[] keyBytes, byte[] iv) throws PEMException {
                if (BcPEMDecryptorProvider.this.password == null) {
                    throw new PasswordException("Password is null, but a password is required");
                }
                return PEMUtilities.crypt(false, keyBytes, BcPEMDecryptorProvider.this.password, dekAlgName, iv);
            }
        };
    }
}
