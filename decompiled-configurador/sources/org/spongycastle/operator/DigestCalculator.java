package org.spongycastle.operator;

import java.io.OutputStream;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/DigestCalculator.class */
public interface DigestCalculator {
    AlgorithmIdentifier getAlgorithmIdentifier();

    OutputStream getOutputStream();

    byte[] getDigest();
}
