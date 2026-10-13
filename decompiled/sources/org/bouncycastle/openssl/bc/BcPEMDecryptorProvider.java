package org.bouncycastle.openssl.bc;

import org.bouncycastle.openssl.PEMDecryptor;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PasswordException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/bc/BcPEMDecryptorProvider.class */
public class BcPEMDecryptorProvider implements PEMDecryptorProvider {
    private final char[] password;

    public BcPEMDecryptorProvider(char[] cArr) {
        this.password = cArr;
    }

    @Override // org.bouncycastle.openssl.PEMDecryptorProvider
    public PEMDecryptor get(final String str) {
        return new PEMDecryptor() { // from class: org.bouncycastle.openssl.bc.BcPEMDecryptorProvider.1
            @Override // org.bouncycastle.openssl.PEMDecryptor
            public byte[] decrypt(byte[] bArr, byte[] bArr2) throws PEMException {
                if (BcPEMDecryptorProvider.this.password == null) {
                    throw new PasswordException("Password is null, but a password is required");
                }
                return PEMUtilities.crypt(false, bArr, BcPEMDecryptorProvider.this.password, str, bArr2);
            }
        };
    }
}
