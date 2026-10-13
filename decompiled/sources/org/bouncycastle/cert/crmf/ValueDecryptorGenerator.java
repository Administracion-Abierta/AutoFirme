package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputDecryptor;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/ValueDecryptorGenerator.class */
public interface ValueDecryptorGenerator {
    InputDecryptor getValueDecryptor(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, byte[] bArr) throws CRMFException;
}
