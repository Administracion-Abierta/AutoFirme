package org.bouncycastle.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/DigestCalculator.class */
public interface DigestCalculator {
    AlgorithmIdentifier getAlgorithmIdentifier();

    OutputStream getOutputStream();

    byte[] getDigest();
}
