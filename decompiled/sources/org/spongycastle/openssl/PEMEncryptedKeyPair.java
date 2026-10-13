package org.spongycastle.openssl;

import java.io.IOException;
import org.spongycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMEncryptedKeyPair.class */
public class PEMEncryptedKeyPair {
    private final String dekAlgName;
    private final byte[] iv;
    private final byte[] keyBytes;
    private final PEMKeyPairParser parser;

    PEMEncryptedKeyPair(String dekAlgName, byte[] iv, byte[] keyBytes, PEMKeyPairParser parser) {
        this.dekAlgName = dekAlgName;
        this.iv = iv;
        this.keyBytes = keyBytes;
        this.parser = parser;
    }

    public PEMKeyPair decryptKeyPair(PEMDecryptorProvider keyDecryptorProvider) throws IOException {
        try {
            PEMDecryptor keyDecryptor = keyDecryptorProvider.get(this.dekAlgName);
            return this.parser.parse(keyDecryptor.decrypt(this.keyBytes, this.iv));
        } catch (IOException e) {
            throw e;
        } catch (OperatorCreationException e2) {
            throw new PEMException("cannot create extraction operator: " + e2.getMessage(), e2);
        } catch (Exception e3) {
            throw new PEMException("exception processing key pair: " + e3.getMessage(), e3);
        }
    }
}
