package org.spongycastle.openssl;

import org.spongycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/openssl/PEMDecryptorProvider.class */
public interface PEMDecryptorProvider {
    PEMDecryptor get(String str) throws OperatorCreationException;
}
