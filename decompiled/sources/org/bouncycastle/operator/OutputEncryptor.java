package org.bouncycastle.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/OutputEncryptor.class */
public interface OutputEncryptor {
    AlgorithmIdentifier getAlgorithmIdentifier();

    OutputStream getOutputStream(OutputStream outputStream);

    GenericKey getKey();
}
